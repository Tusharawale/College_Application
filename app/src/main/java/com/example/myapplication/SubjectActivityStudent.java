package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

public class SubjectActivityStudent extends AppCompatActivity {
    private LinearLayout unitContainer;
    private DatabaseHelper dbHelper;
    private String subjectName;
    private Button viewSyllabusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_student);

        subjectName = getIntent().getStringExtra("subject_name");
        dbHelper = new DatabaseHelper(this);

        TextView subjectTitle = findViewById(R.id.subjectTitle);
        subjectTitle.setText(subjectName);

        unitContainer = findViewById(R.id.unitContainer);
        viewSyllabusButton = findViewById(R.id.viewSyllabusButton);

        viewSyllabusButton.setOnClickListener(view -> viewSyllabusPdf());

        loadUnits();
    }

    private void loadUnits() {
        List<String> units = dbHelper.getUnits(subjectName);
        for (String unit : units) {
            addUnitView(unit);
        }
    }

    private void addUnitView(String unitName) {
        View unitView = LayoutInflater.from(this).inflate(R.layout.item_unit_student, unitContainer, false);
        TextView unitText = unitView.findViewById(R.id.unitTitle);
        Button enterButton = unitView.findViewById(R.id.enterButton);

        unitText.setText(unitName);

        // Navigate to UnitActivityStudent when Enter button is clicked
        enterButton.setOnClickListener(view -> openUnitPage(unitName));

        unitContainer.addView(unitView);
    }

    private void openUnitPage(String unitName) {
        Intent intent = new Intent(SubjectActivityStudent.this, UnitActivityStudent.class);
        intent.putExtra("unit_name", unitName);
        startActivity(intent);
    }

    // Improved PDF Viewer Integration
    private void viewSyllabusPdf() {
        String syllabusPath = dbHelper.getSyllabusPath(subjectName);

        if (syllabusPath != null) {
            File file = new File(syllabusPath);

            if (!file.exists()) {
                Toast.makeText(this, "Syllabus file not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, PdfRenderingActivity.class);
            intent.putExtra("syllabus_path", syllabusPath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No syllabus uploaded", Toast.LENGTH_SHORT).show();
        }
    }

}
