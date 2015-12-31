package com.android.openglandroid.app;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by ericson on 2015/12/31 0031.
 */
public class Triangle {
    private FloatBuffer vertexBuffer;
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {0.0f, 0.6220084559f, 0.0f, -0.5f, -0.311004243f, 0.0f, 0.5f, -0.311004243f, 0.0f};
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private final String vertexShaderCode = "attribute vec4 vPosition;" + "void main(){" + " gl_FragColor=vColor;" + "}";
    private final String fragmentShaderCode = "precision mediump float;" + "uniform vec4 vColor;" + "void main(){" + " gl_FragColor=vColor;" + "}";
    private int mProgram = 0;

    private int mPositionHandle;
    private int mColorHandle;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    public Triangle() {
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        int vertexShader = MyGLRender.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRender.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
