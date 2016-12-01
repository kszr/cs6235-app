package edu.gatech.lostandfound.runnable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import edu.gatech.lostandfound.database.PotentialFoundDataSource;
import edu.gatech.lostandfound.database.PotentialFoundObject;
import edu.gatech.lostandfound.util.HttpUtil;

import static edu.gatech.lostandfound.util.ImageUtil.saveImage;

/**
 * Created by abhishekchatterjee on 12/1/16.
 */
public class PotentialFoundRunnable implements Runnable {
    private static final String IMG_DIR_OTH = "oth";
    private static final String TAG = "PotentialFoundRunnable";

    private Handler handler;
    private int period;
    private Context mContext;
    private PotentialFoundDataSource dataSource;
    private AsyncHttpClient client;

    public PotentialFoundRunnable(Handler handler, int period, Context context) {
        this.handler = handler;
        this.period = period;
        this.mContext = context;
    }

    @Override
    public void run() {
        Log.d(TAG, "Called on main thread");

        dataSource = new PotentialFoundDataSource(mContext);

        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getPotentialFoundList();
        updateImages();

        dataSource.close();

        handler.postDelayed(this, period);
    }

    private void getPotentialFoundList() {
        client = new AsyncHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", PreferenceManager
                    .getDefaultSharedPreferences(mContext)
                    .getString("userid", "NONE"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Attempting to get potential found list from server.");
        client.get(mContext,
                HttpUtil.GET_POTENTIAL_FOUND_OBJECTS_ENDPOINT,
                entity,
                "application/json",
                new JsonHttpResponseHandler() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        try {
                            Log.i(TAG, "Report lost object success");
                            JSONArray names = json.names();
                            JSONArray jsonArray = json.toJSONArray(names);
                            JSONArray objArray = jsonArray.getJSONArray(0);
                            Log.i(TAG, objArray.toString());
                            updateDBEntries(objArray);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable t) {
                        Log.e(TAG, "Report lost object failed: status: " + statusCode);
                        Log.e(TAG, "Response string: " + responseString);
                        Log.e(TAG, t.toString());
                    }
                });
    }

    private void updateDBEntries(JSONArray jsonArray) {
        // TODO: Make this efficient.
        List<PotentialFoundObject> list = dataSource.getAllObjects();
        for(PotentialFoundObject obj : list) {
            dataSource.deleteObject(obj);
        }

        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);

                Date date = new Date(json.getString("date"));
                LatLng latlon = new LatLng(json.getDouble("lat"),json.getDouble("lon"));
                String filename = json.getString("filename");
                String placename = json.getString("placename");
                boolean leaveObject = json.getBoolean("leaveObject");
                // LatLng latlon2 = new LatLng(json.getDouble("lat2"),json.getDouble("lon2"));

                // TODO: Figure out if I need to change db format.
                // TODO: Also, insert a new object into db.

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateImages() {
        List<Bitmap> imgList = getImages();

        // TODO: Save images in internal storage.

        File mydir = mContext.getDir(IMG_DIR_OTH, Context.MODE_PRIVATE); //Creating an internal dir.

        // TODO: Come up with unique filenames.
        for(Bitmap img : imgList) {
            String filename = "tah.png"; // TODO: filename from server
            File fileWithinMyDir = new File(mydir, filename); //Getting a file within the dir.
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(fileWithinMyDir);
                img.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    Log.d(TAG, "Saved image: " + IMG_DIR_OTH + "/" + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Bitmap> getImages() {
        // TODO: Get images from server

        return new ArrayList<>();
    }

    private void getImageFromServer() {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("userid", PreferenceManager
//                    .getDefaultSharedPreferences(FoundActivity.this)
//                    .getString("userid", "NONE"));
            jsonObject.put("userid","amit");    // TODO: Change
            jsonObject.put("filename","/home/amit/images/2.jpeg"); // TODO: Change
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.get(mContext, HttpUtil.GET_IMAGE_ENDPOINT, entity, "application/json", new FileAsyncHttpResponseHandler(mContext) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Log.i(TAG,"Failure - file");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Log.i(TAG, "Success - file");
                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.i(TAG,file.getAbsolutePath());
                saveImage(mContext,bmp,"oth","tah.png");

            }
        });
    }
}
