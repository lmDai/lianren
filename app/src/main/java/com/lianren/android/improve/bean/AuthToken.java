package com.lianren.android.improve.bean;

import java.io.Serializable;

public class AuthToken implements Serializable {
    public String Token;//	String token
    public String DurationSeconds;//	String 认证会话超时时间，单位为秒。一般为1,800s
}
