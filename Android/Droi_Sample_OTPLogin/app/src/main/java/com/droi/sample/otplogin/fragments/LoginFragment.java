package com.droi.sample.otplogin.fragments;

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
import android.widget.RadioButton;

import com.droi.sample.otplogin.FragmentNavigator;
import com.droi.sample.otplogin.R;
import com.droi.sample.otplogin.Utils;
import com.droi.sample.otplogin.models.MyUser;
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

    @BindView(R.id.otp_phone)
    RadioButton otpPhone;

    @BindView(R.id.otp_mail)
    RadioButton otpEmail;

    @BindView(R.id.otp_value)
    EditText otpValue;

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

    @OnClick(R.id.send_otp)
    public void onSendOtpLoginClick() {
        Utils.hideInputKeyboard(getActivity());

        final DroiUser.OtpType type = otpPhone.isChecked() ? DroiUser.OtpType.PHONE : DroiUser.OtpType.EMAIL;
        final String otpValue = this.otpValue.getText().toString();

        DroiUser.requestOTPInBackground(otpValue, type, new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                if (!droiError.isOk()) {
                    String msg = getResources().getString(R.string.login_fail, droiError.toString());
                    Snackbar.make(getActivity().findViewById(R.id.root_view), msg, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Snackbar.make(getActivity().findViewById(R.id.root_view), R.string.otp_sent, Snackbar.LENGTH_SHORT).show();
                FragmentNavigator.naviConfirmLogin((AppCompatActivity)getActivity(), otpValue, type);
            }
        });
    }
}
