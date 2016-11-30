package edu.gatech.lostandfound.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by abhishekchatterjee on 11/18/16.
 */
public class PotentialFoundObject {
    private long id;
    private String foundObjectId;
    private String lostObjectId;
    private Date date;
    private LatLng latLng_found;
    private boolean turnedIn;
    private LatLng latLngTurnedIn;
    private String placeName;
    private String filename;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LatLng getLatLngFound() {
        return latLng_found;
    }

    public void setLatLngFound(LatLng latLng) {
        this.latLng_found = latLng;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFoundObjectId() {
        return foundObjectId;
    }

    public void setFoundObjectId(String foundObjectId) {
        this.foundObjectId = foundObjectId;
    }

    public String getLostObjectId() {
        return lostObjectId;
    }

    public void setLostObjectId(String lostObjectId) {
        this.lostObjectId = lostObjectId;
    }

    public boolean isTurnedIn() {
        return turnedIn;
    }

    public void setTurnedIn(boolean turnedIn) {
        this.turnedIn = turnedIn;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public LatLng getLatLngTurnedIn() {
        return latLngTurnedIn;
    }

    public void setLatLngTurnedIn(LatLng latLngTurnedIn) {
        this.latLngTurnedIn = latLngTurnedIn;
    }
}
