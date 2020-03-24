package com.lianren.android.improve.main.splash;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.lianren.android.LRApplication;
import com.lianren.android.R;
import com.lianren.android.Setting;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.activities.NewLoginActivity;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.main.MainActivity;
import com.lianren.android.ui.dialog.PermissionDialog;
import com.lianren.android.util.DialogHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @package: com.lianren.android.improve.main.splash
 * @user:xhkj
 * @date:2019/12/17
 * @description:启动页设计
 **/
public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    @Bind(R.id.fl_content)
    FrameLayout flContent;
    @Bind(R.id.frameSplash)
    FrameLayout frameSplash;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        setContentView(getContentView());
        ButterKnife.bind(this);
        initData();
    }

    protected int getContentView() {
        return R.layout.activity_splash;
    }

    protected void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            View decorView = window.getDecorView();
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
                    return defaultInsets.replaceSystemWindowInsets(
                            defaultInsets.getSystemWindowInsetLeft(),
                            0,
                            defaultInsets.getSystemWindowInsetRight(),
                            defaultInsets.getSystemWindowInsetBottom());
                }
            });
            ViewCompat.requestApplyInsets(decorView);
            //将状态栏设成透明，如不想透明可设置其他颜色
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
        requestPermission();
    }

    private void doMerge() {
        // 判断是否是新版本
        if (Setting.checkIsNewVersion(this)) {
            String cookie = LRApplication.getInstance().getProperty("cookie");
            if (!TextUtils.isEmpty(cookie)) {
                LRApplication.getInstance().removeProperty("cookie");
                LRApplication.reInit();
            }
        }
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 完成后进行跳转操作
        redirectTo();
    }

    private void redirectTo() {
        if (AccountHelper.isLogin()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            if (getIntent().getSerializableExtra("PREF_EXTRA") != null) {
                intent.putExtra("PREF_EXTRA",
                        getIntent().getSerializableExtra("PREF_EXTRA"));
            }
            startActivity(intent);
        } else {
            NewLoginActivity.show(this);
        }
        finish();
    }

    private static final int PERMISSION_ID = 0x0001;

    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    public void requestPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    doMerge();
                }
            });
        } else {
            showPermissionDialog();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                doMerge();
            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "", "未保证您正常使用，请打开设置开启相应的权限。",
                "去设置", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                redirectTo();
            }
        }).show();
    }

    private PermissionDialog permissionDialog;

    /**
     * 显示权限申请提示框
     */
    private void showPermissionDialog() {
        if (permissionDialog == null)
            permissionDialog = new PermissionDialog(this);
        permissionDialog.setOnCertainButtonClickListener(new PermissionDialog.OnCertainButtonClickListener() {
            @Override
            public void onCertainButtonClick() {
                EasyPermissions.requestPermissions(SplashActivity.this, "", PERMISSION_ID, permissions);
            }
        });
        permissionDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onBackPressed() {

    }
}
