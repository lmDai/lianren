package com.lianren.android.improve.user.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.bean.base.ResultBean;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public interface PhotoContract {
    interface View extends BaseView<Presenter> {
        void showDeleteResult(ResultBean<PhotoBean> result);
        void showUpdateResult(ResultBean<PhotoBean> result);
        void showUpLoadResult(ResultBean<PhotoBean> result);
    }

    interface Presenter extends BasePresenter {
        void deletePhoto(int photo_id);
        void updatePhoto(int photo_id,String img_uri,int location);
        void upLoadPhoto(String img_uri,int location);
    }
}
