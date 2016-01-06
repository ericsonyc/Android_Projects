package com.android.openglsample.app;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ericson on 2016/1/3 0003.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
    Context context;
    MyTriangle triangle;
    MySquare square;

    Pyramid pyramid;
    Cube cube;
    TextureCube textureCube;
    PhotoCube photoCube;

    private float angleTriangle = 0.0f;
    private float angleSquare = 0.0f;
    private float speedTriangle = 0.5f;
    private float speedSquare = -0.4f;

    private static float anglePyramid = 0;
    private static float angleCube = 0;
    private static float speedPyramid = 2.0f;
    private static float speedCube = -1.5f;

    float angleX = 0;
    float angleY = 0;
    float speedX = 0;
    float speedY = 0;
    float z = -6.0f;

    int currentTextureFilter = 0;

    boolean lightingEnabled = false;
    private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightPosition = {0.0f, 0.f, 2.0f, 1.0f};

    boolean blendingEnabled = false;

    public MyGLRenderer(Context context) {
        this.context = context;
        triangle = new MyTriangle();
        square = new MySquare();
        pyramid = new Pyramid();
        cube = new Cube();
        textureCube = new TextureCube();
        photoCube = new PhotoCube(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GLES20.GL_DEPTH_TEST);
        gl.glDepthFunc(GLES20.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);

        textureCube.loadTexture(gl, context);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        photoCube.loadTexture(gl);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition, 0);
        gl.glEnable(GL10.GL_LIGHT1);
        gl.glEnable(GL10.GL_LIGHT0);

        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;
        float aspect = (float) width / height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (lightingEnabled) {
            gl.glEnable(GL10.GL_LIGHTING);
        } else {
            gl.glDisable(GL10.GL_LIGHTING);
        }

        if (blendingEnabled) {
            gl.glEnable(GL10.GL_BLEND);
            gl.glDisable(GL10.GL_DEPTH_TEST);
        } else {
            gl.glDisable(GL10.GL_BLEND);
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

        gl.glLoadIdentity();
        gl.glTranslatef(-1.5f, 0.0f, 0.0f);
        gl.glScalef(0.8f, 0.8f, 0.8f);
//        gl.glRotatef(anglePyramid, 1.0f, 1.0f, 1.0f);
//        triangle.draw(gl);

//        pyramid.draw(gl);
        gl.glTranslatef(0.0f, 0.0f, z);
        gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f);
        photoCube.draw(gl);
        angleX += speedX;
        angleY += speedY;

        gl.glLoadIdentity();
        gl.glTranslatef(1.5f, 0.0f, -6.0f);
//        gl.glRotatef(angleSquare, 1.0f, 0.0f, 0.0f);

        gl.glTranslatef(3.0f, 0.0f, 0.0f);
//        square.draw(gl);

//        gl.glScalef(0.8f, 0.8f, 0.8f);
//        gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f);
        gl.glTranslatef(0.0f, 0.0f, z);
        gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f);
        textureCube.draw(gl, currentTextureFilter);
        angleX += speedX;
        angleY += speedY;

        anglePyramid += speedPyramid;
        angleCube += speedCube;

//        angleTriangle += speedTriangle;
//        angleSquare += speedSquare;
    }
}
