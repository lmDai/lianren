package com.lianren.android.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.home.adapter.ComFragmentAdapter;
import com.lianren.android.improve.user.fragments.OtherUserBaseFragment;
import com.lianren.android.improve.user.fragments.OtherUserRequestFragment;
import com.lianren.android.improve.user.fragments.UserBaseFragment;
import com.lianren.android.improve.user.fragments.UserRequestFragment;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

import static com.lianren.android.improve.user.fragments.UserInfoFragment.ACTION_UP_DATE;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:
 **/
public class UserBaseInfoActivity extends BackActivity {
    @Bind(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @Bind(R.id.buttonBarLayout)
    ButtonBarLayout buttonBarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private String[] mTitles = new String[]{"基本资料", "恋人要求"};
    private List<String> mDataList = Arrays.asList(mTitles);
    private UsersInfoBean usersInfoBean;
    private int type = 1;

    public static void show(Context mContext, UsersInfoBean usersInfoBean, int type) {
        Intent intent = new Intent(mContext, UserBaseInfoActivity.class);
        intent.putExtra("user_info", usersInfoBean);
        intent.putExtra("type", type);
        mContext.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.REFRESH_USER) {
            if (type == 1 && AccountHelper.getUser().id == usersInfoBean.base.id) {
                getUserInfo();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getUserInfo() {
        LRApi.usersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UsersInfoBean>>() {
                    }.getType();
                    ResultBean<UsersInfoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Intent broadcast = new Intent(ACTION_UP_DATE);
                        broadcast.putExtra("user_info_bean", resultBean.data);
                        LocalBroadcastManager.getInstance(UserBaseInfoActivity.this).sendBroadcast(broadcast);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_MATCH, null));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_base_info;
    }

    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(UserBaseInfoActivity.this, R.color.tab_normal_color));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(UserBaseInfoActivity.this, R.color.tab_selected_color));
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
                linePagerIndicator.setColors(ContextCompat.getColor(UserBaseInfoActivity.this, R.color.tab_selected_color));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(UserBaseInfoActivity.this, 15);
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

    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        if (type == 1 && usersInfoBean.base.id == AccountHelper.getUserId()) {
            fragments.add(UserBaseFragment.newInstance(usersInfoBean));
            fragments.add(UserRequestFragment.newInstance(usersInfoBean));
        } else {
            fragments.add(OtherUserBaseFragment.newInstance(usersInfoBean));
            fragments.add(OtherUserRequestFragment.newInstance(usersInfoBean));
        }
        return fragments;
    }

    @Override
    protected void initData() {
        super.initData();
        usersInfoBean = (UsersInfoBean) getIntent().getSerializableExtra("user_info");
        type = getIntent().getIntExtra("type", -1);
        viewPager.setAdapter(new ComFragmentAdapter(getSupportFragmentManager(), getFragments()));
        viewPager.setOffscreenPageLimit(10);
        initMagicIndicatorTitle();
        EventBus.getDefault().register(this);
    }
}
