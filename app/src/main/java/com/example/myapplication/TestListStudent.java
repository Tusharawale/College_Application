package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.TestDatabase;
import com.example.myapplication.TestQuestion;
import java.util.List;

public class TestListStudent extends AppCompatActivity {

    private TextView tvQuestion;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnNext;
    private List<TestQuestion> questionList;
    private int currentIndex = 0, score = 0;
    private TestDatabase db;
    private String selectedAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list_student);

        tvQuestion = findViewById(R.id.tvQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
        btnNext = findViewById(R.id.btnNext);
        db = new TestDatabase(this);
        questionList = db.getAllQuestions();

        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbA) selectedAnswer = rbA.getText().toString();
            else if (checkedId == R.id.rbB) selectedAnswer = rbB.getText().toString();
            else if (checkedId == R.id.rbC) selectedAnswer = rbC.getText().toString();
            else if (checkedId == R.id.rbD) selectedAnswer = rbD.getText().toString();
        });

        btnNext.setOnClickListener(v -> checkAnswerAndNext());

        if (!questionList.isEmpty()) {
            loadQuestion();
        } else {
            Toast.makeText(this, "No questions available!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadQuestion() {
        if (currentIndex < questionList.size()) {
            TestQuestion question = questionList.get(currentIndex);
            tvQuestion.setText(question.getQuestion());
            rbA.setText(question.getOptionA());
            rbB.setText(question.getOptionB());
            rbC.setText(question.getOptionC());
            rbD.setText(question.getOptionD());
            rgOptions.clearCheck();
            selectedAnswer = "";
        } else {
            showResult();
        }
    }

    private void checkAnswerAndNext() {
        if (!selectedAnswer.isEmpty()) {
            TestQuestion question = questionList.get(currentIndex);
            if (selectedAnswer.equals(question.getCorrectAnswer())) {
                score++;
            }
            currentIndex++;
            loadQuestion();
        } else {
            Toast.makeText(this, "Select an answer!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult() {
        Toast.makeText(this, "Test Completed! Your Score: " + score + "/" + questionList.size(), Toast.LENGTH_LONG).show();
        finish();
    }
}
