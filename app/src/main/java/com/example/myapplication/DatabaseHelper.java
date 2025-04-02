package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyApplicationDB";
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_SUBJECTS = "subjects";
    private static final String TABLE_UNITS = "units";
    private static final String TABLE_UNIT_ACTIVITIES = "unit_activities";
    private static final String TABLE_LECTURES = "lectures";

    // Common Column Names
    private static final String COLUMN_ID = "id";

    // Subjects Table Columns
    private static final String COLUMN_SUBJECT_NAME = "subject_name";

    // Units Table Columns
    private static final String COLUMN_UNIT_NAME = "unit_name";

    // Unit Activities Table Columns
    private static final String COLUMN_ACTIVITY_TYPE = "activity_type";

    // Lectures Table Columns
    private static final String COLUMN_LECTURE_NAME = "lecture_name";
    private static final String COLUMN_VIDEO_PATH = "video_path";
    private static final String COLUMN_NOTES_PATH = "notes_path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Subjects Table
        String createSubjectsTable = "CREATE TABLE " + TABLE_SUBJECTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT_NAME + " TEXT UNIQUE)";
        db.execSQL(createSubjectsTable);

        // Create Units Table
        String createUnitsTable = "CREATE TABLE " + TABLE_UNITS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT_NAME + " TEXT, " +
                COLUMN_UNIT_NAME + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_SUBJECT_NAME + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_SUBJECT_NAME + ") ON DELETE CASCADE)";
        db.execSQL(createUnitsTable);

        // Create Unit Activities Table
        String createUnitActivitiesTable = "CREATE TABLE " + TABLE_UNIT_ACTIVITIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UNIT_NAME + " TEXT, " +
                COLUMN_ACTIVITY_TYPE + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_UNIT_NAME + ") REFERENCES " + TABLE_UNITS + "(" + COLUMN_UNIT_NAME + ") ON DELETE CASCADE)";
        db.execSQL(createUnitActivitiesTable);

        // Create Lectures Table
        String createLecturesTable = "CREATE TABLE " + TABLE_LECTURES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UNIT_NAME + " TEXT, " +
                COLUMN_LECTURE_NAME + " TEXT, " +
                COLUMN_VIDEO_PATH + " TEXT, " +
                COLUMN_NOTES_PATH + " TEXT)";
        db.execSQL(createLecturesTable);

        String createSyllabusTable = "CREATE TABLE IF NOT EXISTS syllabus (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject_name TEXT UNIQUE, " +
                "syllabus_path TEXT)";
        db.execSQL(createSyllabusTable);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNIT_ACTIVITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURES);
        onCreate(db);
    }

    // Methods for Subjects
    public boolean addSubject(String subjectName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT_NAME, subjectName);
        long result = db.insert(TABLE_SUBJECTS, null, values);
        return result != -1;
    }

    public List<String> getSubjects() {
        List<String> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SUBJECT_NAME + " FROM " + TABLE_SUBJECTS, null);
        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subjects;
    }

    public void deleteSubject(String subjectName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECTS, COLUMN_SUBJECT_NAME + "=?", new String[]{subjectName});
    }

    // Methods for Units
    public boolean addUnit(String subjectName, String unitName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subject_name", subjectName);
        values.put("unit_name", subjectName + " - " + unitName);  // ✅ Save full name

        long result = db.insert("units", null, values);
        return result != -1;
    }

    public List<String> getUnits(String subjectName) {
        List<String> units = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT unit_name FROM units WHERE subject_name=?", new String[]{subjectName});

        if (cursor.moveToFirst()) {
            do {
                units.add(cursor.getString(0));  // ✅ Now returns "Subject - Unit X"
            } while (cursor.moveToNext());
        }
        cursor.close();
        return units;
    }

    public void deleteUnit(String subjectName, String unitName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_UNITS, COLUMN_SUBJECT_NAME + "=? AND " + COLUMN_UNIT_NAME + "=?",
                new String[]{subjectName, unitName});
    }

    // Methods for Unit Activities
    public boolean insertUnitActivity(String unitName, String activityType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIT_NAME, unitName);
        values.put(COLUMN_ACTIVITY_TYPE, activityType);
        long result = db.insert(TABLE_UNIT_ACTIVITIES, null, values);
        return result != -1;
    }

    public List<String> getUnitActivities(String unitName) {
        List<String> activityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ACTIVITY_TYPE + " FROM " + TABLE_UNIT_ACTIVITIES +
                " WHERE " + COLUMN_UNIT_NAME + "=?", new String[]{unitName});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                activityList.add(cursor.getString(0)); // Get activity type from Cursor
            }
            cursor.close();
        }

        db.close();
        return activityList;
    }

    public void deleteUnitActivity(String unitName, String activityType) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_UNIT_ACTIVITIES, COLUMN_UNIT_NAME + "=? AND " + COLUMN_ACTIVITY_TYPE + "=?",
                new String[]{unitName, activityType});
    }

    // Methods for Lectures
    public long addLecture(String unitName, String lectureName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIT_NAME, unitName);
        values.put(COLUMN_LECTURE_NAME, lectureName);
        long id = db.insert(TABLE_LECTURES, null, values);
        db.close();
        return id;
    }

    public ArrayList<String> getLecturesForUnit(String unitName) {
        ArrayList<String> lectures = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LECTURES,
                new String[]{COLUMN_LECTURE_NAME},
                COLUMN_UNIT_NAME + "=?",
                new String[]{unitName},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                lectures.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lectures;
    }

    public void deleteLecture(String lectureName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LECTURES, COLUMN_LECTURE_NAME + "=?", new String[]{lectureName});
        db.close();
    }
    public boolean saveSyllabusPath(String subjectName, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subject_name", subjectName);
        values.put("syllabus_path", path);

        long result = db.replace("syllabus", null, values); // ✅ Uses REPLACE for updating existing data
        return result != -1;
    }


    public String getSyllabusPath(String subjectName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT syllabus_path FROM syllabus WHERE subject_name = ?",
                new String[]{subjectName});

        if (cursor.moveToFirst()) {
            return cursor.getString(0); // ✅ Returns the stored syllabus path
        }
        cursor.close();
        return null;
    }


    public void logSyllabusData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM syllabus", null);

        if (cursor.moveToFirst()) {
            do {
                String subject = cursor.getString(1);
                String path = cursor.getString(2);
                Log.d("SYLLABUS_DATA", "Subject: " + subject + " | Syllabus Path: " + path);
            } while (cursor.moveToNext());
        } else {
            Log.d("SYLLABUS_DATA", "No syllabus data found.");
        }
        cursor.close();
    }

}

