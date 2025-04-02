package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class StudentActivity extends AppCompatActivity {
    private LinearLayout subjectContainer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        subjectContainer = findViewById(R.id.subjectContainer);
        dbHelper = new DatabaseHelper(this);

        loadSubjects(); // Load subjects from the database
    }

    private void loadSubjects() {
        List<String> subjects = dbHelper.getSubjects();
        for (String subject : subjects) {
            addSubjectView(subject);
        }
    }

    private void addSubjectView(String subjectName) {
        View subjectView = LayoutInflater.from(this).inflate(R.layout.item_subject_student, subjectContainer, false);

        TextView subjectText = subjectView.findViewById(R.id.subjectTitle);
        Button startButton = subjectView.findViewById(R.id.startButton);

        subjectText.setText(subjectName);

        startButton.setOnClickListener(view -> openSubjectPage(subjectName));

        subjectContainer.addView(subjectView);
    }

    private void openSubjectPage(String subjectName) {
        Intent intent = new Intent(StudentActivity.this, SubjectActivityStudent.class);
        intent.putExtra("subject_name", subjectName);
        startActivity(intent);
    }
}
