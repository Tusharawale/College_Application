package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Get references to UI elements
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        // Set default credentials
        final String defaultUsername = "admin123";
        final String defaultPassword = "admin";
        final String secondUsername = "admin904";
        final String secondPassword = "admin";

        // Set login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                if (enteredUsername.equals(defaultUsername) && enteredPassword.equals(defaultPassword)) {
                    Toast.makeText(login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Log.d("LoginStatus", "Login successful, navigating to NextActivity");
                    Intent intent = new Intent(login.this, NextActivity.class);
                    startActivity(intent);
                } else if (enteredUsername.equals(secondUsername) && enteredPassword.equals(secondPassword)) {
                    Toast.makeText(login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Log.d("LoginStatus", "Login successful, navigating to ThirdActivity");
                    Intent intent = new Intent(login.this, ThirdActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(login.this, "Invalid Login ID or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}