package edu.gatech.lostandfound.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */
public class ReportedLostObjectDataSource {
    private SQLiteDatabase database;
    private ReportedLostObjectSQLiteHelper dbHelper;
    private String[] allColumns = { ReportedLostObjectSQLiteHelper.COLUMN_ID,
            ReportedLostObjectSQLiteHelper.COLUMN_LOST_OBJECT_ID,
            ReportedLostObjectSQLiteHelper.COLUMN_DATE,
            ReportedLostObjectSQLiteHelper.COLUMN_LATLNG_LOST,
            ReportedLostObjectSQLiteHelper.COLUMN_FOUND};

    public ReportedLostObjectDataSource(Context context) {
        dbHelper = new ReportedLostObjectSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ReportedLostObject createObject(String loid,
                                           Date date,
                                           LatLng latLngLost,
                                           boolean found) {
        ContentValues values = new ContentValues();
        values.put(ReportedLostObjectSQLiteHelper.COLUMN_LOST_OBJECT_ID,loid);
        values.put(ReportedLostObjectSQLiteHelper.COLUMN_DATE,date.toString());

        String llstring1 = latLngLost.latitude+","+latLngLost.longitude;
        values.put(ReportedLostObjectSQLiteHelper.COLUMN_LATLNG_LOST,llstring1);

        values.put(ReportedLostObjectSQLiteHelper.COLUMN_FOUND,String.valueOf(found));

        long insertId = database.insert(ReportedLostObjectSQLiteHelper.TABLE_REPORTED_LOST_OBJECTS, null,
                values);
        Cursor cursor = database.query(ReportedLostObjectSQLiteHelper.TABLE_REPORTED_LOST_OBJECTS,
                allColumns, ReportedLostObjectSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ReportedLostObject newObject = cursorToObject(cursor);
        cursor.close();
        return newObject;
    }

    public void deleteObject(ReportedLostObject object) {
        long id = object.getId();
        database.delete(ReportedLostObjectSQLiteHelper.TABLE_REPORTED_LOST_OBJECTS, ReportedLostObjectSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<ReportedLostObject> getAllObjects() {
        List<ReportedLostObject> objects = new ArrayList<>();

        Cursor cursor = database.query(ReportedLostObjectSQLiteHelper.TABLE_REPORTED_LOST_OBJECTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReportedLostObject object = cursorToObject(cursor);
            objects.add(object);
            cursor.moveToNext();
        }

        cursor.close();
        return objects;
    }

    private ReportedLostObject cursorToObject(Cursor cursor) {
        ReportedLostObject object = new ReportedLostObject();
        object.setId(cursor.getLong(0));
        object.setLostObjectId(cursor.getString(1));
        object.setDate(new Date(cursor.getString(2)));
        String[] latlng1 = cursor.getString(3).split(",");
        object.setLatLngLost(new LatLng(Double.parseDouble(latlng1[0]), Double.parseDouble(latlng1[1])));
        object.setFound(Boolean.parseBoolean(cursor.getString(4)));
        return object;
    }
}
