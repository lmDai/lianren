package com.lianren.android.improve.bean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2020/1/3
 * @description:
 **/
public class DatingDetailBean {


    public int type;
    public int present_order_id;
    public int remote_order_id;
    public String created_at;
    public int pay_time_limit;
    public int pay_time;
    public int present_status;
    public int remote_status;
    public SubjectBean subject;
    public PresentUserBean present_user;
    public RemoteUserBean remote_user;
    public List<OperatorsBean> operators;

    public static class SubjectBean {


        public int id;
        public String city;
        public String name;
        public String image;
        public String address;
        public String pay_exp;
        public String province;
        public String refund_exp;
        public String time;
        public String good_time;
        public String good_name;
        public double price;
        public double discount_price;
        public String discount;
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
    public static class OperatorsBean {
        public String type;
        public String name;

    }
}
