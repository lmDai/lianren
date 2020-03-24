package com.lianren.android.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lianren.android.improve.bean.Constants;


/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class ListenAccountChangeReceiver extends BroadcastReceiver {
    public static final String TAG = ListenAccountChangeReceiver.class.getSimpleName();
    private Service service;

    private ListenAccountChangeReceiver(Service service) {
        this.service = service;
    }

    public static ListenAccountChangeReceiver start(Service service) {
        ListenAccountChangeReceiver receiveBroadCast = new ListenAccountChangeReceiver(service);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        service.registerReceiver(receiveBroadCast, filter);
        return receiveBroadCast;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (service != null)
            service.stopSelf();
    }

    public void destroy() {
        if (service != null) {
            service.unregisterReceiver(this);
            service = null;
        }
    }
}
