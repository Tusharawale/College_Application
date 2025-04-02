package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DocumentListAdapter extends ArrayAdapter<String> {
    public DocumentListAdapter(Context context, ArrayList<String> documents) {
        super(context, 0, documents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_document, parent, false);
        }

        TextView pdfTitle = convertView.findViewById(R.id.pdfTitle);
        ImageView pdfIcon = convertView.findViewById(R.id.pdfIcon);

        pdfTitle.setText(getItem(position));
        pdfIcon.setImageResource(R.mipmap.ic_pdf); // Ensure you have an icon in mipmap

        return convertView;
    }
}
