package oop1.challenge07;

/**
 * イミュータブルなジェネリック値オブジェクトを表すクラス
 * @param <T> 保持する値の型
 */
public final class Value<T> {
    private final T value;

    /**
     * 指定された値を保持する新しいValueインスタンスを生成します
     * @param value 保持する値
     */
    public Value(T value) {
        this.value = value;
    }

    /**
     * 保持している値を取得します
     * @return 保持している値
     */
    public T get() {
        return value;
    }

    /**
     * 保持している値がnullかどうかを判定します
     * @return 値がnullの場合true、それ以外の場合false
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * このValueオブジェクトの文字列表現を返します
     * @return "Value[value: (保持している値の文字列表現)]"形式の文字列
     */
    @Override
    public String toString() {
        return String.format("Value[value: %s]", String.valueOf(value));
    }

    /**
     * このValueオブジェクトと指定されたオブジェクトが等しいかどうかを判定します
     * @param obj 比較対象のオブジェクト
     * @return 両者が等しい場合true、それ以外の場合false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Value<?> other = (Value<?>) obj;
        if (value == null) return other.value == null;
        return value.equals(other.value);
    }

    /**
     * このValueオブジェクトのハッシュコードを返します
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
