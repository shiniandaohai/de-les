package com.boer.delos.commen;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.PopupWindow;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/20 0020 14:26
 * @Modify:
 * @ModifyDate:
 */


public abstract class BasePopupWindow extends PopupWindow {
    protected View mPopView = null;
    protected Context mContext;
    private LayoutInflater inflater;

    private SparseArray<View> mViewSparseArray;
    private View popWindow;

    public BasePopupWindow(Context context, @LayoutRes int resId) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
        mPopView = inflater.inflate(resId, null);
        mViewSparseArray = new SparseArray<>();
        setContentView(mPopView);

        setProperty();
    }

    public BasePopupWindow(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        mViewSparseArray = new SparseArray<>();
        mPopView = inflater.inflate(setPopWindowResId(), null);
        setContentView(mPopView);

        setProperty();
    }

    protected void setProperty() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        mPopView.startAnimation(animation);
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    protected <T extends View> T getView(int viewId) {
        View view = mViewSparseArray.get(viewId);
        if (view == null) {
            view = mPopView.findViewById(viewId);
            mViewSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    public abstract int setPopWindowResId();

}
