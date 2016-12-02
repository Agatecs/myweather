package com.agate.testcity.util;

/**
 * Created by Agate on 2016/11/29.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
