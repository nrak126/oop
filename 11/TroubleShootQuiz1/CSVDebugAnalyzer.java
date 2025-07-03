import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVDebugAnalyzer {
    public static void main(String[] args) {
        String filepath = "personal_information.csv";
        Charset charset = Charset.forName("Shift-JIS");
        
        int lineNumber = 0;
        int successCount = 0;
        int errorCount = 0;
        
        try (BufferedReader br = Files.newBufferedReader(Path.of(filepath), charset)) {
            // ヘッダースキップ
            br.readLine();
            lineNumber++;
            
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    // CSVパースを試行
                    String[] cells = line.split(",");
                    if (cells.length < 12) {
                        System.err.println("Line " + lineNumber + ": Too few fields (" + cells.length + ")");
                        System.err.println("Content: " + line);
                        errorCount++;
                        continue;
                    }
                    
                    // PersonalInfo作成を試行
                    PersonalInfo pi = new PersonalInfo(
                            cells[1],
                            cells[2], 
                            cells[3],
                            cells[4],
                            cells[5],
                            cells[6],
                            cells[7],
                            cells[8],
                            cells[9],
                            Integer.parseInt(cells[10]),
                            cells[11]
                    );
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("Line " + lineNumber + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    String[] cells = line.split(",");
                    System.err.println("Field count: " + cells.length);
                    if (cells.length >= 10) {
                        System.err.println("Field[10] (age): '" + cells[10] + "'");
                    }
                    if (cells.length >= 11) {
                        System.err.println("Field[11] (blood): '" + cells[11] + "'");
                    }
                    System.err.println("Content: " + line);
                    System.err.println("---");
                }
            }
        } catch (Exception e) {
            System.err.println("File reading error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Total lines: " + lineNumber);
        System.out.println("Success: " + successCount);
        System.out.println("Errors: " + errorCount);
        System.out.println("Expected total data lines: 3000");
    }
}
