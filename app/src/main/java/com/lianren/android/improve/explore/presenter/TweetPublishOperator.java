package com.lianren.android.improve.explore.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import com.lianren.android.AppContext;
import com.lianren.android.R;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.activities.NewLoginActivity;
import com.lianren.android.improve.explore.fragments.PublishImprintFragment;
import com.lianren.android.improve.explore.service.TweetNotificationManager;
import com.lianren.android.improve.explore.service.TweetPublishService;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.TDevice;
import com.lianren.android.util.pickimage.TweetSelectImageAdapter;

import net.oschina.common.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class TweetPublishOperator implements TweetPublishContract.Operator {
    private final static String SHARE_FILE_NAME = PublishImprintFragment.class.getName();
    private final static String SHARE_VALUES_CONTENT = "content";//内容
    private final static String SHARE_VALUES_IMAGES = "images";//图片
    private final static String SHARE_VALUES_TAG = "tag";//主题
    private final static String NOTE_ID = "note_id";//印记id
    private final static String STATUS = "status";//印记公开私密
    private final static String HISTORY = "history";


    private final static String DEFAULT_PRE = "default";
    private TweetPublishContract.View mView;
    private String mDefaultContent;
    private String[] mDefaultImages;
    private List<String> tag;
    private int status;
    private List<String> paths;
    private String note_id;

    @Override
    public void setDataView(TweetPublishContract.View view, String defaultContent,
                            List<String> paths, List<String> tag, int status, String note_id) {
        mView = view;
        if (paths != null) {
            String[] strings = new String[paths.size()];
            mDefaultImages = paths.toArray(strings);
        }
        this.paths = paths;
        mDefaultContent = defaultContent;
        this.tag = tag;
        this.status = status;
        this.note_id = note_id;
    }

    @Override
    public void publish() {
        final Context context = mView.getContext();
        if (context == null)
            return;

        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AccountHelper.isLogin()) {
            NewLoginActivity.show(context);
            return;
        }

        String content = mView.getContent();
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
            AppContext.showToastShort(R.string.tip_content_empty);
            return;
        }

        if (content.length() > PublishImprintFragment.MAX_TEXT_LENGTH) {
            AppContext.showToastShort(R.string.tip_content_too_long);
            return;
        }

        TweetNotificationManager.setup(context);

        final List<TweetSelectImageAdapter.Model> paths = mView.getImages();
        final List<String> tag = mView.getImprintTag();
        final int status = mView.getStatus();
        final String note_id = mView.getNoteId();
        TweetPublishService.startActionPublish(context, content, paths, tag, status, note_id);

        AppContext.showToastShort(R.string.tweet_publishing);

        clearAndFinish(context);
    }

    @Override
    public void onBack() {
        if (saveData()) {
            showDialog();
        } else {
            mView.finish();
        }

    }

    private void showDialog() {
        DialogHelper.getConfirmDialog(mView.getContext(), "", "保存此次编辑？", "保存", "不保存",
                true, new DialogInterface.OnClickListener
                        () {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveXmlData();
                        mView.finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clear(mView.getContext());
                    }
                }).show();
    }

    private void saveXmlData() {
        if (saveData()) {
            final Context context = mView.getContext();
            final String content = mView.getContent();
            List<TweetSelectImageAdapter.Model> image = mView.getImages();
            List<String> paths = new ArrayList<>();
            if (image != null && image.size() > 0) {
                for (TweetSelectImageAdapter.Model model : image) {
                    paths.add(model.path);
                }
            }
            List<String> tags = mView.getImprintTag();
            String noteId = mView.getNoteId();
            int statu = mView.getStatus();
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARE_VALUES_CONTENT, content);
            if (tags != null && tags.size() > 0) {
                String[] strings = new String[tags.size()];
                editor.putStringSet(SHARE_VALUES_TAG, CollectionUtil.toHashSet(tags.toArray(strings)));
            }
            if (!TextUtils.isEmpty(noteId)) {
                editor.putString(NOTE_ID, noteId);
            }
            editor.putInt(STATUS, statu);
            if (paths != null && paths.size() > 0) {
                String[] path = new String[paths.size()];
                editor.putStringSet(SHARE_VALUES_IMAGES, CollectionUtil.toHashSet(paths.toArray(path)));
            }
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
    }

    private boolean isUseXmlCache() {
        return TextUtils.isEmpty(note_id) && (paths == null || paths.size() == 0) && (tag == null || tag.size() == 0) && TextUtils.isEmpty(mDefaultContent);
    }

    private boolean saveData() {
        return !TextUtils.isEmpty(mView.getContent()) || (mView.getImprintTag() != null && mView.getImprintTag().size() > 0)
                || (mView.getImages() != null && mView.getImages().size() > 0) || !TextUtils.isEmpty(mView.getNoteId());
    }

    @Override
    public void loadData() {
        if (isUseXmlCache()) {
            final Context context = mView.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);

            String content = sharedPreferences.getString(SHARE_VALUES_CONTENT, null);
            String note_id = sharedPreferences.getString(NOTE_ID, null);
            int status = sharedPreferences.getInt(STATUS, 1);
            Set<String> images = sharedPreferences.getStringSet(SHARE_VALUES_IMAGES, null);
            Set<String> tags = sharedPreferences.getStringSet(SHARE_VALUES_TAG, null);
            if (tags != null && tags.size() > 0) {
                mView.setTag(CollectionUtil.toArrayList(CollectionUtil.toArray(tags, String.class)));
            }
            if (images != null && images.size() > 0) {
                mView.setImages(CollectionUtil.toArray(images, String.class));
            }
            if (content != null) {
                mView.setContent(content);
            }
            if (!TextUtils.isEmpty(note_id)) {
                mView.setNoteId(note_id);
            }
            mView.setStatus(status);
        } else {
            if (mDefaultImages != null && mDefaultImages.length > 0)
                mView.setImages(mDefaultImages);
            if (!TextUtils.isEmpty(mDefaultContent))
                mView.setContent(mDefaultContent);
            mView.setStatus(status);
            if (tag != null && tag.size() > 0)
                mView.setTag(tag);
            if (!TextUtils.isEmpty(note_id))
                mView.setNoteId(note_id);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final String content = mView.getContent();
        final List<TweetSelectImageAdapter.Model> models = mView.getImages();
        final String[] paths = new String[models.size()];
        if (models != null && models.size() > 0) {
            for (int i = 0; i < models.size(); i++) {
                paths[i] = models.get(i).path;
            }
        }
        if (content != null)
            outState.putString(SHARE_VALUES_CONTENT, content);
        if (paths != null && paths.length > 0)
            outState.putStringArray(SHARE_VALUES_IMAGES, paths);
        // save default
        if (mDefaultContent != null) {
            outState.putString(DEFAULT_PRE + SHARE_VALUES_CONTENT, mDefaultContent);
        }
        if (mDefaultImages != null && mDefaultImages.length > 0) {
            outState.putStringArray(DEFAULT_PRE + SHARE_VALUES_IMAGES, mDefaultImages);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        String content = savedInstanceState.getString(SHARE_VALUES_CONTENT, null);
        String[] images = savedInstanceState.getStringArray(SHARE_VALUES_IMAGES);
        if (content != null) {
            mView.setContent(content);
        }
        if (images != null && images.length > 0) {
            mView.setImages(images);
        }
        // Read default
        mDefaultContent = savedInstanceState.getString(DEFAULT_PRE + SHARE_VALUES_CONTENT, null);
        mDefaultImages = savedInstanceState.getStringArray(DEFAULT_PRE + SHARE_VALUES_IMAGES);
    }


    private void clearAndFinish(Context context) {
        if (isUseXmlCache()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARE_VALUES_CONTENT, null);
            editor.putStringSet(SHARE_VALUES_TAG, null);
            editor.putString(NOTE_ID, null);
            editor.putInt(STATUS, 1);
            editor.putStringSet(SHARE_VALUES_IMAGES, null);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
        mView.finish();
    }

    private void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARE_VALUES_CONTENT, null);
        editor.putStringSet(SHARE_VALUES_TAG, null);
        editor.putString(NOTE_ID, null);
        editor.putInt(STATUS, 1);
        editor.putStringSet(SHARE_VALUES_IMAGES, null);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        mView.finish();
    }
}
