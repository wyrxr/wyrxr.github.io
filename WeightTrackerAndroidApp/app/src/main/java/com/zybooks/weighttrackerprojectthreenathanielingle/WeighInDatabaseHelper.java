package com.zybooks.weighttrackerprojectthreenathanielingle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import java.util.ArrayList;

public class WeighInDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weights.db";
    private static final int VERSION = 1;

    public WeighInDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class WeightTable {
        private static final String TABLE = "weights";
        private static final String COL_ID = "_id";
        private static final String COL_DATE = "date";
        private static final String COL_WEIGHT = "weight";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + WeightTable.TABLE + " (" +
                WeightTable.COL_ID + " integer primary key autoincrement, " +
                WeightTable.COL_DATE + " text, " +
                WeightTable.COL_WEIGHT + " float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + WeightTable.TABLE);
        onCreate(db);
    }

    public List<WeighIn> getWeights() {
        List<WeighIn> weighIns = new ArrayList<WeighIn>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + WeightTable.TABLE, new String[] {});
        if (cursor.moveToFirst()) {
            // Build a list of WeighIn objects from the database
            do {
                long id = cursor.getLong(0);
                String date = cursor.getString(1);
                float weight = cursor.getLong(2);
                weighIns.add(new WeighIn(id, date, weight));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return weighIns;
    }

    public long addWeighIn(String date, float weight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeightTable.COL_DATE, date);
        values.put(WeightTable.COL_WEIGHT, weight);

        long result = db.insert(WeightTable.TABLE, null, values);
        db.close();
        return result;
    }

    public void editWeighIn(long id, float newWeight) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("update " + WeightTable.TABLE + " set " + WeightTable.COL_WEIGHT + " = " +
                newWeight + " where " + WeightTable.COL_ID + " = " + id);
        db.close();
    }

    public void deleteWeighIn(long id) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("delete from " + WeightTable.TABLE + " where " + WeightTable.COL_ID + " = " + id);
        db.close();
    }

    // This method isn't currently used, but it may be useful in the future.
    public WeighIn getWeighIn(int weighInId) {
        SQLiteDatabase db = getReadableDatabase();

        WeighIn result = new WeighIn();
        String sql = "select * from " + WeightTable.TABLE + " where " + WeightTable.COL_ID + " = " + weighInId;
        Cursor cursor = db.rawQuery(sql, new String[] {});
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String date = cursor.getString(1);
                float weight = cursor.getLong(2);
                result = new WeighIn(id, date, weight);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

}
