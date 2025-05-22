import java.util.Scanner;

public class Multiplication100 {
	public static void main(String[] args) {
		// 標準入力をScannerで取得する
		Scanner in = new Scanner(System.in);
		
		// 文字が入力されreturnが押されたらinputLineに代入
		System.out.println("整数値を入力してください");
		String inputLine = in.nextLine();

		// 入力された文字列を数字に変換
		int inputNum = Integer.parseInt(inputLine);

		// 入力された数字に100をかけたものを出力
		System.out.println("計算結果：" + inputNum * 100);

		in.close();
	}
}
