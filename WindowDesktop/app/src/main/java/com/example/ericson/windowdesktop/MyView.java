package com.example.ericson.windowdesktop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.style.TtsSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by ericson on 2016/5/28 0028.
 */

public class MyView extends ViewGroup {

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth : 0, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight : 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int childcount = getChildCount();
        int row = 0, column = 0;
        View bottomView = getChildAt(childcount - 1);
        int contentHeight = height - bottomView.getMeasuredHeight();
        bottomView.layout(0, height - bottomView.getMeasuredHeight(), width, height);
        for (int i = 0; i < childcount - 1; i++) {
            View child = getChildAt(i);
            int ct, cr, cb, cl = column * child.getMeasuredWidth();
            int cwidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();
            ct = cHeight * row;
            cr = cl + cwidth;
            cb = ct + cHeight;
            if (cb > contentHeight) {
                column++;
                row = 0;
                i--;
            } else {
                row++;
                child.layout(cl, ct, cr, cb);
            }
        }
    }
}
