package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class QuizListStudent extends AppCompatActivity {

    private TextView tvQuestion;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnNext;
    private List<QuizModel> quizList;
    private int currentIndex = 0, score = 0;
    private QuizDatabaseHelper db;
    private String selectedAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list_student);

        tvQuestion = findViewById(R.id.tvQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
        btnNext = findViewById(R.id.btnNext);
        db = new QuizDatabaseHelper(this);
        quizList = db.getAllQuizzes();

        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbA) selectedAnswer = rbA.getText().toString();
            else if (checkedId == R.id.rbB) selectedAnswer = rbB.getText().toString();
            else if (checkedId == R.id.rbC) selectedAnswer = rbC.getText().toString();
            else if (checkedId == R.id.rbD) selectedAnswer = rbD.getText().toString();
        });

        btnNext.setOnClickListener(v -> checkAnswerAndNext());

        if (!quizList.isEmpty()) {
            loadQuestion();
        } else {
            Toast.makeText(this, "No quizzes available!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadQuestion() {
        if (currentIndex < quizList.size()) {
            QuizModel quiz = quizList.get(currentIndex);
            tvQuestion.setText(quiz.getQuestion());
            rbA.setText(quiz.getOptionA());
            rbB.setText(quiz.getOptionB());
            rbC.setText(quiz.getOptionC());
            rbD.setText(quiz.getOptionD());
            rgOptions.clearCheck();
            selectedAnswer = "";
        } else {
            showResult();
        }
    }

    private void checkAnswerAndNext() {
        if (!selectedAnswer.isEmpty()) {
            QuizModel quiz = quizList.get(currentIndex);
            if (selectedAnswer.equals(quiz.getCorrectAnswer())) {
                score++;
            }
            currentIndex++;
            loadQuestion();
        } else {
            Toast.makeText(this, "Select an answer!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult() {
        Toast.makeText(this, "Quiz Completed! Your Score: " + score + "/" + quizList.size(), Toast.LENGTH_LONG).show();
        finish();
    }
}
