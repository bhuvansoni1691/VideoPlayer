package com.bhuvan.phase.myvideoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseManager {

    private DatabaseHandler databaseHandler_ = null;

    private Context context_;

    private SQLiteDatabase sqLiteDatabase_ = null;


    public DatabaseManager( Context context ) {
        context_ = context;
    }


    public DatabaseManager open() throws SQLException {
        Log.d( "VIDEO","OPEN DATABASE" );

        if( null == databaseHandler_ )
            databaseHandler_ = new DatabaseHandler(context_);

        if( null == sqLiteDatabase_ )
            sqLiteDatabase_ = databaseHandler_.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHandler_.close();
    }


    public void insert( UserData userData ) {
        Log.d( "VIDEO","INSERT DATABASE" );

        ContentValues contentValues = new ContentValues();
        contentValues.put( DatabaseHandler.KEY_VIDEO_ID, userData.getVideoID() );
        contentValues.put( DatabaseHandler.KEY_IS_VIDEO_LIKED, userData.getIsLiked() );
        contentValues.put( DatabaseHandler.KEY_IS_VIDEO_DISLIKED, userData.getIsDisLiked() );

        sqLiteDatabase_.insert( DatabaseHandler.TABLE_NAME, null, contentValues );
    }


    public int update( UserData userData ) {
        int affectedRow = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHandler.KEY_VIDEO_ID, userData.getVideoID());
            contentValues.put(DatabaseHandler.KEY_IS_VIDEO_LIKED, userData.getIsLiked());
            contentValues.put(DatabaseHandler.KEY_IS_VIDEO_DISLIKED, userData.getIsDisLiked());

            affectedRow = sqLiteDatabase_.update(DatabaseHandler.TABLE_NAME, contentValues,
                    DatabaseHandler.KEY_VIDEO_ID + "=" + userData.getVideoID(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return affectedRow;
    }


    public void delete( String videoID ) {
        sqLiteDatabase_.delete( DatabaseHandler.TABLE_NAME, DatabaseHandler.KEY_VIDEO_ID + "=" + videoID, null );
    }


    public ArrayList<UserData> fetchData() {
        ArrayList<UserData> userDataList = new ArrayList<UserData>();
        String[] coloums = new String[] { DatabaseHandler.KEY_VIDEO_ID, DatabaseHandler.KEY_IS_VIDEO_LIKED, DatabaseHandler.KEY_IS_VIDEO_DISLIKED };
        try {
            Cursor cursor = sqLiteDatabase_.query( DatabaseHandler.TABLE_NAME, coloums, null, null, null, null,null );

            if( null != cursor && cursor.moveToFirst() ) {
                do {
                    UserData userData = getValues(cursor);
                    userDataList.add(userData);
                }while ( cursor.moveToNext() );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return userDataList;
    }


    public UserData fetchData( String videoID ) {
        Log.d( "VIDEO","FETCH DATA :: " + videoID );
        UserData userData = null;
        String[] coloums = new String[] { DatabaseHandler.KEY_VIDEO_ID, DatabaseHandler.KEY_IS_VIDEO_LIKED, DatabaseHandler.KEY_IS_VIDEO_DISLIKED };
        String selection = DatabaseHandler.KEY_VIDEO_ID + "=?";
        String[] selectionArgs = new String[] { videoID };

        try {
            Cursor cursor = sqLiteDatabase_.query( DatabaseHandler.TABLE_NAME, coloums, selection, selectionArgs, null, null,null );

            if( null != cursor && cursor.moveToFirst() ) {
                do {
                    userData = getValues( cursor );
                }while ( cursor.moveToNext() );
            }
        }
        catch ( Exception e) {
            e.printStackTrace();
        }
        Log.d( "VIDEO","FETCH DATA :: " + userData );
        return userData;
    }


    public UserData getValues( Cursor cursor ) {
        UserData userData = new UserData();

        userData.setVideoID( cursor.getString( cursor.getColumnIndex( DatabaseHandler.KEY_VIDEO_ID ) ) );
        userData.setIsLiked( ( cursor.getInt( cursor.getColumnIndex( DatabaseHandler.KEY_IS_VIDEO_LIKED ) ) ) == 1 ? true: false );
        userData.setIsDisLiked( ( cursor.getInt( cursor.getColumnIndex( DatabaseHandler.KEY_IS_VIDEO_DISLIKED ) ) ) == 1 ? true: false );
        return userData;
    }
}
