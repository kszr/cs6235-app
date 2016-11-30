package edu.gatech.lostandfound;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.sql.SQLException;
import java.util.List;

import edu.gatech.lostandfound.database.ReportedFoundObject;
import edu.gatech.lostandfound.database.ReportedFoundObjectDataSource;

/**
 * Created by abhishekchatterjee on 10/26/16.
 */
public class ProfileActivity extends CustomActionBarActivity {
    private final String TAG = "ProfileActivity";
    private ReportedFoundObjectDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GoogleSignInResult goog = Auth.GoogleSignInApi.getSignInResultFromIntent(getIntent());

        dataSource = new ReportedFoundObjectDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        int pts = getGoodSamaritanPoints();
        String points = "<b>Good Samaritan Points:</b> " + pts;
        pom.setText(Html.fromHtml(points));
    }

    private int getGoodSamaritanPoints() {
        List<ReportedFoundObject> list = dataSource.getAllObjects();
        int pts = 0;

        for(ReportedFoundObject obj : list) {
            int base = obj.isClaimed() ? 1 : 0;
            int factor = obj.isTurnedIn() ? 5 : 1;
            pts += base*factor;
        }

        return pts;
    }

    @Override
    protected void onResume() {
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public void finish() {
        dataSource.close();

        super.finish();
    }
}
