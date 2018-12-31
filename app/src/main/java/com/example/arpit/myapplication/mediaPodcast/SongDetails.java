package com.example.arpit.myapplication.mediaPodcast;

import android.net.Uri;

import java.io.Serializable;

public class SongDetails implements Serializable {
    String songName;
    String artist;
    String location;
    Long albumArtId;

    SongDetails(){}
       public SongDetails(String songName, String artist, String location,Long albumArtId){
        this.songName = songName;
        this.artist = artist;
        this.location = location;
        this.albumArtId = albumArtId;
    }

    public String getSongName(){
        return songName;
    }

    public String getArtist() {
        return artist;
    }

    public String getLocation() {
        return location;
    }

    public Long getAlbumArtId(){
        return albumArtId;
    }
}
