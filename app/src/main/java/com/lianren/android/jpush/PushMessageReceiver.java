package com.lianren.android.jpush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.BuildConfig;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BaseActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.NotificationBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.explore.activities.EventDetailActivity;
import com.lianren.android.improve.explore.activities.InviteListActivity;
import com.lianren.android.improve.home.activities.UserSendMessageActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * @package: com.lianren.android.jpush
 * @user:xhkj
 * @date:2020/1/21
 * @description:
 **/
public class PushMessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Log.e(TAG, "[onMessage] " + customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageOpened] " + message);
        boolean isAppRuning = isAppAlive(context, BuildConfig.APPLICATION_ID);//判断APP是否在运行
        Log.e(TAG, "[onNotifyMessageOpened] " + isAppRuning + "");
        String extras = message.notificationExtras;
        NotificationBean resultBean = null;
        try {
            Type type = new TypeToken<NotificationBean>() {
            }.getType();
            resultBean = AppOperator.createGson().fromJson(extras, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isAppRuning) {//运行中
            if (resultBean != null) {
                int type = resultBean.type;
                switch (type) {//type 1=私信,2=邀约,3=喜欢,4=活动开启,5=活动推荐
                    case 1:
                        break;
                    case 2:
                        Intent i = new Intent(context, InviteListActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                        break;
                    case 3:
                        Intent chatIntent = new Intent(context, UserSendMessageActivity.class);
                        UsersInfoBean.BaseBean msg = new UsersInfoBean.BaseBean();
                        msg.nickname = resultBean.nickname;
                        msg.id = resultBean.user_id;
                        msg.uuid = resultBean.uuid;
                        msg.nickname = resultBean.nickname;
                        chatIntent.putExtra("receiver", msg);
                        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(chatIntent);
                        break;
                    case 4:
                    case 5:
                        Intent detail = new Intent(context, EventDetailActivity.class);
                        detail.putExtra("activity_id", resultBean.id);
                        detail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(detail);
                        break;
                }
            }
            return;
        }
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.lianren.android");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //ur_barCode传的参数，就是普通的Intent传值
        launchIntent.putExtra("PREF_EXTRA", resultBean);
        context.startActivity(launchIntent);
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageArrived] " + message);
        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_CHAT, null));
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageDismiss] " + message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Log.e(TAG, "[onRegister] " + registrationId);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Log.e(TAG, "[onConnected] " + isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
    }

    /**
     * 返回app运行状态
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return int 1:前台 2:后台 0:不存在
     */
    public static boolean isAppAlive(Context context, String packageName) {
        return BaseActivity.IS_ACTIVE;
    }
}

