package com.droi.sample.normallogin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.droi.sample.normallogin.FragmentNavigator;
import com.droi.sample.normallogin.R;
import com.droi.sample.normallogin.Utils;
import com.droi.sample.normallogin.models.MyUser;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by skyer on 2017/9/5.
 */

public class SignupFragment extends Fragment {

    private static final String LOG_TAG = "Signup";

    @BindView(R.id.edit_userid) EditText userIdView;
    @BindView(R.id.edit_password) EditText passwordView;
    @BindView(R.id.edit_confirm) EditText confirmPwdView;
    @BindView(R.id.edit_nickname) EditText nicknameView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null)
            return;

        userIdView.setText(bundle.getString("userid"));
        passwordView.setText(bundle.getString("password"));
    }

    @Override
    public void onDestroyView() {
        Utils.hideInputKeyboard(getActivity());
        super.onDestroyView();
    }

    @OnClick(R.id.button_signup)
    @SuppressWarnings("unused")
    void onSignupClick() {
        Utils.hideInputKeyboard(getActivity());

        final String userId = userIdView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPwd = confirmPwdView.getText().toString();
        String nickname = nicknameView.getText().toString();

        // Error handling for user input data
        if (userId.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.root_view), R.string.empty_id_password, Snackbar.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmPwd)) {
            Snackbar.make(getActivity().findViewById(R.id.root_view), R.string.password_not_same, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // New MyUser and fill fields.
        MyUser user = new MyUser();
        user.setUserId(userId);
        user.setPassword(password);
        user.setNickName(nickname);

        // Try to sign up
        user.signUpInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean success, DroiError droiError) {

                View rootView = getActivity().findViewById(R.id.root_view);

                // Sign up OK !! Back to main fragment.
                if (success && droiError.isOk()) {
                    Snackbar.make(rootView, R.string.signup_ok, Snackbar.LENGTH_SHORT).show();
                    FragmentNavigator.naviMain((AppCompatActivity)getActivity(), true);
                    return;
                }

                // Sign up fail.
                int code = droiError.getCode();
                String msg;
                switch (code) {
                    case DroiError.USER_ALREADY_EXISTS:
                        msg = getResources().getString(R.string.user_exists, userId);
                        break;
                    case DroiError.USER_ALREADY_LOGIN: {
                        MyUser curUser = MyUser.getCurrentUser(MyUser.class);
                        msg = getResources().getString(R.string.user_already_login, curUser.getUserId());
                    }
                    default:
                        msg = getResources().getString(R.string.signup_fail, droiError.toString());
                }

                Log.e(LOG_TAG, msg);
                Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
