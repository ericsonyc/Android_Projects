package com.android.androidexercises.app;

import android.animation.*;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import cn.waps.AppConnect;

/**
 * Created by ericson on 2015/12/9 0009.
 */
public class LayoutAnimaActivity extends Activity implements
        CompoundButton.OnCheckedChangeListener {
    private ViewGroup viewGroup;
    private GridLayout mGridLayout;
    private int mVal = 0;
    private LayoutTransition mTransition;

    private CheckBox mAppear, mChangeAppear, mDisAppear, mChangeDisAppear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouttransition);
        viewGroup = (ViewGroup) findViewById(R.id.id_container);

        mAppear = (CheckBox) findViewById(R.id.id_appear);
        mChangeAppear = (CheckBox) findViewById(R.id.id_change_appear);
        mDisAppear = (CheckBox) findViewById(R.id.id_disappear);
        mChangeDisAppear = (CheckBox) findViewById(R.id.id_change_disappear);

        mAppear.setOnCheckedChangeListener(this);
        mChangeAppear.setOnCheckedChangeListener(this);
        mDisAppear.setOnCheckedChangeListener(this);
        mChangeDisAppear.setOnCheckedChangeListener(this);

        // 创建一个GridLayout
        mGridLayout = new GridLayout(this);
        // 设置每列5个按钮
        mGridLayout.setColumnCount(5);
        mGridLayout.setBackgroundColor(Color.BLUE);
        // 添加到布局中
        viewGroup.addView(mGridLayout);
        //默认动画全部开启
        mTransition = new LayoutTransition();
        mGridLayout.setLayoutTransition(mTransition);
    }

    /**
     * 添加按钮
     *
     * @param view
     */
    public void addBtn(View view) {
        final Button button = new Button(this);
        button.setText((++mVal) + "");
        mGridLayout.addView(button, Math.min(1, mGridLayout.getChildCount()));
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGridLayout.removeView(button);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        mTransition = new LayoutTransition();
//        mTransition.setAnimator(
//                LayoutTransition.APPEARING,
//                (mAppear.isChecked() ? mTransition
//                        .getAnimator(LayoutTransition.APPEARING) : null));
//        mTransition
//                .setAnimator(
//                        LayoutTransition.CHANGE_APPEARING,
//                        (mChangeAppear.isChecked() ? mTransition
//                                .getAnimator(LayoutTransition.CHANGE_APPEARING)
//                                : null));
//        mTransition.setAnimator(
//                LayoutTransition.DISAPPEARING,
//                (mDisAppear.isChecked() ? mTransition
//                        .getAnimator(LayoutTransition.DISAPPEARING) : null));
//        mTransition.setAnimator(
//                LayoutTransition.CHANGE_DISAPPEARING,
//                (mChangeDisAppear.isChecked() ? mTransition
//                        .getAnimator(LayoutTransition.CHANGE_DISAPPEARING)
//                        : null));
//        mGridLayout.setLayoutTransition(mTransition);

        setupCustomAnimations(buttonView, isChecked);
    }

    private void setupCustomAnimations(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mChangeAppear) {
            PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
            PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
            PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
            PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
            PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
            PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);
            ObjectAnimator changeln = ObjectAnimator.ofPropertyValuesHolder(mGridLayout, pvhLeft, pvhBottom, pvhRight, pvhTop, pvhScaleX, pvhScaleY).setDuration(mTransition.getDuration(LayoutTransition.CHANGE_APPEARING));

            mTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, changeln);
            changeln.addListener(new AnimatorListenerAdapter() {
                /**
                 * {@inheritDoc}
                 *
                 * @param animation
                 */
                @Override
                public void onAnimationEnd(Animator animation) {
                    View view = (View) ((ObjectAnimator) animation).getTarget();
                    view.setScaleX(1);
                    view.setScaleY(1);
                }
            });
        } else if (buttonView == mAppear) {
            ObjectAnimator animln = ObjectAnimator.ofFloat(null, "rotateY", 30f, 0f).setDuration(mTransition.getDuration(LayoutTransition.APPEARING));
            mTransition.setAnimator(LayoutTransition.APPEARING, animln);
            animln.addListener(new AnimatorListenerAdapter() {
                /**
                 * {@inheritDoc}
                 *
                 * @param animation
                 */
                @Override
                public void onAnimationEnd(Animator animation) {
                    View view = (View) ((ObjectAnimator) animation).getTarget();
                    view.setRotationY(0f);
                }
            });

        } else if (buttonView == mDisAppear) {
            ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotationX", 0f, 90f).setDuration(mTransition.getDuration(LayoutTransition.DISAPPEARING));
            mTransition.setAnimator(LayoutTransition.DISAPPEARING, animOut);
            animOut.addListener(new AnimatorListenerAdapter() {
                /**
                 * {@inheritDoc}
                 *
                 * @param animation
                 */
                @Override
                public void onAnimationEnd(Animator animation) {
                    View view = (View) ((ObjectAnimator) animation).getTarget();
                    view.setRotationX(0f);
                }
            });

        } else if (buttonView == mChangeDisAppear) {
            Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
            Keyframe kf1 = Keyframe.ofFloat(0.9999f, 360f);
            Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
            PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2);
            PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
            PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
            PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
            PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
            ObjectAnimator changeOut = ObjectAnimator.ofPropertyValuesHolder(mGridLayout, pvhLeft, pvhRight, pvhBottom, pvhTop, pvhRotation).setDuration(mTransition.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
            mTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, changeOut);
            changeOut.addListener(new AnimatorListenerAdapter() {
                /**
                 * {@inheritDoc}
                 *
                 * @param animation
                 */
                @Override
                public void onAnimationEnd(Animator animation) {
                    View view = (View) ((ObjectAnimator) animation).getTarget();
                    view.setRotation(0);
                }
            });
        }
        mGridLayout.setLayoutTransition(mTransition);
    }
}

