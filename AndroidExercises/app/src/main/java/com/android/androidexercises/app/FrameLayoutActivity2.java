package com.android.androidexercises.app;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ericson on 2016/1/19 0019.
 */
public class FrameLayoutActivity2 extends Activity {
    FrameLayout frame=null;
    Handler handler=new Handler(){
        int i=0;

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x123){
                i++;
                move(i%8);
            }
            super.handleMessage(msg);
        }
    };

    void move(int i){
        Drawable a=getResources().getDrawable(R.drawable.pic1);
        Drawable b=getResources().getDrawable(R.drawable.pic2);
        Drawable c=getResources().getDrawable(R.drawable.pic3);
        Drawable d=getResources().getDrawable(R.drawable.pic4);
        Drawable e=getResources().getDrawable(R.drawable.pic5);
        Drawable f=getResources().getDrawable(R.drawable.pic6);
        Drawable g=getResources().getDrawable(R.drawable.pic7);
        Drawable h=getResources().getDrawable(R.drawable.pic8);
        switch (i){
            case 0:
                frame.setForeground(a);
                break;
            case 1:
                frame.setForeground(b);
                break;
            case 2:
                frame.setForeground(c);
                break;
            case 3:
                frame.setForeground(d);
                break;
            case 4:
                frame.setForeground(e);
                break;
            case 5:
                frame.setForeground(f);
                break;
            case 6:
                frame.setForeground(g);
                break;
            case 7:
                frame.setForeground(h);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame3);
        frame=(FrameLayout)findViewById(R.id.myframe);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        },0,200);
    }
}
