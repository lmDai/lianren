package com.lianren.android.improve.bean;

import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class MatchingBean {
    public List<UserBean> user;

    public static class UserBean {
        /**
         * present_id : 133
         * remote_id : 175
         * contact_at : null
         * view_at : null
         * remote : {"id":175,"uuid":"46262137","avatar_url":"http://user.asset.qileguai.com/user_60791568727799020.png","nickname":"笑得好开心","birthday":"1999-09-17","sex_zh":"女","about":"","note":[{"tag":"我是","content":""},{"tag":"技能.喜欢.理想","content":""},{"tag":"情感.事业.家庭","content":""}],"status":"using"}
         */

        public int present_id;
        public int remote_id;
        public String contact_at;
        public String view_at;
        public RemoteBean remote;

        public static class RemoteBean {
            /**
             * id : 175
             * uuid : 46262137
             * avatar_url : http://user.asset.qileguai.com/user_60791568727799020.png
             * nickname : 笑得好开心
             * birthday : 1999-09-17
             * sex_zh : 女
             * about :
             * note : [{"tag":"我是","content":""},{"tag":"技能.喜欢.理想","content":""},{"tag":"情感.事业.家庭","content":""}]
             * status : using
             */

            public int id;
            public String uuid;
            public String avatar_url;
            public String nickname;
            public String birthday;
            public String sex_zh;
            public String about;
            public String status;
            public List<NoteBean> note;

            public static class NoteBean {
                /**
                 * tag : 我是
                 * content :
                 */
                public String tag;
                public String content;

            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj instanceof UserBean) {
                return ((UserBean) obj).remote_id == remote_id;
            }
            return super.equals(obj);
        }
    }
}
