package com.example.black.music.test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class TestButton extends View {
    public TestButton(Context context) {
        super(context);
    }

    public TestButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("eventTest","viewOnTouchEvent" + super.onTouchEvent(event));
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.v("eventTest","viewdispatchTouchEvent" + super.dispatchTouchEvent(event));
        return super.dispatchTouchEvent(event);
    }
}
