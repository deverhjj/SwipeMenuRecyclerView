package com.jhj.open.swipemenurecyclerview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * <p>Author: Huajian Jiang
 * <br>Date: 2019/7/5
 * <br>Email: developer.huajianjiang@gmail.com
 */
public class MainAdapter extends FragmentPagerAdapter {

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return i == 0 ? new SlideMenuFragment() : new MainActivityFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
