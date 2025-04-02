package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etCorrect;
    private Button btnSaveQuiz;
    private QuizDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        etQuestion = findViewById(R.id.etQuestion);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);
        etCorrect = findViewById(R.id.etCorrect);
        btnSaveQuiz = findViewById(R.id.btnSaveQuiz);
        db = new QuizDatabaseHelper(this);

        btnSaveQuiz.setOnClickListener(v -> saveQuiz());
    }

    private void saveQuiz() {
        String question = etQuestion.getText().toString().trim();
        String optionA = etOptionA.getText().toString().trim();
        String optionB = etOptionB.getText().toString().trim();
        String optionC = etOptionC.getText().toString().trim();
        String optionD = etOptionD.getText().toString().trim();
        String correct = etCorrect.getText().toString().trim();

        if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || correct.isEmpty()) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.addQuiz(new QuizModel(0, question, optionA, optionB, optionC, optionD, correct));
        Toast.makeText(this, "Quiz Added!", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        etQuestion.setText("");
        etOptionA.setText("");
        etOptionB.setText("");
        etOptionC.setText("");
        etOptionD.setText("");
        etCorrect.setText("");
        etQuestion.requestFocus();
    }
}
