import java.util.Scanner;

public class MessageInput {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		// 文字を表示して入力を待機
		System.out.println("こんにちは、メッセージをどうぞ");
		// 入力された文字をinputLineに入れる
		String inputLine = in.nextLine();

		// 受信したメッセージを表示
		System.out.println("メッセージを受信しました");
		System.out.println("----");
		System.out.println(inputLine);
		System.out.println("----");

		in.close();
	}
}