package com.lianren.android.improve.account.constants;

/**
 * @package: com.lianren.android.improve.account.constants
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public interface UserConstants {
    String HOLD_ACCOUNT = "hold_account";
    String USER_ID = "user_id";
    String USER_UUID = "user_uuid";
    /**
     * 头像
     */
    int AVATAR = 2;
    /**
     * 昵称
     */
    int NICKNAME = 3;
    /**
     * 生日
     */
    int BIRTHDAY = 4;
    /**
     * 性别 boy:男 girl:女
     */
    int SEX = 5;
    /**
     * 身高
     */
    int HEIGHT = 6;
    /**
     * 体重
     */
    int WEIGHT = 7;
    /**
     * 籍贯
     */
    int BIRTH_ID = 8;
    /**
     * 居住地id
     */
    int DOMICILE_ID = 9;
    /**
     * 学历
     */
    int EDU_CODE = 10;
    /**
     * 收入
     */
    int REVENUE_CODE = 12;
    /**
     * 学校id
     */
    int SCHOOL_ID = 13;
    /**
     * 专业id
     */
    int PROFESSION_ID = 14;
    /**
     * 行业id
     */
    int INDUSTRY_ID = 15;
    /**
     * 职业id
     */
    int OCC_ID = 16;
    /**
     * 婚姻状况
     */
    int MARITAL_STATUS = 17;
    /**
     * 子女状况
     */
    int CHILDREN_STATUS = 18;
    /**
     * 喝酒状况
     */
    int DRINK_STATUS = 19;
    /**
     * 吸烟状况
     */
    int SMOKE_STATUS = 20;
    /**
     * 星座
     */
    int CONSTELLATION = 21;
    /**
     * 用户简介
     */
    int ABOUT = 22;
    /**
     * 交友状态 using:我单身，寻找恋人 pause:暂停匹配 close:关闭资料
     */
    int STATUS = 23;

    /**
     * 年龄范围age_range
     */
    int AGE_RANGE = 2;
    /**
     * 居住范围domicile_range
     */
    int DOMICILE_RANGE = 3;
    /**
     * 收入范围revenue_range
     */
    int REVENUE_RANGE = 4;
    /**
     * 学历要求education_range
     */
    int EDU_ID = 5;
    /**
     * 身高范围height_range
     */
    int HEIGHT_RANGE = 6;
    /**
     * 婚姻状况marital_range
     */
    int MARITAL_STATUS_RANGE = 7;
    /**
     * 体重范围weight_range
     */
    int WEIGHT_RANGE = 8;
    /**
     * 子女状况child_range
     */
    int CHILDREN_STATUS_RANGE = 9;
    /**
     * 要求说明require_detail
     */
    int REQUIRE_DETAIL = 10;
    int EDUCATION_RANGE = 11;

    //1001:app 1002:活动 1003:用户 1004:印记 1005:邀约
    String FEED_APP = "1001";
    String FEED_EVENT = "1002";
    String FEED_USER = "1003";
    String FEED_IMPRINT = "1004";
    String FEED_INVITE = "1005";
}
