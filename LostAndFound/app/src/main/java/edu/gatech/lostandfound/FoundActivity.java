package edu.gatech.lostandfound;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import edu.gatech.lostandfound.database.ReportedFoundObjectDataSource;
import edu.gatech.lostandfound.util.HttpUtil;

import static edu.gatech.lostandfound.util.ImageUtil.saveImage;

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
    private boolean flag = true;
    private ProgressDialog mProgressDialog;

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

                Button submit = (Button) findViewById(R.id.submit_found_button);
                assert submit != null;
                submit.setVisibility(View.GONE);

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

                sendPictureToServer();

            }
        });
    }

    private void sendDataToServer() {
        showProgressDialog("Sending report to server…");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", PreferenceManager
                    .getDefaultSharedPreferences(FoundActivity.this)
                    .getString("userid", "NONE"));
//            jsonObject.put("userid","amit");
            jsonObject.put("latlon",lat+":"+lon);
            jsonObject.put("date", new Date().toString());
            jsonObject.put("description", "");
            jsonObject.put("leaveObject",leaveObject ? "1" : "0");
            jsonObject.put("placename",placeName);
            jsonObject.put("filename",filename);
            jsonObject.put("latlon2",latlon2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, jsonObject.toString());
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
                            Log.d(TAG, "Report found object success: " + statusCode);

                            if(json.getString("Result").equals("Failure"))
                                throw new Exception("Result: Failure");

                            foid = json.getString("objid");

                            makeToast("Reported found object", Toast.LENGTH_LONG);
                            Log.i(TAG, json.toString());
                            dataSource.createObject(foid,
                                    new Date(date),
                                    new LatLng(lat, lon),
                                    leaveObject,
                                    latlon2.equals("0:0") ? new LatLng(Double.valueOf(latlon2.split(":")[0]),
                                            Double.valueOf(latlon2.split(":")[1])) : new LatLng(0, 0),
                                    "",
                                    filename,
                                    false);
                            flag = false;
                            ((Activity) mContext).finish();
                        } catch (Exception e) {
                            makeToast("Error reporting found object", Toast.LENGTH_LONG);
                            flag = false;
                            Log.d(TAG, json.toString());
                            e.printStackTrace();
                            ((Activity) mContext).finish();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable t) {
                        Log.e(TAG, "Report found object failed: status: " + statusCode);
                        Log.e(TAG, "Response string: " + responseString);
                        Log.e(TAG, t.toString());
                        makeToast("Error reporting found object", Toast.LENGTH_LONG);
                        flag = false;
                        ((Activity) mContext).finish();
                    }
                });

        hideProgressDialog();
    }

    private void sendPictureToServer() {
        // TODO: Fix file error.

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        File mydir = this.getDir(MY_IMG_DIR, Context.MODE_PRIVATE); //Creating an internal dir.
        File fileWithinMyDir = new File(mydir, filename); //Getting a file within the dir.
        Log.d(TAG,"filename in sendPTS: " + filename);
        Log.d(TAG, "filename: " + fileWithinMyDir.getAbsolutePath());
        StringBody stringBody = null;
        String boundary =  "*****";

        showProgressDialog("Uploading image");

        try {
            stringBody = new StringBody("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        builder.addPart("description", stringBody);

        FileBody fileBody = new FileBody(fileWithinMyDir); //image should be a String
        builder.addPart("found_object_image", fileBody);

        HttpEntity entity = builder.build();

        RequestParams params = new RequestParams();
        try {
            //"photos" is Name of the field to identify file on server
            params.put("description", "");
            params.put("found_object_image", fileWithinMyDir);
        } catch (FileNotFoundException e) {
            //TODO: Handle error
            Log.e(TAG, "FNFException");
            e.printStackTrace();
        }

        Log.i(TAG,"Attempting to upload image to server");
        client.post(this,
                HttpUtil.SEND_IMAGE_ENDPOINT,
                params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        try {
                            Log.i(TAG, "Upload found object image success");
                            makeToast("Uploaded image to server", Toast.LENGTH_LONG);
                            Log.i(TAG, json.toString());
//                            String bob[] = json.getString("Filename:").split("/");
                            filename = json.getString("Filename:");
                            Log.d(TAG, "Filename: " + filename);
                            sendDataToServer();
                        } catch (Exception e) {
                            makeToast("Error uploading image", Toast.LENGTH_LONG);
                            flag = false;
                            Log.d(TAG, json.toString());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable t) {
                        Log.e(TAG, "Upload found object image failed: status: " + statusCode);
                        Log.e(TAG, "Response string: " + responseString);
                        Log.e(TAG, t.toString());
                        flag = false;
                        makeToast("Error uploading image", Toast.LENGTH_LONG);
                    }
                });
        hideProgressDialog();
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
        saveImage(this,photo,MY_IMG_DIR,filename=getRandomString()+".png"); // We don't really care about filename.
        Log.d(TAG, "latitude: " + (lat == null ? "null" : lat.toString()) + "; longitude: " + (lon == null ? "null" : lon.toString()));
    }

    protected String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    private void showProgressDialog(String msg) {
        Log.i(TAG, "Showing progress dialog.");
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }

        Log.i(TAG, "Hiding progress dialog");
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
