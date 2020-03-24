package com.lianren.android.improve.home.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.PairListBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:系统消息列表
 **/
public interface PairListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showPairs(List<PairListBean> mList);

        void showDeleteSuccess(PairListBean tags, int position);

        void showDeleteFailure(int strId);

        void showDeleteFailure(String strId);

        void showAcceptSuccess(PairListBean tags, int position);
    }

    interface Presenter extends BasePresenter {
        void getPairList(int page);

        void dealPair(PairListBean tags, int position, int status);
    }
}
