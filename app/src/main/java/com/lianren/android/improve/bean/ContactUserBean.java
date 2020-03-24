package com.lianren.android.improve.bean;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/31
 * @description:
 **/
public class ContactUserBean {
    public int id;
    public String uuid;
    public String avatar_url;
    public String nickname;
    public String sex_zh;
    public String created_at;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof ContactUserBean) {
            return ((ContactUserBean) obj).id == id;
        }
        return super.equals(obj);
    }
}
