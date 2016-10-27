package edu.gatech.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by abhishekchatterjee on 10/22/16.
 */
public class HomePageActivity extends CustomActionBarActivity {
    private static final String TAG = "HomePageActivity";
    private static final int LOST_ACTIVITY = 0;
    private static final int FOUND_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpButtonListeners();
        setUpHandler();
    }

    private void setUpHandler() {
    }

    private void setUpButtonListeners() {
        Button lostButton = (Button) findViewById(R.id.button_lost);
        assert lostButton != null;
        lostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG,"Clicked 'Lost Activity'.");
                Intent intent = new Intent(HomePageActivity.this, LostActivity.class);
                startActivityForResult(intent, LOST_ACTIVITY);
            }
        });

        Button foundButton = (Button) findViewById(R.id.button_found);
        assert foundButton != null;
        foundButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG,"Clicked 'Found Activity'.");
                Intent intent = new Intent(HomePageActivity.this, FoundActivity.class);
                startActivityForResult(intent, FOUND_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LOST_ACTIVITY && resultCode == Activity.RESULT_OK) {

        } else if(requestCode == FOUND_ACTIVITY && resultCode == Activity.RESULT_OK) {

        }
    }

    @Override
    public void onBackPressed() {

    }
}
