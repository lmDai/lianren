package com.lianren.android.improve.user.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.ImprintsDetailBean;
import com.lianren.android.improve.bean.ImprintsVisibleBean;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.PickStatusBean;
import com.lianren.android.improve.bean.UserRecordMessage;
import com.lianren.android.improve.bean.UserRecordPublisher;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.explore.activities.ImageGalleryImprintActivity;
import com.lianren.android.improve.explore.activities.PublishImprintActivity;
import com.lianren.android.improve.explore.activities.SearchImprintActivity;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.user.adapter.ImprintsCommentAdapter;
import com.lianren.android.improve.user.presenter.ImprintsDetailContract;
import com.lianren.android.improve.user.presenter.ImprintsDetailPresenter;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.TDevice;
import com.lianren.android.widget.LikesView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 印记详情
 */
public class ImprintsDetailActivity extends BackActivity implements ImprintsDetailContract.View {

    @Bind(R.id.fragment_container)
    RecyclerView fragmentContainer;
    private ImprintsDetailContract.Presenter mPresenter;
    private String note_id;
    private String comment_id;
    private ImprintsCommentAdapter mAdapter;
    private ImprintsDetailBean detailBean;
    private CommonHttpResponseHandler publishCommentHandler;
    private int type = 1;
    private int replay_user_id;
    private int mRequestType = 1;
    private int status;//印记状态是否公开 0否 1是
    private ImageView imgHead, imgLike;
    private TextView tvNick, tvTime, tvHide, tvPickNum, tvCommentCount, mViewRefContent;
    private LinearLayout llImageContainer;
    private LikesView likeView;
    private TagFlowLayout tagFlow;
    @Bind(R.id.edit_comment)
    EditText editContent;
    private int user_id;
    private boolean isComment;

    @Override
    protected int getContentView() {
        return R.layout.activity_imprints_detail;
    }

    public static void show(Context mContext, int user_id, String note_id, String comment_id) {
        Intent intent = new Intent();
        intent.putExtra("user_id", user_id);
        intent.putExtra("note_id", note_id);
        intent.putExtra("comment_id", comment_id);
        intent.setClass(mContext, ImprintsDetailActivity.class);
        mContext.startActivity(intent);
    }

    public static void show(Context mContext, int user_id, String note_id, String comment_id, boolean isComment) {
        Intent intent = new Intent();
        intent.putExtra("user_id", user_id);
        intent.putExtra("note_id", note_id);
        intent.putExtra("comment_id", comment_id);
        intent.putExtra("isComment", isComment);
        intent.setClass(mContext, ImprintsDetailActivity.class);
        mContext.startActivity(intent);
    }

    private View getHeaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.adapter_header_imprint, null);
        imgHead = view.findViewById(R.id.img_avater);
        tvNick = view.findViewById(R.id.tv_nick);
        tvTime = view.findViewById(R.id.tv_time);
        tvHide = view.findViewById(R.id.tv_hide);
        mViewRefContent = view.findViewById(R.id.tv_ref_content);
        llImageContainer = view.findViewById(R.id.ll_image_container);
        tagFlow = view.findViewById(R.id.tag_flow);
        imgLike = view.findViewById(R.id.img_like);
        tvPickNum = view.findViewById(R.id.tv_pick_num);
        likeView = view.findViewById(R.id.likeView);
        tvCommentCount = view.findViewById(R.id.tv_comment_count);
        view.findViewById(R.id.ll_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestType = 4;
                LRApi.usersNotePick(detailBean.id, publishCommentHandler);
            }
        });
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.show(ImprintsDetailActivity.this, detailBean.user.id, detailBean.user.uuid);
            }
        });
        tvNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.show(ImprintsDetailActivity.this, detailBean.user.id, detailBean.user.uuid);
            }
        });
        return view;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        isComment = getIntent().getBooleanExtra("isComment", false);
        if (isComment) {
            fragmentContainer.setFocusable(false);
            fragmentContainer.clearFocus();
        }
        mAdapter = new ImprintsCommentAdapter();
        fragmentContainer.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(fragmentContainer);
        mAdapter.addHeaderView(getHeaderView());
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                type = 2;
                UserRecordMessage bean = mAdapter.getItem(position);
                replay_user_id = bean.user.id;
                toReplay(bean.user.nickname);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UserRecordMessage item = mAdapter.getItem(position);
                if (view.getId() == R.id.tv_name) {
                    UserInfoActivity.show(ImprintsDetailActivity.this, item.user.id, item.user.uuid);
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                UserRecordMessage item = mAdapter.getItem(position);
                if (detailBean.user.id == AccountHelper.getUser().id) {
                    showDisLikeDialog(item, position);
                } else if (item.type.equals("1") && item.user.id == AccountHelper.getUser().id) {
                    showDisLikeDialog(item, position);
                } else if (item.type.equals("2") && item.reply_user.id == AccountHelper.getUser().id) {
                    showDisLikeDialog(item, position);
                }
                return false;
            }
        });
    }

    private void showDisLikeDialog(final UserRecordMessage item, final int position) {
        DialogHelper.getConfirmDialog(this, "提示", "是否删除?", "确认", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoadingDialog();
                mPresenter.userNoteDelete(item, position);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private void toReplay(String nickName) {
        editContent.setHint("回复: @" + nickName);
    }

    @Override
    public void showDeleteSuccess(UserRecordMessage tags, int position) {
        dismissLoadingDialog();
        mAdapter.remove(position);
    }

    @Override
    public void showDeleteFaile(String message) {
        dismissLoadingDialog();
        AppContext.showToast(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_more:
                if (detailBean.user.id == AccountHelper.getUser().id) {
                    showSelfDialog();
                } else {
                    showDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //其他用户
    private void showDialog() {
        new BaseDialogFragment.Builder(this)
                .setContentView(R.layout.view_other_user_operator)
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(R.id.tv_cancel_opt, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_scan_info_opt, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        UserInfoActivity.show(ImprintsDetailActivity.this, detailBean.user.id, detailBean.user.uuid);
                        dialog.dismiss();
                    }
                }).setOnClickListener(R.id.tv_feed_back_opt, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                FeedTypeActivity.show(ImprintsDetailActivity.this, UserConstants.FEED_IMPRINT, String.valueOf(detailBean.id));
                dialog.dismiss();
            }
        })
                .show();
    }

    //自己
    private void showSelfDialog() {
        new BaseDialogFragment.Builder(this)
                .setContentView(R.layout.view_user_operator)
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setVisibility(R.id.tv_tag_set, detailBean.tag != null && detailBean.tag.size() > 0 ? View.VISIBLE : View.GONE)
                .setText(R.id.tv_tag_set, (detailBean.tag != null && detailBean.tag.size() > 0 && detailBean.tag.get(0).isTop) ? "取消置顶" :
                        "置顶到个人主页")
                .setText(R.id.tv_hide_opt, status == 0 ? "设为公开" : "设为私密")
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(R.id.tv_cancel_opt, new BaseDialog.OnClickListener() {//取消
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_tag_set, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        dialog.dismiss();
                        mRequestType = 5;
                        LRApi.tagSet(detailBean.tag.get(0).tag, publishCommentHandler);
                    }
                })
                .setOnClickListener(R.id.tv_hide_opt, new BaseDialog.OnClickListener() {//隐藏
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        mRequestType = 2;
                        LRApi.usersNoteVisible(detailBean.id, status == 0 ? 1 : 0, publishCommentHandler);
                        dialog.dismiss();
                    }
                }).setOnClickListener(R.id.tv_edit_opt, new BaseDialog.OnClickListener() {//编辑
            @Override
            public void onClick(BaseDialog dialog, View view) {
                List<String> tag = new ArrayList<>();
                if (detailBean.tag != null && detailBean.tag.size() > 0) {
                    for (ImprintsDetailBean.TagBean tagBean : detailBean.tag) {
                        tag.add(tagBean.tag);
                    }
                }
                PublishImprintActivity.show(ImprintsDetailActivity.this,
                        detailBean.content.image_url, detailBean.content.text, tag, detailBean.status,
                        detailBean.id);
                dialog.dismiss();
            }
        }).setOnClickListener(R.id.tv_delete_opt, new BaseDialog.OnClickListener() {//删除
            @Override
            public void onClick(BaseDialog dialog, View view) {
                mRequestType = 3;
                LRApi.usersNoteDelete(detailBean.id, publishCommentHandler);
                dialog.dismiss();
            }
        })
                .show();
    }

    @Override
    protected void initData() {
        super.initData();
        user_id = getIntent().getIntExtra("user_id", -1);
        note_id = getIntent().getStringExtra("note_id");
        comment_id = getIntent().getStringExtra("comment_id");
        mPresenter = new ImprintsDetailPresenter(this);
        mPresenter.getImprintsDetail(user_id, note_id, comment_id);
        publishCommentHandler = new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                switch (mRequestType) {
                    case 1:
                        AppContext.showToastShort("回复失败");
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                switch (mRequestType) {
                    case 1:
                        type = 1;
                        replay_user_id = -1;
                        editContent.setHint("输入回复内容");
                        editContent.setText("");
                        try {
                            Type type = new TypeToken<ResultBean<UserRecordMessage>>() {
                            }.getType();
                            ResultBean<UserRecordMessage> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                AppContext.showToastShort("回复成功");
                                showSuccess(resultBean.data);
                            } else {
                                AppContext.showToastShort(resultBean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                        break;
                    case 2:
                        try {
                            Type type = new TypeToken<ResultBean<ImprintsVisibleBean>>() {
                            }.getType();
                            ResultBean<ImprintsVisibleBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                status = resultBean.data.status;
                                tvHide.setText(resultBean.data.status == 0 ? "私密" : "公开");
                            } else {
                                AppContext.showToast(resultBean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                        break;
                    case 3:
                        try {
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                finish();
                            } else {
                                AppContext.showToast(resultBean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                        break;
                    case 4:
                        try {
                            Type type = new TypeToken<ResultBean<PickStatusBean>>() {
                            }.getType();
                            ResultBean<PickStatusBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                UserRecordPublisher publisher = new UserRecordPublisher();
                                publisher.id = AccountHelper.getUser().id;
                                publisher.uuid = AccountHelper.getUser().uuid;
                                publisher.avatar_url = AccountHelper.getUser().avatar_url;
                                publisher.nickname = AccountHelper.getUser().nickname;
                                tvPickNum.setText(resultBean.data.pick_num == 0 ? "" : resultBean.data.pick_num + "");
                                imgLike.setImageResource(resultBean.data.pick_status == 0 ? R.mipmap.icon_praise_normal : R.mipmap.icon_praise_blue);
                            } else {
                                AppContext.showToast(resultBean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                        break;
                    case 5:
                        try {
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                if ((detailBean.tag != null && detailBean.tag.size() > 0 && detailBean.tag.get(0).isTop)) {
                                    AppContext.showToast("已取消置顶");
                                } else {
                                    AppContext.showToast("已置顶到主页");
                                }
                                TDevice.closeKeyboard(editContent);
                                mPresenter.getImprintsDetail(user_id, note_id, comment_id);
                                EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                            } else {
                                AppContext.showToast(resultBean.error.message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                        break;
                }

            }

            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
                switch (mRequestType) {
                    case 1:
                        TDevice.closeKeyboard(editContent);
                        break;
                    case 2:
                        break;

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                switch (mRequestType) {
                    case 1:
                        type = 1;
                        replay_user_id = -1;
                        TDevice.closeKeyboard(editContent);
                        break;
                    case 2:
                        break;
                }

            }
        };
    }

    @Override
    public void showSuccess(UserRecordMessage bean) {
        if (bean != null) {
            bean.id = bean.comment_id;
            mAdapter.addData(bean);
        }
    }

    @Override
    public void showImprintsDetail(final ImprintsDetailBean detailBean) {
        this.detailBean = detailBean;
        if (detailBean == null) return;
        if (detailBean.user.id == AccountHelper.getUserId()) {
            tvHide.setVisibility(detailBean.status == 0 ? View.VISIBLE : View.GONE);
            status = detailBean.status;
            tvHide.setText(detailBean.status == 0 ? "私密" : "公开");
        } else {
            tvHide.setVisibility(View.GONE);
        }
        tvPickNum.setText(detailBean.pick_num == 0 ? "" : detailBean.pick_num + "");
        imgLike.setImageResource(detailBean.pick_status == 0 ? R.mipmap.icon_praise_normal : R.mipmap.icon_praise_blue);
        ImageLoader.loadImage(getImageLoader(), imgHead, detailBean.user.avatar_url);
        tvNick.setText(detailBean.user.nickname);
        tvTime.setText(detailBean.created_at);
        mViewRefContent.setText(detailBean.content.text);
        mAdapter.setNewData(detailBean.comments);
        llImageContainer.removeAllViews();
        if (detailBean.content.image_url != null && detailBean.content.image_url.size() > 0) {
            for (int i = 0; i < detailBean.content.image_url.size(); i++) {
                ImageView imageView = new ImageView(this);
                ImageLoader.loadAutoImage(getImageLoader(), imageView, detailBean.content.image_url.get(i));
                final int finalI = i;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageGalleryImprintActivity.show(ImprintsDetailActivity.this, detailBean, finalI);
                    }
                });
                llImageContainer.addView(imageView);
            }
        }
        likeView.setList(detailBean.pick_users);
        likeView.notifyDataSetChanged();
        if (detailBean.comments != null && detailBean.comments.size() > 0) {
            tvCommentCount.setText("回复 " + detailBean.comments.size());
        } else {
            tvCommentCount.setText("回复");
        }
        tagFlow.setAdapter(new TagAdapter<ImprintsDetailBean.TagBean>(detailBean.tag) {
            @Override
            public View getView(FlowLayout parent, int position, final ImprintsDetailBean.TagBean tag) {
                TextView tvTag = (TextView) LayoutInflater.from(ImprintsDetailActivity.this).inflate(R.layout.tag_item_imprints, tagFlow, false);
                tvTag.setText(String.format("#%s", tag.tag));
                return tvTag;
            }
        });
        likeView.setListener(new LikesView.onItemClickListener() {
            @Override
            public void onItemClick(int position, UserRecordPublisher bean) {
                UserInfoActivity.show(ImprintsDetailActivity.this, bean.id, bean.uuid);
            }
        });
        tagFlow.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                List<String> tag = new ArrayList<>();
                ImprintsDetailBean.TagBean tagBean = (ImprintsDetailBean.TagBean) tagFlow.getAdapter().getItem(position);
                tag.add(tagBean.tag);
                SearchImprintActivity.show(ImprintsDetailActivity.this, tag);
                return false;
            }
        });
    }

    @Override
    public void setPresenter(ImprintsDetailContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }

    @OnClick(R.id.img_add)
    public void onViewClicked() {
        String content = editContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(ImprintsDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
            return;
        }
        if (detailBean == null) return;
        mRequestType = 1;
        LRApi.userNoteComment(detailBean.id, type, replay_user_id, content, publishCommentHandler);
    }
}
