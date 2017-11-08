package com.droi.sample.normallogin.fragments;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
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

import com.droi.sample.normallogin.FragmentNavigator;
import com.droi.sample.normallogin.R;
import com.droi.sample.normallogin.Utils;
import com.droi.sample.normallogin.models.MyUser;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;

/**
 * Created by skyer on 2017/9/5.
 */

public class LoginFragment extends Fragment {

    @BindView(R.id.edit_userid) EditText userIdView;
    @BindView(R.id.edit_password) EditText passwordView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        Utils.hideInputKeyboard(getActivity());
        super.onDestroyView();
    }

    @OnClick(R.id.button_login)
    @SuppressWarnings("unused")
    void onLoginClick() {
        Utils.hideInputKeyboard(getActivity());

        final String userId = userIdView.getText().toString();
        String password = passwordView.getText().toString();
        if (userId.isEmpty() || password.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.root_view), R.string.empty_id_password, Snackbar.LENGTH_SHORT).show();
            return;
        }

        MyUser.loginInBackground(userId, password, MyUser.class, new DroiCallback<DroiUser>() {
            @Override
            public void result(DroiUser droiUser, DroiError droiError) {
                View rootView = getActivity().findViewById(R.id.root_view);

                if (droiError.isOk()) {
                    Snackbar.make(rootView, R.string.login_user, Snackbar.LENGTH_SHORT).show();
                    FragmentNavigator.naviMain((AppCompatActivity)getActivity(), true);
                    return;
                }

                String msg;
                switch (droiError.getCode()) {
                    case DroiError.USER_NOT_EXISTS:
                        msg = getResources().getString(R.string.user_not_exists, userId);
                        break;
                    case DroiError.USER_PASSWORD_INCORRECT:
                        msg = getResources().getString(R.string.user_wrong_password);
                        break;
                    default:
                        msg = getResources().getString(R.string.login_fail, droiError.toString());
                        break;
                }

                Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.button_signup)
    @SuppressWarnings("unused")
    void onSignupClick() {
        FragmentNavigator.naviSignup((AppCompatActivity)getActivity(), userIdView.getText().toString(), passwordView.getText().toString());
    }
}
