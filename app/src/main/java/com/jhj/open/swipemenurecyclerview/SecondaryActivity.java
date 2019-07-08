package com.jhj.open.swipemenurecyclerview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jhj.library.widget.SwipeMenuLayout;

public class SecondaryActivity extends AppCompatActivity {
    private static final String TAG = SecondaryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_secondary);

        Window w = getWindow();
        View decorView = w.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //        switchStatusbarMode(this, true, Color.TRANSPARENT);



        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appbar = findViewById(R.id.appbar);
        SwipeMenuLayout swipeLayout = findViewById(R.id.root);
        swipeLayout.setAutoCloseMenu(false);

        CoordinatorLayout coordinator = findViewById(R.id.coordinator);
        CollapsingToolbarLayout collapsing = findViewById(R.id.collapsing_layout);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            appbar.setStateListAnimator();
//        }

        ViewCompat.setOnApplyWindowInsetsListener(swipeLayout, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Log.e(TAG, "forceFitSysWindow>>>");
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) toolbar
                        .getLayoutParams();
                lp.topMargin = insets.getSystemWindowInsetTop();
                toolbar.setLayoutParams(lp);
                return insets;
            }
        });

        collapsing.setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static void forceFitSysWindow(final View listener, final View target) {
        ViewCompat.setOnApplyWindowInsetsListener(listener, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Log.e(TAG, "forceFitSysWindow>>>");
                target.setPadding(target.getPaddingLeft(), insets.getSystemWindowInsetTop(),
                        target.getPaddingRight(), target.getPaddingBottom());
                return insets;
            }
        });
    }

    /**
     * 检查当前设备的 SDK 版本号是否 >= 传递的 api
     * @param api 要检查的 api 版本号
     * @return 如果当前的系统版本号 >= api 则返回 {@code true} ,否则 {@code false}
     */
    public static boolean checkApi(int api) {
        return Build.VERSION.SDK_INT >= api;
    }

    public static void switchStatusbarMode(Activity ctxt, boolean lightMode, int color) {
        if (checkApi(Build.VERSION_CODES.M)) {
            Window w = ctxt.getWindow();
            View decorView = w.getDecorView();
            int flags = decorView.getSystemUiVisibility();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (lightMode) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(flags);
                w.setStatusBarColor(color);
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(flags);
                w.setStatusBarColor(color);
            }
        }
    }
}
