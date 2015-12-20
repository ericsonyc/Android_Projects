package com.android.imageviewsample.app;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by ericson on 2015/12/19 0019.
 */
public class MyButton extends Button {
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setText("MyButton");
    }

    public MyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
