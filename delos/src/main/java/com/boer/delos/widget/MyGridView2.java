package com.boer.delos.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.GridView;

/**
 * @author wangkai
 * @Description: 重写GridView中onMeasure方法，用于实现GridView在ScrollView中正常显示
 * create at 2015/10/30 16:43
 */
public class MyGridView2 extends GridView {
    //设置是否可以滑动，首页舒适生活的图标在某些手机上滑动，界面有偏差，所以设置为不能滑动
    public boolean isCanMove = true;

    public MyGridView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyGridView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView2(Context context) {
        super(context);
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        if (getChildCount() - 1 == getLastVisiblePosition() && getChildCount() % 2 != 0) {
            columnWidth = getScreenWidth(getContext())[0] - getPaddingLeft() - getPaddingRight();
        }
        super.setColumnWidth(columnWidth);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


    //通过重新dispatchTouchEvent方法来禁止滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE && !isCanMove) {
            return true;//禁止Gridview进行滑动
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int[] getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

}
