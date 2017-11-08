package com.droi.sample.oauthlogin;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by skyer on 2017/9/6.
 */

public class Utils {
    public static void hideInputKeyboard(Activity activity) {
        if (activity == null)
            return;

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager ims = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            ims.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
