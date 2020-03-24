package com.lianren.android.improve.user;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.UsersInfoBean;

/**
 * @package: com.lianren.android.improve.user
 * @user:xhkj
 * @date:2019/12/19
 * @description:
 **/
public class UserNoteAdapter extends BaseQuickAdapter<UsersInfoBean.NoteBean, BaseViewHolder> {
    public UserNoteAdapter() {
        super(R.layout.adapter_user_note);
    }

    @Override
    protected void convert(BaseViewHolder helper, UsersInfoBean.NoteBean item) {
        helper.setText(R.id.tv_tag, item.tag)
                .setText(R.id.tv_content, item.content);
    }
}
