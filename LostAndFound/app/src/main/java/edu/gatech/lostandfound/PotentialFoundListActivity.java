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
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.lostandfound.adapter.ImageAdapter;
import edu.gatech.lostandfound.database.PotentialFoundDataSource;
import edu.gatech.lostandfound.database.PotentialFoundObject;

/**
 * Created by abhishekchatterjee on 11/17/16.
 */
public class PotentialFoundListActivity extends CustomActionBarActivity {
    private static final String TAG = "PFLActivity";
    private static final int IMAGE_ACTIVITY = 0;
    private static final String IMG_DIR = "oth";

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

        setUpList();
    }

    private void setUpList() {
        final List<PotentialFoundObject> objectList = dataSource.getAllObjects();
        List<Bitmap> images = new ArrayList<>();

        for(PotentialFoundObject object : objectList) {
            String filename = object.getFilename();
            Log.d(TAG,"Filename: " + filename);
            Bitmap photo = getPhoto(filename);
            if(photo != null)
                images.add(photo);
        }

        Log.d(TAG,"Image list size = " + images.size());
        TextView tv = (TextView) findViewById(R.id.no_objects_to_show);
        GridView gridView = (GridView) findViewById(R.id.potential_found_list);
        if(images.size() == 0) {
            Log.i(TAG, "Image list size is zero");
            assert tv != null;
            tv.setVisibility(View.VISIBLE);
            assert gridView != null;
            gridView.setVisibility(View.GONE);
        } else {
            Log.i(TAG, "Image list size is non-zero");
            assert tv != null;
            tv.setVisibility(View.GONE);
            assert gridView != null;
            gridView.setVisibility(View.VISIBLE);
        }

        ImageAdapter imageAdapter = new ImageAdapter(this,images);

        assert gridView != null;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Log.d(TAG, "Clicked item no. " + myItemInt);
                Intent intent = new Intent(PotentialFoundListActivity.this, ImageActivity.class);
                intent.putExtra("filename", objectList.get(myItemInt).getFilename());
                intent.putExtra("date", objectList.get(myItemInt).getDate().toString());
                intent.putExtra("latlngfound", objectList.get(myItemInt).getLatLngFound().toString());
                intent.putExtra("foid",objectList.get(myItemInt).getFoundObjectId());
                intent.putExtra("loid",objectList.get(myItemInt).getLostObjectId());
                boolean turnedin = objectList.get(myItemInt).isTurnedIn();
                intent.putExtra("turnedin",turnedin);
                if(turnedin) {
                    intent.putExtra("latlngturnedin",objectList.get(myItemInt).getLatLngFound().toString());
                    intent.putExtra("placename",objectList.get(myItemInt).getPlaceName());
                } else {
                    intent.putExtra("latlngturnedin","");
                    intent.putExtra("placename","");
                }
                startActivityForResult(intent, IMAGE_ACTIVITY);
            }
        });

        gridView.setAdapter(imageAdapter);
    }

    private Bitmap getPhoto(String filename) {
        File mydir = this.getDir(IMG_DIR, Context.MODE_PRIVATE);
        File f = new File(mydir, filename);
        if (!f.exists()) {
            Log.d(TAG,"Image " + IMG_DIR + "/"+ filename + " not found!");
            return null;
        }
        String pathname = f.getAbsolutePath();
        Log.d(TAG, "Abs path of image: " + pathname);
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

    @Override
    public void finish() {
        dataSource.close();

        super.finish();
    }
}
