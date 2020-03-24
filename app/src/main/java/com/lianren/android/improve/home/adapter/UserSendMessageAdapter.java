package com.lianren.android.improve.home.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lianren.android.R;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.base.BaseGeneralRecyclerAdapter;
import com.lianren.android.improve.bean.Message;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.widget.TweetTextView;
import com.lianren.android.widget.circle.TweetParser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by huanghaibin_dev
 * on 2016/8/18.
 */
@SuppressLint("SimpleDateFormat")
public class UserSendMessageAdapter extends BaseGeneralRecyclerAdapter<Message> {
    private static final int SENDER = 1;
    private static final int RECEIVER = 2;
    private long authorId;

    public UserSendMessageAdapter(Callback callback) {
        super(callback, NEITHER);
        authorId = AccountHelper.getUserId();
    }

    @SuppressWarnings("all")
    @Override
    public int getItemViewType(int position) {
        Message item = getItem(position);
        if (item.from.id == authorId) {//如果是个人发送的私信
            return SENDER;
        } else {
            return RECEIVER;
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == SENDER)
            return new SenderViewHolder(mInflater.inflate(R.layout.item_list_user_send_message, parent, false));
        else if (type == RECEIVER)
            return new ReceiverViewHolder(mInflater.inflate(R.layout.item_list_receiver_message, parent, false));
        else
            return new SenderViewHolder(mInflater.inflate(R.layout.item_list_receiver_message_picture, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, final Message item, int position) {
        Message preMessage = position != 0 ? getItem(position - 1) : null;
        switch (getItemViewType(position)) {
            case SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.tv_sender.setText(TweetParser.getInstance().parse(mContext, item.msg_content));
                formatTime(preMessage, item, senderViewHolder.tv_send_time);
                ImageLoader.loadImage(Glide.with(mContext), senderViewHolder.imgHead, item.from.avatar_url);
                break;
            case RECEIVER:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                receiverViewHolder.tv_receiver.setText(TweetParser.getInstance().parse(mContext, item.msg_content));
                formatTime(preMessage, item, receiverViewHolder.tv_send_time);
                ImageLoader.loadImage(Glide.with(mContext), receiverViewHolder.imgHead, item.from.avatar_url);
                receiverViewHolder.imgHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoActivity.show(mContext, item.from.id, item.from.uuid);
                    }
                });
                break;
        }
    }


    private void formatTime(Message preMessage, Message item, TextView tv_time) {
        formatTime(tv_time, item.msg_time);
    }

    private void formatTime(TextView tv_time, String time) {
        tv_time.setVisibility(TextUtils.isEmpty(time) ? View.GONE : View.VISIBLE);
        if (TextUtils.isEmpty(time)) return;
        tv_time.setText(time);
    }


    /**
     * 倒序
     *
     * @param items items
     */
    @Override
    public void addAll(List<Message> items) {
        if (items != null) {
            mItems.addAll(0, items);
            notifyDataSetChanged();
        }

    }

    private static class SenderViewHolder extends RecyclerView.ViewHolder {
        TweetTextView tv_sender;
        TextView tv_send_time;
        CircleImageView imgHead;

        SenderViewHolder(View itemView) {
            super(itemView);
            tv_sender = itemView.findViewById(R.id.tv_sender);
            tv_send_time = itemView.findViewById(R.id.tv_send_time);
            imgHead = itemView.findViewById(R.id.img_avater);
            tv_sender.setMovementMethod(LinkMovementMethod.getInstance());
            tv_sender.setFocusable(false);
            tv_sender.setDispatchToParent(true);
            tv_sender.setLongClickable(false);
        }
    }

    private static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TweetTextView tv_receiver;
        TextView tv_send_time;
        CircleImageView imgHead;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            tv_receiver = itemView.findViewById(R.id.tv_receiver);
            tv_send_time = itemView.findViewById(R.id.tv_send_time);
            imgHead = itemView.findViewById(R.id.img_avater);
            tv_receiver.setMovementMethod(LinkMovementMethod.getInstance());
            tv_receiver.setFocusable(false);
            tv_receiver.setDispatchToParent(true);
            tv_receiver.setLongClickable(false);
        }
    }
}
