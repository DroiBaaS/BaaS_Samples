package com.droi.sample.otplogin;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.droi.sample.otplogin.fragments.ConfirmFragment;
import com.droi.sample.otplogin.fragments.LoginFragment;
import com.droi.sample.otplogin.fragments.MainFragment;
import com.droi.sdk.core.DroiUser;

/**
 * Created by skyer on 2017/9/4.
 */

public class FragmentNavigator {

    private static final String PAGE_TAG = "PAGE";

    public static void naviMain(AppCompatActivity activity, boolean clearPopupQueue) {
        MainFragment fragment = new MainFragment();

        FragmentManager mgr = activity.getSupportFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();

        if (clearPopupQueue)
            // Clear all back stack
            mgr.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        transaction.replace(R.id.fragment_base, fragment);
        transaction.commit();
    }

    public static void naviLogin(AppCompatActivity activity) {
        LoginFragment fragment = new LoginFragment();

        FragmentManager mgr = activity.getSupportFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();

        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.fragment_base, fragment);
        transaction.addToBackStack(PAGE_TAG);
        transaction.commit();
    }

    public static void naviConfirmLogin(AppCompatActivity activity, String otpValue, DroiUser.OtpType type) {
        ConfirmFragment fragment = new ConfirmFragment();

        Bundle bundle = new Bundle();
        bundle.putString("type", type.name());
        bundle.putString("value", otpValue);

        fragment.setArguments(bundle);

        FragmentManager mgr = activity.getSupportFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();

        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.fragment_base, fragment);
        transaction.addToBackStack(PAGE_TAG);
        transaction.commit();
    }
}
