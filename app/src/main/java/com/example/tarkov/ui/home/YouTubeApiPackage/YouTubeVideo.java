package com.example.tarkov.ui.home.YouTubeApiPackage;

public class YouTubeVideo {

    private String title;
    private String thumbnailUrl;
    private int duration;
    private String videoId;

    public YouTubeVideo(String title, String thumbnailUrl, String videoId) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoUrl() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }


}