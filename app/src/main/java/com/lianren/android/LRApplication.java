package com.lianren.android;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alibaba.security.rp.RPSDK;
import com.baidu.mapapi.SDKInitializer;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.lianren.android.api.ApiHttpClient;
import com.lianren.android.basicData.db.DaoMaster;
import com.lianren.android.basicData.db.DaoSession;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.base.activities.BaseActivity;
import com.lianren.android.improve.main.update.LRSharedPreference;
import com.lianren.android.util.LocationService;
import com.lianren.android.util.ShareConfig;
import com.lianren.android.util.greendao.MySqliteOpenHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import net.oschina.common.helper.ReadStateHelper;

import java.lang.reflect.Field;

import cn.jpush.android.api.JPushInterface;

/**
 * @package: com.lianren.android
 * @user:xhkj
 * @date:2019/12/17
 * @description:
 **/
public class LRApplication extends AppContext {
    private static final String CONFIG_READ_STATE_PRE = "CONFIG_READ_STATE_PRE_";
    public LocationService locationService;
    private static DaoSession daoSession;

    private void initGreenDao() {
        MigrationHelper.DEBUG = BuildConfig.DEBUG;
        //数据库名字
        MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(this, "BASIC.db", null);

        SQLiteDatabase db = mySqliteOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    /**
     * 提供一个全局的会话
     *
     * @return
     */
    public static DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initGreenDao();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                JPushInterface.clearAllNotifications(activity);
                JPushInterface.setBadgeNumber(activity, 0);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static void reInit() {
        ((LRApplication) LRApplication.getInstance()).init();
        ((LRApplication) LRApplication.getInstance()).initGreenDao();
    }

    static {
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setHeaderMaxDragRate(1.5f);
            }
        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                ClassicsFooter classicsFooter = new ClassicsFooter(context);
                classicsFooter.setTextSizeTitle(12);
                classicsFooter.setDrawableSize(16);
                classicsFooter.setFinishDuration(0);
                return classicsFooter;
            }
        });
    }

    private void init() {
        BaseActivity.IS_ACTIVE = true;
        LRSharedPreference.init(this, "lr_update_sp");
        // 初始化账户基础信息
        AccountHelper.init(this);
        // 初始化网络请求
        ApiHttpClient.init(this);
        if (LRSharedPreference.getInstance().hasShowUpdate()) {//如果已经更新过
            //如果版本大于更新过的版本，就设置弹出更新
            if (BuildConfig.VERSION_CODE > LRSharedPreference.getInstance().getUpdateVersion()) {
                LRSharedPreference.getInstance().putShowUpdate(true);
            }
        }
        // 友盟分享初始化
        UMConfigure.setLogEnabled(false);
        try {
            Class<?> aClass = Class.forName("com.umeng.commonsdk.UMConfigure");
            Field[] fs = aClass.getDeclaredFields();
            for (Field f : fs) {
                Log.e("xxxxxx", "ff=" + f.getName() + "   " + f.getType().getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(this, ShareConfig.UM_KEY, "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        RPSDK.initialize(this);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        locationService = new LocationService(getApplicationContext());
        //初始化百度地图
        SDKInitializer.initialize(this);
    }

    {
        PlatformConfig.setWeixin(ShareConfig.WX_APP_ID, ShareConfig.WX_APP_KEY);
        PlatformConfig.setSinaWeibo(ShareConfig.SINA_APP_ID, ShareConfig.SINA_APP_KEY, ShareConfig.SINA_APP_CALLBACK_URL);
        PlatformConfig.setQQZone(ShareConfig.QQ_APP_ID, ShareConfig.QQ_APP_KEY);
    }

    /**
     * 获取已读状态管理器
     *
     * @param mark 传入标示，如：博客：blog; 新闻：news
     * @return 已读状态管理器
     */
    public static ReadState getReadState(String mark) {
        ReadStateHelper helper = ReadStateHelper.create(getInstance(),
                CONFIG_READ_STATE_PRE + mark, 100);
        return new ReadState(helper);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 一个已读状态管理器
     */
    public static class ReadState {
        private ReadStateHelper helper;

        ReadState(ReadStateHelper helper) {
            this.helper = helper;
        }

        /**
         * 添加已读状态
         *
         * @param key 一般为资讯等Id
         */
        public void put(long key) {
            helper.put(key);
        }

        /**
         * 添加已读状态
         *
         * @param key 一般为资讯等Id
         */
        public void put(String key) {
            helper.put(key);
        }

        /**
         * 获取是否为已读
         *
         * @param key 一般为资讯等Id
         * @return True 已读
         */
        public boolean already(long key) {
            return helper.already(key);
        }

        /**
         * 获取是否为已读
         *
         * @param key 一般为资讯等Id
         * @return True 已读
         */
        public boolean already(String key) {
            return helper.already(key);
        }
    }
}
