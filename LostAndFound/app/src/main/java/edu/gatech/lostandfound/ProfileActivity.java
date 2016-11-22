package edu.gatech.lostandfound;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Created by abhishekchatterjee on 10/26/16.
 */
public class ProfileActivity extends CustomActionBarActivity {
    private final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GoogleSignInResult goog = Auth.GoogleSignInApi.getSignInResultFromIntent(getIntent());

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        String name = mPrefs.getString("displayname", "<NAME>");
        String email = mPrefs.getString("email", "<EMAIL@EMAIL.COM>");

        TextView nom = (TextView) findViewById(R.id.username);
        name = "<b>Name:</b> " + name;
        nom.setText(Html.fromHtml(name));

        TextView eom = (TextView) findViewById(R.id.email);
        email = "<b>Email:</b> " + email;
        eom.setText(Html.fromHtml(email));

        TextView pom = (TextView) findViewById(R.id.points);
        String points = "<b>Good Samaritan Points:</b> 0";
        pom.setText(Html.fromHtml(points));
    }
}
