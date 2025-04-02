package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class UnitActivity extends AppCompatActivity {
    private String unitName;
    private LinearLayout unitContainer;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        unitName = getIntent().getStringExtra("unit_name");
        TextView unitTitle = findViewById(R.id.unitTitle);
        unitTitle.setText(unitName);

        Button createButton = findViewById(R.id.createButton);
        unitContainer = findViewById(R.id.unitContainer);
        databaseHelper = new DatabaseHelper(this);

        loadUnitItemsFromDatabase();

        createButton.setOnClickListener(this::showCreatePopup);
    }

    private void showCreatePopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("Lecture");
        popupMenu.getMenu().add("Quiz");
        popupMenu.getMenu().add("Assignment");
        popupMenu.getMenu().add("Test");
        popupMenu.getMenu().add("PYQ");

        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedOption = item.getTitle().toString();
            databaseHelper.insertUnitActivity(unitName, selectedOption);
            addUnitToUI(selectedOption);
            return true;
        });

        popupMenu.show();
    }

    private void addUnitToUI(String unitActivity) {
        View unitView = LayoutInflater.from(this).inflate(R.layout.item_unit, unitContainer, false);

        TextView unitText = unitView.findViewById(R.id.unitTitle);
        Button openButton = unitView.findViewById(R.id.startButton);
        Button deleteButton = unitView.findViewById(R.id.deleteButton);

        unitText.setText(unitName + " - " + unitActivity);

        openButton.setOnClickListener(view -> openSelectedActivity(unitActivity));
        deleteButton.setOnClickListener(view -> {
            databaseHelper.deleteUnitActivity(unitName, unitActivity);
            unitContainer.removeView(unitView);
        });

        unitContainer.addView(unitView);
    }

    private void loadUnitItemsFromDatabase() {
        List<String> activities = databaseHelper.getUnitActivities(unitName); // Now returns List<String>
        for (String activityType : activities) {
            addUnitToUI(activityType);
        }
    }


    private void openSelectedActivity(String item) {
        Intent intent = null;

        if (item.contains("Lecture")) {
            intent = new Intent(this, LectureActivity.class);
        } else if (item.contains("Quiz")) {
            intent = new Intent(this, QuizActivity.class);
        } else if (item.contains("Assignment")) {
            intent = new Intent(this, AssignmentActivity.class);
        } else if (item.contains("Test")) {
            intent = new Intent(this, TestActivity.class);
        } else if (item.contains("PYQ")) {
            intent = new Intent(this, PyqActivity.class);
        }

        if (intent != null) {
            intent.putExtra("unit_name", unitName);
            startActivity(intent);
        }
    }
}
