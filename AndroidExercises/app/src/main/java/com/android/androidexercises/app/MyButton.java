package com.android.androidexercises.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageButton;

/**
 * Created by ericson on 2016/1/24 0024.
 */
public class MyButton extends ImageButton {
    private static final int INVALIDATE_DURATION=15;
    private static int DIFFUSE_GAP=10;
    private static int TAP_TIMEOUT;
    private int viewwidth,viewHeight;
    private int pointX,pointY;
    private int maxRadio;
    private int shaderRadio;
    private Paint bottomPaint,colorPaint;
    private boolean isPushButton;
    private int eventX,eventY;
    private long downTime=0;
    public MyButton(Context context, AttributeSet attrs){
        super(context,attrs);
        initPaint();
        TAP_TIMEOUT= ViewConfiguration.getLongPressTimeout();
    }

    private  void initPaint(){
        colorPaint=new Paint();
        bottomPaint=new Paint();
        colorPaint.setColor(getResources().getColor(R.color.reveal_color));
        bottomPaint.setColor(getResources().getColor(R.color.bottom_color));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(downTime==0)
                    downTime= SystemClock.elapsedRealtime();
                eventX=(int)event.getX();
                eventY=(int)event.getY();
                countMaxRadio();
                isPushButton=true;
                postInvalidateDelayed(INVALIDATE_DURATION);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(SystemClock.elapsedRealtime()-downTime<TAP_TIMEOUT){
                    DIFFUSE_GAP=30;
                    postInvalidate();
                }else{
                    clearData();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(!isPushButton)return;
        canvas.drawRect(pointX,pointY,pointX+viewwidth,pointY+viewHeight,bottomPaint);
        canvas.save();
        canvas.clipRect(pointX,pointY,pointX+viewwidth,pointY+viewHeight);
        canvas.drawCircle(eventX,eventY,shaderRadio,colorPaint);
        canvas.restore();
        if(shaderRadio<maxRadio){
            postInvalidateDelayed(INVALIDATE_DURATION,pointX,pointY,pointX+viewwidth,pointY+viewHeight);
            shaderRadio+=DIFFUSE_GAP;
        }else{
            clearData();
        }
    }

    private void countMaxRadio(){
        if(viewwidth>viewHeight){
            if(eventX<viewwidth/2){
                maxRadio=viewwidth-eventX;
            }else{
                maxRadio=viewwidth/2+eventX;
            }
        }else{
            if(eventY<viewHeight/2){
                maxRadio=viewHeight-eventY;
            }else{
                maxRadio=viewHeight/2+eventY;
            }
        }
    }

    private void clearData(){
        downTime=0;
        DIFFUSE_GAP=10;
        isPushButton=false;
        shaderRadio=0;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewwidth=w;
        this.viewHeight=h;
    }
}
