package com.example.user.musicplayer;

import android.Manifest;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MediaMetadataRetriever metadataRetriever;
    private RelativeLayout playinc_ab;
    public static FragmentManager fragmentManager;
    public static Fragment currentFragment;
    // these variables are globaly used............

    public static List<Tracks> TRACKS_LIST;
    static MediaPlayer mediaPlayer;
    public static Map<String, ArrayList<Tracks>> ALBUM_LIST;
    public static Map<String, ArrayList<Tracks>> ARTISTS_LIST;
    public static Map<String, ArrayList<Tracks>> GENRES_LIST;
    public static MusicFragment musicFragment;
    public  static IdentifyFragment identifyFragment;
    public static SearchFragment searchFragment;
    public static ArrayList<Tracks> CURRENT_PLAYING;
    public static ArrayList<Tracks> POTENTIAL_PLAYLIST;
    public static int currentIndex = 0;

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    void loadFragment(Fragment fragment){
        fragmentManager
                .beginTransaction()
                .hide(currentFragment)
                .show(fragment)
                .commit();
        currentFragment = fragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playinc_ab = findViewById(R.id.playing_ab);

        mediaPlayer = new MediaPlayer();
        TRACKS_LIST = new ArrayList<>();
        CURRENT_PLAYING = new ArrayList<>();
        POTENTIAL_PLAYLIST = new ArrayList<>();
        ALBUM_LIST = new HashMap<>();
        ARTISTS_LIST = new HashMap<>();
        GENRES_LIST = new HashMap<>();

        metadataRetriever = new MediaMetadataRetriever();

        askStoragePermission();

        fragmentManager = getSupportFragmentManager();

        musicFragment = new MusicFragment();
        currentFragment = musicFragment;
        identifyFragment = new IdentifyFragment();
        searchFragment = new SearchFragment();

        fragmentManager.beginTransaction().add(R.id.frameLayout, musicFragment, "1").commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout, searchFragment, "3").hide(identifyFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout, identifyFragment, "2").hide(searchFragment).commit();

        final BottomNavigationView navigation =  findViewById(R.id.navigationBar);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.music_bar :
                        if(navigation.getSelectedItemId() != R.id.music_bar)
                        loadFragment(musicFragment);
                        return true;
                    case R.id.identify_bar :
                        if(navigation.getSelectedItemId() != R.id.identify_bar)
                        loadFragment(identifyFragment);
                        return true;
                    case R.id.search :
                        if(navigation.getSelectedItemId() != R.id.search)
                        loadFragment(searchFragment);
                        return true;
                }
                return true;
            }
        });
    }
    public void askStoragePermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        FetchTracks fetchTracks = new FetchTracks();
                        fetchTracks.execute("xyz");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "App wont work without storage permission", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
    public void fetchTracks(File root){
        File files[] = root.listFiles();
        for(File file : files){
            if(file.isDirectory()) {
                if(file.getName().equals("Android") || file.getName().equals(".sounds"))
                    continue;
                fetchTracks(file);
            } else{
                if(file.getName().endsWith(".mp3")){
                    //String title = file.getName().substring(0, file.getName().length() - 4);
                    String path = file.getAbsolutePath();
                    metadataRetriever.setDataSource(path);
                    String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    String album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    String genre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                    String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    byte art[] = null;
                    if(title == null){
                        title = file.getName().substring(0, file.getName().length() - 4);
                    }
                    if(artist == null){
                        artist = "Unknown artist";
                    }
                    if(album == null){
                        album = "No Album";
                    }
                    if(genre == null)
                        genre = "Unknown";
                    try {
                        Mp3File mp3File = new Mp3File(path);
                        if(mp3File.hasId3v2Tag()){
                            art = mp3File.getId3v2Tag().getAlbumImage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    TRACKS_LIST.add(new Tracks(title, path, album, genre, artist, art));

                    if(ALBUM_LIST.containsKey(album)){
                        ALBUM_LIST.get(album).add(new Tracks(title, path, album, genre, artist, art));
                    } else {
                        ArrayList<Tracks> temp = new ArrayList<>();
                        temp.add(new Tracks(title, path, album, genre, artist, art));
                        ALBUM_LIST.put(album, temp);
                    }

                    if(ARTISTS_LIST.containsKey(artist)){
                        ARTISTS_LIST.get(artist).add(new Tracks(title, path, album, genre, artist, art));
                    } else {
                        ArrayList<Tracks> temp = new ArrayList<>();
                        temp.add(new Tracks(title, path, album, genre, artist, art));
                        ARTISTS_LIST.put(artist, temp);
                    }

                    if(GENRES_LIST.containsKey(genre)){
                        GENRES_LIST.get(genre).add(new Tracks(title, path, album, genre, artist, art));
                    } else {
                        ArrayList<Tracks> temp = new ArrayList<>();
                        temp.add(new Tracks(title, path, album, genre, artist, art));
                        GENRES_LIST.put(genre, temp);
                    }
                }
            }
        }
    }
    public class FetchTracks extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            fetchTracks(Environment.getExternalStorageDirectory());
            Collections.sort(TRACKS_LIST, new Tracks.SortByTitle());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
