package com.android.androidexercises.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by ericson on 2016/1/19 0019.
 */
public class FrameLayoutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame2);
        FrameLayout frame=(FrameLayout)findViewById(R.id.mylayout);
        final MeziView mezi=new MeziView(FrameLayoutActivity.this);
        mezi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mezi.bitmapX=event.getX()-150;
                mezi.bitmapY=event.getY()-150;
                mezi.invalidate();
                return true;
            }
        });
        frame.addView(mezi);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
