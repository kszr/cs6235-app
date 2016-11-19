package edu.gatech.lostandfound.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abhishekchatterjee on 11/18/16.
 */
public class PotentialFoundDataSource {
    private SQLiteDatabase database;
    private PotentialFoundSQLiteHelper dbHelper;
    private String[] allColumns = { PotentialFoundSQLiteHelper.COLUMN_ID,
            PotentialFoundSQLiteHelper.COLUMN_FILENAME,
            PotentialFoundSQLiteHelper.COLUMN_DATE,
            PotentialFoundSQLiteHelper.COLUMN_LATLNG};

    public PotentialFoundDataSource(Context context) {
        dbHelper = new PotentialFoundSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public PotentialFoundObject createObject(String filename,
                                              Date date,
                                              LatLng latLng) {
        ContentValues values = new ContentValues();
        values.put(PotentialFoundSQLiteHelper.COLUMN_FILENAME,filename);
        values.put(PotentialFoundSQLiteHelper.COLUMN_DATE,date.toString());

        String llstring = latLng.latitude+","+latLng.longitude;
        values.put(PotentialFoundSQLiteHelper.COLUMN_LATLNG,llstring);

        long insertId = database.insert(PotentialFoundSQLiteHelper.TABLE_POTENTIAL_FOUND, null,
                values);
        Cursor cursor = database.query(PotentialFoundSQLiteHelper.TABLE_POTENTIAL_FOUND,
                allColumns, PotentialFoundSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        PotentialFoundObject newObject = cursorToObject(cursor);
        cursor.close();
        return newObject;
    }

    public void deleteObject(PotentialFoundObject object) {
        long id = object.getId();
//        System.out.println("Object deleted with id: " + id);
        database.delete(PotentialFoundSQLiteHelper.TABLE_POTENTIAL_FOUND, PotentialFoundSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<PotentialFoundObject> getAllObjects() {
        List<PotentialFoundObject> objects = new ArrayList<>();

        Cursor cursor = database.query(PotentialFoundSQLiteHelper.TABLE_POTENTIAL_FOUND,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PotentialFoundObject object = cursorToObject(cursor);
            objects.add(object);
            cursor.moveToNext();
        }

        cursor.close();
        return objects;
    }

    private PotentialFoundObject cursorToObject(Cursor cursor) {
        PotentialFoundObject object = new PotentialFoundObject();
        object.setId(cursor.getLong(0));
        object.setFilename(cursor.getString(1));
        object.setDate(new Date(cursor.getString(2)));
        String[] latlng =  cursor.getString(3).split(",");
        object.setLatLng(new LatLng(Double.parseDouble(latlng[0]),Double.parseDouble(latlng[1])));
        return object;
    }

}
