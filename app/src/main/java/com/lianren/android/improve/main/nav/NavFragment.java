package com.lianren.android.improve.main.nav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.lianren.android.R;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.explore.fragments.ExploreFragment;
import com.lianren.android.improve.home.fragments.MainFragment;
import com.lianren.android.improve.user.fragments.UserInfoFragment;

import net.oschina.common.widget.drawable.shape.BorderShape;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.lianren.android.improve.home.fragments.MatchingFragment.ACTION_NEXT_PAGE;

/**
 * @package: com.lianren.android.improve.main.nav
 * @user:xhkj
 * @date:2019/12/17
 * @description:底部导航
 **/
public class NavFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.nav_item_index)
    NavigationButton navItemIndex;
    @Bind(R.id.nav_item_explore)
    NavigationButton navItemExplore;
    @Bind(R.id.nav_item_me)
    NavigationButton navItemMe;
    private Context mContext;
    private int mContainerId;
    private FragmentManager mFragmentManager;
    private NavigationButton mCurrentNavButton;
    private OnNavigationReselectListener mOnNavigationReselectListener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nav;
    }

    public NavFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        ShapeDrawable lineDrawable = new ShapeDrawable(new BorderShape(new RectF(0, 1, 0, 0)));
        lineDrawable.getPaint().setColor(getResources().getColor(R.color.list_divider_color));
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                new ColorDrawable(getResources().getColor(R.color.white)),
                lineDrawable
        });
        root.setBackgroundDrawable(layerDrawable);

        navItemIndex.init(R.drawable.tab_icon_index,
                R.string.main_tab_name_index,
                MainFragment.class);

        navItemExplore.init(R.drawable.tab_icon_explore,
                R.string.main_tab_name_explore,
                ExploreFragment.class);

        navItemMe.init(R.drawable.tab_icon_me,
                R.string.main_tab_name_my,
                UserInfoFragment.class);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mManager != null) {
            if (mReceiver != null)
                mManager.unregisterReceiver(mReceiver);
        }
    }

    protected LocalBroadcastManager mManager;
    private BroadcastReceiver mReceiver;

    private void registerLocalReceiver() {
        if (mManager == null)
            mManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEXT_PAGE);
        if (mReceiver == null)
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_NEXT_PAGE.equals(action)) {
                        doSelect(navItemExplore);
                    }
                }
            };
        mManager.registerReceiver(mReceiver, filter);
    }

    @OnClick({R.id.nav_item_index,
            R.id.nav_item_explore, R.id.nav_item_me})
    @Override
    public void onClick(View v) {
        if (v instanceof NavigationButton) {
            NavigationButton nav = (NavigationButton) v;
            doSelect(nav);
        }
    }

    public void setup(Context context, FragmentManager fragmentManager, int contentId, OnNavigationReselectListener listener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mContainerId = contentId;
        mOnNavigationReselectListener = listener;
        // do clear
        clearOldFragment();
        // do select first
        doSelect(navItemIndex);
        registerLocalReceiver();
    }

    @SuppressWarnings("RestrictedApi")
    private void clearOldFragment() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments.size() == 0)
            return;
        boolean doCommit = false;
        for (Fragment fragment : fragments) {
            if (fragment != this && fragment != null) {
                transaction.remove(fragment);
                doCommit = true;
            }
        }
        if (doCommit)
            transaction.commitNow();
    }

    private void doSelect(NavigationButton newNavButton) {
        NavigationButton oldNavButton = null;
        if (mCurrentNavButton != null) {
            oldNavButton = mCurrentNavButton;
            if (oldNavButton == newNavButton) {
                onReselect(oldNavButton);
                return;
            }
            oldNavButton.setSelected(false);
        }
        newNavButton.setSelected(true);
        doTabChanged(oldNavButton, newNavButton);
        mCurrentNavButton = newNavButton;
    }

    private void doTabChanged(NavigationButton oldNavButton, NavigationButton newNavButton) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (oldNavButton != null) {
            if (oldNavButton.getFragment() != null) {
                ft.detach(oldNavButton.getFragment());
            }
        }
        if (newNavButton != null) {
            if (newNavButton.getFragment() == null) {
                Fragment fragment = Fragment.instantiate(mContext,
                        newNavButton.getClx().getName(), null);
                ft.add(mContainerId, fragment, newNavButton.getTag());
                newNavButton.setFragment(fragment);
            } else {
                ft.attach(newNavButton.getFragment());
            }
        }
        ft.commit();
    }

    private void onReselect(NavigationButton navigationButton) {
        OnNavigationReselectListener listener = mOnNavigationReselectListener;
        if (listener != null) {
            listener.onReselect(navigationButton);
        }
    }

    public interface OnNavigationReselectListener {
        void onReselect(NavigationButton navigationButton);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
