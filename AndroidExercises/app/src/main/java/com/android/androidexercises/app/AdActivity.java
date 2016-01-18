package com.android.androidexercises.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ericson on 2016/1/18 0018.
 */
public class AdActivity extends Activity {

    private ImageView imgCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relative3);
        imgCache =(ImageView)findViewById(R.id.imgCache);
        imgCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
