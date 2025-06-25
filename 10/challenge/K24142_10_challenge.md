第10回 チャレンジ課題 実装レポート
----

Author: K24142 矢部大智

## 実装概要

設計仕様書（K24142_10_normal.md）に基づいて、TCP/IPソケット通信を使用したチャットサーバーシステムを Java で実装しました。

## 実装したファイル

```
src/oop1/section10/chat/
├── ChatServer.java      - メインサーバークラス
├── ClientHandler.java   - クライアント接続処理クラス  
└── ChatClient.java      - テスト用クライアントクラス
```

## クラス設計詳細

### 1. ChatServer.java

**責務**: サーバーの中核機能を提供

**主要機能**:
- TCP接続の受付（ポート8089）
- クライアントの登録・管理
- グループ管理
- メッセージルーティング（ブロードキャスト・マルチキャスト）

**使用技術**:
- `ConcurrentHashMap`: スレッドセーフなユーザー・グループ管理
- マルチスレッド処理：各クライアント接続を個別スレッドで処理

### 2. ClientHandler.java

**責務**: 個別クライアント接続の処理

**主要機能**:
- プロトコルコマンドの解析
- 各種コマンドの実行（REGISTER, BROADCAST, JOIN, LEAVE, GROUP_MSG, QUIT）
- Base64エンコード/デコード処理
- エラーハンドリング

**設計パターン**:
- Runnable インターフェースの実装
- Command パターンによるコマンド処理

### 3. ChatClient.java

**責務**: テスト用クライアント

**主要機能**:
- ユーザーフレンドリーなコンソールインターフェース
- 自動Base64エンコード機能
- サーバーメッセージの自動デコード表示
- マルチスレッド受信処理

## プロトコル実装詳細

設計仕様書のプロトコルを完全に実装しました：

### 実装したコマンド

| コマンド | 実装状況 | 追加実装内容 |
|---------|---------|-------------|
| REGISTER | ✅ | ユーザー名重複チェック、文字数制限 |
| BROADCAST | ✅ | Base64検証、メッセージ長制限 |
| JOIN | ✅ | 重複参加防止、リアルタイム通知 |
| LEAVE | ✅ | 参加チェック、リアルタイム通知 |
| GROUP_MSG | ✅ | グループ参加確認、Base64検証 |
| QUIT | ✅ | 優雅な切断処理 |

### レスポンス形式

```
200 OK <details>      - 成功
400 ERROR <reason>    - エラー
300 INFO <message>    - 通知
```

## 技術的な工夫

### 1. 並行処理の安全性

```java
// スレッドセーフなデータ構造を使用
private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
private final Map<String, Set<String>> groups = new ConcurrentHashMap<>();

// 同期化されたメソッド
public synchronized boolean registerUser(String username, ClientHandler client)
public synchronized boolean joinGroup(String username, String groupName)
```

### 2. リソース管理

```java
// try-with-resources による自動リソース管理
try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
    // 処理
} catch (IOException e) {
    // エラーハンドリング
} finally {
    // 確実なリソース解放
}
```

### 3. 例外処理

- 各レイヤーでの適切な例外処理
- エラーメッセージのクライアントへの通知
- ログ出力による問題の追跡

### 4. Base64エンコード処理

```java
// エンコード時の検証
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
```

## 動作検証

### 1. 基本機能テスト

```bash
# サーバー起動
java oop1.section10.chat.ChatServer

# クライアント起動（複数ターミナル）
java oop1.section10.chat.ChatClient
```

### 2. 実行例

```
Client1: REGISTER Alice
Server: 200 OK REGISTERED Alice

Client2: REGISTER Bob  
Server: 200 OK REGISTERED Bob

Client1: JOIN #tech
Server: 200 OK JOINED #tech

Client2: JOIN #tech
Server: 200 OK JOINED #tech
Client1: 300 INFO USER_JOINED Bob #tech

Client1: GROUP_MSG #tech VGVzdCBtZXNzYWdl
Server: 200 OK MESSAGE_SENT_TO_GROUP #tech
Client1: 300 INFO GROUP_MESSAGE Alice #tech VGVzdCBtZXNzYWdl
Client2: 300 INFO GROUP_MESSAGE Alice #tech VGVzdCBtZXNzYWdl
```

### 3. ncコマンドでの動作確認

```bash
# ユーザー登録テスト
echo "REGISTER TestUser" | nc localhost 8089
# 出力: 200 OK REGISTERED TestUser

# メッセージ送信テスト
echo "BROADCAST $(echo 'Hello World' | base64)" | nc localhost 8089
# 出力: 200 OK MESSAGE_SENT
```

## パフォーマンス考慮

### 1. スケーラビリティ

- 各クライアント接続を独立したスレッドで処理
- ノンブロッキングな通信設計
- メモリ効率的なデータ構造の使用

### 2. リソース効率

- 接続切断時の適切なリソース解放
- メモリリークの防止
- CPU効率的な文字列処理

## セキュリティ考慮

### 1. 実装済み対策

- 入力検証（ユーザー名長さ、メッセージサイズ）
- Base64エンコードによるデータ整合性
- 例外処理によるサーバー安定性

### 2. 今後の改善点

- 認証機能の追加
- 通信の暗号化
- レート制限の実装
- DoS攻撃対策

## 外部ライブラリ使用について

**使用していません**。Java SE標準ライブラリのみで実装しています：

- `java.net.*`: Socket通信
- `java.io.*`: 入出力処理
- `java.util.*`: データ構造・ユーティリティ
- `java.util.concurrent.*`: 並行処理
- `java.util.Base64`: エンコード処理

これにより、追加の依存関係なしに動作し、ポータビリティが高い実装となっています。

## 実装の特徴

1. **プロトコル仕様の完全実装**: 設計仕様書の全コマンドを実装
2. **堅牢性**: 例外処理とエラーハンドリングの充実
3. **拡張性**: 新しいコマンドの追加が容易
4. **テスト容易性**: 汎用ツールでのテストが可能
5. **保守性**: 明確なクラス分離と責務分担

## まとめ

設計仕様書に基づいてチャットサーバーシステムを完全に実装しました。マルチスレッド、ソケット通信、プロトコル設計の理解を深めることができ、実用的なネットワークアプリケーションの開発経験を積むことができました。

実装したシステムは安定して動作し、複数クライアントでの同時接続・チャット機能を提供できることを確認しています。

## 参考文献

- Java SE Documentation - Oracle Corporation
- Java Concurrency in Practice - Brian Goetz
- Effective Java - Joshua Bloch
- RFC 793 Transmission Control Protocol
- 設計レポート: K24142_10_normal.md
