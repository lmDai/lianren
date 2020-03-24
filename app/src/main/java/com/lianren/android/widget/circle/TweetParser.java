package com.lianren.android.widget.circle;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.lianren.android.improve.behavior.InputHelper;
import com.lianren.android.util.HTMLUtil;
import com.lianren.android.util.RichTextParser;

import java.util.regex.Matcher;

/**
 * @package: com.lianren.android.widget.circle
 * @user:xhkj
 * @date:2020/1/15
 * @description:
 **/
public class TweetParser extends RichTextParser {
    private static TweetParser mInstance = new TweetParser();

    public static TweetParser getInstance() {
        return mInstance;
    }

    @Override
    public Spannable parse(Context context, String content) {
        if (TextUtils.isEmpty(content))
            return null;
        content = HTMLUtil.rollbackReplaceTag(content);
        Spannable spannable = parseOnlyAtUser(context, content);
        spannable = InputHelper.displayEmoji(context.getResources(), spannable);
        return spannable;
    }

    /**
     * 清空HTML标签
     */
    public Spannable clearHtmlTag(CharSequence content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        Matcher matcher;
        while (true) {
            matcher = PatternHtml.matcher(builder.toString());
            if (matcher.find()) {
                String str = matcher.group(1);
                builder.replace(matcher.start(), matcher.end(), str);
                continue;
            }
            break;
        }
        return builder;
    }
}

