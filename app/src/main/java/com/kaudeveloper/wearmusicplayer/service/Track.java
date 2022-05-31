package com.kaudeveloper.wearmusicplayer.service;

import android.net.Uri;

public class Track {
    private String title;
    private String name;
    private String artist;
    private int bitmapResId;
    private Uri uri;
    private long duration; // in ms

    Track(String title, String artist,String name, int bitmapResId, Uri uri, long duration) {
        this.title = title;
        this.artist = artist;
        this.name = name;
        this.bitmapResId = bitmapResId;
        this.uri = uri;
        this.duration = duration;
    }

    public   String getTitle() {
        return title;
    }

    public   String getArtist() {
        return artist;
    }
    public  String getName() {
        return name;
    }
    public   int getBitmapResId() {
        return bitmapResId;
    }

    public   Uri getUri() {
        return uri;
    }

    public long getDuration() {
        return duration;
    }

}