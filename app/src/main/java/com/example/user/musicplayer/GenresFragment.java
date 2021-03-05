package com.example.user.musicplayer;


import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
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

public class GenresFragment extends Fragment {

    private RecyclerView recyclerView;
    private Map<String, ArrayList<Tracks>> genreMAP;
    public static  GenresAdapter genresAdapter;

    public GenresFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genres, container, false);

        genreMAP = MainActivity.GENRES_LIST;
        recyclerView  = view.findViewById(R.id.genres_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),1));
        recyclerView.setItemViewCacheSize(20);

        genresAdapter = new GenresAdapter(genreMAP);
        recyclerView.setAdapter(genresAdapter);

        return view;
    }
    public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenreHolder>{
        Map<String, ArrayList<Tracks>> map;
        ArrayList<String> genreList;
        GenresAdapter(Map<String, ArrayList<Tracks>> map){
            this.map = map;
            genreList = new ArrayList<>();
            for(String x : map.keySet()){
                genreList.add(x);
            }
        }

        @NonNull
        @Override
        public GenreHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.genres_row, viewGroup, false);
            final GenreHolder holder = new GenreHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int x = holder.getAdapterPosition();
                    Toast.makeText(getActivity(), "pressed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), UltimateActivity.class);
                    intent.putExtra("title", genreList.get(x));
                    intent.putExtra("toolbar_title", "Genre");
                    MainActivity.POTENTIAL_PLAYLIST = map.get(genreList.get(x));
                    startActivity(intent);
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull GenreHolder genreHolder, int i) {
            try {
                genreHolder.genre.setText(map.get(genreList.get(i)).get(0).getGenre());
                int x = map.get(genreList.get(i)).size();
                genreHolder.no_of_tracks.setText(x + " Tracks");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return map.size();
        }

        class GenreHolder extends RecyclerView.ViewHolder{
            TextView genre, no_of_tracks;
            public GenreHolder(@NonNull View itemView) {
                super(itemView);
                genre = itemView.findViewById(R.id.genre_name);
                no_of_tracks = itemView.findViewById(R.id.no_of_tracks_by_genre);
            }
        }
    }

}
