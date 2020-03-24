package com.lianren.android.improve.user.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.home.adapter.ComFragmentAdapter;
import com.lianren.android.improve.user.activities.UserBaseInfoActivity;
import com.lianren.android.improve.user.activities.UserPhotoActivity;
import com.lianren.android.improve.user.presenter.UsersInfoContract;
import com.lianren.android.improve.user.presenter.UsersInfoPresenter;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.StatusBarUtil;
import com.lianren.android.util.TDevice;
import com.lianren.android.widget.JudgeNestedScrollView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.improve.user.fragments
 * @user:xhkj
 * @date:2019/12/17
 * @description:我的
 **/
public class UserInfoFragment extends BaseFragment implements UsersInfoContract.View {
    @Bind(R.id.iv_header)
    ImageView ivHeader;
    @Bind(R.id.tag_flow)
    TagFlowLayout tagFlow;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_uuid)
    TextView tvUuid;
    @Bind(R.id.relationship)
    LinearLayout relationship;
    @Bind(R.id.signature)
    TextView signature;
    @Bind(R.id.panel)
    RelativeLayout panel;
    @Bind(R.id.avatar)
    CircleImageView avatar;
    @Bind(R.id.panel_lyt)
    RelativeLayout panelLyt;
    @Bind(R.id.collapse)
    CollapsingToolbarLayout collapse;
    @Bind(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.scrollView)
    JudgeNestedScrollView scrollView;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.toolbar_avatar)
    CircleImageView toolbarAvatar;
    @Bind(R.id.toolbar_username)
    TextView toolbarUsername;
    @Bind(R.id.buttonBarLayout)
    ButtonBarLayout buttonBarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.magic_indicator_title)
    MagicIndicator magicIndicatorTitle;
    @Bind(R.id.fl_activity)
    FrameLayout flActivity;
    @Bind(R.id.ll_magic_title)
    LinearLayout llMagicTitle;
    @Bind(R.id.ll_magic)
    LinearLayout llMagic;
    int toolBarPositionY = 0;
    private int mOffset = 0;
    private int mScrollY = 0;
    private String[] mTitles = new String[]{"介绍", "设置"};
    private List<String> mDataList = Arrays.asList(mTitles);
    private UsersInfoContract.Presenter mPresenter;
    private int user_id;
    private String user_uuid;
    private UsersInfoBean mList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        StatusBarUtil.immersive(getActivity());
        StatusBarUtil.setPaddingSmart(getActivity(), toolbar);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                dealWithViewPager();
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int lastScrollY = 0;
            int h = (int) TDevice.dp2px(170);
            int color = ContextCompat.getColor(mContext, R.color.white) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int[] location = new int[2];
                magicIndicator.getLocationOnScreen(location);
                int yPosition = location[1];
                if (yPosition < toolBarPositionY) {
                    llMagicTitle.setVisibility(View.VISIBLE);
                    magicIndicatorTitle.setVisibility(View.VISIBLE);
                    scrollView.setNeedScroll(false);
                } else {
                    llMagicTitle.setVisibility(View.GONE);
                    magicIndicatorTitle.setVisibility(View.GONE);
                    scrollView.setNeedScroll(true);
                }
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBarLayout.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    ivHeader.setTranslationY(mOffset - mScrollY);
                }
                lastScrollY = scrollY;
            }
        });
        buttonBarLayout.setAlpha(0);
        toolbar.setBackgroundColor(0);
        viewPager.setAdapter(new ComFragmentAdapter(getChildFragmentManager(), getFragments()));
        viewPager.setOffscreenPageLimit(10);
        initMagicIndicator();
        initMagicIndicatorTitle();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getUsersInfo(user_uuid, user_id);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.REFRESH_USER) {
            user_id = AccountHelper.getUser().id;
            user_uuid = AccountHelper.getUser().uuid;
            mPresenter.getUsersInfo(user_uuid, user_id);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        mPresenter = new UsersInfoPresenter(this);
        user_id = AccountHelper.getUser().id;
        user_uuid = AccountHelper.getUser().uuid;
        mPresenter.getUsersInfo(user_uuid, user_id);
    }

    private void dealWithViewPager() {
        toolBarPositionY = toolbar.getHeight();
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = flActivity.getMeasuredHeight() - toolBarPositionY - magicIndicator.getHeight() + 1;
        viewPager.setLayoutParams(params);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(IntroductionFragment.newInstance());
        fragments.add(SettingsFragment.newInstance());
        return fragments;
    }

    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.tab_normal_color));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.tab_selected_color));
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(UIUtil.dip2px(context, 10));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.tab_selected_color));
                return linePagerIndicator;
            }
        });
        magicIndicatorTitle.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(mContext, 13);
            }
        });

        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magicIndicatorTitle);
        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
        fragmentContainerHelper.setDuration(300);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                fragmentContainerHelper.handlePageSelected(position);
            }
        });
        viewPager.setOffscreenPageLimit(getFragments().size());
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.tab_normal_color));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.tab_selected_color));
                simplePagerTitleView.setTextSize(12);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(UIUtil.dip2px(context, 10));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.tab_selected_color));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(mContext, 13);
            }
        });

        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magicIndicator);
        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
        fragmentContainerHelper.setDuration(300);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                fragmentContainerHelper.handlePageSelected(position);
            }
        });
        viewPager.setOffscreenPageLimit(getFragments().size());
    }

    public static final String ACTION_UP_DATE = "com.lianren.android.action.update";

    @Override
    public void showUsersInfo(UsersInfoBean mList) {
        this.mList = mList;
        refreshLayout.finishRefresh();
        UsersInfoBean.BaseBean base = mList.base;
        if (base == null) return;
        toolbarUsername.setText(base.nickname);
        tvNickname.setText(base.nickname);
        tvUuid.setText("ID" + base.uuid);
        ImageLoader.loadImage(getImgLoader(), avatar, base.avatar_url);
        ImageLoader.loadImage(getImgLoader(), ivHeader, base.bg_image);
        ImageLoader.loadImage(getImgLoader(), toolbarAvatar, base.avatar_url);
        String strSignature = "";
        if (!TextUtils.isEmpty(base.age)) {//年龄
            strSignature = base.age + "岁";
        }
        if (!TextUtils.isEmpty(base.domicile_name)) {//地区
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
        tagFlow.setAdapter(new TagAdapter<String>(mList.tag) {
            @Override
            public View getView(FlowLayout parent, int position, final String tag) {
                TextView tvTag = (TextView) getActivity().getLayoutInflater().inflate(R.layout.tag_item, tagFlow, false);
                tvTag.setText(tag);
                return tvTag;
            }
        });
        Intent broadcast = new Intent(ACTION_UP_DATE);
        broadcast.putExtra("user_info_bean", mList);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcast);

    }

    @Override
    public void showError(String message) {
        AppContext.showToast(message);
    }

    @Override
    public void setPresenter(UsersInfoContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }


    @OnClick({R.id.ll_user_info, R.id.avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_info:
                UserBaseInfoActivity.show(mContext, mList, 1);
                break;
            case R.id.avatar:
//                Intent intentHead = new Intent(mContext, PhotoActivity.class);
                Intent intentHead = new Intent(mContext, UserPhotoActivity.class);
                intentHead.putExtra("user_info", mList);
                mContext.startActivity(intentHead);
                break;
        }
    }
}
