package com.bhuvan.phase.myvideoplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;

    LinearLayout secondView_;

    String videoURL = ""; //https://media0.giphy.com/media/rwNpHtaMGnStW/giphy.mp4"; //"https://blueappsoftware.in/layout_design_android_blog.mp4";
    String vName = "";
    String id = "";
    TextView videoName, totalhits;

    ImageView likeImage, dislikeImage;

    UserData userData = null;

    SharedPreferences.Editor editor = null;
    SharedPreferences prefs = null;

    int totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        editor = getSharedPreferences( "MYAPP", Context.MODE_PRIVATE).edit();
        prefs = getSharedPreferences( "MYAPP",Context.MODE_PRIVATE);

        if( null != prefs )
            totalCount = prefs.getInt( "Number", 0 );

        Bundle bundle = getIntent().getExtras();
        if( null != bundle ) {
            videoURL = bundle.getString( "URL" );
            vName = bundle.getString( "Name" );
            id = bundle.getString( "ID" );
        }

        secondView_ = (LinearLayout) findViewById( R.id.secondView );
        videoName = (TextView) findViewById( R.id.videoName );
        videoName.setText( vName );

        totalhits = (TextView) findViewById( R.id.totalHit );
        totalhits.setText( "Total hits :: " + totalCount );

        likeImage = (ImageView) findViewById( R.id.likeButton );
        likeImage.setOnClickListener( this );
        dislikeImage = (ImageView) findViewById( R.id.unlikeButton );
        dislikeImage.setOnClickListener( this );

        try {
            exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayerView);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            Uri videoURI = Uri.parse(videoURL);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        userData = Initializer.databaseManager_.fetchData( id );

        if( null == userData ) {
            Log.d( "VIDEO","NULL DATABASE" );
            userData = new UserData();
            userData.setVideoID( id );
        }
        else {
            Log.d( "VIDEO","GET DATABASE" );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if( userData.getIsDisLiked() ) {
            dislikeImage.setImageResource( R.drawable.ic_like );
        }
        else {
            dislikeImage.setImageResource( R.drawable.ic_unlike );
        }

        if( userData.getIsLiked() ) {
            likeImage.setImageResource( R.drawable.ic_like );
        }
        else {
            likeImage.setImageResource( R.drawable.ic_unlike );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.stop();
        exoPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();

        exoPlayer.stop();
        exoPlayer.release();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            secondView_.setVisibility( View.GONE );
        }
        else if( newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ) {
            secondView_.setVisibility( View.VISIBLE );
        }
    }

    @Override
    public void onClick(View view) {

        if( view.getId() == R.id.likeButton ) {
            Log.d("VIDEO", "Like button" );

            if( !userData.getIsLiked() ) {
                likeImage.setImageResource(R.drawable.ic_like);
                userData.setIsLiked( true );

                if (userData.getIsDisLiked()) {
                    userData.setIsDisLiked(false);
                    dislikeImage.setImageResource(R.drawable.ic_unlike);
                }
            }
            else {
                likeImage.setImageResource(R.drawable.ic_unlike );
                userData.setIsLiked( false );
            }
        }
        else if( view.getId() == R.id.unlikeButton ) {
            Log.d("VIDEO", "Dislike button" );

            if( !userData.getIsDisLiked() ) {
                dislikeImage.setImageResource(R.drawable.ic_like);
                userData.setIsDisLiked( true );

                if (userData.getIsLiked()) {
                    userData.setIsLiked(false);
                    likeImage.setImageResource(R.drawable.ic_unlike);
                }
            }
            else {
                dislikeImage.setImageResource(R.drawable.ic_unlike);
                userData.setIsDisLiked( false );
            }
        }

        int affectedRow = Initializer.databaseManager_.update( userData );
        Log.d( "VIDEO","Affectedrow ::" + affectedRow );
        if( affectedRow == 0 ) {
            Initializer.databaseManager_.insert( userData );
        }

        ++totalCount;
        editor.putInt( "Number", totalCount );
        editor.apply();

        totalhits.setText( "Total hits :: " + totalCount );
    }
}
