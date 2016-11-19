package edu.gatech.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
        List<PotentialFoundObject> objectList = dataSource.getAllObjects();

        final ListView listView = (ListView) findViewById(R.id.potential_found_list);

        ArrayAdapter<PotentialFoundObject> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, objectList);
        assert listView != null;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Log.d(TAG,"Clicked item no. " + myItemInt);
                Intent intent = new Intent(PotentialFoundListActivity.this, ImageActivity.class);
                intent.putExtra("filename", ((PotentialFoundObject) listView.getItemAtPosition(myItemInt)).getFilename());
                intent.putExtra("date",((PotentialFoundObject) listView.getItemAtPosition(myItemInt)).getDate().toString());
                intent.putExtra("latlng", ((PotentialFoundObject) listView.getItemAtPosition(myItemInt)).getLatLng().toString());
                startActivityForResult(intent, IMAGE_ACTIVITY);
            }
        });

        listView.setAdapter(arrayAdapter);
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
