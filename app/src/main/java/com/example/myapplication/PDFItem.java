package com.example.myapplication;

public class PDFItem {
    private int id;
    private String bookName;
    private String author;

    public PDFItem(int id, String bookName, String author) {
        this.id = id;
        this.bookName = bookName;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }
}
