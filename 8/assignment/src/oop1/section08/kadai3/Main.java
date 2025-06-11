package oop1.section08.kadai3;

import javax.swing.*;
import java.awt.*;  

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // メインフレームを作成
            JFrame frame = new JFrame("Validation Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 閉じるボタンでプログラム終了
            frame.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // 左寄せのフローレイアウト

            // 年齢入力フィールド (0-100の範囲で検証)
            // RangeValidatorインスタンスを作成し、最小値0、最大値100を設定
            InputValidator ageValidator = new RangeValidator(0, 100);
            // ValidatedTextFieldを作成し、上記のバリデータと表示幅10文字を指定
            ValidatedTextField ageField = new ValidatedTextField(ageValidator, 10);
            frame.add(new JLabel("年齢 (0-100):")); // ラベル
            frame.add(ageField);                    // 検証機能付きテキストフィールド

            // 数量入力フィールド (1-99の範囲で検証) - 別のバリデータインスタンスを使用
            InputValidator quantityValidator = new RangeValidator(1, 99);
            ValidatedTextField quantityField = new ValidatedTextField(quantityValidator, 5);
            frame.add(new JLabel("数量 (1-99):"));
            frame.add(quantityField);

            // フォーカス移動用のダミーボタン（他のコンポーネントにフォーカスを移すため）
            frame.add(new JButton("OK"));

            // フレームのサイズを内容に合わせて自動調整
            frame.pack();
            // フレームを画面中央に表示
            frame.setLocationRelativeTo(null);
            // フレームを表示状態にする
            frame.setVisible(true);
        });
    }
}
