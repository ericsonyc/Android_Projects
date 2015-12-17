package com.android.compass.app;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.waps.AppConnect;

import java.util.Timer;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    RelativeLayout compass_layout;
    TextView textView;
    ImageView imageView;
    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor sensor1;
    Sensor sensor2;
    Timer updateTimer;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    float[] values = new float[3];
    float[] Re = new float[9];
    float startDegree = 0f;
    float pivotX;
    float pivotY;
    CompassView compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.advertise_layout);
        AppConnect.getInstance("70f005ce4d7cdebfd4ed3ae42ba3b3fc", "default", this).showBannerAd(this, layout);
        compass_layout = (RelativeLayout) findViewById(R.id.compass_layout);

        textView = (TextView) findViewById(R.id.text);

//        imageView = (ImageView) findViewById(R.id.imageView);
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        RelativeLayout compassView = (RelativeLayout) findViewById(R.id.compassview);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        compass = new CompassView(this);
        compass.setLayoutParams(layoutParams);
        compassView.addView(compass);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        listener = new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//                    magneticFieldValues = sensorEvent.values;
//                }
//                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    accelerometerValues = sensorEvent.values;
//                }
//                updateUI();
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int i) {
//
//            }
//        };
//        sensorManager.registerListener(listener, sensor1, SensorManager.SENSOR_DELAY_GAME);
//        sensorManager.registerListener(listener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
//        updateTimer = new Timer("updateUI");
//        updateTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                updateUI();
//            }
//        }, 0, 50);

    }

    private void rotateImage(float fromDegree, float toDegree) {

        pivotX = compass.getWidth() / 2 + compass.getPivotX();
        pivotY = compass.getHeight() / 2 + compass.getPivotY();
        Animation rotate = new RotateAnimation(fromDegree, toDegree, pivotX, pivotY);
        compass.setAnimation(rotate);
        rotate.setDuration(100);
        rotate.start();
        compass.invalidate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(listener);
        updateTimer.cancel();
        super.onPause();
    }

    private void updateUI() {

        SensorManager.getRotationMatrix(Re, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(Re, values);
        values[0] = (float) Math.toDegrees(values[0]);
        rotateImage(startDegree, values[0]);
        startDegree = values[0];
    }
}
