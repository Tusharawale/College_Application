package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AssignmentAdapter extends ArrayAdapter<String> {

    public AssignmentAdapter(Context context, List<String> assignments) {
        super(context, 0, assignments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_assignment, parent, false);
        }

        TextView tvAssignmentTitle = convertView.findViewById(R.id.tvAssignmentTitle);
        tvAssignmentTitle.setText(getItem(position));

        return convertView;
    }
}
