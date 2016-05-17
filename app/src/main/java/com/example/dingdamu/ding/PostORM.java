package com.example.dingdamu.ding;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by dingdamu on 10/05/16.
 */
public class PostORM {
    private static final String TAG = "PostORM";
    public static final String TABLE_NAME = "post";
    private static final String COLUMN_URI = "uri";
    private static final String COLUMN_COORDINATES = "coordinates";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_TIME = "time";
    private DatabaseWrapper dw;
    SQLiteDatabase myDataBase;

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_URI + " TEXT, "
                    + COLUMN_COORDINATES + " TEXT, "
                    + COLUMN_ADDRESS + " TEXT, "
                    + COLUMN_TIME + " TEXT)";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public int insertPost(Context c, String uri, String coordinates, String address, String time)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        long postId = 0;
        if (isDatabaseOpened()) {
            ContentValues values = new ContentValues();
            values.put(PostORM.COLUMN_URI,uri);
            values.put(PostORM.COLUMN_COORDINATES,coordinates);
            values.put(PostORM.COLUMN_ADDRESS,address);
            values.put(PostORM.COLUMN_TIME,time);
            postId = myDataBase.insert(PostORM.TABLE_NAME, "null", values);
            Log.e(TAG, "Inserted new Post with ID: " + postId);
            myDataBase.close();
        }
        return (int) postId;
    }

    public ArrayList<String> getUrifromDB(Context c)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> uris = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * from post",null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            uris.add(cur.getString(0));
        }
        return uris;
    }


    public ArrayList<String> getCoordinatesfromDB(Context c)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> coordinates = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * from post",null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            coordinates.add(cur.getString(1));
        }
        return coordinates;
    }

    public ArrayList<String> getAddressfromDB(Context c)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> addresses = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * from post",null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            addresses.add(cur.getString(2));
        }
        return addresses;
    }

    public ArrayList<String> getTimefromDB(Context c)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> times = new ArrayList<String>();
        Cursor cur = myDataBase.rawQuery("SELECT * from post",null);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            times.add(cur.getString(3));
        }
        return times;
    }
    public ArrayList<String> getNeededUrifromDB(Context c,String address)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> uri_now = new ArrayList<String>();

        Cursor cur = myDataBase.rawQuery("SELECT * from post where address = ?",new String[] {address});
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            uri_now.add(cur.getString(0));

        }
        return uri_now;
    }

    public ArrayList<String> getNeededCoordinatesfromDB(Context c,String address)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> coordinates_now = new ArrayList<String>();

        Cursor cur = myDataBase.rawQuery("SELECT * from post where address =?",new String[] {address});
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            coordinates_now.add(cur.getString(1));

        }
        return coordinates_now;
    }
    public ArrayList<String> getNeededAddressfromDB(Context c,String address)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> addresses_now = new ArrayList<String>();

        Cursor cur = myDataBase.rawQuery("SELECT * from post where address =?",new String[] {address});
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            addresses_now.add(cur.getString(2));

        }
        return addresses_now;
    }

    public ArrayList<String> getNeededTimefromDB(Context c,String address)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(c);
        myDataBase = databaseWrapper.getWritableDatabase();
        ArrayList<String> time_now = new ArrayList<String>();

        Cursor cur = myDataBase.rawQuery("SELECT * from post where address =?",new String[] {address});
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext())
        {
            time_now.add(cur.getString(3));

        }
        return time_now;
    }


    public boolean isDatabaseOpened() {
        if (myDataBase == null) {
            return false;
        } else {
            return myDataBase.isOpen();
        }
    }

}

