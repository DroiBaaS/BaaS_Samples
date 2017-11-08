package com.droi.sample.userprofile;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;

import java.util.Date;

public class MyUser extends DroiUser {

    @DroiExpose
    public String name;

    @DroiExpose
    public String address;

    @DroiExpose
    public Date birthday;

    @DroiExpose
    public boolean gender;  // True - Male, False - Female

    @DroiExpose
    public DroiFile photo;

}
