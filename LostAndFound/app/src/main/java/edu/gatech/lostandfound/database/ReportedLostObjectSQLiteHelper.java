package edu.gatech.lostandfound.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */
public class ReportedLostObjectSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_REPORTED_LOST_OBJECTS = "reportedlostobjects";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOST_OBJECT_ID = "loid";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LATLNG_LOST = "latlng_lost";

    private static final String DATABASE_NAME = "reportedlostobjects.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_REPORTED_LOST_OBJECTS + "( " + COLUMN_ID
            + " integer primary key autoincrement, " +
            COLUMN_LOST_OBJECT_ID + " text not null, " +
            COLUMN_DATE + " text not null, " +
            COLUMN_LATLNG_LOST + " text not null " + ");";

    public ReportedLostObjectSQLiteHelper(Context context) {
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTED_LOST_OBJECTS);
        onCreate(db);
    }
}
