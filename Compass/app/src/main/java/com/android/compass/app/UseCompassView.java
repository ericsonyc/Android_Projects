package com.android.compass.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.waps.AppConnect;

/**
 * Created by ericson on 2015/12/12 0012.
 */
public class UseCompassView extends Activity {

    private static final String TAG = "UseCompassView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.advertise_layout);
        AppConnect.getInstance("70f005ce4d7cdebfd4ed3ae42ba3b3fc", "default", this).showBannerAd(this, layout);
        final RelativeLayout compassView = (RelativeLayout) findViewById(R.id.compassview);

        ViewTreeObserver vto = compassView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                compassView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.leftMargin = 30;
        CompassView compass = new CompassView(this);
        compass.setLayoutParams(layoutParams);
        compassView.addView(compass);
    }

}
