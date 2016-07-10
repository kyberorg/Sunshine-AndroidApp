package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * {@link LocationAdapter} exposes a list of previously used locations
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class LocationAdapter extends CursorAdapter {

    private static final String TAG = LocationAdapter.class.getSimpleName();

    public LocationAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_location, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String location = cursor.getString(LocationFragment.COL_LOCATION_CITY);
        viewHolder.locationText.setText(location);

        viewHolder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openLocationInMap(location, context);
            }
        });
    }

    public static class ViewHolder {
        public final TextView locationText;
        public final ImageButton mapButton;

        public ViewHolder(View view) {
            locationText = (TextView) view.findViewById(R.id.location_item_city);
            mapButton = (ImageButton) view.findViewById(R.id.location_item_map_button);
        }
    }

}
