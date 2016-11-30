package edu.gatech.lostandfound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Date;

/**
 * Created by abhishekchatterjee on 10/23/16.
 */
public class LostActivity extends CustomActionBarActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LostActivity";
    private static final int PLACE_PICKER_REQUEST = 0;

    private Context mContext = this;
    private boolean onMap = false;
    private boolean visible = false;
    private Place selectedPlace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        new GoogleApiClient
            .Builder(this)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .enableAutoManage(this, this)
            .build();

        startPlacePicker();

        final Button buttonGoback = (Button) findViewById(R.id.button_goback);
        assert buttonGoback != null;
        buttonGoback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Go Back'.");
                toggleVisible();
                startPlacePicker();
            }
        });

        final Button buttonConf = (Button) findViewById(R.id.button_confirm);
        assert buttonConf != null;
        buttonConf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Confirm'.");
                buttonGoback.setClickable(false);
                buttonConf.setClickable(false);
                reportLostLocation();
            }
        });
    }

    private void reportLostLocation() {
        Log.d(TAG,"reportLostLocation: selectedPlace == null? " + (selectedPlace==null));
        if(selectedPlace == null) {
            Log.i(TAG,"Nothing to send to server.");
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (dialog == null) {
                    dialog = new ProgressDialog(mContext);
                    dialog.setMessage(getString(R.string.submitting));
                    dialog.setIndeterminate(true);
                }
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                // TODO: Send data to server here:
                //       userid,
                //       latlon,
                //       date.
                String userId = PreferenceManager.getDefaultSharedPreferences(LostActivity.this).getString("userid","NONE");
                String placename = selectedPlace.getName().toString();
                String latlon = selectedPlace.getLatLng().latitude+","+selectedPlace.getLatLng().longitude;
                String date = new Date().toString();

                Log.d(TAG,"reportLostLocation(): " + userId +
                        ",(" + latlon +
                        ")," + date);

                return null;
            }

            protected void onPostExecute(Void result) {
                if (dialog.isShowing()) {
                    dialog.setMessage(getString(R.string.submitted));
//                    try {
//                        wait(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    dialog.dismiss();
                }

                ((Activity) mContext).finish();
            }
        }.execute();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            Log.d(TAG, "place: " + place.getName());

            String sourceText = "Confirm object lost at <b>" + place.getName() + "</b>?";
            TextView confText = (TextView) findViewById(R.id.lost_confirm_text);
            assert confText != null;
            confText.setText(Html.fromHtml(sourceText));

            selectedPlace = place;

            toggleVisible();

            onMap = false;
        }
    }

    private void toggleVisible() {
        TextView confText = (TextView) findViewById(R.id.lost_confirm_text);
        Button buttonConf = (Button) findViewById(R.id.button_confirm);
        Button buttonGoback = (Button) findViewById(R.id.button_goback);

        if(!visible) {
            assert confText != null;
            confText.setVisibility(View.VISIBLE);

            assert buttonConf != null;
            buttonConf.setVisibility(View.VISIBLE);

            assert buttonGoback != null;
            buttonGoback.setVisibility(View.VISIBLE);

            visible = true;
        } else {
            assert confText != null;
            confText.setVisibility(View.GONE);

            assert buttonConf != null;
            buttonConf.setVisibility(View.GONE);

            assert buttonGoback != null;
            buttonGoback.setVisibility(View.GONE);

            visible = false;
        }
    }

    private void startPlacePicker() {
        onMap = true;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(onMap) {
            ((Activity) mContext).finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            if(onMap)
                onBackPressed();

        return super.onKeyDown(keyCode, event);
    }
}
