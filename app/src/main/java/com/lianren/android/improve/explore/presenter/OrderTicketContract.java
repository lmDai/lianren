package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.OrderTicketBean;
import com.lianren.android.improve.bean.TicketOrderDetailBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:订单票券
 **/
public interface OrderTicketContract {

    interface View extends BaseView<Presenter> {
        void showOrderTicket(OrderTicketBean bean);
    }

    interface Presenter extends BasePresenter {
        void getOrderTicket(String order_id);
    }
}
