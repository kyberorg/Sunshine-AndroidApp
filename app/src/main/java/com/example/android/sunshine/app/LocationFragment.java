package com.example.android.sunshine.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.android.sunshine.app.data.WeatherContract;


public class LocationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int LOCATION_LOADER = 1;
    //indexes
    static final int COL_LOCATION_ID = 0;
    static final int COL_LOCATION_CITY = 1;
    private static final String TAG = LocationFragment.class.getSimpleName();
    //columns
    private static final String[] LOCATION_COLUMNS = {
            WeatherContract.LocationEntry.TABLE_NAME + "." + WeatherContract.LocationEntry._ID ,
            WeatherContract.LocationEntry.COLUMN_CITY_NAME
    };

    private LocationAdapter locationAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        //Init EditText with current location, if any
        final EditText locationInput = (EditText) rootView.findViewById(R.id.location_input);
        String currentLocation = Utility.getPreferredLocation(getActivity());
        if(currentLocation != null && !currentLocation.isEmpty()) {

            locationInput.setText(currentLocation);
            //placing cursor to end
            locationInput.setSelection(locationInput.getText().length());
            locationInput.clearFocus();
        }

        //On button click
        Button updateButton = (Button) rootView.findViewById(R.id.location_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLocation = locationInput.getText().toString();
                Utility.setPreferredLocation(newLocation, getActivity());
                //we've done
                finishIt();
            }
        });

        //Populate list of recent
        ListView recentLocations = (ListView) rootView.findViewById(R.id.recent_locations_list);
        locationAdapter = new LocationAdapter(getActivity(), null, 0);
        recentLocations.setAdapter(locationAdapter);

        recentLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor != null) {
                    String selectedLocation = cursor.getString(COL_LOCATION_CITY);
                    Log.d(TAG, selectedLocation + " Selected");
                    //updating weather
                    Utility.setPreferredLocation(selectedLocation, getActivity());
                    //we've done
                    finishIt();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOCATION_LOADER, null, this).forceLoad();
        super.onActivityCreated(savedInstanceState);
    }

    private void finishIt() {
        getActivity().onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri locationsUri = WeatherContract.LocationEntry.CONTENT_URI;

        String sortOrder = WeatherContract.LocationEntry._ID + " DESC";

        //where not
        final String WHERE_CITY_NOT = WeatherContract.LocationEntry.COLUMN_CITY_NAME + " != ?";
        final String[] currentLocation = new String[]{Utility.getPreferredLocation(getActivity())};

        return new CursorLoader(getActivity(),
                locationsUri,
                LOCATION_COLUMNS,
                WHERE_CITY_NOT,
                currentLocation,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        locationAdapter.swapCursor(data);

        if(data != null && data.moveToFirst() && data.getCount() >= 1) {
            View recents = getActivity().findViewById(R.id.fragment_location_recents);
            recents.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        locationAdapter.swapCursor(null);
    }

}
