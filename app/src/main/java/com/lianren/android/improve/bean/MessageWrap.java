package com.lianren.android.improve.bean;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/25
 * @description:
 **/
public class MessageWrap {
    public final int code;
    public final Object message;

    public MessageWrap(int code, Object message) {
        this.code = code;
        this.message = message;
    }

}
