package com.jhj.open.swipemenurecyclerview;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
