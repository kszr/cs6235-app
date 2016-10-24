package edu.gatech.lost_and_found;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by abhishekchatterjee on 10/23/16.
 */
public class FoundActivity extends AppCompatActivity {
    private static final String TAG = "FoundActivity";
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_ACTIVITY = 0;

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
                Log.d(TAG, "Clicked camera icon.");
                startCameraActivity();
            }
        });

        Button lost_and_found = (Button) findViewById(R.id.lost_and_found_button);
        assert lost_and_found != null;
        lost_and_found.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Clicked 'Lost and Found'.");
            }
        });

        Button keep_object = (Button) findViewById(R.id.keep_object_button);
        assert keep_object != null;
        keep_object.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Clicked 'Keep Object'.");
            }
        });
    }

    private void startCameraActivity() {
        if(!(ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED)) {
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

    /**
     * Allows the user to view a thumbnail of the image that was just taken and
     * continue, retake the image, or cancel out accordingly.
     */
    private void promptImageOkay() {
        if(true) {

        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ImageButton cameraButton = (ImageButton) findViewById(R.id.button_camera);
            assert cameraButton != null;
            cameraButton.setClickable(false);

            TextView step2 = (TextView) findViewById(R.id.step2);
            assert step2 != null;
            step2.setVisibility(View.VISIBLE);

            TextView step2_text = (TextView) findViewById(R.id.step2_text);
            assert step2_text != null;
            step2_text.setVisibility(View.VISIBLE);

            Button lost_and_found = (Button) findViewById(R.id.lost_and_found_button);
            assert lost_and_found != null;
            lost_and_found.setVisibility(View.VISIBLE);

            Button keep_object = (Button) findViewById(R.id.keep_object_button);
            assert keep_object != null;
            keep_object.setVisibility(View.VISIBLE);
        }
    }
}
