package com.example.p1uber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class DriverDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Drivers.db";
    public static final String TABLE = "drivers";
    public static final String id = "id";
    public static final String col1 = "driver_name";
    public static final String col2 = "lat";
    public static final String col3 = "long";
    public static final String col4 = "rating";
    public static final String col5 = "car";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE + " (" +
            id + " INTEGER PRIMARY KEY," + col1 + " TEXT, " + col2 + " DOUBLE, " + col3 + " DOUBLE, " + col4 + " INT, " + col5 + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE;

    public DriverDB(Context context) {
        super(context, TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int updated) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int old, int updated) {
        onUpgrade(db, old, updated);
    }

    public long noRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE);
        db.close();
        return count;
    }

    // ADDS DRIVER TO DRIVER DATABASE
    public boolean addDriver(String driver, double lat, double longitude, int rating, String car) {
        if (!recordExists(driver)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues vals = new ContentValues();

            vals.put(col1, driver);
            vals.put(col2, lat);
            vals.put(col3, longitude);
            vals.put(col4, rating);
            vals.put(col5, car);

            db.insert(TABLE, null, vals);
            return true;
        }
        return false;
    }

    public boolean recordExists(String name) {
        if (this == null) {
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + col1 + " FROM " + TABLE + " WHERE " + col1 + " = '" + name + "'";
        Cursor cur = db.rawQuery(select, null);
        if(cur.getCount() <= 0) {
            cur.close();
            return false;
        }
        cur.close();
        return true;
    }

    // COLLECTS ALL DRIVERS FROM DATABASE
    public ArrayList<String> getDrivers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + TABLE;
        Cursor cursor = db.rawQuery(select, null);
        ArrayList<String> cursorRes = new ArrayList<String>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            cursorRes.add(cursor.getString(1));
        }
        return cursorRes;
    }

    // GETS AN ARRAY RESULT OF ALL DRIVER DATA
    public String[] getDriver(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + TABLE + " WHERE " + col1 + " = '" + name + "'";
        Cursor cursor = db.rawQuery(select, null);
        int count = cursor.getCount();
        Log.d("Count", Integer.toString(count));
        cursor.moveToLast();

        String[] data = new String[2];
        Log.d("colNo", "ColNo: " + cursor.getColumnIndex(col4));
        data[0] = Integer.toString(cursor.getInt(4));
        data[1] = cursor.getString(5);
        cursor.close();
        return data;
    }

    // CHOOSES 3 RANDOM ROWS FROM THE DB
    public String selectRandom() {
        SQLiteDatabase db = this.getReadableDatabase();
        long rows = noRows();

        // Select 3 random cars and return those rows
        final int random = new Random().nextInt((int) rows + 1);
        // colNames._ID

        return "some row";
    }

//    public void removeAll() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
//        db.close();
//    }
}
