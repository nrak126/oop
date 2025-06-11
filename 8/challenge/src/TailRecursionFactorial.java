package src;
public class TailRecursionFactorial {
    public static long factorialTailRecursive(long n, long accumulator) {
        // ガード節：ベースケースの確認
        if (n <= 1) {
            return accumulator;
        }
        // 末尾再帰呼び出しを実装
        return factorialTailRecursive(n - 1, n * accumulator);
    }

    public static long factorial(long n) {
        // 不正な入力に対するガード節
        if (n < 0) {
            throw new IllegalArgumentException("階乗は負の数に対して定義されていません: " + n);
        }
        return factorialTailRecursive(n, 1);
    }

    public static void main(String[] args) {
        int num = 5; // 小さな値でテスト
        System.out.println(num + "! = " + factorial(num));

        // 大きな値で試し、スタックオーバーフローが発生することを確認する
        int largeNum = 20000;
        try {
            System.out.println("計算中: " + largeNum + "! ...");
            long result = factorial(largeNum);
            System.out.println(largeNum + "! = " + result);
        } catch (StackOverflowError e) {
            System.err.println("StackOverflowError が発生しました！");
            System.err.println("末尾再帰最適化が行われていないため、深い再帰でスタックが枯渇しました。");
        }
        
        // 注意: 非常に大きな数を指定すると計算結果がlongの範囲を超えるため、
        // スタックオーバーフローの確認に焦点を当てる。
    }
}
