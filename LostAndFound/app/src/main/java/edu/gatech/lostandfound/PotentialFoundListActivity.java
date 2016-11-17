package edu.gatech.lostandfound;

import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by abhishekchatterjee on 11/17/16.
 */
public class PotentialFoundListActivity extends CustomActionBarActivity {
    private static final String TAG = "PotentialFoundListActivity";
    private Toast universalToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potential_found_list_activity);
    }
}
