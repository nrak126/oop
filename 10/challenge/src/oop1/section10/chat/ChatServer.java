package oop1.section10.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * チャットサーバー
 * 複数のクライアントが接続し、テキストベースのチャット通信を行う
 * 
 * 機能:
 * - ユーザー登録
 * - 全体メッセージ（ブロードキャスト）
 * - グループチャット（マルチキャスト）
 * - グループ参加・退出
 */
public class ChatServer {
    private static final int PORT = 8089;
    private ServerSocket serverSocket;
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> groups = new ConcurrentHashMap<>();
    private volatile boolean isRunning = false;
    
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        
        // シャットダウンフック
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nサーバーをシャットダウンしています...");
            server.stop();
        }));
        
        server.start();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("チャットサーバーが起動しました。ポート: " + PORT);
            System.out.println("Ctrl+C でサーバーを停止できます。");
            
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    new Thread(clientHandler).start();
                    System.out.println("新しいクライアントが接続しました: " + clientSocket.getRemoteSocketAddress());
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("クライアント接続エラー: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("サーバー起動エラー: " + e.getMessage());
        }
    }
    
    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            // 全クライアントに終了通知
            for (ClientHandler client : clients.values()) {
                client.disconnect();
            }
        } catch (IOException e) {
            System.err.println("サーバー停止エラー: " + e.getMessage());
        }
    }
    
    /**
     * ユーザーを登録する
     */
    public synchronized boolean registerUser(String username, ClientHandler client) {
        if (clients.containsKey(username)) {
            return false;
        }
        clients.put(username, client);
        System.out.println("ユーザー登録: " + username);
        return true;
    }
    
    /**
     * ユーザーの登録を解除する
     */
    public synchronized void unregisterUser(String username) {
        if (username != null) {
            clients.remove(username);
            // 全グループから退出
            for (Set<String> groupMembers : groups.values()) {
                groupMembers.remove(username);
            }
            System.out.println("ユーザー退出: " + username);
        }
    }
    
    /**
     * 全体メッセージをブロードキャストする
     */
    public void broadcastMessage(String sender, String message) {
        String encodedMessage = "300 INFO BROADCAST_MESSAGE " + sender + " " + message;
        for (ClientHandler client : clients.values()) {
            client.sendMessage(encodedMessage);
        }
        System.out.println("ブロードキャスト from " + sender + ": " + decodeBase64(message));
    }
    
    /**
     * グループに参加する
     */
    public synchronized boolean joinGroup(String username, String groupName) {
        if (!clients.containsKey(username)) {
            return false;
        }
        
        Set<String> groupMembers = groups.computeIfAbsent(groupName, k -> ConcurrentHashMap.newKeySet());
        if (groupMembers.contains(username)) {
            return false; // 既に参加済み
        }
        
        groupMembers.add(username);
        
        // グループメンバーに通知
        String notification = "300 INFO USER_JOINED " + username + " " + groupName;
        for (String member : groupMembers) {
            ClientHandler client = clients.get(member);
            if (client != null) {
                client.sendMessage(notification);
            }
        }
        
        System.out.println(username + " がグループ " + groupName + " に参加しました");
        return true;
    }
    
    /**
     * グループから退出する
     */
    public synchronized boolean leaveGroup(String username, String groupName) {
        Set<String> groupMembers = groups.get(groupName);
        if (groupMembers == null || !groupMembers.contains(username)) {
            return false;
        }
        
        groupMembers.remove(username);
        
        // グループメンバーに通知
        String notification = "300 INFO USER_LEFT " + username + " " + groupName;
        for (String member : groupMembers) {
            ClientHandler client = clients.get(member);
            if (client != null) {
                client.sendMessage(notification);
            }
        }
        
        // グループが空になったら削除
        if (groupMembers.isEmpty()) {
            groups.remove(groupName);
        }
        
        System.out.println(username + " がグループ " + groupName + " から退出しました");
        return true;
    }
    
    /**
     * グループメッセージを送信する
     */
    public boolean sendGroupMessage(String sender, String groupName, String message) {
        Set<String> groupMembers = groups.get(groupName);
        if (groupMembers == null || !groupMembers.contains(sender)) {
            return false;
        }
        
        String encodedMessage = "300 INFO GROUP_MESSAGE " + sender + " " + groupName + " " + message;
        for (String member : groupMembers) {
            ClientHandler client = clients.get(member);
            if (client != null) {
                client.sendMessage(encodedMessage);
            }
        }
        
        System.out.println("グループメッセージ from " + sender + " to " + groupName + ": " + decodeBase64(message));
        return true;
    }
    
    /**
     * Base64デコード（ログ用）
     */
    private String decodeBase64(String encoded) {
        try {
            return new String(Base64.getDecoder().decode(encoded));
        } catch (Exception e) {
            return encoded;
        }
    }
}
