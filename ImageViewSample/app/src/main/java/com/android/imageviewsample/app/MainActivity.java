package com.android.imageviewsample.app;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "TouchImageView";
    private MatrixImageView imageView;
    private TouchImageView img;
    private MyButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        imageView=(MatrixImageView)findViewById(R.id.imageView);
//        Resources res=getResources();
//        Bitmap bmp= BitmapFactory.decodeResource(res,R.mipmap.finddragon);
//        imageView.setImageBitmap(bmp);

//        TouchImageView img = (TouchImageView) findViewById(R.id.touch);
//        img.setImageResource(R.mipmap.finddragon);
//        img.setMaxZoom(4);


        RelativeLayout group = (RelativeLayout) ((ViewGroup) (this.getWindow().getDecorView().findViewById(android.R.id.content))).getChildAt(0);
        TouchImageView img = new TouchImageView(this);
        img.setImageResource(R.mipmap.finddragon);
//        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setMaxZoom(6);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ABOVE, R.id.button);
        img.setLayoutParams(params);
        group.addView(img);

        img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "click");
    }
}
