package oop1.section08.kadai2;

import java.util.List;

/**
 * StringListProcessorインターフェースのデフォルト実装
 */
public class DefaultStringListProcessor implements StringListProcessor {
    
    @Override
    public String concatenateAndUppercase(List<String> texts) throws InvalidCollectionDataException, EmptyCollectionException, NullItemInCollectionException {
        // nullチェック
        if (texts == null) {
            throw new InvalidCollectionDataException("Input list of strings cannot be null.");
        }
        
        // 空リストチェック
        if (texts.isEmpty()) {
            throw new EmptyCollectionException("Input list of strings cannot be empty for concatenation.");
        }
        
        // null要素チェックと連結処理
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < texts.size(); i++) {
            String current = texts.get(i);
            if (current == null) {
                throw new NullItemInCollectionException("List of strings contains a null item which is not allowed for concatenation, at index " + i, i);
            }
            
            result.append(current);
        }
        
        return result.toString().toUpperCase();
    }
}
