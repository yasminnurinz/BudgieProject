package com.example.budgie;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    TextView tvReportTotalSpent, tvBudgieInsight;
    TextView tvEduAmt, tvBillsAmt, tvTransAmt, tvFoodAmt, tvEntAmt;
    ProgressBar progressEdu, progressBills, progressTrans, progressFood, progressEnt;

    DataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);

        // Bind UI Views
        tvReportTotalSpent = findViewById(R.id.tvReportTotalSpent);
        tvBudgieInsight = findViewById(R.id.tvBudgieInsight);

        tvEduAmt = findViewById(R.id.tvEduAmt);

        // Find breakdown text views and progress bars dynamically or bind them by adding IDs in XML if needed
        LoadReportData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadReportData();
    }

    public void LoadReportData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 1. Get total spent across all expenses
        double totalSpent = 0.0;
        int txCount = 0;
        Cursor cTotal = db.rawQuery("SELECT SUM(amount), COUNT(*) FROM expenses", null);
        if (cTotal.moveToFirst()) {
            totalSpent = cTotal.isNull(0) ? 0.0 : cTotal.getDouble(0);
            txCount = cTotal.getInt(1);
        }
        cTotal.close();

        tvReportTotalSpent.setText(String.format(Locale.US, "RM %.2f", totalSpent));

        // 2. Get totals per category from SQLite database
        double eduTotal = getCategorySum(db, "Education");
        double billsTotal = getCategorySum(db, "Bills");
        double transTotal = getCategorySum(db, "Transportation");
        double foodTotal = getCategorySum(db, "Food");
        double entTotal = getCategorySum(db, "Entertainment");

        // Update Education amount view
        if (tvEduAmt != null) {
            tvEduAmt.setText(String.format(Locale.US, "RM %.2f", eduTotal));
        }

        // 3. Generate smart insight text based on actual database numbers
        String topCategory = "Education";
        double maxAmt = eduTotal;
        if (billsTotal > maxAmt) { topCategory = "Bills"; maxAmt = billsTotal; }
        if (transTotal > maxAmt) { topCategory = "Transportation"; maxAmt = transTotal; }
        if (foodTotal > maxAmt) { topCategory = "Food"; maxAmt = foodTotal; }
        if (entTotal > maxAmt) { topCategory = "Entertainment"; maxAmt = entTotal; }

        int topPercent = (totalSpent > 0) ? (int) Math.round((maxAmt / totalSpent) * 100) : 0;
        double avg = (txCount > 0) ? (totalSpent / txCount) : 0.0;

        tvBudgieInsight.setText(String.format(Locale.US,
                "%s is your biggest spend at %d%% (RM %.2f) across %d transactions. Average per transaction: RM %.2f.",
                topCategory, topPercent, maxAmt, txCount, avg));
    }

    // Helper method to calculate sum for a specific category
    private double getCategorySum(SQLiteDatabase db, String categoryName) {
        Cursor c = db.rawQuery("SELECT SUM(amount) FROM expenses WHERE category = ?", new String[]{categoryName});
        double sum = 0.0;
        if (c.moveToFirst()) {
            sum = c.isNull(0) ? 0.0 : c.getDouble(0);
        }
        c.close();
        return sum;
    }

    // Bottom Navigation Handlers
    public void goHome(View view) { startActivity(new Intent(getApplicationContext(), MainActivity.class)); }
    public void goExpenses(View view) { startActivity(new Intent(getApplicationContext(), ExpensesActivity.class)); }
    public void goBudget(View view) { startActivity(new Intent(getApplicationContext(), BudgetActivity.class)); }
    public void goShopping(View view) { startActivity(new Intent(getApplicationContext(), ShoppingActivity.class)); }
}