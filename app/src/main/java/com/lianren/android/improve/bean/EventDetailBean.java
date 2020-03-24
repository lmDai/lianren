package com.lianren.android.improve.bean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class EventDetailBean {

    public String id;
    public String name;
    public String type;
    public String address;
    public String image;
    public String province;
    public String city;
    public String total_cost;
    public String time;
    public String apply_e_time;
    public String intro;
    public String describe;
    public String telephone;
    public int status;
    public int pair_status;
    public String share_uri;
    public ShareBean share;
    public String org_name;
    public String org_logo;
    public String org_describe;
    public int collect_status;
    public List<?> comments;
    public List<UsersBean> users;
    public String pair_uri;


    public static class ShareBean {
        /**
         * uri : http://share.qileguai.com/html/activity/60
         * title : 来来来-链人线下活动.行业/IT互联网
         * describe : 11月1日 急急急
         * image : http://merchant.asset.qileguai.com/merchant_33761572446091456.jpg
         */
        public String uri;
        public String title;
        public String describe;
        public String image;
    }

    public static class UsersBean {
        /**
         * id : 205
         * uuid : 28294393
         * nickname : 你好我好大家好
         * avatar_url : http://user.asset.qileguai.com/user_90581572426167220.png
         */
        public String id;
        public String uuid;
        public String nickname;
        public String avatar_url;
    }
}
