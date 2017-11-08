package com.droi.sample.otplogin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.droi.sample.otplogin.FragmentNavigator;
import com.droi.sample.otplogin.R;
import com.droi.sample.otplogin.Utils;
import com.droi.sample.otplogin.models.MyUser;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by skyer on 2017/10/2.
 */

public class ConfirmFragment extends Fragment {

    @BindView(R.id.otp_code)
    EditText otpCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        Utils.hideInputKeyboard(getActivity());

        super.onDestroyView();
    }

    @OnClick(R.id.confirm_login)
    public void onConfirmOtpCodeLogin() {
        Utils.hideInputKeyboard(getActivity());

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey("type")) {
            throw new RuntimeException("No type in login confirmation.");
        }

        String value = bundle.getString("value");
        DroiUser.OtpType type = DroiUser.OtpType.valueOf(bundle.getString("type"));

        DroiUser.loginOTPInBackground(value, type, otpCode.getText().toString(), MyUser.class, new DroiCallback<MyUser>() {
            @Override
            public void result(MyUser droiUser, DroiError droiError) {

                if (!droiError.isOk()) {
                    Snackbar.make(getActivity().findViewById(R.id.root_view), getResources().getString(R.string.login_fail, droiError.toString()), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                droiUser.setNickName("Skyer");
                droiUser.saveInBackground(new DroiCallback<Boolean>() {
                    @Override
                    public void result(Boolean aBoolean, DroiError droiError) {
                        String msg;
                        if (!droiError.isOk())
                            msg = getResources().getString(R.string.login_fail, droiError.toString());
                        else
                            msg = getResources().getString(R.string.login_ok);

                        Snackbar.make(getActivity().findViewById(R.id.root_view), msg, Snackbar.LENGTH_SHORT).show();
                        FragmentNavigator.naviMain((AppCompatActivity)getActivity(), true);
                    }
                });
            }
        });
    }
}
