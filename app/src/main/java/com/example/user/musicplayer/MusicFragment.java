package com.example.user.musicplayer;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

public class MusicFragment extends Fragment {

    private TabLayout mTabLayout;
    private PagerAdapter adapter;
    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.music_tab_layout);
        final ViewPager mPager = (ViewPager) view.findViewById(R.id.viewPager);
        adapter = new PagerAdapter(getFragmentManager(), mTabLayout.getTabCount());

        mTabLayout.setupWithViewPager(mPager);
        mPager.setAdapter(adapter);
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
}
