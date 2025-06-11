package oop1.section08.kadai2;

/**
 * リストがnullであるなど、データそのものが処理に適さない根本的な問題を示す例外
 */
public class InvalidCollectionDataException extends Exception {
    /**
     * エラーメッセージを受け取るコンストラクタ
     * @param message エラーメッセージ
     */
    public InvalidCollectionDataException(String message) {
        super(message);
    }
}
