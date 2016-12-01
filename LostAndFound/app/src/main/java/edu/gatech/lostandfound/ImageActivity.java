package edu.gatech.lostandfound;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private static final String IMG_DIR = "oth"; // TODO: Change to 'oth'.

    private String filename;
    private Date date;
    private LatLng latLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String filename = extras.getString("filename");
        String date = extras.getString("date");
        String latlngfound = extras.getString("latlngfound");
        boolean turnedin = extras.getBoolean("turnedin");
        String latlngturnedin = extras.getString("latlngturnedin");
        String placename = extras.getString("placename");

        ImageView img = (ImageView) findViewById(R.id.img_preview);
        Bitmap photo = getPhoto(filename);
        assert photo != null;
        int newWidth = photo.getWidth()*3;
        int newHeight = photo.getHeight()*3;
        photo = Bitmap.createScaledBitmap(photo, newWidth, newHeight, true);
        assert img != null;
        img.setImageBitmap(photo);
        final Context context = this;
//        img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: The image viewer closes immediately.
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.fromFile(new File(context.getDir(IMG_DIR, Context.MODE_PRIVATE), filename)), "image/*");
//                startActivity(intent);
//            }
//        });

        TextView dt = (TextView) findViewById(R.id.date);
        assert dt != null;
        dt.setText("Date: " + date);

        TextView llf = (TextView) findViewById(R.id.latlngfound);
        assert llf != null;
        llf.setText("Location found: " + latlngfound);

        TextView ti = (TextView) findViewById(R.id.turnedin);
        assert ti != null;
        ti.setText(turnedin ? "Item turned in" : "Item left at location");

        if(turnedin) {
            TextView llti = (TextView) findViewById(R.id.latlngturnedin);
            assert llti != null;
            llti.setVisibility(View.VISIBLE);
            llti.setText("Location turned in: " + latlngturnedin);

            TextView pn = (TextView) findViewById(R.id.placename);
            assert pn != null;
            llti.setVisibility(View.VISIBLE);
            llti.setText("Name of place: " + placename);
        }

    }

    private Bitmap getPhoto(String filename) {
        File mydir = this.getDir(IMG_DIR, Context.MODE_PRIVATE);
        File f = new File(mydir, filename);
        if (!f.exists()) {
            Log.d(TAG,"Image " + IMG_DIR + "/"+ filename + " not found!");
            return null;
        }
        String pathname = f.getAbsolutePath();
        Bitmap bmp = BitmapFactory.decodeFile(pathname);
        Log.d(TAG,"Opened image " + IMG_DIR + "/"+filename);
        return bmp;
    }
}
