package com.lianren.android.improve.home.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.MatchingBean;
import com.lianren.android.improve.bean.MessageBean;
import com.lianren.android.improve.bean.NoticeCountBean;
import com.lianren.android.improve.bean.base.ResultPageBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public interface MessageContract {
    interface View extends BaseView<Presenter> {
        void showNoticeCount(NoticeCountBean countBean);

        void showChatList(List<MessageBean> data);
    }

    interface Presenter extends BasePresenter {
        void getNoticeCount();
        void getChatList(int page);
    }
}
