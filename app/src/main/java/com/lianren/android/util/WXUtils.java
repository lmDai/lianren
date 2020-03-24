package com.lianren.android.util;

import android.content.Context;
import android.util.Log;

import com.lianren.android.improve.bean.pay.WXpay;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXUtils {
    private static String TAG = "WXUtils";
    public static IWXAPI WXApi;
    public static String APPID;

    public static void order2WXPay(Context context, WXpay wx) {
        if (wx == null) {
            return;
        }
        APPID = wx.appid;
        String partnerId = wx.partnerid;
        String prepayId = wx.prepayid;
        String packageValue = wx.package_type;
        String nonceStr = wx.noncestr;
        String timeStamp = wx.timestamp;
        String sign = wx.sign;

        Log.i(TAG, "APP_ID:" + APPID);
        Log.i(TAG, "PARTNER_ID:" + partnerId);
        Log.i(TAG, "PREPAY_ID:" + prepayId);
        Log.i(TAG, "PACKAGE:" + packageValue);
        Log.i(TAG, "NONCE:" + nonceStr);
        Log.i(TAG, "TIME:" + timeStamp);
        Log.i(TAG, "SIGN:" + sign);

        WXApi = WXAPIFactory.createWXAPI(context, null);
        PayReq request = new PayReq();
        request.appId = APPID;
        request.partnerId = partnerId;
        request.prepayId = prepayId;
        request.packageValue = packageValue;
        request.nonceStr = nonceStr;
        request.timeStamp = timeStamp;
        request.sign = sign;

        WXApi.registerApp(APPID);
        WXApi.sendReq(request);
    }
}
