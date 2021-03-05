package com.example.user.musicplayer;

import android.graphics.Bitmap;

import java.util.Comparator;

public class Tracks {
    private String title, path, album, genre, artist;
    private byte[] art;
    private boolean playing;

    Tracks(){
        art = null;
        playing = false;
    }
    public Tracks(String title, String path, String album, String genre, String artist, byte[] art) {
        this.title = title;
        this.path = path;
        this.album = album;
        this.genre = genre;
        this.artist = artist;
        this.art = art;
        this.playing = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public byte[] getArt() {
        return art;
    }

    public void setArt( byte[] art) {
        this.art = art;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public String toString() {
        return title;
    }

    static class SortByTitle implements Comparator<Tracks>{

        @Override
        public int compare(Tracks o1, Tracks o2) {
            return o1.title.compareToIgnoreCase(o2.title);
        }
    }
}
