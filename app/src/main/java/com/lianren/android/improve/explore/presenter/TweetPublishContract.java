package com.lianren.android.improve.explore.presenter;

import android.content.Context;
import android.os.Bundle;

import com.lianren.android.util.pickimage.TweetSelectImageAdapter;

import java.util.List;

/**
 * @package: com.lianren.android.improve.explore.presenter
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public interface TweetPublishContract {
    interface Operator {

        void publish();

        void onBack();

        void loadData();

        void onSaveInstanceState(Bundle outState);

        void onRestoreInstanceState(Bundle savedInstanceState);

        void setDataView(View view, String defaultContent,
                         List<String> paths, List<String> tag,int status,String note_id);
    }

    interface View {
        Context getContext();

        String getContent();

        int getStatus();

        List<String> getImprintTag();

        void setContent(String content);

        List<TweetSelectImageAdapter.Model> getImages();

        void setImages(String[] paths);

        void finish();

        Operator getOperator();

        boolean onBackPressed();

        void setStatus(int status);

        void setTag(List<String> tag);

        String getNoteId();

        void setNoteId(String note_id);
    }
}
