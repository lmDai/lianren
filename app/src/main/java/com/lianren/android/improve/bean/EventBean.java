package com.lianren.android.improve.bean;

import java.io.Serializable;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/23
 * @description:
 **/
public class EventBean implements Serializable {
    public String id;
    public String type;
    public String name;
    public String province;
    public String city;
    public String address;
    public String image;
    public String total_cost;
    public String total_num;
    public String s_time;
    public String e_time;
    public String apply_e_time;
    public String time;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof EventBean) {
            return ((EventBean) obj).id == id;
        }
        return super.equals(obj);
    }
}
