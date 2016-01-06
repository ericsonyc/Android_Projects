package com.android.openglsample.app;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by ericson on 2016/1/4 0004.
 */
public class NewGLSurfaceView extends GLSurfaceView {

    MyGLRenderer renderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320.0f;
    private float previousX;
    private float previousY;

    public NewGLSurfaceView(Context context) {
        super(context);
        renderer = new MyGLRenderer(context);
        this.setRenderer(renderer);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                renderer.speedY -= 0.1f;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                renderer.speedY += 0.1f;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                renderer.speedX -= 0.1f;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                renderer.speedX += 0.1f;
                break;
            case KeyEvent.KEYCODE_A:
                renderer.z -= 0.2f;
                break;
            case KeyEvent.KEYCODE_Z:
                renderer.z += 0.2f;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                renderer.currentTextureFilter = (renderer.currentTextureFilter + 1) % 3;
                break;
            case KeyEvent.KEYCODE_L:
                renderer.lightingEnabled = !renderer.lightingEnabled;
                break;
            case KeyEvent.KEYCODE_B:
                renderer.blendingEnabled = !renderer.blendingEnabled;
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        float deltaX, deltaY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                deltaX = currentX - previousX;
                deltaY = currentY - previousY;
                renderer.angleX += deltaY * TOUCH_SCALE_FACTOR;
                renderer.angleY += deltaX * TOUCH_SCALE_FACTOR;
        }
        previousX = currentX;
        previousY = currentY;
        return true;
    }
}
