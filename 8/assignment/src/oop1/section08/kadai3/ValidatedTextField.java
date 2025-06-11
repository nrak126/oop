package oop1.section08.kadai3;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * 入力検証機能付きのカスタムテキストフィールド
 */
public class ValidatedTextField extends JTextField {
    private final InputValidator validator;
    
    /**
     * コンストラクタ
     * @param validator 入力検証を行うバリデータ
     * @param columns テキストフィールドの表示幅
     */
    public ValidatedTextField(InputValidator validator, int columns) {
        super(columns);
        this.validator = validator;
        
        // フォーカスリスナーを追加
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // フォーカスが失われた際に入力値を検証
                String inputText = getText();
                try {
                    validator.validate(inputText);
                } catch (ValidationException ex) {
                    // 検証エラーの場合、エラーダイアログを表示
                    JOptionPane.showMessageDialog(
                        ValidatedTextField.this,
                        ex.getMessage(),
                        "入力エラー",
                        JOptionPane.ERROR_MESSAGE
                    );
                    // フォーカスを戻す
                    SwingUtilities.invokeLater(() -> requestFocusInWindow());
                }
            }
        });
    }
}
