package edu.gatech.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by abhishekchatterjee on 10/26/16.
 */
public class CustomActionBarActivity extends AppCompatActivity {
    private static final String TAG = "CustomActionBarActivity";
    private Toast universalToast;

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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("sign_out",false);
            startActivity(intent);
            return true;
        } else if (id == R.id.view_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.go_to_potential_list) {
            Intent intent = new Intent(this, PotentialFoundListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void makeToast(String text, int length) {
        if(universalToast != null)
            universalToast.cancel();
        universalToast = Toast.makeText(this, text, length);
        universalToast.show();
    }
}
