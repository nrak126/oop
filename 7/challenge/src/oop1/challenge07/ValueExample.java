package oop1.challenge07;

/**
 * Value<T>クラスとValueUtilsの使用例を示すクラス
 */
public class ValueExample {
    public static void main(String[] args) {
        // 例1: 文字列を保持するValueオブジェクトの作成と操作
        Value<String> stringValue = ValueUtils.of("Hello, World!");
        System.out.println("例1:");
        System.out.println("元の値: " + stringValue);
        Value<Integer> lengthValue = ValueUtils.map(stringValue, String::length);
        System.out.println("文字列の長さ: " + lengthValue);

        // 例2: nullを含むValueオブジェクトの操作
        Value<String> nullValue = ValueUtils.of(null);
        System.out.println("\n例2:");
        System.out.println("nullValue.isNull(): " + nullValue.isNull());
        Value<Integer> mappedNullValue = ValueUtils.map(nullValue, String::length);
        System.out.println("null値のマッピング結果: " + mappedNullValue);

        // 例3: Valueオブジェクトの等価性の確認
        Value<Integer> value1 = ValueUtils.of(42);
        Value<Integer> value2 = ValueUtils.of(42);
        Value<Integer> value3 = ValueUtils.of(100);
        
        System.out.println("\n例3:");
        System.out.println("value1: " + value1);
        System.out.println("value2: " + value2);
        System.out.println("value3: " + value3);
        System.out.println("value1.equals(value2): " + value1.equals(value2));
        System.out.println("value1.equals(value3): " + value1.equals(value3));
    }
}
