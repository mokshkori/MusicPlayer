package com.example.user.musicplayer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TracksFragment extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView tracksView;
    public static  TrackAdapter tracksAdapter;
    private List<Tracks> tracksList;
    private MediaPlayer player;
    private FastScroller fastScroller;
    public TracksFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);
        tracksList = MainActivity.TRACKS_LIST;

        tracksAdapter = new TrackAdapter(getContext() , tracksList);
        tracksView = view.findViewById(R.id.tracksView);
        tracksView.setHasFixedSize(true);
        tracksView.setItemViewCacheSize(20);
        tracksView.setDrawingCacheEnabled(true);
        tracksView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        tracksView.setAdapter(tracksAdapter);

        fastScroller = view.findViewById(R.id.fastscroll);
        fastScroller.setRecyclerView(tracksView);
        player = MainActivity.mediaPlayer;
        return view;
    }
    public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> implements SectionTitleProvider {
        private List<Tracks> list;
        Context context;
        public TrackAdapter(Context context, List<Tracks> list){
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.tracks_row, viewGroup, false);
            final ViewHolder viewHolder = new ViewHolder(view);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String path = list.get(viewHolder.getAdapterPosition()).getPath();

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
                        viewHolder.isPlaying.setImageResource(R.drawable.sound_bars);
                        Intent intent = new Intent(getContext(), CurrentPlaying.class);
                        intent.putExtra("Index", viewHolder.getAdapterPosition());
                        MainActivity.CURRENT_PLAYING = (ArrayList<Tracks>) MainActivity.TRACKS_LIST;
                        startActivity(intent);
                        Toast.makeText(getActivity(), "playing....", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "some error occured", Toast.LENGTH_SHORT).show();
                        player.reset();
                    }
                }
            });
            viewHolder.optionMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getActivity(), viewHolder.optionMenu);
                    menu.inflate(R.menu.track_option_menu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch(id){
                                case R.id.play_option : view.performClick();
                                    return true;
                                case R.id.add_to_fav :
                                    Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.add_to_playlis :
                                    Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                                    PlaylistDB db = new PlaylistDB(getActivity());
                                    db.addTrack(tracksList.get(viewHolder.getAdapterPosition()));
                                    return true;
                                case R.id.delete_option :
                                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                                default: return false;
                            }
                        }
                    });

                    menu.show();
                }
            });
            return viewHolder ;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            viewHolder.trackName.setText(list.get(i).getTitle());
            viewHolder.artist.setText(list.get(i).getArtist());
            if(tracksList.get(i).isPlaying())
            viewHolder.isPlaying.setImageResource(R.drawable.sound_bars);
            else
                viewHolder.isPlaying.setImageBitmap(null);

            if(list.get(i).getArt() == null)
                viewHolder.thumbNail.setImageResource(R.drawable.no_album_art);
            else{
                byte art[] = list.get(i).getArt();
                Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                viewHolder.thumbNail.setImageBitmap(bitmap);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public String getSectionTitle(int position) {
            return tracksList.get(position).getTitle().substring(0,1).toUpperCase(Locale.US);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView trackName;
            private TextView artist;
            private ImageView thumbNail;
            private ImageView isPlaying;
            private ImageView optionMenu;

            public ViewHolder(@NonNull View itemView) {
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
