package com.example.android.sunshine.app.notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;

public class ForecastNotifier {

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[]{
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID ,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP ,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP ,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };
    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;
    private static final int INDEX_SHORT_DESC = 3;

    private static final int WEATHER_NOTIFICATION_ID = 3004;

    public static void notifyWithInterval(Context context, long interval) {
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        long lastSync = prefs.getLong(lastNotificationKey, 0);

        if(System.currentTimeMillis() - lastSync >= interval) {
            // Last sync was more than 1 day ago, let's send a notification with the weather.
            notify(context);

            //refreshing last sync
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastNotificationKey, System.currentTimeMillis());
            editor.apply();
        }
    }

    static void notify(Context context) {
        String locationQuery = Utility.getPreferredLocation(context);

        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());

        // we'll query our contentProvider, as always
        Cursor cursor = context.getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);

        assert cursor != null;
        if(cursor.moveToFirst()) {
            int weatherId = cursor.getInt(INDEX_WEATHER_ID);
            double high = cursor.getDouble(INDEX_MAX_TEMP);
            double low = cursor.getDouble(INDEX_MIN_TEMP);
            String desc = cursor.getString(INDEX_SHORT_DESC);

            String location = Utility.getPreferredLocation(context);

            int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
            String title = context.getString(R.string.app_name_for_notification);

            // Define the text of the forecast.
            String contentText = String.format(context.getString(R.string.format_notification),
                    location,
                    desc,
                    Utility.formatTemperature(context, high),
                    Utility.formatTemperature(context, low));

            //Multi-line text
            NotificationCompat.InboxStyle multiLine = new NotificationCompat.InboxStyle();
            multiLine.addLine(location);
            multiLine.addLine(contentText);


            //build your notification here.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(iconId)
                    .setContentTitle(title);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.setStyle(multiLine);
            } else {
                builder.setContentText(contentText);
            }

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            builder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(WEATHER_NOTIFICATION_ID, builder.build());

            cursor.close();
        }
    }
}
