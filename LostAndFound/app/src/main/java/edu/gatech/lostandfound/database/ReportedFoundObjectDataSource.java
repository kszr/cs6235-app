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
public class ReportedFoundObjectDataSource {
    private SQLiteDatabase database;
    private ReportedFoundObjectSQLiteHelper dbHelper;
    private String[] allColumns = { ReportedFoundObjectSQLiteHelper.COLUMN_ID,
            ReportedFoundObjectSQLiteHelper.COLUMN_FOUND_OBJECT_ID,
            ReportedFoundObjectSQLiteHelper.COLUMN_DATE,
            ReportedFoundObjectSQLiteHelper.COLUMN_LATLNG_FOUND,
            ReportedFoundObjectSQLiteHelper.COLUMN_TURNED_IN,
            ReportedFoundObjectSQLiteHelper.COLUMN_LATLNG_TURNED_IN,
            ReportedFoundObjectSQLiteHelper.COLUMN_PLACE_NAME,
            ReportedFoundObjectSQLiteHelper.COLUMN_FILENAME,
            ReportedFoundObjectSQLiteHelper.COLUMN_CLAIMED};

    public ReportedFoundObjectDataSource(Context context) {
        dbHelper = new ReportedFoundObjectSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ReportedFoundObject createObject(String foid,
                                    Date date,
                                    LatLng latLngFound,
                                    boolean turnedIn,
                                    LatLng latLngTurnedIn,
                                    String placeName,
                                    String filename,
                                    boolean claimed) {
        ContentValues values = new ContentValues();
        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_FOUND_OBJECT_ID,foid);
        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_FILENAME,filename);
        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_DATE,date.toString());

        String llstring1 = latLngFound.latitude+","+latLngFound.longitude;
        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_LATLNG_FOUND,llstring1);

        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_TURNED_IN,String.valueOf(turnedIn));

        String llstring2 = "";
        String pn = "";

        if(turnedIn) {
            llstring2 = latLngTurnedIn.latitude+","+latLngTurnedIn.longitude;
            pn = placeName;
        }

        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_LATLNG_TURNED_IN,llstring2);
        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_PLACE_NAME,pn);

        values.put(ReportedFoundObjectSQLiteHelper.COLUMN_CLAIMED,String.valueOf(claimed));

        long insertId = database.insert(ReportedFoundObjectSQLiteHelper.TABLE_REPORTED_FOUND_OBJECTS, null,
                values);
        Cursor cursor = database.query(ReportedFoundObjectSQLiteHelper.TABLE_REPORTED_FOUND_OBJECTS,
                allColumns, ReportedFoundObjectSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ReportedFoundObject newObject = cursorToObject(cursor);
        cursor.close();
        return newObject;
    }

    public void deleteObject(ReportedFoundObject object) {
        long id = object.getId();
//        System.out.println("Object deleted with id: " + id);
        database.delete(ReportedFoundObjectSQLiteHelper.TABLE_REPORTED_FOUND_OBJECTS, ReportedFoundObjectSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<ReportedFoundObject> getAllObjects() {
        List<ReportedFoundObject> objects = new ArrayList<>();

        Cursor cursor = database.query(ReportedFoundObjectSQLiteHelper.TABLE_REPORTED_FOUND_OBJECTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReportedFoundObject object = cursorToObject(cursor);
            objects.add(object);
            cursor.moveToNext();
        }

        cursor.close();
        return objects;
    }

    private ReportedFoundObject cursorToObject(Cursor cursor) {
        ReportedFoundObject object = new ReportedFoundObject();
        object.setId(cursor.getLong(0));
        object.setFoundObjectId(cursor.getString(1));
        object.setDate(new Date(cursor.getString(2)));
        String[] latlng1 = cursor.getString(3).split(",");
        object.setLatLngFound(new LatLng(Double.parseDouble(latlng1[0]), Double.parseDouble(latlng1[1])));
        boolean turnedin = Boolean.parseBoolean(cursor.getString(4));
        object.setTurnedIn(turnedin);
        if(turnedin) {
            String[] latlng2 = cursor.getString(5).split(",");
            object.setLatLngTurnedIn(new LatLng(Double.parseDouble(latlng2[0]),Double.parseDouble(latlng2[1])));
            object.setPlaceName(cursor.getString(6));
        } else {
            object.setLatLngTurnedIn(null);
            object.setPlaceName("");
        }
        object.setFilename(cursor.getString(7));
        object.setClaimed(Boolean.parseBoolean(cursor.getString(8)));
        return object;
    }

}
