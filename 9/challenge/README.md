# ZIP圧縮ツール - ドラッグ&ドロップ対応

このアプリケーションは、ファイルやフォルダをドラッグ&ドロップでZIP圧縮できるGUIツールです。

## 機能

### 基本機能
- **ウィンドウ表示**: 起動時にGUIウィンドウが表示される
- **ドラッグ&ドロップ**: エクスプローラーやFinderからファイル/フォルダをドロップ
- **ファイル一覧表示**: ドロップされたファイルをリスト表示
- **ZIP圧縮**: ドロップされたファイルを1つのZIPファイルに圧縮
- **保存先**: 最初にドロップされたファイルと同じディレクトリに保存
- **ファイル名重複防止**: archive.zip, archive_1.zip, archive_2.zip...のように自動連番

### エラーハンドリング
- ファイル読み込み失敗時の詳細エラー表示
- 圧縮処理中のエラーを分かりやすくダイアログ表示

### フィードバック機能
- 圧縮処理中の進行状況表示
- 圧縮完了時の成功メッセージと保存先表示
- ファイルサイズ情報の表示

### 追加機能
- **視覚的フィードバック**: ドラッグ中の背景色変更
- **重複ファイル除去**: 同じファイルは重複して追加されない
- **バックグラウンド処理**: 圧縮処理中もUIが応答可能
- **リストクリア機能**: ドロップしたファイルリストを簡単にクリア
- **ファイルタイプ表示**: ファイルとフォルダを区別して表示

## 使い方

1. アプリケーションを起動
2. ウィンドウ上部の「ドロップエリア」にファイルやフォルダをドラッグ&ドロップ
3. 「ZIP圧縮実行」ボタンをクリック
4. 圧縮完了のダイアログが表示されたら完了

## コンパイル・実行方法

### コマンドライン
```bash
# コンパイル
javac src/ZipCompressorApp.java

# 実行
cd src
java ZipCompressorApp
```

### スクリプト使用
```bash
# 実行権限を付与（初回のみ）
chmod +x run.sh

# 実行
./run.sh
```

## 動作環境

- Java 8以降
- macOS / Windows / Linux

## ファイル構成

```
challenge/
├── src/
│   └── ZipCompressorApp.java    # メインアプリケーション
├── run.sh                       # 実行スクリプト
└── README.md                    # このファイル
```
