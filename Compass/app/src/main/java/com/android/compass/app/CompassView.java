package com.android.compass.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

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
    //sensor
    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor sensor1;
    Sensor sensor2;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    float[] values = new float[3];
    float[] Re = new float[9];
    float startDegree = 0f;
    float pivotX;
    float pivotY;

    public CompassView(Context context) {
        super(context);
        Log.i(TAG, "Constructor");
        sensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
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
        Log.i(TAG, "init paints");

        //init sensor

        sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        setListener();
    }

    private void setListener(){
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magneticFieldValues = sensorEvent.values;
                }
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelerometerValues = sensorEvent.values;
                }
                updateUI();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(listener, sensor1, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        vWidth = w;
        vHeight = h;
        compassRadius = Math.min(w, h) * 0.8f / 2;
        tickLength = (1 / 8f) * compassRadius;
        textHeight = textPaint.descent() - textPaint.ascent();
        Log.i(TAG, "onSizeChanged");
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.CYAN);
        Log.i(TAG, "onDraw");
        canvas.drawCircle(this.vWidth / 2, (this.vHeight / 2 - compassRadius) / 3 + compassRadius, compassRadius, circlePaint);
        int degress;
        float textWidth;
        for (int i = 0; i < 24; i++) {
            canvas.save();
            canvas.translate(this.vWidth / 2, (this.vHeight / 2 - compassRadius) / 3 + compassRadius);
            degress = i * 15;
            canvas.rotate(degress);
            switch (i) {
                case 0:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength * 3 / 2, tickPaint);
                    textWidth = textPaint.measureText("南");
                    drawText(canvas, "南", textWidth);
                    break;
                case 3:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength / 3, tickPaint);
                    textWidth = textPaint.measureText("225");
                    drawText(canvas, "225", textWidth);
                    break;
                case 6:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength * 3 / 2, tickPaint);
                    textWidth = textPaint.measureText("西");
                    drawText(canvas, "西", textWidth);
                    break;
                case 9:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength / 3, tickPaint);
                    textWidth = textPaint.measureText("315");
                    drawText(canvas, "315", textWidth);
                    break;
                case 12:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength * 3 / 2, tickPaint);
                    textWidth = textPaint.measureText("北");
                    drawText(canvas, "北", textWidth);
                    canvas.drawLine(0, 0, 0, compassRadius / 3 * 2, textPaint);
                    break;
                case 15:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength / 3, tickPaint);
                    textWidth = textPaint.measureText("45");
                    drawText(canvas, "45", textWidth);
                    break;
                case 18:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength * 3 / 2, tickPaint);
                    textWidth = textPaint.measureText("东");
                    drawText(canvas, "东", textWidth);
//                    canvas.drawLine(0, -compassRadius + tickLength + textHeight + 10, -textWidth / 3, -computeHorizontalScrollExtent() + tickLength + textHeight + 30, tickPaint);
//                    canvas.drawLine(0, -compassRadius + tickLength + textHeight + 10, -textWidth / 3, -compassRadius + tickLength + textHeight + 30, tickPaint);
                    break;
                case 21:
                    canvas.drawLine(0, -compassRadius - tickLength / 3, 0, -compassRadius + tickLength / 3, tickPaint);
                    textWidth = textPaint.measureText("135");
                    drawText(canvas, "135", textWidth);
                    break;
                default:
                    break;
            }
            canvas.restore();
        }
    }

    private void rotateImage(float fromDegree, float toDegree) {

        pivotX = this.vWidth / 2 + this.getPivotX();
        pivotY = this.vHeight / 2 + this.getPivotY();
        Animation rotate = new RotateAnimation(fromDegree, toDegree, pivotX, pivotY);
        this.setAnimation(rotate);
        rotate.setDuration(100);
        rotate.start();
        this.invalidate();
    }

    private void drawText(Canvas canvas, String text, float textWidth) {
        canvas.drawText(text, -(textWidth / 2), -compassRadius + tickLength + textHeight, textPaint);
    }

    private void updateUI() {

        SensorManager.getRotationMatrix(Re, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(Re, values);
        values[0] = (float) Math.toDegrees(values[0]);
        rotateImage(startDegree, values[0]);
        startDegree = values[0];
    }
}
