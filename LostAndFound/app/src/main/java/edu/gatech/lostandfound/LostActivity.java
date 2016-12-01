package edu.gatech.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.Header;
import edu.gatech.lostandfound.database.ReportedLostObjectDataSource;
import edu.gatech.lostandfound.util.HttpUtil;

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

    // db column entries, etc.
    private String loid = "";
    private String date = "";
    private String latlon = "";

    private ReportedLostObjectDataSource dataSource = new ReportedLostObjectDataSource(this);
    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
                reportLostObject();
                finish();
            }
        });
    }

    private void reportLostObject() {
        Log.d(TAG, "reportLostObject: selectedPlace == null? " + (selectedPlace == null));
        if (selectedPlace == null) {
            Log.i(TAG, "Nothing to send to server.");
            return;
        }

        latlon = selectedPlace.getLatLng().latitude + ":" + selectedPlace.getLatLng().longitude;
        date = new Date().toString();

        sendDataToServer();
    }

    private void sendDataToServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", PreferenceManager
                    .getDefaultSharedPreferences(LostActivity.this)
                    .getString("userid", "NONE"));
//            jsonObject.put("userid","abhishek"); // TODO: Replace constant.
            jsonObject.put("latlon", latlon);
            jsonObject.put("date", date);
            jsonObject.put("description", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Attempting to send reported lost object info to server.");
        client.post(mContext,
                HttpUtil.REPORT_LOST_OBJECT_ENDPOINT,
                entity,
                "application/json",
//                new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
//                        try {
//                            Log.i(TAG, "Report lost object success");
//                            loid = json.getString("objid");
////                            String result = json.getString("Result");
//                            makeToast("Reported lost object", Toast.LENGTH_LONG);
//                            Log.i(TAG, json.toString());
//                            dataSource.createObject(loid,
//                                    new Date(date),
//                                    selectedPlace.getLatLng(),
//                                    false);
//                        } catch (JSONException e) {
//                            makeToast("Error reporting lost object", Toast.LENGTH_LONG);
//                            Log.d(TAG, json.toString());
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable t) {
//                        Log.e(TAG, "Report lost object failed: status: " + statusCode);
//                        Log.e(TAG, "Response string: " + responseString);
//                        Log.e(TAG, t.toString());
//                        makeToast("Error reporting lost object", Toast.LENGTH_LONG);
//                    }
//                });
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, "Report lost object failed: status: " + statusCode);
                        Log.e(TAG, "Response string: " + responseString);
                        Log.e(TAG, throwable.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Log.i(TAG, "Report lost object success");
                            Log.i(TAG, responseString);
//                            loid = json.getString("objid");
////                            String result = json.getString("Result");
//                            makeToast("Reported lost object", Toast.LENGTH_LONG);
//                            Log.i(TAG, json.toString());
//                            dataSource.createObject(loid,
//                                    new Date(date),
//                                    selectedPlace.getLatLng(),
//                                    false);
                        } catch (Exception e) {
                            makeToast("Error reporting lost object", Toast.LENGTH_LONG);
//                            Log.d(TAG, json.toString());
                            e.printStackTrace();
                        }
                    }
                }
        );
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
    protected void onResume() {
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public void finish() {
        dataSource.close();

        super.finish();
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
