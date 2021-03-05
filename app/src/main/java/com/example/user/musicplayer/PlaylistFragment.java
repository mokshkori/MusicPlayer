package com.example.user.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;


public class PlaylistFragment extends Fragment {
    List<Tracks> playlists ;
    RecyclerView playlistView;
    MediaPlayer player;
    private PlaylistAdapter adapter;
    private PlaylistDB db;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_playlist, container, false);
        db = new PlaylistDB(getContext());

        player = MainActivity.mediaPlayer;

        playlists =  db.getAllTracks();
        playlistView = view.findViewById(R.id.playlistRecyclerView);
        playlistView.setItemViewCacheSize(20);
        playlistView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistView.setHasFixedSize(true);
        adapter = new PlaylistAdapter(getContext(), playlists);
        playlistView.setAdapter(adapter);
        return view;
    }
    class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {
        List<Tracks> list;
        Context context;
        PlaylistAdapter(Context context, List<Tracks> list){
            this.context = context;
            this.list = list;
        }
        @NonNull
        @Override
        public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.tracks_row, viewGroup, false);
            final PlaylistHolder playlistHolder = new PlaylistHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = list.get(playlistHolder.getAdapterPosition()).getPath();
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
                        playlistHolder.isPlaying.setImageResource(R.drawable.sound_bars);
                        Intent intent = new Intent(getContext(), CurrentPlaying.class);
                        intent.putExtra("Index", playlistHolder.getAdapterPosition());
                        MainActivity.CURRENT_PLAYING = (ArrayList<Tracks>) list;
                        startActivity(intent);
                        Toast.makeText(getActivity(), "playing....", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "some error occured", Toast.LENGTH_SHORT).show();
                        player.reset();
                    }
                }
            });
            playlistHolder.optionMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getActivity(), playlistHolder.optionMenu);
                    menu.inflate(R.menu.playlist_option_menu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch(id){
                                case R.id.playlist_play_option : view.performClick();
                                    return true;
                                case R.id.playlist_delete_option :
                                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                                    db.deleteTrack(list.get(playlistHolder.getAdapterPosition()));
                                    playlists = db.getAllTracks();
                                    list = db.getAllTracks();
                                    adapter.notifyDataSetChanged();
                                    return true;
                                default: return false;
                            }
                        }
                    });
                    menu.show();
                }
            });
            return playlistHolder ;
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistHolder playlistHolder, int i) {
            playlistHolder.trackName.setText(list.get(i).getTitle());
            playlistHolder.artist.setText(list.get(i).getArtist());
            if(list.get(i).isPlaying())
                playlistHolder.isPlaying.setImageResource(R.drawable.sound_bars);
            else
                playlistHolder.isPlaying.setImageBitmap(null);

            if(list.get(i).getArt() == null)
                playlistHolder.thumbNail.setImageResource(R.drawable.no_album_art);
            else{
                byte art[] = list.get(i).getArt();
                Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                playlistHolder.thumbNail.setImageBitmap(bitmap);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class PlaylistHolder extends RecyclerView.ViewHolder {
            private TextView trackName;
            private TextView artist;
            private ImageView thumbNail;
            private ImageView isPlaying;
            private ImageView optionMenu;

            public PlaylistHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setClickable(true);
                itemView.setLongClickable(true);
                trackName = itemView.findViewById(R.id.track_name);
                artist = itemView.findViewById(R.id.artist);
                thumbNail = itemView.findViewById(R.id.track_thumbnail);
                isPlaying = itemView.findViewById(R.id.playing_icon);
                optionMenu = itemView.findViewById(R.id.track_option_menu);
                optionMenu.setClickable(true);
            }
        }
    }
}
