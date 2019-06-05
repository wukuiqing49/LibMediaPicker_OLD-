package com.wkq.media.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class EditSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int leftSpace;
    private int rightSpace;
    private int thumbnailsCount;

    public EditSpacingItemDecoration(int leftSpace, int rightSpace, int thumbnailsCount) {
        this.leftSpace = leftSpace;
        this.rightSpace = rightSpace;
        this.thumbnailsCount = thumbnailsCount;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 第一个的前面和最后一个的后面
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = leftSpace;
            outRect.right = 0;
        } else if (thumbnailsCount > 10 && position == thumbnailsCount - 1) {
            outRect.left = 0;
            outRect.right = rightSpace;
        } else {
            outRect.left = 0;
            outRect.right = 0;
        }
    }
}