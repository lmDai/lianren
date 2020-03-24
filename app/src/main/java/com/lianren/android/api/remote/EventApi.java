package com.lianren.android.api.remote;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.util.TDevice;
import com.loopj.android.http.RequestParams;

import java.util.List;

import static com.lianren.android.api.ApiHttpClient.post;

/**
 * @package: com.lianren.android.api.remote
 * @user:xhkj
 * @date:2019/12/27
 * @description:
 **/
public class EventApi {
    private static RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        if (AccountHelper.isLogin())
            params.put("user_id", AccountHelper.getUserId());
        params.put("identity_type", "phone");
        params.put("platform", "android");
        params.put("phone_mode", TDevice.getSystemModel());
        params.put("system_version", TDevice.getSystemVersion());
        params.put("app_version", TDevice.getVersionCode() + "");
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
        params.put("app_version", TDevice.getVersionCode() + "");
        return params;
    }

    public static void h5Uri(String id, CommonHttpResponseHandler handler) {
        RequestParams params = getRequestParams();
        params.put("id", id);
        post(LRUrls.H5_URI, params, handler);
    }

    public static void payOrder(String payment, String order_id, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("payment", payment);
        jsonObject.put("order_id", order_id);
        post(LRUrls.PAY_ORDER, jsonObject, handler);
    }

    public static void orderList(int page, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("page", page);
        post(LRUrls.ORDER_LIST, jsonObject, handler);
    }

    public static void datingList(int page, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("page", page);
        post(LRUrls.DATING_LIST, jsonObject, handler);
    }

    public static void orderTicket(String order_id, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("order_id", order_id);
        post(LRUrls.ORDER_TICKET, jsonObject, handler);
    }

    public static void fbFeedType(String type, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("type", type);
        post(LRUrls.FB_TYPE, jsonObject, handler);
    }

    public static void feedBackCommit(String fb_obj_id, String fb_type, List<String> img_uri, String content, String contact, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        if (!TextUtils.isEmpty(fb_obj_id))
            jsonObject.put("fb_obj_id", fb_obj_id);
        if (!TextUtils.isEmpty(fb_type))
            jsonObject.put("fb_type", fb_type);
        jsonObject.put("content", content);
        if (img_uri != null && img_uri.size() > 0)
            jsonObject.put("img_uri", img_uri.get(0));
        jsonObject.put("contact_type", "phone");
        if (!TextUtils.isEmpty(contact))
            jsonObject.put("contact", contact);
        post(LRUrls.FEED_BACK_COMMIT, jsonObject, handler);
    }

    public static void orderContactUpdate(String telephone, String nickname, String order_id, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("telephone", telephone);
        jsonObject.put("nickname", nickname);
        jsonObject.put("order_id", order_id);
        post(LRUrls.ORDER_CONTACT_UPDATE, jsonObject, handler);
    }

    public static void shopFind(int page, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("page", page);
        post(LRUrls.SHOP_FIND, jsonObject, handler);
    }

    public static void orderCancel(String order_id, String reason, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("order_id", order_id);
        jsonObject.put("reason", reason);
        post(LRUrls.ORDER_CANCEL, jsonObject, handler);
    }

    public static void datingRequestDeal(int dating_id, int status, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("dating_id", dating_id);
        jsonObject.put("status", status);
        post(LRUrls.DATING_REQUEST_DEAL, jsonObject, handler);
    }
    public static void datingCancel(int dating_id,  CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("dating_id", dating_id);
        post(LRUrls.DATING_CANCEL, jsonObject, handler);
    }
    public static void orderDetail(int order_id,  CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("order_id", order_id);
        post(LRUrls.ORDER_DETAIL, jsonObject, handler);
    }
    public static void activityCollect(int activity_id,  CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("activity_id", activity_id);
        post(LRUrls.ACTIVITY_COLLECT, jsonObject, handler);
    }
}
