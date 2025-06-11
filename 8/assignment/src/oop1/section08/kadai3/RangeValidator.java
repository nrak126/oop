package oop1.section08.kadai3;

/**
 * 数値の範囲検証を行うバリデータ
 */
public class RangeValidator implements InputValidator {
    private final int min;
    private final int max;
    
    /**
     * コンストラクタ
     * @param min 最小値
     * @param max 最大値
     */
    public RangeValidator(int min, int max) {
        this.min = min;
        this.max = max;
    }
    
    @Override
    public void validate(String input) throws ValidationException {
        try {
            int value = Integer.parseInt(input);
            if (value < min || value > max) {
                throw new ValidationException("数値は" + min + "から" + max + "の間である必要があります。");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("数値として解釈できません。");
        }
    }
}
