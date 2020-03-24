package com.lianren.android.widget.circle;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.lianren.android.LRApplication;
import com.lianren.android.R;


/**
 * @author yiw
 * @Description:
 * @date 16/1/2 16:32
 */
public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

    private int DEFAULT_COLOR_ID = R.color.day_colorPrimary;
    /**
     * text颜色
     */
    private int textColor;

    public SpannableClickable() {
        this.textColor = LRApplication.context().getResources().getColor(DEFAULT_COLOR_ID);
    }

    public SpannableClickable(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setColor(textColor);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
