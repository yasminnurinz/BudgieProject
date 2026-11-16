package com.example.budgie;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class ExpenseManagementAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final Integer[] ids;
    private final String[] titles;
    private final String[] categories;
    private final String[] dates;
    private final Double[] amounts;
    private final DataHelper dbHelper;

    public ExpenseManagementAdapter(Context context, Integer[] ids, String[] titles, String[] categories, String[] dates, Double[] amounts) {
        super(context, R.layout.custom_expense_row, titles);
        this.context = context;
        this.ids = ids;
        this.titles = titles;
        this.categories = categories;
        this.dates = dates;
        this.amounts = amounts;
        this.dbHelper = new DataHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_expense_row, null, true);

        ImageView imgIcon = row.findViewById(R.id.imgCategoryIcon);
        TextView tvTitle = row.findViewById(R.id.tvItemTitle);
        TextView tvSubtitle = row.findViewById(R.id.tvItemSubtitle);
        TextView tvAmount = row.findViewById(R.id.tvItemAmount);
        Button btnEdit = row.findViewById(R.id.btnEditRow);
        Button btnDelete = row.findViewById(R.id.btnDeleteRow);

        tvTitle.setText(titles[position]);
        tvSubtitle.setText(categories[position] + " · " + dates[position]);
        tvAmount.setText(String.format(Locale.US, "-RM %.2f", amounts[position]));

        // Set category icon based on category type
        String cat = categories[position];
        if (cat.equalsIgnoreCase("Transportation")) {
            imgIcon.setImageResource(android.R.drawable.ic_menu_directions);
        } else if (cat.equalsIgnoreCase("Bills")) {
            imgIcon.setImageResource(android.R.drawable.ic_dialog_info);
        } else if (cat.equalsIgnoreCase("Food")) {
            imgIcon.setImageResource(android.R.drawable.ic_menu_agenda);
        } else {
            imgIcon.setImageResource(android.R.drawable.ic_menu_compass);
        }

        // Delete Button Action
        btnDelete.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("DELETE FROM expenses WHERE id = " + ids[position]);
            Toast.makeText(context, "Expense Deleted", Toast.LENGTH_SHORT).show();
            if (context instanceof ExpensesActivity) {
                ((ExpensesActivity) context).LoadFilteredExpenses("All");
            }
        });

        // Edit Button Action (Popup Dialog to update amount or title)
        btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Expense");

            final EditText input = new EditText(context);
            input.setText(String.valueOf(amounts[position]));
            input.setHint("New amount");
            builder.setView(input);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String newAmtStr = input.getText().toString();
                if (!newAmtStr.isEmpty()) {
                    double newAmt = Double.parseDouble(newAmtStr);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("UPDATE expenses SET amount = " + newAmt + " WHERE id = " + ids[position]);
                    Toast.makeText(context, "Expense Updated", Toast.LENGTH_SHORT).show();
                    if (context instanceof ExpensesActivity) {
                        ((ExpensesActivity) context).LoadFilteredExpenses("All");
                    }
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        return row;
    }
}