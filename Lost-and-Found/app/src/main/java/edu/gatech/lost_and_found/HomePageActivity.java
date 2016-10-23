package edu.gatech.lost_and_found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by abhishekchatterjee on 10/22/16.
 */
public class HomePageActivity extends AppCompatActivity {
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
        lostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        Button foundButton = (Button) findViewById(R.id.button_found);
        foundButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

}
