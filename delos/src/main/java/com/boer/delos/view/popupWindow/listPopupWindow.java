package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BasePopupWindow;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Area;
import com.boer.delos.model.Room;

import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/22 0022 20:05
 * @Modify:
 * @ModifyDate:
 */


public class listPopupWindow extends BasePopupWindow {
    private ListView mListView;
    private AreaAdapter mAdapter;
    private Room mRoom;
    private List<Area> mList;
    private IObjectInterface<Area> listener;

    private TextView tvCancle;
    private TextView tvOk;


    public listPopupWindow(Context context, @LayoutRes int resId) {
        super(context, resId);
        initView();
    }

    public void setRoom(Room room) {
        mRoom = room;
        mList = room.getAreas();

    }

    @Override
    protected void setProperty() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        AlphaAnimation animation = new AlphaAnimation(0.6f, 1.0f);
        animation.setDuration(100);
        mPopView.startAnimation(animation);
    }

    @Override
    public int setPopWindowResId() {
        return 0;
    }

    private void initView() {
        mListView = getView(R.id.list_area);
        tvCancle = getView(R.id.tvCancle);
        tvOk = getView(R.id.tvOk);
    }

    public void setAdapter() {
        AreaAdapter mAdapter = new AreaAdapter(mContext, mList, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onClickListenerOK(mList.get(position), position, null);
                }
            }
        });
    }

    public void setListener(IObjectInterface<Area> listener) {
        this.listener = listener;
    }

    static class AreaAdapter extends MyBaseAdapter<Area> {
        public AreaAdapter(Context mContext, List<Area> listData, int itemLayoutId) {
            super(mContext, listData, itemLayoutId);
        }

        @Override
        public void convert(MyViewHolder holder, Area item, int position) {
            TextView textView = holder.getView(android.R.id.text1);
            textView.setText(item.getName());
        }
    }

}
