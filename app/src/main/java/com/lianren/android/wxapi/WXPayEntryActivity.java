package com.lianren.android.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lianren.android.improve.base.activities.BaseActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.util.WXUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public  class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";
    //private TextView tvPayResult;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WXUtils.APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        EventBus.getDefault().post(new MessageWrap(Constants.PAY_CODE,resp));

//        if (getWXPayCallback() == null) {
//            Toast.makeText(mContext, "请设置支付回调", Toast.LENGTH_SHORT).show();
//        }
//        switch (resp.errCode) {
//            case 0:
//                getWXPayCallback().wxpaySuccess();
//                break;
//            case -1://配置信息错误，appid和应用签名
//                getWXPayCallback().wxpayFail(resp.errCode);
//                break;
//            case -2://取消支付
//                getWXPayCallback().wxpayCancel();
//                break;
//            default:
//                getWXPayCallback().wxpayFail(resp.errCode);
//                break;
//        }
        finish();
    }
}