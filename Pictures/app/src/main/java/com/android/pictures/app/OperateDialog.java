package com.android.pictures.app;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by ericson on 2015/12/17 0017.
 */
public class OperateDialog extends Dialog {
    private Context context;

    public OperateDialog(Context context) {
        super(context);
        this.context = context;
    }

    public OperateDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }
}
