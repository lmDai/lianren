package com.lianren.android.improve.bean.base;

/**
 * @package: com.lianren.android.improve.bean.base
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class ResultBean<T> {
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAIL = 1;
    public static final int UNAUTHORIZED = 401;
    public static final int VIP = 501;
    public static final int REAL_NAME = 502;
    public static final int USER_INFO = 511;
    public static final int NO_HEAD = 512;

    public ErrorBean error;
    public int code;
    public T data;

    public boolean isSuccess() {
        return code == RESULT_SUCCESS && data != null;
    }

    public boolean isUnauthorizedError() {
        return code == UNAUTHORIZED;
    }

    public boolean isVIP() {
        return code == VIP;
    }

    public boolean isREAL_NAME() {
        return code == REAL_NAME;
    }

    public boolean isUSER_INFO() {
        return code == USER_INFO;
    }

    public boolean isNO_HEAD() {
        return code == NO_HEAD;
    }
}
