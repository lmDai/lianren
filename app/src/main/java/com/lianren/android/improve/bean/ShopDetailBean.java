package com.lianren.android.improve.bean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class ShopDetailBean {

    public int id;
    public String name;
    public String province;
    public String city;
    public String address;
    public String image;
    public String intro;
    public String describe;
    public String telephone;
    public int status;
    public String org_name;
    public String org_logo;
    public String org_describe;
    public String total_cost;
    public String time;
    public String s_time;
    public String e_time;
    public List<CommentBean> comments;


    public static class CommentBean {
        public String nickname;
        public String avatar_name;
        public String content;
    }
}
