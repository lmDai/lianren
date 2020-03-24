package com.lianren.android.improve.explore.presenter;


import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;

import java.util.List;

/**
 * 用户搜索标签界面
 * Created by haibin on 2018/05/28.
 */
public interface SearchTagsContract {

    interface View extends BaseView<Presenter> {
        void showSearchSuccess(List<String> list);
        void showSearchFailure(int strId);
        void showSearchFailure(String str);

        void showTagAdd();
    }

    interface Presenter extends BasePresenter {
        void search(String keyword);
        void tagAdd(String keyword);
    }
}
