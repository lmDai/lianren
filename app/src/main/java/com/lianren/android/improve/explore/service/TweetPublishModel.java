package com.lianren.android.improve.explore.service;

import com.lianren.android.util.pickimage.TweetSelectImageAdapter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Created by JuQiu
 * on 16/7/21.
 */

@SuppressWarnings("WeakerAccess")
public class TweetPublishModel implements Serializable {
    private String id;
    private long date;
    private String content;
    private List<TweetSelectImageAdapter.Model> srcImages;
    private List<TweetSelectImageAdapter.Model> cacheImages;
    private String cacheImagesToken;
    private int cacheImagesIndex;
    private String errorString;
    private List<String> tag;
    private int status;
    private String note_id;

    public String getNote_id() {
        return note_id;
    }

    public void setNote_id(String note_id) {
        this.note_id = note_id;
    }

    public TweetPublishModel() {
        id = UUID.randomUUID().toString();
        date = System.currentTimeMillis();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public TweetPublishModel(String content, List<TweetSelectImageAdapter.Model> images) {
        this();
        this.content = content;
        this.srcImages = images;
    }

    public TweetPublishModel(String content, List<TweetSelectImageAdapter.Model> images, List<String> tag) {
        this();
        this.content = content;
        this.srcImages = images;
        this.tag = tag;
    }

    public TweetPublishModel(String content, List<TweetSelectImageAdapter.Model> images, List<String> tag,
                             int status, String note_id) {
        this();
        this.content = content;
        this.srcImages = images;
        this.tag = tag;
        this.status = status;
        this.note_id = note_id;
    }

    public String getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public List<TweetSelectImageAdapter.Model> getSrcImages() {
        return srcImages;
    }

    public List<TweetSelectImageAdapter.Model> getCacheImages() {
        return cacheImages;
    }

    public String getCacheImagesToken() {
        return cacheImagesToken;
    }

    public int getCacheImagesIndex() {
        return cacheImagesIndex;
    }

    public void setCacheImages(List<TweetSelectImageAdapter.Model> cacheImages) {
        this.cacheImages = cacheImages;
        this.srcImages = null;
    }

    public void setCacheImagesInfo(int cacheImagesIndex, String cacheImagesToken) {
        this.cacheImagesToken = cacheImagesToken;
        this.cacheImagesIndex = cacheImagesIndex;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}
