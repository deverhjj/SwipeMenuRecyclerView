package com.jhj.open.swipemenurecyclerview;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Window w = getWindow();
//        View decorView = w.getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        switchStatusbarMode(this, true, Color.TRANSPARENT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
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
