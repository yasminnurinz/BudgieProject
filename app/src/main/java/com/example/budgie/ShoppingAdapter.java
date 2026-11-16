package com.example.budgie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ShoppingAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final Integer[] ids;
    private final String[] names;
    private final Double[] prices;
    private final Integer[] statuses;
    private final DataHelper dbHelper;

    public ShoppingAdapter(Context context, Integer[] ids, String[] names, Double[] prices, Integer[] statuses) {
        super(context, R.layout.custom_shopping_row, names);
        this.context = context;
        this.ids = ids;
        this.names = names;
        this.prices = prices;
        this.statuses = statuses;
        this.dbHelper = new DataHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_shopping_row, null, true);

        CheckBox checkBox = row.findViewById(R.id.checkBoxItem);
        TextView tvName = row.findViewById(R.id.tvItemName);
        TextView tvPrice = row.findViewById(R.id.tvItemPrice);
        Button btnDelete = row.findViewById(R.id.btnDeleteRow);

        tvName.setText(names[position]);
        tvPrice.setText(String.format("RM %.2f", prices[position]));
        checkBox.setChecked(statuses[position] == 1);

        checkBox.setOnClickListener(v -> {
            int newStatus = checkBox.isChecked() ? 1 : 0;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE shopping_checklist SET is_checked = " + newStatus + " WHERE id = " + ids[position]);
            if (context instanceof ShoppingActivity) {
                ((ShoppingActivity) context).LoadShoppingData();
            }
        });

        btnDelete.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("DELETE FROM shopping_checklist WHERE id = " + ids[position]);
            if (context instanceof ShoppingActivity) {
                ((ShoppingActivity) context).LoadShoppingData();
            }
        });

        return row;
    }
}