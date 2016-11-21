package edu.gatech.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by abhishekchatterjee on 10/24/16.
 */
public class FoundAndTurnInActivity extends CustomActionBarActivity implements OnMapReadyCallback {
    private static final String TAG = "FoundAndTurnInActivity";
    private double lat;
    private double lon;
    private GoogleMap mGoogleMap;

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
        mGoogleMap = googleMap;
        mGoogleMap.setOnInfoWindowClickListener(getInfoWindowClickListener());
        LatLng position = new LatLng(lat, lon);

        // TODO: Add markers for all the suggested places where the user may turn in
        // the found object.
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Marker"));

        pointToPosition(position);
    }

    private void pointToPosition(LatLng position) {
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(17).build();
        //Zoom in and animate the camera.
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private OnInfoWindowClickListener getInfoWindowClickListener() {
        return new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // TODO: What happens when a marker is clicked?
            }
        };
    }
}
