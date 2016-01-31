package com.android.androidexercises.app;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by ericson on 2016/1/24 0024.
 */
public class EditTextActivity extends EditText {
    private final static String TAG = "EditTextWithDel";
    private Drawable imgEnable;
    private Drawable imgAble;
    private Context mContext;

    public EditTextActivity(Context context) {
        super(context);
        mContext=context;
        init();
    }

    public EditTextActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    public EditTextActivity(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext=context;
        init();
    }

    public void init() {
        imgEnable = mContext.getResources().getDrawable(R.drawable.cancel);
        Log.i(TAG,"width:"+imgEnable.getIntrinsicWidth()+",height:"+imgEnable.getIntrinsicHeight());
        imgEnable.setBounds(0,0,20,20);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        setDrawable();
    }

    private void setDrawable() {
        if (length() < 1)
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgEnable, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG,"onTouchEvent");
        if(imgEnable!=null&&event.getAction()==MotionEvent.ACTION_UP){
            Log.i(TAG,"imgEnable");
            int eventX=(int)event.getRawX();
            int eventY=(int)event.getRawY();
            Log.e(TAG,"eventX="+eventX+",eventY="+eventY);
            Rect rect=new Rect();
            getGlobalVisibleRect(rect);
            rect.left=rect.right-100;
            Log.i(TAG,"rect left:"+rect.left+",rect right:"+rect.right+",rect top:"+rect.top+",rect bottom:"+rect.bottom);
            if(rect.contains(eventX,eventY)){
                Log.i(TAG,"contains");
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
