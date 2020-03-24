package com.lianren.android.widget.userphoto;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.bean.QiNiuBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.pickimage.media.SelectImageActivity;
import com.lianren.android.util.pickimage.media.config.SelectOptions;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * @package: com.lianren.android.widget
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class UserPhotoPreviewer extends RecyclerView implements UserPhototAdapter.Callback {
    private UserPhototAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;
    public int type;//文件类型 1用户个人图片(相册、头像) 2商家图片 3印记图片 4其他图片(举报等非1，2，3类的图片)
    private ProgressDialog mWaitDialog;

    public UserPhotoPreviewer(Context context) {
        super(context);
        init();
    }

    public UserPhotoPreviewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserPhotoPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setType(int type) {
        this.type = type;
    }


    private void init() {
        mImageAdapter = new UserPhototAdapter(this, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        this.setLayoutManager(layoutManager);
        this.setAdapter(mImageAdapter);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ItemTouchHelper.Callback callback = new UserPhotoPreviewerItemTouchCallback(mImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    //显示头像
    public void setData(List<PhotoBean> mList) {
        if (mList != null) {
            mImageAdapter.clear();
            for (PhotoBean photoBean : mList) {
                mImageAdapter.add(photoBean);
            }
            mImageAdapter.notifyDataSetChanged();
        }
    }

    public List<PhotoBean> getData() {
        return mImageAdapter.getData();
    }

    @Override
    public void onLoadMoreClick() {
        SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                .setHasCam(true)
                .setSelectCount(1)
                .setCallback(new SelectOptions.Callback() {
                    @Override
                    public void doSelected(String[] images) {
                        qiniuParams(images[0]);
                    }
                }).build());
    }

    @Override
    public RequestManager getImgLoader() {
        if (mCurImageLoader == null) {
            mCurImageLoader = Glide.with(getContext());
        }
        return mCurImageLoader;
    }

    @Override
    public void onStartDrag(ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    protected void showLoadingDialog() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogHelper.getProgressDialog(getContext(), true);
        }
        mWaitDialog.setMessage("加载中...");
        mWaitDialog.show();
    }

    protected void showLoadingDialog(String message) {
        if (mWaitDialog == null) {
            mWaitDialog = DialogHelper.getProgressDialog(getContext(), true);
        }
        mWaitDialog.setMessage(message);
        mWaitDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (mWaitDialog == null) return;
        mWaitDialog.dismiss();
    }

    public void qiniuParams(final String file_name) {
        LRApi.qiniuUpload(file_name, type, new CommonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast("图片上传失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<QiNiuBean>>() {
                    }.getType();

                    ResultBean<QiNiuBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        uploadImages(resultBean.data, file_name);
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

    private void uploadImages(final QiNiuBean qiNiuBean, String fileName) {
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(fileName, qiNiuBean.file_name, qiNiuBean.upload_token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {//上传成功
                    String domain = qiNiuBean.domain;
                    String url = domain + key;
                    int index = mImageAdapter.getItemCount();
                    upLoadPhoto(url, index);
                } else {//上传失败
                    AppContext.showToast("图片上传失败");
                }
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                showLoadingDialog("正在上传...");
                if (percent >= 1) {
                    dismissLoadingDialog();
                }
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        }));
    }

    public void upLoadPhoto(String img_uri, int location) {
        LRApi.userImageUpload(img_uri, location, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PhotoBean>>() {
                    }.getType();
                    ResultBean<PhotoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mImageAdapter.add(resultBean.data);
                        mImageAdapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
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
