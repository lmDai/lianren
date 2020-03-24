package com.lianren.android.improve.account;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.lianren.android.api.ApiHttpClient;
import com.lianren.android.improve.account.activities.NewLoginActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.User;
import com.lianren.android.improve.notice.NoticeManager;

import net.oschina.common.helper.SharedPreferencesHelper;

import cn.jpush.android.api.JPushInterface;

/**
 * @package: com.lianren.android.improve.account
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public final class AccountHelper {
    private static final String TAG = AccountHelper.class.getSimpleName();
    private User user;
    private Application application;
    @SuppressLint("StaticFieldLeak")
    private static AccountHelper instances;

    private AccountHelper(Application application) {
        this.application = application;
    }

    public static void init(Application application) {
        if (instances == null)
            instances = new AccountHelper(application);
        else {
            instances.user = SharedPreferencesHelper.loadFormSource(instances.application, User.class);
        }
    }

    public static boolean isLogin() {
        return getUserId() > 0;
    }


    public static long getUserId() {
        return getUser().id;
    }

    public synchronized static User getUser() {
        if (instances == null) {
            return new User();
        }
        if (instances.user == null)
            instances.user = SharedPreferencesHelper.loadFormSource(instances.application, User.class);
        if (instances.user == null)
            instances.user = new User();
        return instances.user;
    }

    public static boolean updateUserCache(User user) {
        if (user == null)
            return false;
        instances.user = user;
        return SharedPreferencesHelper.save(instances.application, user);
    }

    private static void clearUserCache() {
        instances.user = null;
        SharedPreferencesHelper.remove(instances.application, User.class);
    }

    public static boolean login(final User user) {

        int count = 10;
        boolean saveOk;
        // 保存缓存
        while (!(saveOk = updateUserCache(user)) && count-- > 0) {
            SystemClock.sleep(100);
        }

        if (saveOk) {
            // 登陆成功,重新启动消息服务
            NoticeManager.init(instances.application);
        }
        return saveOk;
    }

    /**
     * 退出登陆操作需要传递一个View协助完成延迟检测操作
     *
     * @param view     View
     * @param runnable 当清理完成后回调方法
     */
    public static void logout(final View view, final Runnable runnable) {
        // 清除用户缓存
        clearUserCache();
        // 等待缓存清理完成
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.removeCallbacks(this);
                User user = SharedPreferencesHelper.load(instances.application, User.class);
                // 判断当前用户信息是否清理成功
                if (user == null || user.id <= 0) {
                    clearAndPostBroadcast(instances.application);
                    runnable.run();
                } else {
                    view.postDelayed(this, 200);
                }
            }
        }, 200);

    }

    /**
     * 当前用户信息清理完成后调用方法清理服务等信息
     *
     * @param application Application
     */
    private static void clearAndPostBroadcast(Application application) {
        // 清理网络相关
        ApiHttpClient.destroyAndRestore(application);
        // 用户退出时清理红点并退出服务
        NoticeManager.clear(application, NoticeManager.FLAG_CLEAR_ALL);
        NoticeManager.exitServer(application);
        JPushInterface.clearAllNotifications(application);
        // Logout 广播
        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(application).sendBroadcast(intent);

        NewLoginActivity.show(application.getApplicationContext(), "");

    }
}
