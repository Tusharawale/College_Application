package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LectureActivity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int PICK_NOTES_REQUEST = 2;
    private String unitName;
    private LinearLayout lectureContainer;
    private LecturesDatabaseHelper dbHelper;
    private String currentLectureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        dbHelper = new LecturesDatabaseHelper(this);
        unitName = getIntent().getStringExtra("unit_name");

        TextView lectureTitle = findViewById(R.id.lectureTitle);
        lectureTitle.setText(unitName + " - Lectures");

        Button addLectureButton = findViewById(R.id.addLectureButton);
        lectureContainer = findViewById(R.id.lectureContainer);

        loadLectures(); // Load existing lectures

        addLectureButton.setOnClickListener(this::addLecture);
    }

    private void loadLectures() {
        lectureContainer.removeAllViews();
        List<String> lectures = dbHelper.getLecturesForUnit(unitName);

        for (String lectureName : lectures) {
            addLectureToUI(lectureName);
        }
    }

    private void addLecture(View view) {
        int lectureNumber = dbHelper.getLecturesForUnit(unitName).size() + 1;
        String lectureName = unitName + " - Lecture " + lectureNumber;

        long result = dbHelper.addLecture(unitName, lectureName);
        if (result != -1) {
            addLectureToUI(lectureName);
            Toast.makeText(this, "Lecture added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add lecture", Toast.LENGTH_SHORT).show();
        }
    }

    private void addLectureToUI(String lectureName) {
        View lectureView = LayoutInflater.from(this).inflate(R.layout.item_lecture, lectureContainer, false);
        TextView lectureText = lectureView.findViewById(R.id.lectureTitle);
        lectureText.setText(lectureName);

        lectureView.setOnClickListener(view -> showLectureOptions(lectureName));

        lectureContainer.addView(lectureView);
    }

    private void showLectureOptions(String lectureName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(lectureName)
                .setItems(new String[]{"Upload Video", "Watch Video", "Upload Notes", "View Notes", "Delete Lecture"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    uploadVideo(lectureName);
                                    break;
                                case 1:
                                    watchVideo(lectureName);
                                    break;
                                case 2:
                                    uploadNotes(lectureName);
                                    break;
                                case 3:
                                    viewNotes(lectureName);
                                    break;
                                case 4:
                                    deleteLecture(lectureName);
                                    break;
                            }
                        })
                .show();
    }

    private void uploadVideo(String lectureName) {
        currentLectureName = lectureName;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void watchVideo(String lectureName) {
        String videoPath = dbHelper.getVideoPath(lectureName);
        if (videoPath != null && !videoPath.isEmpty()) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra("video_path", videoPath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No video uploaded for this lecture", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadNotes(String lectureName) {
        currentLectureName = lectureName;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Notes PDF"), PICK_NOTES_REQUEST);
    }

    private void viewNotes(String lectureName) {
        String notesPath = dbHelper.getNotesPath(lectureName);
        if (notesPath != null && !notesPath.isEmpty()) {
            Intent intent = new Intent(this, PdfRenderingActivity.class);
            intent.putExtra("notes_path", notesPath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No notes uploaded for this lecture", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLecture(String lectureName) {
        dbHelper.deleteLecture(lectureName);
        loadLectures(); // Refresh list
        Toast.makeText(this, "Lecture deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_VIDEO_REQUEST) {
                Uri selectedVideo = data.getData();
                if (selectedVideo != null) {
                    dbHelper.updateVideoPath(currentLectureName, selectedVideo);
                    Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_NOTES_REQUEST) {
                Uri selectedNotes = data.getData();
                if (selectedNotes != null) {
                    dbHelper.updateNotesPath(currentLectureName, selectedNotes);
                    Toast.makeText(this, "Notes uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
