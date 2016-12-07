package com.agate.testcity.util;

import android.graphics.Bitmap;

/**
 * Created by Agate on 2016/11/29.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onFinish(Bitmap bitmap);
    void onError(Exception e);
}
