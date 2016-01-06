package com.android.openglsample.app;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * Created by ericson on 2016/1/3 0003.
 */
public class MyGLActivity extends Activity {
    private GLSurfaceView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        glView = new GLSurfaceView(this);
//        glView.setRenderer(new MyGLRenderer(this));
        glView=new NewGLSurfaceView(this);
        this.setContentView(glView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }
}
