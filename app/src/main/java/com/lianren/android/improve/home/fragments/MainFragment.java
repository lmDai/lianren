package com.lianren.android.improve.home.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BaseActivity;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.NoticeCountBean;
import com.lianren.android.improve.explore.activities.PublishImprintActivity;
import com.lianren.android.improve.notice.NoticeManager;
import com.lianren.android.interf.OnTabReselectListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.home.fragments
 * @user:xhkj
 * @date:2019/12/17
 * @description主页
 **/
public class MainFragment extends BaseFragment implements OnTabReselectListener,
        View.OnClickListener, NoticeManager.NoticeNotify {
    @Bind(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    @Bind(R.id.viewStatusBar)
    View mStatusBar;
    private ImageView badgeImageView;
    private BadgePagerTitleView badgePagerTitleView;
    private Adapter mAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        NoticeManager.bindNotify(this);
        if (BaseActivity.hasSetStatusBarColor) {
            mStatusBar.setBackgroundColor(getResources().getColor(R.color.status_bar_color));
        }
        mAdapter = new Adapter(getChildFragmentManager());
        mAdapter.reset(getFragments());
        mAdapter.reset(getTitles());
        mViewPager.setAdapter(mAdapter);
        initMagicIndicator2();
        mViewPager.setCurrentItem(0);

    }

    public static class Adapter extends FragmentPagerAdapter {
        private List<Fragment> mFragment = new ArrayList<>();
        private Fragment mCurFragment;
        private String[] mTitles;

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void reset(List<Fragment> fragments) {
            mFragment.clear();
            mFragment.addAll(fragments);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                mCurFragment = (Fragment) object;
            }
        }

        public Fragment getCurFragment() {
            return mCurFragment;
        }

        void reset(String[] titles) {
            this.mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragment.get(position);
        }

        @Override
        public int getCount() {
            return mFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    private void initMagicIndicator2() {
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return getFragments() == null ? 0 : getFragments().size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                badgePagerTitleView = new BadgePagerTitleView(context);
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.tab_normal_color));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.tab_selected_color));
                simplePagerTitleView.setText(getTitles()[index]);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                badgePagerTitleView.setInnerPagerTitleView(simplePagerTitleView);
                // setup badge
                if (index == 1) {
                    badgeImageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.simple_red_dot_badge_layout, null);
                    badgePagerTitleView.setBadgeView(badgeImageView);
                    badgePagerTitleView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_RIGHT, -UIUtil.dip2px(context, 5)));
                    badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 4));
                }
                // cancel badge when click tab, default true
                badgePagerTitleView.setAutoCancelBadge(false);
                return badgePagerTitleView;
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
        titleContainer.setDividerPadding(UIUtil.dip2px(mContext, 15));
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(mContext, 15);
            }
        });
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    @OnClick({R.id.img_write_print})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_write_print://发布印记
                PublishImprintActivity.show(mContext);
                break;
        }
    }

    @Override
    public void onTabReselect() {
    }

    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(MatchingFragment.newInstance());
        fragments.add(MessageFragment.newInstance());
        return fragments;
    }

    protected String[] getTitles() {
        return getResources().getStringArray(R.array.synthesize_titles);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeManager.bindNotify(this);
    }

    @Override
    public void onNoticeArrived(NoticeCountBean countBean) {
        if (badgePagerTitleView != null)
            if (badgePagerTitleView.getBadgeView() != null) {
                if (countBean != null) {
                    int count = 0;
                    if (countBean.pair != null) count = count + countBean.pair.count;
                    if (countBean.system != null) count = count + countBean.system.count;
                    if (countBean.dating != null) count = count + countBean.dating.count;
                    badgePagerTitleView.getBadgeView().setVisibility(count > 0 || (countBean.message != null ? countBean.message.count == 1 : false)
                            ? View.VISIBLE : View.GONE);
                }
            }
    }
}
