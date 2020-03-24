package com.lianren.android.improve.base.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.lianren.android.R;

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
import java.util.List;

/**
 * @package: com.lianren.android.improve.base.fragments
 * @user:xhkj
 * @date:2019/12/18
 * @description: Tab Fragment
 **/
public abstract class BasePagerFragment extends BaseFragment {
    protected MagicIndicator mTabLayout;
    protected ViewPager mViewPager;
    protected Adapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_view_pager;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTabLayout = root.findViewById(R.id.magic_indicator);
        mViewPager = root.findViewById(R.id.viewPager);
        mAdapter = new Adapter(getChildFragmentManager());
        mAdapter.reset(getFragments());
        mAdapter.reset(getTitles());
        mViewPager.setAdapter(mAdapter);
        if (mTabLayout != null) {
            setupTabView();
        }

    }

    protected void setupTabView() {
        initMagicIndicatorTitle();
    }

    @Override
    protected void initData() {

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

    protected abstract List<Fragment> getFragments();

    protected abstract String[] getTitles();

    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return getFragments().size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
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
        mTabLayout.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(mContext, 15);
            }
        });

        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(mTabLayout);
        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
        fragmentContainerHelper.setDuration(300);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                fragmentContainerHelper.handlePageSelected(position);
            }
        });

    }
}
