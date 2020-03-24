package com.lianren.android.api;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.LRApplication;
import com.lianren.android.improve.account.activities.NewLoginActivity;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.main.BreakActivity;
import com.lianren.android.improve.notice.NoticeManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.api
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public abstract class CommonHttpResponseHandler extends AsyncHttpResponseHandler {

    private static final String LOG_TAG = "TextHttpRH";

    /**
     * Creates new instance with default UTF-8 encoding
     */
    public CommonHttpResponseHandler() {
        this(DEFAULT_CHARSET);
    }

    /**
     * Creates new instance with given string encoding
     *
     * @param encoding String encoding, see {@link #setCharset(String)}
     */
    public CommonHttpResponseHandler(String encoding) {
        super();
        setCharset(encoding);
    }

    /**
     * Attempts to encode response bytes as string of set encoding
     *
     * @param charset     charset to create string with
     * @param stringBytes response bytes
     * @return String of set encoding or null
     */
    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            String toReturn = (stringBytes == null) ? null : new String(stringBytes, charset);
            if (toReturn != null && toReturn.startsWith(UTF8_BOM)) {
                return toReturn.substring(1);
            }
            return toReturn;
        } catch (UnsupportedEncodingException e) {
            AsyncHttpClient.log.e(LOG_TAG, "Encoding response into string failed", e);
            return null;
        }
    }

    /**
     * Called when request fails
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     * @param throwable      throwable returned when processing request
     */
    public abstract void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable);

    /**
     * Called when request succeeds
     *
     * @param statusCode     http response status line
     * @param headers        response headers if any
     * @param responseString string response of given charset
     */
    public abstract void onSuccess(int statusCode, Header[] headers, String responseString);

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        Log.i("http_result_onSuccess", getResponseString(responseBytes, getCharset()));
        try {
            Type type = new TypeToken<ResultBean>() {
            }.getType();
//            if (resultBean.code == ResultBean.RESULT_SUCCESS) {
//                onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));
//            } else if (resultBean.code == ResultBean.RESULT_FAIL) {
//                onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));
//            } else if (resultBean.code == ResultBean.UNAUTHORIZED) {
//                NoticeManager.clear(LRApplication.getInstance(), NoticeManager.FLAG_CLEAR_ALL);
//                NoticeManager.exitServer(LRApplication.getInstance());
//                NewLoginActivity.show(LRApplication.context(), getResponseString(responseBytes, getCharset()));
//            } else if (resultBean.code == ResultBean.VIP) {
//                BreakActivity.show(LRApplication.context(), getResponseString(responseBytes, getCharset()));
//                NoticeManager.exitServer(LRApplication.getInstance());
//            } else if (resultBean.code == ResultBean.REAL_NAME) {
//                BreakActivity.show(LRApplication.context(), getResponseString(responseBytes, getCharset()));
//                NoticeManager.exitServer(LRApplication.getInstance());
//            } else if (resultBean.code == ResultBean.USER_INFO) {
//                BreakActivity.show(LRApplication.context(), getResponseString(responseBytes, getCharset()));
//                NoticeManager.exitServer(LRApplication.getInstance());
//            } else if (resultBean.code == ResultBean.NO_HEAD) {
//                BreakActivity.show(LRApplication.context(), getResponseString(responseBytes, getCharset()));
//                NoticeManager.exitServer(LRApplication.getInstance());
//            }
            ResultBean resultBean = AppOperator.createGson().fromJson(getResponseString(responseBytes, getCharset()), type);
            if (resultBean.code == ResultBean.RESULT_SUCCESS || resultBean.code == ResultBean.RESULT_FAIL) {
                onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));
            } else if (resultBean.isUnauthorizedError()) { //登录失效
                NoticeManager.clear(LRApplication.getInstance(), NoticeManager.FLAG_CLEAR_ALL);
                NoticeManager.exitServer(LRApplication.getInstance());
                JPushInterface.clearAllNotifications(LRApplication.getInstance());
                NewLoginActivity.show(LRApplication.context(), getResponseString(responseBytes, getCharset()));
            } else {
                BreakActivity.show(LRApplication.context(), getResponseString(responseBytes, getCharset()));
                onSuccess(statusCode, headers, getResponseString(responseBytes, getCharset()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), e);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        onFailure(statusCode, headers, getResponseString(responseBytes, getCharset()), throwable);
    }
}

