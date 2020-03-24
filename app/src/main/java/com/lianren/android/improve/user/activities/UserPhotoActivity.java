package com.lianren.android.improve.user.activities;

import android.os.Bundle;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.user.presenter.UserPhotoContract;
import com.lianren.android.improve.user.presenter.UserPhotoPresenter;
import com.lianren.android.widget.userphoto.UserPhotoPreviewer;

import java.util.List;

import butterknife.Bind;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2020/1/6
 * @description:用户相册
 **/
public class UserPhotoActivity extends BackActivity implements UserPhotoContract.View {
    public static final int TYPE_HEAD = 5;
    @Bind(R.id.recycler_images)
    UserPhotoPreviewer recyclerImages;
    private UserPhotoContract.Presenter mPresenter;

    @Override
    protected int getContentView() {
        return R.layout.activity_user_photo;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerImages.setType(1);
        mPresenter = new UserPhotoPresenter(this);
        mPresenter.usersPhotoList();//获取用户相册数据
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.userPhotoSaveAll(recyclerImages.getData());
    }

    @Override
    public void showUsersPhotoList(List<PhotoBean> mList) {
        recyclerImages.setData(mList);
    }

    @Override
    public void setPresenter(UserPhotoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }
}
