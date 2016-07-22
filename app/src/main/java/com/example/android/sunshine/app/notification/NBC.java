package com.example.android.sunshine.app.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.settings.SettingsUtility;

/**
 * NBC - stands for Notification Broadcasts Class
 */
public class NBC {
    private static final String TAG = NBC.class.getSimpleName();

    /**
     * Weather update waiting time in seconds
     */
    private static int JITTER = 30;

    private static void waitForWeatherUpdateSomeTime() {
        try {
            Thread.sleep(JITTER * 1000);
        } catch(InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static class MorningNotificationReceiver extends BroadcastReceiver {
        private static final String TAG = MorningNotificationReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received morning notification");

            boolean areDailyNotificationsEnabled = SettingsUtility.areDailyNotificationsEnabled(context);
            boolean isMorningNotificationsEnabled = SettingsUtility.isMorningNotificationEnabled(context);

            if(areDailyNotificationsEnabled && isMorningNotificationsEnabled) {
                Utility.updateWeather(context);
                NBC.waitForWeatherUpdateSomeTime();
                ForecastNotifier.notify(context);
            }
        }
    }

    public static class EveningNotificationReceiver extends BroadcastReceiver {
        private static final String TAG = EveningNotificationReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received evening notification");

            boolean areDailyNotificationsEnabled = SettingsUtility.areDailyNotificationsEnabled(context);
            boolean isEveningNotificationsEnabled = SettingsUtility.isEveningNotificationEnabled(context);

            if(areDailyNotificationsEnabled && isEveningNotificationsEnabled) {
                Utility.updateWeather(context);
                NBC.waitForWeatherUpdateSomeTime();
                Log.d(TAG, "Do notify");
                ForecastNotifier.notify(context);
            }
        }
    }
}
