package edu.gatech.lostandfound.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import edu.gatech.lostandfound.PotentialFoundListActivity;
import edu.gatech.lostandfound.R;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */

public class BackgroundService extends IntentService {
    private static final String TAG = "BackgroundService";
    private static final String BASE_URL = "http://73.82.210.153:8080/lost_and_found";

    public static final String REGISTER_USER_ENDPOINT = "";
    public static final String REPORT_LOST_OBJECT_ENDPOINT = BASE_URL + "/report_lost_object";
    public static final String REPORT_FOUND_OBJECT_ENDPOINT = "";
    public static final String CLAIM_OBJECT_ENDPOINT = "";
    public static final String SEND_IMAGE_ENDPOINT = "";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private Context mContext = null;

    public class BackgroundTask extends AsyncTask<String, String, Void> {
        Context mContext = null;

        public BackgroundTask(Context context) {
            mContext = context;
        }

        protected void onPreExecute() {

        }

        protected Void doInBackground(final String... args) {

            return null;
        }

        protected void onPostExecute(final Void unused) {

        }
    }

    public BackgroundService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BackgroundService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mContext = getBaseContext();
//        new BackgroundTask(mContext).execute();
    }

    public String sendDataToServer(String data, String endpoint) {
        return null;
    }

    public void sendImageToServer(Bitmap bmp, String filename) {

    }

    public void updatePotentialFoundObjects() {

        notifyUser("title","text",PotentialFoundListActivity.class);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notifyUser(String title, String text, Class<?> activityClass) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_icon)
                        .setContentTitle(title)
                        .setContentText(text);

        Intent resultIntent = new Intent(this, activityClass);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(activityClass);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(mId, mBuilder.build());
    }
}
