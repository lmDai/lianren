package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.PickStatusBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:印记列表
 **/
public interface ImprintsListContract {
    interface EmptyView {
        void hideEmptyLayout();

        void showErrorLayout(int errorType);
    }

    interface View extends BaseView<Presenter> {
        void showImprintsList(List<ImprintsBean> mList);
        void showPickResult(PickStatusBean bean, ImprintsBean imprintsBean, int position);
    }

    interface Presenter extends BasePresenter {
        void getImprintsList(List<String> tag, int page);
        void notiPick(ImprintsBean bean, int position);
    }
}
