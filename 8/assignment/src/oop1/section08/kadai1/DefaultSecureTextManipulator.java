package oop1.section08.kadai1;

/**
 * SecureTextManipulatorインターフェースのデフォルト実装
 */
public class DefaultSecureTextManipulator implements SecureTextManipulator {
    
    @Override
    public String getFirstNCharsAsUpperCase(String text, int n) {
        // 早期リターン: textがnullまたは空文字列、またはnが0以下の場合
        if (text == null || text.isEmpty() || n <= 0) {
            return "";
        }
        
        // 文字列の長さがnより短い場合は文字列全体を使用
        int endIndex = Math.min(text.length(), n);
        String substring = text.substring(0, endIndex);
        
        return substring.toUpperCase();
    }
}
