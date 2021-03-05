package com.example.user.musicplayer;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    Fragment trackFragment ;
    private int tabCount;
    String tabs[] = {"Albums", "Artists", "genres", "tracks", "playlists"};
    PagerAdapter(FragmentManager manager, int tabCount){
        super(manager);
        this.tabCount = tabCount;
        trackFragment = new TracksFragment();
    }
    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch (i){
            case 0 : fragment = new AlbumFragment();
                return  fragment;
            case 1 : fragment = new ArtistsFragment();
                return  fragment;
            case 2 : fragment = new GenresFragment();
                return fragment;
            case 3 : return trackFragment;
            case 4 : fragment = new PlaylistFragment();
                return fragment;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }
}
