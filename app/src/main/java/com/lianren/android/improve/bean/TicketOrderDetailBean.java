package com.lianren.android.improve.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class TicketOrderDetailBean implements Serializable {
    public PartyOrderInfo order;//订单信息
    public PartyBasicInfo subject;//(活动/邀约)信息
    public List<PartyOrderTicketInfo> good;//订单商品信息
    public PartyOrderUserInfo user;//下单用户信息
    public List<PreferInfo> prefer;

    public class PartyOrderInfo implements Serializable{
        public String id;
        public String no;
        public String type;
        public int status;
        public String created_at;
        public int pay_time_limit;
        public double amount;
        public double actual_amount;
    }

    public class PartyBasicInfo implements Serializable{
        public int id;
        public String name;
        public String province;
        public String city;
        public String address;
        public String image;
        public String time;
        public String pay_exp;
        public String refund_exp;
    }

    public class PartyOrderTicketInfo implements Serializable{
        public String id;
        public String name;
        public double price;
        public int number;
    }

    public class PartyOrderUserInfo implements Serializable{
        public String nickname;
        public String telephone;
    }

    public class PreferInfo implements Serializable{
        public String name;
        public String info;
    }
}
