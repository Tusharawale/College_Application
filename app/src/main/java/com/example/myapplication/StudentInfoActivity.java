package com.example.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StudentInfoActivity extends AppCompatActivity {
    private static final String TAG = "StudentInfoActivity";

    private Button  btnSaveNotes, btnDeleteNotes, btnUpload;
    private EditText etDocumentName , etNotes;
    private Spinner spinnerDocumentType;
    private TextView fileName;
    private ListView documentList;

    private ArrayList<String> documents, documentUris;
    private ArrayAdapter<String> adapter;
    private Uri selectedFileUri = null;
    private StudentDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        initializeViews();
        dbHelper = new StudentDatabaseHelper(this);

        documents = new ArrayList<>();
        documentUris = new ArrayList<>();
        adapter = new DocumentListAdapter(this, documents);  // Use custom adapter
        documentList.setAdapter(adapter);

        setupSpinner();
        btnUpload.setOnClickListener(v -> selectFile());
        loadDocuments();
        loadNotes();

        documentList.setOnItemClickListener((parent, view, position, id) -> {
            String uriString = documentUris.get(position);
            showPdfDialog(this, uriString);
        });

        btnSaveNotes.setOnClickListener(v -> saveNotes());
        btnDeleteNotes.setOnClickListener(v -> deleteNotes());
    }


    private void initializeViews() {
        btnUpload = findViewById(R.id.btn_upload);
        etDocumentName = findViewById(R.id.et_document_name);
        spinnerDocumentType = findViewById(R.id.spinner_document_type);
        fileName = findViewById(R.id.file_name);
        documentList = findViewById(R.id.document_list);
        etNotes = findViewById(R.id.et_notes);
        btnSaveNotes = findViewById(R.id.btn_save_notes);
        btnDeleteNotes = findViewById(R.id.btn_delete_notes);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.document_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocumentType.setAdapter(spinnerAdapter);
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                String name = getFileName(selectedFileUri);
                fileName.setText(name);

                try {
                    getContentResolver().takePersistableUriPermission(selectedFileUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Log.d(TAG, "Took persistable URI permission for: " + selectedFileUri);
                } catch (SecurityException e) {
                    Log.e(TAG, "Failed to take persistable URI permission", e);
                }

                String docName = etDocumentName.getText().toString().trim();
                if (docName.isEmpty()) {
                    docName = name;
                }

                String docType = spinnerDocumentType.getSelectedItem().toString();
                String uriString = selectedFileUri.toString();

                saveDocumentToDB(docName, docType, name, uriString);
                loadDocuments();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name", e);
            }
        }
        return result != null ? result : uri.getLastPathSegment();
    }

    private void saveDocumentToDB(String docName, String docType, String fileName, String fileUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("doc_name", docName);
        values.put("doc_type", docType);
        values.put("file_name", fileName);
        values.put("file_uri", fileUri);

        long id = db.insert("documents", null, values);
        db.close();

        if (id != -1) {
            Log.d(TAG, "Document saved to DB with ID: " + id);
            Toast.makeText(this, "Document saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to save document to DB");
            Toast.makeText(this, "Failed to save document", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDocuments() {
        documents.clear();
        documentUris.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM documents", null);

        while (cursor.moveToNext()) {
            String docName = cursor.getString(1);
            String docType = cursor.getString(2);
            String fileName = cursor.getString(3);
            String fileUri = cursor.getString(4);
            documents.add(docName + " (" + docType + ")");
            documentUris.add(fileUri);
            Log.d(TAG, "Loaded document: " + docName + ", URI: " + fileUri);
        }

        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    private void showPdfDialog(Context context, String pdfUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pdf_view, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        view.findViewById(R.id.btn_view_pdf).setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfRenderingStudentInfo.class);
            intent.putExtra("pdfUri", pdfUri);
            Log.d(TAG, "Launching PDF viewer with URI: " + pdfUri);   // check this code somthitime problem over her
            context.startActivity(intent);
            dialog.dismiss();
        });

        view.findViewById(R.id.btn_delete_pdf).setOnClickListener(v -> {
            deleteDocument(pdfUri);
            dialog.dismiss();
        });

        view.findViewById(R.id.btn_cancel_pdf).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void deleteDocument(String fileUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete("documents", "file_uri=?", new String[]{fileUri});
        db.close();

        if (rowsDeleted > 0) {
            Log.d(TAG, "Document deleted: " + fileUri);
            Toast.makeText(this, "Document deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to delete document");
            Toast.makeText(this, "Failed to delete document", Toast.LENGTH_SHORT).show();
        }

        loadDocuments(); // Refresh the list after deletion
    }
    private void loadNotes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT content FROM notes LIMIT 1", null);

        if (cursor.moveToFirst()) {
            etNotes.setText(cursor.getString(0));
        }
        cursor.close();
        db.close();
    }
    private void saveNotes() {
        String notes = etNotes.getText().toString().trim();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM notes"); // Remove old notes
        ContentValues values = new ContentValues();
        values.put("content", notes);
        db.insert("notes", null, values);
        db.close();

        Toast.makeText(this, "Notes saved!", Toast.LENGTH_SHORT).show();
    }
    private void deleteNotes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM notes");
        db.close();

        etNotes.setText(""); // Clear UI
        Toast.makeText(this, "Notes deleted!", Toast.LENGTH_SHORT).show();
    }
}
