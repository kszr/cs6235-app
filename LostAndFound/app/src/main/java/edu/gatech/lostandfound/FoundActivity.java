package edu.gatech.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by abhishekchatterjee on 10/23/16.
 */
public class FoundActivity extends CustomActionBarActivity {
    private static final String TAG = "FoundActivity";
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_ACTIVITY = 0;
    private static final int FOUND_AND_TURN_IN = 1;
    private static final int FOUND_AND_LEAVE = 2;
    private static final int SUBMIT = 3;

    private Bitmap photo = null;
    private Double lat = null;
    private Double lon = null;
    private boolean leaveObject = false;

    private Toast universalToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
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
                Intent intent = new Intent(FoundActivity.this, FoundAndTurnInActivity.class);
                startActivityForResult(intent, FOUND_AND_TURN_IN);
            }
        });

        Button keep_object = (Button) findViewById(R.id.leave_object_button);
        assert keep_object != null;
        keep_object.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked 'Leave Object'.");

                Button submit_button = (Button) findViewById(R.id.submit_found_button);
                assert submit_button != null;
                submit_button.setVisibility(View.VISIBLE);
            }

        });
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

        } else if(requestCode == FOUND_AND_LEAVE && resultCode == Activity.RESULT_OK) {

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

        Log.d(TAG,"Latitude: " + (lat == null ? "null" : lat.toString()) + "; Longitude: " + (lon == null ? "null" : lon.toString()));
    }

    private void makeToast(String text, int length) {
        if(universalToast != null)
            universalToast.cancel();
        universalToast = Toast.makeText(this, text, length);
        universalToast.show();
    }

}
