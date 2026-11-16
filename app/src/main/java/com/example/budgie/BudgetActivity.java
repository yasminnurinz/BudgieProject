package com.example.budgie;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {

    EditText editTextBudgetAmount;
    Button buttonUpdateBudget, chip300, chip500, chip800, chip1200;
    TextView tvSpentTotal, tvPercentage, tvOfBudget, tvLeftAmount, tvStatusBanner, tvDailyAllowance;
    ProgressBar progressBarBudget;
    DataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);

        editTextBudgetAmount = findViewById(R.id.editTextBudgetAmount);
        buttonUpdateBudget = findViewById(R.id.buttonUpdateBudget);
        chip300 = findViewById(R.id.chip300);
        chip500 = findViewById(R.id.chip500);
        chip800 = findViewById(R.id.chip800);
        chip1200 = findViewById(R.id.chip1200);

        tvSpentTotal = findViewById(R.id.tvSpentTotal);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvOfBudget = findViewById(R.id.tvOfBudget);
        tvLeftAmount = findViewById(R.id.tvLeftAmount);
        tvStatusBanner = findViewById(R.id.tvStatusBanner);
        tvDailyAllowance = findViewById(R.id.tvDailyAllowance);
        progressBarBudget = findViewById(R.id.progressBarBudget);

        LoadBudgetData();

        chip300.setOnClickListener(v -> editTextBudgetAmount.setText("300"));
        chip500.setOnClickListener(v -> editTextBudgetAmount.setText("500"));
        chip800.setOnClickListener(v -> editTextBudgetAmount.setText("800"));
        chip1200.setOnClickListener(v -> editTextBudgetAmount.setText("1200"));

        buttonUpdateBudget.setOnClickListener(v -> {
            String newBudgetStr = editTextBudgetAmount.getText().toString();
            if (newBudgetStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a budget amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double newBudget = Double.parseDouble(newBudgetStr);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE budget SET amount = " + newBudget + " WHERE id = 1");

            Toast.makeText(getApplicationContext(), "Budget Successfully Updated", Toast.LENGTH_SHORT).show();
            LoadBudgetData();
        });
    }

    public void LoadBudgetData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        double budget = 300.00;
        Cursor cBudget = db.rawQuery("SELECT amount FROM budget WHERE id = 1", null);
        if (cBudget.moveToFirst()) {
            budget = cBudget.getDouble(0);
        }
        cBudget.close();
        editTextBudgetAmount.setText(String.format(Locale.US, "%.0f", budget));

        double totalSpent = 0.0;
        Cursor cExpenses = db.rawQuery("SELECT SUM(amount) FROM expenses", null);
        if (cExpenses.moveToFirst()) {
            totalSpent = cExpenses.getDouble(0);
        }
        cExpenses.close();

        double left = budget - totalSpent;
        int percent = (budget > 0) ? (int) ((totalSpent / budget) * 100) : 0;

        tvSpentTotal.setText(String.format(Locale.US, "RM %.2f", totalSpent));
        tvPercentage.setText(percent + "%");
        progressBarBudget.setProgress(Math.min(percent, 100));
        tvOfBudget.setText(String.format(Locale.US, "of RM %.2f", budget));
        tvLeftAmount.setText(String.format(Locale.US, "RM %.2f left", left));

        if (percent > 100) {
            tvStatusBanner.setText("Over budget! Review your spending.");
            tvStatusBanner.setTextColor(0xFFD32F2F);
        } else {
            tvStatusBanner.setText("On track");
            tvStatusBanner.setTextColor(0xFF2E7D32);
        }

        double daily = (left > 0) ? (left / 3.0) : 0.0;
        tvDailyAllowance.setText(String.format(Locale.US, "You can spend about RM %.2f per day for the next 3 days.", daily));
    }

    public void goHome(View view) { startActivity(new Intent(getApplicationContext(), MainActivity.class)); }
    public void goExpenses(View view) { startActivity(new Intent(getApplicationContext(), ExpensesActivity.class)); }
    public void goShopping(View view) { startActivity(new Intent(getApplicationContext(), ShoppingActivity.class)); }
    public void goReports(View view) { startActivity(new Intent(getApplicationContext(), ReportsActivity.class)); }
}