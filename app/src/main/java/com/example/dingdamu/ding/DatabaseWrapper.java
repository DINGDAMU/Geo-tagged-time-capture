package com.example.dingdamu.ding;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dingdamu on 10/05/16.
 */
public class DatabaseWrapper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseWrapper";
    private static final String DATABASE_NAME = "QuestionDatabase.db";
    private static final int DATABASE_VERSION = 1;


    public DatabaseWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "Creating database [" + DATABASE_NAME + " v." + DATABASE_VERSION + "]...");
        db.execSQL(PostORM.SQL_CREATE_TABLE);
        // db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +PostORM.TABLE_NAME);
        onCreate(db);
    }
}

