package com.example.p1uber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.sql.ResultSet;
import java.util.Set;

import static com.example.p1uber.UserDB.colNames.col1;
import static com.example.p1uber.UserDB.colNames.col2;

public class UserDB extends SQLiteOpenHelper {

    public class colNames implements BaseColumns {
        public static final String table = "users";
        public static final String col1 = "username";
        public static final String col2 = "password";
        public static final String col3 = "location";
    }

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + colNames.table + " (" +
            colNames._ID + " INTEGER PRIMARY KEY," + col1 + " TEXT," + colNames.col2 + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + colNames.table;

    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "Users";

    public UserDB(Context context) {
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

    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(col1, username);
        vals.put(col2, password);
        db.insert(TABLE, null, vals);
    }

    public boolean recordExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + col1 + " FROM " + TABLE + " WHERE " + col1 + " = '" + username + "'";
        Cursor cur = db.rawQuery(select, null);
        if(cur.getCount() <= 0) {
            cur.close();
            return false;
        }
        cur.close();
        return true;
    }
}