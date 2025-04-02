package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class QuizDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "quizzes";

    public QuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question TEXT, optionA TEXT, optionB TEXT, optionC TEXT, optionD TEXT, correct TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addQuiz(QuizModel quiz) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", quiz.getQuestion());
        values.put("optionA", quiz.getOptionA());
        values.put("optionB", quiz.getOptionB());
        values.put("optionC", quiz.getOptionC());
        values.put("optionD", quiz.getOptionD());
        values.put("correct", quiz.getCorrectAnswer());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<QuizModel> getAllQuizzes() {
        List<QuizModel> quizList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                quizList.add(new QuizModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return quizList;
    }
}
