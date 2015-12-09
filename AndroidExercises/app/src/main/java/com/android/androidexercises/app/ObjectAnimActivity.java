package com.android.androidexercises.app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ObjectAnimActivity extends Activity implements View.OnClickListener {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_layout);
        imageView = (ImageView) findViewById(R.id.id_flight);
        imageView.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(final View v) {
        if (v == imageView) {
//            ObjectAnimator.ofFloat(v, "rotationX", 0.0f, 360.0f).setDuration(500).start();
            ObjectAnimator anim=ObjectAnimator.ofFloat(v, "hello", 1.0f, 0.0f).setDuration(500);
            anim.start();
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float cVal=(Float)animation.getAnimatedValue();
                    v.setAlpha(cVal);
                    v.setScaleX(cVal);
                    v.setScaleY(cVal);
                    v.setRotationX(cVal*360);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
