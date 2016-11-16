package edu.gatech.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

/**
 * Created by abhishekchatterjee on 10/23/16.
 */
public class LostActivity extends CustomActionBarActivity implements OnConnectionFailedListener {
    private static final String TAG = "LostActivity";
    private static final int PLACE_PICKER_REQUEST = 1;

    private GoogleApiClient mGoogleApiClient;
    private boolean confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        startPlacePicker();

        final Button buttonGoback = (Button) findViewById(R.id.button_goback);
        assert buttonGoback != null;
        buttonGoback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Go Back'.");
                startPlacePicker();
            }
        });

        final Button buttonConf = (Button) findViewById(R.id.button_confirm);
        assert buttonConf != null;
        buttonConf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Confirm'.");
                confirm = true;
                buttonGoback.setClickable(false);
                buttonConf.setClickable(false);
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            Log.d(TAG,"place: " + place.getName());

            String sourceText = "Confirm object lost at <b>" + place.getName() + "</b>?";
            TextView confText = (TextView) findViewById(R.id.lost_confirm_text);
            assert confText != null;
            confText.setVisibility(View.VISIBLE);
            confText.setText(Html.fromHtml(sourceText));

            Button buttonConf = (Button) findViewById(R.id.button_confirm);
            assert buttonConf != null;
            buttonConf.setVisibility(View.VISIBLE);

            Button buttonGoback = (Button) findViewById(R.id.button_goback);
            assert buttonGoback != null;
            buttonGoback.setVisibility(View.VISIBLE);
        }
    }

    private void startPlacePicker() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
