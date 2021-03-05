package com.example.user.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class ArtistsFragment extends Fragment {
    private Map<String, ArrayList<Tracks>> artistMAP;
    private RecyclerView recyclerView;
    public static ArtistAdapter artistAdapter;
    public ArtistsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        artistMAP = MainActivity.ARTISTS_LIST;

        recyclerView = view.findViewById(R.id.albums_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));

        artistAdapter = new ArtistAdapter(artistMAP, view.getContext());
        recyclerView.setAdapter(artistAdapter);
        return view ;
    }
    public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder>{
        Map<String, ArrayList<Tracks>> map;
        ArrayList<String> artistName;
        Context context;
        ArtistAdapter(Map<String, ArrayList<Tracks>> map, Context context){
            this.map = map;
            this.context = context;
            artistName = new ArrayList<>();
            for(String x : map.keySet()){
                artistName.add(x);
            }
        }
        @Override
        public ArtistHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View view  = LayoutInflater.from(getContext()).inflate(R.layout.artist_row,viewGroup, false);
            final ArtistHolder holder = new ArtistHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int x = holder.getAdapterPosition();
                    Toast.makeText(getActivity(), "pressed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), UltimateActivity.class);
                    intent.putExtra("title", artistName.get(x));
                    intent.putExtra("toolbar_title", "Artist - " + artistName.get(x));
                    MainActivity.POTENTIAL_PLAYLIST = map.get(artistName.get(x));
                    startActivity(intent);
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistHolder artistHolder, int i) {
            try {
                artistHolder.artist.setText(map.get(artistName.get(i)).get(0).getArtist());
                int x = map.get(artistName.get(i)).size();
                artistHolder.no_of_tracks.setText(x + " Tracks");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return map.size();
        }

        class ArtistHolder extends RecyclerView.ViewHolder{
            TextView artist, no_of_tracks;
            public ArtistHolder(@NonNull View itemView) {
                super(itemView);
                artist = itemView.findViewById(R.id.artist_name);
                no_of_tracks = itemView.findViewById(R.id.no_of_tracks_by_artist);
            }
        }
    }
}
