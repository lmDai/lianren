package com.lianren.android.improve.user.presenter;

import com.lianren.android.improve.base.BasePresenter;
import com.lianren.android.improve.base.BaseView;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.ImprintsDetailBean;
import com.lianren.android.improve.bean.MatchingBean;
import com.lianren.android.improve.bean.UserRecordMessage;

import java.util.List;

/**
 * @package: com.lianren.android.improve.user.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:印记列表
 **/
public interface ImprintsDetailContract {

    interface View extends BaseView<Presenter> {
        void showImprintsDetail(ImprintsDetailBean detailBean);

        void showSuccess(UserRecordMessage bean);

        void showDeleteSuccess(UserRecordMessage tags, int position);

        void showDeleteFaile(String message);
    }

    interface Presenter extends BasePresenter {
        void getImprintsDetail(int userid, String note_id, String comment_id);

        void userNoteComment(String note_id, int type, int reply_user_id, String content);

        void userNoteDelete(UserRecordMessage item, int position);
    }
}
