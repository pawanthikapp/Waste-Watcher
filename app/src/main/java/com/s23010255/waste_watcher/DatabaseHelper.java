package com.s23010255.waste_watcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WasteWatcher.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createQuery);


        db.execSQL("INSERT OR IGNORE INTO " + TABLE_USERS +
                " (" + COL_USERNAME + ", " + COL_EMAIL + ", " + COL_PASSWORD + ") " +
                "VALUES ('testuser', 'test@email.com', '1234')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    public boolean registerUser(String username, String email, String password) {
        if (isUserExists(email)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);

        Log.d("DB_REGISTER", "Insert result: " + result);
        Log.d("SIGNUP_DEBUG", "registerUser called");
        Log.d("SIGNUP_DEBUG", "Values: " + username + ", " + email + ", " + password);

        return result != -1;
    }


    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    public boolean isUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?",
                new String[]{email});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
