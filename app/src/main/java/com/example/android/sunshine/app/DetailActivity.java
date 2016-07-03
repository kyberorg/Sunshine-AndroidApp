package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.example.android.sunshine.app.data.WeatherContract;

import static com.example.android.sunshine.app.R.id.container;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String TAG = DetailFragment.class.getSimpleName();

        private static final String HASHTAG = " #WeatherApp";

        private static String forecastString;

        private static final int DETAIL_LOADER = 0;

        private static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID ,
                WeatherContract.WeatherEntry.COLUMN_DATE ,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC ,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP ,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP ,
        };

        private static final int COL_WEATHER_ID = 0;
        private static final int COL_WEATHER_DATE = 1;
        private static final int COL_WEATHER_DESC = 2;
        private static final int COL_WEATHER_MAX_TEMP = 3;
        private static final int COL_WEATHER_MIN_TEMP = 4;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if(intent != null) {
                forecastString = intent.getDataString();
            }

            if(forecastString != null) {
                TextView detailsText = (TextView) rootView.findViewById(R.id.detail_text);
                detailsText.setText(forecastString);
            }

            getLoaderManager().initLoader(DETAIL_LOADER, null, this).forceLoad();

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail_fragment, menu);

            MenuItem shareItem = menu.findItem(R.id.action_share);

            ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

            if(shareActionProvider != null && forecastString != null) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(TAG, "Share Action Provider is not available");
            }

            super.onCreateOptionsMenu(menu, inflater);
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + HASHTAG);
            return shareIntent;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if(intent == null) {
                return null;
            }
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(TAG, "In onLoadFinished");
            if(!data.moveToFirst()) {
                return;
            }

            String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
            String weatherDesc = data.getString(COL_WEATHER_DESC);

            String high = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP));
            String low = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP));

            forecastString = String.format("%s - %s - %s/%s", dateString, weatherDesc, high, low);

            TextView detailTextView = (TextView) getView().findViewById(R.id.detail_text);
            detailTextView.setText(forecastString);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
