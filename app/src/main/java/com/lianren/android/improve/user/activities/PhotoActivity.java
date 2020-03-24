package com.lianren.android.improve.user.activities;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.bean.QiNiuBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.improve.user.adapter.SelectImgsAdapter;
import com.lianren.android.improve.user.presenter.PhotoContract;
import com.lianren.android.improve.user.presenter.PhotoPresenter;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.pickimage.media.SelectImageActivity;
import com.lianren.android.util.pickimage.media.config.SelectOptions;
import com.lianren.android.widget.viewhelper.DefaultItemTouchHelpCallback;
import com.lianren.android.widget.viewhelper.DefaultItemTouchHelper;
import com.lianren.android.widget.viewhelper.SelectImgHolder;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * @package: com.lianren.android.improve.user.activities
 * @user:xhkj
 * @date:2020/1/6
 * @description:相册
 **/
public class PhotoActivity extends BackActivity implements SelectImgsAdapter.Callback, PhotoContract.View {
    public static final int TYPE_HEAD = 500;
    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.recycleView)
    RecyclerView recycleView;
    String add = "添加";
    SelectImgsAdapter adapter;
    DefaultItemTouchHelper helper;
    private List<UsersInfoBean.PhotoBean> photos = new ArrayList<>();
    private Map<String, Integer> updateMap = new HashMap<>();
    private int uid = -1;
    private static final int REQUEST_QINIU_TYPE = 1;//文件类型 1用户个人图片(相册、头像) 2商家图片 3印记图片 4其他图片(举报等非1，2，3类的图片)
    private UsersInfoBean usersInfoBean;
    private PhotoContract.Presenter mPresenter;

    @Override
    protected int getContentView() {
        return R.layout.activity_photo;
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
        mPresenter = new PhotoPresenter(this);
        getUserInfo();
    }

    private void showUserInfo(UsersInfoBean usersInfoBean) {
        this.usersInfoBean = usersInfoBean;
        photos = usersInfoBean.photo;
        UsersInfoBean.PhotoBean photo = new UsersInfoBean.PhotoBean();
        photo.id = -1;
        photo.img_uri = add;
        photo.location = SelectImgsAdapter.maxImg;
        photos.add(photos.size(), photo);
        adapter = new SelectImgsAdapter(photos, this);
        adapter.setCallback(this);
        helper = new DefaultItemTouchHelper(new DefaultItemTouchHelpCallback(onItemTouchCallbackListener));
        helper.attachToRecyclerView(recycleView);
        helper.setDragEnable(true);
        recycleView.setLayoutManager(new GridLayoutManager(this, 3));
        recycleView.addItemDecoration(new SpaceItemDecoration(3, 11, false));
        recycleView.setAdapter(adapter);
    }

    private void getUserInfo() {
        LRApi.usersInfo(AccountHelper.getUser().uuid, AccountHelper.getUser().id, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UsersInfoBean>>() {
                    }.getType();
                    ResultBean<UsersInfoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        showUserInfo(resultBean.data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            //滑动删除的时候，从数据源移除，并刷新这个Item。
            if (photos != null) {
                photos.remove(adapterPosition);
                adapter.updateData(photos);
                adapter.notifyItemRemoved(adapterPosition);
            }
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (photos == null || photos.size() == 0) {
                return false;
            }
            UsersInfoBean.PhotoBean photo = photos.get(srcPosition);
            String url = photo.img_uri;
            int location = photo.location;
            int pid = photo.id;
            if (add.equals(url)) {
                return false;
            }
            //除了+按钮不允许移动外，其他均可移动
            Collections.swap(photos, srcPosition, targetPosition);// 更换数据源中的数据Item的位置
            adapter.notifyItemMoved(srcPosition, targetPosition);// 更新UI中的Item的位置，主要是给用户看到交互效果

            updateMap.put("srcPosition", srcPosition);
            updateMap.put("targetPosition", targetPosition);
            showLoadingDialog();
            mPresenter.updatePhoto(pid, null, targetPosition + 1);
            return true;
        }

        @Override
        public void complete() {
            //如果完成之后不刷新会导致删除的时候下标错误的情况
            if (!recycleView.isComputingLayout())
                adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void startDrag(SelectImgHolder holder) {
        helper.startDrag(holder);
    }

    @Override
    public void delPicture(UsersInfoBean.PhotoBean photo, final int position) {
        String url = photo.img_uri;
        if (photos.size() - 1 == position && add.equals(url)) {
            return;
        }
        DialogHelper.getConfirmDialog(this, "提示", "确认要删除?", "确认", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoadingDialog();
                UsersInfoBean.PhotoBean delPhoto = photos.get(position);
                int delPhotoId = delPhoto.id;
                mPresenter.deletePhoto(delPhotoId);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    @Override
    public void addPicture() {
        SelectImageActivity.show(this, new SelectOptions.Builder()
                .setSelectCount(1)
                .setHasCam(true)
                .setCallback(new SelectOptions.Callback() {
                    @Override
                    public void doSelected(String[] images) {
                        String path = images[0];
                        qiniuParams(path);
                    }
                }).build());
    }

    public void qiniuParams(final String file_name) {
        showLoadingDialog();
        LRApi.qiniuUpload(file_name, 1, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<QiNiuBean>>() {
                    }.getType();

                    ResultBean<QiNiuBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        uploadImages(resultBean.data, file_name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });

    }

    private void uploadImages(final QiNiuBean qiNiuBean, String fileName) {
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(fileName, qiNiuBean.file_name, qiNiuBean.upload_token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {//上传成功
                    String domain = qiNiuBean.domain;
                    String url = domain + key;
                    int index = photos.size() - 1;
                    mPresenter.upLoadPhoto(url, index);
                } else {//上传失败

                }
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        }));
    }

    @Override
    public void itemClick(int position) {

    }

    @Override
    public void showUpLoadResult(ResultBean<PhotoBean> result) {
        dismissLoadingDialog();
        if (result.isSuccess()) {
            PhotoBean photo = result.data;
            if (photo == null) {
                return;
            }
            int local = photo.location;
            /**添加的图片需要在+之前**/
            photo.location = (local);
            UsersInfoBean.PhotoBean photoBean = new UsersInfoBean.PhotoBean();
            photoBean.img_uri = photo.img_uri;
            photoBean.id = photo.photo_id;
            photoBean.location = local;
            photos.add(local, photoBean);
            adapter.updateData(photos);

            dealSourcePhotos(photos);
        } else {
            AppContext.showToast(result.error.message);
        }
    }

    @Override
    public void showDeleteResult(ResultBean<PhotoBean> result) {
        dismissLoadingDialog();
        if (result.isSuccess()) {
            PhotoBean delPhoto = result.data;
            if (delPhoto == null) {
                return;
            }
            int delPhotoLocation = delPhoto.location;
            int delPhotoId = delPhoto.photo_id;
            UsersInfoBean.PhotoBean resourcePhoto = photos.get(delPhotoLocation);
            int resourcePhotoId = resourcePhoto.id;
            if (delPhotoId == resourcePhotoId) {
                photos.remove(delPhotoLocation);
            } else {
                for (int i = 0; i < photos.size(); i++) {
                    UsersInfoBean.PhotoBean forPhoto = photos.get(i);
                    int forPhotoId = forPhoto.id;
                    if (delPhotoId == forPhotoId) {
                        photos.remove(i);
                    }
                }
            }
            /**添加的图片需要在+之前**/
            adapter.updateData(photos);

            dealSourcePhotos(photos);
        } else {
            AppContext.showToast(result.error.message);
        }
    }

    @Override
    public void showUpdateResult(ResultBean<PhotoBean> result) {
        dismissLoadingDialog();
        if (result.isSuccess()) {
            PhotoBean photo = result.data;
            if (photo == null) {
                return;
            }
            int local = photo.location;
            String url = photo.img_uri;
            int pid1 = photo.photo_id;

            UsersInfoBean.PhotoBean userInfoPhoto = photos.get(local);
            int pid2 = userInfoPhoto.id;

            if (pid1 == pid2) {
                userInfoPhoto.location = (local);
                userInfoPhoto.img_uri = (url);
            } else {
                for (UsersInfoBean.PhotoBean tempPhoto : photos) {
                    int pid3 = tempPhoto.id;
                    if (pid1 == pid3) {
                        tempPhoto.location = (local);
                        tempPhoto.img_uri = (url);
                    }
                }
            }
            /**添加的图片需要在+之前**/
            adapter.updateData(photos);
            dealSourcePhotos(photos);
        } else {
            resetPhotoLocation();
        }
    }

    private void resetPhotoLocation() {
        //除了+按钮不允许移动外，其他均可移动

        int srcPosition = updateMap.get("targetPosition");
        int targetPosition = updateMap.get("srcPosition");

        Collections.swap(photos, srcPosition, targetPosition);// 更换数据源中的数据Item的位置
        adapter.notifyItemMoved(srcPosition, targetPosition);// 更新UI中的Item的位置，主要是给用户看到交互效果
    }

    /**
     * 增删改后处理原相册
     */
    private void dealSourcePhotos(List<UsersInfoBean.PhotoBean> photos) {
        List<UsersInfoBean.PhotoBean> temps = new ArrayList<>();
        temps.addAll(photos);
        for (int i = 0; i < temps.size(); i++) {
            int id = temps.get(i).id;
            if (id == -1) {//-1代表添加图标
                temps.remove(i);
            }
        }
    }

    @Override
    public void setPresenter(PhotoContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {
        AppContext.showToast(strId);
        dismissLoadingDialog();
    }

    /**
     * GridView间距
     **/
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public SpaceItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = (int) TDevice.dp2px(spacing);
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }

    }
}
