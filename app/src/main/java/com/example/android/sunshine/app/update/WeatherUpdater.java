package com.example.android.sunshine.app.update;


import android.content.Context;
import android.support.annotation.NonNull;
import org.json.JSONException;

public abstract class WeatherUpdater {

    protected Context context;

    public abstract void performUpdate(@NonNull Context context);

    protected abstract void getWeatherDataFromJson(String forecastJsonStr,
                                                   String locationSetting)
            throws JSONException;

    protected abstract long addLocation(String locationSetting, String cityName, double lat, double lon);
}
