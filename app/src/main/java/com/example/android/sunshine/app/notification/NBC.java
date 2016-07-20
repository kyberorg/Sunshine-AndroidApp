package com.example.android.sunshine.app.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.android.sunshine.app.settings.SettingsUtility;

/**
 * NBC - stands for Notification Broadcasts Class
 */
public class NBC {

    public static class MorningNotificationReceiver extends BroadcastReceiver {
        private static final String TAG = MorningNotificationReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received morning notification");

            boolean areDailyNotificationsEnabled = SettingsUtility.areDailyNotificationsEnabled(context);
            boolean isMorningNotificationsEnabled = SettingsUtility.isMorningNotificationEnabled(context);

            if(areDailyNotificationsEnabled && isMorningNotificationsEnabled) {
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
                ForecastNotifier.notify(context);
            }
        }
    }
}
