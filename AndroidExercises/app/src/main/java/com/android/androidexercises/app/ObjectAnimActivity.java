package com.android.androidexercises.app;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

public class ObjectAnimActivity extends Activity implements View.OnClickListener {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.value_animation);
        imageView = (ImageView) findViewById(R.id.id_ball);
        imageView.setOnClickListener(this);
        Button vertical = (Button) findViewById(R.id.vertical);
        Button parabola = (Button) findViewById(R.id.parabola);
        Button delete = (Button) findViewById(R.id.delete);
        vertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalRun(v);
            }
        });
        parabola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeOut(v);
            }
        });
        delete.setOnClickListener(this);
    }

    public void paowuxian(View view) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(3000);
        valueAnimator.setObjectValues(new PointF(0, 0));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setEvaluator(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                PointF point = new PointF();
                point.x = 200 * fraction * 3;
                point.y = 0.5f * 200 * (fraction * 3) * fraction * 3;
                return point;
            }
        });
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                imageView.setX(point.x);
                imageView.setY(point.y);
            }
        });
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
//            ObjectAnimator anim = ObjectAnimator.ofFloat(v, "hello", 1.0f, 0.0f).setDuration(1000);
//            anim.start();
//            final long start = anim.getCurrentPlayTime();
//            Log.i("time",""+start);
//            float x=v.getX();
//            float y=v.getY();
//            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    float cVal=(Float)animation.getAnimatedValue();
//                    v.setAlpha(cVal);
//                    v.setScaleX(cVal);
//                    v.setScaleY(cVal);
//                    v.setRotationX(cVal*360);

//                    long current=animation.getCurrentPlayTime();
//                    float transX=(float)(current-start)*3f;
//                    float transY=(float)((current-start)*(current-start)*2f);
//
//                    v.setTranslationX(500);
//                    v.setTranslationY(500);
//                    Log.i("time","")
//                }
//            });


//            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
//            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
//            PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);
//            ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY, pvhZ).setDuration(1000).start();

            verticalRun(v);
        } else if (v.getId() == R.id.delete) {
            fadeOut(v);

        }
    }

    public void verticalRun(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        imageView.setX(0);
        ValueAnimator animator = ValueAnimator.ofFloat(0, height - imageView.getHeight());
        animator.setTarget(imageView);
        animator.setDuration(1000).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageView.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
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

    public void fadeOut(final View view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null)
                    parent.removeView(imageView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }
}
