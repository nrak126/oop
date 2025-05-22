package jp.ac.ait.k24142.library;

public class LibrarySystemMain {
    public static void main(String[] args) {
        Library lib = new Library();

        // 本の追加
        Book b1 = new Book("123", "Java入門", "山田太郎");
        Book b2 = new Book("456", "Python実践", "佐藤花子");
        Book b3 = new Book("789", "C++基礎", "中村次郎");

        lib.addBook(b1);
        lib.addBook(b2);
        lib.addBook(b3);

        // 会員の追加
        LibraryMember m1 = new LibraryMember("m001", "鈴木一郎", 2);
        LibraryMember m2 = new LibraryMember("m002", "田中二郎", 1);

        lib.registerMember(m1);
        lib.registerMember(m2);

        // 貸出
        lib.lendBookToMember("m001", "123");
        lib.lendBookToMember("m001", "456");
        lib.lendBookToMember("m002", "123"); // 失敗（既に貸出中）

        // 返却
        lib.receiveBookFromMember("m001", "123");

        // 検索
        Book[] result = lib.searchBook("python");
        System.out.println("=== 'python' の検索結果 ===");
        for (Book b : result) {
            System.out.println(b.getBookDetails());
        }

        // 表示
        lib.displayAllBooks();
        lib.displayAvailableBooks();
        lib.displayAllMembersWithBorrowedBooks();
    }
}