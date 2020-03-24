package com.lianren.android.improve.bean;

import java.io.Serializable;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2020/1/3
 * @description:
 **/
public class Version implements Serializable {
    public String version;
    public String intro;
    public String url;
    public int update_type;// 0不需要 1建议升级 2强制升级
}
