package com.lianren.android.improve.bean;

import java.io.Serializable;

public class UserRecordMessage implements Serializable {
    public String id;//	String 留言id
    public String content;//	String  留言内容
    public String created_at;//	String 留言时间
    public String type;//	String 类型 1:留言 2:留言回复
    public UserRecordPublisher user;//	Object 用户信息(type=1:留言人信息 type=2:被回复人信息)
    public UserRecordPublisher reply_user;//	Object 回复人信息(type==2才有效)

    public String note_id;
    public String comment_id;
}
