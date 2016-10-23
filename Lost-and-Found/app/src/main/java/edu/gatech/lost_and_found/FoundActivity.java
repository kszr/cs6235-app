package edu.gatech.lost_and_found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by abhishekchatterjee on 10/23/16.
 */
public class FoundActivity extends AppCompatActivity {
    private static final String TAG = "FoundActivity";
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
                Log.d(TAG, "Clicked 'Lost Activity'.");
                Intent intent = new Intent(FoundActivity.this, LostActivity.class);
                startActivityForResult(intent, CAMERA_ACTIVITY);
            }
        });
    }
}
