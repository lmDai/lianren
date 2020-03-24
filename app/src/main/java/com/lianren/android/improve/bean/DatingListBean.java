package com.lianren.android.improve.bean;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2020/1/6
 * @description:
 **/
public class DatingListBean {
    public int present_status;
    public int remote_status;
    public int type;
    public String created_at;
    public int id;

    public SubjectBean subject;
    public PresentUserBean present_user;
    public RemoteUserBean remote_user;

    public static class SubjectBean {


        public int id;
        public String city;
        public String name;
        public String province;
        public String image;
        public String address;
        public String time;
    }

    public static class PresentUserBean {
        public int id;
        public String uuid;
        public String nickname;
        public String avatar_url;
    }

    public static class RemoteUserBean {
        /**
         * id : 175
         * uuid : 46262137
         * nickname : 笑得好开心
         * avatar_url : http://user.asset.qileguai.com/user_60791568727799020.png
         */

        public int id;
        public String uuid;
        public String nickname;
        public String avatar_url;

    }
}
