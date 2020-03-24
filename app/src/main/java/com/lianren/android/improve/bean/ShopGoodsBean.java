package com.lianren.android.improve.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/24
 * @description:
 **/
public class ShopGoodsBean implements Serializable {
    public String id;
    public String name;
    public String province;
    public String city;
    public String address;
    public String image;
    public String describe;
    public String time;
    public String s_time;
    public String e_time;
    public List<GoodsBean> goods;

    public class GoodsBean implements Serializable {
        public int id;
        public String name;
        public String describe;
        public double price;
        public double discount_price;
        public String discount;
        public int number;
        public int limit_num;
        public List<String> scope;
        public boolean checked;
        public int purchaseNum = 1;//购买数量
        public String time;
    }
}

