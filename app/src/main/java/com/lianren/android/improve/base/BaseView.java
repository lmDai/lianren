package com.lianren.android.improve.base;

public  interface BaseView<Presenter extends BasePresenter> {

    void setPresenter(Presenter presenter);

    void showNetworkError(int strId);

}
