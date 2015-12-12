package com.android.compass.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Created by ericson on 2015/12/10 0010.
 */
public class CompassView extends View {

    private static String TAG = "UseCompassView";

    private Paint circlePaint, tickPaint;
    private TextPaint textPaint;
    private float vWidth, vHeight;
    private float compassRadius;
    private float tickLength;
    private float textHeight, textWidth;

    public CompassView(Context context) {
        super(context);
        Log.i(TAG,"Constructor");
        initPaint(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    private void initPaint(Context context) {
        //对圆盘画初始化
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.FILL);

        //对刻度画笔进行初始化
        tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tickPaint.setColor(Color.RED);
        tickPaint.setStrokeWidth(3);

        //对字的画笔进行初始化
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(20);
        Log.i(TAG,"init paints");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        vWidth = w;
        vHeight = h;
        compassRadius = Math.min(w, h) * 0.75f / 2;
        tickLength = (1 / 8f) * compassRadius;
        textHeight = textPaint.descent() - textPaint.ascent();
        Log.i(TAG,"onSizeChanged");
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.CYAN);
        Log.i(TAG,"onDraw");
        canvas.drawCircle(this.vWidth/2, this.vHeight/2, compassRadius, circlePaint);
        int degress;
        float textWidth;
        for (int i = 0; i < 24; i++) {
            canvas.save();
            canvas.translate(compassRadius, compassRadius);
            degress = i * 15;
            canvas.rotate(15 * i);
            canvas.drawLine(0, -compassRadius, 0, -compassRadius + tickLength, tickPaint);
            switch (degress) {
                case 0:
                    textWidth = textPaint.measureText("45");
                    drawText(canvas, "45", textWidth);
                    break;
                case 45:
                    textWidth = textPaint.measureText("东");
                    drawText(canvas, "东", textWidth);
                    break;
                case 90:
                    textWidth = textPaint.measureText("135");
                    drawText(canvas, "135", textWidth);
                    break;
                case 135:
                    textWidth = textPaint.measureText("南");
                    drawText(canvas, "南", textWidth);
                    break;
                case 180:
                    textWidth = textPaint.measureText("225");
                    drawText(canvas, "西", textWidth);
                    break;
                case 225:
                    textWidth = textPaint.measureText("西");
                    drawText(canvas, "西", textWidth);
                    break;
                case 270:
                    textWidth = textPaint.measureText("北");
                    drawText(canvas, "北", textWidth);
                    canvas.drawLine(0, -compassRadius + tickLength + textHeight + 10, -textWidth / 3, -computeHorizontalScrollExtent() + tickLength + textHeight + 30, tickPaint);
                    canvas.drawLine(0, -compassRadius + tickLength + textHeight + 10, -textWidth / 3, -compassRadius + tickLength + textHeight + 30, tickPaint);
                    break;
                default:
                    break;
            }
            canvas.restore();
        }
    }

    private void drawText(Canvas canvas, String text, float textWidth) {
        canvas.drawText(text, -(textWidth / 2), -compassRadius + tickLength + textHeight, textPaint);
    }
}
