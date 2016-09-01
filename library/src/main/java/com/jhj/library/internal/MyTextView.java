package com.jhj.library.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by jhj_Plus on 2016/6/18.
 */
public class MyTextView extends TextView {
    private static final String TAG = "MyTextView";

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled=super.onTouchEvent(event);
        Log.e(TAG,"onTouchEvent"+MotionEvent.actionToString(event.getAction())+",,handled="+false);
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG,"widthMeasureSpec="+MeasureSpec.toString(widthMeasureSpec));
        Log.e(TAG,"heightMeasureSpec="+MeasureSpec.toString(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG,"width="+getMeasuredWidth()+",height="+getMeasuredHeight()+",l="+left+"," +
                "t="+top+"," +
                "r="+right+",b="+bottom);
    }
}
