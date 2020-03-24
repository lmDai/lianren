package com.lianren.android.improve.bean;

import com.alibaba.fastjson.JSON;

import org.greenrobot.greendao.converter.PropertyConverter;

public class RangConvert implements PropertyConverter<BasicBean.RangBean, String> {

    @Override
    public BasicBean.RangBean convertToEntityProperty(String databaseValue) {
        return JSON.parseObject(databaseValue, BasicBean.RangBean.class);
    }

    @Override
    public String convertToDatabaseValue(BasicBean.RangBean entityProperty) {
        return JSON.toJSONString(entityProperty);
    }
}
