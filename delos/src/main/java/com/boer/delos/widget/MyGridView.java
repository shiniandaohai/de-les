package com.boer.delos.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * @author wangkai
 * @Description: 重写GridView中onMeasure方法，用于实现GridView在ScrollView中正常显示
 * create at 2015/10/30 16:43
 */
public class MyGridView extends GridView {
    //设置是否可以滑动，首页舒适生活的图标在某些手机上滑动，界面有偏差，所以设置为不能滑动
    public boolean isCanMove = true;

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


    //通过重新dispatchTouchEvent方法来禁止滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_MOVE&&!isCanMove){
            return true;//禁止Gridview进行滑动
        }
        return super.dispatchTouchEvent(ev);
    }



}
