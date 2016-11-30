package edu.gatech.lostandfound.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */
public class ReportedFoundObject {
    private long id;
    private String foundObjectId;
    private Date date;
    private LatLng latLngFound;
    private boolean turnedIn;
    private LatLng latLngTurnedIn;
    private String placeName;
    private String filename;
    private boolean claimed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LatLng getLatLngFound() {
        return latLngFound;
    }

    public void setLatLngFound(LatLng latLng_found) {
        this.latLngFound = latLng_found;
    }

    public boolean isTurnedIn() {
        return turnedIn;
    }

    public void setTurnedIn(boolean turnedIn) {
        this.turnedIn = turnedIn;
    }

    public LatLng getLatLngTurnedIn() {
        return latLngTurnedIn;
    }

    public void setLatLngTurnedIn(LatLng latLngTurnedIn) {
        this.latLngTurnedIn = latLngTurnedIn;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFoundObjectId() {
        return foundObjectId;
    }

    public void setFoundObjectId(String foundObjectId) {
        this.foundObjectId = foundObjectId;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
