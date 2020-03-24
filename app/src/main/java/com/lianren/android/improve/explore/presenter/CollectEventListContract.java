package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.EventBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:印记列表
 **/
public interface CollectEventListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showEventList(List<EventBean> mList);

        void showCollect(EventBean tags, int position);
    }

    interface Presenter extends BasePresenter {
        void getActivityFind(int type, int page);

        void activityCollect(EventBean tags, int position);
    }
}
