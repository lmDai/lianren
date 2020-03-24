package com.lianren.android.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.home.activities.UserSendMessageActivity;
import com.lianren.android.improve.user.UserNoteAdapter;
import com.lianren.android.improve.user.presenter.UsersInfoContract;
import com.lianren.android.improve.user.presenter.UsersInfoPresenter;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.StatusBarUtil;
import com.lianren.android.util.pickimage.media.ImageGalleryActivity;
import com.lianren.android.widget.RecycleViewDivider;
import com.lianren.android.widget.SimplexToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.SmartUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:用户详细资料
 **/
public class UserInfoActivity extends BackActivity implements UsersInfoContract.View {
    @Bind(R.id.parallax)
    ImageView parallax;
    @Bind(R.id.header)
    ClassicsHeader header;
    @Bind(R.id.relationship)
    LinearLayout relationship;
    @Bind(R.id.signature)
    TextView signature;
    @Bind(R.id.panel)
    RelativeLayout panel;
    @Bind(R.id.panel_lyt)
    RelativeLayout panelLyt;
    @Bind(R.id.collapse)
    CollapsingToolbarLayout collapse;
    @Bind(R.id.fmc_center_dynamic)
    LinearLayout fmcCenterDynamic;
    @Bind(R.id.scrollView)
    NestedScrollView scrollView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.toolbar_avatar)
    CircleImageView toolbarAvatar;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.buttonBarLayout)
    ButtonBarLayout buttonBarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_note)
    RecyclerView recyclerNote;
    @Bind(R.id.tv_nickname)
    TextView tvNickName;
    @Bind(R.id.tv_uuid)
    TextView tvUUid;
    @Bind(R.id.avatar)
    CircleImageView circlAvater;
    @Bind(R.id.tag_flow)
    TagFlowLayout tagFlowLayout;
    @Bind(R.id.ll_chat)
    LinearLayout llChat;
    @Bind(R.id.ll_apply_pair)
    LinearLayout llApplyPair;
    @Bind(R.id.tv_pair_status)
    TextView tvPairStatus;
    @Bind(R.id.ll_scan_imprint)
    LinearLayout llScanImprint;
    @Bind(R.id.img_next)
    ImageView imgNext;
    @Bind(R.id.ll_user_base_info)
    LinearLayout llUserBaseInfo;
    @Bind(R.id.rl_menu)
    RelativeLayout rlMenu;
    private int mOffset = 0;
    private int mScrollY = 0;
    private UsersInfoContract.Presenter mPresenter;
    private int user_id;
    private String user_uuid;
    private UserNoteAdapter mAdapter;
    private UsersInfoBean mList;
    private int requestType = 1;
    private CommonHttpResponseHandler handler = new CommonHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            switch (requestType) {
                case 1://喜欢
                    try {
                        Type type = new TypeToken<ResultBean>() {
                        }.getType();
                        ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            AppContext.showToast(R.string.liveness_success);
                        } else {
                            AppContext.showToast(resultBean.error.message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                    break;
            }
        }
    };
    private Menu mMenu;

    @Override
    protected int getContentView() {
        return R.layout.activity_user_info;
    }


    public static void show(Context mContext, int user_id, String user_uuid) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra(UserConstants.USER_ID, user_id);
        intent.putExtra(UserConstants.USER_UUID, user_uuid);
        mContext.startActivity(intent);
    }

    @Override
    protected void initData() {
        super.initData();
        if (getIntent() != null) {
            user_id = getIntent().getIntExtra(UserConstants.USER_ID, -1);
            user_uuid = getIntent().getStringExtra(UserConstants.USER_UUID);
        }
        rlMenu.setVisibility(user_id == AccountHelper.getUser().id ? View.GONE : View.VISIBLE);
        mPresenter = new UsersInfoPresenter(this);
        mPresenter.getUsersInfo(user_uuid, user_id);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setMargin(this, header);
        StatusBarUtil.darkMode(this, false);
        setSupportActionBar(toolbar);
        setDarkToolBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getUsersInfo(user_uuid, user_id);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                mOffset = offset / 2;
                parallax.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private int h = SmartUtil.dp2px(170);
            private int color = ContextCompat.getColor(getApplicationContext(), R.color.day_colorPrimary) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBarLayout.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    parallax.setTranslationY(mOffset - mScrollY);
                }
                lastScrollY = scrollY;
            }
        });
        buttonBarLayout.setAlpha(0);
        toolbar.setBackgroundColor(0);
        mAdapter = new UserNoteAdapter();
        recyclerNote.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        recyclerNote.setLayoutManager(new LinearLayoutManager(this));
        recyclerNote.setNestedScrollingEnabled(false);
        mAdapter.bindToRecyclerView(recyclerNote);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UsersInfoBean.NoteBean item = mAdapter.getItem(position);
                ImprintsDetailActivity.show(UserInfoActivity.this, user_id, item.id, "");
            }
        });
    }

    private void getSelectorDialog() {
        if (mList == null) return;
        if (mList.base == null) return;
        final int contact_status = mList.base.contact_status;//0:陌生人 1:联系人 2:黑名单
        String contactStr = "屏蔽";
        if (contact_status == 0) {
            contactStr = "屏蔽";
        } else if (contact_status == 1) {
            contactStr = "屏蔽";
        } else if (contact_status == 2) {
            contactStr = "已屏蔽";
        }

        new BaseDialogFragment.Builder(this)
                .setContentView(R.layout.view_user_info_operator)
                .setVisibility(R.id.tv_shiled_opt, mList.type == 1 ? View.VISIBLE : View.GONE)
                .setVisibility(R.id.view_line, mList.type == 1 ? View.VISIBLE : View.GONE)
                .setVisibility(R.id.ll_like, mList.type == 1 ? View.VISIBLE : View.GONE)
                .setText(R.id.tv_shiled_opt, contactStr)
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(R.id.ll_like, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                        requestType = 1;
                        if (user_id == AccountHelper.getUser().id) {
                            AppContext.showToast("不能喜欢自己");
                        } else {
                            LRApi.usersLike(user_id, handler);
                        }
                    }
                })
                .setOnClickListener(R.id.tv_shiled_opt, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        if (contact_status == 2) {
                            dealBlackRevert(mList.base.id);
                        } else {
                            dealBlackAdd(mList.base.id);
                        }
                        dialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_collection_opt, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {

                        dialog.dismiss();
                    }
                }).setOnClickListener(R.id.tv_feed_back_opt, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                FeedTypeActivity.show(UserInfoActivity.this, UserConstants.FEED_USER, String.valueOf(mList.base.id));
                dialog.dismiss();
            }
        }).setOnClickListener(R.id.tv_cancel_opt, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                dialog.dismiss();
            }
        })
                .show();
    }

    //加入黑名单
    public void dealBlackAdd(final int id) {
        LRApi.contactBlackAdd(id,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        AppContext.showToast(R.string.request_error_hint);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    mPresenter.getUsersInfo(user_uuid, user_id);
                                } else {
                                    AppContext.showToast(bean.error.message);
                                }
                            } else {
                                AppContext.showToast(R.string.request_error_hint);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //移除黑名单
    public void dealBlackRevert(final int id) {
        LRApi.contactBlackRevert(id,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        AppContext.showToast(R.string.request_error_hint);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    mPresenter.getUsersInfo(user_uuid, user_id);
                                } else {
                                    AppContext.showToast(bean.error.message);
                                }
                            } else {
                                AppContext.showToast(R.string.request_error_hint);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //申请加入匹配
    public void pairsApplyAdd(final int id) {
        LRApi.parisApplyAdd(id,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        AppContext.showToast(R.string.request_error_hint);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    mPresenter.getUsersInfo(user_uuid, user_id);
                                } else {
                                    AppContext.showToast(bean.error.message);
                                }
                            } else {
                                AppContext.showToast(R.string.request_error_hint);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void showUsersInfo(UsersInfoBean mList) {
        this.mList = mList;
        if (mList == null) {
            SimplexToast.show("该用户信息不存在");
            finish();
        }
        refreshLayout.finishRefresh();
        recyclerNote.setVisibility(mList.type == 1 ? View.VISIBLE : View.GONE);
        llApplyPair.setVisibility(mList.type == 1 ? View.GONE : View.VISIBLE);
        llChat.setVisibility(mList.type == 1 ? View.VISIBLE : View.GONE);
        llScanImprint.setVisibility(View.VISIBLE);
        imgNext.setVisibility(mList.type == 1 ? View.VISIBLE : View.GONE);
        if (mList.type == 1) {
            List<UsersInfoBean.NoteBean> notes = new ArrayList<>();
            if (mList.note != null && mList.note.size() > 0) {
                for (UsersInfoBean.NoteBean noteBean : mList.note) {
                    if (!TextUtils.isEmpty(noteBean.content)) {
                        notes.add(noteBean);
                    }
                }
            }
            mAdapter.setNewData(notes);
        } else {
            tvPairStatus.setText(mList.base.pairApply_status == 0 ? "申请" : "已申请");
        }
        UsersInfoBean.BaseBean base = mList.base;
        if (base == null) return;
        ImageLoader.loadImage(getImageLoader(), parallax, base.bg_image);
        tvNickName.setText(base.nickname);
        tvUUid.setText("ID" + base.uuid);
        ImageLoader.loadImage(getImageLoader(), circlAvater, base.avatar_url);
        ImageLoader.loadImage(getImageLoader(), toolbarAvatar, base.avatar_url);
        String strSignature = "";
        if (!TextUtils.isEmpty(base.age)) {//年龄
            strSignature = base.age + "岁";
        }
        if (!TextUtils.isEmpty(base.domicile_name)) {//居住地名称
            if (!TextUtils.isEmpty(strSignature)) {
                strSignature = strSignature + " · " + base.domicile_name;
            } else {
                strSignature = base.domicile_name;
            }
        }
        if (!TextUtils.isEmpty(base.occupation_name)) {//专业
            if (!TextUtils.isEmpty(strSignature)) {
                strSignature = strSignature + " · " + base.occupation_name;
            } else {
                strSignature = base.occupation_name;
            }

        }
        if (!TextUtils.isEmpty(base.industry_name)) {//行业
            if (!TextUtils.isEmpty(strSignature)) {
                strSignature = strSignature + " · " + base.industry_name;
            } else {
                strSignature = base.industry_name;
            }
        }
        signature.setText(strSignature);
        tagFlowLayout.setAdapter(new TagAdapter<String>(mList.tag) {
            @Override
            public View getView(FlowLayout parent, int position, final String tag) {
                TextView tvTag = (TextView) UserInfoActivity.this.getLayoutInflater().inflate(R.layout.tag_item, tagFlowLayout, false);
                tvTag.setText(tag);
                return tvTag;
            }
        });
    }

    @Override
    public void showError(String message) {
        AppContext.showToast(message);
        finish();
    }

    @Override
    public void setPresenter(UsersInfoContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }


    @OnClick({R.id.ll_user_base_info, R.id.ll_apply_pair,
            R.id.ll_scan_imprint, R.id.ll_chat, R.id.avatar, R.id.rl_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_base_info://查看用户基础信息
                if (mList == null) return;
                if (mList.type == 2) return;
                UserBaseInfoActivity.show(this, mList, 2);
                break;
            case R.id.ll_scan_imprint:
                ImprintsListActivity.show(this, mList.base.id);
                break;
            case R.id.ll_chat:
                if (mList.base.id == AccountHelper.getUser().id) {
                    AppContext.showToast("不能跟自己聊天");
                } else {
                    UserSendMessageActivity.show(this, mList.base);
                }
                break;
            case R.id.ll_apply_pair://申请加入匹配
                pairsApplyAdd(mList.base.id);
                break;
            case R.id.avatar:
                if (mList == null) return;
                if (mList.photo != null && mList.photo.size() > 0) {
                    String[] photos = new String[mList.photo.size()];
                    for (int i = 0; i < mList.photo.size(); i++) {
                        photos[i] = mList.photo.get(i).img_uri;
                    }
                    ImageGalleryActivity.show(this, photos, 0);
                }
                break;
            case R.id.rl_menu:
                getSelectorDialog();
                break;
        }
    }

}
