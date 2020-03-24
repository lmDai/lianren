package com.lianren.android.improve.bean;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/26
 * @description:
 **/
public class ProfessionBean extends SectionEntity<BasicBean.ItemBean.ChildrenBean> {
    private boolean isMore;
    public String sectionName;

    public ProfessionBean(boolean isHeader, String header, boolean isMroe) {
        super(isHeader, header);
        this.isMore = isMroe;
    }

    public ProfessionBean(String sectionName, BasicBean.ItemBean.ChildrenBean t) {
        super(t);
        this.sectionName = sectionName;
    }


    public ProfessionBean(BasicBean.ItemBean.ChildrenBean t) {
        super(t);
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean mroe) {
        isMore = mroe;
    }
}
