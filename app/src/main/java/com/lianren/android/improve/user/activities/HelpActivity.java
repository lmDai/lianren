package com.lianren.android.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.PrivacyBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.main.WebActivity;
import com.lianren.android.util.TDevice;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/24
 * @description:反馈帮助
 **/
public class HelpActivity extends BackActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ll_feed_back)
    LinearLayout llFeedBack;
    @Bind(R.id.ll_help)
    LinearLayout llHelp;
    @Bind(R.id.ll_recommend)
    LinearLayout llRecommend;
    private UsersInfoBean usersInfoBean;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;

    public static void show(Context mContext, UsersInfoBean usersInfoBean) {
        Intent intent = new Intent();
        intent.putExtra("user_info", usersInfoBean);
        intent.setClass(mContext, HelpActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_help;
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
        mShareListener = new CustomShareListener(this);
    }

    @OnClick({R.id.ll_feed_back, R.id.ll_help, R.id.ll_recommend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_feed_back://反馈
                FeedTypeActivity.show(this,UserConstants.FEED_APP);
                break;
            case R.id.ll_help://帮助
                getHelp();
                break;
            case R.id.ll_recommend://推荐链人
                share();
                break;
        }
    }

    private void getHelp() {
        LRApi.helpInfo(new CommonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.tip_network_error);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (!TDevice.hasInternet()) {
                    AppContext.showToast(R.string.tip_network_error);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PrivacyBean>>() {
                    }.getType();

                    ResultBean<PrivacyBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        WebActivity.show(HelpActivity.this, resultBean.data.uri);
                    } else {
                        AppContext.showToast(R.string.tip_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
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

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<HelpActivity> mActivity;

        private CustomShareListener(HelpActivity activity) {
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

    private void share() {
        final String desc = getResources().getString(R.string.app_name);
        // 推荐即分享
        final String title = getResources().getString(R.string.app_name);
        final String url = "http://www.lianrenapp.cn";
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
                                TDevice.copyTextToBoard(url);
                            } else if (snsPlatform.mShowWord.equals("复制链接")) {
                                TDevice.copyTextToBoard(url);
                            } else {
                                UMWeb web = new UMWeb(url);
                                web.setTitle(title);
                                web.setDescription(desc);
                                web.setThumb(new UMImage(HelpActivity.this, R.mipmap.ic_launcher));
                                new ShareAction(HelpActivity.this).withMedia(web)
                                        .setPlatform(share_media)
                                        .setCallback(mShareListener)
                                        .share();
                            }
                        }
                    });
        mShareAction.open();
    }
}
