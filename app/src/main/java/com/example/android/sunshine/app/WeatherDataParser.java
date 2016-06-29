package com.example.android.sunshine.app;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser {

    private static final String TAG = WeatherDataParser.class.getSimpleName();

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {

        JSONObject weatherJson = new JSONObject(weatherJsonStr);

        JSONArray days = weatherJson.getJSONArray("list");

        if(days.length() <= dayIndex) {
            Log.e(TAG, "JSON has less elements in 'list' params, then requested. " +
                    "Requested index: " + dayIndex +
                    " Length is: " + days.length());
            return -1;
        }

        JSONObject dayInfo = days.getJSONObject(dayIndex);
        JSONObject temperatureInfo = dayInfo.getJSONObject("temp");

        return temperatureInfo.getDouble("max");
    }
}
