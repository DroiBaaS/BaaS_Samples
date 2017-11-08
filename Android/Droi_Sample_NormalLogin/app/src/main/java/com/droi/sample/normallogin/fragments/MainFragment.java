package com.droi.sample.normallogin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droi.sample.normallogin.MainActivity;
import com.droi.sample.normallogin.models.MyUser;
import com.droi.sdk.core.DroiUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.droi.sample.normallogin.FragmentNavigator;
import com.droi.sample.normallogin.R;

/**
 * Created by skyer on 2017/9/4.
 */

public class MainFragment extends Fragment {

    @BindView(R.id.login_text)
    TextView loginText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyUser currentUser = MyUser.getCurrentUser(MyUser.class);

        // No user login, switch to login/signup fragment
        if (currentUser == null || !currentUser.isLoggedIn()) {
            FragmentNavigator.naviLogin((AppCompatActivity) getActivity());
            return;
        }

        // Fill the welcome text
        loginText.setVisibility(View.VISIBLE);
        loginText.setText(getResources().getString(R.string.login_text, currentUser.getNickName()));

        // Refresh side menu for user nickname
        ((MainActivity)getActivity()).getSideMenu().refresh();
    }
}
