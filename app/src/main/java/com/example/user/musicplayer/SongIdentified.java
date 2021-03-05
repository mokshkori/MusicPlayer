package com.example.user.musicplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SongIdentified extends AppCompatActivity {

    private TextView songTitle, songArtist, lyricsView;
    private AppCompatImageView youtube, spotify, download;
    private Intent intent;
    private String title, artist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_identified);

        songTitle = (TextView) findViewById(R.id.identified_song_title);
        songArtist = (TextView) findViewById(R.id.identified_song_artist);
        lyricsView = (TextView) findViewById(R.id.identified_lyrics_view);

        youtube = (AppCompatImageView) findViewById(R.id.you_tube);
        spotify = (AppCompatImageView) findViewById(R.id.spotify);
        download = (AppCompatImageView) findViewById(R.id.download);

        intent = getIntent();

        title = intent.getStringExtra("title");
        artist = intent.getStringExtra("artist");

        songArtist.setText(artist);
        songTitle.setText(title);

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(v);
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadLinks(v);
            }
        });
    }
    public void playVideo(View view){
        String restLink = "";
        for(String x : title.split(" ")){
            restLink += x + "+";
        }
        for(String x : artist.split(" ")){
            restLink += x + "+";
        }
        Log.i("bnm", restLink);
        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + restLink.substring(0, restLink.length()-1)));
        startActivity(mIntent);
    }
    public void downloadLinks(View view){
        String restLink = "";
        for(String x : title.split(" ")){
            restLink += x + "+";
        }
        for(String x : artist.split(" ")){
            restLink += x + "+";
        }
        Log.i("bnm", restLink);
        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://musicpleer.la/#!" + restLink.substring(0, restLink.length()-1)));
        startActivity(mIntent);
    }
}
