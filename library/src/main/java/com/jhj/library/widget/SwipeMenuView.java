package com.jhj.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by jhj_Plus on 2016/4/11.
 */
public class SwipeMenuView extends LinearLayout {
    private static final String TAG = "SwipeMenuView";

    public SwipeMenuView(Context context) {
        this(context,null);
    }

    public SwipeMenuView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SwipeMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
