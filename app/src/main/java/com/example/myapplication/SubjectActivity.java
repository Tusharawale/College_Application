package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SubjectActivity extends AppCompatActivity {
    private LinearLayout unitContainer;
    private Context context;
    private String subjectName;
    private DatabaseHelper dbHelper;
    private Button syllabusButton;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        context = this;
        subjectName = getIntent().getStringExtra("subject_name");
        dbHelper = new DatabaseHelper(this);

        TextView subjectTitle = findViewById(R.id.subjectTitle);
        subjectTitle.setText(subjectName);

        Button createUnitButton = findViewById(R.id.createUnitButton);
        syllabusButton = findViewById(R.id.syllabusButton);
        unitContainer = findViewById(R.id.unitContainer);

        createUnitButton.setOnClickListener(view -> addNewUnit());
        syllabusButton.setOnClickListener(view -> showSyllabusDialog());

        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri pdfUri = result.getData().getData();
                        savePdfToInternalStorage(pdfUri);
                    }
                });

        loadUnits();
    }

    // 🔹 Adds a new unit
    private void addNewUnit() {
        int unitNumber = dbHelper.getUnits(subjectName).size() + 1;
        String unitName = subjectName + " - Unit " + unitNumber;

        if (dbHelper.addUnit(subjectName, unitName)) {
            addUnitToUI(unitName);
        } else {
            Toast.makeText(context, "Unit already exists!", Toast.LENGTH_SHORT).show();
        }
    }

    // 🔹 Adds unit to the UI
    private void addUnitToUI(String unitName) {
        View unitView = LayoutInflater.from(context).inflate(R.layout.item_unit, unitContainer, false);

        TextView unitText = unitView.findViewById(R.id.unitTitle);
        Button startButton = unitView.findViewById(R.id.startButton);
        Button deleteButton = unitView.findViewById(R.id.deleteButton);

        unitText.setText(unitName);

        startButton.setOnClickListener(view -> openUnitPage(unitName));

        deleteButton.setOnClickListener(view -> {
            dbHelper.deleteUnit(subjectName, unitName);
            unitContainer.removeView(unitView);
            Toast.makeText(context, unitName + " deleted", Toast.LENGTH_SHORT).show();
        });

        unitContainer.addView(unitView);
    }

    // 🔹 Loads existing units
    private void loadUnits() {
        for (String unit : dbHelper.getUnits(subjectName)) {
            addUnitToUI(unit);
        }
    }

    // 🔹 Opens the selected unit
    private void openUnitPage(String unitName) {
        Intent intent = new Intent(SubjectActivity.this, UnitActivity.class);
        intent.putExtra("unit_name", unitName);
        startActivity(intent);
    }

    // 🔹 Shows syllabus dialog
    private void showSyllabusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Syllabus Options")
                .setItems(new String[]{"Upload PDF", "View PDF"}, (dialog, which) -> {
                    if (which == 0) {
                        selectPdfFromStorage();
                    } else {
                        viewSyllabusPdf();
                    }
                })
                .show();
    }

    // 🔹 Opens file picker for PDF selection
    private void selectPdfFromStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pdfPickerLauncher.launch(intent);
    }

    // 🔹 Saves the selected PDF file to internal storage
    private void savePdfToInternalStorage(Uri pdfUri) {
        try {
            File syllabusFile = new File(getFilesDir(), subjectName + "_syllabus.pdf");
            try (InputStream inputStream = getContentResolver().openInputStream(pdfUri);
                 FileOutputStream outputStream = new FileOutputStream(syllabusFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            dbHelper.saveSyllabusPath(subjectName, syllabusFile.getAbsolutePath());
            dbHelper.logSyllabusData(); // 🔹 Log data for debugging
            Toast.makeText(this, "Syllabus uploaded successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to upload syllabus", Toast.LENGTH_SHORT).show();
        }
    }

    // 🔹 Corrected logic for viewing syllabus PDFs
    private void viewSyllabusPdf() {
        dbHelper.logSyllabusData();  // 🔹 Log data for debugging

        String syllabusPath = dbHelper.getSyllabusPath(subjectName);
        if (syllabusPath != null) {
            File file = new File(syllabusPath);

            if (!file.exists()) {
                Toast.makeText(this, "Syllabus file not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, PdfRenderingActivity.class);
            intent.putExtra("syllabus_path", syllabusPath); // Pass the file path
            startActivity(intent);


            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No syllabus uploaded", Toast.LENGTH_SHORT).show();
        }
    }

}
