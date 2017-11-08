package com.droi.sample.otplogin.models;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiUser;

/**
 * Created by skyer on 2017/9/6.
 */

public class MyUser extends DroiUser {
    @DroiExpose
    private String nickName;

    public void setNickName(String name) {
        nickName = name;
    }

    public String getNickName() {
        return nickName;
    }
}
