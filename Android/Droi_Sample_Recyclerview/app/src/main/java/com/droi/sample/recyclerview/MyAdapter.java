/*
 * Copyright (c) 2017-present Shanghai Droi Technology Co., Ltd.
 * All rights reserved.
 */

package com.droi.sample.recyclerview;

import android.net.Uri;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.droi.sample.recyclerview.model.News;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private SortedList<News> mSortedList = new SortedList<News>(News.class, new SortedList.Callback<News>() {
        @Override
        public int compare(News o1, News o2) {
            return ( - o1.getCreationTime().compareTo(o2.getCreationTime()));
        }

        @Override
        public void onChanged(int position, int count) {

        }

        @Override
        public boolean areContentsTheSame(News oldItem, News newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(News item1, News item2) {
            return false;
        }

        @Override
        public void onInserted(int position, int count) {
            notifyDataSetChanged();
        }

        @Override
        public void onRemoved(int position, int count) {

        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {

        }
    });
    private int mLayoutType;

    public MyAdapter(int layoutType) {
        mLayoutType = layoutType;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        // create a new view
        View v = null;
        if (mLayoutType == Constants.Layout_Linear) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.data_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.data_item_card, parent, false);
        }

        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        News target = mSortedList.get(position);
        holder.itemHeader.setText(target.titile);
        holder.itemDate.setText(target.getCreationTime().toString());

        // set image
        int resId = getResIdByType(target.type);
        Uri uri = ResourceHelper.getResourceUri(holder.itemImage.getResources(), resId);
        Glide.with(holder.itemImage.getContext()).load(uri).centerCrop().into(holder.itemImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    public void AddNews(News news) {
        mSortedList.add(news);
    }

    public void replaceNews(List<News> list) {
        mSortedList.beginBatchedUpdates();
        mSortedList.clear();
        mSortedList.addAll(list);
        mSortedList.endBatchedUpdates();
    }

    private int getResIdByType(int type) {
        switch (type) {
            case News.TYPE_STOCK_UP:
                return R.drawable.ic_trending_up_black_24dp;
            case News.TYPE_STOCK_FLAT:
                return R.drawable.ic_trending_flat_black_24dp;
            case News.TYPE_STOCK_DOWN:
                return R.drawable.ic_trending_down_black_24dp;
            case News.TYPE_BREAKING:
                return R.drawable.ic_grade_black_24dp;
            default:
                return R.drawable.ic_highlight_off_black_24dp;
        }
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.itemImage)
    public ImageView itemImage;

    @BindView(R.id.itemHeader)
    public TextView itemHeader;

    @BindView(R.id.itemDate)
    public TextView itemDate;


    public MyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

