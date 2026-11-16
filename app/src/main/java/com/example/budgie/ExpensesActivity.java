package com.example.budgie;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ExpensesActivity extends AppCompatActivity {

    EditText editTextAmount, editTextTitle, editTextDate;
    Spinner spinnerCategory;
    Button buttonAddExpense, filterAll, filterFood, filterTransport, filterEducation, filterBills;
    ListView listViewExpenses;

    DataHelper dbHelper;
    String[] categories = {"Select Category", "Education", "Transportation", "Bills", "Food", "Entertainment"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expenses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);

        editTextAmount = findViewById(R.id.editTextAmount);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDate = findViewById(R.id.editTextDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonAddExpense = findViewById(R.id.buttonAddExpense);
        listViewExpenses = findViewById(R.id.listViewExpenses);

        filterAll = findViewById(R.id.filterAll);
        filterFood = findViewById(R.id.filterFood);
        filterTransport = findViewById(R.id.filterTransport);
        filterEducation = findViewById(R.id.filterEducation);
        filterBills = findViewById(R.id.filterBills);

        // Spinner Setup
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(spinnerAdapter);

        // Date Picker Dialog Setup
        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ExpensesActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String[] monthsShort = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                        String formattedDate = monthsShort[selectedMonth] + " " + selectedDay;
                        editTextDate.setText(formattedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        LoadFilteredExpenses("All");

        // Filter button listeners
        filterAll.setOnClickListener(v -> LoadFilteredExpenses("All"));
        filterFood.setOnClickListener(v -> LoadFilteredExpenses("Food"));
        filterTransport.setOnClickListener(v -> LoadFilteredExpenses("Transportation"));
        filterEducation.setOnClickListener(v -> LoadFilteredExpenses("Education"));
        filterBills.setOnClickListener(v -> LoadFilteredExpenses("Bills"));

        // Add Expense Button Click
        buttonAddExpense.setOnClickListener(v -> {
            String amtStr = editTextAmount.getText().toString();
            String titleStr = editTextTitle.getText().toString();
            String catStr = spinnerCategory.getSelectedItem().toString();
            String dateStr = editTextDate.getText().toString();

            if (amtStr.isEmpty() || titleStr.isEmpty() || dateStr.isEmpty() || catStr.equals("Select Category")) {
                Toast.makeText(getApplicationContext(), "Please fill in all fields and select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT INTO expenses (title, category, date, amount) VALUES ('" +
                    titleStr + "','" + catStr + "','" + dateStr + "'," + Double.parseDouble(amtStr) + ")");

            Toast.makeText(getApplicationContext(), "Expense Successfully Added", Toast.LENGTH_SHORT).show();
            editTextAmount.setText("");
            editTextTitle.setText("");
            editTextDate.setText("");
            spinnerCategory.setSelection(0);

            LoadFilteredExpenses("All");
        });
    }

    public void LoadFilteredExpenses(String filterCategory) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        if (filterCategory.equals("All")) {
            cursor = db.rawQuery("SELECT * FROM expenses ORDER BY id DESC", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM expenses WHERE category = ? ORDER BY id DESC", new String[]{filterCategory});
        }

        int count = cursor.getCount();
        Integer[] ids = new Integer[count];
        String[] titles = new String[count];
        String[] cats = new String[count];
        String[] dates = new String[count];
        Double[] amounts = new Double[count];

        cursor.moveToFirst();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            ids[i] = cursor.getInt(0);
            titles[i] = cursor.getString(1);
            cats[i] = cursor.getString(2);
            dates[i] = cursor.getString(3);
            amounts[i] = cursor.getDouble(4);
        }
        cursor.close();

        ExpenseManagementAdapter adapter = new ExpenseManagementAdapter(this, ids, titles, cats, dates, amounts);
        listViewExpenses.setAdapter(adapter);
    }

    // Bottom Navigation Handlers
    public void goHome(View view) { startActivity(new Intent(getApplicationContext(), MainActivity.class)); }
    public void goBudget(View view) { startActivity(new Intent(getApplicationContext(), BudgetActivity.class)); }
    public void goShopping(View view) { startActivity(new Intent(getApplicationContext(), ShoppingActivity.class)); }
    public void goReports(View view) { startActivity(new Intent(getApplicationContext(), ReportsActivity.class)); }
}