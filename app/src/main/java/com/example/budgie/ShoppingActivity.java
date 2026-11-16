package com.example.budgie;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class ShoppingActivity extends AppCompatActivity {

    EditText editTextShoppingBudget, editTextItemName, editTextItemPrice;
    Button buttonSetShoppingBudget, buttonAddItem;
    TextView tvShoppingBudgetLeft, tvBoxBudget, tvBoxEstTotal, tvBoxInCart, tvChecklistProgress, tvClearChecked;
    ListView listViewChecklist;
    DataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);

        editTextShoppingBudget = findViewById(R.id.editTextShoppingBudget);
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemPrice = findViewById(R.id.editTextItemPrice);
        buttonSetShoppingBudget = findViewById(R.id.buttonSetShoppingBudget);
        buttonAddItem = findViewById(R.id.buttonAddItem);

        tvShoppingBudgetLeft = findViewById(R.id.tvShoppingBudgetLeft);
        tvBoxBudget = findViewById(R.id.tvBoxBudget);
        tvBoxEstTotal = findViewById(R.id.tvBoxEstTotal);
        tvBoxInCart = findViewById(R.id.tvBoxInCart);
        tvChecklistProgress = findViewById(R.id.tvChecklistProgress);
        tvClearChecked = findViewById(R.id.tvClearChecked);
        listViewChecklist = findViewById(R.id.listViewChecklist);

        LoadShoppingData();

        buttonSetShoppingBudget.setOnClickListener(v -> {
            String bStr = editTextShoppingBudget.getText().toString();
            if (bStr.isEmpty()) {
                Toast.makeText(this, "Enter shopping budget", Toast.LENGTH_SHORT).show();
                return;
            }
            double newB = Double.parseDouble(bStr);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE shopping_budget SET amount = " + newB + " WHERE id = 1");
            Toast.makeText(this, "Shopping Budget Updated", Toast.LENGTH_SHORT).show();
            LoadShoppingData();
        });

        buttonAddItem.setOnClickListener(v -> {
            String name = editTextItemName.getText().toString();
            String priceStr = editTextItemPrice.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Enter item name and price", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT INTO shopping_checklist (item_name, price, is_checked) VALUES ('" + name + "', " + price + ", 0)");

            editTextItemName.setText("");
            editTextItemPrice.setText("");
            Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();
            LoadShoppingData();
        });

        tvClearChecked.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("DELETE FROM shopping_checklist WHERE is_checked = 1");
            Toast.makeText(this, "Cleared checked items", Toast.LENGTH_SHORT).show();
            LoadShoppingData();
        });
    }

    public void LoadShoppingData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        double shopBudget = 50.00;
        Cursor cB = db.rawQuery("SELECT amount FROM shopping_budget WHERE id = 1", null);
        if (cB.moveToFirst()) {
            shopBudget = cB.getDouble(0);
        }
        cB.close();
        editTextShoppingBudget.setText(String.format(Locale.US, "%.0f", shopBudget));

        Cursor cList = db.rawQuery("SELECT * FROM shopping_checklist", null);
        int count = cList.getCount();

        Integer[] ids = new Integer[count];
        String[] names = new String[count];
        Double[] prices = new Double[count];
        Integer[] statuses = new Integer[count];

        double estTotal = 0.0;
        double inCart = 0.0;
        int checkedCount = 0;

        cList.moveToFirst();
        for (int i = 0; i < count; i++) {
            cList.moveToPosition(i);
            ids[i] = cList.getInt(0);
            names[i] = cList.getString(1);
            prices[i] = cList.getDouble(2);
            statuses[i] = cList.getInt(3);

            estTotal += prices[i];
            if (statuses[i] == 1) {
                inCart += prices[i];
                checkedCount++;
            }
        }
        cList.close();

        double left = shopBudget - estTotal;

        tvShoppingBudgetLeft.setText(String.format(Locale.US, "RM %.2f", left));
        tvBoxBudget.setText(String.format(Locale.US, "RM %.2f", shopBudget));
        tvBoxEstTotal.setText(String.format(Locale.US, "RM %.2f", estTotal));
        tvBoxInCart.setText(String.format(Locale.US, "RM %.2f", inCart));
        tvChecklistProgress.setText(checkedCount + "/" + count + " done");

        ShoppingAdapter adapter = new ShoppingAdapter(this, ids, names, prices, statuses);
        listViewChecklist.setAdapter(adapter);
    }

    public void goHome(View view) { startActivity(new Intent(getApplicationContext(), MainActivity.class)); }
    public void goExpenses(View view) { startActivity(new Intent(getApplicationContext(), ExpensesActivity.class)); }
    public void goBudget(View view) { startActivity(new Intent(getApplicationContext(), BudgetActivity.class)); }
    public void goReports(View view) { startActivity(new Intent(getApplicationContext(), ReportsActivity.class)); }
}