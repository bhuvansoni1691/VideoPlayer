package com.bhuvan.phase.myvideoplayer;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class Initializer {

    static DatabaseManager databaseManager_;


    Context context_ = null;


    public Initializer(Context context) {

        context_ = context;
        Log.d( "VIDEO","INITIALIZER" );

        databaseManager_ = new DatabaseManager( context );
        databaseManager_.open();


    }





    void deInitialize() {
        databaseManager_.close();
    }
}
