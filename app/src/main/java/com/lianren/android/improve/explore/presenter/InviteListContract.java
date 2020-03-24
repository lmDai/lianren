package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.DatingListBean;
import com.lianren.android.improve.bean.TicketOrderDetailBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:邀约列表
 **/
public interface InviteListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showDatingList(List<DatingListBean> mList);
    }

    interface Presenter extends BasePresenter {
        void getDatingList(int page);
    }
}
