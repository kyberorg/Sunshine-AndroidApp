package com.example.android.sunshine.app.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import com.example.android.sunshine.app.R;


public class TimePreference extends DialogPreference {
    private static final String TAG = TimePreference.class.getSimpleName();

    public static final int WRONG_NUMBER = -1;
    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker picker = null;

    private String defaultValue = "00:00";

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText(R.string.time_picker_dialog_set_button_text);
        setNegativeButtonText(R.string.time_picker_dialog_cancel_button_text);

    }

    public static int getHour(String time) {
        if(time == null || time.trim().isEmpty()) {
            return WRONG_NUMBER;
        }
        String[] pieces = time.split(":");
        int hour;
        try {
            hour = Integer.parseInt(pieces[0]);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return WRONG_NUMBER;
        }

        return hour;
    }

    public static int getMinute(String time) {
        if(time == null || time.trim().isEmpty()) {
            return WRONG_NUMBER;
        }
        String[] pieces = time.split(":");
        int minutes;
        try {
            minutes = Integer.parseInt(pieces[1]);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return WRONG_NUMBER;
        }
        return minutes;
    }


    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());

        return picker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
        picker.setIs24HourView(true);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();

            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);

            if(callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time = "09:00";

        if(defaultValue != null) {
            time = defaultValue.toString();
            this.defaultValue = time;
        }

        if(restorePersistedValue) {
            time = getPersistedString(time);
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
    }

    public String valueToString(Object value) {
        String timeFromValue = value.toString();

        if(timeFromValue.trim().equals("")){
            timeFromValue = defaultValue;
        }

        int hours = getHour(timeFromValue);
        int minutes = getMinute(timeFromValue);

        String hoursStr = (hours <= 9) ? "0" + hours : "" + hours;
        String minutesStr = (minutes <= 9) ? "0" + minutes : "" + minutes;
        return hoursStr + ":" + minutesStr;
    }
}
