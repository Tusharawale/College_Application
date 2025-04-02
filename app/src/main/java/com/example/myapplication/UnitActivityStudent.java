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

public class UnitActivityStudent extends AppCompatActivity {
    private LinearLayout activityContainer;
    private DatabaseHelper dbHelper;
    private String unitName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_student);

        unitName = getIntent().getStringExtra("unit_name");
        dbHelper = new DatabaseHelper(this);

        TextView unitTitle = findViewById(R.id.unitTitle);
        unitTitle.setText(unitName);

        activityContainer = findViewById(R.id.activityContainer);

        loadUnitActivities();
    }

    private void loadUnitActivities() {
        List<String> activities = dbHelper.getUnitActivities(unitName);
        for (String activity : activities) {
            addActivityView(activity);
        }
    }

    private void addActivityView(String activityName) {
        View activityView = LayoutInflater.from(this).inflate(R.layout.item_activity_student, activityContainer, false);
        TextView activityText = activityView.findViewById(R.id.activityTitle);
        Button startButton = activityView.findViewById(R.id.startActivityButton);

        activityText.setText(activityName);

        // Navigate to ActivityDetailStudent when the "Start" button is clicked
        startButton.setOnClickListener(view -> openActivityDetail(activityName));

        activityContainer.addView(activityView);
    }

    private void openActivityDetail(String activityType) {
        Intent intent = new Intent(this, ActivityDetailStudent.class);
        intent.putExtra("unit_name", unitName);
        intent.putExtra("activity_type", activityType);
        startActivity(intent);
    }

}
