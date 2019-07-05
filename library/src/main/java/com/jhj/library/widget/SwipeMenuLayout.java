package com.jhj.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.jhj.dev.library.R;
import com.jhj.library.internal.MyGravity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhj_Plus on 2016/4/11.
 */
public class SwipeMenuLayout extends ViewGroup {
    private static final String TAG = "SwipeMenuLayout";

    private List<View> mMatchParentChildren = new ArrayList<>(1);

    private View mItemView;
    private View mMenuView;

    public static final int STATE_DETAIL_MOVING = 0;
    public static final int STATE_DETAIL_OPENING = 1;
    public static final int STATE_DETAIL_OPENED = 2;
    public static final int STATE_DETAIL_CLOSING = 3;
    public static final int STATE_DETAIL_CLOSED = 4;
    private int mDetailState = STATE_DETAIL_CLOSED;
    
    
    public static final int STATE_IDLE = 0;// STATE_DETAIL_CLOSED 或 STATE_DETAIL_OPENED 状态
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;// STATE_DETAIL_CLOSING 或 STATE_DETAIL_OPENING 状态
    private int mState = STATE_IDLE;

    private static final float MIN_FLING_VELOCITY_X = 400;//dp

    private static final Interpolator DEFAULT_OPEN_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator DEFAULT_CLOSE_INTERPOLATOR = new LinearInterpolator();

    private static final int DEFAULT_SCROLL_OPEN_DURATION=100;
    private static final int DEFAULT_SCROLL_CLOSE_DURATION=100;

    private GestureDetectorCompat mGestureDetector;
    private ScrollerCompat mOpenScroller;
    private ScrollerCompat mCloseScroller;
    private float mMinFlingVelocityX;

    private static final int NO_FLING = -1;
    private static final int FLING_RIGHT = 0;
    private static final int FLING_LEFT = 1;
    private int mFlingDirection = NO_FLING;
    private boolean mIsFling;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private ViewConfiguration mViewConfiguration = ViewConfiguration.get(getContext());
    private int mTouchSlop = mViewConfiguration.getScaledTouchSlop();

    public SwipeMenuLayout(Context context) {
        super(context);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr,0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs,defStyleAttr,defStyleRes);
    }


    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //设置绘制 Child 的顺序可以改变，忽略布局文件中定义 SwipeMenuView 和 ItemView 的顺序
       // setChildrenDrawingOrderEnabled(true);
       //
        mGestureDetector=new GestureDetectorCompat(getContext(),new GestureListener());

        mOpenScroller=ScrollerCompat.create(getContext(),DEFAULT_OPEN_INTERPOLATOR);

        mCloseScroller=ScrollerCompat.create(getContext(),DEFAULT_CLOSE_INTERPOLATOR);

        mMinFlingVelocityX=dp2Px(MIN_FLING_VELOCITY_X);

    }


    private float dp2Px(float value) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return value * density;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        Log.i(TAG,"-----------------------onMeasure-----------s--s---------");

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final boolean measureMatchParentChildren =
                widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY;
        mMatchParentChildren.clear();

        int maxHeight=0;

        Log.e(TAG,"widthMeasureSpec="+MeasureSpec.toString(widthMeasureSpec));
        Log.e(TAG,"heightMeasureSpec="+MeasureSpec.toString(heightMeasureSpec));


        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {

                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (isItemView(child)) {

                    mItemView = child;

                    final int itemViewWidthSpec = MeasureSpec.makeMeasureSpec(
                            widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
                    final int itemViewHeightSpec = getChildMeasureSpec(heightMeasureSpec, lp
                            .topMargin + lp.bottomMargin, lp.height);

                    child.measure(itemViewWidthSpec, itemViewHeightSpec);

                } else if (isSwipeMenuView(child)) {

                    mMenuView = child;

                    // 测量 mode 需要调整
                    final int swipeMenuWidthSpec = getChildMeasureSpec(widthMeasureSpec, lp
                            .leftMargin + lp.rightMargin, lp.width);
//                            MeasureSpec.makeMeasureSpec(
//                            widthSize - lp.leftMargin - lp.rightMargin,
//                            MeasureSpec.EXACTLY);

                    final int swipeMenuHeightSpec = getChildMeasureSpec(heightMeasureSpec,
                            lp.topMargin + lp.bottomMargin, lp.height);

                    Log.e(TAG, "result_size=" + MeasureSpec.getSize(swipeMenuHeightSpec)
                            + ",heightMode===AT_MOST" + (MeasureSpec.getMode(swipeMenuHeightSpec) == MeasureSpec
                            .AT_MOST));

                    Log.e(TAG, "swipeMenuWidthSpec>>>" + MeasureSpec.toString(swipeMenuWidthSpec));

                    child.measure(swipeMenuWidthSpec, swipeMenuHeightSpec);

                    Log.e(TAG, "SwipeMenuView_width=" + child.getMeasuredWidth() + "," +
                            "SwipeMenuView_height=" + child.getMeasuredHeight());

                } else {
                    throw new IllegalStateException("Child " + child + " at index " + i +
                            " does not have a valid layout_gravity - must be Gravity.LEFT, " +
                            "Gravity.RIGHT or Gravity.NO_GRAVITY");
                }


                if (measureMatchParentChildren) {
                    if (lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child);
                    }
                }

                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        Log.e(TAG, "maxH------->" + maxHeight);

        Log.e(TAG, "setMeasuredDimension==>" + "width=" + widthSize + ",height=" + maxHeight);
        setMeasuredDimension(widthSize, maxHeight);

        final int reMeasureChildCount = mMatchParentChildren.size();

        if (reMeasureChildCount > 0) {
            for (int i = 0; i < reMeasureChildCount; i++) {
                final View child = mMatchParentChildren.get(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        child.getMeasuredWidth(), MeasureSpec.EXACTLY);
                final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight
                                () - lp.topMargin - lp.bottomMargin,
                        MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.i(TAG,"______________________onLayout___________________________"+",l="+l+",t="+t+"," +
                "r="+r+",b="+b);
        int childCount = getChildCount();
        final int width=getMeasuredWidth();
        final int height=getMeasuredHeight();
        int swipeMenuLeft;

        Log.e(TAG,"onLayout_width="+width+",height="+height+",childCount="+childCount);

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                final int childWidth=child.getMeasuredWidth();
                final int childHeight=child.getMeasuredHeight();

                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (isItemView(child)) {
                    Log.e(TAG, "ItemView_width=" + childWidth + ",ItemView_height=" + childHeight);
                    final int ivL = lp.leftMargin;
                    final int ivt = lp.topMargin;
                    final int ivr = ivL + childWidth;
                    final int ivb = ivt + childHeight;
                    Log.e(TAG,ivL+","+ivt+","+ivr+","+ivb);
                    child.layout(ivL, ivt, ivr, ivb);

                } else {
                    Log.e(TAG, "index=" + i);
                    Log.e(TAG,
                            "SwipeView_width=" + childWidth + ",SwipeView_height=" + childHeight);

                    final int gravity = lp.gravity;

                    //TODO 是否考虑水平居中?
                    if (checkSwipeMenuViewAbsoluteGravity(child, Gravity.LEFT)) {
                        swipeMenuLeft = -childWidth;
                        if ((lp.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {
                            swipeMenuLeft = lp.rightMargin;
                        }
                    } else {
                        swipeMenuLeft = width + lp.leftMargin;
                        if ((lp.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {
                            swipeMenuLeft = width - lp.rightMargin - childWidth;
                        }
                    }

                    int verGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
                    int deepGravity = gravity & MyGravity.DEEP_GRAVITY_MASK;

                    switch (verGravity | deepGravity) {
                        case Gravity.TOP:
                        case Gravity.TOP | MyGravity.ABOVE:
                        case Gravity.TOP | MyGravity.BELOW:
                            child.layout(swipeMenuLeft, lp.topMargin,
                                    swipeMenuLeft + childWidth, childHeight + lp.topMargin+t);
                            break;

                        case Gravity.BOTTOM:
                        case Gravity.BOTTOM | MyGravity.ABOVE:
                        case Gravity.BOTTOM | MyGravity.BELOW:
                            child.layout(swipeMenuLeft,  lp.bottomMargin - childHeight,
                                    swipeMenuLeft + childWidth, lp.bottomMargin);
                            break;

                        case Gravity.CENTER_VERTICAL:
                        case Gravity.CENTER_VERTICAL | MyGravity.ABOVE:
                        case MyGravity.ABOVE://默认垂直居中显示
                        case Gravity.CENTER_VERTICAL | MyGravity.BELOW:
                        case MyGravity.BELOW://默认垂直居中显示
                            int top = height - childHeight >> 1;
                            if (top < lp.topMargin) {
                                top = lp.topMargin;
                            } else if ( height - lp.bottomMargin < top + childHeight) {
                                top = height - lp.bottomMargin - childHeight;
                            }
                            child.layout(swipeMenuLeft, top, swipeMenuLeft + childWidth,
                                    top + childHeight);
                            break;
                        default:
                            child.layout(swipeMenuLeft,lp.topMargin,swipeMenuLeft+childWidth,
                                    lp.topMargin+childHeight);
                            break;
                    }

                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }




//    @Override
//    protected int getChildDrawingOrder(int childCount, int i) {
//       // Log.i(TAG, "childCount=" + childCount + ",i=" + i);
//        //TODO
//        return isItemView(getChildAt(i)) ? 1 : 0;
//    }


    boolean isItemView(View child) {
        return ((LayoutParams) child.getLayoutParams()).gravity == LayoutParams.NO_GRAVITY;
    }

    boolean isSwipeMenuView(View child) {
        int gravity = ((LayoutParams) child.getLayoutParams()).gravity;

        int absGravity = GravityCompat.getAbsoluteGravity(gravity,
                ViewCompat.getLayoutDirection(child));

        return (absGravity & Gravity.LEFT) != 0 || (absGravity & Gravity.RIGHT) != 0;
    }


    @Override
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    boolean checkSwipeMenuViewAbsoluteGravity(View swipeMenuView, int checkFor) {
        int absGravity = getSwipeMenuViewAbsoluteGravity(swipeMenuView);
        return (absGravity & checkFor) == checkFor;
    }

    int getSwipeMenuViewAbsoluteGravity(View swipeMenuView) {
        int gravity = ((LayoutParams) swipeMenuView.getLayoutParams()).gravity;
        return GravityCompat.getAbsoluteGravity(gravity,
                ViewCompat.getLayoutDirection(this));
    }

    private View findSwipeMenuViewByGravity(int gravity) {
        final int absHorizontalGravity = GravityCompat.getAbsoluteGravity(gravity,
                ViewCompat.getLayoutDirection(this)) & Gravity.HORIZONTAL_GRAVITY_MASK;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            final int childAbsGravity = getSwipeMenuViewAbsoluteGravity(child);
            if ((childAbsGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == absHorizontalGravity) {
                return child;
            }
        }
        return null;
    }

    public View getItemView() {
        return mItemView;
    }

    public View getMenuView() {
        return mMenuView;
    }


    //TODO 考虑 margin
    private Rect mRect = new Rect();
    public View findChildViewUnder(float x, float y) {
        mItemView.getHitRect(mRect);
        Log.i(TAG, "findChildViewUnder////" + "x=" + x + ",y=" + y+"//rect="+mRect.left+","+mRect
                .top+","+mRect.right+","+mRect.bottom);
        if (mRect.contains((int) x, (int) y)) {
            return mItemView;
        }
        mMenuView.getHitRect(mRect);
        if (mRect.contains((int) x, (int) y)) {
           return mMenuView;
        }
        return null;
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public static final int NO_GRAVITY = -1;

        public static final int DEFAULT_GRAVITY = Gravity.TOP | Gravity.LEFT;

        public int gravity = NO_GRAVITY;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height,int gravity) {
            super(width, height);
            this.gravity=gravity;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a=c.obtainStyledAttributes(attrs,R.styleable.SwipeMenuLayout_Layout);
            gravity=a.getInt(R.styleable.SwipeMenuLayout_Layout_layout_gravity,NO_GRAVITY);
            a.recycle();

        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            gravity=source.gravity;
        }
    }

    //-------------------------------------------------------------------


    public int getState() {
        return mState;
    }

    public int getDetailState() {
        return mDetailState;
    }

    public boolean isOpened() {
        return mDetailState == STATE_DETAIL_OPENED;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean handled = super.onInterceptTouchEvent(ev);

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onSwipe(ev);
                break;
            case MotionEvent.ACTION_MOVE:

                final float absDiffX = Math.abs(mInitialMotionX - ev.getX());
                Log.e(TAG, "onInterceptTouchEvent////ACTION_MOVE////diffX=" + absDiffX +
                        "///mTouchSlop" +
                        "=" + mTouchSlop);
                // TouchSlop 避免过于对触摸列表里视图侧滑菜单滑动事件判断的敏感度，
                // 从而达到在一定的 ACTION_MOVE 范围内不视为侧滑手势，达到列表项在正常的触摸点击手势范围内
                if (absDiffX > mTouchSlop) {
                    handled = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                //点击 ItemView 或 菜单 时自动关闭菜单
                //当菜单已经打开，点击 ItemView 时拦截该事件，为了不让 ItemView 产生 onClick 事件和点击 UI 效果
                View touchedGrandChild = findChildViewUnder(ev.getX(), ev.getY());
                boolean canCloseMenu = isOpened() &&
                        mState!=SwipeMenuLayout.STATE_DRAGGING;
                if (canCloseMenu) {
                    Log.i(TAG,"onInterceptTouchEvent_ACTION_UP/////--->SmoothCloseMenu");
                    smoothCloseMenu();
                    handled = touchedGrandChild == mItemView;
                }

                break;
            default:
                break;
        }

        Log.e(TAG, "***************onInterceptTouchEvent**********handled***" + handled + "," +
                MotionEvent.actionToString(ev.getAction()));

        return handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean handled=super.onTouchEvent(event);

        int action=event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                handled=onSwipe(event);
                Log.e(TAG,"***************onTouchEvent*******handled******"+handled+MotionEvent
                        .actionToString
                        (event.getAction()));
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG,"***************onTouchEvent*******ACTION_CANCEL******"+"smoothCloseMenu");
                smoothCloseMenu();
            default:
                break;
        }

        Log.e(TAG,"onTouchEvent/////"+handled+MotionEvent
                .actionToString
                        (event.getAction())+",,,handled====>"+handled);
        return handled;
    }

    @Override
    public void computeScroll() {
        if (mDetailState==STATE_DETAIL_OPENING) {
            mCloseScroller.abortAnimation();
            if (mOpenScroller.computeScrollOffset()) {
                Log.e(TAG, "mOpenScroller.getCurX====" + mOpenScroller.getCurrX()+",getFinalX"+mOpenScroller.getFinalX());
                swipe(mOpenScroller.getCurrX());
                postInvalidate();
            } else if (mOpenScroller.isFinished()) {
                mState=STATE_IDLE;
                mDetailState=STATE_DETAIL_OPENED;
            }
        } else if (mDetailState==STATE_DETAIL_CLOSING) {
            mOpenScroller.abortAnimation();
            if (mCloseScroller.computeScrollOffset()) {
                swipe(mCloseScroller.getCurrX());
                postInvalidate();
            } else if (mCloseScroller.isFinished()) {
                mState=STATE_IDLE;
               mDetailState=STATE_DETAIL_CLOSED;
            }
        }
    }


    private float mOldMoveX;
    private static final int NO_VIEW_POSITION = -1;
    private int mItemViewRight = NO_VIEW_POSITION;
    public boolean onSwipe(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        int action=ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG,"ACTION_DOWN===="+ev.getX());

                if (mState!=STATE_IDLE) {
                    mState = STATE_IDLE;
                }

                mInitialMotionX=ev.getX();
                mInitialMotionY=ev.getY();
                mOldMoveX=mInitialMotionX;
                break;
            case MotionEvent.ACTION_MOVE:

                //当前 MenuLayout 或 ItemView 被用户拖拽
                if (mState != STATE_DRAGGING) {
                    mState = STATE_DRAGGING;
                }

                final float curX = ev.getX();
                final float curDx = curX - mOldMoveX;

                mItemViewRight = swipeManually(curDx);

                mOldMoveX = curX;

                break;
            case MotionEvent.ACTION_UP:

                Log.i(TAG, "ACTION_UP"+ev.getX()+",,mOldMoveX=="+mOldMoveX);

                final int upX = (int) ev.getX();

                //这里注意 upX可能
                final int dx = (int) (upX - mInitialMotionX);

                if (dx < 0) {
                    Log.i(TAG,"dx < 0");
                    // 当手指向左滑动打开菜单 随后 向右 Fling 关闭菜单时需要判断当前 Fling 的方向和是否可以打开菜单两个条件
                    //
                    if (mFlingDirection == FLING_LEFT ||
                            (mFlingDirection != FLING_RIGHT && canOpenMenu()))
                    {
                        Log.i(TAG,
                                "dx < 0 ==== smoothOpenMenu" + "/////" + mIsFling + ",FLING_LEFT");
                        smoothOpenMenu();
                    } else if (mFlingDirection == FLING_RIGHT || canCloseMenu())
                    {
                        Log.i(TAG,"dx < 0 ==== smoothCloseMenu"+"/////"+mIsFling+",FLING_RIGHT");
                        smoothCloseMenu();
                    }
                } else {
                    Log.i(TAG,"dx > 0");
                    if (mFlingDirection == FLING_RIGHT ||
                            (mFlingDirection != FLING_LEFT && canCloseMenu()))
                    {
                        Log.i(TAG,
                                "dx > 0==== smoothCloseMenu" + "/////" + mIsFling + ",FLING_RIGHT");
                        smoothCloseMenu();
                    } else if (mFlingDirection == FLING_LEFT || canOpenMenu())
                    {
                        Log.i(TAG, "dx > 0==== smoothOpenMenu"+"/////"+mIsFling+",FLING_LEFT");
                        smoothOpenMenu();
                    }
                }

                break;
            default:
                break;
        }
        return true;
    }

    private boolean canOpenMenu() {
        Log.i(TAG,"canOpenMenu_mItemViewRight======>"+mItemViewRight);
        return mItemViewRight != NO_VIEW_POSITION &&
                mItemViewRight < mMenuView.getRight() - (mMenuView.getWidth() >> 1);
    }

    private boolean canCloseMenu() {
        Log.i(TAG,"canCloseMenu_mItemViewRight======>"+mItemViewRight);
        return mItemViewRight != NO_VIEW_POSITION &&
                mItemViewRight >= mMenuView.getRight() - (mMenuView.getWidth() >> 1);
    }

    /**
     * 动画侧滑方法
     * @param x ItemView 的 左上角横坐标
     */
    private void swipe(int x) {

      final  View swipeMenuView=mMenuView;
        LayoutParams lp_mv = (LayoutParams) swipeMenuView.getLayoutParams();
//        LayoutParams lp_iv = (LayoutParams) itemView.getLayoutParams();
//        View menuView = findSwipeMenuViewByGravity(mMenuGravity);
//        LayoutParams lp_mv = (LayoutParams) menuView.getLayoutParams();

        //此处不应判断并限制，因为 Scroller 可能设置了其他 Interpolator
//        if (x < -menuView.getMeasuredWidth() - lp_mv.rightMargin) {
//            x = -menuView.getMeasuredWidth() - lp_mv.rightMargin;
//        }
//        if (x > lp_iv.leftMargin) {
//            x = lp_iv.leftMargin;
//        }

        if (checkSwipeMenuViewAbsoluteGravity(swipeMenuView, Gravity.LEFT)) {
            if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {

            } else if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.ABOVE) {

            } else {

            }
        } else {
            if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {

                swipeItemView(x,false);

            } else if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.ABOVE) {

            } else {
                final int itemViewRight = swipeItemView(x, true);
                swipeMenuView(itemViewRight,true);
            }
        }

    }

    private void swipeMenuView(int itemViewRight, boolean toghther) {
        View itemView = mItemView;
        LayoutParams lp_iv = (LayoutParams) itemView.getLayoutParams();
        View swipeMenuView = mMenuView;
        LayoutParams lp_mv = (LayoutParams) swipeMenuView.getLayoutParams();

        final int swipeMenuWidth = swipeMenuView.getMeasuredWidth();

        final int l=itemViewRight+lp_iv.rightMargin+lp_mv.leftMargin;

        swipeMenuView.layout(l,swipeMenuView.getTop(),l+swipeMenuWidth,swipeMenuView.getBottom());
    }

    private int swipeItemView(int x, boolean toghther) {
        View itemView = mItemView;
        itemView.layout(x, itemView.getTop(), itemView.getMeasuredWidth() + x,
                itemView.getBottom());
        return itemView.getRight();
    }

    /**
     * 手动侧滑方法,用户一直拖拽移动时，实时更新 ItemView 或者/和 SwipeMenuView 的位置
     * @param dx 手指实时移动的距离(newMoveX-oldMoveX)
     */
    private int swipeManually(final float dx) {

        int itemViewRight=NO_VIEW_POSITION;

        if ((dx < 0 && mDetailState == STATE_DETAIL_OPENED) || (dx > 0 && mDetailState == STATE_DETAIL_CLOSED)) {
            Log.i(TAG, "&&&&&&&&&&&& SwipeManually Break************************"+(dx < 0 && mDetailState
                    == STATE_DETAIL_OPENED?"OPEN":"CLOSE"));
            return NO_VIEW_POSITION;
        }

        View itemView = mItemView;
        LayoutParams lp_iv = (LayoutParams) itemView.getLayoutParams();
        View swipeMenuView = mMenuView;
        LayoutParams lp_mv = (LayoutParams) swipeMenuView.getLayoutParams();

        if (checkSwipeMenuViewAbsoluteGravity(swipeMenuView, Gravity.LEFT)) {
            if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {

            } else if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.ABOVE) {

            } else {

            }
        } else {
            if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {

                itemViewRight=swipeItemViewManually(dx,false);

            } else if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.ABOVE) {

            } else {
                itemViewRight = swipeItemViewManually(dx, true);
                swipeMenuViewManually(true,itemViewRight);
            }
        }

        return itemViewRight;
    }

    private void swipeMenuViewManually(boolean swipeTogether,int itemViewRight) {
//        Log.i(TAG,"swipeMenuViewManually------->"+dx);
        View itemView = mItemView;
        View swipeMenuView = mMenuView;
        LayoutParams lp_mv = (LayoutParams) swipeMenuView.getLayoutParams();
        LayoutParams lp_iv = (LayoutParams) itemView.getLayoutParams();
        final int swipeMenuWidth = swipeMenuView.getMeasuredWidth();
        final int itemViewWidth = itemView.getMeasuredWidth();
    //    Log.e(TAG,"swipeMenuView.getLeft()===="+swipeMenuView.getLeft());
        int l = itemViewRight+lp_mv.leftMargin+lp_iv.rightMargin;
//        if (l < itemViewWidth + lp_iv.rightMargin + lp_iv.leftMargin - swipeMenuWidth -
//                lp_mv.leftMargin)
//        {
//            l = itemViewWidth + lp_iv.rightMargin + lp_iv.leftMargin - swipeMenuWidth -
//                    lp_mv.leftMargin;
//        } else if (l > itemViewWidth + lp_iv.rightMargin + lp_iv.leftMargin +
//                lp_mv.leftMargin)
//        {
//            l = itemViewWidth + lp_iv.rightMargin + lp_iv.leftMargin  +
//                    lp_mv.leftMargin;
//        }
        swipeMenuView.layout(l, swipeMenuView.getTop(), l + swipeMenuWidth,
                swipeMenuView.getBottom());
    }

    private int swipeItemViewManually(float dx, boolean swipeTogether) {
     //   Log.i(TAG,"swipeItemViewManually------->"+dx);
        View itemView = mItemView;
        LayoutParams lp_iv = (LayoutParams) itemView.getLayoutParams();

        View swipeMenuView = mMenuView;
        LayoutParams lp_mv = (LayoutParams) swipeMenuView.getLayoutParams();

        int itemViewWidth = itemView.getMeasuredWidth();
        int swipeMenuWidth = swipeMenuView.getMeasuredWidth();


        final int itemViewLeft = itemView.getLeft();

        int l = (int) (itemViewLeft + dx);

        if (swipeTogether) {
            if (l < -swipeMenuWidth - lp_mv.leftMargin+lp_iv.leftMargin) {
                l = -swipeMenuWidth - lp_mv.leftMargin+lp_iv.leftMargin;
            } else if (l > lp_iv.leftMargin) {
                l = lp_iv.leftMargin;
            }
        } else {
            if (l < -swipeMenuWidth + lp_iv.leftMargin - lp_mv.rightMargin) {
                l = -swipeMenuWidth + lp_iv.leftMargin - lp_mv.rightMargin;
            } else if (l > lp_iv.leftMargin) {
                l = lp_iv.leftMargin;
            }
        }

        itemView.layout(l, itemView.getTop(), l + itemViewWidth, itemView.getBottom());

        if (l == -swipeMenuWidth + lp_iv.leftMargin -
                (swipeTogether ? lp_mv.leftMargin : lp_mv.rightMargin))
        {
            Log.i(TAG, "OPENED");
            mDetailState = STATE_DETAIL_OPENED;
        } else if (l == lp_iv.leftMargin) {
            mDetailState = STATE_DETAIL_CLOSED;
           Log.i(TAG, "CLOSED");
        } else {
            Log.i(TAG, "MOVING");
            mDetailState = STATE_DETAIL_MOVING;
        }

        return itemView.getRight();
    }

    public void smoothOpenMenu() {

        if (mDetailState==STATE_DETAIL_OPENED) {
            Log.i(TAG,"smoothOpenMenu break ,current state="+STATE_DETAIL_OPENED);
            mState=STATE_IDLE;
           return;
        }

        mState=STATE_SETTLING;
        mDetailState=STATE_DETAIL_OPENING;

        View itemView = mItemView;
        LayoutParams lp_iv= (LayoutParams) itemView.getLayoutParams();
        View menuView = mMenuView;
        LayoutParams lp_mv= (LayoutParams) menuView.getLayoutParams();



//        Log.i(TAG,"itemView.getLeft()="+itemView.getLeft()+",menuView.getLeft()=" +
//                ""+menuView.getLeft()+",dx------------>"+dx+",menuView.rightMargin="+lp_mv.rightMargin);

        int dx=0;

        if (checkSwipeMenuViewAbsoluteGravity(menuView, Gravity.LEFT)) {
            if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {

            } else if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.ABOVE) {

            } else {

            }
        } else {
            if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.BELOW) {
                final int dis=itemView.getRight()-menuView.getLeft();
                final boolean isOver = dis < 0;
                dx = isOver ? Math.abs(dis) - lp_iv.rightMargin : -dis - lp_iv.rightMargin;

            } else if ((lp_mv.gravity & MyGravity.DEEP_GRAVITY_MASK) == MyGravity.ABOVE) {

            } else {
                //TODO 考虑菜单 leftMargin过长问题
                dx = getMeasuredWidth() - menuView.getRight();
            }
        }

        mOpenScroller.startScroll(itemView.getLeft(), 0, dx, 0, DEFAULT_SCROLL_OPEN_DURATION);

        postInvalidate();

    }


    public void smoothCloseMenu() {
        if (mDetailState == STATE_DETAIL_CLOSED) {
            Log.i(TAG,"smoothCloseMenu break ,current state="+STATE_DETAIL_CLOSED);
            mState=STATE_IDLE;
            return;
        }

        mState=STATE_SETTLING;
        mDetailState = STATE_DETAIL_CLOSING;

        View itemView = mItemView;
        LayoutParams lp = (LayoutParams) itemView.getLayoutParams();
        mCloseScroller.startScroll(itemView.getLeft(), 0,
                itemView.getMeasuredWidth() + lp.leftMargin - itemView.getRight(), 0,
                DEFAULT_SCROLL_CLOSE_DURATION);
        postInvalidate();

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private int absGravity;

        @Override
        public boolean onDown(MotionEvent e) {
            mIsFling = false;
            mFlingDirection = NO_FLING;
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            Log.i(TAG, "onFling_velocityX=" + velocityX + ",velocityY=" + velocityY);

            if (Math.abs(velocityX) >= mMinFlingVelocityX) {
                Log.i(TAG, "onFling------->" + mMinFlingVelocityX);
                mIsFling = true;
                mFlingDirection = velocityX < 0 ? FLING_LEFT : FLING_RIGHT;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }

}
