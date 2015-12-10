package com.android.androidexercises.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import cn.waps.AppConnect;

/**
 * Created by ericson on 2015/12/10 0010.
 */
public class AdvertiseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guanggao);
        LinearLayout layout = (LinearLayout) findViewById(R.id.advertisement);
        AppConnect.getInstance(this).showBannerAd(this, layout);
    }
}
