package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.example.android.sunshine.app.data.WeatherContract;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "DetailFragment";

    private String location;
    private boolean twoPane = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if(findViewById(R.id.weather_detail_container) != null) {
            //Tablets mode
            twoPane = true;

            Bundle args = null;

            //If we already have record for today, let's init DetailFragment with URI for today's weather
            Uri uriForToday = getUriForToday();
            if(uriForToday != null){
                args = new Bundle();
                args.putParcelable(DetailFragment.DETAIL_URI, uriForToday);
            }

            DetailFragment fragment = new DetailFragment();
            if(args != null) {
                fragment.setArguments(args);
            }

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, fragment)
                        .commit();
            }
        } else {
            //Phone mode
            twoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));

        forecastFragment.setUseTodayLayout(!twoPane);
    }

    @Override
    protected void onResume() {
        String currentLocation = Utility.getPreferredLocation(this);
        if(currentLocation !=null && ! location.equals(currentLocation)){
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(ff != null) {
                ff.onLocationChange();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if(df != null) {
                df.onLocationChanged(location);
            }

            location = currentLocation;
        }

        //Update location in main screen
        TextView locationTitle = (TextView) findViewById(R.id.location_title);
        locationTitle.setText(location);

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }

        if(id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);
        Utility.openLocationInMap(location, this);
    }

    public void openLocationActivity() {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    @Nullable
    private Uri getUriForToday(){
        //if we have today item in list are passing Uri for today to fragment
        Fragment forecastFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast);

        if(forecastFragment != null) {
            long today = System.currentTimeMillis();
            Uri allWeatherForLocation = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location,today);
            String sortByDateOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
            Cursor c = getContentResolver().query(allWeatherForLocation, null, null, null, sortByDateOrder);

            if(c != null && c.moveToFirst()) {
                int dateIdx = c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
                long date = c.getLong(dateIdx);
                return WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, date);
            }
        }

        return null;
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if(twoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dateUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(dateUri);
            startActivity(intent);
        }
    }
}
