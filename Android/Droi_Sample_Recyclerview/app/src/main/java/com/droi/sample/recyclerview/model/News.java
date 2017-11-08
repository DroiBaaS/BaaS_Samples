package com.droi.sample.recyclerview.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by james on 2017/9/5.
 */

public class News extends DroiObject {

    public static final int TYPE_STOCK_UP = 0;
    public static final int TYPE_STOCK_FLAT = 1;
    public static final int TYPE_STOCK_DOWN = 2;
    public static final int TYPE_BREAKING = 3;

    public static final String[] TYPE_STRS = { "股市正在上漲", "股市持平", "股市下跌", "最新消息"};

    @DroiExpose
    public String titile;

    @DroiExpose
    public int type;

}
