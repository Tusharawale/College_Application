package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfRenderingActivity extends AppCompatActivity {
    private ImageView pdfImageView;
    private Button nextPageButton, prevPageButton;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;
    private int currentPageIndex = 0;
    private int pageCount;

    private AssignmentDatabaseHelper dbHelper; // Database Helper for fetching PDFs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_rendering);

        pdfImageView = findViewById(R.id.pdfImageView);
        nextPageButton = findViewById(R.id.nextPageButton);
        prevPageButton = findViewById(R.id.prevPageButton);

        dbHelper = new AssignmentDatabaseHelper(this);

        // Get data from intent (PDF paths or PDF Title for database)
        String pdfPath = getIntent().getStringExtra("notes_path");
        if (pdfPath == null) pdfPath = getIntent().getStringExtra("pdf_path");
        if (pdfPath == null) pdfPath = getIntent().getStringExtra("syllabus_path");
        if (pdfPath == null) pdfPath = getIntent().getStringExtra("pdf_library");
        if (pdfPath == null) pdfPath = getIntent().getStringExtra("assignment_path");

        // Check for database PDF titles
        String pdfTitle = getIntent().getStringExtra("PDF_TITLE");

        if (pdfPath != null) {
            openPdfFile(pdfPath); // For file-based PDFs
        } else if (pdfTitle != null) {
            openPdfFromDatabase(pdfTitle); // For database-stored PDFs
        } else {
            Toast.makeText(this, "No PDF found", Toast.LENGTH_SHORT).show();
            finish();
        }

        nextPageButton.setOnClickListener(view -> showPage(currentPageIndex + 1));
        prevPageButton.setOnClickListener(view -> showPage(currentPageIndex - 1));
    }

    // Open PDF from File Path
    private void openPdfFile(String pdfPath) {
        try {
            Uri pdfUri = Uri.parse(pdfPath);
            File file = new File(pdfUri.getPath());

            if (!file.exists()) {
                Toast.makeText(this, "PDF file not found!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            pageCount = pdfRenderer.getPageCount();
            showPage(0);
        } catch (IOException e) {
            Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

                fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);
                pageCount = pdfRenderer.getPageCount();
                showPage(0);

            } catch (IOException e) {
                Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void showPage(int index) {
        if (index < 0 || index >= pageCount) return;

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        pdfImageView.setImageBitmap(bitmap);

        currentPageIndex = index;
        prevPageButton.setEnabled(index > 0);
        nextPageButton.setEnabled(index < pageCount - 1);
    }

    @Override
    protected void onDestroy() {
        try {
            if (currentPage != null) currentPage.close();
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
