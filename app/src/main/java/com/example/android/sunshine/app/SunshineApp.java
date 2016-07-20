package com.example.android.sunshine.app;

import android.app.Application;
import com.example.android.sunshine.app.cron.Cron;
import com.example.android.sunshine.app.notification.NotificationType;
import com.example.android.sunshine.app.settings.SettingsUtility;

import static com.example.android.sunshine.app.settings.TimePreference.getHour;
import static com.example.android.sunshine.app.settings.TimePreference.getMinute;


public class SunshineApp extends Application {
    @Override
    public void onCreate() {
        setDailyNotification();
        super.onCreate();
    }

    private void setDailyNotification() {
        boolean areDailyNotificationsEnabled = SettingsUtility.areDailyNotificationsEnabled(getApplicationContext());
        if(areDailyNotificationsEnabled) {
            //morning notification
            boolean isMorningNotificationEnabled = SettingsUtility.isMorningNotificationEnabled(getApplicationContext());
            if(isMorningNotificationEnabled) {
                String morningTime = SettingsUtility.getMorningNotificationTime(getApplicationContext());
                Cron.Params morningParams = Cron.Params.createParams()
                        .context(getApplicationContext())
                        .type(NotificationType.MORNING)
                        .time(getHour(morningTime), getMinute(morningTime))
                        .create();

                Cron.addAlarm(morningParams);
            }

            //evening notification
            boolean isEveningNotificationEnabled = SettingsUtility.isEveningNotificationEnabled(getApplicationContext());
            if(isEveningNotificationEnabled) {
                String eveningTime = SettingsUtility.getEveningNotificationTime(getApplicationContext());
                Cron.Params eveningParams = Cron.Params.createParams()
                        .context(getApplicationContext())
                        .type(NotificationType.EVENING)
                        .time(getHour(eveningTime), getMinute(eveningTime))
                        .create();
                Cron.addAlarm(eveningParams);
            }
        }
    }

}
