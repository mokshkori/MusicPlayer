package com.example.user.musicplayer;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    private Map<String, ArrayList<Tracks>> almumsMap;
    private RecyclerView recyclerView;
    public static AlbumAdapter albumAdapter;
    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        recyclerView = view.findViewById(R.id.albums_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(30);
        almumsMap = new HashMap<>();

        almumsMap = MainActivity.ALBUM_LIST;

        albumAdapter = new AlbumAdapter(almumsMap);
        recyclerView.setAdapter(albumAdapter);

        return view;
    }
    public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder>{
        Map<String, ArrayList<Tracks>> map;
        List<String> albumNames;

        AlbumAdapter(Map<String, ArrayList<Tracks>> map){
            this.map = map;
            albumNames = new ArrayList<>();
            for(String x : map.keySet()){
                albumNames.add(x);
            }
        }
        @NonNull
        @Override
        public AlbumHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.album_card, viewGroup, false);

            final AlbumHolder holder = new AlbumHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int x = holder.getAdapterPosition();
                    Toast.makeText(getActivity(), "pressed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), UltimateActivity.class);
                    intent.putExtra("title", albumNames.get(x));
                    intent.putExtra("toolbar_title", "Album");
                    MainActivity.POTENTIAL_PLAYLIST = map.get(albumNames.get(x));
                    startActivity(intent);
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumHolder albumHolder, int i) {
            byte art[] = null;
            try {
                art = almumsMap.get(albumNames.get(i)).get(0).getArt();
                if (art != null)
                    albumHolder.albumart.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
                else
                    albumHolder.albumart.setImageResource(R.drawable.no_album_art);
                albumHolder.artist.setText(almumsMap.get(albumNames.get(i)).get(0).getArtist());
                albumHolder.albumName.setText(almumsMap.get(albumNames.get(i)).get(0).getAlbum());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return almumsMap.size();
        }

        class AlbumHolder extends RecyclerView.ViewHolder{
            ImageView albumart;
            TextView albumName, artist;
            public AlbumHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setClickable(true);
                albumart = itemView.findViewById(R.id.album_card_album_art);
                albumName = itemView.findViewById(R.id.album_card_album_name);
                artist = itemView.findViewById(R.id.album_card_artist);
            }
        }
    }
}
