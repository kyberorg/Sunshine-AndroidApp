package com.example.android.sunshine.app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.android.sunshine.app.R;

public class SettingsUtility {
    public static boolean areOnUpdateNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(
                context.getString(R.string.pref_enable_on_update_notifications_key),
                Boolean.parseBoolean(context.getString(R.string.pref_enable_on_update_notifications_key)
                ));
    }

    public static long getOnUpdateNotificationInterval(Context context) {
        //TODO interval settings not supported yet
        return 0L;
    }

    public static boolean areDailyNotificationsEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(
                context.getString(R.string.pref_enable_daily_notifications_key),
                Boolean.parseBoolean(context.getString(R.string.pref_enable_daily_notifications_default))
        );
    }

    public static boolean isMorningNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(
                context.getString(R.string.pref_enable_morning_notification_key),
                Boolean.parseBoolean(context.getString(R.string.pref_enable_morning_notification_default))
        );
    }

    public static String getMorningNotificationTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_time_morning_notification_key),
                context.getString(R.string.pref_time_morning_notification_default)
        );
    }

    public static boolean isEveningNotificationEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(
                context.getString(R.string.pref_enable_evening_notification_key),
                Boolean.parseBoolean(context.getString(R.string.pref_enable_evening_notification_default))
        );
    }

    public static String getEveningNotificationTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_time_evening_notification_key),
                context.getString(R.string.pref_time_evening_notification_default)
        );
    }
}
