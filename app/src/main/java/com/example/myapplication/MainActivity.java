package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout subjectContainer;
    private Context context;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        subjectContainer = findViewById(R.id.subjectContainer);
        Button createSubjectButton = findViewById(R.id.createSubjectButton);

        dbHelper = new DatabaseHelper(this);

        createSubjectButton.setOnClickListener(view -> showSubjectNameDialog());


        loadSubjects(); // Load subjects from the database

        Button libraryButton = findViewById(R.id.libraryButton);
        libraryButton.setOnClickListener(view -> openLibraryPage());


    }

    private void showSubjectNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_subject_name, null);
        builder.setView(dialogView);

        EditText subjectInput = dialogView.findViewById(R.id.subjectNameInput);
        Button createButton = dialogView.findViewById(R.id.createButton);

        AlertDialog dialog = builder.create();
        createButton.setOnClickListener(v -> {
            String subjectName = subjectInput.getText().toString().trim();
            if (!subjectName.isEmpty()) {
                if (dbHelper.addSubject(subjectName)) {
                    addNewSubject(subjectName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Subject already exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Enter a subject name!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void addNewSubject(String subjectName) {
        View subjectView = LayoutInflater.from(context).inflate(R.layout.item_subject, subjectContainer, false);

        TextView subjectText = subjectView.findViewById(R.id.subjectTitle);
        Button startButton = subjectView.findViewById(R.id.startButton);
        Button deleteButton = subjectView.findViewById(R.id.deleteButton);

        subjectText.setText(subjectName);

        startButton.setOnClickListener(view -> openSubjectPage(subjectName));

        deleteButton.setOnClickListener(view -> {
            dbHelper.deleteSubject(subjectName);
            subjectContainer.removeView(subjectView);
        });

        subjectContainer.addView(subjectView);
    }

    private void loadSubjects() {
        List<String> subjects = dbHelper.getSubjects();
        for (String subject : subjects) {
            addNewSubject(subject);
        }
    }

    private void openSubjectPage(String subjectName) {
        Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
        intent.putExtra("subject_name", subjectName);
        startActivity(intent);
    }

    private void openStudentPage() {
        Intent intent = new Intent(MainActivity.this, StudentActivity.class);
        startActivity(intent);
    }

    private void openLibraryPage() {
        Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
        startActivity(intent);
    }

    // ✅ Added method to open LibraryStudent
    private void openLibraryStudentPage() {
        Intent intent = new Intent(MainActivity.this, LibraryStudent.class);
        startActivity(intent);
    }
}
