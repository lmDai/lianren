package com.lianren.android.improve.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class EventTicketBean implements Serializable {
    public String id;
    public String name;
    public String type;
    public String province;
    public String city;
    public String address;
    public String image;
    public String total_cost;
    public String time;
    public String pay_exp;
    public String refund_exp;
    public List<GoodsBean> goods;

    public class GoodsBean implements Serializable {
        public int id;
        public String name;
        public String describe;
        public double price;
        public double discount_price;
        public String discount;
        public int number;
        public int leave_num;
        public int limit_num;
        public List<String> scope;
        public String time = "";
        public long s_time;
        public long e_time;
        public boolean checked;
        public int purchaseNum = 1;//购买数量
    }
}

