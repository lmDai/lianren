package com.lianren.android.improve.home.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.ShopBean;
import com.lianren.android.improve.bean.SystemMessageBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:系统消息列表
 **/
public interface SpaceListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showShopList(List<ShopBean> mList);
    }

    interface Presenter extends BasePresenter {
        void getShopList(int page);
    }
}
