package jp.ac.ait.k24142.library;

public class LibraryMain {
    public static void main(String[] args) {
        Book[] books = {
            new Book("1234567890", "Java入門", "山田太郎"),
            new Book("2345678901", "C++実践", "佐藤花子"),
            new Book("3456789012", "Pythonで学ぶAI", "鈴木一郎"),
            new Book("4567890123", "データベース設計", "田中宏"),
            new Book("5678901234", "ネットワーク基礎", "井上愛"),
            new Book("6789012345", "アルゴリズム図鑑", "中村圭"),
            new Book("7890123456", "セキュリティ入門", "山本涼"),
            new Book("8901234567", "Linux操作", "小林誠"),
            new Book("9012345678", "HTML&CSS", "松本陽"),
            new Book("0123456789", "ソフトウェア工学", "西村舞")
        };

        LibraryMember member1 = new LibraryMember("M001", "佐々木優");
        LibraryMember member2 = new LibraryMember("M002", "高橋健", 3);

        System.out.println("=== 1冊貸出 ===");
        member1.borrowBook(books[0]);
        member1.displayMemberInfo();

        System.out.println("\n=== 複数冊貸出 ===");
        Book[] toBorrow = { books[1], books[2], books[3] };
        int borrowedCount = member1.borrowBooks(toBorrow);
        System.out.println(borrowedCount + "冊を貸出しました。");
        member1.displayMemberInfo();

        System.out.println("\n=== 貸出上限の確認 ===");
        member2.borrowBook(books[4]);
        member2.borrowBook(books[5]);
        member2.borrowBook(books[6]);
        member2.borrowBook(books[7]); // 上限オーバー
        member2.displayMemberInfo();

        System.out.println("\n=== 返却処理 ===");
        member1.returnBook(books[0]);
        member1.displayMemberInfo();
    }
}