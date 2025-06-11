package oop1.section08.kadai1;

import java.util.List;

/**
 * SafeCollectionProcessorインターフェースのデフォルト実装
 */
public class DefaultSafeCollectionProcessor implements SafeCollectionProcessor {
    
    @Override
    public int sumPositiveNumbers(List<Integer> numbers) {
        // 早期リターン: リストがnullまたは空の場合
        if (numbers == null || numbers.isEmpty()) {
            return 0;
        }
        
        int sum = 0;
        for (Integer number : numbers) {
            // null要素は無視し、正の数のみを合計に加える
            if (number != null && number > 0) {
                sum += number;
            }
        }
        
        return sum;
    }
    
    @Override
    public int countLongStrings(List<String> texts, int minLength) {
        // 早期リターン: リストがnullまたは空の場合
        if (texts == null || texts.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (String text : texts) {
            // null要素は無視し、指定長以上の文字列のみをカウント
            if (text != null && text.length() >= minLength) {
                count++;
            }
        }
        
        return count;
    }
}
