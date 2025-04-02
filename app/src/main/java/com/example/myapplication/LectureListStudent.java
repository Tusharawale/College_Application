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

public class LectureListStudent extends AppCompatActivity {
    private String unitName;
    private LinearLayout lectureContainer;
    private LecturesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_list_student);

        dbHelper = new LecturesDatabaseHelper(this);
        unitName = getIntent().getStringExtra("unit_name");

        lectureContainer = findViewById(R.id.lectureContainer);
        loadLectures();
    }

    private void loadLectures() {
        lectureContainer.removeAllViews();
        List<String> lectures = dbHelper.getLecturesForUnit(unitName);

        for (String lectureName : lectures) {
            addLectureToUI(lectureName);
        }
    }

    private void addLectureToUI(String lectureName) {
        View lectureView = LayoutInflater.from(this).inflate(R.layout.item_lecture_student, lectureContainer, false);
        TextView lectureText = lectureView.findViewById(R.id.lectureTitle);
        lectureText.setText(lectureName);

        lectureView.setOnClickListener(view -> openLectureOptions(lectureName));

        lectureContainer.addView(lectureView);
    }

    private void openLectureOptions(String lectureName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(lectureName)
                .setItems(new String[]{"View Video", "View Notes"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    watchVideo(lectureName);
                                    break;
                                case 1:
                                    viewNotes(lectureName);
                                    break;
                            }
                        })
                .show();
    }

    private void watchVideo(String lectureName) {
        String videoPath = dbHelper.getVideoPath(lectureName);
        if (videoPath != null) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra("video_path", videoPath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No video available", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewNotes(String lectureName) {
        String notesPath = dbHelper.getNotesPath(lectureName);
        if (notesPath != null) {
            Intent intent = new Intent(this, PdfRenderingActivity.class);
            intent.putExtra("notes_path", notesPath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No notes available", Toast.LENGTH_SHORT).show();
        }
    }
}
