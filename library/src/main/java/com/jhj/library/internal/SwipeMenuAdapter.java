package com.jhj.library.internal;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jhj.library.widget.SwipeMenuLayout;

/**
 * Created by jhj_Plus on 2016/4/11.
 */
public class SwipeMenuAdapter extends WrapperAdapter {
    private static final String TAG = "SwipeMenuAdapter";

    private Context mContext;

    private RecyclerView.Adapter mOuterAdapter;


    public SwipeMenuAdapter(Context context, RecyclerView.Adapter outerAdapter) {
        mContext = context;
        mOuterAdapter = outerAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = mOuterAdapter.onCreateViewHolder(parent, viewType);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mOuterAdapter.onBindViewHolder(holder,position);
    }

    @Override
    public int getItemCount() {
        return mOuterAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mOuterAdapter.getItemViewType(position);
    }


    @Override
    public RecyclerView.Adapter getWrappedAdapter() {
        return mOuterAdapter;
    }

    public interface OnItemViewClickListener {
        void onItemViewClicked(View view, int position);
    }

//    public void setOnItemViewClickListener(OnItemViewClickListener listener) {
//        mListener=listener;
//    }

    private View.OnClickListener mItemViewClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,"00000000000000",Toast.LENGTH_SHORT).show();
        }
    };


    private View findItemView(View child) {
        Log.e(TAG,"child========>"+child);
        if (child == null || !(child instanceof ViewGroup)) {
            return null;
        }

        SwipeMenuLayout swipeMenuLayout = findSwipeMenuLayout(child);
        return swipeMenuLayout.getItemView();
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

}
