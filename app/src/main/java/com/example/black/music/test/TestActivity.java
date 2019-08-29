package com.example.black.music.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.black.music.R;

public class TestActivity extends Activity {

    private ViewGroup viewGroup;
    private View testBt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        viewGroup = findViewById(R.id.viewgroup);
        testBt = findViewById(R.id.testbt);

        viewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        testBt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;

                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean tmp = super.onTouchEvent(event);
        Log.v("eventTest","activityOnTouchEvent" + tmp);
        return tmp;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.v("eventTest","activitydispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }
}
