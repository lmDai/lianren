package com.lianren.android.widget.circle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;

import com.lianren.android.R;

/**
 * @package: com.lianren.android.widget.circle
 * @user:xhkj
 * @date:2020/1/13
 * @description:
 **/
public class ExpandTextView extends AppCompatTextView {
    /**
     * 展开状态 true：展开，false：收起
     */
    private boolean expandState = false;

    /**
     * 状态接口
     */
    Callback mCallback;

    /**
     * 源文字内容
     */
    private String mText = "";
    /**
     * 最多展示的行数
     */
    private int maxLineCount = 3;
    /**
     * 省略文字
     */
    private String ellipsizeText = "...";
    /**
     * 展开文案文字
     */
    private String expandText = "展开更多";
    /**
     * 展开文案文字颜色
     */
    private int expandTextColor = Color.parseColor("#74D1CC");
    /**
     * 收起文案文字
     */
    private String collapseText = "收起更多";
    /**
     * 收起文案文字颜色
     */
    private int collapseTextColor = Color.parseColor("#74D1CC");
    /**
     * 是否支持收起功能
     */
    private boolean collapseEnable = true;
    /**
     * 是否添加下划线
     */
    private boolean underlineEnable = false;

    public ExpandTextView(Context context) {
        super(context);
        initTextView();
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextView();
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextView();
    }

    private void initTextView() {
        setVerticalScrollBarEnabled(true);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (TextUtils.isEmpty(mText)) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
        StaticLayout sl = new StaticLayout(mText, getPaint(),
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), Layout.Alignment.ALIGN_CENTER, 1f, 0f, true);
        int lineCount = sl.getLineCount();
        if (lineCount > maxLineCount) {
            if (expandState) {
                setText(mText);
                //是否支持收起功能
                if (collapseEnable) {
                    // 收起文案和源文字组成的新的文字
                    String newEndLineText = mText + collapseText;
                    //收起文案和源文字组成的新的文字
                    SpannableString spannableString = new SpannableString(newEndLineText);
                    //给收起设成监听
                    spannableString.setSpan(new SpannableClickable(getResources().getColor(R.color.day_colorPrimary)) {
                                                @Override
                                                public void onClick(View widget) {
                                                    if (mCallback != null) {
                                                        mCallback.onCollapseClick();
                                                    }
                                                }
                                            }, newEndLineText.length() - collapseText.length(), newEndLineText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (underlineEnable) {
                        //给收起添加下划线
                        spannableString.setSpan(new UnderlineSpan(), newEndLineText.length() -
                                collapseText.length(), newEndLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    //给收起设成蓝色
                    spannableString.setSpan(new ForegroundColorSpan(collapseTextColor), newEndLineText.length() - collapseText.length(),
                            newEndLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setText(spannableString);
                }
                if (mCallback != null) {
                    mCallback.onExpand();
                }
            } else {
                lineCount = maxLineCount;
                // 省略文字和展开文案的宽度
                float dotWidth = getPaint().measureText(ellipsizeText + expandText);
                // 找出显示最后一行的文字
                int start = sl.getLineStart(lineCount - 1);
                int end = sl.getLineEnd(lineCount - 1);
                String lineText = mText.substring(start, end);
                // 将第最后一行最后的文字替换为 ellipsizeText和expandText
                int endIndex = 0;
                for (int i = 0; i < lineText.length() - 1; i++) {
                    String str = lineText.substring(i, lineText.length());
                    // 找出文字宽度大于 ellipsizeText 的字符
                    if (getPaint().measureText(str) >= dotWidth) {
                        endIndex = i;
                        break;
                    }
                }
                // 新的文字
                String newEndLineText = mText.substring(0, start) + lineText.substring(0, endIndex) + ellipsizeText + expandText;
                //全部文字
                SpannableString spannableString = new SpannableString(newEndLineText);
                //给查看全部设成监听
                spannableString.setSpan(new SpannableClickable(getResources().getColor(R.color.day_colorPrimary)) {
                                            @Override
                                            public void onClick(View widget) {
                                                if (mCallback != null) {
                                                    mCallback.onExpandClick();
                                                }
                                            }
                                        }, newEndLineText.length() - expandText.length(), newEndLineText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (underlineEnable) {
                    spannableString.setSpan(new UnderlineSpan(), newEndLineText.length() - expandText.length(),
                            newEndLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                //给查看全部设成颜色
                spannableString.setSpan(expandTextColor, newEndLineText.length() - expandText.length(), newEndLineText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 最终显示的文字
                setText(spannableString);

                if (mCallback != null) {
                    mCallback.onCollapse();
                }
            }
        } else {
            setText(mText);
            if (mCallback != null) {
                mCallback.onLoss();
            }

        }
        // 重新计算高度
        int lineHeight = 0;
        for (int i = 0; i < lineCount; i++) {
            Rect lineBound = new Rect();
            sl.getLineBounds(i, lineBound);
            lineHeight += lineBound.height();
        }
        lineHeight = (int) (getPaddingTop() + getPaddingBottom() + lineHeight * getLineSpacingMultiplier());
        setMeasuredDimension(getMeasuredWidth(), lineHeight);
    }

    /**
     * 设置要显示的文字以及状态
     *
     * @param text
     * @param expanded true：展开，false：收起
     * @param callback
     */
    public void setText(String text, boolean expanded, Callback callback) {
        mText = text;
        expandState = expanded;
        mCallback = callback;

        // 设置要显示的文字，这一行必须要，否则 onMeasure 宽度测量不正确
        setText(text);
    }

    /**
     * 展开收起状态变化
     *
     * @param expanded
     */
    public void setChanged(boolean expanded) {
        expandState = expanded;
        requestLayout();
    }

    public interface Callback {
        /**
         * 展开状态
         */
        void onExpand();

        /**
         * 收起状态
         */
        void onCollapse();

        /**
         * 行数小于最小行数，不满足展开或者收起条件
         */
        void onLoss();

        /**
         * 点击全文
         */
        void onExpandClick();

        /**
         * 点击收起
         */
        void onCollapseClick();
    }
}
