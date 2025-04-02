package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfRenderingStudentInfo extends AppCompatActivity {
    private static final String TAG = "PdfRenderingStudentInfo";
    private ImageView pdfImageView;
    private Button btnNext, btnPrev;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private int pageIndex = 0;
    private File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_rendering_student_info);

        pdfImageView = findViewById(R.id.pdfImageView);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        String pdfUriString = getIntent().getStringExtra("pdfUri");

        if (pdfUriString != null && !pdfUriString.isEmpty()) {
            try {
                Uri pdfUri = Uri.parse(pdfUriString);
                openPdfRenderer(pdfUri);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing URI", e);
                Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
            }
        }

        btnNext.setOnClickListener(v -> showPage(pageIndex + 1));
        btnPrev.setOnClickListener(v -> showPage(pageIndex - 1));
    }

    private void openPdfRenderer(Uri pdfUri) {
        try {
            pdfFile = createTempFileFromUri(this, pdfUri);

            if (pdfFile == null || !pdfFile.exists()) {
                Log.e(TAG, "Error: PDF file not found!");
                Toast.makeText(this, "Error: PDF file not found!", Toast.LENGTH_SHORT).show();
                return;
            }

            parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            showPage(0);
        } catch (Exception e) {
            Log.e(TAG, "Error loading PDF", e);
            Toast.makeText(this, "Error loading PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private void showPage(int index) {
        if (pdfRenderer == null || index < 0 || index >= pdfRenderer.getPageCount()) return;

        if (currentPage != null) currentPage.close();

        currentPage = pdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        pdfImageView.setImageBitmap(bitmap);
        pageIndex = index;

        btnPrev.setEnabled(pageIndex > 0);
        btnNext.setEnabled(pageIndex < pdfRenderer.getPageCount() - 1);
    }

    private File createTempFileFromUri(Context context, Uri uri) {
        try {
            File tempFile = new File(context.getCacheDir(), "temp.pdf");

            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }

            return tempFile.exists() ? tempFile : null;
        } catch (IOException e) {
            Log.e(TAG, "Failed to create temp file", e);
            return null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (currentPage != null) currentPage.close();
            if (pdfRenderer != null) pdfRenderer.close();
            if (parcelFileDescriptor != null) parcelFileDescriptor.close();
            if (pdfFile != null && pdfFile.exists()) pdfFile.delete();
        } catch (Exception ignored) {}
    }
}
