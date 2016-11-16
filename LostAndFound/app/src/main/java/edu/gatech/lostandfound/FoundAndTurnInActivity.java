package edu.gatech.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by abhishekchatterjee on 10/24/16.
 */
public class FoundAndTurnInActivity extends CustomActionBarActivity implements OnMapReadyCallback {
    private static final String TAG = "FoundAndTurnInActivity";
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_and_turn_in);

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat",0.);
        lon = intent.getDoubleExtra("lon",0.);
        Log.d(TAG, "lat: " + lat + "; lon: " + lon);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title("Marker"));

    }

}
