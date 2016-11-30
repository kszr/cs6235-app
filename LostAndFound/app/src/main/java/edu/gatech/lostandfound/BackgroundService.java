package edu.gatech.lostandfound;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */

public class BackgroundService extends IntentService {
    private static final String TAG = "BackgroundService";
    private static final String SEND_USER_INFO = "";
    private static final String REPORT_LOST_OBJECT = "";
    private static final String REPORT_FOUND_OJECT = "";
    private static final String SEND_IMAGE = "";

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

    public void callService(int work) {

    }

    public String sendDataToServer(String addr, String data) {
        return null;
    }

    public void sendImageToServer(Bitmap bmp, String filename) {

    }

    public void updatePotentialFoundObjects() {

        notifyUser();
    }

    private void notifyUser() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        Intent resultIntent = new Intent(this, PotentialFoundListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PotentialFoundListActivity.class);
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
