package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.DatingCreateBean;
import com.lianren.android.improve.bean.ShopGoodsBean;
import com.lianren.android.improve.bean.base.ResultBean;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:活动详情
 **/
public interface ShopGoodsContract {

    interface View extends BaseView<Presenter> {
        void showShopGood(ShopGoodsBean shopBean);

        void showResult(ResultBean<DatingCreateBean> resultBean);
    }

    interface Presenter extends BasePresenter {
        void getShopGoods(int shop_id, int source);

        void datingCreate(int remote_id, int type, int obj_id, int good_id, String time);
    }
}
