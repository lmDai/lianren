package com.lianren.android.improve.bean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/30
 * @description:
 **/
public class OrderTicketBean {
    public int is_refund;
    public String explain;
    public SubjectBean subject;

    public class SubjectBean {
        public String id;
        public String name;
        public String province;
        public String city;
        public String address;
        public String image;
        public String time;
    }

    public TicketOrderDetailBean.PartyOrderUserInfo user;
    public List<Ticket> tickets;

    public class Ticket {
        public String no;
        public int status;
        public String name;
        public double price;
        public String time;
        public String qr_image;
    }
}
