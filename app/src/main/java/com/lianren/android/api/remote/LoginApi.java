package com.lianren.android.api.remote;

import com.alibaba.fastjson.JSONObject;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.util.TDevice;
import com.loopj.android.http.RequestParams;

import static com.lianren.android.api.ApiHttpClient.post;

/**
 * @package: com.lianren.android.api.remote
 * @user:xhkj
 * @date:2019/12/27
 * @description:
 **/
public class LoginApi {
    private static RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        if (AccountHelper.isLogin())
            params.put("user_id", AccountHelper.getUserId());
        params.put("identity_type", "phone");
        params.put("platform", "android");
        params.put("phone_mode", TDevice.getSystemModel());
        params.put("system_version", TDevice.getSystemVersion());
        params.put("app_version", TDevice.getVersionCode()+"");
        return params;
    }
    private static JSONObject getJsonObject() {
        JSONObject params = new JSONObject();
        if (AccountHelper.isLogin())
            params.put("user_id", AccountHelper.getUserId());
        params.put("identity_type", "phone");
        params.put("platform", "android");
        params.put("phone_mode", TDevice.getSystemModel());
        params.put("system_version", TDevice.getSystemVersion());
        params.put("app_version", TDevice.getVersionCode()+"");
        return params;
    }
    public static void codeSend(String phone, String send_way,CommonHttpResponseHandler handler) {
        RequestParams params = getRequestParams();
        params.put("phone", phone);
        params.put("send_way", send_way);
        post(LRUrls.CODES_SEND, params, handler);
    }
    public static void register(String registration_id,String identifier, String credential, String code, CommonHttpResponseHandler handler) {
        RequestParams params = getRequestParams();
        params.put("registration_id", registration_id);
        params.put("identifier", identifier);
        params.put("credential", credential);
        params.put("code", code);
        post(LRUrls.REGISTER, params, handler);
    }
    public static void forgetPassword(String identifier, String new_credential, String code, CommonHttpResponseHandler handler) {
        RequestParams params = getRequestParams();
        params.put("identifier", identifier);
        params.put("new_credential", new_credential);
        params.put("code", code);
        post(LRUrls.FORGET_PASSWORD, params, handler);
    }
    public static void codesVerify(String phone, String send_way, String code, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("phone", phone);
        params.put("send_way", send_way);
        params.put("code", code);
        post(LRUrls.CODES_VERIFY, params, handler);

    }
    public static void changePhone(String new_identifier, String code,  CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("new_identifier", new_identifier);
        params.put("code", code);
        post(LRUrls.CHANGE_PHONE, params, handler);
    }
    public static void passportsChange(String identifier,String credential, String new_credential,  CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("identifier", identifier);
        params.put("credential", credential);
        params.put("new_credential", new_credential);
        post(LRUrls.PASSPORTS_CHANGE_PWD, params, handler);

    }
}
