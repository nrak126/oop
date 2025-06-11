package oop1.section08.kadai2;

import java.util.List;

/**
 * NumberListAnalyzerインターフェースのデフォルト実装
 */
public class DefaultNumberListAnalyzer implements NumberListAnalyzer {
    
    @Override
    public int findMaximumValue(List<Integer> numbers) throws InvalidCollectionDataException, EmptyCollectionException, NullItemInCollectionException {
        // nullチェック
        if (numbers == null) {
            throw new InvalidCollectionDataException("Input list cannot be null.");
        }
        
        // 空リストチェック
        if (numbers.isEmpty()) {
            throw new EmptyCollectionException("Input list cannot be empty for finding maximum value.");
        }
        
        // null要素チェックと最大値計算
        Integer maxValue = null;
        for (int i = 0; i < numbers.size(); i++) {
            Integer current = numbers.get(i);
            if (current == null) {
                throw new NullItemInCollectionException("List contains a null item which is not allowed for maximum value calculation, at index " + i, i);
            }
            
            if (maxValue == null || current > maxValue) {
                maxValue = current;
            }
        }
        
        return maxValue;
    }
}
