package com.lianren.android.improve.explore.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BaseBackActivity;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.explore.presenter.TweetPublishContract;
import com.lianren.android.improve.explore.presenter.TweetPublishOperator;
import com.lianren.android.util.pickimage.TweetSelectImageAdapter;
import com.lianren.android.widget.RichEditText;
import com.lianren.android.widget.TweetPicturesPreviewer;
import com.lianren.android.widget.tag.TagPickPreviewer;

import net.oschina.common.adapter.TextWatcherAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @package: com.lianren.android.improve.explore.fragments
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class PublishImprintFragment extends BaseFragment implements View.OnClickListener,
        TweetPublishContract.View {
    public static final int MAX_TEXT_LENGTH = 2000;
    public static final int REQUEST_CODE_SELECT_TOPIC = 0x0002;
    @Bind(R.id.edit_content)
    RichEditText mEditContent;

    @Bind(R.id.recycler_images)
    TweetPicturesPreviewer mLayImages;

    @Bind(R.id.txt_indicator)
    TextView mIndicator;

    @Bind(R.id.icon_back)
    View mIconBack;

    @Bind(R.id.icon_send)
    View mIconSend;
    @Bind(R.id.tag_pick)
    TagPickPreviewer tagPickPreviewer;
    @Bind(R.id.check_status)
    CheckBox checkStatus;
    private TweetPublishContract.Operator mOperator;
    private List<String> tag;
    private int status;
    private String note_id;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_publish_imprint;
    }

    public PublishImprintFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        // init operator
        this.mOperator = new TweetPublishOperator();
        String defaultContent = null;
        List<String> paths = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            defaultContent = bundle.getString("content");//内容
            paths = (List<String>) bundle.getSerializable("image");//图片
            tag = (List<String>) bundle.getSerializable("tag");//标签
            status = bundle.getInt("status");//标签
            note_id = bundle.getString("note_id");//标签

        }
        this.mOperator.setDataView(this, defaultContent, paths, tag, status, note_id);

        super.onAttach(context);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        tag = new ArrayList<>();
        mLayImages.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideAllKeyBoard();
                return false;
            }
        });
        checkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus.setText(isChecked ? "公开" : "私密");
            }
        });
        // add text change listener
        mEditContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final int len = s.length();
                final int surplusLen = MAX_TEXT_LENGTH - len;
                // set the send icon state
                setSendIconStatus(len > 0 && surplusLen >= 0, s.toString());
                // checkShare the indicator state
                if (surplusLen > 10) {
                    // hide
                    if (mIndicator.getVisibility() != View.INVISIBLE) {
                        ViewCompat.animate(mIndicator)
                                .alpha(0)
                                .setDuration(200)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIndicator.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .start();
                    }
                } else {
                    // show
                    if (mIndicator.getVisibility() != View.VISIBLE) {
                        ViewCompat.animate(mIndicator)
                                .alpha(1f)
                                .setDuration(200)
                                .withStartAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIndicator.setVisibility(View.VISIBLE);
                                    }
                                })
                                .start();
                    }

                    mIndicator.setText(String.valueOf(surplusLen));
                    //noinspection deprecation
                    mIndicator.setTextColor(surplusLen >= 0 ?
                            getResources().getColor(R.color.tweet_indicator_text_color) :
                            getResources().getColor(R.color.day_colorPrimary));
                }
            }
        });
        showSoftKeyboard(mEditContent);
    }

    private void setSendIconStatus(boolean haveContent, String content) {
        if (haveContent) {
            content = content.trim();
            haveContent = !TextUtils.isEmpty(content);
        }
        mIconSend.setEnabled(haveContent);
        mIconSend.setSelected(haveContent);
    }


    @Override
    public void setStatus(int status) {
        checkStatus.setChecked(status == 1);
    }

    @Override
    protected void initData() {
        super.initData();
        mOperator.loadData();
    }


    // 用于拦截后续的点击事件
    private long mLastClickTime;

    @OnClick({R.id.txt_indicator, R.id.icon_back,
            R.id.icon_send, R.id.edit_content})
    @Override
    public void onClick(View v) {
        // 用来解决快速点击多个按钮弹出多个界面的情况
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return;
        mLastClickTime = nowTime;
        try {
            switch (v.getId()) {
                case R.id.txt_indicator:
                    handleClearContentClick();
                    break;
                case R.id.icon_back:
                    mOperator.onBack();
                    break;
                case R.id.icon_send:
                    mOperator.publish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClearContentClick() {
        if (mIndicator.isSelected()) {
            mIndicator.setSelected(false);
            mEditContent.setText("");
        } else {
            mIndicator.setSelected(true);
            mIndicator.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIndicator.setSelected(false);
                }
            }, 1000);
        }
    }

    @Override
    public void setTag(final List<String> tags) {
        tagPickPreviewer.set(tags);
    }

    @Override
    public String getNoteId() {
        return note_id;
    }

    @Override
    public void setNoteId(String note_id) {
        this.note_id = note_id;
    }


    private void hideSoftKeyboard() {
        mEditContent.clearFocus();
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEditContent.getWindowToken(), 0);
    }

    private void showSoftKeyboard(final EditText requestView) {
        if (requestView == null)
            return;
        requestView.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(requestView,
                InputMethodManager.SHOW_FORCED);
    }

    private void hideAllKeyBoard() {
        hideSoftKeyboard();
    }

    @Override
    public int getStatus() {
        return checkStatus.isChecked() ? 1 : 0;
    }

    @Override
    public String getContent() {
        return mEditContent.getText().toString();
    }

    @Override
    public List<String> getImprintTag() {
        return tagPickPreviewer.getPaths();
    }

    @Override
    public void setContent(String content) {
        mEditContent.setText(content);
        mEditContent.setSelection(mEditContent.getText().length());
    }

    @Override
    public List<TweetSelectImageAdapter.Model> getImages() {
        return mLayImages.getModels();
    }

    @Override
    public void setImages(String[] paths) {
        mLayImages.set(paths);
    }

    @Override
    public void finish() {
        hideAllKeyBoard();
        // finish
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseBackActivity) {
            ((BaseBackActivity) activity).onSupportNavigateUp();
        }
    }

    @Override
    public TweetPublishContract.Operator getOperator() {
        return mOperator;
    }

    @Override
    public boolean onBackPressed() {
        mOperator.onBack();
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mOperator.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestartInstance(Bundle bundle) {
        super.onRestartInstance(bundle);
        if (bundle != null)
            mOperator.onRestoreInstanceState(bundle);
    }

}
