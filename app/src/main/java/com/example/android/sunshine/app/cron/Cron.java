package com.example.android.sunshine.app.cron;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.example.android.sunshine.app.notification.NotificationType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Cron {
    private static final String TAG = Cron.class.getSimpleName();

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    public static void addAlarm(Params params) {

        if(params.hasErrors()) {
            logErrors(params);
            return;
        }

        Log.d(TAG, "Setting alarm for H: " + params.hour + " M: " + params.min);

        long currentTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, params.hour);
        calendar.set(Calendar.MINUTE, params.min);
        calendar.set(Calendar.SECOND, 0);

        if(currentTime > calendar.getTimeInMillis()) {
            //current time already after alarm time => setting alarm for tomorrow
            calendar.setTimeInMillis(currentTime + Cron.DAY_IN_MILLIS);
            calendar.set(Calendar.HOUR_OF_DAY, params.hour);
            calendar.set(Calendar.MINUTE, params.min);
            calendar.set(Calendar.SECOND, 0);
        }

        Intent intent = new Intent(params.context, params.type.getReceiverClass());

        PendingIntent pi = PendingIntent.getBroadcast(params.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) params.context.getSystemService(Context.ALARM_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pi
            );
        } else {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pi
            );
        }
    }

    public static void removeAlarm(Context context, NotificationType type) {
        Intent intent = new Intent(context, type.getReceiverClass());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    private static void logErrors(Params params) {
        StringBuilder errorCollector = new StringBuilder("Not setting alarm. Errors found: ");
        for(String error : params.getErrors()) {
            errorCollector.append(error).append(" ");
        }
        errorCollector.trimToSize();
        Log.e(TAG, errorCollector.toString());
    }

    public static class Params {
        int hour;
        int min;
        Context context;
        NotificationType type;

        List<String> errors = new ArrayList<>();

        public static Params.Builder createParams() {

            Builder b = new Params.Builder();

            //init with defaults
            b.params.hour = -1;
            b.params.min = -1;
            b.params.context = null;
            b.params.type = null;

            return b;
        }

        boolean hasErrors() {
            return errors.size() > 0;
        }

        List<String> getErrors() {
            return errors;
        }

        public static class Builder {

            private Params params = new Params();

            public Params.Builder context(Context context) {
                params.context = context;
                return this;
            }

            public Params.Builder time(int hour, int min) {
                params.hour = hour;
                params.min = min;
                return this;
            }

            public Params.Builder type(NotificationType type) {
                params.type = type;
                return this;
            }

            public Params create() {
                final int MIN_HOUR = 0;
                final int MAX_HOUR = 24;
                if(params.hour < MIN_HOUR && params.hour > MAX_HOUR) {
                    @SuppressLint("DefaultLocale") //This message will be in logs only
                            String message = String.format("Hour value should  be within range %d to %d", MIN_HOUR, MAX_HOUR);
                    params.errors.add(message);
                }

                final int MIN_MINUTE = 0;
                final int MAX_MINUTE = 59;
                if(params.min < MIN_MINUTE && params.min > MAX_MINUTE) {
                    @SuppressLint("DefaultLocale") //This message will be in logs only
                            String message = String.format("Minutes value should  be within range %d to %d", MIN_MINUTE, MAX_MINUTE);
                    params.errors.add(message);
                }

                if(params.context == null) {
                    @SuppressLint("DefaultLocale") //This message will be in logs only
                            String message = "Context cannot be NULL";
                    params.errors.add(message);
                }

                if(params.type == null) {
                    String message = "Notification Type cannot be NULL, some class should receive alarm";
                    params.errors.add(message);
                }

                return params;
            }
        }

    }

}
