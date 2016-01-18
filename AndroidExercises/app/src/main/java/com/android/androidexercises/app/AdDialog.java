package com.android.androidexercises.app;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by ericson on 2016/1/18 0018.
 */
public class AdDialog extends AlertDialog {

    private Context mContext;

    public AdDialog(Context context) {
        super(context);
        mContext = context;
    }

    public AdDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout=inflater.inflate(R.layout.relative3,null);
        layout.setMinimumHeight(600);
        this.setContentView(layout);
    }
}
