package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.EventDetailBean;
import com.lianren.android.improve.bean.ShopDetailBean;
import com.lianren.android.improve.bean.UriBean;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:空间详情
 **/
public interface ShopDetailContract {

    interface View extends BaseView<Presenter> {
        void shopShopDetail(ShopDetailBean detailBean);

        void showUri(UriBean uri);
    }

    interface Presenter extends BasePresenter {
        void getShopDetail(int shop_id);

        void getUri(String id);
    }
}
