package edu.gatech.lostandfound.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by abhishekchatterjee on 11/18/16.
 */
public class PotentialFoundObject {
    private long id;
    private Date date;
    private String filename;
    private LatLng latLng;

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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
