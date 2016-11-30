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
public class ReportedObjectDataSource {
    private SQLiteDatabase database;
    private PotentialFoundSQLiteHelper dbHelper;
    private String[] allColumns = { PotentialFoundSQLiteHelper.COLUMN_ID,
            PotentialFoundSQLiteHelper.COLUMN_DATE,
            PotentialFoundSQLiteHelper.COLUMN_LATLNG_FOUND,
            PotentialFoundSQLiteHelper.COLUMN_TURNED_IN,
            PotentialFoundSQLiteHelper.COLUMN_LATLNG_TURNED_IN,
            PotentialFoundSQLiteHelper.COLUMN_PLACE_NAME,
            PotentialFoundSQLiteHelper.COLUMN_FILENAME};

    public ReportedObjectDataSource(Context context) {
        dbHelper = new PotentialFoundSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ReportedObject createObject(Date date,
                                             LatLng latLngFound,
                                             boolean turnedIn,
                                             LatLng latLngTurnedIn,
                                             String placeName,
                                             String filename) {
        ContentValues values = new ContentValues();
        values.put(PotentialFoundSQLiteHelper.COLUMN_FILENAME,filename);
        values.put(PotentialFoundSQLiteHelper.COLUMN_DATE,date.toString());

        String llstring1 = latLngFound.latitude+","+latLngFound.longitude;
        values.put(PotentialFoundSQLiteHelper.COLUMN_LATLNG_FOUND,llstring1);

        values.put(PotentialFoundSQLiteHelper.COLUMN_TURNED_IN,String.valueOf(turnedIn));

        String llstring2 = "";
        String pn = "";

        if(turnedIn) {
            llstring2 = latLngTurnedIn.latitude+","+latLngTurnedIn.longitude;
            pn = placeName;
        }

        values.put(PotentialFoundSQLiteHelper.COLUMN_LATLNG_TURNED_IN,llstring2);
        values.put(PotentialFoundSQLiteHelper.COLUMN_PLACE_NAME,pn);

        long insertId = database.insert(ReportedObjectSQLiteHelper.TABLE_REPORTED_OBJECTS, null,
                values);
        Cursor cursor = database.query(ReportedObjectSQLiteHelper.TABLE_REPORTED_OBJECTS,
                allColumns, ReportedObjectSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ReportedObject newObject = cursorToObject(cursor);
        cursor.close();
        return newObject;
    }

    public void deleteObject(PotentialFoundObject object) {
        long id = object.getId();
//        System.out.println("Object deleted with id: " + id);
        database.delete(ReportedObjectSQLiteHelper.TABLE_REPORTED_OBJECTS, ReportedObjectSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<ReportedObject> getAllObjects() {
        List<ReportedObject> objects = new ArrayList<>();

        Cursor cursor = database.query(ReportedObjectSQLiteHelper.TABLE_REPORTED_OBJECTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReportedObject object = cursorToObject(cursor);
            objects.add(object);
            cursor.moveToNext();
        }

        cursor.close();
        return objects;
    }

    private ReportedObject cursorToObject(Cursor cursor) {
        ReportedObject object = new ReportedObject();
        object.setId(cursor.getLong(0));
        object.setDate(new Date(cursor.getString(1)));
        String[] latlng1 = cursor.getString(2).split(",");
        object.setLatLngFound(new LatLng(Double.parseDouble(latlng1[0]), Double.parseDouble(latlng1[1])));
        boolean turnedin = Boolean.parseBoolean(cursor.getString(3));
        object.setTurnedIn(turnedin);
        if(turnedin) {
            String[] latlng2 = cursor.getString(4).split(",");
            object.setLatLngTurnedIn(new LatLng(Double.parseDouble(latlng2[0]),Double.parseDouble(latlng2[1])));
            object.setPlaceName(cursor.getString(5));
        } else {
            object.setLatLngTurnedIn(null);
            object.setPlaceName("");
        }
        object.setFilename(cursor.getString(6));
        return object;
    }

}
