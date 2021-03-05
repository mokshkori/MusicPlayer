package com.example.user.musicplayer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class CurrentPlaying extends AppCompatActivity {

    private ArrayList<Tracks> playing_list;
    private Toolbar toolbar;
    private String title = "";
    private String artist = "";
    private int currentPlayingSongIndex;
    private byte art[] = null;
    public MediaPlayer player;

    private ImageView playButton, nextButton, previousButton, albumArt;
    private SeekBar seekBar;
    private TextView titleView, artistView, currentPositionView, durationView, lyricsView, repeat;

    private boolean isInLoop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_playing);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);

        player = MainActivity.mediaPlayer;

        playing_list = MainActivity.CURRENT_PLAYING;

        playButton = findViewById(R.id.play_button);
        nextButton =  findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        seekBar = findViewById(R.id.seekbar);
        titleView = findViewById(R.id.current_playing_title);
        artistView =  findViewById(R.id.current_playing_artist);
        currentPositionView = findViewById(R.id.current_position);
        durationView = findViewById(R.id.duration);
        repeat = findViewById(R.id.repeat);
        albumArt = findViewById(R.id.album_art);

        Intent intent = getIntent();
        currentPlayingSongIndex = intent.getIntExtra("Index", 0);
        title = playing_list.get(currentPlayingSongIndex).getTitle();
        artist = playing_list.get(currentPlayingSongIndex).getArtist();
        art = playing_list.get(currentPlayingSongIndex).getArt();

        titleView.setText(title);
        artistView.setText(artist);


        if (art == null) {
            albumArt.setImageResource(R.drawable.no_album_art);
        } else {
            albumArt.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
        }

        seekBar.setMax(player.getDuration());
        seekBar.setProgress(player.getCurrentPosition());
        durationView.setText(getTime(player.getDuration()));


        if (player.isLooping()) {
            repeat.setTextColor(getResources().getColor(R.color.Black));
        } else {
            repeat.setTextColor(getResources().getColor(R.color.Grey));
        }

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seekBar.setProgress(player.getCurrentPosition());
            }
        }, 0, 1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int x = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    x = progress;
                    currentPositionView.setText(getTime(progress));
                }
                currentPositionView.setText(getTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(x);
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (currentPlayingSongIndex < playing_list.size()) {
                    playNext();
                } else {
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isLooping()) {
                    player.setLooping(false);
                    repeat.setTextColor(getResources().getColor(R.color.Grey));
                } else {
                    player.setLooping(true);
                    repeat.setTextColor(getResources().getColor(R.color.Black));
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevious();
            }
        });

    }

    public void play(View view) {
        if (player.isPlaying()) {
            player.pause();
            playButton.setImageResource(android.R.drawable.ic_media_play);
        } else {
            player.start();
            playButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    public String getTime(int miliSec) {
        String minutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(miliSec));
        int sec = (int) (TimeUnit.MILLISECONDS.toSeconds(miliSec) % 60);
        String seconds = "";
        if (sec < 10) {
            seconds = "0" + sec;
        } else {
            seconds = "" + sec;
        }
        return minutes + ":" + seconds;
    }

    public void updateUI() {
        titleView.setText(playing_list.get(currentPlayingSongIndex).getTitle());
        artistView.setText(playing_list.get(currentPlayingSongIndex).getArtist());
        byte[] art = playing_list.get(currentPlayingSongIndex).getArt();
        if (art != null)
            albumArt.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
        else
            albumArt.setImageResource(R.drawable.no_album_art);
        currentPositionView.setText(getTime(player.getCurrentPosition()));
        durationView.setText(getTime(player.getDuration()));
        seekBar.setMax(player.getDuration());
    }

    public void playNext() {
        if (currentPlayingSongIndex < playing_list.size()-1) {
            currentPlayingSongIndex++;
            try {
                player.reset();
                player.setDataSource(playing_list.get(currentPlayingSongIndex).getPath());
                player.prepare();
                player.start();
                updateUI();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                player.reset();
            }
        }
        else {
            currentPlayingSongIndex  = -1;
            playNext();
        }
    }
    public void playPrevious(){
        if (currentPlayingSongIndex > 0) {
            currentPlayingSongIndex--;
            try {
                player.reset();
                player.setDataSource(playing_list.get(currentPlayingSongIndex).getPath());
                player.prepare();
                player.start();
                updateUI();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            currentPlayingSongIndex--;
            playNext();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
