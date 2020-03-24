package com.lianren.android.api.remote;

/**
 * @package: com.lianren.android.api.remote
 * @user:xhkj
 * @date:2019/12/18
 * @description:
 **/
public class LRUrls {

    public static final String BASIC_DATA = "/api/basic/suggest";//基础数据
    public static final String LOGIN = "api/passports/_login";//登录
    public static final String REGISTER = "api/passports/_register";//注册
    public static final String FORGET_PASSWORD = "api/passports/_forget";//忘记密码
    public static final String CODES_SEND = "api/codes/_send";//手机验证码
    public static final String USERS_HOME = "api/users/home";//首页匹配
    public static final String USERS_INFO = "api/users/info";//用户详细信息
    public static final String USERS_IDENTI_STATUS = "api/users/identities/status";//获取实名认证状态
    public static final String USERS_NOTE_LIST = "api/users/note/my/list";//印记列表
    public static final String USERS_NOTE_TAG_LIST = "api/users/note/list";//印记列表
    public static final String QINIU = "api/upload/qiniuUpload";//获取七牛参数
    public static final String USER_NOTE_ADD = "api/users/note/add";//添加印记
    public static final String USER_NOTE_UPDATE = "api/users/note/update";//修改印记
    public static final String USER_NOTE_VIEW = "api/users/note/view";//印记详情
    public static final String USER_NOTE_COMMENT = "api/users/note/comment";//印记留言
    public static final String USER_NOTE_RECOMMEND = "api/users/note/recommend";//推荐印记
    public static final String USER_ACTIVITY_SUGGETST = "api/users/activity/suggest";//活动推荐
    public static final String ACTIVITY_FIND = "api/activity/find";//搜索活动
    public static final String ACTIVITY_COLLECT_LIST = "api/activity/collect/list";//搜索活动
    public static final String ACTIVITY_DETAIL = "api/activity/detail";//活动详情
    public static final String DATING_DETAIL = "api/dating/detail";//活动详情
    public static final String ACTIVITY_GOOD = "api/activity/good";//活动商品
    public static final String SHOP_GOOD = "api/shop/good";//空间商品
    public static final String ORDER_CREATE = "api/order/create";//创建订单
    public static final String GOODS_LIST_VIP = "api/goods/list/vip";//获取vip商品
    public static final String USER_VIP_PAY = "api/pay/virtual";//vip支付
    public static final String USER_AUTH_VERIFY = "api/users/identities/token";//获取实名认证Token
    public static final String USER_AUTH_VERIFY_PRICE = "api/goods/list/identity";//实名认证价格
    public static final String HELP_INFO = "api/app/help";//帮助信息
    public static final String USER_INFO_UPDATE = "api/users/update";//修改用户信息
    public static final String USER_INFO_RANGE_UPDATE = "api/users/requires/update";//修改用户要求信息
    public static final String USER_NOTE_TAGS = "api/users/note/tag/list";//推荐印记标签
    public static final String USER_NOTE_TAGS_ADD = "api/users/note/tag/add";//添加标签
    public static final String H5_URI = "api/h5/uri";//h5访问地址
    public static final String PAY_ORDER = "api/pay/order/doPay";//订单支付
    public static final String ORDER_LIST = "api/order/list";//我的订单
    public static final String ORDER_TICKET = "api/order/ticket";//订单票券
    public static final String FB_TYPE = "api/fb/fbtype";// 取得反馈分类
    public static final String FEED_BACK_COMMIT = "api/feedback/submit";// 保存反馈信息
    public static final String ORDER_CONTACT_UPDATE = "api/order/contact/update";// 修改订单用户信息
    public static final String NOTICE_COUNT = "api/users/notice/count";// 用户消息合计信息(匹配申请、系统消息)
    public static final String USER_CHATE_LIST = "api/users/chat/list";// 聊天用户列表
    public static final String SYSTEM_LIST = "api/users/notice/system/list";// 用户系统通知列表
    public static final String NOTICE_PAIR_LIST = "api/users/notice/pair/list";// 用户匹配申请通知列表
    public static final String USER_PAIR_DEAL = "api/users/pairs/apply/deal";// 用户匹配申请通知列表
    public static final String SHOP_FIND = "api/shop/find";// 搜索空间
    public static final String CONTEACT_LIST = "api/users/contacts/list";// 联系人
    public static final String CONTEACT_LIKE = "api/users/like/list";// 喜欢列表
    public static final String CONTEACT_SIGRID = "api/users/sigrid/list";// 被喜欢列表
    public static final String CONTACT_BLACK_LIST = "api/users/contacts/black/list";// 黑名单列表
    public static final String CONTACT_BLACK_ADD = "api/users/contacts/black/add";// 添加黑名单
    public static final String CONTACT_DELETE = "api/users/contacts/delete";// 删除联系人
    public static final String CONTACT_BLACK_REVERT = "api/users/contacts/black/revert";// 撤销黑名单
    public static final String ORDER_CANCEL = "api/order/cancel";// 取消订单
    public static final String MESSAGE_ADD = "api/users/message/add";// 取消订单
    public static final String MESSAGE_FIND = "api/users/message/find";// 取消订单
    public static final String APP_AGREEMENT = "api/app/agreement";// 用户协议
    public static final String APP_PRIVACY = "api/app/privacy";// 隐私协议
    public static final String CODES_VERIFY = "api/codes/verify";// 手机验证码验证
    public static final String APP_VERSION = "api/app/version";// 获取最新app版本信息
    public static final String PARIS_APPLY_ADD = "api/users/pairs/apply/add";// 申请加入匹配
    public static final String USERS_NOTE_VISIBLE = "api/users/note/visible";// 设置记录是否可见
    public static final String USERS_NOTE_DELETE = "api/users/note/delete";// 删除印记
    public static final String SHOP_DETAIL = "api/shop/detail";// 空间详情
    public static final String DATING_CREATE = "api/dating/create";// 发起邀约
    public static final String DATING_LIST = "api/dating/list";// 邀约列表
    public static final String DATING_REQUEST_DEAL = "api/dating/request/deal";// 处理邀约请求
    public static final String DATING_CANCEL = "api/dating/cancel";// 取消邀约
    public static final String ORDER_DETAIL = "api/order/detail";// 取消邀约
    public static final String PHOTOS_DELETE = "api/users/photos/delete";// 删除相册图片
    public static final String USER_IMAGE_UPLOAD = "api/users/photos/save";//上传相册图片
    public static final String USERS_LIKE = "api/users/like";//喜欢
    public static final String ACTIVITY_COLLECT = "api/activity/collect";//收藏活动
    public static final String USERS_NOTE_PICK = "api/users/note/pick";//收藏活动
    public static final String NOTIECE_SYSTEM_VIEW = "api/users/notice/system/view";//用户系统通知查看
    public static final String DATING_OPEN_STATUS = "api/dating/open/status";//邀约是开放
    public static final String CHANGE_PHONE = "api/passports/change_phone";//修改电话号码
    public static final String TAG_SET = "api/users/tag/set";//添加/取消用户信息标签
    public static final String PAIRS_DISLIKE = "api/users/pairs/dislike";//不喜欢
    public static final String NOTICE_NOTE_COUNT = "api/users/notice/note/count";//印记留言通知数量
    public static final String NOTICE_NOTE_LIST = "api/users/notice/note/list";//留言通知列表
    public static final String PASSPORTS_CHANGE_PWD = "api/passports/changePwd";//修改密码
    public static final String USERS_PHOTOS_LIST="api/users/photos/list";//查询用户图片
    public static final String USERS_PHOTOS_SAVE_ALL="api/users/photos/save_all";//保存所有图片
    public static final String USERS_CONTACTS_STATUS="api/users/contacts/status";// 查看联系人状态
    public static final String USERS_NOTE_COMMENT_DELTTE="api/users/note/comment/delete";// 删除印记留言

}
