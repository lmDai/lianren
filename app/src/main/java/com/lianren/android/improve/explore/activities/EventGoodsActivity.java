package com.lianren.android.improve.explore.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.DatingCreateBean;
import com.lianren.android.improve.bean.EventTicketBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.explore.adapter.EventGoodAdapter;
import com.lianren.android.improve.explore.presenter.EventGoodsContract;
import com.lianren.android.improve.explore.presenter.EventGoodsPresenter;
import com.lianren.android.util.ImageLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/19
 * @description:邀约选择
 **/
public class EventGoodsActivity extends BackActivity implements EventGoodsContract.View {
    @Bind(R.id.img_avater)
    CircleImageView imgAvater;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.recycler_goods)
    RecyclerView recyclerGoods;
    @Bind(R.id.tv_select_time)
    TextView tvSelectTime;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private EventGoodsContract.Presenter mPresenter;
    private int shop_id;
    private int source;
    private EventGoodAdapter mAdapter;
    private UsersInfoBean.BaseBean mReceiver;
    private String time;

    public static void show(Context context, int shop_id, int source) {
        Intent intent = new Intent();
        intent.putExtra("activity_id", shop_id);
        intent.putExtra("source", source);
        intent.setClass(context, EventGoodsActivity.class);
        context.startActivity(intent);
    }

    public static void show(Context context, int shop_id, int source, UsersInfoBean.BaseBean mReceiver) {
        Intent intent = new Intent();
        intent.putExtra("activity_id", shop_id);
        intent.putExtra("source", source);
        intent.putExtra("receiver", mReceiver);
        intent.setClass(context, EventGoodsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_event_goods;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new EventGoodAdapter();
        recyclerGoods.setLayoutManager(new LinearLayoutManager(this));
        recyclerGoods.setNestedScrollingEnabled(false);
        mAdapter.bindToRecyclerView(recyclerGoods);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.setChecked(position);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        showLoadingDialog("加载中....");
        mReceiver = (UsersInfoBean.BaseBean) getIntent().getSerializableExtra("receiver");
        shop_id = getIntent().getIntExtra("activity_id", -1);
        source = getIntent().getIntExtra("source", -1);
        mPresenter = new EventGoodsPresenter(this);
        mPresenter.getActivityGood(String.valueOf(shop_id), source);
        ImageLoader.loadImage(getImageLoader(), imgAvater, mReceiver.avatar_url);
        tvNickname.setText(mReceiver.nickname);
    }

    @Override
    public void showResult(ResultBean<DatingCreateBean> resultBean) {
        dismissLoadingDialog();
        if (resultBean.isSuccess()) {
            InviteStatusActivity.show(this, resultBean.data.dating_id);
        } else {
            AppContext.showToast(resultBean.error.message);
        }
    }


    @Override
    public void setPresenter(EventGoodsContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        dismissLoadingDialog();
        AppContext.showToast(strId);
    }

    private void createOrder() {
        if (mAdapter.getChecked() == null) return;
        if (TextUtils.isEmpty(time)) {
            AppContext.showToast("请选择邀约时间");
            return;
        }
        showLoadingDialog();
        mPresenter.datingCreate(mReceiver.id, 1, shop_id, mAdapter.getChecked().id, time);
    }

    private long mLastClickTime;
    private TimePickerView pvTime;//年龄

    @OnClick({R.id.ll_choose_time, R.id.btn_status})
    public void onViewClicked(View view) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return;
        mLastClickTime = nowTime;
        switch (view.getId()) {
            case R.id.ll_choose_time://选择时间
                initTimePicker();
                break;
            case R.id.btn_status://发起邀约
                createOrder();
                break;
        }
    }

    //出生日期选择
    private void initTimePicker() {//Dialog 模式下，在底部弹出
        if (pvTime == null) {
            showLoadingDialog();
            pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    time = getTime(date);
                    tvSelectTime.setText("邀约 " + getTime(date));
                }
            }).setType(new boolean[]{true, true, true, true, true, true})
                    .setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setTitleSize(14)//标题文字大小
                    .setTitleText("邀约时间")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(true)//是否循环滚动
                    .setTitleColor(ContextCompat.getColor(this, R.color.tab_selected_color))//标题文字颜色
                    .setSubmitColor(ContextCompat.getColor(this, R.color.day_colorPrimary))//确定按钮文字颜色
                    .setCancelColor(ContextCompat.getColor(this, R.color.main_gray))//取消按钮文字颜色
                    .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .isDialog(true)
                    .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                    .setLineSpacingMultiplier(2.0f)
                    .isAlphaGradient(true)
                    .build();
            Dialog mDialog = pvTime.getDialog();
            if (mDialog != null) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM);
                params.leftMargin = 0;
                params.rightMargin = 0;
                pvTime.getDialogContainerLayout().setLayoutParams(params);
                Window dialogWindow = mDialog.getWindow();
                if (dialogWindow != null) {
                    dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                    dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                    dialogWindow.setDimAmount(0.3f);
                }
            }
            dismissLoadingDialog();
        }
        pvTime.show();
    }

    public String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void showEventGood(EventTicketBean detailBean) {
        dismissLoadingDialog();
        tvName.setText(detailBean.name);
        tvAddress.setText(detailBean.address);
        for (int i = 0; i < detailBean.goods.size(); i++) {
            detailBean.goods.get(i).checked = i == 0;
        }
        mAdapter.setNewData(detailBean.goods);
    }

}
