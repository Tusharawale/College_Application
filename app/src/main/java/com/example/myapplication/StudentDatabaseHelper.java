package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "CREATE TABLE documents (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "doc_name TEXT NOT NULL, " +
                    "doc_type TEXT NOT NULL, " +
                    "file_name TEXT NOT NULL, " +
                    "file_uri TEXT NOT NULL);";
    private static final String CREATE_NOTES_TABLE =
            "CREATE TABLE notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "content TEXT NOT NULL);";
    public StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_NOTES_TABLE); // Notes table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS documents");
        onCreate(db);
    }

}
