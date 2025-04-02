package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.TestDatabase;
import com.example.myapplication.TestQuestion;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private List<TestQuestion> questionList;
    private TestDatabase db;
    private Context context;

    public QuestionAdapter(List<TestQuestion> questionList, TestDatabase db, Context context) {
        this.questionList = questionList;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        TestQuestion question = questionList.get(position);
        holder.tvQuestion.setText(question.getQuestion());

        holder.btnDelete.setOnClickListener(v -> {
            db.deleteQuestion(question.getId());
            questionList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Question Deleted!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;
        Button btnDelete;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
