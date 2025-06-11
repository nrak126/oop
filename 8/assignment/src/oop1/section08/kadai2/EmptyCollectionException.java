package oop1.section08.kadai2;

/**
 * リストが空であり、処理を続行できない場合の例外
 */
public class EmptyCollectionException extends InvalidCollectionDataException {
    /**
     * エラーメッセージを受け取るコンストラクタ
     * @param message エラーメッセージ
     */
    public EmptyCollectionException(String message) {
        super(message);
    }
}
