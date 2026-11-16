package com.example.budgie;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvMonthRemaining, tvRemainingAmount, tvBudgetAmount, tvSpentAmount, tvSeeAll;
    ProgressBar progressBudget;
    Button btnQuickExpenses, btnQuickBudget, btnQuickShopping, btnQuickReports;
    ListView listViewRecent;

    protected Cursor cursor;
    DataHelper dbcenter;
    public static MainActivity ma;

    String[] titleList;
    String[] subtitleList;
    String[] amountList;
    Integer[] imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvMonthRemaining = findViewById(R.id.tvMonthRemaining);
        tvRemainingAmount = findViewById(R.id.tvRemainingAmount);
        tvBudgetAmount = findViewById(R.id.tvBudgetAmount);
        tvSpentAmount = findViewById(R.id.tvSpentAmount);
        tvSeeAll = findViewById(R.id.tvSeeAll);
        progressBudget = findViewById(R.id.progressBudget);

        btnQuickExpenses = findViewById(R.id.btnQuickExpenses);
        btnQuickBudget = findViewById(R.id.btnQuickBudget);
        btnQuickShopping = findViewById(R.id.btnQuickShopping);
        btnQuickReports = findViewById(R.id.btnQuickReports);

        listViewRecent = findViewById(R.id.listViewRecent);

        ma = this;
        dbcenter = new DataHelper(this);
        RefreshDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshDashboard();
    }

    public void RefreshDashboard() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();

        double budget = 300.00;
        String month = "October 2026";
        Cursor budgetCursor = db.rawQuery("SELECT * FROM budget WHERE id = 1", null);
        if (budgetCursor.moveToFirst()) {
            month = budgetCursor.getString(1);
            budget = budgetCursor.getDouble(2);
        }
        budgetCursor.close();

        cursor = db.rawQuery("SELECT * FROM expenses ORDER BY id DESC", null);
        int count = cursor.getCount();

        titleList = new String[count];
        subtitleList = new String[count];
        amountList = new String[count];
        imageList = new Integer[count];

        double totalSpent = 0.0;
        cursor.moveToFirst();

        for (int cc = 0; cc < count; cc++) {
            cursor.moveToPosition(cc);
            String itemTitle = cursor.getString(1);
            String itemCategory = cursor.getString(2);
            String itemDate = cursor.getString(3);
            double itemAmount = cursor.getDouble(4);

            totalSpent += itemAmount;

            titleList[cc] = itemTitle;
            subtitleList[cc] = itemCategory + " · " + itemDate;
            amountList[cc] = String.format(Locale.US, "-RM %.2f", itemAmount);

            if (itemCategory.equalsIgnoreCase("Transportation")) {
                imageList[cc] = android.R.drawable.ic_menu_directions;
            } else if (itemCategory.equalsIgnoreCase("Bills")) {
                imageList[cc] = android.R.drawable.ic_dialog_info;
            } else {
                imageList[cc] = android.R.drawable.ic_menu_agenda;
            }
        }
        cursor.close();

        double remaining = budget - totalSpent;
        tvMonthRemaining.setText(month + " · Remaining");
        tvRemainingAmount.setText(String.format(Locale.US, "RM %.2f", remaining));
        tvBudgetAmount.setText(String.format(Locale.US, "RM %.2f", budget));
        tvSpentAmount.setText(String.format(Locale.US, "RM %.2f", totalSpent));

        int progressPercent = (budget > 0) ? (int) ((totalSpent / budget) * 100) : 0;
        progressBudget.setProgress(Math.min(progressPercent, 100));

        ExpenseListAdapter adapter = new ExpenseListAdapter(MainActivity.this, titleList, subtitleList, amountList, imageList);
        listViewRecent.setAdapter(adapter);

        listViewRecent.setOnItemClickListener((parent, view, position, id) ->
                Toast.makeText(getApplicationContext(), titleList[position] + " (" + amountList[position] + ")", Toast.LENGTH_SHORT).show());
    }

    public void openExpenses(View view) {
        startActivity(new Intent(getApplicationContext(), ExpensesActivity.class));
    }

    public void openBudget(View view) {
        startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
    }

    public void openShopping(View view) {
        startActivity(new Intent(getApplicationContext(), ShoppingActivity.class));
    }

    public void openReports(View view) {
        startActivity(new Intent(getApplicationContext(), ReportsActivity.class));
    }
}