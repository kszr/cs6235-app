package edu.gatech.lost_and_found;

import android.os.Bundle;

/**
 * Created by abhishekchatterjee on 10/26/16.
 */
public class ProfileActivity extends CustomActionBarActivity {
    private final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
