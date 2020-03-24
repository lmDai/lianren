package com.lianren.android.improve.user.adapter;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lianren.android.R;
import com.lianren.android.improve.bean.UserRecordMessage;
import com.lianren.android.improve.bean.UserRecordPublisher;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.widget.circle.SpannableClickable;

public class ImprintsCommentAdapter extends BaseQuickAdapter<UserRecordMessage, BaseViewHolder> {

    public ImprintsCommentAdapter() {
        super(R.layout.list_item_imprints_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserRecordMessage item) {

        switch (item.type) {
            case "1":
                helper.setText(R.id.tv_name, item.user.nickname)
                        .setText(R.id.tv_content, item.content)
                        .setText(R.id.tv_pub_date, item.created_at)
                        .addOnClickListener(R.id.tv_name);
                break;
            case "2":
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append("回复 ");
                builder.append(setClickableSpan(item.user.nickname, item.user));
                builder.append(" ");
                builder.append(item.content);
                helper.setText(R.id.tv_name, item.reply_user.nickname)
                        .setText(R.id.tv_content, builder)
                        .setText(R.id.tv_pub_date, item.created_at)
                        .addOnClickListener(R.id.tv_name);
                break;
        }


    }

    @NonNull
    private SpannableString setClickableSpan(final String textStr, final UserRecordPublisher item) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable(mContext.getResources().getColor(R.color.day_colorPrimary)) {
                                    @Override
                                    public void onClick(View widget) {
                                        UserInfoActivity.show(mContext, item.id, item.uuid);
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }
}

