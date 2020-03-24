package com.lianren.android.improve.bean;

import java.io.Serializable;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class PairListBean implements Serializable {
    public String apply_id;
    public String apply_time;
    public int status;
    public PairUserBean user;

    public static class PairUserBean implements Serializable{
        public String id;
        public String uuid;
        public String avatar_url;
        public String nickname;
        public String birthday;
        public String sex_zh;
        public String about;
        public String domicile_name;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof PairListBean) {
            return ((PairListBean) obj).apply_id == apply_id;
        }
        return super.equals(obj);
    }
}
