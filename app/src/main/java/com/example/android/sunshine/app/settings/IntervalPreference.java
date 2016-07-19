package com.example.android.sunshine.app.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import com.example.android.sunshine.app.R;


public class IntervalPreference extends DialogPreference {
    private static final String TAG = IntervalPreference.class.getSimpleName();

    private NumberPicker picker;
    private IntervalPicker picker1;

    public IntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(R.string.time_picker_dialog_set_button_text);
        setNegativeButtonText(R.string.time_picker_dialog_cancel_button_text);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.gravity = Gravity.CENTER;

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(params);

        picker1 = new IntervalPicker(getContext());
        picker1.setLayoutParams(params);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker1);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        //TODO setting current values
        //picker.setMaxValue(12);
        //picker.setMinValue(0);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult) {
           /* //TODO get values
           *//* int number = picker.getDigit();
            String timeUnit = picker.getTimeUnit();*//*
           int number = picker.getValue();
           String timeUnit = "min";

            //TODO logic
            String newValue = number + " " + timeUnit;
            if(callChangeListener(newValue)) {
                //FIXME persist long
                persistLong(number);
            }*/
        }

    }
}
