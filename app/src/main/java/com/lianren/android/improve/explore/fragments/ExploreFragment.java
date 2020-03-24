package com.lianren.android.improve.explore.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lianren.android.AppContext;
import com.lianren.android.LRApplication;
import com.lianren.android.R;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.base.activities.BaseActivity;
import com.lianren.android.improve.base.fragments.BaseFragment;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.EventBean;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.PickStatusBean;
import com.lianren.android.improve.explore.activities.EventActivity;
import com.lianren.android.improve.explore.activities.EventDetailActivity;
import com.lianren.android.improve.explore.activities.ImageGalleryImprintActivity;
import com.lianren.android.improve.explore.activities.PublishImprintActivity;
import com.lianren.android.improve.explore.activities.SearchImprintActivity;
import com.lianren.android.improve.explore.adapter.HomeEventAdapter;
import com.lianren.android.improve.explore.adapter.TagsAdapter;
import com.lianren.android.improve.explore.presenter.ExploreContract;
import com.lianren.android.improve.explore.presenter.ExplorePresenter;
import com.lianren.android.improve.home.activities.NoticeNoteActivity;
import com.lianren.android.improve.user.activities.ImprintsDetailActivity;
import com.lianren.android.improve.user.activities.UserInfoActivity;
import com.lianren.android.improve.user.adapter.ImprintsListAdapter;
import com.lianren.android.util.LocationService;
import com.lianren.android.util.PageUtil;
import com.lianren.android.widget.RecycleViewDivider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.lianren.android.improve.main.MainActivity.ACTION_REQUEST_LOCATION;

/**
 * @package: com.lianren.android.improve.explore.fragments
 * @user:xhkj
 * @date:2019/12/17
 * @description:发现
 **/
public class ExploreFragment extends BaseFragment implements ExploreContract.View, OnLoadMoreListener,
        OnRefreshListener, EasyPermissions.PermissionCallbacks {
    public static final int RC_LOCATION = 0x05;//定位权限
    @Bind(R.id.viewStatusBar)
    View mStatusBar;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Bind(R.id.img_count)
    ImageView imgCount;
    @Bind(R.id.tv_count)
    TextView tvCount;
    private HomeEventAdapter homeEventAdapter;
    private ImprintsListAdapter imprintsListAdapter;
    private ExploreContract.Presenter mPresenter;
    private RecyclerView recyclerEvent, recyclerTags;
    private int page = 1;
    private TagsAdapter tagsAdapter;
    private String longitude;
    private String latitude;
    private Location location;
    private View llEvent;
    private View llAllEvent;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_explore;
    }

    public static ExploreFragment newInstance() {
        Bundle args = new Bundle();
        ExploreFragment fragment = new ExploreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected LocalBroadcastManager mManager;
    private BroadcastReceiver mReceiver;

    @Override
    protected void initData() {
        super.initData();
        registerLocalReceiver();
        EventBus.getDefault().register(this);
        mPresenter = new ExplorePresenter(this);
        requestLocation();
        mPresenter.getNoteRecommend((int) AccountHelper.getUserId(), page);//推荐印记
        mPresenter.getNoteTags();
        mPresenter.getNoticeNoteCount();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageWrap event) {
        if (event.code == Constants.REFRESH_NOTE_COUNT) {
            mPresenter.getNoticeNoteCount();
        }
    }


    /**
     * register localReceiver
     */
    private void registerLocalReceiver() {
        if (mManager == null)
            mManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REQUEST_LOCATION);
        if (mReceiver == null)
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_REQUEST_LOCATION.equals(action)) {
                        requestLocation();
                    }
                }
            };
        mManager.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        locationService = ((LRApplication) getActivity().getApplication()).locationService;
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        if (BaseActivity.hasSetStatusBarColor) {
            mStatusBar.setBackgroundColor(getResources().getColor(R.color.status_bar_color));
        }
        homeEventAdapter = new HomeEventAdapter();
        imprintsListAdapter = new ImprintsListAdapter();
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        imprintsListAdapter.bindToRecyclerView(recyclerview);
        imprintsListAdapter.addHeaderView(getHeaderView());
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        imprintsListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ImprintsBean item = imprintsListAdapter.getItem(position);
                if (view.getId() == R.id.ll_pick) {//点赞
                    mPresenter.notiPick(item, position);
                } else if (view.getId() == R.id.page_number_point) {
                    ImageGalleryImprintActivity.show(mContext, item);
                } else if (view.getId() == R.id.tv_content) {
                    ImprintsDetailActivity.show(mContext, item.user.id, item.id, "");
                } else if (view.getId() == R.id.ll_user) {
                    UserInfoActivity.show(mContext, item.user.id, item.user.uuid);
                } else if (view.getId() == R.id.ll_comment) {
                    ImprintsDetailActivity.show(mContext, item.user.id, item.id, "", true);
                }
            }
        });
        imprintsListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImprintsBean item = imprintsListAdapter.getItem(position);
                ImprintsDetailActivity.show(mContext, item.user.id, item.id, "");
            }
        });
    }

    private View getHeaderView() {
        View view = mInflater.inflate(R.layout.adapter_header_explore, null, false);
        recyclerEvent = view.findViewById(R.id.recycler_event);
        llEvent = view.findViewById(R.id.fragment_container);
        recyclerTags = view.findViewById(R.id.recycler_tags);
        llAllEvent = view.findViewById(R.id.ll_all_event);
        view.findViewById(R.id.ll_all_event)
                .setOnClickListener(new View.OnClickListener() {//全部活动
                    @Override
                    public void onClick(View v) {
                        EventActivity.show(mContext, 0);
                    }
                });
        recyclerEvent.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        homeEventAdapter.bindToRecyclerView(recyclerEvent);
        homeEventAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EventBean item = homeEventAdapter.getItem(position);
                EventDetailActivity.show(mContext, item.id);
            }
        });
        tagsAdapter = new TagsAdapter();
        recyclerTags.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        recyclerTags.setLayoutManager(new LinearLayoutManager(mContext));
        tagsAdapter.bindToRecyclerView(recyclerTags);
        tagsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String item = tagsAdapter.getItem(position);
                List<String> tag = new ArrayList<>();
                tag.add(item);
                SearchImprintActivity.show(mContext, tag);
            }
        });
        return view;
    }

    @Override
    public void showRecommendEvent(List<EventBean> mList) {
        homeEventAdapter.setNewData(mList);
        llEvent.setVisibility(mList != null && mList.size() > 0 ? View.VISIBLE : View.GONE);
        llAllEvent.setVisibility(mList != null && mList.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showImprintsList(List<ImprintsBean> mList) {
        PageUtil.getSingleton().showPage(page, refreshLayout, imprintsListAdapter, mList);
    }

    @Override
    public void setPresenter(ExploreContract.Presenter presenter) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        mPresenter.getNoteRecommend((int) AccountHelper.getUserId(), page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mPresenter.getNoticeNoteCount();
        mPresenter.getNoteRecommend((int) AccountHelper.getUserId(), page);
        requestLocation();
    }

    @Override
    public void showNoteTags(List<String> mlist) {
        tagsAdapter.setNewData(mlist);
    }

    @Override
    public void showPickResult(PickStatusBean bean, ImprintsBean imprintsBean, int position) {
        ImprintsBean item = imprintsListAdapter.getItem(position);
        if (item != null && imprintsBean.equals(item)) {
            imprintsListAdapter.notifyItem(bean, position);
        }
    }

    @Override
    public void showNoticeNoteCount(int count) {
        if (count == 0) {
            imgCount.setVisibility(View.VISIBLE);
            tvCount.setVisibility(View.GONE);
            tvCount.setText("");
        } else {
            imgCount.setVisibility(View.GONE);
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText(count + "");
        }
    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @SuppressLint("InlinedApi")
    @AfterPermissionGranted(RC_LOCATION)
    public void requestLocation() {
        if (EasyPermissions.hasPermissions(mContext, permissions)) {
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
        mPresenter.getRecommendEvent(longitude, latitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @OnClick({R.id.img_write_print, R.id.shadow_notice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_write_print://发布印记
                PublishImprintActivity.show(mContext);
                break;
            case R.id.shadow_notice:
                NoticeNoteActivity.show(mContext);
                break;
        }
    }

    private LocationService locationService;

    @Override
    public void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
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
                mPresenter.getRecommendEvent(location.getLongitude() + "", location.getLatitude() + "");
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }
//
//        /**
//         * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
//         * @param locType 当前定位类型
//         * @param diagnosticType 诊断类型（1~9）
//         * @param diagnosticMessage 具体的诊断信息释义
//         */
//        @Override
//        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
//            super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
//            StringBuffer sb = new StringBuffer(256);
//            sb.append("诊断结果: ");
//            if (locType == BDLocation.TypeOffLineLocationFail) {
//                if (diagnosticType == 3) {
//                    AppContext.showToast("定位失败，请您检查您的网络状态");
//                }
//            } else if (locType == BDLocation.TypeCriteriaException) {
//                if (diagnosticType == 4) {
//                    AppContext.showToast("定位失败，无法获取任何有效定位依据");
//                } else if (diagnosticType == 5) {
//                    AppContext.showToast("定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位");
//                } else if (diagnosticType == 6) {
//                    AppContext.showToast("定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试");
//                } else if (diagnosticType == 7) {
//                    AppContext.showToast("定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试");
//                } else if (diagnosticType == 9) {
//                    AppContext.showToast("定位失败，无法获取任何有效定位依据");
//                }
//            } else if (locType == BDLocation.TypeServerError) {
//                if (diagnosticType == 8) {
//                    AppContext.showToast("定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限");
//                }
//            }
//        }
    };
}
