package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AssignmentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "assignments.db";
    private static final int DATABASE_VERSION = 1;

    public AssignmentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE assignments (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, pdf_data BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS assignments");
        onCreate(db);
    }
}
