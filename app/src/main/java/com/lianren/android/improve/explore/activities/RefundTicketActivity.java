package com.lianren.android.improve.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.EventApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.base.ResultBean;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/27
 * @description:订单票券
 **/
public class RefundTicketActivity extends BackActivity {

    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.btn_commit)
    Button btnCommit;
    private String order_id;

    public static void show(Context context, String order_id) {
        Intent intent = new Intent();
        intent.putExtra("order_id", order_id);
        intent.setClass(context, RefundTicketActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_order_refund;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        order_id = getIntent().getStringExtra("order_id");
    }

    @OnClick(R.id.btn_commit)
    public void onViewClicked() {
        String content = editContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            AppContext.showToast("请输入退票原因");
            return;
        }
        refundTicket(content);
    }

    private void refundTicket(String content) {
        EventApi.orderCancel(order_id, content, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();

                    ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_INVITE_STATUS_LIST, null));
                        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_INVITE_STATUS, null));
                        finish();
                    } else {
                        AppContext.showToast(resultBean.error.message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }
}

