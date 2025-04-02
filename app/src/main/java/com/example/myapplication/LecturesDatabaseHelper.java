package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LecturesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LecturesDB";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private static final String TABLE_LECTURES = "lectures";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UNIT_NAME = "unit_name";
    private static final String COLUMN_LECTURE_NAME = "lecture_name";
    private static final String COLUMN_VIDEO_PATH = "video_path";
    private static final String COLUMN_NOTES_PATH = "notes_path";

    public LecturesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_LECTURES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UNIT_NAME + " TEXT, " +
                COLUMN_LECTURE_NAME + " TEXT, " +
                COLUMN_VIDEO_PATH + " TEXT, " +
                COLUMN_NOTES_PATH + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURES);
        onCreate(db);
    }

    public long addLecture(String unitName, String lectureName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIT_NAME, unitName);
        values.put(COLUMN_LECTURE_NAME, lectureName);
        return db.insert(TABLE_LECTURES, null, values);
    }

    public ArrayList<String> getLecturesForUnit(String unitName) {
        ArrayList<String> lectures = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LECTURES, new String[]{COLUMN_LECTURE_NAME},
                COLUMN_UNIT_NAME + "=?", new String[]{unitName}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                lectures.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lectures;
    }


    public void deleteLecture(String lectureName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LECTURES, COLUMN_LECTURE_NAME + "=?", new String[]{lectureName});
    }

    public String getVideoPath(String lectureName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LECTURES, new String[]{COLUMN_VIDEO_PATH},
                COLUMN_LECTURE_NAME + "=?", new String[]{lectureName}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }

    public String getNotesPath(String lectureName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LECTURES, new String[]{COLUMN_NOTES_PATH},
                COLUMN_LECTURE_NAME + "=?", new String[]{lectureName}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }

    public void updateVideoPath(String lectureName, Uri videoUri) {
        try {
            String fileName = lectureName.replace(" ", "_") + ".mp4";
            String destinationPath = context.getFilesDir().getAbsolutePath() + "/" + fileName;

            copyFile(videoUri, destinationPath);

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_VIDEO_PATH, destinationPath);
            db.update(TABLE_LECTURES, values, COLUMN_LECTURE_NAME + "=?", new String[]{lectureName});
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNotesPath(String lectureName, Uri notesUri) {
        try {
            String fileName = lectureName.replace(" ", "_") + ".pdf";
            String destinationPath = context.getFilesDir().getAbsolutePath() + "/" + fileName;

            copyFile(notesUri, destinationPath);

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTES_PATH, destinationPath);
            db.update(TABLE_LECTURES, values, COLUMN_LECTURE_NAME + "=?", new String[]{lectureName});
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(Uri sourceUri, String destinationPath) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
        FileOutputStream outputStream = new FileOutputStream(destinationPath);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
    }
}
