package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PDFAdapter extends BaseAdapter {

    private Context context;
    private List<PDFItem> pdfList;
    private LibraryDatabaseHelper dbHelper;

    public PDFAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.dbHelper = new LibraryDatabaseHelper(context);
        this.pdfList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String bookName = cursor.getString(1);
            String author = cursor.getString(2);
            pdfList.add(new PDFItem(id, bookName, author));
        }
        cursor.close();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.pdf_list_item, parent, false);
        }

        TextView bookNameText = convertView.findViewById(R.id.bookNameText);
        TextView authorNameText = convertView.findViewById(R.id.authorNameText);
        Button viewPdfButton = convertView.findViewById(R.id.viewPdfButton);
        Button deletePdfButton = convertView.findViewById(R.id.deletePdfButton);

        PDFItem pdfItem = pdfList.get(position);
        bookNameText.setText(pdfItem.getBookName());
        authorNameText.setText("By " + pdfItem.getAuthor());

        // View PDF Button Click
        viewPdfButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfRenderingLibraryActivity.class);
            intent.putExtra("PDF_ID", pdfItem.getId());
            context.startActivity(intent);
        });

        // Delete PDF Button Click
        deletePdfButton.setOnClickListener(v -> showDeleteConfirmation(pdfItem.getId(), position));

        return convertView;
    }

    private void showDeleteConfirmation(int pdfId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deletePDF(pdfId);
                    pdfList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Book Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
