package com.example.android.sunshine.app.settings;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import android.util.Log;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.cron.Cron;
import com.example.android.sunshine.app.notification.NotificationType;

import static com.example.android.sunshine.app.settings.TimePreference.getHour;
import static com.example.android.sunshine.app.settings.TimePreference.getMinute;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_time_morning_notification_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_time_evening_notification_key)));

        //For true/false preferences, attaching an OnPreferenceClickListener
        setOnClickListenerFor(findPreference(getString(R.string.pref_enable_daily_notifications_key)));
        setOnClickListenerFor(findPreference(getString(R.string.pref_enable_morning_notification_key)));
        setOnClickListenerFor(findPreference(getString(R.string.pref_enable_evening_notification_key)));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listeners immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void setOnClickListenerFor(Preference pref) {
        pref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if(preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if(preference instanceof TimePreference) {
            //For our time preference we are adding leading zeros
            TimePreference pref = (TimePreference) preference;
            preference.setSummary(pref.valueToString(value));
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

        //if morning notification time changed
        if(preference.getKey().equals(this.getString(R.string.pref_time_morning_notification_key))) {
            onMorningNotificationTimeChanged(stringValue);
        }

        //if evening notification time changed
        if(preference.getKey().equals(this.getString(R.string.pref_time_evening_notification_key))) {
            onEveningNotificationTimeChanged(stringValue);
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if(!(preference instanceof CheckBoxPreference)) {
            Log.wtf(TAG, "Expect " + CheckBoxPreference.class.getSimpleName() + " here");
            return false;
        }

        CheckBoxPreference pref = (CheckBoxPreference) preference;

        //if daily notifications enabled/disabled
        if(preference.getKey().equals(getString(R.string.pref_enable_daily_notifications_key))) {
            onDailyNotificationChange(pref.isChecked());
        }

        //if morning notification enabled/disabled
        if(preference.getKey().equals(this.getString(R.string.pref_enable_morning_notification_key))) {
            onMorningNotificationChanged(pref.isChecked());
        }

        //if evening notification enabled/disabled
        if(preference.getKey().equals(this.getString(R.string.pref_enable_evening_notification_key))) {
            onEveningNotificationChanged(pref.isChecked());
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if(intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }

    private void onMorningNotificationTimeChanged(String value) {
        String currentValue = SettingsUtility.getMorningNotificationTime(this);
        if(!value.equals(currentValue)) {
            Log.d(TAG, "Morning notification time changed");
            int hour = getHour(value);
            int min = getMinute(value);

            Cron.Params params = Cron.Params.createParams()
                    .context(getApplicationContext())
                    .time(hour, min)
                    .type(NotificationType.MORNING)
                    .create();
            Cron.addAlarm(params);
        }
    }

    private void onEveningNotificationTimeChanged(String value) {
        String currentValue = SettingsUtility.getEveningNotificationTime(this);
        if(!value.equals(currentValue)) {
            Log.d(TAG, "Evening notification time changed");
            int hour = getHour(value);
            int min = getMinute(value);

            Cron.Params params = Cron.Params.createParams()
                    .context(getApplicationContext())
                    .time(hour, min)
                    .type(NotificationType.EVENING)
                    .create();
            Cron.addAlarm(params);
        }
    }

    private void onMorningNotificationChanged(boolean value) {

        Preference morningTime = findPreference(getString(R.string.pref_time_morning_notification_key));

        boolean disabled = !value;
        if(disabled) {
            Log.d(TAG, "Morning notifications are disabled");
            Cron.removeAlarm(getApplicationContext(), NotificationType.MORNING);
            morningTime.setEnabled(false);
        } else {
            Log.d(TAG, "Morning notifications are enabled");

            String notificationTime = SettingsUtility.getMorningNotificationTime(this);

            Cron.addAlarm(Cron.Params.createParams()
                    .context(getApplicationContext())
                    .type(NotificationType.MORNING)
                    .time(getHour(notificationTime), getMinute(notificationTime))
                    .create()
            );
            morningTime.setEnabled(true);
        }

    }

    private void onEveningNotificationChanged(boolean value) {

        Preference eveningTime = findPreference(getString(R.string.pref_time_evening_notification_key));

        boolean disabled = !value;
        if(disabled) {
            Log.d(TAG, "Evening notifications are disabled");
            Cron.removeAlarm(getApplicationContext(), NotificationType.EVENING);
            eveningTime.setEnabled(false);
        } else {
            Log.d(TAG, "Evening notifications are enabled");

            String notificationTime = SettingsUtility.getEveningNotificationTime(this);

            Cron.addAlarm(Cron.Params.createParams()
                    .context(getApplicationContext())
                    .type(NotificationType.EVENING)
                    .time(getHour(notificationTime), getMinute(notificationTime))
                    .create()
            );
            eveningTime.setEnabled(true);
        }
    }

    private void onDailyNotificationChange(boolean enabled) {
        CheckBoxPreference morningEnabled = (CheckBoxPreference) findPreference(getString(R.string.pref_enable_morning_notification_key));
        CheckBoxPreference eveningEnabled = (CheckBoxPreference) findPreference(getString(R.string.pref_enable_evening_notification_key));

        if(enabled) {
            morningEnabled.setEnabled(true);
            eveningEnabled.setEnabled(true);

            morningEnabled.setChecked(true);
            eveningEnabled.setChecked(true);

            onMorningNotificationChanged(true);
            onEveningNotificationChanged(true);
        } else {
            morningEnabled.setEnabled(false);
            eveningEnabled.setEnabled(false);

            morningEnabled.setChecked(false);
            eveningEnabled.setChecked(false);

            onMorningNotificationChanged(false);
            onEveningNotificationChanged(false);
        }
    }

}
