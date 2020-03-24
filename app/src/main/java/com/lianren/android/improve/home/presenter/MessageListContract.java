package com.lianren.android.improve.home.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.NoticeSystemBean;
import com.lianren.android.improve.bean.SystemMessageBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:系统消息列表
 **/
public interface MessageListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showSystemMessage(List<SystemMessageBean> mList);
        void showNoticeResult(NoticeSystemBean result);
    }

    interface Presenter extends BasePresenter {
        void getSystemMessage(int page);
        void noticeSystemView(int notice_id);
    }
}
