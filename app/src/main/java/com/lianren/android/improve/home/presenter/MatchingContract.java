package com.lianren.android.improve.home.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.improve.bean.MatchingBean;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public interface MatchingContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showMatchings(MatchingBean mList);
        void showDeleteSuccess(MatchingBean.UserBean tags, int position);

        void showDeleteFaile(String message);
    }

    interface Presenter extends BasePresenter {
        void getMatchings();
        void pairsDisLike(MatchingBean.UserBean tags, int position);
    }
}
