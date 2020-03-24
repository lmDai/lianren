package com.lianren.android.improve.bean.pay;

import java.io.Serializable;

public class UserVipPay implements Serializable {
    public ApplePay iap;//	Object 苹果支付
    public WXpay wx;//	Object 微信
    public Alipay alipay;//	Object 支付宝
    public OrderBean order;

    public static class OrderBean {
        public String id;
        public String no;
        public int status;
    }

}
