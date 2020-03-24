package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.EventDetailBean;
import com.lianren.android.improve.bean.UriBean;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:活动详情
 **/
public interface EventDetailContract {

    interface View extends BaseView<Presenter> {
        void showEventDetail(EventDetailBean detailBean);

        void showUri(UriBean uri);

        void showError(String message);

        void showCollectStatus(int status);
    }

    interface Presenter extends BasePresenter {
        void getEventDetail(String activity_id);

        void getUri(String id);

        void activityCollect(int activity_id);
    }
}
