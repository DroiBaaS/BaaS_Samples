package com.droi.sample.oauthlogin;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droi.sample.oauthlogin.models.MyUser;
import com.droi.sample.oauthlogin.wxapi.WXEntryActivity;

/**
 * Created by skyer on 2017/9/6.
 */

public class SideMenu implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView naviView;
    private TextView nicknameView;
    private WXEntryActivity activity;

    public SideMenu(WXEntryActivity activity) {
        this.activity = activity;

        // Init view
        naviView = activity.findViewById(R.id.nav_main);
        ViewGroup naviHeaderView = (ViewGroup) naviView.getHeaderView(0);
        nicknameView = naviHeaderView.findViewById(R.id.user_name);

        // Config NavigationView
        naviView.setNavigationItemSelectedListener(this);

        refresh();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                MyUser curUser = MyUser.getCurrentUser(MyUser.class);
                if (curUser != null && curUser.isLoggedIn())
                    curUser.logout();
                Snackbar.make(activity.findViewById(R.id.root_view), R.string.user_logout, Snackbar.LENGTH_SHORT).show();
                activity.getDrawerLayout().closeDrawer(GravityCompat.START);
                FragmentNavigator.naviMain(activity, true);
                return true;
            }
        }
        return false;
    }

    public void refresh() {
        MyUser curUser = MyUser.getCurrentUser(MyUser.class);
        if (curUser == null || !curUser.isLoggedIn())
            nicknameView.setText("");
        else
            nicknameView.setText(curUser.getNickName());
    }
}
