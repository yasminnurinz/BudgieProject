package com.example.budgie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpenseListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] title;
    private final String[] subtitle;
    private final String[] amount;
    private final Integer[] imageID;

    public ExpenseListAdapter(Context context, String[] title, String[] subtitle, String[] amount, Integer[] imageID) {
        super(context, R.layout.custom_expense_layout, title);
        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
        this.amount = amount;
        this.imageID = imageID;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.custom_expense_layout, null, true);

        TextView titleTxt = rootView.findViewById(R.id.title_id);
        TextView subtitleTxt = rootView.findViewById(R.id.subtitle_id);
        TextView amountTxt = rootView.findViewById(R.id.amount_id);
        ImageView imageView = rootView.findViewById(R.id.imag_id);

        titleTxt.setText(title[position]);
        subtitleTxt.setText(subtitle[position]);
        amountTxt.setText(amount[position]);
        imageView.setImageResource(imageID[position]);

        return rootView;
    }
}