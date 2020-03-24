package com.lianren.android.improve.bean;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class ItemBaseBean {
    public String title;
    public String content;
    public boolean isLocked;
    public boolean isHead;

    public ItemBaseBean(String title, String content, boolean isLocked, boolean isHead) {
        this.title = title;
        this.content = content;
        this.isLocked = isLocked;
        this.isHead = isHead;
    }

    public ItemBaseBean(String title, String content, boolean isLocked) {
        this.title = title;
        this.content = content;
        this.isLocked = isLocked;
    }

    public ItemBaseBean(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
