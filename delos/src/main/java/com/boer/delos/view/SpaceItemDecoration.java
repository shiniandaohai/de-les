package com.boer.delos.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.DensityUtil;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/6 0006 21:30
 * @Modify:
 * @ModifyDate:
 */


public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = DensityUtil.dip2px(space);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) != 0) {
            outRect.top = space;
            outRect.bottom = space;
        }
    }

}
