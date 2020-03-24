package com.lianren.android.improve.bean;

import com.contrarywind.interfaces.IPickerViewData;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/25
 * @description:
 **/
@Entity(nameInDb = "BASIC")
public class BasicBean implements Serializable {
    private static final long serialVersionUID = -1187954447356117121L;
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "type")
    public String type;
    @Property(nameInDb = "name")
    public String name;
    @Convert(converter = ItemConvert.class, columnType = String.class)
    public List<ItemBean> item;
    @Convert(converter = RangConvert.class, columnType = String.class)
    public RangBean range;

    @Generated(hash = 1292110119)
    public BasicBean(Long id, String type, String name, List<ItemBean> item,
                     RangBean range) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.item = item;
        this.range = range;
    }

    @Generated(hash = 1947590039)
    public BasicBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemBean> getItem() {
        return this.item;
    }

    public void setItem(List<ItemBean> item) {
        this.item = item;
    }

    public RangBean getRange() {
        return this.range;
    }

    public void setRange(RangBean range) {
        this.range = range;
    }


    public static class ItemBean implements IPickerViewData, Serializable {
        private static final long serialVersionUID = -6338099918868943539L;
        public String id;
        public String name;
        public int parent_id;
        public List<ChildrenBean> children;

        public ItemBean() {
        }

        public ItemBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getPickerViewText() {
            return name;
        }

        public static class ChildrenBean implements IPickerViewData, Serializable {
            private static final long serialVersionUID = -1044218255454794090L;
            public String id;
            public String name;
            public String parent_id;

            @Override
            public String getPickerViewText() {
                return name;
            }
        }
    }

    public static class RangBean implements Serializable {
        private static final long serialVersionUID = 6298263073751061276L;
        public String start;
        public String end;
    }
}
