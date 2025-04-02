package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityDetailStudent extends AppCompatActivity {
    private String unitName;
    private String activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);

        unitName = getIntent().getStringExtra("unit_name");
        activityType = getIntent().getStringExtra("activity_type");

        TextView titleText = findViewById(R.id.activityTitle);
        titleText.setText(unitName + " - " + activityType);

        Button enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(this::navigateToPage);
    }

    private void navigateToPage(View view) {
        Intent intent = null;

        if (activityType.equals("Lecture")) {
            intent = new Intent(this, LectureListStudent.class);
        } else if (activityType.equals("Quiz")) {
            intent = new Intent(this, QuizListStudent.class);
        } else if (activityType.equals("Assignment")) {
            intent = new Intent(this, AssignmentListStudent.class);
        } else if (activityType.equals("Test")) {
            intent = new Intent(this, TestListStudent.class);
        } else if (activityType.equals("PYQ")) {
            intent = new Intent(this, PyqListStudent.class);
        }

        if (intent != null) {
            intent.putExtra("unit_name", unitName);
            startActivity(intent);
        }
    }
}
