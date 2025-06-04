package oop1.challenge07;

import java.util.function.Function;

/**
 * Value<T>オブジェクトを操作するためのユーティリティクラス
 */
public final class ValueUtils {
    // インスタンス化を防ぐためのプライベートコンストラクタ
    private ValueUtils() {}

    /**
     * 指定された値を保持するValue<E>の新しいインスタンスを生成します
     * @param value 保持する値
     * @param <E> 値の型
     * @return 新しいValue<E>インスタンス
     */
    public static <E> Value<E> of(E value) {
        return new Value<>(value);
    }

    /**
     * 指定されたValue<T>が保持している値にmapper関数を適用し、
     * その結果を保持する新しいValue<R>インスタンスを返します
     * @param originalValue 元のValueオブジェクト
     * @param mapper 値を変換する関数
     * @param <T> 元の値の型
     * @param <R> 変換後の値の型
     * @return 変換された値を保持する新しいValue<R>インスタンス
     */
    public static <T, R> Value<R> map(Value<T> originalValue, Function<T, R> mapper) {
        if (originalValue == null || originalValue.isNull()) {
            return new Value<>(null);
        }
        return new Value<>(mapper.apply(originalValue.get()));
    }
}
