package com.lianren.android.api;

import com.loopj.android.http.AsyncHttpClient;

/**
 * @package: com.lianren.android.api
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class API {
    public static AsyncHttpClient mClient = new AsyncHttpClient();

    static {
        mClient.setURLEncodingEnabled(false);
    }
}
