package com.boer.delos.commen;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2016/1/18 0018.
 * fragment可见时才进行数据加载操作，即Fragment的懒加载。
 */
public abstract class LazyFragment extends Fragment {
    protected boolean isVisible;

    /**
     * 在这里实现Fragment数据的缓加载.
     */

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && isAdded()) {
            isVisible = true;
            onVisible();
            lazyLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            lazyLoad();
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }

}