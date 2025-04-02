package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AssignmentActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;

    private EditText editTitle;
    private Button btnSelectPdf, btnUpload;
    private ListView listViewAssignments;

    private AssignmentDatabaseHelper dbHelper;
    private ArrayList<String> assignmentTitles;
    private ArrayAdapter<String> adapter;

    private Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        initViews();
        setupListAdapter();
        loadAssignmentList();

        btnSelectPdf.setOnClickListener(v -> openFileChooser());
        btnUpload.setOnClickListener(v -> uploadPdf());
        listViewAssignments.setOnItemClickListener((parent, view, position, id) ->
                showCustomDialog(assignmentTitles.get(position))
        );
    }

    // Initialize Views
    private void initViews() {
        editTitle = findViewById(R.id.editTitle);
        btnSelectPdf = findViewById(R.id.btnSelectPdf);
        btnUpload = findViewById(R.id.btnUpload);
        listViewAssignments = findViewById(R.id.listViewAssignments);

        dbHelper = new AssignmentDatabaseHelper(this);
        assignmentTitles = new ArrayList<>();
    }

    // Setup List Adapter
    // Setup List Adapter
    // Setup List Adapter
    private void setupListAdapter() {
        adapter = new AssignmentAdapter(this, assignmentTitles);  // Updated Adapter
        listViewAssignments.setAdapter(adapter);
    }



    // Load Assignments List
    private void loadAssignmentList() {
        assignmentTitles.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title FROM assignments", null);

        while (cursor.moveToNext()) {
            assignmentTitles.add(cursor.getString(0));
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }

    // Show Custom Dialog for Options
    private void showCustomDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_assignment_options, null);
        builder.setView(view);

        Button btnViewPdf = view.findViewById(R.id.btnViewPdf);
        Button btnDeletePdf = view.findViewById(R.id.btnDeletePdf);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnViewPdf.setOnClickListener(v -> {
            Intent intent = new Intent(this, PdfRenderingActivity.class);
            intent.putExtra("PDF_TITLE", title); // Pass PDF title to next activity
            startActivity(intent);
            dialog.dismiss();
        });


        btnDeletePdf.setOnClickListener(v -> {
            deleteAssignment(title);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // PDF Upload Process
    private void uploadPdf() {
        String title = editTitle.getText().toString().trim();

        if (title.isEmpty() || pdfUri == null) {
            Toast.makeText(this, "Please enter title and select a PDF", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToDatabase(title);
        }
    }

    // Open File Chooser for PDF Selection
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            Toast.makeText(this, "PDF Selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Upload PDF to Database
    private void uploadPdfToDatabase(String title) {
        try {
            byte[] pdfData = readPdfFromUri(pdfUri);

            if (pdfData != null) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO assignments (title, pdf_data) VALUES (?, ?)",
                        new Object[]{title, pdfData});

                Toast.makeText(this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();
                loadAssignmentList();
            } else {
                Toast.makeText(this, "Error reading PDF data", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Log.e("PDF_UPLOAD_ERROR", "Error reading PDF", e);
            Toast.makeText(this, "Error reading PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // Open PDF from Database
    private void openPdfFromDatabase(String title) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT pdf_data FROM assignments WHERE title=?", new String[]{title});

        if (cursor.moveToFirst()) {
            byte[] pdfData = cursor.getBlob(0);

            try {
                File tempFile = new File(getCacheDir(), title + ".pdf");
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(pdfData);
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(tempFile), "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);

            } catch (IOException e) {
                Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
                Log.e("PDF_OPEN_ERROR", "Error opening PDF", e);
            }
        }
        cursor.close();
    }

    // Delete Assignment
    private void deleteAssignment(String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete("assignments", "title = ?", new String[]{title});

        if (deletedRows > 0) {
            Toast.makeText(this, "Assignment deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete assignment", Toast.LENGTH_SHORT).show();
        }

        loadAssignmentList();
    }

    // Read PDF from URI
    private byte[] readPdfFromUri(Uri uri) throws IOException {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            if (inputStream != null) {
                byte[] data = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }

                return buffer.toByteArray();
            }
        }

        return null;
    }
}
