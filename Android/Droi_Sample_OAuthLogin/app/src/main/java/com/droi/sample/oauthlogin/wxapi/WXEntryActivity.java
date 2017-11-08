package com.droi.sample.oauthlogin.wxapi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.droi.sample.oauthlogin.FragmentNavigator;
import com.droi.sample.oauthlogin.R;
import com.droi.sample.oauthlogin.SideMenu;

import butterknife.BindView;
import butterknife.ButterKnife;

// For Weixin needed. The requested activity must named with wxapi.WXEnteryActivity
public class WXEntryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolBar;

    @BindView(R.id.drawer_main)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_main)
    NavigationView navigationView;

    private SideMenu sideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Views
        ButterKnife.bind(this);

        // Config toolbar
        setSupportActionBar(toolBar);

        // Config Toolbar toggler
        ActionBarDrawerToggle toggler = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggler);
        toggler.syncState();

        // Init Side Menu
        sideMenu = new SideMenu(this);

        // Navigate main fragment
        FragmentNavigator.naviMain(this, true);
    }

    @Override
    // Redirect onActivityResult to foreground fragment.
    // QQ, Sina needed
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();

        int count = fm.getBackStackEntryCount();
        Fragment fragment = fm.getFragments().get(count == 0 ? 0 : count-1);

        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    // Redirect onNewIntent to foreground fragment as onActivityResult
    // Weixin (WeChat) needed
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        FragmentManager fm = getSupportFragmentManager();

        int count = fm.getBackStackEntryCount();
        Fragment fragment = fm.getFragments().get(count == 0 ? 0 : count-1);

        fragment.onActivityResult(0, 0, intent);
    }

    public SideMenu getSideMenu() {
        return sideMenu;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }
}
