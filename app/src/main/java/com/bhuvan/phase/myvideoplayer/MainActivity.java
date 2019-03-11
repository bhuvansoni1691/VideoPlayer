package com.bhuvan.phase.myvideoplayer;


import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DownloadVideo downloadVideo;
    ViewGroup mainViewGroup = null;

    ProgressBar progressBar;

    ConnectivityManager connectivityManager_;
    NetworkInfo networkInfo = null;

    MainActivity mainActivity_;

    static final String DEFAULT_ULR = "http://api.giphy.com/v1/gifs/search?q=ryan+gosling&api_key=IoHKAIl8UCj6qnPS18lgEho9OSeBcUQI&limit=5";
    String firedURL = DEFAULT_ULR;

    // Map of name and video data
    // public static HashMap<String,VideoData> videoMap = new HashMap<String,VideoData>();
    public static ArrayList<VideoData> videoDataList = new ArrayList<VideoData>();

    Initializer initializer_ = null;

    MyBroadcastReceiver myBroadcastReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity_ = this;

        progressBar = (ProgressBar) findViewById( R.id.progressBar );

        initializer_ = new Initializer( this );

        connectivityManager_ = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        mainViewGroup = (ViewGroup) findViewById( R.id.mainView );


        myBroadcastReceiver = new MyBroadcastReceiver( this );
        registerStateChangeListener();

        if( !checkNetworkUpdate() ) {
            launchIdleFragment();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        initializer_.deInitialize();
    }


    void registerStateChangeListener() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver( myBroadcastReceiver, intentFilter );
    }


    void launchVideoFragment() {
        if( null != mainViewGroup ) {
            VideoFragment videoFragment = new VideoFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(mainViewGroup.getId(),videoFragment, VideoFragment.class.getName());
            fragmentTransaction.commitAllowingStateLoss();
        }
    }


    void launchIdleFragment() {
        if( null != mainViewGroup ) {
            IdleFragment idleFragment = new IdleFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(mainViewGroup.getId(),idleFragment, IdleFragment.class.getName());
            fragmentTransaction.commitAllowingStateLoss();
        }
    }


    public boolean checkNetworkUpdate() {
        networkInfo = connectivityManager_.getActiveNetworkInfo();
        boolean isNetConnected = networkInfo != null && networkInfo.isConnected();

        Log.d( "VIDEO", "Is Network Connected :: " + isNetConnected );
        return isNetConnected;
    }


    private class DownloadVideo extends AsyncTask<Void, Void, Void> {
        StringBuffer stringBuffer = new StringBuffer();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE );
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL( firedURL );

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout( 32000 );

                int responseCode = httpURLConnection.getResponseCode();
                Log.d( "VIDEO","Response Code :" + responseCode );
                if( responseCode == HttpURLConnection.HTTP_OK ) {
                    InputStream inputStream = new BufferedInputStream( httpURLConnection.getInputStream() );
                    InputStreamReader inputStreamReader = new InputStreamReader( inputStream );
                    BufferedReader bufferedReader = new BufferedReader( inputStreamReader );

                    String line = bufferedReader.readLine();

                    while( line != null ) {
                        stringBuffer.append( line );
                        line = bufferedReader.readLine();
                    }
                    // Here we need to parse the data which is in JSON.
                    parseData( stringBuffer.toString() );

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE );
            launchVideoFragment();
        }
    }


    void handleNetworkChangeEvent() {

        if( checkNetworkUpdate() ) {
            launchVideoFragment();

            downloadVideo = new DownloadVideo();
            downloadVideo.execute();
        }
        else {
            launchIdleFragment();
        }
    }


    void parseData( String data ) {
        // Get the object of JSON string
        Log.d( "VIDEO","PARSE JSON" );
        Log.d( "VIDEO","DATA :" + data );

        if( null != data ) {
            try {
                videoDataList.clear();
                JSONObject jsonObject = new JSONObject(data);

                // Get the array from object
                JSONArray jsonArray = jsonObject.getJSONArray( "data" );

                Log.d( "VIDEO","JSON Length :: " + jsonArray.length() );

                for( int i=0; i<jsonArray.length(); i++ ) {
                    // Get the object from array
                    JSONObject dataObj = jsonArray.getJSONObject(i);

                    // Get the image Title
                    String title = dataObj.getString( "title" );
                    String id = dataObj.getString( "id" );

                    // Get the IMAGE object from dataObj
                    JSONObject imageObj = dataObj.getJSONObject( "images" );

                    // Get the mp4 object from imageObj
                    JSONObject mp4Obj = imageObj.getJSONObject( "original_mp4" );
                    String mp4URL = mp4Obj.getString("mp4" );

                    // Get the 480 image obj from imageObj
                    JSONObject image480 = imageObj.getJSONObject( "480w_still" );
                    String imageURL = image480.getString( "url" );

                    Log.d( "VIDEO", "Title :: " + title + "\n" + "ID :: " + id + "\n" + "MP4_URL ::" + mp4URL + "\n" + "Image :: " + imageURL );
                    Log.d( "VIDEO", "==============================" );

                    VideoData videoData = new VideoData();
                    videoData.setId( id );
                    videoData.setVideoURL( mp4URL );
                    videoData.setImageURL( imageURL );
                    videoData.setTitle( title );

                    videoDataList.add( videoData );
                }
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.searchview,menu );

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView( menu.findItem( R.id.action_search ) );
        SearchManager searchManager = (SearchManager) getSystemService( SEARCH_SERVICE );
        searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("VIDEO", "Search text :: " + s );
                firedURL ="http://api.giphy.com/v1/gifs/search?q=ryan+gosling&api_key=IoHKAIl8UCj6qnPS18lgEho9OSeBcUQI&q="+ s +"&limit=5";
                downloadVideo = new DownloadVideo();
                downloadVideo.execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("VIDEO", "Text change :: " + s );
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
