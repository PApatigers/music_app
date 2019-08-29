package com.example.black.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class HotListPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;

    public HotListPagerAdapter(FragmentManager fragmentManager , List<Fragment> list){
        super(fragmentManager);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
