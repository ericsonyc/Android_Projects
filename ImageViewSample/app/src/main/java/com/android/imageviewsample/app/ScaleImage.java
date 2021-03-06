package com.android.imageviewsample.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.*;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ericson on 2015/12/22 0022.
 */
public class ScaleImage extends ImageView {

    private Matrix mMatrix = new Matrix();
    private Matrix mChangeMatrix = new Matrix();
    private Bitmap mBitmap = null;
    private DisplayMetrics mDisplayMetrics;
    private float mMinScale = 0.2f;
    private float mMaxScale = 5.0f;
    private static final int STATE_NONE = 0;
    private static final int STATE_DRAG = 1;
    private static final int STATE_ZOOM = 2;
    private int mState = STATE_NONE;
    private PointF mFirstPointF = new PointF();
    private PointF mSecondPointF = new PointF();
    private float mDistance = 1f;
    private float mCenterX, mCenterY;

    private static final String TAG = "ScaleImage";

    public ScaleImage(Context context) {
        super(context);
    }

    public ScaleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        BitmapDrawable mBitmapDrawable = (BitmapDrawable) this.getDrawable();
        if (mBitmapDrawable != null) {
            mBitmap = mBitmapDrawable.getBitmap();
            build_image();
        }
    }

    private void Scale() {
        float level[] = new float[9];
        mMatrix.getValues(level);
        if (mState == STATE_ZOOM) {
            if (level[0] < mMinScale) {
                mMatrix.setScale(mMinScale, mMinScale);
                mMatrix.postTranslate(mCenterX, mCenterY);
            }
            if (level[0] > mMaxScale)
                mMatrix.set(mChangeMatrix);
        }
    }

    private float Spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getX(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void MidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void build_image() {
        Context mContext = getContext();
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        this.setScaleType(ScaleType.MATRIX);
        this.setImageBitmap(mBitmap);
        Log.i(TAG, "widthPixels:" + mDisplayMetrics.widthPixels);
        Log.i(TAG, "heightPixels:" + mDisplayMetrics.heightPixels);
        Log.i(TAG, "getWidth:" + mBitmap.getWidth());
        Log.i(TAG, "getHeight:" + mBitmap.getHeight());

        mCenterX = (float) ((mDisplayMetrics.widthPixels / 2) - mBitmap.getWidth() / 2);
        mCenterY = (float) (mDisplayMetrics.heightPixels / 2 - mBitmap.getHeight() / 2);
        mMatrix.postTranslate(mCenterX, mCenterY);
        this.setImageMatrix(mMatrix);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "action_down");
                        mChangeMatrix.set(mMatrix);
                        mFirstPointF.set(event.getX(), event.getY());
                        mState = STATE_DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.i(TAG, "action_pointer_down");
                        mDistance = Spacing(event);
                        if (Spacing(event) > 10) {
                            mChangeMatrix.set(mMatrix);
                            MidPoint(mSecondPointF, event);
                            mState = STATE_ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
//                        mBitmap=Bitmap.createBitmap(mBitmap,0,0,mBitmap.getWidth(),mBitmap.getHeight(),mMatrix,true);
                        Log.i(TAG, "action_up");
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.i(TAG, "action_pointer_up");

                        mState = STATE_NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i(TAG, "action_move");
                        if (mState == STATE_DRAG) {
                            mMatrix.set(mChangeMatrix);
                            mMatrix.postTranslate(event.getX() - mFirstPointF.x, event.getY() - mFirstPointF.y);
                        } else if (mState == STATE_ZOOM) {
                            float NewDistance = Spacing(event);
                            if (NewDistance > 10) {
                                mMatrix.set(mChangeMatrix);
                                float NewScale = NewDistance / mDistance;
                                mMatrix.postScale(NewScale, NewScale, mSecondPointF.x, mSecondPointF.y);
                            }
                        }
                        break;
                }
                ScaleImage.this.setImageMatrix(mMatrix);
//                ScaleImage.this.setImageBitmap(mBitmap);
                Scale();
                invalidate();
                return true;
            }
        });
    }
}
