package com.lianren.android.improve.user.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.ContactUserBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:联系人
 **/
public interface ContactLikeContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showContactUser(List<ContactUserBean> mList);
    }

    interface Presenter extends BasePresenter {
        void getContactUser(int page,int type);
    }
}
