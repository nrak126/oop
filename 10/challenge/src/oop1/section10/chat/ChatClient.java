package oop1.section10.chat;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Scanner;

/**
 * チャットクライアント
 * コンソールベースのシンプルなチャットクライアント
 * テスト用として作成
 */
public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8089;
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Scanner scanner;
    private volatile boolean isConnected = false;
    
    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }
    
    public void start() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            isConnected = true;
            
            System.out.println("チャットサーバーに接続しました。");
            System.out.println("コマンド例:");
            System.out.println("  REGISTER <username>");
            System.out.println("  BROADCAST <message>");
            System.out.println("  JOIN <group>");
            System.out.println("  LEAVE <group>");
            System.out.println("  GROUP_MSG <group> <message>");
            System.out.println("  QUIT");
            System.out.println("注意: メッセージは自動的にBase64エンコードされます");
            System.out.println();
            
            // サーバーからのメッセージを受信するスレッド
            Thread receiverThread = new Thread(this::receiveMessages);
            receiverThread.start();
            
            // ユーザー入力を処理
            processUserInput();
            
        } catch (IOException e) {
            System.err.println("サーバー接続エラー: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    /**
     * ユーザー入力を処理する
     */
    private void processUserInput() {
        while (isConnected && scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            
            String[] parts = input.split(" ", 3);
            String command = parts[0].toUpperCase();
            
            try {
                switch (command) {
                    case "REGISTER":
                        if (parts.length >= 2) {
                            sendCommand("REGISTER " + parts[1]);
                        } else {
                            System.out.println("使用法: REGISTER <username>");
                        }
                        break;
                        
                    case "BROADCAST":
                        if (parts.length >= 2) {
                            String message = parts[1];
                            String encoded = Base64.getEncoder().encodeToString(message.getBytes());
                            sendCommand("BROADCAST " + encoded);
                        } else {
                            System.out.println("使用法: BROADCAST <message>");
                        }
                        break;
                        
                    case "JOIN":
                        if (parts.length >= 2) {
                            sendCommand("JOIN " + parts[1]);
                        } else {
                            System.out.println("使用法: JOIN <group>");
                        }
                        break;
                        
                    case "LEAVE":
                        if (parts.length >= 2) {
                            sendCommand("LEAVE " + parts[1]);
                        } else {
                            System.out.println("使用法: LEAVE <group>");
                        }
                        break;
                        
                    case "GROUP_MSG":
                        if (parts.length >= 3) {
                            String group = parts[1];
                            String message = parts[2];
                            String encoded = Base64.getEncoder().encodeToString(message.getBytes());
                            sendCommand("GROUP_MSG " + group + " " + encoded);
                        } else {
                            System.out.println("使用法: GROUP_MSG <group> <message>");
                        }
                        break;
                        
                    case "QUIT":
                        sendCommand("QUIT");
                        return;
                        
                    case "HELP":
                        showHelp();
                        break;
                        
                    default:
                        System.out.println("不明なコマンド: " + command + " (HELPでヘルプを表示)");
                }
            } catch (Exception e) {
                System.err.println("コマンド処理エラー: " + e.getMessage());
            }
        }
    }
    
    /**
     * サーバーからのメッセージを受信する
     */
    private void receiveMessages() {
        try {
            String line;
            while (isConnected && (line = reader.readLine()) != null) {
                processServerMessage(line);
            }
        } catch (IOException e) {
            if (isConnected) {
                System.err.println("サーバーとの通信が切断されました: " + e.getMessage());
            }
        }
    }
    
    /**
     * サーバーからのメッセージを処理する
     */
    private void processServerMessage(String message) {
        System.out.println("[サーバー] " + message);
        
        // メッセージの種類に応じて詳細表示
        if (message.startsWith("300 INFO BROADCAST_MESSAGE")) {
            String[] parts = message.split(" ", 5);
            if (parts.length >= 5) {
                String sender = parts[3];
                String encodedMsg = parts[4];
                try {
                    String decodedMsg = new String(Base64.getDecoder().decode(encodedMsg));
                    System.out.println("  >>> " + sender + " (全体): " + decodedMsg);
                } catch (Exception e) {
                    System.out.println("  >>> メッセージのデコードに失敗しました");
                }
            }
        } else if (message.startsWith("300 INFO GROUP_MESSAGE")) {
            String[] parts = message.split(" ", 6);
            if (parts.length >= 6) {
                String sender = parts[3];
                String group = parts[4];
                String encodedMsg = parts[5];
                try {
                    String decodedMsg = new String(Base64.getDecoder().decode(encodedMsg));
                    System.out.println("  >>> " + sender + " (" + group + "): " + decodedMsg);
                } catch (Exception e) {
                    System.out.println("  >>> メッセージのデコードに失敗しました");
                }
            }
        }
    }
    
    /**
     * サーバーにコマンドを送信する
     */
    private void sendCommand(String command) {
        if (writer != null && isConnected) {
            writer.println(command);
        }
    }
    
    /**
     * ヘルプを表示する
     */
    private void showHelp() {
        System.out.println("=== チャットクライアント ヘルプ ===");
        System.out.println("REGISTER <username>        - ユーザー登録");
        System.out.println("BROADCAST <message>        - 全体メッセージ送信");
        System.out.println("JOIN <group>               - グループ参加");
        System.out.println("LEAVE <group>              - グループ退出");
        System.out.println("GROUP_MSG <group> <msg>    - グループメッセージ送信");
        System.out.println("QUIT                       - 終了");
        System.out.println("HELP                       - このヘルプを表示");
        System.out.println("================================");
    }
    
    /**
     * 接続を切断する
     */
    private void disconnect() {
        isConnected = false;
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            System.err.println("切断エラー: " + e.getMessage());
        }
        System.out.println("サーバーとの接続を終了しました。");
    }
}
