package com.zybooks.weighttrackerprojectthreenathanielingle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "logins.db";
    private static final int VERSION = 1;

    public LoginDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class LoginTable {
        private static final String TABLE = "login";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LoginTable.TABLE + " (" +
                LoginTable.COL_ID + " integer primary key autoincrement, " +
                LoginTable.COL_USERNAME + " text, " +
                LoginTable.COL_PASSWORD + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + LoginTable.TABLE);
        onCreate(db);
    }

    public boolean addLogin(String username, String password) throws NoSuchAlgorithmException {

        SQLiteDatabase db = getReadableDatabase();

        // Check if username already exists.
        Cursor cursor = db.rawQuery("select * from " + LoginTable.TABLE +
                " where " + LoginTable.COL_USERNAME + " = '" + username + "'", new String[] {});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }

        cursor.close();

        // Hash the password.
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());
        byte [] hashedDigest = md.digest();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hashedDigest) {
            stringBuilder.append(String.format("%02X", b));
        }
        String hashedPassword = stringBuilder.toString();
        ContentValues values = new ContentValues();
        values.put(LoginTable.COL_USERNAME, username);
        values.put(LoginTable.COL_PASSWORD, hashedPassword);

        // insert into the database
        db.insert(LoginTable.TABLE, null, values);
        db.close();
        return true;

    }

    public boolean checkLogin(String username, String password) throws NoSuchAlgorithmException {
        SQLiteDatabase db = getReadableDatabase();

        // Hash the password.
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());
        byte [] hashedDigest = md.digest();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hashedDigest) {
            stringBuilder.append(String.format("%02X", b));
        }
        String hashedPassword = stringBuilder.toString();

        // Check if username and hashed password matches
        Cursor cursor = db.rawQuery("select * from " + LoginTable.TABLE +
                " where " + LoginTable.COL_PASSWORD + " = '" + hashedPassword + "'" +
                " and " + LoginTable.COL_USERNAME + " = '" + username + "'", new String[] {});
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }
}