package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private Context context;
    private List<QuizModel> quizList;

    public QuizAdapter(Context context, List<QuizModel> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizModel quiz = quizList.get(position);
        holder.tvQuestion.setText(quiz.getQuestion());
        holder.tvOptionA.setText("A: " + quiz.getOptionA());
        holder.tvOptionB.setText("B: " + quiz.getOptionB());
        holder.tvOptionC.setText("C: " + quiz.getOptionC());
        holder.tvOptionD.setText("D: " + quiz.getOptionD());
        holder.tvCorrect.setText("Correct: " + quiz.getCorrectAnswer());
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvOptionA, tvOptionB, tvOptionC, tvOptionD, tvCorrect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvOptionA = itemView.findViewById(R.id.tvOptionA);
            tvOptionB = itemView.findViewById(R.id.tvOptionB);
            tvOptionC = itemView.findViewById(R.id.tvOptionC);
            tvOptionD = itemView.findViewById(R.id.tvOptionD);
            tvCorrect = itemView.findViewById(R.id.tvCorrect);
        }
    }
}
