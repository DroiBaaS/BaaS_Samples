/*
 * Copyright (c) 2017-present Shanghai Droi Technology Co., Ltd.
 * All rights reserved.
 */

package com.droi.sample.recyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.Snackbar;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.core.TaskDispatcher;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.droi.sample.recyclerview.model.News;

public class MyFragment extends Fragment {
    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Determine the layout
        Bundle argument = getArguments();
        int layoutType = argument.getInt(Keywords.LAYOUT_TYPE);
        switch (layoutType) {
            case Constants.Layout_Grid:
                mLayoutManager = new GridLayoutManager(getContext(), 2);
                break;
            case Constants.Layout_Linear:
            default:
                mLayoutManager = new LinearLayoutManager(getContext());
                break;
        }

        mAdapter = new MyAdapter(layoutType);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        refreshNews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item =  menu.add(Menu.NONE, R.id.refresh_menu, 0, R.string.refresh);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_menu_refresh_white_24px);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_menu) {
            refreshNews();
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshNews() {
        long onedDayAgoMillis = System.currentTimeMillis() - ONE_WEEK_MILLY;
        Date oneDayAgoDate  = new Date(onedDayAgoMillis);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(News.class).where(DroiCondition.gt("_CreationTime", oneDayAgoDate)).build();

        // Query and make sure check DroiError
        query.runQueryInBackground(new DroiQueryCallback<News>() {
            @Override
            public void result(List<News> list, DroiError droiError) {
                // Query successfull, then update result to adapter
                if (droiError.isOk()) {
                    Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Latest news count is " + list.size(), Snackbar.LENGTH_SHORT).show();
                    mAdapter.replaceNews(list);
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Fail to query, check DroiError/LOGCAT first." + list.size(), Snackbar.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "Fail to query and DroiError: " + droiError.toString());
                }
            }
        });
    }


    @OnClick(R.id.add)
    @SuppressWarnings("unused")
    public void onAddClick() {
        final News tmp = new News();
        tmp.type = rand.nextInt(4);
        tmp.titile = News.TYPE_STRS[tmp.type];
        tmp.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                if (aBoolean) {
                    mAdapter.AddNews(tmp);
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Fail to add news, check DroiError/Logcat first.", Snackbar.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "Fail to add news and DroiError: " + droiError.toString());
                }
            }
        });
    }

    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    MyAdapter mAdapter;
    Random rand = new Random();
    RecyclerView.LayoutManager mLayoutManager;

    static long ONE_DAY_MILLI = 24 * 60 * 60 * 1000;
    static long ONE_WEEK_MILLY = 7 * ONE_DAY_MILLI;
    static String DISP_NAME = "CRUD_DISP";
    static String LOG_TAG = "DroiSample";

}
