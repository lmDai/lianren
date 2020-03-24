package com.lianren.android.improve.home.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.LRApplication;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.base.BaseDialog;
import com.lianren.android.base.BaseDialogFragment;
import com.lianren.android.base.BaseRecyclerAdapter;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.BaseGeneralRecyclerAdapter;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.Message;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.StatusBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.behavior.KeyboardInputDelegation;
import com.lianren.android.improve.feedback.FeedTypeActivity;
import com.lianren.android.improve.home.adapter.UserSendMessageAdapter;
import com.lianren.android.improve.home.presenter.ChatMessageContract;
import com.lianren.android.improve.home.presenter.ChatMessagePresenter;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.util.LocationService;
import com.lianren.android.util.TDevice;
import com.lianren.android.widget.RecyclerRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @package: com.lianren.android.improve.home.activities
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class UserSendMessageActivity extends BackActivity implements BaseGeneralRecyclerAdapter.Callback, ChatMessageContract.View
        , EasyPermissions.PermissionCallbacks {
    public static final int RC_LOCATION = 0x06;//定位权限
    @Bind(R.id.root)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refreshLayout)
    RecyclerRefreshLayout refreshLayout;
    private KeyboardInputDelegation mDelegation;

    EditText mViewInput;
    private UsersInfoBean.BaseBean mReceiver;
    private ProgressDialog mDialog;
    private boolean isFirstLoading = true;
    private Map<String, Message> mSendQuent = new HashMap<>();
    private UserSendMessageAdapter mAdapter;
    private ChatMessageContract.Presenter mPresenter;
    private int page = 1;

    public static void show(Context context, UsersInfoBean.BaseBean sender) {
        Intent intent = new Intent(context, UserSendMessageActivity.class);
        intent.putExtra("receiver", sender);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_send_message;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    private long mLastClickTime;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return false;
        mLastClickTime = nowTime;
        if (item.getItemId() == R.id.menu_item_more) {
            requestLocation();
        }
        return false;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new UserSendMessageAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = page + 1;
                mPresenter.getChatMessage(mReceiver.id + "", page);
            }
        });
    }

    private void showBottomDialog(int status) {
        new BaseDialogFragment.Builder(this)
                .setContentView(R.layout.view_chatting_setting)
                .setAnimStyle(BaseDialog.AnimStyle.BOTTOM)
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setVisibility(R.id.ll_invite, status == 1 ? View.VISIBLE : View.GONE)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(R.id.ll_invite, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        InviteBaseActivity.show(UserSendMessageActivity.this, mReceiver);
                        dialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.ll_user_info, new BaseDialog.OnClickListener() {
                    @Override
                    public void onClick(BaseDialog dialog, View view) {
                        UserInfoActivity.show(UserSendMessageActivity.this, mReceiver.id, mReceiver.uuid);
                        dialog.dismiss();
                    }
                }).setOnClickListener(R.id.ll_feed_back, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                FeedTypeActivity.show(UserSendMessageActivity.this, UserConstants.FEED_USER, mReceiver.uuid);
                dialog.dismiss();
            }
        }).setOnClickListener(R.id.ll_shiled, new BaseDialog.OnClickListener() {
            @Override
            public void onClick(BaseDialog dialog, View view) {
                dealBlackAdd(mReceiver.id);
                dialog.dismiss();
            }
        })
                .show();
    }

    public void dealBlackAdd(final int id) {
        LRApi.contactBlackAdd(id,
                new CommonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    finish();
                                } else {

                                }
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
        mDialog = new ProgressDialog(this);
        mReceiver = (UsersInfoBean.BaseBean) getIntent().getSerializableExtra("receiver");
        setTitle(mReceiver.nickname);
        init();
        mPresenter = new ChatMessagePresenter(this);
        mPresenter.getChatMessage(mReceiver.id + "", page);
    }

    protected void init() {
        mDelegation = KeyboardInputDelegation.delegation(this, mCoordinatorLayout, null);
        mDelegation.showEmoji(getSupportFragmentManager());
        mDelegation.setSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mDelegation.getInputText().replaceAll("[ \\s\\n]+", " ");
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(UserSendMessageActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDialog.setMessage("正在发送中...");
                mDialog.show();
                LRApi.pubMessage(mReceiver.id + "", "text", content, new CallBack(null));
            }
        });
        mViewInput = mDelegation.getInputView();
    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(mAdapter.getItems().size() - 1);
    }

    @Override
    public RequestManager getImgLoader() {
        return getImageLoader();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    @Override
    public void showChatMessage(List<Message> mList) {
        Collections.reverse(mList); // 倒序排列
        if (page == 1) {
            mAdapter.clear();
            mAdapter.addAll(mList);
            refreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(mList);
        }
        mAdapter.setState(mList == null || mList.size() < 10 ? BaseRecyclerAdapter.STATE_NO_MORE :
                BaseRecyclerAdapter.STATE_LOADING, true);
        refreshLayout.onComplete();
        if (isFirstLoading) {
            scrollToBottom();
            isFirstLoading = false;
        }
    }

    @Override
    public void showResult(int status) {
        showBottomDialog(status);
    }

    @Override
    public void showDatingStatus(StatusBean statusBean) {//邀约是否开放
        if (statusBean.status == 1) {//开放
            mPresenter.getContactsStatus(mReceiver.id);
        } else {
            showBottomDialog(0);
        }
    }

    @Override
    public void setPresenter(ChatMessageContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
    }

    private class CallBack extends CommonHttpResponseHandler {
        private String filePath;

        private CallBack(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Toast.makeText(UserSendMessageActivity.this, "发送失败，请检查数据", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_CHAT, null));
            Type type = new TypeToken<ResultBean<Message>>() {
            }.getType();
            try {
                ResultBean<Message> resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    mAdapter.addItem(resultBean.data);
                    scrollToBottom();
                    mViewInput.setText("");
                    TDevice.closeKeyboard(mViewInput);
                } else {
                    if (filePath != null) {
                        Message message = mSendQuent.get(filePath);
                        message.msg_seq = "-1";
                        mAdapter.updateItem(mAdapter.getItems().indexOf(message));
                    }
                    AppContext.showToast(resultBean.error.message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mDialog.dismiss();
        }

        @Override
        public void onStart() {
            super.onStart();
            // 发送前添加到最近联系人队列
//            ContactsCacheManager.addRecentCache(mReceiver);
        }
    }

    private LocationService locationService;

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationService = ((LRApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.start();
        }
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {

            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                int tag = 1;
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlongtitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nProvince : ");// 获取省份
                sb.append(location.getProvince());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nTown : ");// 获取镇信息
                sb.append(location.getTown());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nStreetNumber : ");// 获取街道号码
                sb.append(location.getStreetNumber());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append("poiName:");
                        sb.append(poi.getName() + ", ");
                        sb.append("poiTag:");
                        sb.append(poi.getTags() + "\n");
                    }
                }
                if (location.getPoiRegion() != null) {
                    sb.append("PoiRegion: ");// 返回定位位置相对poi的位置关系，仅在开发者设置需要POI信息时才会返回，在网络不通或无法获取时有可能返回null
                    PoiRegion poiRegion = location.getPoiRegion();
                    sb.append("DerectionDesc:"); // 获取POIREGION的位置关系，ex:"内"
                    sb.append(poiRegion.getDerectionDesc() + "; ");
                    sb.append("Name:"); // 获取POIREGION的名字字符串
                    sb.append(poiRegion.getName() + "; ");
                    sb.append("Tags:"); // 获取POIREGION的类型
                    sb.append(poiRegion.getTags() + "; ");
                    sb.append("\nSDK版本: ");
                }
                sb.append(locationService.getSDKVersion()); // 获取SDK版本
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                locationService.stop();
                mPresenter.getDatingOpenStatus(location.getLongitude() + "", location.getLatitude() + "");
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }
    };
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @SuppressLint("InlinedApi")
    @AfterPermissionGranted(RC_LOCATION)
    public void requestLocation() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            locationService.start();
        } else {
            EasyPermissions.requestPermissions(this, "", RC_LOCATION, permissions);
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        locationService.start();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
