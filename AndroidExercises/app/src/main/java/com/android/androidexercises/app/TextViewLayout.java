package com.android.androidexercises.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by ericson on 2016/1/22 0022.
 */
public class TextViewLayout extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.textview3);
        EditText edit = new EditTextActivity(this);
        setContentView(edit);
    }
}
