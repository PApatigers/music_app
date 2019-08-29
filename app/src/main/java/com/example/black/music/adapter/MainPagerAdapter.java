package com.example.black.music.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> list ;
    private List<String> tabList;

    public MainPagerAdapter(FragmentManager fragmentManager , List<Fragment> list){
        super(fragmentManager);
        this.list = list;
    }

    public MainPagerAdapter(FragmentManager fragmentManager , List<Fragment> list , List<String> tabList){
        super(fragmentManager);
        this.list = list;
        this.tabList = tabList;
    }
    @Override
    public Fragment getItem(int i) {
        return list.get (i);
    }

    @Override
    public int getCount() {
        return list!=null ? list.size () : 0 ;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position );
    }
}
