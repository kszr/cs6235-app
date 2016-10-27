package edu.gatech.lost_and_found;

import android.app.SearchManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.List;

import edu.gatech.lost_and_found.MainActivity;
import edu.gatech.lost_and_found.HomePageActivity;

/**
 * Created by abhishekchatterjee on 10/26/16.
 */
public class CustomActionBarActivity extends AppCompatActivity {
    private static final String TAG = "CustomActionBarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.home_page) {
            Intent intent = new Intent(this, HomePageActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.sign_out) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(TAG, "sign_out");
            startActivity(intent);
            return true;
        } else if (id == R.id.view_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
