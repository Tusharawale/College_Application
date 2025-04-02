package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PyqListStudent extends AppCompatActivity {
    private String unitName;
    private LinearLayout pyqContainer;
    private PyqDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyq_list_student);

        dbHelper = new PyqDatabaseHelper(this);
        unitName = getIntent().getStringExtra("unit_name");

        pyqContainer = findViewById(R.id.pyqContainer);
        loadPyqs();
    }

    private void loadPyqs() {
        pyqContainer.removeAllViews();
        List<String> pyqs = dbHelper.getPyqsForUnit(unitName);

        for (String pyqName : pyqs) {
            addPyqToUI(pyqName);
        }
    }

    private void addPyqToUI(String pyqName) {
        View pyqView = LayoutInflater.from(this).inflate(R.layout.item_pyq_student, pyqContainer, false);
        TextView pyqText = pyqView.findViewById(R.id.pyqTitle);
        pyqText.setText(pyqName);

        pyqView.setOnClickListener(view -> openPyqOptions(pyqName));

        pyqContainer.addView(pyqView);
    }

    private void openPyqOptions(String pyqName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(pyqName)
                .setItems(new String[]{"View PYQ", "Download PYQ"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    viewPyq(pyqName);
                                    break;
                                case 1:
                                    downloadPyqPdf(pyqName);
                                    break;
                            }
                        })
                .show();
    }

    private void viewPyq(String pyqName) {
        String pdfPath = dbHelper.getPdfPath(pyqName);
        if (pdfPath != null) {
            Intent intent = new Intent(this, PdfRenderingActivity.class);
            intent.putExtra("pdf_path", pdfPath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No PYQ available", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadPyqPdf(String pyqName) {
        String pdfPath = dbHelper.getPdfPath(pyqName);
        if (pdfPath != null) {
            Toast.makeText(this, "PYQ downloaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No PYQ available for download", Toast.LENGTH_SHORT).show();
        }
    }
}
