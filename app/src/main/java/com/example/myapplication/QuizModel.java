package com.example.myapplication;

public class QuizModel {
    private int id;
    private String question, optionA, optionB, optionC, optionD, correctAnswer;

    public QuizModel(int id, String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
}
