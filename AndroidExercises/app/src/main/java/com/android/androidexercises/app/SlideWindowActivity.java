package com.android.androidexercises.app;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ericson on 2015/12/8 0008.
 */
public class SlideWindowActivity extends Activity implements View.OnTouchListener {

    private static final String TAG = "SlideWindowActivity";

    LinearLayout view;
    ViewGroup root;
    TextView text;
    float lastX;
    float lastY;

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p/>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_main);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        root = (RelativeLayout)findViewById(R.id.relative);
//        RelativeLayout.LayoutParams rootparam = new RelativeLayout.LayoutParams(width, height);
        root.setBackgroundColor(Color.BLUE);
//        root.setLayoutParams(rootparam);

        Log.i(TAG, "width:" + width + ",height:" + height);
        view = new LinearLayout(this);
//        LinearLayout.LayoutParams linearparam = new LinearLayout.LayoutParams(width / 2, height / 2);
        view.setBackgroundColor(Color.BLACK);
//        view.setLayoutParams(linearparam);
        RelativeLayout.LayoutParams relativeparam = new RelativeLayout.LayoutParams(width/2, height/2);
//        relativeparam.leftMargin = 100;
//        relativeparam.rightMargin = 100;
//        relativeparam.topMargin = 100;
//        relativeparam.bottomMargin = 100;
        relativeparam.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setEnabled(true);
        view.setOnTouchListener(this);
        view.setLayoutParams(relativeparam);
        root.addView(view);
//        root.addView(view, relativeparam);
        Log.i(TAG, "" + root.getChildCount());
//        setContentView(root);
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.i(TAG, "x:" + x + ",y:" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getRawY();
                Log.i(TAG, "lastX:" + lastX + ",lastY:" + lastY);
                Log.i(TAG, "action_down");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "action_up");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TAG, "action_pointer_down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "action_move");

                break;
        }
        root.invalidate();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("test", "run onTouchEvent");
        return super.onTouchEvent(event);
    }
}
