package src;
public class Fibonacci {
    // 通常の再帰によるフィボナッチ数列（末尾再帰ではない）
    public static long fibonacciRecursive(int n) {
        // ガード節：不正な入力のチェック
        if (n < 0) {
            throw new IllegalArgumentException("フィボナッチ数列は負の数に対して定義されていません: " + n);
        }
        
        // ベースケース
        if (n <= 1) {
            return n;
        }
        
        // ここでの再帰呼び出しは末尾ではない
        // なぜなら、戻り値に対してさらに演算（加算）を行っているから
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }
    
    // 末尾再帰版のフィボナッチ数列
    public static long fibonacciTailRecursiveHelper(int n, long a, long b) {
        // ガード節：ベースケース
        if (n == 0) {
            return a;
        }
        if (n == 1) {
            return b;
        }
        
        // 末尾再帰呼び出し：戻り値に対して追加の演算を行わない
        return fibonacciTailRecursiveHelper(n - 1, b, a + b);
    }
    
    public static long fibonacciTailRecursive(int n) {
        // ガード節：不正な入力のチェック
        if (n < 0) {
            throw new IllegalArgumentException("フィボナッチ数列は負の数に対して定義されていません: " + n);
        }
        
        return fibonacciTailRecursiveHelper(n, 0, 1);
    }
    
    // ループ版のフィボナッチ数列
    public static long fibonacciLoop(int n) {
        // ガード節：不正な入力のチェック
        if (n < 0) {
            throw new IllegalArgumentException("フィボナッチ数列は負の数に対して定義されていません: " + n);
        }
        
        // ベースケース
        if (n <= 1) {
            return n;
        }
        
        // 末尾再帰のアキュムレータのパターンをループで再現
        long a = 0;  // F(0)
        long b = 1;  // F(1)
        
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        
        return b;
    }

    public static void main(String[] args) {
        int num = 10;
        
        System.out.println("=== フィボナッチ数列の比較 ===");
        System.out.println("F(" + num + ") 通常の再帰: " + fibonacciRecursive(num));
        System.out.println("F(" + num + ") 末尾再帰: " + fibonacciTailRecursive(num));
        System.out.println("F(" + num + ") ループ: " + fibonacciLoop(num));
        
        // 大きな値でのテスト
        int largeNum = 40;
        System.out.println("\n=== 大きな値でのテスト (n=" + largeNum + ") ===");
        
        // 通常の再帰は非常に遅い
        System.out.println("通常の再帰は計算時間が非常に長いためスキップします。");
        
        // 末尾再帰版
        try {
            long startTime = System.currentTimeMillis();
            long result = fibonacciTailRecursive(largeNum);
            long endTime = System.currentTimeMillis();
            System.out.println("F(" + largeNum + ") 末尾再帰: " + result + " (時間: " + (endTime - startTime) + "ms)");
        } catch (StackOverflowError e) {
            System.err.println("末尾再帰版でStackOverflowError が発生しました！");
        }
        
        // ループ版
        long startTime = System.currentTimeMillis();
        long result = fibonacciLoop(largeNum);
        long endTime = System.currentTimeMillis();
        System.out.println("F(" + largeNum + ") ループ: " + result + " (時間: " + (endTime - startTime) + "ms)");
    }
}
