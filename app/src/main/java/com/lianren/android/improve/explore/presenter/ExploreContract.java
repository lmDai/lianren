package com.lianren.android.improve.explore.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.PickStatusBean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.home.presenter
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public interface ExploreContract {
    interface View extends BaseView<Presenter> {
        void showRecommendEvent(List<EventBean> mList);
        void showImprintsList(List<ImprintsBean> mList);
        void showNoteTags(List<String> mlist);
        void showPickResult(PickStatusBean bean, ImprintsBean imprintsBean, int position);
        void showNoticeNoteCount(int count);
    }

    interface Presenter extends BasePresenter {
        void getRecommendEvent(String longitude,String latitude);
        void getNoteRecommend(int user_id, int page);
        void getNoteTags();
        void notiPick(ImprintsBean bean, int position);
        void getNoticeNoteCount();
    }
}
