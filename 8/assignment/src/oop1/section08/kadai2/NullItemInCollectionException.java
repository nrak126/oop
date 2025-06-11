package oop1.section08.kadai2;

/**
 * リスト内にnull要素が含まれており、それが許容されない場合の例外
 */
public class NullItemInCollectionException extends InvalidCollectionDataException {
    private final int index;
    
    /**
     * エラーメッセージと問題が発生したインデックスを受け取るコンストラクタ
     * @param message エラーメッセージ
     * @param index 問題が発生したインデックス
     */
    public NullItemInCollectionException(String message, int index) {
        super(message);
        this.index = index;
    }
    
    /**
     * 問題が発生したインデックスを取得
     * @return インデックス
     */
    public int getIndex() {
        return index;
    }
}
