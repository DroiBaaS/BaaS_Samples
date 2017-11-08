package com.droi.sample.push;

import android.app.Application;

import com.droi.sample.push.models.MyUser;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.push.DroiPush;

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

        // Droi Core SDK initialization
        Core.initialize(this);

        // Droi Push initialization
        DroiPush.initialize(this, "r9PODZva2zZO4EU1DynZ20ZKZYA0c7nZB3eGQ8BLLCaPWDeOBbLQaeNKQalxXFKd");
    }
}
