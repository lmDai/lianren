package com.lianren.android.improve.bean;

import java.io.Serializable;

/**
 * 私信
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */
public class Message implements Serializable {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_FILE = 5;

    /**
     * from_user : 133
     * to_user : 160
     * msg_time : 2020-01-02 14:36:19
     * msg_type : text
     * msg_content : fgghhh
     * id : RPP4ZG8BY67zAPlnHHrg
     */

    public String msg_time;
    public String msg_seq;
    public String msg_type;
    public String msg_content;
    public FromBean from;
    public toBean to;

    public static class FromBean {
        public int id;
        public String uuid;
        public String avatar_url;
        public String nickname;
    }

    public static class toBean {
        public int id;
        public String uuid;
        public String avatar_url;
        public String nickname;
    }
}
