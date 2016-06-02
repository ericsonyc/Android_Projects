package com.example.ericson.animatorexamples;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView=(ImageView)findViewById(R.id.image);
        Button margin = (Button)findViewById(R.id.margin);
        margin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueAnimator animator=ValueAnimator.ofInt(0,20);
                animator.setDuration(5000);
                animator.start();
                ObjectAnimator animator1=ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
                animator1.setDuration(5000);
                animator1.start();
            }
        });
    }
}
