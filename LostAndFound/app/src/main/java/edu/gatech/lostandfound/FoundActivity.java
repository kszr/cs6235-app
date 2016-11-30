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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    private Double lat = null;
    private Double lon = null;
    private boolean leaveObject = true;
    private Place place = null;
    private boolean onMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

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

                // Send data to server.
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
                        // TODO: Send data to server:
                        //      userid,
                        //      latlng,
                        //      photo (send file to server, but store filepath in db?),
                        //      leaveObject (true => chose to leave object where it is),
                        //      place (null if leaveObject is true; else place where user turned object in).
                        String userId = PreferenceManager.getDefaultSharedPreferences(FoundActivity.this).getString("userid", "NONE");
                        String latlng = lat + "," + lon;
                        String lvobj = String.valueOf(leaveObject);
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        if (dialog.isShowing()) {
                            dialog.setMessage(getString(R.string.submitted));
//                            try {
//                                wait(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            dialog.dismiss();
                        }

                        /**
                         * Ideally would like to erase the back stack after going back to HomePageActivity,
                         * but we overrode HPA's onBackPressed() method, which would preven the user
                         * from navigating back to this activity.
                         */

                        ((Activity) mContext).finish();
                    }
                }.execute();
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
            // Nothing
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
        Log.d(TAG,"latitude: " + (lat == null ? "null" : lat.toString()) + "; longitude: " + (lon == null ? "null" : lon.toString()));
    }

    private void saveImage(Bitmap bmp) {
        File mydir = this.getDir(MY_IMG_DIR, Context.MODE_PRIVATE); //Creating an internal dir.

        // TODO: Come up with unique filenames.
        String filename = "tah.png";
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
