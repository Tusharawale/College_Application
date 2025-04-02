package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PyqActivity extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST = 1;
    private String unitName;
    private ListView pyqListView;
    private ArrayAdapter<String> adapter;
    private List<String> pyqItems; // ✅ Changed from ArrayList<String> to List<String>
    private PyqDatabaseHelper dbHelper;
    private String currentPyqName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyq);

        unitName = getIntent().getStringExtra("unit_name");
        TextView title = findViewById(R.id.pyqTitle);
        title.setText(unitName + " - Previous Year Questions");

        dbHelper = new PyqDatabaseHelper(this);
        Button addPyqButton = findViewById(R.id.addPyqButton);
        pyqListView = findViewById(R.id.pyqListView);

        loadPyqs();

        addPyqButton.setOnClickListener(v -> showAddPyqDialog());

        pyqListView.setOnItemClickListener((parent, view, position, id) -> {
            String pyqName = pyqItems.get(position);
            showPyqOptions(pyqName);
        });
    }

    private void loadPyqs() {
        pyqItems = dbHelper.getPyqsForUnit(unitName);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pyqItems);
        pyqListView.setAdapter(adapter);
    }

    private void showAddPyqDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_pyq, null);
        EditText pyqNameInput = view.findViewById(R.id.pyqNameInput);

        builder.setView(view)
                .setTitle("Add New PYQ")
                .setPositiveButton("Add", (dialog, which) -> {
                    String pyqName = pyqNameInput.getText().toString().trim();
                    if (!pyqName.isEmpty()) {
                        currentPyqName = pyqName;
                        selectPdfFile();
                    } else {
                        Toast.makeText(this, "Enter a valid PYQ name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void selectPdfFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PYQ PDF"), PICK_PDF_REQUEST);
    }

    private void showPyqOptions(String pyqName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(pyqName)
                .setItems(new String[]{"View PDF", "Delete"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    viewPdf(pyqName);
                                    break;
                                case 1:
                                    deletePyq(pyqName);
                                    break;
                            }
                        })
                .show();
    }

    private void viewPdf(String pyqName) {
        String pdfPath = dbHelper.getPdfPath(pyqName);
        if (pdfPath != null) {
            Intent intent = new Intent(this, PdfRenderingActivity.class);
            intent.putExtra("pdf_path", pdfPath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show();
        }
    }


    private void deletePyq(String pyqName) {
        new AlertDialog.Builder(this)
                .setTitle("Delete PYQ")
                .setMessage("Are you sure you want to delete this PYQ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deletePyq(pyqName);
                    loadPyqs();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedPdf = data.getData();
            if (selectedPdf != null && currentPyqName != null) {  // ✅ Fixed potential null issue
                dbHelper.addPyq(unitName, currentPyqName, selectedPdf);
                loadPyqs();
                Toast.makeText(this, "Added: " + currentPyqName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding PYQ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
