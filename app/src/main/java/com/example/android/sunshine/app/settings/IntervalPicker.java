package com.example.android.sunshine.app.settings;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class IntervalPicker extends LinearLayout {
    public IntervalPicker(Context context) {
        this(context, null);
    }

    public IntervalPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TextView tv = new TextView(context, attrs);
        tv.setText("Haba-haba");

        LinearLayout main = new LinearLayout(context, attrs);
        main.setOrientation(HORIZONTAL);

        LinearLayout numberPicker = new LinearLayout(context, attrs);
        numberPicker.setOrientation(VERTICAL);

        Button up = new Button(context, attrs);
        up.setText("+");

        TextView currentValue = new TextView(context, attrs);
        currentValue.setText("123");

        Button down = new Button(context, attrs);
        down.setText("-");

        numberPicker.addView(up);
        numberPicker.addView(currentValue);
        numberPicker.addView(down);

        LinearLayout unitsPicker = new LinearLayout(context, attrs);
        unitsPicker.setOrientation(VERTICAL);

        TextView first = new TextView(context, attrs);
        first.setText("seconds");

        TextView second = new TextView(context, attrs);
        second.setText("mins");

        TextView third = new TextView(context, attrs);
        third.setText("hours");

        unitsPicker.addView(first);
        unitsPicker.addView(second);
        unitsPicker.addView(third);

        main.addView(numberPicker);
        main.addView(unitsPicker);

        addView(tv);
        addView(main);
    }

}
