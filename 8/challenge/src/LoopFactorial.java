package src;
public class LoopFactorial {
    public static long factorialLoop(long n) {
        // 不正な入力に対するガード節
        if (n < 0) {
            throw new IllegalArgumentException("階乗は負の数に対して定義されていません: " + n);
        }
        
        // ループを使って階乗を計算する処理を実装
        long result = 1;
        
        // 末尾再帰のアキュムレータのパターンをループで再現
        for (long i = n; i > 1; i--) {
            result *= i;
        }
        
        return result;
    }

    public static void main(String[] args) {
        int num = 5;
        System.out.println(num + "! = " + factorialLoop(num));

        int largeNum = 20000; // 課題1でエラーになった値
        // 注意: 計算結果のオーバーフローはここでは考慮しない
        try {
            System.out.println("計算中: " + largeNum + "! (Loop)...");
            factorialLoop(largeNum); // StackOverflowErrorが起きないことを確認
            System.out.println(largeNum + "! (Loop) StackOverflowError なしで計算完了しました。");
        } catch (StackOverflowError e) {
            System.err.println("StackOverflowError occurred with loop version!");
        } catch (Exception e) {
            System.out.println("その他の例外が発生: " + e.getClass().getSimpleName());
        }
    }
}
