package com.lianren.android.improve.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.explore.adapter.SearchTagsAdapter;
import com.lianren.android.improve.explore.presenter.SearchTagsContract;
import com.lianren.android.improve.explore.presenter.SearchTagsPresenter;
import com.lianren.android.util.TDevice;
import com.lianren.android.widget.RecycleViewDivider;
import com.lianren.android.widget.tag.SelectOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 印记话题
 */
public class ImprintTopicActivity extends BackActivity implements SearchTagsContract.View {
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.recyclerview)
    RecyclerView mRecycler;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.tv_search)
    TextView tvSearch;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.tag_add)
    TextView tagAdd;
    @Bind(R.id.ll_content)
    LinearLayout llContent;
    private SearchTagsPresenter mPresenter;
    private SearchTagsAdapter mAdapter;
    private static SelectOptions mOption;
    private List<String> mSelectedImage;

    public static void show(Context context, SelectOptions options) {
        mOption = options;
        context.startActivity(new Intent(context, ImprintTopicActivity.class));
    }


    private void showResult(String str) {
        if (mSelectedImage.contains(str)) {
            AppContext.showToast("请勿重复添加！");
            return;
        }
        mSelectedImage.add(str);
        if (mSelectedImage.size() != 0) {
            mOption.getCallback().doSelected(mSelectedImage);
            finish();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_imprint_topic;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mSelectedImage = new ArrayList<>();

        if (mOption.getSelectCount() > 1 && mOption.getSelectedImages() != null) {
            List<String> images = mOption.getSelectedImages();
            for (String s : images) {
                mSelectedImage.add(s);
            }
        }

        mAdapter = new SearchTagsAdapter();
        mRecycler.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecycler);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showResult(mAdapter.getItem(position));
            }
        });
        editContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.search(s.toString());
            }
        });
        editContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //如果actionId是搜索的id，则进行下一步的操作
                    TDevice.closeKeyboard(editContent);
                    mPresenter.search(editContent.getText().toString());
                }
                return false;
            }
        });
    }

    @Override
    public void showTagAdd() {
        showResult(editContent.getText().toString());
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new SearchTagsPresenter(this);
        mPresenter.search("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showSearchSuccess(List<String> list) {
        if (list != null && list.size() > 0) {
            refreshLayout.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
        } else {
            refreshLayout.setVisibility(View.GONE);
            llContent.setVisibility(View.VISIBLE);
            tvContent.setText(editContent.getText().toString());
        }
        mAdapter.setNewData(list);
    }

    @Override
    public void showSearchFailure(int strId) {
        AppContext.showToast(strId);
    }

    @Override
    public void showSearchFailure(String str) {
        AppContext.showToast(str);
    }

    @Override
    public void setPresenter(SearchTagsContract.Presenter presenter) {
    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }


    @OnClick({R.id.tv_search, R.id.tag_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                finish();
                break;
            case R.id.tag_add:
                mPresenter.tagAdd(tvContent.getText().toString());
                break;
        }
    }
}
