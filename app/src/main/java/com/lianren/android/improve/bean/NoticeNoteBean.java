package com.lianren.android.improve.bean;

import java.io.Serializable;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2020/1/17
 * @description:
 **/
public class NoticeNoteBean implements Serializable {
    public String comment_id;
    public String content;
    public String created_at;
    public String type;
    public UserBean user;

    public static class UserBean {
        public int id;
        public String uuid;
        public String avatar_url;
        public String nickname;
        public String sex;
    }

    public NoteBean note;

    public static class NoteBean {
        public String id;
        public ContentBean content;

        public static class ContentBean {
            public String text;
            public String voice_url;
            public String iamge_url;
        }
    }
}
