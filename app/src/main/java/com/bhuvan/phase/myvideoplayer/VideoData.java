package com.bhuvan.phase.myvideoplayer;

public class VideoData {

    private String title;
    private String id;
    private String videoURL;
    private String imageURL;


    public VideoData() {
        title = "";
        id = "";
        videoURL = "";
        imageURL = "";
    }

    public void setTitle(String name) { title = name; }
    public String getTitle() { return title; }

    public void setId(String name ) { id = name; }
    public String getId() { return id; }

    public void setVideoURL( String url ) { videoURL = url; }
    public String getVideoURL() { return videoURL; }

    public void setImageURL( String url ) { imageURL = url; }
    public String getImageURL() { return imageURL; }
}
