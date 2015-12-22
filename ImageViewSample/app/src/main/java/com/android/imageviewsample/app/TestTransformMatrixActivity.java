package com.android.imageviewsample.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by ericson on 2015/12/22 0022.
 */
public class TestTransformMatrixActivity extends Activity implements View.OnTouchListener {

    private TransformMatrixView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view = new TransformMatrixView(this);
        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.setOnTouchListener(this);
        setContentView(view);
    }

    class TransformMatrixView extends ImageView {

        private Bitmap bitmap;
        private Matrix matrix;

        public TransformMatrixView(Context context) {
            super(context);
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.reader);
            matrix = new Matrix();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.drawBitmap(bitmap, matrix, null);
            super.onDraw(canvas);
        }

        @Override
        public void setImageMatrix(Matrix matrix) {
            this.matrix.set(matrix);
            super.setImageMatrix(matrix);
        }

        public Bitmap getImageBitmap() {
            return bitmap;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(view.getImageBitmap().getWidth(), view.getImageBitmap().getHeight());
            view.setImageMatrix(matrix);

            float[] matrixValues = new float[9];
            matrix.getValues(matrixValues);
            for (int i = 0; i < 3; i++) {
                String temp = new String();
                for (int j = 0; j < 3; j++) {
                    temp += matrixValues[3 * i + j] + "\t";
                }
            }
            view.invalidate();
        }
        return true;
    }
}
