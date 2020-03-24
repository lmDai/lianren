package com.lianren.android.improve.explore.service;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.PicturesCompressor;
import com.lianren.android.util.pickimage.TweetSelectImageAdapter;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import net.oschina.common.utils.BitmapUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/7/21.
 * 动弹发布执行者
 */
@SuppressWarnings("unused")
class TweetPublishOperator implements Runnable, Contract.IOperator {
    private final int serviceStartId;
    private final int notificationId;
    private Contract.IService service;
    private TweetPublishModel model;
    private List<String> updateImageUrls = new ArrayList<>();

    interface UploadImageCallback {
        void onUploadImageDone();

        void onUploadImage(int index, String token);
    }

    TweetPublishOperator(TweetPublishModel model, Contract.IService service, int startId) {
        this.model = model;
        this.notificationId = model.getId().hashCode();
        this.serviceStartId = startId;
        this.service = service;
    }

    /**
     * 执行动弹发布操作
     */
    @Override
    public void run() {
        // call to service
        this.service.start(model.getId(), this);
        // notify
        notifyMsg(R.string.tweet_publishing);
        // doing
        final TweetPublishModel model = this.model;
        if (model.getSrcImages() == null && model.getCacheImages() == null) {
            // 当没有图片的时候,直接进行发布动弹
            publish();
        } else {
            if (model.getCacheImages() == null) {
                notifyMsg(R.string.tweet_image_wait);
                final String cacheDir = service.getCachePath(model.getId());
                model.setCacheImages(saveImageToCache(cacheDir, model.getSrcImages()));
                service.updateModelCache(model.getId(), model);
                if (model.getCacheImages() == null) {
                    notifyMsg(R.string.tweet_image_wait_failed);
                    AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_FAILED));
                    publish();
                    return;
                }
            }
            // 开始上传图片,并回调进度
            uploadImages(model.getCacheImagesIndex(), model.getCacheImagesToken(), model.getCacheImages(),
                    new UploadImageCallback() {
                        @Override
                        public void onUploadImageDone() {
                            publish();
                        }

                        @Override
                        public void onUploadImage(int index, String token) {
                            model.setCacheImagesInfo(index, token);
                            // update to cache file
                            service.updateModelCache(model.getId(), model);
                        }
                    });
        }
    }

    /**
     * 上传图片
     *
     * @param index    上次图片的坐标
     * @param token    上传Token
     * @param paths    上传的路径数组
     * @param runnable 完全上传完成时回调
     */
    private void uploadImages(final int index, final String token, final List<TweetSelectImageAdapter.Model> paths, final UploadImageCallback runnable) {
        // call progress
        runnable.onUploadImage(index, token);
        // checkShare done
        if (index < 0 || index >= paths.size()) {
            runnable.onUploadImageDone();
            return;
        }
        final String path = paths.get(index).path;
        String key = paths.get(index).qiNiuBean.file_name;
        String uploadToken = paths.get(index).qiNiuBean.upload_token;
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(path, key, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {//上传成功
                    String domain = paths.get(index).qiNiuBean.domain;
                    String url = domain + key;
                    updateImageUrls.add(url);
                    uploadImages(index + 1, token, paths, runnable);
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
    public void stop() {
        final Contract.IService service = this.service;
        if (service != null) {
            this.service = null;
            service.stop(model.getId(), serviceStartId);
        }
    }

    /**
     * 发布动弹
     */
    private void publish() {
        LRApi.userNoteAdd(model.getNote_id(), model.getStatus(), model.getTag(), model.getContent(), updateImageUrls, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String error = "";
                String response = responseString == null ? "" : responseString;
                if (throwable != null) {
                    throwable.printStackTrace();
                    error = throwable.getMessage();
                    if (error.contains("UnknownHostException")
                            || error.contains("Read error: ssl")
                            || error.contains("Connection timed out")) {
                        saveError("Publish", "network error");
                    } else {
                        saveError("Publish", response + " " + error);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        setSuccess();
                        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                    } else {
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }

    private void notifyMsg(int resId, Object... values) {
        notifyMsg(false, resId, values);
    }

    private void notifyMsg(boolean done, int resId, Object... values) {
        Contract.IService service = this.service;
        if (service != null) {
            service.notifyMsg(notificationId, model.getId(), done, done, resId, values);
        }
    }

    private void setSuccess() {
        AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_SUCCESS));
        notifyMsg(R.string.tweet_publish_success);
        try {
            Thread.sleep(1600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Contract.IService service = this.service;
        if (service != null) {
            service.updateModelCache(model.getId(), null);
            service.notifyCancel(notificationId);
        }
        stop();
    }

    private void setError(int resId, Object... values) {
        AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_FAILED));
        notifyMsg(true, resId, values);
        stop();
    }

    // Max upload 860KB/3M
    private static final long MAX_UPLOAD_LENGTH = 3072 * 1024;

    /**
     * 保存文件到缓存中
     *
     * @param cacheDir 缓存文件夹
     * @param paths    原始路径
     * @return 转存后的路径
     */
    private static List<TweetSelectImageAdapter.Model> saveImageToCache(String cacheDir, List<TweetSelectImageAdapter.Model> paths) {
        List<TweetSelectImageAdapter.Model> ret = new ArrayList<>();
        BitmapFactory.Options options = BitmapUtil.createOptions();
        for (final TweetSelectImageAdapter.Model path : paths) {
            String ext = null;
            try {
                int lastDotIndex = path.path.lastIndexOf(".");
                if (lastDotIndex != -1)
                    ext = path.path.substring(lastDotIndex + 1).toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(ext)) {
                ext = "jpg";
            }

            try {
                String tempFile = String.format("%s/IMG_%s.%s", cacheDir, SystemClock.currentThreadTimeMillis(), ext);
                if (PicturesCompressor.compressImage(path.path, tempFile, MAX_UPLOAD_LENGTH,
                        80, 1280, 1280 * 16, null, options, true)) {
                    TweetPublishService.log("OPERATOR doImage:" + tempFile + " " + new File(tempFile).length());
                    // verify the picture ext.
                    tempFile = PicturesCompressor.verifyPictureExt(tempFile);
                    ret.add(new TweetSelectImageAdapter.Model(tempFile, path.qiNiuBean));
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TweetPublishService.log("OPERATOR compressImage error:" + path);
        }
        if (ret.size() > 0) {
            return ret;
        }
        return null;
    }

    private void saveError(String cmd, String log) {
        AppContext.getInstance().sendBroadcast(new Intent(TweetPublishService.ACTION_FAILED));
        model.setErrorString(String.format("%s | %s", cmd, log));
        service.updateModelCache(model.getId(), model);
    }
}
