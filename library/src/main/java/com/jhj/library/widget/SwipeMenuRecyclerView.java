package com.jhj.library.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jhj_Plus on 2016/4/11.
 */
public class SwipeMenuRecyclerView extends RecyclerView {
    private static final String TAG = "SwipeMenuRecyclerView";

    private OnItemClickListener mOnItemClickListener;

    public SwipeMenuRecyclerView(Context context) {
        super(context);
        init();
    }

    public SwipeMenuRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs,0);
    }


    public SwipeMenuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    private float dp2Px(float value) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return value * density;
    }



    private SwipeMenuLayout mOldTouchedChild;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        boolean handled=false;

        Log.e(TAG,"*************onInterceptTouchEvent****************"+"==>"+MotionEvent
                .actionToString(e.getAction()));


        int action=e.getAction();

        //当手指触摸到ItemView之间的间隔时总是返回null
        final View itemView=findChildViewUnder(e.getX(), e.getY());

        if (itemView!=null) {
            Log.i(TAG,"ItemView has be found out"+itemView);
        }


        SwipeMenuLayout touchedChild = findSwipeMenuLayout(itemView);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG,"onInterceptTouchEvent_ACTION_DOWN");


                if (touchedChild != mOldTouchedChild && mOldTouchedChild != null &&
                        (mOldTouchedChild.getDetailState()==SwipeMenuLayout
                                .STATE_DETAIL_OPENING||mOldTouchedChild
                        .getDetailState()==SwipeMenuLayout.STATE_DETAIL_OPENED))
                {

                    Log.i(TAG,"onInterceptTouchEvent_ACTION_DOWN////------->Intercept ##1");

                    mOldTouchedChild.smoothCloseMenu();

                    mOldTouchedChild=touchedChild;

                    return true;
                }


                mOldTouchedChild=touchedChild;

                break;
            case MotionEvent.ACTION_MOVE:

                Log.i(TAG,"onInterceptTouchEvent_ACTION_MOVE");

                boolean becauseOf1 = touchedChild != null &&
                        touchedChild.getState() == SwipeMenuLayout.STATE_DRAGGING;
                boolean becauseOf2 = touchedChild != mOldTouchedChild && mOldTouchedChild != null &&
                        mOldTouchedChild.getState() == SwipeMenuLayout.STATE_DRAGGING;
                boolean becauseOf3 = (touchedChild != null &&
                        touchedChild.getDetailState() == SwipeMenuLayout.STATE_DETAIL_OPENED) ||
                        (touchedChild != mOldTouchedChild &&
                                mOldTouchedChild != null && mOldTouchedChild.getDetailState() ==
                                SwipeMenuLayout.STATE_DETAIL_OPENED);


                if (itemView!=null) {
                    Log.i(TAG,"ItemView has be found out");
                }

                if (touchedChild != null) {
                    Log.i(TAG, "touchedChildPosition=" +
                            getChildAdapterPosition(itemView));
                }

                if (becauseOf1 || becauseOf2 || becauseOf3) {

                    Log.i(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&//" +
                            "becauseOf1="+becauseOf1+"," +
                            "becauseOf2="+becauseOf2+"," +
                            "becauseOf3="+becauseOf3+"///touchedChildPosition=" +
                            getChildAdapterPosition(itemView));
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG,"onInterceptTouchEvent_ACTION_UP");
                boolean canCloseMenu =
                        touchedChild != mOldTouchedChild && mOldTouchedChild != null &&
                                mOldTouchedChild.isOpened();
                if (canCloseMenu) {
                    Log.i(TAG,"onInterceptTouchEvent_ACTION_UP/////--->SmoothCloseMenu");
                    mOldTouchedChild.smoothCloseMenu();
                    return true;
                }
                break;
            default:
                break;
        }

        handled = super.onInterceptTouchEvent(e);

        Log.e(TAG,"onInterceptTouchEvent_Super_handled=====>"+handled);

        return handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        boolean handled=super.onTouchEvent(e);

        Log.e(TAG,"*************onTouchEvent****************"+handled+"==>"+MotionEvent
                .actionToString(e.getAction()));

        final View itemView=findChildViewUnder(e.getX(), e.getY());

        Log.i(TAG,"ItemView==null?"+(itemView==null)+(itemView!=null?getChildAdapterPosition
                (itemView):""));

        return handled;
    }


    private SwipeMenuLayout findSwipeMenuLayout(View child) {
        if (child == null || !(child instanceof ViewGroup)) {
            return null;
        }

        if (child instanceof SwipeMenuLayout) {
            return (SwipeMenuLayout) child;
        } else {
            ViewGroup viewGroup = (ViewGroup) child;
            final int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView instanceof SwipeMenuLayout) {
                    return (SwipeMenuLayout) childView;
                }
            }
        }
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(View itemView, int position);
    }


}
