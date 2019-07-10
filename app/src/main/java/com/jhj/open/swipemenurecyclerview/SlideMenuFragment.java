package com.jhj.open.swipemenurecyclerview;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>Author: Huajian Jiang
 * <br>Date: 2019/7/5
 * <br>Email: developer.huajianjiang@gmail.com
 */
public class SlideMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_slide_menu, container, false);
    }

}
