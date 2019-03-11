package com.bhuvan.phase.myvideoplayer;

public class UserData {
    private String videoId;
    private boolean isLiked;
    private boolean isDisLiked;

    public UserData() {
        videoId = "";
        isLiked = false;
        isDisLiked = false;
    }

    public void setVideoID(String id ) { this.videoId = id; }
    public String getVideoID() { return videoId; }

    public void setIsLiked( boolean liked ) { isLiked = liked; }
    public boolean getIsLiked() { return isLiked; }

    public void setIsDisLiked( boolean status ) { isDisLiked = status; }
    public boolean getIsDisLiked() { return isDisLiked; }
}
