package com.android.openglsample.app;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by ericson on 2016/1/2 0002.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private MyRender render;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;



    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        render = new MyRender();
        setRenderer(render);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                if (y > getHeight() / 2) {
                    dx = dx * -1;
                }
                if (x < getWidth() / 2) {
                    dy = dy * -1;
                }
                render.setAngle(render.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
                break;
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }


}
