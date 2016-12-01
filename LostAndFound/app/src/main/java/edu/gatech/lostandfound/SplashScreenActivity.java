package edu.gatech.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by abhishekchatterjee on 10/25/16.
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        final ImageView iv = (ImageView) findViewById(R.id.splash_screen);

        Thread launching = new Thread() {
            public void run() {
                try {
                    sleep(2000);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        launching.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
