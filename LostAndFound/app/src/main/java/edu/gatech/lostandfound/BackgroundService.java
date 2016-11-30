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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */

public class BackgroundService extends IntentService {
    private static final String TAG = "BackgroundService";

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

    public String sendDataToServer(String data) {
        return null;
    }
}
