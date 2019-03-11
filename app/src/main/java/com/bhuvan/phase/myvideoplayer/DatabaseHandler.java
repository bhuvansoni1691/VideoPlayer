package com.bhuvan.phase.myvideoplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String DB_NAME = "myVideo.db"; // /sdcard/necumobility5/

    public static final String TABLE_NAME = "USER_COLLECTION";

    public static final String KEY_ID = "id";
    public static final String KEY_VIDEO_ID = "videoID";
    public static final String KEY_IS_VIDEO_LIKED = "isVideoLiked";
    public static final String KEY_IS_VIDEO_DISLIKED = "isVideoDisliked";


    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_VIDEO_ID + " INTEGER,"
            + KEY_IS_VIDEO_LIKED + " INTEGER DEFAULT 0,"
            +KEY_IS_VIDEO_DISLIKED + " INTEGER DEFAULT 0);";


    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d( "VIDEO","CREATE DATABASE" );

        sqLiteDatabase.execSQL( CREATE_TABLE );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
