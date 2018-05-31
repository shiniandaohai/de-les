package com.boer.delos.commen;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/15 0015 18:23
 * @Modify:
 * @ModifyDate:
 */
public class MySwipeLayoutHolder {
    private List<SwipeLayout> mSwipeLayoutList;
    private SwipeLayout mSwipeLayout;
    private boolean isSwipe;
    private int cancleSwipePos;
    private Mode mMode = Mode.Single;

    public enum Mode {
        Single, Multiple
    }

    private final SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    protected MySwipeLayoutHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        // setTag
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static MySwipeLayoutHolder get(Context context, View convertView,
                                          ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new MySwipeLayoutHolder(context, parent, layoutId, position);
        }
        return (MySwipeLayoutHolder) convertView.getTag();
    }

    public View getConvertView() {
        return mConvertView;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        if (view instanceof SwipeLayout) {
            this.mSwipeLayout = (SwipeLayout) view;
        }
        return (T) view;
    }

    public void setSwipe(boolean swipe) {
        isSwipe = swipe;
        if (isSwipe) {
            chileHolderControlSwipe();
        }
    }

    protected void chileHolderControlSwipe() {
        mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                if (mMode == Mode.Single) {
                    singleChildMenu(layout);
                }
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                mSwipeLayoutList.add(layout);
//                singleChildMenu(layout);
            }
        });

    }

    private void singleChildMenu(SwipeLayout swipeLayout) {
        if (mSwipeLayoutList == null) {
            mSwipeLayoutList = new ArrayList<>();
        }
        for (SwipeLayout s : mSwipeLayoutList) {
            if (s == swipeLayout) {
                continue;
            }
            s.close();
        }
    }

    public boolean isSwipe() {
        return isSwipe;
    }

    public int getCancleSwipePos() {
        return cancleSwipePos;
    }

    public void setCancleSwipePos(int cancleSwipePos) {
        this.cancleSwipePos = cancleSwipePos;
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }
}
