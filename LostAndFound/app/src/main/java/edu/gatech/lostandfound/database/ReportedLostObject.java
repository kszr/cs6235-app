package edu.gatech.lostandfound.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */
public class ReportedLostObject {
    private long id;
    private String lostObjectId;
    private Date date;
    private LatLng latLngLost;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLostObjectId() {
        return lostObjectId;
    }

    public void setLostObjectId(String lostObjectId) {
        this.lostObjectId = lostObjectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LatLng getLatLngLost() {
        return latLngLost;
    }

    public void setLatLngLost(LatLng latLngLost) {
        this.latLngLost = latLngLost;
    }
}
