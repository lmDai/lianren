package com.lianren.android.improve.bean;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/27
 * @description:
 **/
public class BreakBean {

    /**
     * title : 匹配失败
     * content : 你的头像非真人或非正面照，无法进行匹配
     * bottom : {"name":"上传真人头像","type":1,"link":{"android":"//userinfo","ios":"//userinfo","h5":"http://"}}
     */

    public String title;
    public String content;
    public BottomBean bottom;


    public static class BottomBean {
        /**
         * name : 上传真人头像
         * type : 1
         * link : {"android":"//userinfo","ios":"//userinfo","h5":"http://"}
         */

        public String name;
        public int type;
        public LinkBean link;

        public static class LinkBean {

            public String android;
            public String ios;
            public String h5;

        }
    }
}
