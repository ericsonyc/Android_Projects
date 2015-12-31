package com.android.openglandroid.app;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by ericson on 2015/12/31 0031.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRender mRender = null;

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRender = new MyGLRender();
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.
    }
}
