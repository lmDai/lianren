package com.lianren.android.improve.bean;

import java.io.Serializable;

public class AuthTokenBean implements Serializable {
    public String ticket_id;//	String 认证任务id
    public AuthToken verifyToken;//	Object 验证token
}
