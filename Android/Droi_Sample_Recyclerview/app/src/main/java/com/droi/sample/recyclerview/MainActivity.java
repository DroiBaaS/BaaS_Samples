/*
 * Copyright (c) 2017-present Shanghai Droi Technology Co., Ltd.
 * All rights reserved.
 */

package com.droi.sample.recyclerview;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;

import com.droi.sample.recyclerview.model.News;

import java.security.Key;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private boolean hasInitDroiBaas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Add ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Init DroiBaaS
        if (savedInstanceState == null || !savedInstanceState.containsKey(Keywords.HAS_INIT_BAAS) || !savedInstanceState.getBoolean(Keywords.HAS_INIT_BAAS))
            initDroiBaas();

        naviToLinearLayout();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Keywords.HAS_INIT_BAAS, hasInitDroiBaas);

        super.onSaveInstanceState(outState);
    }

    private void initDroiBaas() {
        Core.initialize(this);

        DroiObject.registerCustomClass(News.class);

        hasInitDroiBaas = true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_linearlayout) {
            naviToLinearLayout();
        } else if (id == R.id.nav_gridlayout) {
            naviToGridLayout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void naviToLinearLayout(){
        naviToFragment(Constants.Layout_Linear);
    }

    private void naviToGridLayout(){
        naviToFragment(Constants.Layout_Grid);
    }

    private void naviToFragment(int layoutType) {
        FragmentManager fm = this.getSupportFragmentManager();
        MyFragment fragment = MyFragment.newInstance();

        Bundle args = new Bundle();
        args.putInt(Keywords.LAYOUT_TYPE, layoutType);
        fragment.setArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_panel, fragment);
        ft.commit();
    }
}
