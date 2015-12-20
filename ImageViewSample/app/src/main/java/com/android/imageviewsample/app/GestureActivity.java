package com.android.imageviewsample.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ericson on 2015/12/19 0019.
 */
public class GestureActivity extends Activity implements View.OnTouchListener {

    private static final String TAG = "MyGesture";

    private GestureDetector mGestureDetector;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture);

        GestureListener listener = new GestureListener();
        mGestureDetector = new GestureDetector(this, listener);
        mGestureDetector.setOnDoubleTapListener(listener);
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnTouchListener(this);
        tv.setFocusable(true);
        tv.setClickable(true);
        tv.setLongClickable(true);

    }

    private class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "onSingleTapConfirmed");
            Toast.makeText(GestureActivity.this, "onSingleTapConfirmed", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap");
            Toast.makeText(GestureActivity.this, "onDoubleTap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i(TAG, "onDoubleTapEvent");
            Toast.makeText(GestureActivity.this, "onDoubleTapEvent", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.i(TAG, "onDown");
            Toast.makeText(GestureActivity.this, "onDown", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.i(TAG, "onShowPress");
            Toast.makeText(GestureActivity.this, "onShowPress", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i(TAG, "onSingleTapUp");
            Toast.makeText(GestureActivity.this, "onSingleTapUp", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll" + (e2.getX() - e1.getX()) + " " + distanceX);
            Toast.makeText(GestureActivity.this, "onScroll", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "onLongPress");
            Toast.makeText(GestureActivity.this, "onLongPress", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i(TAG, "onFling");
            Toast.makeText(GestureActivity.this, "onFling", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
