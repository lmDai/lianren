package com.lianren.android.improve.bean.pay;

import java.io.Serializable;

public class WXpay implements Serializable {
    public String appid;//	String 微信appid

    public String partnerid;//	String 微信商户号

    public String prepayid;//	String 预支付ID

    public String noncestr;//	String  加密串

    public String package_type;//	String 打包方式

    public String timestamp;//	String 时间戳

    public String sign;//	String 签名

}
