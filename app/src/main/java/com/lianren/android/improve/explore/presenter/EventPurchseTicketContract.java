package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.EventTicketBean;
import com.lianren.android.improve.bean.TicketOrderBean;
import com.lianren.android.improve.bean.TicketOrderDetailBean;
import com.lianren.android.improve.bean.base.ResultBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:活动详情
 **/
public interface EventPurchseTicketContract {

    interface View extends BaseView<Presenter> {
        void showEventGood(EventTicketBean detailBean);

        void showResult(ResultBean<TicketOrderDetailBean> orderDetailBean);
    }

    interface Presenter extends BasePresenter {
        void getActivityGood(String activity_id, int source);

        void createOrder(int type, String obj_id, List<TicketOrderBean> goods);
    }
}
