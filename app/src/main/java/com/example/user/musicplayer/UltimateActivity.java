package com.example.user.musicplayer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UltimateActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  Toolbar toolbar;
    private ArrayList<Tracks> current_list;
    private ImageView albumArt;
    private TextView textView, no_of_tracks;
    private  UltimateAdapter adapter;
    private MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimate);

        recyclerView =  findViewById(R.id.ultimate_rv);
        toolbar = findViewById(R.id.ultimate_toolbar);
        textView = findViewById(R.id.ultimate_title);
        no_of_tracks = findViewById(R.id.no_of_tracks);
        albumArt = findViewById(R.id.ultimate_albumart);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        current_list = MainActivity.POTENTIAL_PLAYLIST;
        player = MainActivity.mediaPlayer;

        adapter = new UltimateAdapter(current_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        getSupportActionBar().setTitle(intent.getStringExtra("toolbar_title"));
        textView.setText(title);
        if(current_list.size() != 1)
            no_of_tracks.setText(current_list.size() + " Tracks");
        else
            no_of_tracks.setText("1 Track");

        byte art[] = current_list.get(0).getArt();
        if(art != null){
            albumArt.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
        }
    }
    public class UltimateAdapter extends RecyclerView.Adapter<UltimateAdapter.Holder>{
        ArrayList<Tracks> list;
        UltimateAdapter(ArrayList<Tracks> list){
            this.list = list;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ultimate_row,viewGroup, false);
            final Holder holder = new Holder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "pressed", Toast.LENGTH_SHORT).show();
                    String path = list.get(holder.getAdapterPosition()).getPath();

                    try {
                        if (player.isPlaying()) {
                            player.reset();
                            player.setDataSource(path);
                            player.prepare();
                            player.start();
                        } else {

                            player.setDataSource(path);
                            player.prepare();
                            player.start();
                        }
                        Intent intent = new Intent(getApplicationContext(), CurrentPlaying.class);
                        intent.putExtra("Index", holder.getAdapterPosition());
                        MainActivity.CURRENT_PLAYING = current_list;
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "playing....", Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int i) {
            holder.title.setText(list.get(i).getTitle());
            holder.artist.setText(list.get(i).getArtist());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class Holder extends RecyclerView.ViewHolder{
            TextView title, artist;
            public Holder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.uf_rv_title);
                artist = itemView.findViewById(R.id.uf_rv_artist);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
