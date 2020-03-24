package com.lianren.android.improve.user.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.activities.NewLoginActivity;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.Version;
import com.lianren.android.improve.main.update.CheckUpdateManager;
import com.lianren.android.improve.main.update.DownloadService;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.FileUtil;
import com.lianren.android.util.MethodsCompat;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.UIHelper;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/24
 * @description:更多设置
 **/
public class MoreSettingsActivity extends BackActivity implements EasyPermissions.PermissionCallbacks, CheckUpdateManager.RequestPermissions {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ll_infomation)
    LinearLayout llInfomation;
    @Bind(R.id.ll_register_info)
    LinearLayout llRegisterInfo;
    @Bind(R.id.tv_version_name)
    TextView tvVersionName;
    @Bind(R.id.ll_version)
    LinearLayout llVersion;
    @Bind(R.id.tv_cache)
    TextView tvCache;
    @Bind(R.id.ll_clear_cache)
    LinearLayout llClearCache;
    @Bind(R.id.ll_logout)
    LinearLayout llLogout;
    @Bind(R.id.switch_info)
    Switch aSwitch;
    private UsersInfoBean usersInfoBean;
    private Version mVersion;
    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限

    public static void show(Context mContext, UsersInfoBean usersInfoBean) {
        Intent intent = new Intent();
        intent.putExtra("user_info", usersInfoBean);
        intent.setClass(mContext, MoreSettingsActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_more_settings;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        usersInfoBean = (UsersInfoBean) getIntent().getSerializableExtra("user_info");
        tvVersionName.setText(TDevice.getVersionName());
        calculateCacheSize();
        aSwitch.setChecked(!JPushInterface.isPushStopped(this));
    }

    /**
     * 计算缓存的大小
     */
    private void calculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = this.getFilesDir();
        File cacheDir = this.getCacheDir();

        fileSize += FileUtil.getDirSize(filesDir);
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(this);
            fileSize += FileUtil.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        tvCache.setText(cacheSize);
    }

    @OnClick({R.id.ll_infomation, R.id.ll_register_info, R.id.ll_version, R.id.ll_clear_cache, R.id.ll_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_infomation:
                if (JPushInterface.isPushStopped(this)) {
                    JPushInterface.resumePush(this);
                    aSwitch.setChecked(true);
                } else {
                    JPushInterface.stopPush(this);
                    aSwitch.setChecked(false);
                }
                break;
            case R.id.ll_register_info:
                RegisterInfoActivity.show(this, usersInfoBean);
                break;
            case R.id.ll_version:
                onClickUpdate();
                break;
            case R.id.ll_clear_cache:
                onClickCleanCache();
                break;
            case R.id.ll_logout:
                UIHelper.clearAppCache(false);
                // 注销操作
                AccountHelper.logout(llLogout, new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        //getActivity().finish();
                        tvCache.setText("0KB");
                    }
                });
                break;
        }
    }

    private void onClickUpdate() {
        CheckUpdateManager manager = new CheckUpdateManager(this, true);
        manager.setCaller(this);
        manager.checkUpdate(true);
    }

    private void onClickCleanCache() {
        DialogHelper.getConfirmDialog(this, "是否清空缓存?", new DialogInterface.OnClickListener
                () {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UIHelper.clearAppCache(true);
                tvCache.setText("0KB");
            }
        }).show();
    }

    @Override
    public void call(Version version) {
        this.mVersion = version;
        requestExternalStorage();
    }

    @SuppressLint("InlinedApi")
    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DownloadService.startService(this, mVersion.url);
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "温馨提示", "需要开启" + getString(R.string.app_name) + "对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
