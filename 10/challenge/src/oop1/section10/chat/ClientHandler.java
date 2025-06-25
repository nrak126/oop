package oop1.section10.chat;

import java.io.*;
import java.net.*;
import java.util.Base64;

/**
 * 各クライアントの接続を処理するハンドラークラス
 * クライアントからのコマンドを解析し、適切な処理を行う
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ChatServer server;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private volatile boolean isConnected = true;
    
    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("ClientHandler初期化エラー: " + e.getMessage());
            disconnect();
        }
    }
    
    @Override
    public void run() {
        try {
            String line;
            while (isConnected && (line = reader.readLine()) != null) {
                processCommand(line.trim());
            }
        } catch (IOException e) {
            System.err.println("クライアント通信エラー: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    /**
     * クライアントからのコマンドを処理する
     */
    private void processCommand(String command) {
        if (command.isEmpty()) return;
        
        String[] parts = command.split(" ", 3);
        String cmd = parts[0].toUpperCase();
        
        try {
            switch (cmd) {
                case "REGISTER":
                    handleRegister(parts);
                    break;
                case "BROADCAST":
                    handleBroadcast(parts);
                    break;
                case "JOIN":
                    handleJoin(parts);
                    break;
                case "LEAVE":
                    handleLeave(parts);
                    break;
                case "GROUP_MSG":
                    handleGroupMessage(parts);
                    break;
                case "QUIT":
                    handleQuit();
                    break;
                default:
                    sendMessage("400 ERROR UNKNOWN_COMMAND " + cmd);
            }
        } catch (Exception e) {
            sendMessage("400 ERROR PROCESSING_ERROR " + e.getMessage());
            System.err.println("コマンド処理エラー: " + e.getMessage());
        }
    }
    
    /**
     * ユーザー登録処理
     */
    private void handleRegister(String[] parts) {
        if (parts.length < 2) {
            sendMessage("400 ERROR INVALID_ARGUMENTS");
            return;
        }
        
        if (username != null) {
            sendMessage("400 ERROR ALREADY_REGISTERED");
            return;
        }
        
        String newUsername = parts[1];
        
        // ユーザー名の検証
        if (newUsername.isEmpty() || newUsername.contains(" ") || newUsername.length() > 50) {
            sendMessage("400 ERROR INVALID_USERNAME");
            return;
        }
        
        if (server.registerUser(newUsername, this)) {
            this.username = newUsername;
            sendMessage("200 OK REGISTERED " + newUsername);
        } else {
            sendMessage("400 ERROR USERNAME_ALREADY_EXISTS " + newUsername);
        }
    }
    
    /**
     * ブロードキャスト処理
     */
    private void handleBroadcast(String[] parts) {
        if (username == null) {
            sendMessage("400 ERROR NOT_REGISTERED");
            return;
        }
        
        if (parts.length < 2) {
            sendMessage("400 ERROR INVALID_ARGUMENTS");
            return;
        }
        
        String message = parts[1];
        
        // Base64デコードして検証
        try {
            byte[] decoded = Base64.getDecoder().decode(message);
            if (decoded.length > 1024) {
                sendMessage("400 ERROR MESSAGE_TOO_LONG");
                return;
            }
        } catch (IllegalArgumentException e) {
            sendMessage("400 ERROR INVALID_BASE64");
            return;
        }
        
        server.broadcastMessage(username, message);
        sendMessage("200 OK MESSAGE_SENT");
    }
    
    /**
     * グループ参加処理
     */
    private void handleJoin(String[] parts) {
        if (username == null) {
            sendMessage("400 ERROR NOT_REGISTERED");
            return;
        }
        
        if (parts.length < 2) {
            sendMessage("400 ERROR INVALID_ARGUMENTS");
            return;
        }
        
        String groupName = parts[1];
        
        if (server.joinGroup(username, groupName)) {
            sendMessage("200 OK JOINED " + groupName);
        } else {
            sendMessage("400 ERROR ALREADY_JOINED " + groupName);
        }
    }
    
    /**
     * グループ退出処理
     */
    private void handleLeave(String[] parts) {
        if (username == null) {
            sendMessage("400 ERROR NOT_REGISTERED");
            return;
        }
        
        if (parts.length < 2) {
            sendMessage("400 ERROR INVALID_ARGUMENTS");
            return;
        }
        
        String groupName = parts[1];
        
        if (server.leaveGroup(username, groupName)) {
            sendMessage("200 OK LEFT " + groupName);
        } else {
            sendMessage("400 ERROR NOT_IN_GROUP " + groupName);
        }
    }
    
    /**
     * グループメッセージ処理
     */
    private void handleGroupMessage(String[] parts) {
        if (username == null) {
            sendMessage("400 ERROR NOT_REGISTERED");
            return;
        }
        
        if (parts.length < 3) {
            sendMessage("400 ERROR INVALID_ARGUMENTS");
            return;
        }
        
        String groupName = parts[1];
        String message = parts[2];
        
        // Base64デコードして検証
        try {
            byte[] decoded = Base64.getDecoder().decode(message);
            if (decoded.length > 1024) {
                sendMessage("400 ERROR MESSAGE_TOO_LONG");
                return;
            }
        } catch (IllegalArgumentException e) {
            sendMessage("400 ERROR INVALID_BASE64");
            return;
        }
        
        if (server.sendGroupMessage(username, groupName, message)) {
            sendMessage("200 OK MESSAGE_SENT_TO_GROUP " + groupName);
        } else {
            sendMessage("400 ERROR NOT_IN_GROUP " + groupName);
        }
    }
    
    /**
     * 終了処理
     */
    private void handleQuit() {
        sendMessage("200 OK BYE");
        disconnect();
    }
    
    /**
     * クライアントにメッセージを送信する
     */
    public void sendMessage(String message) {
        if (writer != null && isConnected) {
            writer.println(message);
        }
    }
    
    /**
     * 接続を切断する
     */
    public void disconnect() {
        isConnected = false;
        server.unregisterUser(username);
        
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("接続切断エラー: " + e.getMessage());
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
}
