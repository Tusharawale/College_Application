package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AssignmentListStudent extends Activity {

    private ListView listView;
    private AssignmentDatabaseHelper dbHelper;
    private AssignmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list_student);

        listView = findViewById(R.id.listView);
        dbHelper = new AssignmentDatabaseHelper(this);

        loadPdfList();

        // Click event for list items
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPdfTitle = adapter.getItem(position);
            showPdfDialog(selectedPdfTitle);
        });
    }

    // Load PDF titles from database
    private void loadPdfList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title FROM assignments", null);

        ArrayList<String> pdfTitles = new ArrayList<>();
        while (cursor.moveToNext()) {
            pdfTitles.add(cursor.getString(0));
        }
        cursor.close();

        adapter = new AssignmentAdapter(this, pdfTitles);
        listView.setAdapter(adapter);
    }

    // Show dialog box with "View" and "Cancel" options
    private void showPdfDialog(String pdfTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PDF Options")
                .setMessage("What would you like to do?")
                .setPositiveButton("View PDF", (dialog, which) -> openPdf(pdfTitle))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Open PDF in PdfRenderingActivity
    private void openPdf(String pdfTitle) {
        Intent intent = new Intent(this, PdfRenderingActivity.class);
        intent.putExtra("PDF_TITLE", pdfTitle);
        startActivity(intent);
    }
}
