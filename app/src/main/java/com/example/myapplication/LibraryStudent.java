package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class LibraryStudent extends AppCompatActivity {

    private LibraryDatabaseHelper dbHelper;
    private ListView listView;
    private PDFAdapterStudent adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_student);

        dbHelper = new LibraryDatabaseHelper(this);
        listView = findViewById(R.id.pdfListViewStudent);

        loadPDFList();
    }

    private void loadPDFList() {
        Cursor cursor = dbHelper.getAllPDFs();
        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }

        adapter = new PDFAdapterStudent(this, cursor);
        listView.setAdapter(adapter);

        cursor.close(); // Now closing safely
    }
}
