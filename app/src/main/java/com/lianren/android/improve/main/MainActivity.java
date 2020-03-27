package com.lianren.android.improve.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppConfig;
import com.lianren.android.AppContext;
import com.lianren.android.LRApplication;
import com.lianren.android.R;
import com.lianren.android.Setting;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.base.BaseApplication;
import com.lianren.android.improve.base.activities.BaseActivity;
import com.lianren.android.improve.bean.BasicBean;
import com.lianren.android.improve.bean.NotificationBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.Version;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.explore.activities.EventDetailActivity;
import com.lianren.android.improve.explore.activities.InviteListActivity;
import com.lianren.android.improve.home.activities.UserSendMessageActivity;
import com.lianren.android.improve.main.nav.NavFragment;
import com.lianren.android.improve.main.nav.NavigationButton;
import com.lianren.android.improve.main.update.CheckUpdateManager;
import com.lianren.android.improve.main.update.DownloadService;
import com.lianren.android.improve.notice.NoticeManager;
import com.lianren.android.interf.OnTabReselectListener;
import com.lianren.android.util.CacheManager;
import com.lianren.android.util.ClipManager;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.greendao.DaoSessionUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements NavFragment.OnNavigationReselectListener,
        EasyPermissions.PermissionCallbacks, CheckUpdateManager.RequestPermissions {
    public static boolean IS_SHOW = false;
    private long mBackPressedTime;
    private NavFragment mNavBar;
    private Version mVersion;
    public static final String ACTION_NOTICE = "ACTION_NOTICE";

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        IS_SHOW = true;
        setSwipeBackEnable(false);
        setStatusBarDarkMode();
        FragmentManager manager = getSupportFragmentManager();
        mNavBar = ((NavFragment) manager.findFragmentById(R.id.fag_nav));
        mNavBar.setup(this, manager, R.id.main_container, this);
    }

    @Override
    protected void initData() {
        super.initData();
        initIntent();
        NoticeManager.init(this);
        getBasicData();
        checkUpdate();
    }

    private void initIntent() {
        NotificationBean extras = (NotificationBean) getIntent().getSerializableExtra("PREF_EXTRA");
        // 点击通知栏传过来的启动页-->mainActivity-->指定跳转页
        if (extras != null) {
            int type = extras.type;
            switch (type) {//type 1=私信,2=邀约,3=喜欢,4=活动开启,5=活动推荐
                case 1:
                    break;
                case 2:
                    InviteListActivity.show(this);
                    break;
                case 3:
                    UsersInfoBean.BaseBean msg = new UsersInfoBean.BaseBean();
                    msg.nickname = extras.nickname;
                    msg.id = extras.user_id;
                    msg.uuid = extras.uuid;
                    UserSendMessageActivity.show(this, msg);
                    break;
                case 4:
                case 5:
                    EventDetailActivity.show(this, extras.id);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_SHOW = false;
        NoticeManager.stopListen(this);
        ClipManager.unregister();
    }

    private static void getBasicData() {
        DaoSessionUtils.deleteAllBasicBean(new BasicBean());
        LRApi.basicData(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<BasicBean>>>() {
                    }.getType();
                    ResultBean<List<BasicBean>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        for (BasicBean basicBean : bean.data) {
                            DaoSessionUtils.insertBasicBean(basicBean);
                        }
                        CacheManager.saveToJson(LRApplication.getInstance(), "BasicBean", bean.data);
                    } else {
                        CacheManager.removeCahche(LRApplication.getInstance(), "BasicBean");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void show(Context context) {
        IS_SHOW = true;
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        boolean isDoubleClick = BaseApplication.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true);
        if (isDoubleClick) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                Toast.makeText(this, R.string.tip_double_click_exit, Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }
    }

    @Override
    protected boolean isSetStatusBarColor() {
        return false;
    }

    private void checkUpdate() {
        if (!AppContext.get(AppConfig.KEY_CHECK_UPDATE, true)) {
            return;
        }
        CheckUpdateManager manager = new CheckUpdateManager(this, false);
        manager.setCaller(this);
        manager.checkUpdate(false);
    }

    @Override
    public void onReselect(NavigationButton navigationButton) {
        Fragment fragment = navigationButton.getFragment();
        if (fragment != null
                && fragment instanceof OnTabReselectListener) {
            OnTabReselectListener listener = (OnTabReselectListener) fragment;
            listener.onTabReselect();
        }
    }

    @Override
    public void call(Version version) {
        this.mVersion = version;
//        requestExternalStorage();
    }

    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DownloadService.startService(this, mVersion.url);
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    public static String ACTION_REQUEST_LOCATION = "com.lianren.android.requestlocation";

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        for (String str : perms) {
            if (str.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Intent broadcast = new Intent(ACTION_REQUEST_LOCATION);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        for (String perm : perms) {
            if (perm.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                DialogHelper.getConfirmDialog(this, "温馨提示", "需要开启" + getString(R.string.app_name) + "对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    }
                }, null).show();

            } else {
                Setting.updateLocationPermission(getApplicationContext(), false);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
