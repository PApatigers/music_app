package com.example.black.music.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ShufflingPagerAdpater extends PagerAdapter {

    private List<ImageView> imageList;
    private ViewPager viewPager;
    public ShufflingPagerAdpater(ViewPager viewPager, List a){
        this.imageList = a;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = imageList.get(position%imageList.size());
        ViewPager parent = (ViewPager)imageView.getParent();
        Log.v("shuffing",""+position%imageList.size() + (parent == null));

        if(parent != null)
            parent.removeView(imageView);
        viewPager.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        viewPager.removeView(imageList.get(position%imageList.size()));
    }
}
