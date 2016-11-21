package edu.gatech.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.gatech.lostandfound.database.PotentialFoundDataSource;
import edu.gatech.lostandfound.database.PotentialFoundObject;

/**
 * Created by abhishekchatterjee on 11/17/16.
 */
public class PotentialFoundListActivity extends CustomActionBarActivity {
    private static final String TAG = "PFLActivity";
    private static final int IMAGE_ACTIVITY = 0;
    private static final String IMG_DIR = "myn"; // Change to "oth".

    private PotentialFoundDataSource dataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potential_found_list);

        dataSource = new PotentialFoundDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        POPULATEDUMMYLIST();

        setUpList();
    }

    /**
     * DUMMY METHOD TO POPULATE DB.
     */
//    private void POPULATEDUMMYLIST() {
//        dataSource.createObject("pathtoimg.png", new Date(116,10,18),new LatLng(30.0,30.0));
//    }

    private void setUpList() {
        // TODO: Find a way to display image thumbnails in the list rather than text.

        final List<PotentialFoundObject> objectList = dataSource.getAllObjects();
        List<Bitmap> images = new ArrayList<>();

        for(PotentialFoundObject object : objectList) {
            String filename = object.getFilename();
            Bitmap photo = getPhoto(filename);
            images.add(photo);
        }

        final ListView listView = (ListView) findViewById(R.id.potential_found_list);

        ArrayAdapter<Bitmap> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, images);

        assert listView != null;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Log.d(TAG,"Clicked item no. " + myItemInt);
                Intent intent = new Intent(PotentialFoundListActivity.this, ImageActivity.class);
                intent.putExtra("filename", objectList.get(myItemInt).getFilename());
                intent.putExtra("date", objectList.get(myItemInt).getDate().toString());
                intent.putExtra("latlng", objectList.get(myItemInt).getLatLng().toString());
                startActivityForResult(intent, IMAGE_ACTIVITY);
            }
        });

        listView.setAdapter(arrayAdapter);
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
        Log.d(TAG, "Opened image " + IMG_DIR + "/" + filename);
        return bmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_ACTIVITY && resultCode == Activity.RESULT_OK) {

        }
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

}
