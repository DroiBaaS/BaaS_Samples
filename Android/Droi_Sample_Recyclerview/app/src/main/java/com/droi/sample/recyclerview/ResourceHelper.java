/*
 * Copyright (c) 2017-present Shanghai Droi Technology Co., Ltd.
 * All rights reserved.
 */

package com.droi.sample.recyclerview;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;

public class ResourceHelper {
    public static Uri getResourceUri(Resources resource, int resourceId){
        return new  Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resource.getResourcePackageName(resourceId))
                .appendPath(resource.getResourceTypeName(resourceId))
                .appendPath(resource.getResourceEntryName(resourceId))
                .build();
    }
}
