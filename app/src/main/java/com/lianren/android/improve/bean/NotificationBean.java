package com.lianren.android.improve.bean;

import java.io.Serializable;

public class NotificationBean implements Serializable {

    /**
     * id :
     * type : 3
     */
//当前返回数据中的type 1=私信,2=邀约,3=喜欢,4=活动开启,5=活动推荐
    public String id;
    public int type;
    public int user_id;
    public String nickname;
    public String uuid;

}
