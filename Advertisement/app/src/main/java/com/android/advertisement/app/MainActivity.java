package com.android.advertisement.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import cn.waps.AppConnect;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.id_layout);
        AppConnect.getInstance(this).showBannerAd(this, layout);
    }
}
