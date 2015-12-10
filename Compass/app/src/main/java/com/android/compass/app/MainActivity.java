package com.android.compass.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.waps.AppConnect;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.advertise_layout);
        ImageView imageView = (ImageView) findViewById(R.id.compass_image);
        AppConnect.getInstance(this).showBannerAd(this, layout);

    }
}
