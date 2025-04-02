package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PDFAdapterStudent extends BaseAdapter {

    private Context context;
    private List<PDFItem> pdfList;

    public PDFAdapterStudent(Context context, Cursor cursor) {
        this.context = context;
        this.pdfList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String bookName = cursor.getString(1);
                String author = cursor.getString(2);
                pdfList.add(new PDFItem(id, bookName, author));
            }
        }
    }

    @Override
    public int getCount() {
        return pdfList.size();
    }

    @Override
    public Object getItem(int position) {
        return pdfList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pdfList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pdf_list_item_student, parent, false);
        }

        TextView bookNameText = convertView.findViewById(R.id.bookNameText);
        TextView authorNameText = convertView.findViewById(R.id.authorNameText);
        Button viewPdfButton = convertView.findViewById(R.id.viewPdfButton);

        PDFItem pdfItem = pdfList.get(position);
        bookNameText.setText(pdfItem.getBookName());
        authorNameText.setText("By " + pdfItem.getAuthor());

        viewPdfButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfRenderingLibraryActivity.class);
            intent.putExtra("PDF_ID", pdfItem.getId());
            context.startActivity(intent);
        });

        return convertView;
    }
}
