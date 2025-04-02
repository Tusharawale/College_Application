package com.example.myapplication;

public class Option {
    public long id;
    public String text;
    public boolean isCorrect;

    public Option(long id, String text, boolean isCorrect) {
        this.id = id;
        this.text = text;
        this.isCorrect = isCorrect;
    }
}