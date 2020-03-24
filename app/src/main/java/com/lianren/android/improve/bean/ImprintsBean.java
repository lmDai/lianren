package com.lianren.android.improve.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class ImprintsBean implements Serializable {
    public String id;
    public int picks;
    public int replys;
    public List<String> tag;
    public ContentBean content;
    public boolean expand;
    public int status;

    public static class ContentBean implements Serializable {
        public String text;
        public List<String> image_url;
    }

    public String created_at;
    public UserBean user;

    public static class UserBean implements Serializable {
        public int id;
        public String uuid;
        public String avatar_url;
        public String nickname;
        public String sex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof ImprintsBean) {
            return ((ImprintsBean) obj).id == id;
        }
        return super.equals(obj);
    }
}
