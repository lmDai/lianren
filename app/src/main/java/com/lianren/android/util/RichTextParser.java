package com.lianren.android.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import com.lianren.android.improve.bean.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @package: com.lianren.android.util
 * @user:xhkj
 * @date:2019/12/27
 * @description:
 **/
public  abstract class RichTextParser {
    public static boolean machPhoneNum(CharSequence phoneNumber) {
        String regex = "^[1][0-9][0-9]\\d{8}$";
        return Pattern.matches(regex, phoneNumber);
    }
    public static String showPhone(String phonenum) {
        if (machPhoneNum(phonenum)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < phonenum.length(); i++) {
                char c = phonenum.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return phonenum;
    }
    private static final Pattern PatternAtUserWithHtml = Pattern.compile(
            "<a href=['\"]http[s]?://my.oschina.net/(\\w+|u/([0-9]+))['\"][^<>]+>(@([^@<>]+))</a>"
    );

    static final Pattern PatternAtUser = Pattern.compile(
            "@[^@\\s:]+"
    );

    // #Java#
    private static final Pattern PatternSoftwareTagWithHtml = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>(#[^#@<>\\s]+#)</a>"
    );
    static final Pattern PatternSoftwareTag = Pattern.compile(
            "#([^#@<>\\s]+)#"
    );

    // @user links
    @Deprecated
    static final Pattern PatternAtUserAndLinks = Pattern.compile(
            "<a\\s+href=['\"]http://my\\.oschina\\.net/([0-9a-zA-Z_]+)['\"][^<>]*>(@[^@<>\\s]+)</a>" +
                    "|<a href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // git tag
    private static final Pattern PatternGit = Pattern.compile(
            "<a\\s+href=\'http[s]?://(git.oschina.net|gitee.com)/[^>]*\'[^>]*data-project=\'([0-9]*)\'[^>]*>([^<>]*)</a>"
    );

    // 代码片段
    private static final Pattern PatternGist = Pattern.compile(
            "<a\\s+href=['\"]http[s]?://(git.oschina.net|gitee.com)/([^\\s]+)/([^\\s]+)['\"][^>]+data-url=['\"]([^\\s]+)['\"][^>]+>([^>]+)</a>"
    );

    // links
    private static final Pattern PatternLinks = Pattern.compile(
            "<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // team task
    private static final Pattern PatternTeamTask = Pattern.compile(
            "<a\\s+style=['\"][^'\"]*['\"]\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>"
    );

    // html task
    public static final Pattern PatternHtml = Pattern.compile(
            "<[^<>]+>([^<>]+)</[^<>]+>"
    );

    /**
     * 解析
     *
     * @param context context
     * @param content content
     * @return Spannable
     */
    public abstract Spannable parse(Context context, String content);

    /**
     * @param sequence       文本
     * @param pattern        正则
     * @param usedGroupIndex 使用的组号
     * @param showGroupIndex 显示的组号
     * @param listener       点击回掉
     * @return 匹配后的文本
     */
    @SuppressWarnings("all")
    private static Spannable assimilate(CharSequence sequence,
                                        Pattern pattern,
                                        int usedGroupIndex,
                                        int showGroupIndex,
                                        final OnClickListener listener) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        Matcher matcher;
        while (true) {
            matcher = pattern.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(usedGroupIndex);
                final String group1 = matcher.group(showGroupIndex);
                builder.replace(matcher.start(), matcher.end(), group1);
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        listener.onClick(group0);
                    }
                };
                builder.setSpan(span, matcher.start(), matcher.start() + group1.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                continue;
            }
            break;
        }
        return builder;
    }


    interface OnClickListener {
        void onClick(String str);
    }


    /**
     * 格式化<a href="url" ...>@xxx</a>
     * // http://my.oschina.net/u/user_id
     * // http://my.oschina.net/user_ident
     *
     * @param context context
     * @param content content
     * @return Spannable
     */
    public static Spannable parseOnlyAtUser(final Context context, CharSequence content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher;
        while (true) {
            matcher = PatternAtUserWithHtml.matcher(builder.toString());
            if (matcher.find()) {
                final String group0 = matcher.group(1); // ident 标识 如retrofit
                final String group1 = matcher.group(2); // uid id
                final String group2 = matcher.group(3); // @Nick
                final String group3 = matcher.group(4); // Nick
                builder.replace(matcher.start(), matcher.end(), group2);
                long uid;
                try {
                    uid = group1 == null ? 0 : Integer.valueOf(group1);
                } catch (Exception e) {
                    uid = 0;
                }
                final long _uid = uid;
                ClickableSpan span = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {

                    }
                };
                builder.setSpan(span, matcher.start(), matcher.start() + group2.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                continue;
            }
            break;
        }
        return builder;
    }



    public static boolean checkIsZH(String input) {
        char[] charArray = input.toLowerCase().toCharArray();
        for (char c : charArray) {
            String tempC = Character.toString(c);
            if (tempC.matches("[\u4E00-\u9FA5]+")) {
                return true;
            }
        }
        return false;
    }

}
