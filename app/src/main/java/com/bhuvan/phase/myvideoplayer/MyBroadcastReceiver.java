package com.bhuvan.phase.myvideoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class MyBroadcastReceiver extends BroadcastReceiver {

    MainActivity mainActivity_ = null;

    public MyBroadcastReceiver(Context context_) {}


    public MyBroadcastReceiver( MainActivity main) {
        mainActivity_ = main;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals( ConnectivityManager.CONNECTIVITY_ACTION ) ) {
            if( null != mainActivity_ ) {
                mainActivity_.handleNetworkChangeEvent();
            }
        }
    }
}
