package com.lianren.android.widget.circle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.lianren.android.util.TDevice;

/**
 * @package: com.lianren.android.widget.circle
 * @user:xhkj
 * @date:2020/1/14
 * @description:
 **/
public class ScrollTextView extends View {

    private static final String TAG = "ScrollTextView";

    Paint paint = new Paint();
    private ValueAnimator objectAnimator;
    private Paint.FontMetrics fontMetrics;
    private int oneTextWidth;
    private int top;
    private int bottom;
    private int leading;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setTextSize(TDevice.dp2px(16));
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#CACACA"));
        fontMetrics = paint.getFontMetrics();
        oneTextWidth = (int) paint.measureText("0");
        top = (int) (fontMetrics.top + 0.5f);
        bottom = (int) (fontMetrics.bottom + 0.5f);
        //基准线
        leading = (int) (fontMetrics.leading + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), bottom - top);
    }

    String max;//最长位数
    int moveNum;

    String content = "";
    String newContent;

    int duration = 300;
    boolean isUp;


    /**
     * @param num 设置字数
     */
    public void setTextNum(String num) {
        if (num == null) {
            return;
        }
        newContent = num;
        if (!TextUtils.isEmpty(content)) {
            String text = content;
            int lastNum = Integer.parseInt(text);
            int newNum = Integer.parseInt(num);

            if (newNum > lastNum) {
                //点赞  上滚
                max = num;
                isUp = true;
            } else {
                //取消点赞      下滚
                max = text;
                isUp = false;
            }

            //确定滚动位数
            for (int i = 0; i < max.length(); i++) {
                try {
                    char newS = num.charAt(i);
                    char lastS = text.charAt(i);
                    //不相等就加1  如果越界  直接就是最大变化数
                    if (newS != lastS) {
                        moveNum++;
                    }

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    moveNum = max.length();
                }
            }

            objectAnimator = ObjectAnimator//
                    .ofFloat(1f, 0f)//
                    .setDuration(duration);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.start();
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float progress = (Float) animation.getAnimatedValue();
                    offsetY = progress;
                    invalidate();

                    if (offsetY == 0f) {//结束
                        onComplete();
                    }
                }
            });


        } else {
            invalidate();
        }

        //这里是因为找不到正确的内部方法，不得以而为之。。。
        int length = newContent.length();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = oneTextWidth * length + getPaddingRight() + getPaddingLeft();
        setLayoutParams(layoutParams);

    }

    private void onComplete() {
        content = newContent;
        moveNum = 0;
    }

    float offsetY;

    @Override
    protected void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(content)) {
            //直接绘制
            canvas.drawText(newContent, getPaddingLeft(), +leading - top, paint);
            onComplete();
        } else {
            //动画绘制
            char[] chars = content.toCharArray();
            char[] newChars = newContent.toCharArray();

            for (int i = max.length() - 1; i >= 0; i--) {
                float dy = leading - top;
                float dx = getPaddingLeft() + i * oneTextWidth;

                if (moveNum <= chars.length - 1 - i) {
                    //不滚动的
                    canvas.drawText(String.valueOf(chars[i]), dx, +dy, paint);
                } else {
                    //滚动的
                    try {
                        if (isUp) {
                            //上滚  顺序不能换，下面那一行可能会越界,换行出不了效果
                            canvas.drawText(String.valueOf(newChars[i]), dx, +dy * (1f + offsetY), paint);
                            canvas.drawText(String.valueOf(chars[i]), dx, +dy * offsetY, paint);
                        } else {
                            //下滚
                            canvas.drawText(String.valueOf(chars[i]), dx, +dy * (2f - offsetY), paint);
                            canvas.drawText(String.valueOf(newChars[i]), dx, +dy * (1f - offsetY), paint);
                        }

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onDraw(canvas);


    }
}
