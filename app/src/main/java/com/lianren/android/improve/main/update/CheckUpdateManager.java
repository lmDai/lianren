package com.lianren.android.improve.main.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.cretin.www.cretinautoupdatelibrary.model.DownloadInfo;
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig;
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.Version;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.TDevice;

import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.main.update
 * @user:xhkj
 * @date:2020/1/3
 * @description:
 **/
public class CheckUpdateManager {


    private ProgressDialog mWaitDialog;
    private Context mContext;
    private boolean mIsShowDialog;

    public CheckUpdateManager(Context context, boolean showWaitingDialog) {
        this.mContext = context;
        mIsShowDialog = showWaitingDialog;
        if (mIsShowDialog) {
            mWaitDialog = DialogHelper.getProgressDialog(mContext);
            mWaitDialog.setMessage("正在检查中...");
            mWaitDialog.setCancelable(true);
            mWaitDialog.setCanceledOnTouchOutside(true);
        }
    }


    public void checkUpdate(final boolean isHasShow) {
        if (mIsShowDialog) {
            mWaitDialog.show();
        }
        LRApi.appVersion(TDevice.getVersionName() + "", new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    if (mIsShowDialog) {
                        DialogHelper.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
                    }
                    if (mWaitDialog != null) {
                        mWaitDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<Version> bean = AppOperator.createGson()
                            .fromJson(responseString, new TypeToken<ResultBean<Version>>() {
                            }.getType());
                    if (bean != null && bean.isSuccess()) {
                        Version version = bean.data;
                        if (version.update_type != 0) {
                            //是否弹出更新
//                            UpdateActivity.show((Activity) mContext, version);
                            DownloadInfo info = new DownloadInfo().setApkUrl(version.url)
                                   .setProdVersionCode(Integer.parseInt(version.version))
                                    .setProdVersionName(version.version)
                                    .setForceUpdateFlag(version.update_type == 1 ? 0 : 1)
                                    .setUpdateLog(version.intro);
                            AppUpdateUtils.getInstance().getUpdateConfig().setUiThemeType(TypeConfig.UI_THEME_G);
                            //打开文件MD5校验
                            AppUpdateUtils.getInstance().getUpdateConfig().setNeedFileMD5Check(false);
                            AppUpdateUtils.getInstance().getUpdateConfig().setDataSourceType(TypeConfig.DATA_SOURCE_TYPE_MODEL);
                            //开启或者关闭后台静默下载功能
                            AppUpdateUtils.getInstance().getUpdateConfig().setAutoDownloadBackground(false);

                            AppUpdateUtils.getInstance().getUpdateConfig().setShowNotification(false);
                            AppUpdateUtils.getInstance()
                                    .checkUpdate(info);

                        } else {
                            if (mIsShowDialog) {
                                DialogHelper.getMessageDialog(mContext, "已经是新版本了").show();
                            }
                        }
                    } else {
                        if (mIsShowDialog) {
                            DialogHelper.getMessageDialog(mContext, "已经是新版本了").show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mWaitDialog != null) {
                    mWaitDialog.dismiss();
                }
            }
        });
    }

    @SuppressWarnings("all")
    public void setCaller(RequestPermissions caller) {
        RequestPermissions mCaller = caller;
    }

    public interface RequestPermissions {
        void call(Version version);
    }
}

