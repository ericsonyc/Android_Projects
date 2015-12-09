package com.android.androidexercises.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ericson on 2015/12/9 0009.
 */
public class AnomatorSetActivity extends Activity {
    private ImageView mBlueBall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animationset);
        mBlueBall = (ImageView) findViewById(R.id.id_ball);
    }

    public void togetherRun(View view) {
//        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mBlueBall, "scaleX", 1.0f, 2f);
//        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mBlueBall, "scaleY", 1.0f, 2f);
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.setDuration(2000);
//        animSet.setInterpolator(new LinearInterpolator());
//        animSet.playTogether(anim1, anim2);
//        animSet.start();
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.scalex);
        mBlueBall.setPivotX(0);
        mBlueBall.setPivotY(0);
        mBlueBall.invalidate();
        anim.setTarget(mBlueBall);
        anim.start();
    }

    public void playWithAfter(View view) {
        float cx = mBlueBall.getX();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mBlueBall, "scaleX", 1.0f, 2f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mBlueBall, "scaleY", 1f, 2f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(mBlueBall, "x", cx, 0f);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(mBlueBall, "x", cx);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.play(anim2).with(anim3);
        animSet.play(anim4).after(anim3);
        animSet.setDuration(1000);
        animSet.start();
    }
}
