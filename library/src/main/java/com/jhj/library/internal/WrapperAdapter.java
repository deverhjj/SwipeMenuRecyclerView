package com.jhj.library.internal;

import android.support.v7.widget.RecyclerView;

/**
 * Created by jhj_Plus on 2016/4/11.
 */
public abstract class WrapperAdapter extends RecyclerView.Adapter {
    private static final String TAG = "WrapperAdapter";

    public abstract RecyclerView.Adapter getWrappedAdapter();
}
