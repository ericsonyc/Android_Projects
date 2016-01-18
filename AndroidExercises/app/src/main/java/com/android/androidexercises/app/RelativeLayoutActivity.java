package com.android.androidexercises.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by ericson on 2016/1/18 0018.
 */
public class RelativeLayoutActivity extends Activity {

    private static String TAG="RelativeLayout";
    private RelativeLayout relative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relativemain3);
        Log.i(TAG,"setContentView done");
//        final Intent intent=new Intent(RelativeLayoutActivity.this,AdDialog.class);
        Log.i(TAG,"intent");
        final AdDialog dialog=new AdDialog(this);
        relative=(RelativeLayout)findViewById(R.id.RelativeLayout3);
        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(intent);
                dialog.show();
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
