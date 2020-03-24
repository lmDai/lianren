package com.lianren.android.api.remote;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.improve.account.AccountHelper;
import com.lianren.android.improve.account.constants.UserConstants;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.bean.TicketOrderBean;
import com.lianren.android.util.TDevice;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

import static com.lianren.android.api.ApiHttpClient.post;

/**
 * @package: com.lianren.android.api.remote
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class LRApi {
    private static RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.setUseJsonStreamer(true);
        if (AccountHelper.isLogin())
            params.put("user_id", AccountHelper.getUserId());
        params.put("identity_type", "phone");
        params.put("platform", "android");
        params.put("phone_mode", TDevice.getSystemModel());
        params.put("system_version", TDevice.getSystemVersion());
        params.put("app_version", TDevice.getVersionCode() + "");
        return params;
    }

    private static JSONObject getJsonObject() {
        JSONObject params = new JSONObject();
        if (AccountHelper.isLogin())
            params.put("user_id", AccountHelper.getUserId());
        params.put("identity_type", "phone");
        params.put("platform", "android");
        params.put("phone_mode", TDevice.getSystemModel());
        params.put("system_version", TDevice.getSystemVersion());
        params.put("app_version", TDevice.getVersionCode() + "");
        return params;
    }

    public static void basicData(TextHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.BASIC_DATA, params, handler);
    }

    /**
     * login account
     *
     * @param identifier username
     * @param credential pwd
     * @param handler    handler
     */
    public static void login(String registration_id, String identifier, String credential, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("registration_id", registration_id);
        params.put("identifier", identifier);
        params.put("credential", credential);
        post(LRUrls.LOGIN, params, handler);
    }

    //验证码登录
    public static void loginCode(String registration_id, String identifier, String code, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("registration_id", registration_id);
        params.put("identifier", identifier);
        params.put("code", code);
        post(LRUrls.LOGIN, params, handler);
    }

    public static void usersHome(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.USERS_HOME, params, handler);
    }

    public static void userActivitySuggest(String longitude, String latitude, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        post(LRUrls.USER_ACTIVITY_SUGGETST, params, handler);
    }

    public static void usersInfo(String user_uuid, int user_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        if (!TextUtils.isEmpty(user_uuid))
            params.put("user_uuid", user_uuid);
        if (user_id != -1)
            params.put("user_id", user_id);
        post(LRUrls.USERS_INFO, params, handler);
    }

    public static void usersContactsStatus(int contact_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("contact_id", contact_id);
        post(LRUrls.USERS_CONTACTS_STATUS, params, handler);
    }

    public static void usersIdentiStatus(String ticket_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("ticket_id", ticket_id);
        post(LRUrls.USERS_IDENTI_STATUS, params, handler);
    }

    public static void userNoteList(int user_id, int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("user_id", user_id);
        params.put("page", page);
        post(LRUrls.USERS_NOTE_LIST, params, handler);
    }

    public static void usersNoteTagList(List<String> tag, int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("tag", JSONArray.parseArray(JSON.toJSONString(tag)));
        params.put("page", page);
        post(LRUrls.USERS_NOTE_TAG_LIST, params, handler);
    }

    //获取七牛参数
    public static void qiniuUpload(String file_name, int type, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("file_name", file_name);
        params.put("type", type);
        post(LRUrls.QINIU, params, handler);
    }

    public static void userNoteAdd(String note_id, int status, List<String> tag, String text, List<String> image_url, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("status", status);
        params.put("tag", JSONArray.parseArray(JSON.toJSONString(tag)));
        params.put("text", text);
        params.put("image_url", image_url);
        if (!TextUtils.isEmpty(note_id)) {
            params.put("note_id", note_id);
            post(LRUrls.USER_NOTE_UPDATE, params, handler);
        } else {
            post(LRUrls.USER_NOTE_ADD, params, handler);
        }
    }


    public static void userNoteView(int userid, String note_id, String comment_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("note_id", note_id);
//        if (userid != -1) {
//            params.put("user_id", userid);
//        }
        if (!TextUtils.isEmpty(comment_id))
            params.put("comment_id", comment_id);
        post(LRUrls.USER_NOTE_VIEW, params, handler);
    }

    public static void userNoteComment(String note_id, int type, int reply_user_id, String content, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("note_id", note_id);
        params.put("type", type);
        if (type == 2) {
            params.put("reply_user_id", reply_user_id);
        }
        params.put("content", content);
        post(LRUrls.USER_NOTE_COMMENT, params, handler);
    }
    public static void usersNoteCommentDelete(String comment_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("comment_id", comment_id);
        post(LRUrls.USERS_NOTE_COMMENT_DELTTE, params, handler);
    }
    public static void pairsDislike(int remote_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("remote_id", remote_id);
        post(LRUrls.PAIRS_DISLIKE, params, handler);
    }

    public static void noticeNoteCount(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.NOTICE_NOTE_COUNT, params, handler);
    }

    public static void userNoticeNoteList(int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("page", page);
        post(LRUrls.NOTICE_NOTE_LIST, params, handler);
    }

    public static void userNoteRecommed(int user_id, List<String> tag, int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("user_id", user_id);
        if (tag != null && tag.size() > 0)
            params.put("tag", tag);
        params.put("page", page);
        post(LRUrls.USER_NOTE_RECOMMEND, params, handler);
    }

    public static void activityFind(int type, int page, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("type", type);
        jsonObject.put("page", page);
        post(LRUrls.ACTIVITY_FIND, jsonObject, handler);
    }

    public static void activityCollectList(int page, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("page", page);
        post(LRUrls.ACTIVITY_COLLECT_LIST, jsonObject, handler);
    }

    public static void activityDetail(String activity_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("activity_id", activity_id);
        post(LRUrls.ACTIVITY_DETAIL, params, handler);
    }

    public static void activityGood(String activity_id, int source, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("activity_id", activity_id);
        params.put("source", source);
        post(LRUrls.ACTIVITY_GOOD, params, handler);
    }

    public static void shopGood(int shop_id, int source, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("shop_id", shop_id);
        params.put("source", source);
        post(LRUrls.SHOP_GOOD, params, handler);
    }

    public static void datingDetail(int dating_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("dating_id", dating_id);
        post(LRUrls.DATING_DETAIL, params, handler);
    }

    public static void createOrder(int type, String obj_id, List<TicketOrderBean> goods, CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        jsonObject.put("type", type);
        jsonObject.put("obj_id", obj_id);
        jsonObject.put("goods", JSONArray.parseArray(JSON.toJSONString(goods)));
        post(LRUrls.ORDER_CREATE, jsonObject, handler);
    }

    public static void goodsListVip(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.GOODS_LIST_VIP, params, handler);
    }

    public static void payVirtul(String good_id, String number, String payment, String receipt, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("good_id", good_id);
        params.put("number", number);
        params.put("payment", payment);
        params.put("receipt", receipt);
        post(LRUrls.USER_VIP_PAY, params, handler);
    }

    public static void userIdentitiesToken(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.USER_AUTH_VERIFY, params, handler);
    }

    public static void userAuthVerifyPrice(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.USER_AUTH_VERIFY_PRICE, params, handler);
    }

    public static void helpInfo(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.HELP_INFO, params, handler);
    }

    public static void upDateUserInfo(String avatar_url, String nickname, String birthday, String sex,
                                      int height, int weight, int birth_place_id, int domicile_id,
                                      int education_code, int revenue_code, int school_id,
                                      int profession_id, int industry_id, int occupation_id, String marital_status,
                                      int is_has_children, String drink_frequency, String smoke_frequency,
                                      String constellation, String about, String status, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("avatar_url", avatar_url);
        params.put("nickname", nickname);
        params.put("birthday", birthday);
        params.put("sex", sex);
        params.put("height", height);
        params.put("weight", weight);
        params.put("birth_place_id", birth_place_id);
        params.put("domicile_id", domicile_id);
        params.put("education_code", education_code);
        params.put("revenue_code", revenue_code);
        params.put("school_id", school_id);
        params.put("profession_id", profession_id);
        params.put("industry_id", industry_id);
        params.put("occupation_id", occupation_id);
        params.put("marital_status", marital_status);
        params.put("is_has_children", is_has_children);
        params.put("drink_frequency", drink_frequency);
        params.put("smoke_frequency", smoke_frequency);
        params.put("constellation", constellation);
        params.put("about", about);
        params.put("status", status);
        post(LRUrls.USER_INFO_UPDATE, params, handler);
    }


    public static void upDateUserInfo(String content, int paramsType, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        if (paramsType == UserConstants.AVATAR) {
            params.put("avatar_url", content);
        } else if (paramsType == UserConstants.NICKNAME) {
            params.put("nickname", content);
        } else if (paramsType == UserConstants.BIRTHDAY) {
            params.put("birthday", content);
        } else if (paramsType == UserConstants.SEX) {
            params.put("sex", content);
        } else if (paramsType == UserConstants.HEIGHT) {
            params.put("height", content);
        } else if (paramsType == UserConstants.WEIGHT) {
            params.put("weight", content);
        } else if (paramsType == UserConstants.BIRTH_ID) {
            params.put("birth_place_id", content);
        } else if (paramsType == UserConstants.DOMICILE_ID) {
            params.put("domicile_id", content);
        } else if (paramsType == UserConstants.EDU_CODE) {
            params.put("education_code", content);
        } else if (paramsType == UserConstants.REVENUE_CODE) {
            params.put("revenue_code", content);
        } else if (paramsType == UserConstants.SCHOOL_ID) {
            params.put("school_id", content);
        } else if (paramsType == UserConstants.PROFESSION_ID) {
            params.put("profession_id", content);
        } else if (paramsType == UserConstants.INDUSTRY_ID) {
            params.put("industry_id", content);
        } else if (paramsType == UserConstants.OCC_ID) {
            params.put("occupation_id", content);
        } else if (paramsType == UserConstants.MARITAL_STATUS) {
            params.put("marital_status", content);
        } else if (paramsType == UserConstants.CHILDREN_STATUS) {
            params.put("is_has_children", content);
        } else if (paramsType == UserConstants.DRINK_STATUS) {
            params.put("drink_frequency", content);
        } else if (paramsType == UserConstants.SMOKE_STATUS) {
            params.put("smoke_frequency", content);
        } else if (paramsType == UserConstants.CONSTELLATION) {
            params.put("constellation", content);
        } else if (paramsType == UserConstants.ABOUT) {
            params.put("about", content);
        } else if (paramsType == UserConstants.STATUS) {
            params.put("status", content);
        }
        post(LRUrls.USER_INFO_UPDATE, params, handler);
    }


    public static void upDateUserRequest(Object content, int paramsType, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        if (paramsType == UserConstants.AGE_RANGE) {
            params.put("age_range", content);
        } else if (paramsType == UserConstants.EDUCATION_RANGE) {
            params.put("education_range", content);
        } else if (paramsType == UserConstants.HEIGHT_RANGE) {
            params.put("height_range", content);
        } else if (paramsType == UserConstants.REVENUE_RANGE) {
            params.put("revenue_range", content);
        } else if (paramsType == UserConstants.DOMICILE_RANGE) {
            params.put("domicile_range", JSONArray.parseArray(JSON.toJSONString(content)));
        } else if (paramsType == UserConstants.MARITAL_STATUS_RANGE) {
            params.put("marital_range", content);
        } else if (paramsType == UserConstants.CHILDREN_STATUS_RANGE) {
            params.put("child_range", content);
        } else if (paramsType == UserConstants.WEIGHT_RANGE) {
            params.put("weight_range", content);
        } else if (paramsType == UserConstants.REQUIRE_DETAIL) {
            params.put("require_detail", content);
        }
        post(LRUrls.USER_INFO_RANGE_UPDATE, params, handler);
    }

    public static void userNoteTags(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("limit", 3);
        post(LRUrls.USER_NOTE_TAGS, params, handler);
    }

    public static void noticeCount(CommonHttpResponseHandler handler) {
        JSONObject jsonObject = getJsonObject();
        post(LRUrls.NOTICE_COUNT, jsonObject, handler);
    }

    public static void userChatList(int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("page", page);
        post(LRUrls.USER_CHATE_LIST, params, handler);
    }

    public static void systemMessageList(int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("page", page);
        post(LRUrls.SYSTEM_LIST, params, handler);
    }

    public static void noticePairList(int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("page", page);
        post(LRUrls.NOTICE_PAIR_LIST, params, handler);
    }

    public static void pairApplyDeal(String apply_id, int status, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("apply_id", apply_id);
        params.put("status", status);
        post(LRUrls.USER_PAIR_DEAL, params, handler);
    }

    public static void contactList(int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("page", page);
        post(LRUrls.CONTEACT_LIST, params, handler);
    }

    public static void contactLike(int page, int type, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("page", page);
        if (type == 0) {
            post(LRUrls.CONTEACT_LIKE, params, handler);
        } else {
            post(LRUrls.CONTEACT_SIGRID, params, handler);
        }
    }

    public static void contactBlackList(int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("page", page);
        post(LRUrls.CONTACT_BLACK_LIST, params, handler);
    }

    public static void contactBlackAdd(int contact_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("contact_id", contact_id);
        post(LRUrls.CONTACT_BLACK_ADD, params, handler);
    }

    public static void contactDelete(int contact_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("contact_id", contact_id);
        post(LRUrls.CONTACT_DELETE, params, handler);
    }

    public static void contactBlackRevert(int contact_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("contact_id", contact_id);
        post(LRUrls.CONTACT_BLACK_REVERT, params, handler);
    }

    public static void usersLike(int remote_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("remote_id", remote_id);
        post(LRUrls.USERS_LIKE, params, handler);
    }

    public static void pubMessage(String remote_id, String msg_type, String msg_content, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("remote_id", remote_id);
        params.put("msg_type", msg_type);
        params.put("msg_content", msg_content);
        post(LRUrls.MESSAGE_ADD, params, handler);
    }

    public static void getChatMessage(String remote_id, int page, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("remote_id", remote_id);
        params.put("page", page);
        post(LRUrls.MESSAGE_FIND, params, handler);
    }

    public static void datingOpenStatus(String longitude, String latitude, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        post(LRUrls.DATING_OPEN_STATUS, params, handler);
    }

    public static void appAgreeMent(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.APP_AGREEMENT, params, handler);
    }

    public static void appPrivacy(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.APP_PRIVACY, params, handler);
    }

    public static void appVersion(String app_version, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("app_version", app_version);
        post(LRUrls.APP_VERSION, params, handler);
    }

    public static void parisApplyAdd(int remote_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("remote_id", remote_id);
        post(LRUrls.PARIS_APPLY_ADD, params, handler);
    }

    public static void usersNoteVisible(String note_id, int status, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("note_id", note_id);
        params.put("status", status);
        post(LRUrls.USERS_NOTE_VISIBLE, params, handler);
    }

    public static void tagSet(String tag, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("tag", tag);
        post(LRUrls.TAG_SET, params, handler);
    }

    public static void usersNoteDelete(String note_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("note_id", note_id);
        post(LRUrls.USERS_NOTE_DELETE, params, handler);
    }

    public static void usersNotePick(String note_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("note_id", note_id);
        post(LRUrls.USERS_NOTE_PICK, params, handler);
    }

    public static void shopDetail(int shop_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("shop_id", shop_id);
        post(LRUrls.SHOP_DETAIL, params, handler);
    }

    public static void datingCreate(int remote_id, int type, int obj_id, int good_id, String time, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("remote_id", remote_id);
        params.put("type", type);
        params.put("obj_id", obj_id);
        params.put("good_id", good_id);
        params.put("time", time);
        post(LRUrls.DATING_CREATE, params, handler);
    }

    public static void userNoteTagList(String tag, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        if (!TextUtils.isEmpty(tag))
            params.put("tag", tag);
        post(LRUrls.USER_NOTE_TAGS, params, handler);
    }

    public static void userNoteTagAdd(String tag, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        if (!TextUtils.isEmpty(tag))
            params.put("tag", tag);
        post(LRUrls.USER_NOTE_TAGS_ADD, params, handler);
    }

    public static void photosDelete(int photo_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("photo_id", photo_id);
        post(LRUrls.PHOTOS_DELETE, params, handler);
    }

    public static void usersPhotoList(CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        post(LRUrls.USERS_PHOTOS_LIST, params, handler);
    }

    public static void userPhotosSaveAll(List<PhotoBean> mList, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("photo", JSONArray.parseArray(JSON.toJSONString(mList)));
        post(LRUrls.USERS_PHOTOS_SAVE_ALL, params, handler);
    }

    public static void userImageUpload(int photo_id, String img_uri, int location, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("photo_id", photo_id);
        params.put("img_uri", img_uri);
        params.put("location", location);
        post(LRUrls.USER_IMAGE_UPLOAD, params, handler);
    }

    public static void userImageUpload(String img_uri, int location, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("img_uri", img_uri);
        params.put("location", location);
        post(LRUrls.USER_IMAGE_UPLOAD, params, handler);
    }

    public static void noticeSystemView(int notice_id, CommonHttpResponseHandler handler) {
        JSONObject params = getJsonObject();
        params.put("notice_id", notice_id);
        post(LRUrls.NOTIECE_SYSTEM_VIEW, params, handler);
    }
}
