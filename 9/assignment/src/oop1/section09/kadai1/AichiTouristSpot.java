package oop1.section09.kadai1;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AichiTouristSpot {
    // 愛工大の緯度経度
    private static final double AIT_LATITUDE = 35.1834122;
    private static final double AIT_LONGITUDE = 137.1130419;
    
    // CSVファイル名のリスト
    private static final String[] CSV_FILES = {
        "c200326.csv", // ルートスポット
        "c200328.csv", // 寄ってこみゃ
        "c200329.csv", // 地域資源スポット（風景・自然）
        "c200330.csv", // 地域資源スポット（施設）
        "c200361.csv", // 文化財マップ建造物
        "c200362.csv", // 文化財マップ名勝
        "c200363.csv", // 文化財マップ天然記念物
        "c200364.csv"  // 文化財マップ史跡
    };
    
    // 観光スポットデータクラス
    static class TouristSpotData {
        double latitude;
        double longitude;
        double distance;
        String name;
        
        TouristSpotData(double latitude, double longitude, String name) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.distance = calculateDistance(latitude, longitude);
        }
        
        // 愛工大からの距離を計算
        private double calculateDistance(double lat, double lng) {
            return Math.sqrt(Math.pow(lat - AIT_LATITUDE, 2) + Math.pow(lng - AIT_LONGITUDE, 2));
        }
    }
    
    public static void main(String[] args) {
        List<TouristSpotData> touristSpots = new ArrayList<>();
        
        // 各CSVファイルを処理
        for (String fileName : CSV_FILES) {
            File file = new File(fileName);
            if (file.exists()) {
                System.out.println("Processing: " + fileName);
                processCSVFile(fileName, touristSpots);
            } else {
                System.out.println("File not found: " + fileName);
            }
        }
        
        // 距離順でソート
        touristSpots.sort(Comparator.comparingDouble(spot -> spot.distance));
        
        // TouristSpot.csvに出力
        outputToCSV(touristSpots, "TouristSpot.csv");
        
        System.out.println("処理完了: " + touristSpots.size() + " 件のデータを処理しました");
    }
    
    // CSVファイルを処理
    private static void processCSVFile(String fileName, List<TouristSpotData> touristSpots) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), Charset.forName("MS932")))) {
            
            String line;
            boolean isFirstLine = true;
            String[] headers = null;
            int shapeColumnIndex = -1;
            int nameColumnIndex = -1;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    // ヘッダー行を解析
                    headers = parseCSVLine(line);
                    shapeColumnIndex = findShapeColumn(headers);
                    nameColumnIndex = findNameColumn(headers);
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = parseCSVLine(line);
                if (values.length > Math.max(shapeColumnIndex, nameColumnIndex) && 
                    shapeColumnIndex >= 0 && nameColumnIndex >= 0) {
                    
                    String shapeData = values[shapeColumnIndex];
                    String name = values[nameColumnIndex];
                    
                    // 緯度経度を抽出
                    double[] coordinates = extractCoordinates(shapeData);
                    if (coordinates != null && !name.trim().isEmpty()) {
                        touristSpots.add(new TouristSpotData(coordinates[0], coordinates[1], name.trim()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + fileName + ": " + e.getMessage());
        }
    }
    
    // CSVの行をパース（簡易版）
    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());
        
        return values.toArray(new String[0]);
    }
    
    // 形状データ（緯度経度）の列を見つける
    private static int findShapeColumn(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].toLowerCase();
            if (header.contains("形状") || header.contains("shape") || header.contains("point")) {
                return i;
            }
        }
        return -1;
    }
    
    // 名称の列を見つける
    private static int findNameColumn(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].toLowerCase();
            if (header.contains("名称") || header.contains("名前") || header.contains("施設名") || 
                header.contains("スポット名") || header.contains("title") || header.contains("name")) {
                return i;
            }
        }
        return -1;
    }
    
    // POINT(経度 緯度)から緯度経度を抽出
    private static double[] extractCoordinates(String pointString) {
        if (pointString == null || pointString.trim().isEmpty()) {
            return null;
        }
        
        // POINT(136.80953740291 35.3576773867654) の形式を想定
        Pattern pattern = Pattern.compile("POINT\\(([0-9.-]+)\\s+([0-9.-]+)\\)");
        Matcher matcher = pattern.matcher(pointString);
        
        if (matcher.find()) {
            try {
                double longitude = Double.parseDouble(matcher.group(1));
                double latitude = Double.parseDouble(matcher.group(2));
                return new double[]{latitude, longitude};
            } catch (NumberFormatException e) {
                System.err.println("Invalid coordinate format: " + pointString);
            }
        }
        
        return null;
    }
    
    // CSVファイルに出力
    private static void outputToCSV(List<TouristSpotData> touristSpots, String fileName) {
        // 既存ファイルがあれば削除
        File outputFile = new File(fileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "UTF-8"))) {
            
            // ヘッダー行
            writer.println("緯度情報,経度情報,愛工大からの距離,データ名");
            
            // データ行
            for (TouristSpotData spot : touristSpots) {
                writer.printf("%.10f,%.10f,%.10f,%s%n",
                    spot.latitude, spot.longitude, spot.distance, spot.name);
            }
            
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}
