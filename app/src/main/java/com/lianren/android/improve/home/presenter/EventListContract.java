package com.lianren.android.improve.home.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.improve.bean.ShopBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:系统消息列表
 **/
public interface EventListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showEventList(List<EventBean> mList);
    }

    interface Presenter extends BasePresenter {
        void getEventList(int page);
    }
}
