/**
 * 実行可能な処理の契約を定義するインターフェイス。
 * このインターフェイスを実装するクラスは、具体的な実行処理を持つことになります。
 */
public interface Executable {
    /**
     * 実行可能な処理を行い、結果メッセージを返却します。
     *
     * @return 処理結果を示す文字列
     */
    String execute();
}
