package com.lianren.android.improve.bean;

import java.io.Serializable;
import java.util.List;

public class ImprintsDetailBean implements Serializable {
    public String id;//	int 印记id
    public List<TagBean> tag;
    public ImprintsContent content;//	Object 印记详细内容
    public int status;//	int 是否公开 0否 1是
    public String created_at;//	String 创建时间
    public int pick_num;//	int 点赞数
    public int pick_status;//	int 点赞状态
    public UserRecordPublisher user;//	Object 发布人
    public List<UserRecordMessage> comments;//	Object[] 留言
    public List<UserRecordPublisher> pick_users;//点赞用户

    public static class TagBean implements  Serializable{
        public String tag;
        public boolean isTop;
    }
}
