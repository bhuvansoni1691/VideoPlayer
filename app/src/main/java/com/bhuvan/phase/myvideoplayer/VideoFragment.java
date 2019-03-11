package com.bhuvan.phase.myvideoplayer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    GridView gridView;
    GridViewAdapter gridViewAdapter;

    static VideoFragment videoFragment;

    public static VideoFragment getInstance() {
        return videoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        Log.d( "VIDEO","LAUNCH_VIDEO_FRAGMENT" );
        videoFragment = this;

        gridView = (GridView) view.findViewById( R.id.videoView );
        gridViewAdapter = new GridViewAdapter( getActivity(), R.layout.imagelayout );
        gridView.setAdapter( gridViewAdapter );

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        gridViewAdapter.notifyDataSetChanged();
    }

    class GridHolder {
        ImageView imageView;
        TextView textView;
    }


    class GridViewAdapter extends ArrayAdapter<String> {
        ArrayList<VideoData> videoData = new ArrayList<VideoData>();
        LayoutInflater layoutInflater;

        public GridViewAdapter(@NonNull Context context, int resource) {
            super(context, resource);
            Log.d( "VIDEO","GRID VIEW" );
            videoData = MainActivity.videoDataList;
            Log.d( "VIDEO","GRID VIEW :: " + videoData.size() );
            layoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        }

        @Override
        public int getCount() {
            return videoData.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Log.d( "VIDEO","VIEW :: " + position );

            GridHolder gridHolder = new GridHolder();

            if( null == convertView ) {
                convertView = layoutInflater.inflate( R.layout.imagelayout, parent, false );
            }

            gridHolder.imageView = (ImageView) convertView.findViewById( R.id.videoImage );
            gridHolder.imageView.setTag( position );
            gridHolder.imageView.setOnTouchListener( onTouchListener );

            gridHolder.textView = (TextView) convertView.findViewById( R.id.videoImageText );

            VideoData data = videoData.get( position );

            gridHolder.textView.setText( data.getTitle() );

            DownloadImageTask downloadImageTask = new DownloadImageTask( gridHolder.imageView );
            downloadImageTask.execute( data.getImageURL() );

            return convertView;
        }



        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            videoData = MainActivity.videoDataList;
        }

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if( motionEvent.getAction() == MotionEvent.ACTION_UP ) {
                    int position = (int) view.getTag();

                    Intent intent = new Intent(getActivity(), VideoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Name", videoData.get(position).getTitle());
                    intent.putExtra("URL", videoData.get(position).getVideoURL());
                    intent.putExtra( "ID", videoData.get(position).getId());
                    getActivity().startActivity(intent);
                }

                return true;
            }
        };


        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

            ImageView imageView;


            public DownloadImageTask( ImageView bmImageview ) {
                imageView = bmImageview;
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = strings[0];

                Bitmap bitmap = null;
                try {
                    InputStream inputStream = new URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream( inputStream );
                }
                catch (Exception e) {

                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageView.setImageBitmap( bitmap );
            }
        }
    }

}
