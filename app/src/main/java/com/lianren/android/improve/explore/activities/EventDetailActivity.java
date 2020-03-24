package com.lianren.android.improve.explore.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.EventDetailBean;
import com.lianren.android.improve.bean.UriBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.explore.presenter.EventDetailContract;
import com.lianren.android.improve.explore.presenter.EventDetailPresenter;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.main.WebActivity;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.pickimage.media.ImageGalleryActivity;
import com.lianren.android.widget.PileAvertView;
import com.lianren.android.widget.RoundImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:活动详情
 **/
public class EventDetailActivity extends BackActivity implements EventDetailContract.View, EasyPermissions.PermissionCallbacks {
    private static final int PERMISSION_ID = 0x001;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.image)
    RoundImageView image;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.total_cost)
    TextView totalCost;
    @Bind(R.id.intro)
    TextView intro;
    @Bind(R.id.apply_e_time)
    TextView applyETime;
    @Bind(R.id.org_logo)
    CircleImageView orgLogo;
    @Bind(R.id.org_describe)
    TextView orgDescribe;
    @Bind(R.id.users)
    PileAvertView users;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.ll_scan_detail)
    LinearLayout llScanDetail;
    @Bind(R.id.ll_join)
    LinearLayout llJoin;
    @Bind(R.id.ll_call)
    LinearLayout llCall;
    @Bind(R.id.btn_status)
    Button btnStatus;
    @Bind(R.id.tv_total_users)
    TextView tvTotalUsers;
    @Bind(R.id.ll_user)
    LinearLayout llUser;
    private String activity_id;
    private EventDetailContract.Presenter mPresenter;
    private EventDetailBean detailBean;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;
    private UsersInfoBean.BaseBean mReceiver;

    public static void show(Context mContext, String id, UsersInfoBean.BaseBean mReceiver) {
        Intent intent = new Intent();
        intent.putExtra("activity_id", id);
        intent.putExtra("receiver", mReceiver);
        intent.setClass(mContext, EventDetailActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_event_detail;
    }

    public static void show(Context mContext, String activity_id) {
        Intent intent = new Intent();
        intent.putExtra("activity_id", activity_id);
        intent.setClass(mContext, EventDetailActivity.class);
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
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(R.id.tv_cancel_opt, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {

                        dialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_share, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        share();
                        dialog.dismiss();
                    }
                }).setOnClickListener(R.id.tv_feed_back_opt, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                FeedTypeActivity.show(EventDetailActivity.this, UserConstants.FEED_EVENT, detailBean.id);
                dialog.dismiss();
            }
        })
                .show();
    }

    @Override
    protected void initData() {
        super.initData();
        mReceiver = (UsersInfoBean.BaseBean) getIntent().getSerializableExtra("receiver");
        activity_id = getIntent().getStringExtra("activity_id");
        mPresenter = new EventDetailPresenter(this);
        mPresenter.getEventDetail(activity_id);
        mShareListener = new CustomShareListener(this);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getEventDetail(activity_id);
            }
        });
    }

    @Override
    public void setPresenter(EventDetailContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
        refreshLayout.finishRefresh(false);
    }

    @Override
    public void showEventDetail(EventDetailBean detailBean) {
        refreshLayout.finishRefresh(true);
        if (detailBean == null) return;
        this.detailBean = detailBean;
        llCall.setVisibility(TextUtils.isEmpty(detailBean.telephone) ? View.INVISIBLE : View.VISIBLE);
        llCall.setEnabled(!TextUtils.isEmpty(detailBean.telephone));
        ImageLoader.loadAutoImage(getImageLoader(), image, detailBean.image);
        name.setText(detailBean.name);
        time.setText(detailBean.time);
        address.setText(detailBean.address);
        totalCost.setText(detailBean.total_cost);
        intro.setText(detailBean.intro);
        if (detailBean.status != 0) {
            applyETime.setVisibility(View.VISIBLE);
            applyETime.setText("报名截止:" + detailBean.apply_e_time);
        } else {
            applyETime.setVisibility(View.INVISIBLE);
        }

        ImageLoader.loadImage(getImageLoader(), orgLogo, detailBean.org_logo);
        String describe = "<font color='#414141'><b>" + detailBean.org_name + ":</b></font>" + detailBean.org_describe;
        orgDescribe.setText(Html.fromHtml(describe));
        if (detailBean.users != null && detailBean.users.size() > 0) {
            llUser.setVisibility(View.VISIBLE);
            tvTotalUsers.setText("参与(" + detailBean.users.size() + ")");
            users.setAvertImages(detailBean.users);
        } else {
            llUser.setVisibility(View.GONE);
        }
        if (mReceiver != null) {
            btnStatus.setEnabled(true);
            btnStatus.setText("选择");
        } else {
            //状态 0展示 1已上线 2停止报名

            switch (detailBean.status) {
                case 0:
                    if (detailBean.collect_status == 1) {
                        btnStatus.setEnabled(true);
                        btnStatus.setText("已想参与");
                    } else {
                        btnStatus.setEnabled(true);
                        btnStatus.setText("想参与");
                    }
                    break;
                case 1:
                    btnStatus.setEnabled(true);
                    btnStatus.setText("购票");
                    break;
                case 2:
                    btnStatus.setEnabled(false);
                    btnStatus.setText("已结束报名");
                    break;
                default:
                    btnStatus.setEnabled(false);
                    btnStatus.setText("系统繁忙");
                    break;
            }

        }

        //匹配功能开放状态 0未开放 1开放
        llJoin.setVisibility(detailBean.pair_status == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showUri(UriBean uri) {
        dismissLoadingDialog();
        WebActivity.show(this, uri.uri);
    }

    private long mLastClickTime;

    @OnClick({R.id.ll_scan_detail, R.id.ll_join, R.id.ll_call, R.id.btn_status, R.id.image})
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
            case R.id.ll_join://参加
                showLoadingDialog();
                mPresenter.getUri(activity_id);
                break;
            case R.id.ll_call://拨打电话
                requestPermission();
                break;
            case R.id.btn_status://状态
                if (mReceiver != null) {
                    EventGoodsActivity.show(this, Integer.parseInt(activity_id), 1, mReceiver);
                } else {
                    if (detailBean.status == 0) {//收藏
                        mPresenter.activityCollect(Integer.parseInt(activity_id));
                    } else {
                        EventPurchseTicketActivity.show(this, activity_id, 0);
                    }
                }
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


    private static class CustomShareListener implements UMShareListener {

        private WeakReference<EventDetailActivity> mActivity;

        private CustomShareListener(EventDetailActivity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    private void share() {
        if (mShareAction == null)
            mShareAction = new ShareAction(this).setDisplayList(
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                    SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.MORE)
                    .addButton("复制文本", "复制文本", "umeng_socialize_copy", "umeng_socialize_copy")
                    .addButton("复制链接", "复制链接", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                    .setShareboardclickCallback(new ShareBoardlistener() {
                        @Override
                        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                            if (snsPlatform.mShowWord.equals("复制文本")) {
                                TDevice.copyTextToBoard(detailBean.share.describe);
                            } else if (snsPlatform.mShowWord.equals("复制链接")) {
                                TDevice.copyTextToBoard(detailBean.share.uri);
                            } else {
                                UMWeb web = new UMWeb(detailBean.share.uri);
                                web.setTitle(detailBean.share.title);
                                web.setDescription(detailBean.share.describe);
                                web.setThumb(new UMImage(EventDetailActivity.this, detailBean.share.image));
                                new ShareAction(EventDetailActivity.this).withMedia(web)
                                        .setPlatform(share_media)
                                        .setCallback(mShareListener)
                                        .share();
                            }
                        }
                    });
        mShareAction.open();
    }

    @Override
    public void showError(String message) {
        AppContext.showToast(message);
        finish();
    }

    @Override
    public void showCollectStatus(int status) {
        btnStatus.setEnabled(true);
        if (status == 0) {
            btnStatus.setText("想参与");
        } else {
            btnStatus.setText("已想参与");
        }
    }
}
