package com.example.budgie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "budgie.db";
    private static final int DATABASE_VERSION = 2; // Incremented version to refresh schema

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table for monthly budget
        db.execSQL("CREATE TABLE budget (id integer primary key, month text null, amount real);");
        // Table for expenses
        db.execSQL("CREATE TABLE expenses (id integer primary key autoincrement, title text null, category text null, date text null, amount real);");
        // Table for shopping budget
        db.execSQL("CREATE TABLE shopping_budget (id integer primary key, amount real);");
        // Table for shopping checklist items (is_checked: 0 = unchecked, 1 = checked/in-cart)
        db.execSQL("CREATE TABLE shopping_checklist (id integer primary key autoincrement, item_name text null, price real, is_checked integer);");

        // Sample default data
        db.execSQL("INSERT INTO budget (id, month, amount) VALUES (1, 'October 2026', 300.00);");
        db.execSQL("INSERT INTO shopping_budget (id, amount) VALUES (1, 50.00);");
        db.execSQL("INSERT INTO shopping_checklist (item_name, price, is_checked) VALUES ('Rice 2kg', 6.50, 1);");
        db.execSQL("INSERT INTO shopping_checklist (item_name, price, is_checked) VALUES ('Eggs (12)', 4.00, 0);");
        db.execSQL("INSERT INTO shopping_checklist (item_name, price, is_checked) VALUES ('Instant noodles', 3.20, 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS budget");
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS shopping_budget");
        db.execSQL("DROP TABLE IF EXISTS shopping_checklist");
        onCreate(db);
    }
}