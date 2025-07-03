import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVFileConverter {
    public static void main(String[] args) {

        String filepath = "personal_information.csv";
        // エンコーディングエラーを適切に処理
        Charset charset = Charset.forName("Shift-JIS");

        // 読み込んだCSVデータの格納先
        List<PersonalInfo> data = new ArrayList<>();
        int lineNumber = 0;
        int errorCount = 0;
        
        try (InputStreamReader isr = new InputStreamReader(
                Files.newInputStream(Path.of(filepath)), charset);
             BufferedReader br = new BufferedReader(isr)) {
            
            // 先頭の行は列タイトルなので読み込んで捨てる
            br.readLine();
            lineNumber++;

            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    PersonalInfo pi = getPersonalInfo(line);
                    data.add(pi);
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("Error processing line " + lineNumber + ": " + e.getMessage());
                    // エラーの詳細をログに出力するが処理は続行
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            // エンコーディングエラーの場合も処理を続行
        }

        System.err.println("Total lines processed: " + lineNumber);
        System.err.println("Successful records: " + data.size());
        System.err.println("Error count: " + errorCount);

        // データの並び替えを行う
        data.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.nameKana(), b.nameKana()));

        // CSVファイルの変換結果を出力しつつ、出力用のファイルのデータを生成する
        StringBuilder outputData = new StringBuilder("氏名,住所,年齢,血液型");
        System.out.println("氏名,住所,年齢,血液型");
        outputData.append(System.lineSeparator());
        data.forEach((pi) -> {
            outputData.append(pi.toCSVRow());
            outputData.append(System.lineSeparator());

            System.out.println(pi.toCSVRow());
        });

        // ファイル書き込み
        try {
            Files.writeString(Path.of("output.csv"), outputData.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PersonalInfo getPersonalInfo(String line) {
        // 改良されたCSVパーサー：ダブルクォートとカンマを適切に処理
        List<String> fields = parseCSVLine(line);
        
        if (fields.size() < 12) {
            throw new IllegalArgumentException("Insufficient fields: " + fields.size());
        }

        PersonalInfo pi = new PersonalInfo(
                fields.get(1),  // 氏名
                fields.get(2),  // 氏名（カタカナ）
                cleanEmailAddress(fields.get(3)), // メールアドレス（クリーニング済み）
                fields.get(4),  // 郵便番号
                fields.get(5),  // 住所1
                fields.get(6),  // 住所2
                fields.get(7),  // 住所3
                fields.get(8),  // 住所4
                fields.get(9),  // 住所5
                Integer.parseInt(fields.get(10)), // 年齢
                fields.get(11)  // 血液型
        );
        return pi;
    }
    
    /**
     * 改良されたCSVパーサー
     * ダブルクォートで囲まれたフィールドとエスケープされたカンマを適切に処理
     */
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // エスケープされたダブルクォート
                    field.append('"');
                    i++; // 次の文字をスキップ
                } else {
                    // クォートの開始または終了
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // フィールドの区切り
                result.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        
        // 最後のフィールドを追加
        result.add(field.toString().trim());
        
        return result;
    }
    
    /**
     * メールアドレスのクリーニング
     * 複数のメールアドレスが含まれている場合は最初のものを使用
     */
    private static String cleanEmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            return email;
        }
        
        // カンマで分割されている場合は最初のメールアドレスを使用
        String[] parts = email.split(",");
        if (parts.length > 1) {
            // 最初の有効なメールアドレスを探す
            for (String part : parts) {
                String trimmed = part.trim();
                if (isValidEmail(trimmed)) {
                    return trimmed;
                }
            }
        }
        
        return email.trim();
    }
    
    /**
     * 簡単なメールアドレス検証
     */
    private static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
