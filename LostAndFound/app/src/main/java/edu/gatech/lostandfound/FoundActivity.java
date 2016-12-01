package edu.gatech.lostandfound;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import edu.gatech.lostandfound.database.ReportedFoundObjectDataSource;
import edu.gatech.lostandfound.util.HttpUtil;

/**
 * Created by abhishekchatterjee on 10/23/16.
 */
public class FoundActivity extends CustomActionBarActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "FoundActivity";
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_ACTIVITY = 0;
    private static final int FOUND_AND_TURN_IN = 1;
    private static final int FOUND_AND_LEAVE = 2;
    private static final int SUBMIT = 3;
    private static final String MY_IMG_DIR = "myn"; // Directory in internal storage for images taken by me.

    private Context mContext = this;
    private Bitmap photo = null;
    private String foid = "";
    private String date = "";
    private Double lat = null;
    private Double lon = null;
    private String filename = "";
    private boolean leaveObject = true;
    private String latlon2 = "0:0";
    private Place place = null;
    private String placeName = "";
    private boolean onMap = false;

    private AsyncHttpClient client = new AsyncHttpClient();
    private ReportedFoundObjectDataSource dataSource = new ReportedFoundObjectDataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

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

        setUpButtons();
    }

    private void setUpButtons() {
        ImageButton cameraButton = (ImageButton) findViewById(R.id.button_camera);
        assert cameraButton != null;
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked camera icon.");
                startCameraActivity();
            }
        });

        Button lost_and_found = (Button) findViewById(R.id.turn_in_object_button);
        assert lost_and_found != null;
        lost_and_found.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Lost and Found'.");
                leaveObject = false;
                Intent intent = new Intent(FoundActivity.this, FoundAndTurnInActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("filename",filename);

                startActivityForResult(intent, FOUND_AND_TURN_IN);
            }
        });

        Button keep_object = (Button) findViewById(R.id.leave_object_button);
        assert keep_object != null;
        keep_object.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Leave Object'.");
                leaveObject = true;
                Button submit_button = (Button) findViewById(R.id.submit_found_button);
                assert submit_button != null;
                submit_button.setVisibility(View.VISIBLE);
            }

        });

        Button submit_button = (Button) findViewById(R.id.submit_found_button);
        assert submit_button != null;
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Submit'.");

                date = new Date().toString();

                sendDataToServer();
            }
        });
    }

    private void sendDataToServer() {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("userid", PreferenceManager
//                    .getDefaultSharedPreferences(FoundActivity.this)
//                    .getString("userid", "NONE"));
            jsonObject.put("userid","abhishek");
            jsonObject.put("latlon", lat+":"+lon);
            jsonObject.put("date", new Date().toString());
            jsonObject.put("description", "");
            jsonObject.put("leaveObject",leaveObject ? "1" : "0");
            jsonObject.put("placename",placeName);
            jsonObject.put("latlon2",latlon2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Attempting to send reported found object info to server.");
        client.post(mContext,
                HttpUtil.REPORT_FOUND_OBJECT_ENDPOINT,
                entity,
                "application/json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        try {
                            Log.i(TAG, "Report found object success");
                            foid = json.getString("objid");
//                            String result = json.getString("Result");
                            makeToast("Reported found object", Toast.LENGTH_LONG);
                            Log.i(TAG, json.toString());
                            dataSource.createObject(foid,
                                    new Date(date),
                                    new LatLng(lat,lon),
                                    leaveObject,
                                    latlon2.equals(":") ? new LatLng(Double.valueOf(latlon2.split(":")[0]),
                                                                Double.valueOf(latlon2.split(":")[1])) : new LatLng(0,0),
                                    "",
                                    filename,
                                    false);
                        } catch (JSONException e) {
                            makeToast("Error reporting found object", Toast.LENGTH_LONG);
                            Log.d(TAG, json.toString());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable t) {
                        Log.e(TAG, "Report found object failed: status: " + statusCode);
                        Log.e(TAG, "Response string: " + responseString);
                        Log.e(TAG, t.toString());
                        makeToast("Error reporting found object", Toast.LENGTH_LONG);
                    }
                });

        // TODO: Send picture to server.
    }

    private void startCameraActivity() {
        if (!(ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{CAMERA_PERMISSION},
                1
            );
            return;
        }

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_ACTIVITY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK) {
            getImageData(data);

            ImageButton cameraButton = (ImageButton) findViewById(R.id.button_camera);
            assert cameraButton != null;
            cameraButton.setClickable(false);

            TextView step2 = (TextView) findViewById(R.id.step2);
            assert step2 != null;
            step2.setVisibility(View.VISIBLE);

            TextView step2_text = (TextView) findViewById(R.id.step2_text);
            assert step2_text != null;
            step2_text.setVisibility(View.VISIBLE);

            Button lost_and_found = (Button) findViewById(R.id.turn_in_object_button);
            assert lost_and_found != null;
            lost_and_found.setVisibility(View.VISIBLE);

            Button keep_object = (Button) findViewById(R.id.leave_object_button);
            assert keep_object != null;
            keep_object.setVisibility(View.VISIBLE);
        } else if(requestCode == FOUND_AND_TURN_IN && resultCode == Activity.RESULT_OK) {
            leaveObject = false;

            Button submit_button = (Button) findViewById(R.id.submit_found_button);
            assert submit_button != null;
            submit_button.setVisibility(View.VISIBLE);

            Button leave_button = (Button) findViewById(R.id.leave_object_button);
            assert leave_button != null;
            leave_button.setClickable(false);

            Button turn_in_button = (Button) findViewById(R.id.turn_in_object_button);
            assert turn_in_button != null;
            turn_in_button.setClickable(false);

            latlon2 = data.getStringExtra("latlon2");
            placeName = data.getStringExtra("placeName");
        }
    }

    private void getImageData(Intent data) {
        photo = (Bitmap) data.getExtras().get("data");

        Log.i(TAG, "Captured image.");

        /**
         * Android does not support Exif information on byte streams, and lat/lon information
         * can only be obtained from image files. As a result, we use the LocationManager
         * to get location data.
         */
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null)
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Log.d(TAG, "Location null: " + (location == null));

        if(location == null) {
            lat = null;
            lon = null;
        } else {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
        saveImage(photo);
        Log.d(TAG, "latitude: " + (lat == null ? "null" : lat.toString()) + "; longitude: " + (lon == null ? "null" : lon.toString()));
    }

    private void saveImage(Bitmap bmp) {
        File mydir = this.getDir(MY_IMG_DIR, Context.MODE_PRIVATE); //Creating an internal dir.

        // TODO: Come up with unique filenames.
        filename = "tah.png";
        File fileWithinMyDir = new File(mydir, filename); //Getting a file within the dir.
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileWithinMyDir);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                Log.d(TAG,"Saved image: " + MY_IMG_DIR + "/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            onBackPressed();

        return super.onKeyDown(keyCode, event);
    }
}
