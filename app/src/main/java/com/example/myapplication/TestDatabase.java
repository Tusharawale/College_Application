package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapplication.TestQuestion;
import java.util.ArrayList;
import java.util.List;

public class TestDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "testApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_QUESTIONS = "questions";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_OPTION_A = "optionA";
    private static final String COLUMN_OPTION_B = "optionB";
    private static final String COLUMN_OPTION_C = "optionC";
    private static final String COLUMN_OPTION_D = "optionD";
    private static final String COLUMN_CORRECT = "correctAnswer";

    public TestDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION + " TEXT, "
                + COLUMN_OPTION_A + " TEXT, "
                + COLUMN_OPTION_B + " TEXT, "
                + COLUMN_OPTION_C + " TEXT, "
                + COLUMN_OPTION_D + " TEXT, "
                + COLUMN_CORRECT + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        onCreate(db);
    }

    public void addQuestion(TestQuestion question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, question.getQuestion());
        values.put(COLUMN_OPTION_A, question.getOptionA());
        values.put(COLUMN_OPTION_B, question.getOptionB());
        values.put(COLUMN_OPTION_C, question.getOptionC());
        values.put(COLUMN_OPTION_D, question.getOptionD());
        values.put(COLUMN_CORRECT, question.getCorrectAnswer());

        db.insert(TABLE_QUESTIONS, null, values);
        db.close();
    }

    public List<TestQuestion> getAllQuestions() {
        List<TestQuestion> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS, null);

        if (cursor.moveToFirst()) {
            do {
                TestQuestion question = new TestQuestion(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );
                questionList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }// ✅ Delete a Question
    public void deleteQuestion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("questions", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

}
