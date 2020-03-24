package com.lianren.android.improve.explore.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.lianren.android.BuildConfig;
import com.lianren.android.R;
import com.lianren.android.improve.base.activities.BaseBackActivity;
import com.lianren.android.improve.explore.fragments.PublishImprintFragment;
import com.lianren.android.improve.explore.presenter.TweetPublishContract;
import com.lianren.android.improve.explore.service.TweetPublishService;

import net.oschina.common.utils.CollectionUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.lianren.android.improve.explore.activities
 * @user:xhkj
 * @date:2019/12/20
 * @description:发布印记
 **/
public class PublishImprintActivity extends BaseBackActivity {
    private TweetPublishContract.View mView;

    @Override
    protected int getContentView() {
        return R.layout.activity_publish_imprint;
    }

    public static void show(Context mContext) {
        mContext.startActivity(new Intent(mContext, PublishImprintActivity.class));
    }

    public static void show(Context mContext, List<String> image, String content, List<String> tag, int status, String note_id) {
        Intent intent = new Intent(mContext, PublishImprintActivity.class);
        if (image != null) {
            intent.putExtra("image", (Serializable) image);
        }
        if (content != null) {
            intent.putExtra("content", content);
        }
        if (tag != null) {
            intent.putExtra("tag", (Serializable) tag);
        }
        intent.putExtra("status", status);
        intent.putExtra("note_id", note_id);

        mContext.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle == null) bundle = new Bundle();
        readFastShareByOther(bundle, intent);
        PublishImprintFragment fragment = new PublishImprintFragment();
        // init the args bounds
        fragment.setArguments(bundle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_tweet_publish, fragment);
        trans.commit();
        mView = fragment;
    }

    /**
     * 读取快速分享到当前界面的内容
     *
     * @param intent 需要写入源
     */
    private void readFastShareByOther(Bundle bundle, Intent intent) {
        // Check
        if (intent == null)
            return;
        String type = intent.getType();
        if (TextUtils.isEmpty(type))
            return;

        //判断当前分享的内容是文本，还是图片
        if ("text/plain".equals(type)) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            bundle.putString("defaultContent", text);
        } else if (type.startsWith("image/")) {
            ArrayList<String> uris = new ArrayList<>();
            Object obj = intent.getExtras().get(Intent.EXTRA_STREAM);
            if (obj instanceof Uri) {
                Uri uri = (Uri) obj;
                String decodePath = decodePath(uri);
                if (decodePath != null)
                    uris.add(decodePath);
            } else {
                try {
                    @SuppressWarnings("unchecked")
                    ArrayList<Uri> list = (ArrayList<Uri>) obj;
                    //大于9张图片的分享，直接只使用前9张
                    if (list != null && list.size() > 0) {
                        for (int i = 0, len = list.size(); i < len; i++) {
                            if (i > 9) {
                                break;
                            }
                            String decodePath = decodePath(list.get(i));
                            if (decodePath != null)
                                uris.add(decodePath);
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace();
                }
            }
            if (uris.size() > 0) {
                String[] paths = CollectionUtil.toArray(uris, String.class);
                bundle.putStringArray("defaultImages", paths);
            }
        }
    }

    /**
     * 通过uri当中的唯一id搜索本地相册图片，是否真的存在。然后返回真实的path路径
     *
     * @param uri rui
     * @return path
     */
    private String decodePath(Uri uri) {
        String decodePath = null;
        String uriPath = uri.toString();

        if (uriPath != null && uriPath.startsWith("content://")) {
            int id = 0;
            try {
                id = Integer.parseInt(uriPath.substring(uriPath.lastIndexOf("/") + 1, uriPath.length()));
            } catch (Exception e) {
                e.printStackTrace();
                return parseUri(uri);
            }

            Uri tempUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media.DATA};
            String selection = MediaStore.Images.Media._ID + "=?";
            String[] selectionArgs = {id + ""};

            Cursor cursor = getContentResolver().query(tempUri, projection, selection, selectionArgs, null);
            try {
                while (cursor != null && cursor.moveToNext()) {
                    String temp = cursor.getString(0);
                    File file = new File(temp);
                    if (file.exists()) {
                        decodePath = temp;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        } else {
            return uriPath;
        }
        return decodePath;
    }

    private String parseUri(Uri uri) {
        String path = uri.getPath();
        Log.e("path", "path" + path);
        if (path != null) {
            File file = new File(path.replace("/raw/", ""));
            return file.exists() ? file.getPath() : "";
        }
        return "";
    }

    @Override
    protected void initData() {
        super.initData();
        // before the fragment show
        registerPublishStateReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //暂不处理已在当前界面下的分享
    }

    @Override
    protected void onPause() {
        unRegisterPublishStateReceiver();
        super.onPause();
    }

    private void registerPublishStateReceiver() {
        if (mPublishStateReceiver != null)
            return;
        IntentFilter intentFilter = new IntentFilter(TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED);
        BroadcastReceiver receiver = new SearchReceiver();
        registerReceiver(receiver, intentFilter);
        mPublishStateReceiver = receiver;

        // start search
        TweetPublishService.startActionSearchFailed(this);
    }

    private void unRegisterPublishStateReceiver() {
        final BroadcastReceiver receiver = mPublishStateReceiver;
        mPublishStateReceiver = null;
        if (receiver != null)
            unregisterReceiver(receiver);
    }

    private BroadcastReceiver mPublishStateReceiver;

    private class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED.equals(intent.getAction())) {
                String[] ids = intent.getStringArrayExtra(TweetPublishService.EXTRA_IDS);
                if (ids == null || ids.length == 0)
                    return;
//                TweetPublishQueueActivity.show(TweetPublishActivity.this, ids);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mView != null && mView.onBackPressed()) {
            mView.getOperator().onBack();
        }
    }
}
