package edu.gatech.lostandfound;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int HOME_PAGE_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    private static String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        verifyPermissions();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        assert signInButton != null;
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Sign-in button was clicked.");
                showProgressDialog();
                signIn();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = getIntent();

        boolean signed_in = intent.getBooleanExtra("sign_out", true); // False, if the user clicked "sign out" in the menu.
        boolean signed_in_default = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("signed_in",false); // False, if the user was signed out when the app was last closed.

        if(signed_in && signed_in_default) {
            cachedSignIn();
        } else {
            // Since the user signed out, make sure that this preference is remembered the next time the app is opened.
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor.putBoolean("signed_in",false);
            editor.apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                Log.i(TAG, "Signed in: Going to Home Page");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("displayname",result.getSignInAccount().getDisplayName());
                editor.putString("userid",result.getSignInAccount().getId());
                editor.putString("email",result.getSignInAccount().getEmail());
                editor.apply();

                registerUser(result.getSignInAccount());
                hideProgressDialog();
                handleSignInResult(result);
            }
        }
    }

    /**
     * Registering a new user with the server. This involves sending user information
     * to the server whenever the user explicitly signs in (as opposed to using cached sign-in).
     * The server then decides whether the user is a new user or an existing user. The justification
     * for doing it this way is twofold:
     *      1. A user's registration status should not be tied to the app, because an existing user
     *      of the app can sign into the app from a different device or after having erased the app's
     *      local storage.
     *      2. Inasmuch as a cached sign in is used in most cases and a user has to explicitly sign out
     *      and then sign in in order for this method to be called, the overhead associated with
     *      a large user base sending user information to the server on sign-in should not be too high.
     * @param account
     */
    private void registerUser(final GoogleSignInAccount account) {
        Log.i(TAG,"registerUser: Sending user information to the server.");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String id = account.getId();
                String name = account.getDisplayName();
                String email = account.getEmail();

                Log.d(TAG,"registerUser(): " + id +
                        "," + name +
                        "," + email);

                // TODO: Send the above information to the server.
                return null;
            }
        }.execute();
    }

    private void cachedSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.i(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            assert signInButton != null;
            signInButton.setVisibility(View.GONE);
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            startActivityForResult(intent, HOME_PAGE_CODE);
        } else {
            Log.e(TAG,"handleSignInResult: Could not able to cache sign in.");
            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            assert signInButton != null;
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void signIn() {
        // Remember to stay signed in the next time the app is opened.
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putBoolean("signed_in",true);
        editor.apply();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.signing_in));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void verifyPermissions() {
        if(!allPermissionsGranted()) {
            for(String permission : PERMISSIONS) {
                if(!(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(
                        this,
                        new String[]{permission},
                        1
                    );
                }
            }
        }
    }

    private boolean allPermissionsGranted() {
        for(String permission : PERMISSIONS)
            if(!(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED))
                return false;
        return true;
    }
}
