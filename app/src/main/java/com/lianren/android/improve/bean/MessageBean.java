package com.lianren.android.improve.bean;

import java.io.Serializable;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class MessageBean implements Serializable {
    public UserBean user;
    public String msg;
    public String msg_time;
    public int is_new;

    public static class UserBean implements Serializable {
        public int id;
        public String uuid;
        public String avatar_url;
        public String nickname;
    }
}
