package com.example.myapplication;

import java.util.ArrayList;

public class Question {
    public long id;
    public String text;
    public ArrayList<Option> options;

    public Question(long id, String text) {
        this.id = id;
        this.text = text;
        this.options = new ArrayList<>();
    }
}