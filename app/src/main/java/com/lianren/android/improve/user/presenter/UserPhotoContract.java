package com.lianren.android.improve.user.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.bean.base.ResultBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:用户相册
 **/
public interface UserPhotoContract {
    interface View extends BaseView<Presenter> {

        void showUsersPhotoList(List<PhotoBean> mList);//用户图片
    }

    interface Presenter extends BasePresenter {
        void usersPhotoList();

        void userPhotoSaveAll(List<PhotoBean> mList);
    }
}
