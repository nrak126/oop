package jp.ac.ait.k24142.library;

import java.util.ArrayList;
import java.util.Locale;

public class Library {
    private ArrayList<Book> books;
    private ArrayList<LibraryMember> members;

    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    // --- 蔵書管理 ---
    public boolean addBook(Book book) {
        for (Book b : books) {
            if (b.getIsbn().equals(book.getIsbn())) {
                System.out.println("同じISBNの本がすでに登録されています: " + book.getIsbn());
                return false;
            }
        }
        books.add(book);
        return true;
    }

    public boolean removeBook(String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                if (b.isBorrowed()) {
                    System.out.println("貸出中の本は削除できません: " + isbn);
                    return false;
                }
                books.remove(b);
                return true;
            }
        }
        System.out.println("該当する本が見つかりません: " + isbn);
        return false;
    }

    public Book findBookByIsbn(String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    // --- 会員管理 ---
    public boolean registerMember(LibraryMember member) {
        for (LibraryMember m : members) {
            if (m.getMemberId().equals(member.getMemberId())) {
                System.out.println("同じ会員IDがすでに存在します: " + member.getMemberId());
                return false;
            }
        }
        members.add(member);
        return true;
    }

    public boolean unregisterMember(String memberId) {
        for (LibraryMember m : members) {
            if (m.getMemberId().equals(memberId)) {
                if (m.getCurrentBorrowCount() > 0) {
                    System.out.println("本を借りている会員は退会できません: " + memberId);
                    return false;
                }
                members.remove(m);
                return true;
            }
        }
        System.out.println("該当する会員が見つかりません: " + memberId);
        return false;
    }

    public LibraryMember findMemberById(String memberId) {
        for (LibraryMember m : members) {
            if (m.getMemberId().equals(memberId)) {
                return m;
            }
        }
        return null;
    }

    // --- 貸出・返却 ---
    public boolean lendBookToMember(String memberId, String isbn) {
        LibraryMember member = findMemberById(memberId);
        Book book = findBookByIsbn(isbn);
        if (member == null) {
            System.out.println("会員が見つかりません: " + memberId);
            return false;
        }
        if (book == null) {
            System.out.println("本が見つかりません: " + isbn);
            return false;
        }
        return member.borrowBook(book);
    }

    public boolean receiveBookFromMember(String memberId, String isbn) {
        LibraryMember member = findMemberById(memberId);
        Book book = findBookByIsbn(isbn);
        if (member == null) {
            System.out.println("会員が見つかりません: " + memberId);
            return false;
        }
        if (book == null) {
            System.out.println("本が見つかりません: " + isbn);
            return false;
        }
        return member.returnBook(book);
    }

    // --- 検索 ---
    public Book[] searchBook(String keyword) {
        keyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword) || b.getAuthor().toLowerCase().contains(keyword)) {
                result.add(b);
            }
        }
        return result.toArray(new Book[0]);
    }

    // --- 表示 ---
    public void displayAllBooks() {
        System.out.println("=== 図書館蔵書一覧 ===");
        for (Book b : books) {
            System.out.println(b.getBookDetails());
        }
    }

    public void displayAvailableBooks() {
        System.out.println("=== 貸出可能な本 ===");
        for (Book b : books) {
            if (!b.isBorrowed()) {
                System.out.println(b.getBookDetails());
            }
        }
    }

    public void displayAllMembersWithBorrowedBooks() {
        System.out.println("=== 会員情報と貸出中の本 ===");
        for (LibraryMember m : members) {
            m.displayMemberInfo();
            System.out.println();
        }
    }
}