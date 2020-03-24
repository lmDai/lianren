package com.lianren.android.improve.user.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.ContactUserBean;
import com.lianren.android.improve.bean.PairListBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:联系人
 **/
public interface ContactListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showContactUser(List<ContactUserBean> mList);

        void showDeleteSuccess(ContactUserBean tags, int position);

        void showDeleteFailure(int strId);

        void showDeleteFailure(String strId);
    }

    interface Presenter extends BasePresenter {
        void getContactUser(int page);

        void dealBlackAdd(ContactUserBean tags, int position);
        void dealDelete(ContactUserBean tags, int position);
    }
}
