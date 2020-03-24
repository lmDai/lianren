package com.lianren.android.improve.home.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.Message;
import com.lianren.android.improve.bean.StatusBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:系统消息列表
 **/
public interface ChatMessageContract {

    interface View extends BaseView<Presenter> {
        void showChatMessage(List<Message> mList);

        void showDatingStatus(StatusBean statusBean);

        void showResult(int status);
    }

    interface Presenter extends BasePresenter {
        void getChatMessage(String remote_id, int page);

        void getDatingOpenStatus(String longitude, String latitude);

        void getContactsStatus(int user_id);
    }
}
