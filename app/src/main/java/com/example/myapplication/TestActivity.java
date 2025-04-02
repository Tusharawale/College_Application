package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.TestDatabase;
import com.example.myapplication.TestQuestion;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etCorrect;
    private Button btnSave;
    private RecyclerView rvQuestions;
    private TestDatabase db;
    private QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        etQuestion = findViewById(R.id.etQuestion);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);
        etCorrect = findViewById(R.id.etCorrect);
        btnSave = findViewById(R.id.btnSave);
        rvQuestions = findViewById(R.id.rvQuestions);

        db = new TestDatabase(this);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));

        btnSave.setOnClickListener(v -> saveQuestion());

        // Load questions into RecyclerView
        loadQuestions();
    }

    private void saveQuestion() {
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

        if (!correct.equalsIgnoreCase(optionA) && !correct.equalsIgnoreCase(optionB) && !correct.equalsIgnoreCase(optionC) && !correct.equalsIgnoreCase(optionD)) {
            Toast.makeText(this, "Correct answer must match one of the options!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.addQuestion(new TestQuestion(0, question, optionA, optionB, optionC, optionD, correct));
        Toast.makeText(this, "Question Added!", Toast.LENGTH_SHORT).show();
        clearFields();
        loadQuestions();  // Refresh list after adding a question
    }

    private void loadQuestions() {
        List<TestQuestion> questions = db.getAllQuestions();
        adapter = new QuestionAdapter(questions, db, this);
        rvQuestions.setAdapter(adapter);
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
