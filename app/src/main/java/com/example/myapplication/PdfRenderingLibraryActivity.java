package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfRenderingLibraryActivity extends AppCompatActivity {

    private ImageView pdfImageView;
    private Button nextPageButton, prevPageButton;
    private LibraryDatabaseHelper dbHelper;
    private int pdfId;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;
    private int pageIndex = 0; // Start at the first page

    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f; // Default scale

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_library_rendering);

        pdfImageView = findViewById(R.id.pdfImageView);
        nextPageButton = findViewById(R.id.nextPageButton);
        prevPageButton = findViewById(R.id.prevPageButton);
        dbHelper = new LibraryDatabaseHelper(this);

        pdfId = getIntent().getIntExtra("PDF_ID", -1);

        if (pdfId != -1) {
            displayPDF(pdfId);
        } else {
            Toast.makeText(this, "Error loading PDF", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Next Page Button Click Listener
        nextPageButton.setOnClickListener(v -> showNextPage());

        // Previous Page Button Click Listener
        prevPageButton.setOnClickListener(v -> showPrevPage());

        // Initialize Gesture Detector for Zoom
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Enable touch listener to detect pinch gestures
        pdfImageView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void displayPDF(int pdfId) {
        Cursor cursor = dbHelper.getPDF(pdfId);

        if (cursor.moveToFirst()) {
            byte[] pdfBytes = cursor.getBlob(3); // Assuming PDF is stored as BLOB in SQLite
            preparePDFRenderer(pdfBytes);
            showPage(pageIndex);
        } else {
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void preparePDFRenderer(byte[] pdfBytes) {
        try {
            // Create a temporary file for the PDF
            File tempFile = File.createTempFile("temp_pdf", ".pdf", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(pdfBytes);
            fos.close();

            // Open the file for rendering
            fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
        } catch (IOException e) {
            Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPage(int index) {
        if (pdfRenderer == null || index >= pdfRenderer.getPageCount() || index < 0) {
            return;
        }

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(index);

        // Create a properly sized bitmap with a white background to prevent shadow text
        Bitmap bitmap = Bitmap.createBitmap(
                (int) (currentPage.getWidth() * scaleFactor),
                (int) (currentPage.getHeight() * scaleFactor),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE); // Ensure white background

        // Render the page onto the bitmap
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        pdfImageView.setImageBitmap(bitmap);

        // Enable or disable buttons based on page count
        prevPageButton.setEnabled(index > 0);
        nextPageButton.setEnabled(index < pdfRenderer.getPageCount() - 1);
    }

    private void showNextPage() {
        if (pageIndex < pdfRenderer.getPageCount() - 1) {
            pageIndex++;
            showPage(pageIndex);
        }
    }

    private void showPrevPage() {
        if (pageIndex > 0) {
            pageIndex--;
            showPage(pageIndex);
        }
    }

    // Scale Gesture Listener for Pinch Zoom
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Limit zoom scale between 1.0x and 4.0x
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 4.0f));

            showPage(pageIndex);
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (currentPage != null) currentPage.close();
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
