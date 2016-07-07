package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.sunshine.app.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private static final String HASHTAG = " #WeatherApp";
    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID ,
            WeatherContract.WeatherEntry.COLUMN_DATE ,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC ,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP ,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP ,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY ,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE ,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED ,
            WeatherContract.WeatherEntry.COLUMN_DEGREES ,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID ,

    };
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;


    private ImageView iconView;
    private TextView friendlyDateView;
    private TextView dateView;
    private TextView descriptionView;
    private TextView highTempView;
    private TextView lowTempView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;

    private String forecastString;
    private ShareActionProvider shareActionProvider;
    private Uri uri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        if(args != null) {
            uri = args.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        dateView = (TextView) rootView.findViewById(R.id.detail_date_text);
        friendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_text);
        descriptionView = (TextView) rootView.findViewById(R.id.detail_conditions_text);
        highTempView = (TextView) rootView.findViewById(R.id.detail_high_text);
        lowTempView = (TextView) rootView.findViewById(R.id.detail_low_text);
        humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_text);
        windView = (TextView) rootView.findViewById(R.id.detail_wind_text);
        pressureView = (TextView) rootView.findViewById(R.id.detail_pressure_text);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if(shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(TAG, "Share Action Provider is not available");
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent createShareForecastIntent() {

        String location = Utility.getPreferredLocation(getActivity());
        String stringForSharing = String.format("Weather in %s: %s %s", location, forecastString, HASHTAG);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, stringForSharing);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this); //FIXME here Loader is NULL (reason is: see line 121)
    }

    void onLocationChanged(String newLocation) {
        if(uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            uri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "In onCreateLoader");

        if(uri != null) {
            return new CursorLoader(
                    getActivity(),
                    uri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "In onLoadFinished");
        if(data == null || !data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        friendlyDateView.setText(friendlyDateText);
        dateView.setText(dateText);

        String weatherDesc = data.getString(COL_WEATHER_DESC);
        descriptionView.setText(weatherDesc);

        //For accessibility, we adding description to icon field
        iconView.setContentDescription(weatherDesc);

        double highTemp = data.getDouble(COL_WEATHER_MAX_TEMP);
        String highTempString = Utility.formatTemperature(getActivity(), highTemp);
        highTempView.setText(highTempString);

        double lowTemp = data.getDouble(COL_WEATHER_MIN_TEMP);
        String lowTempString = Utility.formatTemperature(getActivity(), lowTemp);
        lowTempView.setText(lowTempString);

        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        humidityView.setText(getActivity().getString(R.string.formatted_humidity, humidity));

        float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
        float winDirStr = data.getFloat(COL_WEATHER_DEGREES);
        windView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, winDirStr));

        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        pressureView.setText(getActivity().getString(R.string.formatted_pressure, pressure));

        forecastString = String.format("%s - %s - %s/%s", dateText, weatherDesc, highTempString, lowTempString);

        //Updating Share indent after creating forecastString with actual weather data
        if(shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(TAG, "Share Action Provider is not available");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}