package com.lianren.android.improve.home.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.home.adapter.ComFragmentAdapter;
import com.lianren.android.improve.home.fragments.EventFragment;
import com.lianren.android.improve.home.fragments.SpaceFragment;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:
 **/
public class InviteBaseActivity extends BackActivity {
    @Bind(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @Bind(R.id.buttonBarLayout)
    ButtonBarLayout buttonBarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
//    private String[] mTitles = new String[]{"空间", "活动"};
    private String[] mTitles = new String[]{"空间"};
    private List<String> mDataList = Arrays.asList(mTitles);
    private UsersInfoBean.BaseBean mReceiver;

    public static void show(Context mContext) {
        mContext.startActivity(new Intent(mContext, InviteBaseActivity.class));
    }

    public static void show(Context mContext, UsersInfoBean.BaseBean usersInfoBean) {
        Intent intent = new Intent(mContext, InviteBaseActivity.class);
        intent.putExtra("receiver", usersInfoBean);
        mContext.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_invite_base;
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
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(InviteBaseActivity.this, R.color.tab_normal_color));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(InviteBaseActivity.this, R.color.tab_selected_color));
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
                linePagerIndicator.setColors(ContextCompat.getColor(InviteBaseActivity.this, R.color.tab_selected_color));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(InviteBaseActivity.this, 15);
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
        fragments.add(SpaceFragment.newInstance(mReceiver));
//        fragments.add(EventFragment.newInstance(mReceiver));
        return fragments;
    }

    @Override
    protected void initData() {
        super.initData();
        mReceiver = (UsersInfoBean.BaseBean) getIntent().getSerializableExtra("receiver");
        viewPager.setAdapter(new ComFragmentAdapter(getSupportFragmentManager(), getFragments()));
        viewPager.setOffscreenPageLimit(10);
        initMagicIndicatorTitle();
    }
}
