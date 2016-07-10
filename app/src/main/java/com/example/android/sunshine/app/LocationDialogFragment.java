package com.example.android.sunshine.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Dialog with appears when clicked on location string
 */
public class LocationDialogFragment extends DialogFragment {

    private static final String TAG = LocationDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.location_dialog_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity mainActivity;
                try{
                    mainActivity = (MainActivity) getActivity();
                } catch(ClassCastException cce){
                    Log.w(TAG, "Activity is not MainActivity. Have no idea what to do.");
                    return;
                }

                switch(which) {
                    case 0: //show on map
                       mainActivity.openPreferredLocationInMap();
                        break;
                    case 1: //change location
                        mainActivity.openLocationActivity();
                        break;
                    default:
                        Log.wtf(TAG, "Got Selected option nr. " + which);
                }
            }
        });

        return builder.create();
    }
}
