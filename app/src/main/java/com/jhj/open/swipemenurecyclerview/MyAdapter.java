package com.jhj.open.swipemenurecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by jhj_Plus on 2016/5/15.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "MyAdapter";

    private Context mContext;

    public MyAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG,"onCreateViewHolder----++-++-----------");
        return new MyViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 18;
    }

    public ItemDecor addItemDecor() {
        return new ItemDecor();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.layout_content_parent).setOnClickListener(this);
            Log.e(TAG,"itemView==>"+itemView+",,tag==>"+itemView.getTag());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, getAdapterPosition() + "", Toast.LENGTH_SHORT).show();
        }

    }

    class ItemDecor extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                RecyclerView.State state)
        {
            outRect.set(0, 164, 0, 0);
        }
    }

}
