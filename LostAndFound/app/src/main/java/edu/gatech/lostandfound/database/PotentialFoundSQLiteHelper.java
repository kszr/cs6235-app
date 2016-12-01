package edu.gatech.lostandfound.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by abhishekchatterjee on 11/18/16.
 */
public class PotentialFoundSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_POTENTIAL_FOUND = "potentialfound";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FOUND_OBJECT_ID = "foid";
    public static final String COLUMN_LOST_OBJECT_ID = "loid";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LATLNG_FOUND = "latlng_found";
    public static final String COLUMN_TURNED_IN = "turnedin";
    public static final String COLUMN_LATLNG_TURNED_IN = "latlng_turnedin";
    public static final String COLUMN_PLACE_NAME = "placename";
    public static final String COLUMN_FILENAME = "filename";

    private static final String DATABASE_NAME = "potentialfound.db";
    private static final int DATABASE_VERSION = 2;  // Needs to be updated whenever the schema is changed.

    private static final String DATABASE_CREATE = "create table "
            + TABLE_POTENTIAL_FOUND + "( " + COLUMN_ID
            + " integer primary key autoincrement, " +
            COLUMN_FOUND_OBJECT_ID + " text not null, " +
            COLUMN_LOST_OBJECT_ID + " text not null, " +
            COLUMN_DATE + " text not null, " +
            COLUMN_LATLNG_FOUND + " text not null, " +
            COLUMN_TURNED_IN + " text not null, " +
            COLUMN_LATLNG_TURNED_IN + " text not null, " +
            COLUMN_PLACE_NAME + " text not null, " +
            COLUMN_FILENAME + " text not null" + ");";

    public PotentialFoundSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PotentialFoundSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POTENTIAL_FOUND);
        onCreate(db);
    }
}
