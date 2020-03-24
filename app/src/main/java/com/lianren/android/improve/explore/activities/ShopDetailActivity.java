package com.lianren.android.improve.explore.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.ShopDetailBean;
import com.lianren.android.improve.bean.UriBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.explore.presenter.ShopDetailContract;
import com.lianren.android.improve.explore.presenter.ShopDetailPresenter;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.main.WebActivity;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.pickimage.media.ImageGalleryActivity;
import com.lianren.android.widget.RoundImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:空间详情
 **/
public class ShopDetailActivity extends BackActivity implements ShopDetailContract.View, EasyPermissions.PermissionCallbacks {
    private static final int PERMISSION_ID = 0x001;
    @Bind(R.id.image)
    RoundImageView image;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_total_cost)
    TextView tvTotalCost;
    @Bind(R.id.intro)
    TextView intro;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.btn_status)
    Button btnStatus;
    @Bind(R.id.ll_call)
    LinearLayout llCall;
    private int shop_id;
    private ShopDetailContract.Presenter mPresenter;
    private ShopDetailBean detailBean;
    private UsersInfoBean.BaseBean mReceiver;
    private int status;

    public static void show(Context mContext, int shop_id, UsersInfoBean.BaseBean mReceiver) {
        Intent intent = new Intent();
        intent.putExtra("shop_id", shop_id);
        intent.putExtra("receiver", mReceiver);
        intent.setClass(mContext, ShopDetailActivity.class);
        mContext.startActivity(intent);
    }

    public static void show(Context mContext, int shop_id, int status) {
        Intent intent = new Intent();
        intent.putExtra("shop_id", shop_id);
        intent.putExtra("status", status);
        intent.setClass(mContext, ShopDetailActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_shop_detail;
    }

    public static void show(Context mContext, int shop_id) {
        Intent intent = new Intent();
        intent.putExtra("shop_id", shop_id);
        intent.setClass(mContext, ShopDetailActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_more:
                showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDialog() {
        new BaseDialogFragment.Builder(this)
                .setContentView(R.layout.view_event_detail_operator)
                .setVisibility(R.id.tv_share, View.GONE)
                .setVisibility(R.id.view_line, View.GONE)
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(R.id.tv_cancel_opt, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {

                        dialog.dismiss();
                    }
                }).setOnClickListener(R.id.tv_feed_back_opt, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                FeedTypeActivity.show(ShopDetailActivity.this, UserConstants.FEED_INVITE, detailBean.id + "");
                dialog.dismiss();
            }
        })
                .show();
    }

    @Override
    protected void initData() {
        super.initData();
        shop_id = getIntent().getIntExtra("shop_id", -1);
        status = getIntent().getIntExtra("status", -1);
        mReceiver = (UsersInfoBean.BaseBean) getIntent().getSerializableExtra("receiver");
        mPresenter = new ShopDetailPresenter(this);
        mPresenter.getShopDetail(shop_id);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getShopDetail(shop_id);
            }
        });
    }

    @Override
    public void setPresenter(ShopDetailContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
        refreshLayout.finishRefresh(false);
    }

    @Override
    public void shopShopDetail(ShopDetailBean detailBean) {
        refreshLayout.finishRefresh(true);
        if (detailBean == null) return;
        this.detailBean = detailBean;
        ImageLoader.loadAutoHeight(getImageLoader(), image, detailBean.image);
        tvName.setText(detailBean.name);
        tvAddress.setText(detailBean.address);
        tvTime.setText("营业时间：" + detailBean.time);
        tvTotalCost.setText(detailBean.total_cost);
        intro.setText(detailBean.intro);
        llCall.setVisibility(TextUtils.isEmpty(detailBean.telephone) ? View.INVISIBLE : View.VISIBLE);
        llCall.setEnabled(!TextUtils.isEmpty(detailBean.telephone));
        if (mReceiver != null) {
            btnStatus.setEnabled(true);
            btnStatus.setText("选择");
        } else {
            // 0已下线 1已上线 2停止报名
            switch (detailBean.status) {
                case 0:
                    btnStatus.setEnabled(true);
                    btnStatus.setText("已下线");
                    break;
                case 1:
                    btnStatus.setEnabled(true);
                    btnStatus.setText("已上线");
                    break;
                case 2:
                    btnStatus.setEnabled(false);
                    btnStatus.setText("停止报名");
                    break;
                default:
                    btnStatus.setEnabled(false);
                    btnStatus.setText("系统繁忙");
                    break;
            }
        }
        if (status == -2) {
            btnStatus.setEnabled(true);
            btnStatus.setText("已选择");
        }
    }

    @Override
    public void showUri(UriBean uri) {
        dismissLoadingDialog();
        WebActivity.show(this, uri.uri);
    }

    private long mLastClickTime;

    @OnClick({R.id.ll_scan_detail, R.id.ll_call, R.id.btn_status, R.id.image})
    public void onViewClicked(View view) {
        // 用来解决快速点击多个按钮弹出多个界面的情况
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return;
        mLastClickTime = nowTime;
        switch (view.getId()) {
            case R.id.ll_scan_detail://详情 describe
                WebActivity.show(this, detailBean.describe);
                break;
            case R.id.ll_call://拨打电话
                requestPermission();
                break;
            case R.id.btn_status://状态
                if (status == -2) {
                    finish();
                } else
                    ShopGoodsActivity.show(this, shop_id, 1, mReceiver);
                break;
            case R.id.image:
                ImageGalleryActivity.show(this, detailBean.image);
                break;
        }
    }

    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    public void requestPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE)) {
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    call();
                }
            });
        } else {
            EasyPermissions.requestPermissions(this, "", PERMISSION_ID, Manifest.permission.CALL_PHONE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "", "未保证您正常使用，请打开设置开启相应的权限。", "去设置", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
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
                call();
            }
        });
    }

    private void call() {
        TDevice.callPhone(this, detailBean.telephone);
    }

}
