package com.droi.sample.oauthlogin.fragments;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.droi.sample.oauthlogin.FragmentNavigator;
import com.droi.sample.oauthlogin.R;
import com.droi.sample.oauthlogin.Utils;
import com.droi.sample.oauthlogin.models.MyUser;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.core.OAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;

/**
 * Created by skyer on 2017/9/5.
 */

public class LoginFragment extends Fragment {

    private OAuthProvider provider;

    // Login OAuth callback handler
    private DroiCallback<MyUser> loginCallback = new DroiCallback<MyUser>() {
        @Override
        public void result(MyUser myUser, DroiError droiError) {
            // Login fail.
            if (!droiError.isOk()) {
                String msg = getResources().getString(R.string.login_fail, droiError.toString());
                Snackbar.make(getActivity().findViewById(R.id.root_view), msg, Snackbar.LENGTH_SHORT).show();
                return;
            }

            // Login OK !
            // Change user nickname here
            myUser.setNickName("OAuth User");
            myUser.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    // Update nickname fail.
                    if (!droiError.isOk()) {
                        String msg = getResources().getString(R.string.login_fail, droiError.toString());
                        Snackbar.make(getActivity().findViewById(R.id.root_view), msg, Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // All login process done !
                    Snackbar.make(getActivity().findViewById(R.id.root_view), R.string.login_user, Snackbar.LENGTH_SHORT).show();
                    FragmentNavigator.naviMain((AppCompatActivity) getActivity(), true);
                }
            });

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Redirect onActivity result to provider.
        if (provider != null)
            provider.handleActivityResult(requestCode, resultCode, data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.login_qq)
    public void onQQLoginClick() {
        provider = OAuthProvider.createAuthProvider(OAuthProvider.AuthProvider.QQ, getActivity());
        DroiUser.loginOAuthAsync(getActivity(), provider, loginCallback, MyUser.class);
    }

    @OnClick(R.id.login_sina)
    public void onSinaLoginClick() {
        provider = OAuthProvider.createAuthProvider(OAuthProvider.AuthProvider.Sina, getActivity());
        DroiUser.loginOAuthAsync(getActivity(), provider, loginCallback, MyUser.class);
    }

    @OnClick(R.id.login_wechat)
    public void onWechatLoginClick() {
        provider = OAuthProvider.createAuthProvider(OAuthProvider.AuthProvider.Weixin, getActivity());
        DroiUser.loginOAuthAsync(getActivity(), provider, loginCallback, MyUser.class);
    }
}
