package jp.ac.ait.k24142.library;

import java.util.ArrayList;

public class LibraryMember {
    private final String memberId;
    private String name;
    private ArrayList<Book> borrowedBooks;
    private int maxBorrowLimit;

    public LibraryMember(String memberId, String name) {
        this(memberId, name, 5);
    }

    public LibraryMember(String memberId, String name, int maxBorrowLimit) {
        this.memberId = memberId;
        this.name = name;
        if (maxBorrowLimit <= 0) {
            System.err.println("警告: 貸出上限が無効です。1冊に設定します。");
            this.maxBorrowLimit = 1;
        } else {
            this.maxBorrowLimit = maxBorrowLimit;
        }
        this.borrowedBooks = new ArrayList<>();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }

    public int getCurrentBorrowCount() {
        return borrowedBooks.size();
    }

    public boolean canBorrowMore() {
        return getCurrentBorrowCount() < maxBorrowLimit;
    }

    public boolean borrowBook(Book book) {
        if (!canBorrowMore()) {
            System.out.println("貸出上限に達しています: " + name);
            return false;
        }
        if (book.isBorrowed()) {
            System.out.println("本はすでに貸出中です: " + book.getTitle());
            return false;
        }
        if (book.borrowBook()) {
            borrowedBooks.add(book);
            return true;
        }
        return false;
    }

    public int borrowBooks(Book[] booksToBorrow) {
        int count = 0;
        for (Book book : booksToBorrow) {
            if (borrowBook(book)) {
                count++;
            }
        }
        return count;
    }

    public boolean returnBook(Book book) {
        if (!borrowedBooks.contains(book)) {
            System.out.println("この本は借りていません: " + book.getTitle());
            return false;
        }
        if (book.returnBook()) {
            borrowedBooks.remove(book);
            return true;
        }
        return false;
    }

    public void displayMemberInfo() {
        System.out.println("会員ID: " + memberId);
        System.out.println("名前: " + name);
        System.out.println("最大貸出冊数: " + maxBorrowLimit);
        System.out.println("現在の貸出冊数: " + getCurrentBorrowCount());
        System.out.println("貸出中の本:");
        for (Book b : borrowedBooks) {
            System.out.println("  ・" + b.getTitle() + " (ISBN: " + b.getIsbn() + ")");
        }
    }
}