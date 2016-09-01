package com.jhj.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by jhj_Plus on 2016/4/11.
 */
public class SwipeMenuItem extends LinearLayout {
    private static final String TAG = "SwipeMenuItem";

    public SwipeMenuItem(Context context) {
        this(context,null);
    }

    public SwipeMenuItem(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SwipeMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeMenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
