package edu.gatech.lostandfound;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Created by abhishekchatterjee on 11/18/16.
 */
public class ImageActivity extends CustomActionBarActivity {
    private static final String TAG = "ImageActivity";
    private static final String IMG_DIR = "myn"; // TODO: Change to 'oth'.

    private String filename;
    private Date date;
    private LatLng latLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String filename = extras.getString("filename");
        String date = extras.getString("date");
        String latlng = extras.getString("latlng");

        ImageView img = (ImageView) findViewById(R.id.img_preview);
        Bitmap photo = getPhoto(filename);
        img.setImageBitmap(photo);

        TextView dt = (TextView) findViewById(R.id.date);
        dt.setText("Date: " + date);

        TextView ll = (TextView) findViewById(R.id.latlng);
        ll.setText("Location: " + latlng);

    }

    private Bitmap getPhoto(String filename) {
        File mydir = this.getDir(IMG_DIR, Context.MODE_PRIVATE);
        File f = new File(mydir, filename);
        if (!f.exists()) {
            Log.d(TAG,"Image " + IMG_DIR + "/"+filename + " not found!");
            return null;
        }
        String pathname = f.getAbsolutePath();
        Bitmap bmp = BitmapFactory.decodeFile(pathname);
        Log.d(TAG,"Opened image " + IMG_DIR + "/"+filename);
        return bmp;
    }
}
