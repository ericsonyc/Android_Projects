package com.face.android.faceproject;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button detection;
    Button recognition;
    Button emotionBtn;
    private ResideMenu resideMenu;
    private ResideMenuItem itemDetect;
    private ResideMenuItem itemRecognize;
    private ResideMenuItem itemEmotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        setContentView(R.layout.activity_main);
//        detection = (Button) findViewById(R.id.detectionButton);
//        detection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, DetectActivity.class));
//            }
//        });
//        recognition = (Button) findViewById(R.id.recongitionButton);
//        recognition.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, RecognitionActivity.class));
//            }
//        });
//
//        emotionBtn = (Button) findViewById(R.id.emotionRecognize);
//        emotionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, EmotionActivity.class));
//            }
//        });
        setUpMenu();
        if (savedInstanceState == null)
            changeFragment(new DetectFragment());

    }

    private void setUpMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.mipmap.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemDetect = new ResideMenuItem(this, R.mipmap.detect, "Detect");
        itemRecognize = new ResideMenuItem(this, R.mipmap.recognize, "Recognize");
        itemEmotion = new ResideMenuItem(this, R.mipmap.emotion, "Emotion");

        itemDetect.setOnClickListener(this);
        itemRecognize.setOnClickListener(this);
        itemEmotion.setOnClickListener(this);

        resideMenu.addMenuItem(itemDetect, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemRecognize, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemEmotion, ResideMenu.DIRECTION_LEFT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
//        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
//            }
//        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return resideMenu.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        if (view == itemDetect){
            changeFragment(new DetectFragment());
        }else if (view == itemRecognize){
            changeFragment(new RecognizeFragment());
        }else if (view == itemEmotion){
            changeFragment(new EmotionFragment());
        }

        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
//            Toast.makeText(MainActivity.this, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
//            Toast.makeText(MainActivity.this, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
