package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PyqDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PyqDB";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private static final String APP_STORAGE_PATH = "/storage/pyqs/";

    private static final String TABLE_PYQ = "pyqs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UNIT_NAME = "unit_name";
    private static final String COLUMN_PYQ_NAME = "pyq_name";
    private static final String COLUMN_PDF_PATH = "pdf_path";

    public PyqDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        createStorageDirectory();
    }

    private void createStorageDirectory() {
        File directory = new File(context.getFilesDir() + APP_STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PYQ + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_UNIT_NAME + " TEXT, "
                + COLUMN_PYQ_NAME + " TEXT, "
                + COLUMN_PDF_PATH + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PYQ);
        onCreate(db);
    }

    public void addPyq(String unitName, String pyqName, Uri pdfUri) {
        try {
            String fileName = pyqName.replace(" ", "_") + "_" + System.currentTimeMillis() + ".pdf";
            String destinationPath = context.getFilesDir() + APP_STORAGE_PATH + fileName;

            copyFile(pdfUri, destinationPath);

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_UNIT_NAME, unitName);
            values.put(COLUMN_PYQ_NAME, pyqName);
            values.put(COLUMN_PDF_PATH, destinationPath);
            db.insert(TABLE_PYQ, null, values);
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

    public List<String> getPyqsForUnit(String unitName) {
        List<String> pyqs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PYQ_NAME + " FROM " + TABLE_PYQ +
                " WHERE " + COLUMN_UNIT_NAME + "=?", new String[]{unitName});

        if (cursor.moveToFirst()) {
            do {
                pyqs.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pyqs;
    }

    public String getPdfPath(String pyqName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String pdfPath = null;

        Cursor cursor = db.query(TABLE_PYQ,
                new String[]{COLUMN_PDF_PATH},
                COLUMN_PYQ_NAME + "=?",
                new String[]{pyqName},
                null, null, null);

        if (cursor.moveToFirst()) {
            pdfPath = cursor.getString(0);
        }
        cursor.close();
        db.close();

        if (pdfPath != null) {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                return null;
            }
        }
        return pdfPath;
    }

    public void deletePyq(String pyqName) {
        String pdfPath = getPdfPath(pyqName);
        if (pdfPath != null) {
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                pdfFile.delete();
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PYQ, COLUMN_PYQ_NAME + "=?", new String[]{pyqName});
        db.close();
    }
}