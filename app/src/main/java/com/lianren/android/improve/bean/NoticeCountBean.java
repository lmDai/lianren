package com.lianren.android.improve.bean;


import android.content.Context;

import net.oschina.common.helper.SharedPreferencesHelper;

import java.io.Serializable;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class NoticeCountBean implements Serializable {

    /**
     * pair : {"count":0,"msg":"0位用户申请匹配"}
     * system : {"count":9,"msg":""}
     */

    public PairBean pair;
    public SystemBean system;
    public DatingBean dating;
    public OrderBean order;
    public MessageTotalBean message;

    public static class MessageTotalBean implements Serializable {
        public int count;//未读消息数量
    }


    public static class DatingBean implements Serializable {
        public int count;//未读消息数量
        public int total_count;//消息总数
    }

    public static class OrderBean implements Serializable {
        public int count;
    }

    public NoticeCountBean set(NoticeCountBean bean) {
        this.pair = bean.pair;
        this.system = bean.system;
        this.order = bean.order;
        this.dating = bean.dating;
        this.message = bean.message;
        return this;
    }

    public static class PairBean implements Serializable {
        /**
         * count : 0
         * msg : 0位用户申请匹配
         */

        public int count;
        public String msg;
    }

    public static class SystemBean implements Serializable {
        /**
         * count : 9
         * msg :
         */
        public int count;
        public String msg;
    }

    public NoticeCountBean save(Context context) {
        SharedPreferencesHelper.save(context, this);
        return this;
    }

    public static NoticeCountBean getInstance(Context context) {
        NoticeCountBean bean = SharedPreferencesHelper.load(context, NoticeCountBean.class);
        if (bean == null)
            bean = new NoticeCountBean();
        return bean;
    }
}
