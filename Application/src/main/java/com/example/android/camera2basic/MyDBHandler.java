package com.example.android.camera2basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "studentDB.db";
    public static final String TABLE_NAME = "HeartRate";
    public static final String COLUMN_CREATED_AT = "DateTime";
    public static final String COLUMN_BPM = "HeartRate";
    public static final String COLUMN_AVNN = "HRV_AVNN";
    public static final String COLUMN_SDNN = "HRV_SDNN";
    public static final String COLUMN_RMSSD = "HRV_RMSSD";
    public static final String COLUMN_PPN50 = "HRV_PPN50";

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_CREATED_AT + " TEXT PRIMARY KEY ," +
                COLUMN_BPM + " DOUBLE ," +
                COLUMN_AVNN + " DOUBLE ," +
                COLUMN_SDNN + " DOUBLE ," +
                COLUMN_RMSSD + " DOUBLE ," +
                COLUMN_PPN50 + " DOUBLE " + ");";
        Log.v("TableCreated", CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public ArrayList<ArrayList<Object>> loadHandler() {
        ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            ArrayList<Object> curResult = new ArrayList<Object>();
            if (cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)) != null) {
                String dateTime = cursor.getString(0);
                Double bpm = cursor.getDouble(1);
                Double avnn = cursor.getDouble(2);
                Double sdnn = cursor.getDouble(3);
                Double rmssd = cursor.getDouble(4);
                Double ppn50 = cursor.getDouble(5);
                curResult.add(dateTime);
                curResult.add(bpm);
                curResult.add(avnn);
                curResult.add(sdnn);
                curResult.add(rmssd);
                curResult.add(ppn50);
                result.add(curResult);
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return result;
    }

    public void addHandler(HeartData hd) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CREATED_AT, getDateTime());
        values.put(COLUMN_BPM, hd.getBPM());
        values.put(COLUMN_AVNN, hd.getAVNN());
        values.put(COLUMN_SDNN, hd.getSDNN());
        values.put(COLUMN_RMSSD, hd.getRMSSD());
        values.put(COLUMN_PPN50, hd.getPPN50());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Object> findHandler(String dateTime) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CREATED_AT + " = " + "'" + dateTime + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Object> curResult = new ArrayList<Object>();

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            Double bpm = cursor.getDouble(1);
            Double avnn = cursor.getDouble(2);
            Double sdnn = cursor.getDouble(3);
            Double rmssd = cursor.getDouble(4);
            Double ppn50 = cursor.getDouble(5);
            curResult.add(dateTime);
            curResult.add(bpm);
            curResult.add(avnn);
            curResult.add(sdnn);
            curResult.add(rmssd);
            curResult.add(ppn50);
            cursor.close();
        } else {
            curResult = null;
        }

        db.close();
        return curResult;
    }

    public boolean deleteHandler(String dateTime) {
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CREATED_AT + " = '" + dateTime + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
//            student.setStudentID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME, COLUMN_CREATED_AT + "=?", new String[]{dateTime});
            cursor.close();
            result = true;
        }

        db.close();
        return result;
    }

    public ArrayList<String> getAllDate() {
        ArrayList<String> result = new ArrayList<String>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT)) != null) {
                String dateTime = cursor.getString(0);
                result.add(dateTime);
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return result;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}