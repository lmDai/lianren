package com.lianren.android.improve.user.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.MatchingBean;
import com.lianren.android.improve.bean.UsersInfoBean;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public interface UsersInfoContract {
    interface View extends BaseView<Presenter> {
        void showUsersInfo(UsersInfoBean mList);
        void showError(String message);
    }

    interface Presenter extends BasePresenter {
        void getUsersInfo(String user_uuid,int user_id);
    }
}
