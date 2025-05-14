package jp.ac.ait.k24142.library;

public class Book {
    private final String isbn;
    private String title;
    private String author;
    private boolean isBorrowed = false;

    public Book(String isbn, String title, String author) {
        if (isbn == null || isbn.isEmpty()) {
            System.err.println("警告: ISBNが無効です。");
            this.isbn = "UNKNOWN";
        } else {
            this.isbn = isbn;
        }

        if (title == null || title.isEmpty()) {
            System.err.println("警告: タイトルが無効です。");
            this.title = "No Title";
        } else {
            this.title = title;
        }

        if (author == null || author.isEmpty()) {
            System.err.println("警告: 著者が無効です。");
            this.author = "Unknown Author";
        } else {
            this.author = author;
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public boolean borrowBook() {
        if (isBorrowed) {
            System.out.println("すでに貸出中です: " + title);
            return false;
        }
        isBorrowed = true;
        return true;
    }

    public boolean returnBook() {
        if (!isBorrowed) {
            System.out.println("この本はすでに返却されています: " + title);
            return false;
        }
        isBorrowed = false;
        return true;
    }

    public String getBookDetails() {
        return String.format("ISBN: %s, タイトル: %s, 著者: %s, 貸出中: %s", isbn, title, author, isBorrowed ? "はい" : "いいえ");
    }
}