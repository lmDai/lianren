package com.lianren.android.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @package: com.lianren.android.widget
 * @user:xhkj
 * @date:2019/12/20
 * @description:
 **/
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    protected int mSpace = 10;

    private boolean mIncludeEdge = true;

    public GridItemDecoration(int space) {
        this.mSpace = space;
    }

    public GridItemDecoration(int space, boolean includeEdge) {
        this.mSpace = space;
        this.mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        //列数
        int spanCount = gridLayoutManager.getSpanCount();
        int position = parent.getChildLayoutPosition(view);
        int column = (position) % spanCount;
        if (mIncludeEdge) {
            outRect.left = mSpace - column * mSpace / spanCount;
            outRect.right = (column + 1) * mSpace / spanCount;
        } else {
            outRect.left = column * mSpace / spanCount;
            outRect.right = mSpace - (column + 1) * mSpace / spanCount;
        }
    }
}
