package oop1.section10;

import java.io.*;
import java.net.*;

/**
 * 簡易HTTPサーバー
 * ポート8088で動作し、HTTPリクエストを受け取ってHTMLレスポンスを返す
 */
public class SimpleHTTPServer {
    private static final int PORT = 8088;
    private ServerSocket serverSocket;
    
    public static void main(String[] args) {
        SimpleHTTPServer server = new SimpleHTTPServer();
        server.start();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("SimpleHTTPServer started on port " + PORT);
            System.out.println("Access: http://localhost:" + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
    
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            // HTTPリクエストを読み取る
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            System.out.println("Request: " + requestLine);
            
            // リクエストヘッダーを読み飛ばす
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // ヘッダーを読み飛ばす
            }
            
            // HTTPリクエストを解析
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2) {
                String method = parts[0];
                String path = parts[1];
                
                if ("GET".equals(method)) {
                    handleGetRequest(path, out);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
    
    private void handleGetRequest(String path, PrintWriter out) {
        if (path.equals("/") || path.equals("/index.html")) {
            sendIndexHtml(out);
        } else if (path.startsWith("/hello?name=")) {
            String name = extractNameFromPath(path);
            sendHelloHtml(out, name);
        } else if (path.equals("/style.css")) {
            sendStyleCss(out);
        } else if (path.equals("/script.js")) {
            sendScriptJs(out);
        } else {
            send404(out);
        }
    }
    
    private String extractNameFromPath(String path) {
        String query = path.substring("/hello?name=".length());
        try {
            return URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return query;
        }
    }
    
    private void sendIndexHtml(PrintWriter out) {
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"ja\">");
        out.println("<head>");
        out.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        out.println("  <meta charset=\"UTF-8\">");
        out.println("  <title>SimpleHTTPServer</title>");
        out.println("  <link rel=\"stylesheet\" href=\"style.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("  <main>");
        out.println("    <h1>このページはSimpleHTTPServerより生成されて返されています。</h1>");
        out.println("    <p><button class=\"fire\">Push!!</button></p>");
        out.println("    <p class=\"copyright\">K24142 - 矢部大智</p>");
        out.println("  </main>");
        out.println("  <script src=\"script.js\"></script>");
        out.println("</body>");
        out.println("</html>");
    }
    
    private void sendHelloHtml(PrintWriter out, String name) {
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"ja\">");
        out.println("<head>");
        out.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        out.println("  <meta charset=\"UTF-8\">");
        out.println("  <title>SimpleHTTPServer</title>");
        out.println("  <link rel=\"stylesheet\" href=\"style.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("  <main>");
        out.println("    <h1>こんにちは！" + name + "さん！！</h1>");
        out.println("  </main>");
        out.println("</body>");
        out.println("</html>");
    }
    
    private void sendStyleCss(PrintWriter out) {
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/css; charset=utf-8");
        out.println();
        out.println("* {");
        out.println("  margin: 0;");
        out.println("  padding: 0;");
        out.println("  box-sizing: border-box;");
        out.println("}");
        out.println("body {");
        out.println("  height: 100vh;");
        out.println("  display: flex;");
        out.println("  justify-content: center;");
        out.println("  align-items: center;");
        out.println("}");
        out.println("main {");
        out.println("  height: 450px;");
        out.println("  max-height: 90vh;");
        out.println("  width: 800px;");
        out.println("  max-width: 90vw;");
        out.println("  border-radius: 10px;");
        out.println("  box-shadow: rgba(0, 0, 0, 0.1) 0px 20px 60px -10px;");
        out.println("  display: flex;");
        out.println("  justify-content: center;");
        out.println("  align-items: center;");
        out.println("  flex-direction: column;");
        out.println("}");
        out.println("h1 {");
        out.println("  padding: 0 3em;");
        out.println("  margin-bottom: 2em;");
        out.println("  text-align: center;");
        out.println("}");
        out.println("button {");
        out.println("  font-size: 1.25em;");
        out.println("  padding: 0.5em 1em;");
        out.println("}");
        out.println(".copyright {");
        out.println("  margin-top: 20px;");
        out.println("  text-decoration: underline;");
        out.println("  font-style: italic;");
        out.println("}");
    }
    
    private void sendScriptJs(PrintWriter out) {
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/javascript; charset=utf-8");
        out.println();
        out.println("var btn = document.querySelector('button.fire');");
        out.println("btn.addEventListener('click', function() {");
        out.println("  alert('Hello, SimpleHTTPServer!!');");
        out.println("});");
    }
    
    private void send404(PrintWriter out) {
        out.println("HTTP/1.0 404 Not Found");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"ja\">");
        out.println("<head>");
        out.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        out.println("  <meta charset=\"UTF-8\">");
        out.println("  <title>404</title>");
        out.println("  <link rel=\"stylesheet\" href=\"style.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("  <main>");
        out.println("    <h1>404... Not Found!</h1>");
        out.println("  </main>");
        out.println("</body>");
        out.println("</html>");
    }
}
