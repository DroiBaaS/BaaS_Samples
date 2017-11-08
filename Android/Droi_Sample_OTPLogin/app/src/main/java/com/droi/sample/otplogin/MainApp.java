package com.droi.sample.otplogin;

import android.app.Application;

import com.droi.sample.otplogin.models.MyUser;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiUser;

/**
 * Created by skyer on 2017/9/5.
 */

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Disable Auto login anonymous feature
        DroiUser.setAutoAnonymousUser(false);

        // Register DroiObjects
        DroiObject.registerCustomClass(MyUser.class);

        // Droi Core SDK initialize
        Core.initialize(this);
    }
}
