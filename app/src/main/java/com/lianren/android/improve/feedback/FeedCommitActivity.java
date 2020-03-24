package com.lianren.android.improve.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.EventApi;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.pickimage.TweetSelectImageAdapter;
import com.lianren.android.widget.TweetPicturesPreviewer;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * 提交反馈信息
 */
public class FeedCommitActivity extends FeedBaseActivity {
    private List<String> updateImageUrls = new ArrayList<>();
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.recycler_images)
    TweetPicturesPreviewer recyclerImages;
    @Bind(R.id.tv_type)
    TextView tvType;
    private String fb_obj_id;
    private String fb_type;
    private String contact;
    private String type;

    public static void show(Context mContenxt, String fb_obj_id, String fb_type, String contact,
                            String type) {
        Intent intent = new Intent();
        intent.putExtra("fb_obj_id", fb_obj_id);
        intent.putExtra("fb_type", fb_type);
        intent.putExtra("contact", contact);
        intent.putExtra("type", type);
        intent.setClass(mContenxt, FeedCommitActivity.class);
        mContenxt.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_feed_commit;
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
        fb_obj_id = getIntent().getStringExtra("fb_obj_id");
        fb_type = getIntent().getStringExtra("fb_type");
        contact = getIntent().getStringExtra("contact");
        type = getIntent().getStringExtra("type");
        tvType.setText("反馈类型：" + type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit, menu);
        return true;
    }

    private long mLastClickTime;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 500)
            return false;
        mLastClickTime = nowTime;
        if (item.getItemId() == R.id.menu_commit) {
            publish();
        }
        return false;
    }

    private void publish() {
        String content = editContent.getText().toString();
        final List<TweetSelectImageAdapter.Model> paths = recyclerImages.getModels();
        if (TextUtils.isEmpty(content)) {
            AppContext.showToastShort(R.string.tip_content_empty);
            return;
        }
        // 开始上传图片,并回调进度
        uploadImages(0, paths, new UploadImageCallback() {
            @Override
            public void onUploadImageDone() {
                feedCommit();
            }

            @Override
            public void onUploadImage(int index) {
                showLoadingDialog("正在提交...");
            }
        });
    }

    private void feedCommit() {
        String content = editContent.getText().toString();
        EventApi.feedBackCommit(fb_obj_id, fb_type, updateImageUrls, content, contact, new CommonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast("提交失败！请稍后再试");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        AppContext.showToast("提交成功！");
                        sendLocalReceiver();
                    } else {
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }

    interface UploadImageCallback {
        void onUploadImageDone();

        void onUploadImage(int index);
    }

    /**
     * 上传图片
     *
     * @param index    上次图片的坐标
     * @param paths    上传的路径数组
     * @param runnable 完全上传完成时回调
     */
    private void uploadImages(final int index, final List<TweetSelectImageAdapter.Model> paths, final UploadImageCallback runnable) {
        // call progress
        runnable.onUploadImage(index);
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
                    uploadImages(index + 1, paths, runnable);
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

}
