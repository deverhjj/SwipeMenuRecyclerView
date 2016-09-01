package com.jhj.library.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by jhj_Plus on 2016/4/11.
 */
public class MyLayout extends FrameLayout {
    private static final String TAG = "MyLayout";

    public MyLayout(Context context) {
        super(context);
    }

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed,l,t,r,b);

        Log.i(TAG,"_____________________onLayout________________________"+getTag()+",l="+l+",t="+t+"," +
                "r="+r+",b="+b);

        Log.e(TAG,"width="+getMeasuredWidth()+",height="+getMeasuredHeight());

       int childCount=getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child=getChildAt(i);
            LayoutParams lp= (LayoutParams) child.getLayoutParams();

            int zGravity = MyGravity.DEEP_GRAVITY_MASK & lp.gravity;

            switch (zGravity) {
                case MyGravity.ABOVE:
                    Log.e(TAG, "---------ABOVE--------");
                    break;
                case MyGravity.BELOW:
                    Log.e(TAG, "---------BELOW-------");
                    break;
                default:
                    break;
            }

            int vGravity=MyGravity.VERTICAL_GRAVITY_MASK & lp.gravity;

            switch (vGravity) {
                case MyGravity.TOP:
                    Log.e(TAG, "---------TOP-------");
                    break;
                case MyGravity.BOTTOM:
                    Log.e(TAG, "---------BOTTOM-------");
                    break;
                case MyGravity.CENTER_VERTICAL:
                    Log.e(TAG, "---------CENTER_VERTICAL-------");
                    break;
                default:
                    break;
            }



            int combine = vGravity | zGravity;

            switch (combine) {
                case MyGravity.TOP:
                    Log.e(TAG, "---------TOP-------");
                    break;
                case MyGravity.BOTTOM:
                    Log.e(TAG, "---------BOTTOM-------");
                    break;
                case MyGravity.CENTER_VERTICAL:
                    Log.e(TAG, "---------CENTER_VERTICAL-------");
                    break;

                case MyGravity.ABOVE:
                    Log.e(TAG, "---------ABOVE-------");
                    break;
                case MyGravity.BELOW:
                    Log.e(TAG, "---------BELOW-------");

                    break;

                case Gravity.TOP | MyGravity.ABOVE:
                    Log.e(TAG, "---------TOP|ABOVE-------");
                    break;
                case Gravity.BOTTOM | MyGravity.ABOVE:
                    Log.e(TAG, "---------BOTTOM|ABOVE-------");
                    break;
                case Gravity.CENTER_VERTICAL | MyGravity.ABOVE:
                    Log.e(TAG, "---------CENTER_VERTICAL|ABOVE-------");
                    break;

                case Gravity.TOP | MyGravity.BELOW:
                    Log.e(TAG, "---------TOP|BELOW-------");
                    break;
                case Gravity.BOTTOM | MyGravity.BELOW:
                    Log.e(TAG, "---------BOTTOM|BELOW-------");
                    break;
                case Gravity.CENTER_VERTICAL | MyGravity.BELOW:
                    Log.e(TAG, "----------CENTER_VERTICAL|BELOW-------");
                    break;

                default:
                    break;
            }


        }



    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled=super.onInterceptTouchEvent(ev);

        Log.e(TAG,"onInterceptTouchEvent"+MotionEvent.actionToString(ev.getAction())+",," +
                "handled="+handled+",,tag=>"+getTag());
        return handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean handled=super.onTouchEvent(event);

        Log.e(TAG,"onTouchEvent"+MotionEvent.actionToString(event.getAction())+",,handled="+handled+",,tag=>"+getTag());

        return handled;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i(TAG,"_____________________onMeasure________________________"+getTag());

        Log.e(TAG,"widthMeasureSpec="+MeasureSpec.toString(widthMeasureSpec));
        Log.e(TAG,"heightMeasureSpec="+MeasureSpec.toString(heightMeasureSpec));


    }



}
