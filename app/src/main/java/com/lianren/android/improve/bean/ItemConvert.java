package com.lianren.android.improve.bean;

import com.alibaba.fastjson.JSON;
import com.lianren.android.improve.bean.BasicBean;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

public class ItemConvert implements PropertyConverter<List<BasicBean.ItemBean>, String> {
    @Override
    public List<BasicBean.ItemBean> convertToEntityProperty(String databaseValue) {
        return JSON.parseArray(databaseValue, BasicBean.ItemBean.class);
    }

    @Override
    public String convertToDatabaseValue(List<BasicBean.ItemBean> entityProperty) {
        return JSON.toJSONString(entityProperty);
    }
}
