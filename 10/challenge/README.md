# チャットサーバー実装 - 第10回チャレンジ課題

## 概要

このプロジェクトは、TCP/IPソケット通信を使用したテキストベースのチャットサーバーシステムです。
設計仕様に基づいて実装されており、複数のクライアントが同時に接続してリアルタイムチャットを行えます。

## 機能

- **ユーザー登録**: クライアントがサーバーに接続時にユーザー名を登録
- **全体メッセージ（ブロードキャスト）**: 接続中の全ユーザーにメッセージを送信
- **グループチャット**: 特定のグループに参加したユーザー間でのメッセージ交換
- **グループ管理**: グループへの参加・退出機能
- **リアルタイム通知**: グループの参加・退出時の通知機能

## プロジェクト構成

```
src/
└── oop1/
    └── section10/
        └── chat/
            ├── ChatServer.java      # サーバーメインクラス
            ├── ClientHandler.java   # クライアント接続処理
            └── ChatClient.java      # テスト用クライアント
```

## クラス設計

### ChatServer.java
- メインサーバークラス
- クライアント接続の管理
- ユーザー・グループ管理
- メッセージルーティング機能

### ClientHandler.java
- 個別クライアント接続の処理
- プロトコルコマンドの解析
- マルチスレッド対応

### ChatClient.java
- テスト用コンソールクライアント
- ユーザーフレンドリーなインターフェース
- 自動Base64エンコード機能

## 使用技術・ライブラリ

- **Java SE標準ライブラリのみ使用**
  - `java.net.*`: Socket通信
  - `java.io.*`: 入出力処理  
  - `java.util.concurrent.*`: マルチスレッド・並行処理
  - `java.util.Base64`: メッセージエンコード

- **設計パターン**
  - マルチスレッド設計（各クライアント接続を個別スレッドで処理）
  - Observer pattern的な通知機能
  - Thread-safeなデータ構造の使用

## コンパイル・実行方法

### 1. コンパイル
```bash
# プロジェクトルートで実行
javac -d . src/oop1/section10/chat/*.java
```

### 2. サーバー起動
```bash
java oop1.section10.chat.ChatServer
```
サーバーはポート8089で起動します。

### 3. クライアント起動（別ターミナル）
```bash
java oop1.section10.chat.ChatClient
```

## プロトコル仕様

### 基本コマンド形式
```
COMMAND <argument1> <argument2> ...
```

### サポートコマンド

| コマンド | 機能 | 例 |
|---------|------|-----|
| `REGISTER <username>` | ユーザー登録 | `REGISTER Alice` |
| `BROADCAST <base64_message>` | 全体メッセージ | `BROADCAST SGVsbG8=` |
| `JOIN <group>` | グループ参加 | `JOIN #general` |
| `LEAVE <group>` | グループ退出 | `LEAVE #general` |
| `GROUP_MSG <group> <base64_message>` | グループメッセージ | `GROUP_MSG #tech SGVsbG8=` |
| `QUIT` | 接続終了 | `QUIT` |

### レスポンス形式

| ステータス | 意味 | 例 |
|-----------|------|-----|
| `200 OK` | 成功 | `200 OK REGISTERED Alice` |
| `400 ERROR` | エラー | `400 ERROR USERNAME_ALREADY_EXISTS` |
| `300 INFO` | 通知 | `300 INFO BROADCAST_MESSAGE Alice SGVsbG8=` |

## 使用例

### 1. ユーザー登録とブロードキャスト
```
Client: REGISTER Alice
Server: 200 OK REGISTERED Alice

Client: BROADCAST SGVsbG8gV29ybGQ=  (Hello World)
Server: 200 OK MESSAGE_SENT
Server: 300 INFO BROADCAST_MESSAGE Alice SGVsbG8gV29ybGQ=
```

### 2. グループチャット
```
Client: JOIN #tech
Server: 200 OK JOINED #tech

Client: GROUP_MSG #tech VGVjaCBkaXNjdXNzaW9u  (Tech discussion)
Server: 200 OK MESSAGE_SENT_TO_GROUP #tech
Server: 300 INFO GROUP_MESSAGE Alice #tech VGVjaCBkaXNjdXNzaW9u
```

## ncコマンドでのテスト

汎用ツールでもテスト可能です：

```bash
# ユーザー登録
echo "REGISTER TestUser" | nc localhost 8089

# Base64エンコードしてメッセージ送信
echo "BROADCAST $(echo 'Hello World' | base64)" | nc localhost 8089
```

## 特徴・工夫点

1. **Thread-Safe設計**: `ConcurrentHashMap`を使用してマルチスレッド環境での安全性を確保

2. **Base64エンコード**: メッセージにスペースや特殊文字が含まれても正確に送信可能

3. **エラーハンドリング**: 詳細なエラーメッセージで問題の特定が容易

4. **リソース管理**: try-with-resourcesとfinally句で確実なリソース解放

5. **拡張性**: 新しいコマンドの追加が容易な設計

6. **テスト容易性**: シンプルなクライアントとncコマンドでのテストが可能

## セキュリティ上の注意

現在の実装では以下の点にご注意ください：

- 認証機能なし（学習目的のため）
- 暗号化なし（平文通信）
- DoS攻撃対策なし

実用環境では追加のセキュリティ対策が必要です。

## 動作確認済み環境

- Java SE 11以上
- macOS / Linux / Windows
- 複数クライアント同時接続テスト済み

## Author

K24142 矢部大智
