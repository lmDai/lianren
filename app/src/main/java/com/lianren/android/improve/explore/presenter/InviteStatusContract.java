package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.DatingDetailBean;
import com.lianren.android.improve.bean.ShopGoodsBean;
import com.lianren.android.improve.bean.base.ResultBean;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:活动详情
 **/
public interface InviteStatusContract {

    interface View extends BaseView<Presenter> {
        void showDatingDetail(DatingDetailBean shopBean);

        void showResult(ResultBean resultBean);
    }

    interface Presenter extends BasePresenter {
        void getDatingDetail(int dating_id);

        void datingRequestDeal(int dating_id,int status);
        void datingCancel(int dating_id);
    }
}
