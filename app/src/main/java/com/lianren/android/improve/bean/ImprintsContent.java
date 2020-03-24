package com.lianren.android.improve.bean;

import java.io.Serializable;
import java.util.List;

public class ImprintsContent implements Serializable {
    public String text;//	String 文本信息
    public String voice_url;//	String 声音链接
    public List<String> image_url;//	String[] 图片地址
}
