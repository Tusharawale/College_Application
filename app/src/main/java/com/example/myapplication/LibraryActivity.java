package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private LibraryDatabaseHelper dbHelper;
    private ListView listView;
    private List<String> pdfDetails;
    private List<Integer> pdfIds;
    private Uri selectedPdfUri;
    private String bookName, authorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        dbHelper = new LibraryDatabaseHelper(this);
        listView = findViewById(R.id.pdfListView);

        loadPDFList();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int pdfId = pdfIds.get(position);
            Intent intent = new Intent(LibraryActivity.this, PdfRenderingLibraryActivity.class);
            intent.putExtra("PDF_ID", pdfId);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            int pdfId = pdfIds.get(position);
            dbHelper.deletePDF(pdfId);
            loadPDFList();
            Toast.makeText(this, "PDF Deleted", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    public void uploadPDF(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        filePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedPdfUri = result.getData().getData();
                    showBookDetailsDialog();
                }
            });

    private void showBookDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Book Details");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_book_details, null);
        builder.setView(dialogView);

        EditText bookNameInput = dialogView.findViewById(R.id.editBookName);
        EditText authorInput = dialogView.findViewById(R.id.editAuthorName);

        builder.setPositiveButton("Save", (dialog, which) -> {
            bookName = bookNameInput.getText().toString();
            authorName = authorInput.getText().toString();
            savePDFToDatabase();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void savePDFToDatabase() {
        if (selectedPdfUri != null && bookName != null && authorName != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedPdfUri);
                byte[] pdfData = new byte[inputStream.available()];
                inputStream.read(pdfData);
                inputStream.close();

                dbHelper.insertPDF(bookName, authorName, pdfData);
                loadPDFList();
                Toast.makeText(this, "Book Added Successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error uploading PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadPDFList() {
        Cursor cursor = dbHelper.getAllPDFs();
        PDFAdapter adapter = new PDFAdapter(this, cursor);
        listView.setAdapter(adapter);
    }
}