package com.lianren.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.lianren.android.R;
import com.lianren.android.improve.bean.ProfessionBean;

import java.util.List;

public class SectionItemDecoration extends RecyclerView.ItemDecoration {
    private List<ProfessionBean> mData;
    private Paint mBgPaint;
    private TextPaint mTextPaint;
    private Rect mBounds;

    private int mSectionHeight;
    private int mBgColor;
    private int mTextColor;
    private int mTextSize;

    public SectionItemDecoration(Context context, List<ProfessionBean> data) {
        this.mData = data;
        mBgColor = context.getResources().getColor(R.color.white);
        mSectionHeight = context.getResources().getDimensionPixelSize(R.dimen.space_49);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.font_16);
        mTextColor = context.getResources().getColor(R.color.day_colorPrimary);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColor);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mBounds = new Rect();
    }

    public void setData(List<ProfessionBean> data) {
        this.mData = data;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
                if (position == 0) {
                    drawSection(c, left, right, child, params, position);
                } else {
                    if (null != mData.get(position).sectionName
                            && !mData.get(position).sectionName.equals(mData.get(position - 1).sectionName)) {
                        drawSection(c, left, right, child, params, position);
                    }
                }
            }
        }
    }

    private void drawSection(Canvas c, int left, int right, View child,
                             RecyclerView.LayoutParams params, int position) {
        c.drawRect(left,
                child.getTop() - params.topMargin - mSectionHeight,
                right,
                child.getTop() - params.topMargin, mBgPaint);
        mTextPaint.getTextBounds(mData.get(position).sectionName,
                0,
                mData.get(position).sectionName.length(),
                mBounds);
        c.drawText(mData.get(position).sectionName,
                child.getPaddingLeft()+16,
                child.getTop() - params.topMargin - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        if (pos < 0) return;
        if (mData == null || mData.isEmpty()) return;
        String section = mData.get(pos).sectionName;
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;

        boolean flag = false;
        if ((pos + 1) < mData.size()) {
            if (null != section && !section.equals(mData.get(pos + 1).sectionName)) {
                if (child.getHeight() + child.getTop() < mSectionHeight) {
                    c.save();
                    flag = true;
                    c.translate(0, child.getHeight() + child.getTop() - mSectionHeight);
                }
            }
        }
        c.drawRect(parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(),
                parent.getPaddingTop() + mSectionHeight, mBgPaint);
        mTextPaint.getTextBounds(section, 0, section.length(), mBounds);
        c.drawText(section,
                child.getPaddingLeft()+16,
                parent.getPaddingTop() + mSectionHeight - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
        if (flag)
            c.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
            if (position == 0) {
                outRect.set(0, mSectionHeight, 0, 0);
            } else {
                if (null != mData.get(position).sectionName
                        && !mData.get(position).sectionName.equals(mData.get(position - 1).sectionName)) {
                    outRect.set(0, mSectionHeight, 0, 0);
                }
            }
        }
    }

}
