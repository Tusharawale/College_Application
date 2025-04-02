package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class NextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_next);

        Button myClassButton = findViewById(R.id.myClassButton);
        Button infoButton = findViewById(R.id.infoButton);
        Button libraryStudentButton = findViewById(R.id.libraryStudentButton);

        myClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextActivity.this, StudentActivity.class);
                startActivity(intent);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NextActivity.this, StudentInfoActivity.class);
                startActivity(intent);
            }
        });

        // ✅ Corrected the libraryStudentButton click event
        libraryStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLibraryStudentPage();
            }
        });
    }

    // ✅ Moved this method outside onCreate()
    private void openLibraryStudentPage() {
        Intent intent = new Intent(NextActivity.this, LibraryStudent.class);
        startActivity(intent);
    }
}
