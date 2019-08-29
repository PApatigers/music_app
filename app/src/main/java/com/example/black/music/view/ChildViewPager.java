package com.example.black.music.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager {

    private float x1;       //触屏落点位置

    public ChildViewPager(@NonNull Context context) {
        super(context);
    }

    public ChildViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.v("tag",""+getParent().getClass().getName());
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true );
                x1 = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //判断是否在第一页，
                int position = getCurrentItem();
                float x2 = ev.getX();
                if(position == 0){

                    //滑动距离超过50交给父控件处理，不下发
                    Log.v("eventTest",""+(x2-x1));
                    if(Math.abs(x2-x1) > 300){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else{
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else if(position == getAdapter().getCount() -1){
                    getParent().requestDisallowInterceptTouchEvent(false);
                }else{
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
        }
        return super.dispatchTouchEvent(ev);
    }
}
