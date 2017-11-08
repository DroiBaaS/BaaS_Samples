package com.droi.sample.userprofile;

import android.app.Application;

import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initiate DroiBaaS
        Core.initialize(this);
        DroiObject.registerCustomClass( MyUser.class );
    }
}
