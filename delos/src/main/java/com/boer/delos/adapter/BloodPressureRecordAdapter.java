package com.boer.delos.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.PressureResult;
import com.boer.delos.utils.TimeUtil;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/17 0017 21:20
 * @Modify:
 * @ModifyDate:
 */


public class BloodPressureRecordAdapter extends MyBaseAdapter<PressureResult.PressureBean> {

    private List<SwipeLayout> swipeLayoutChilds;
    private ISimpleInterfaceInt mListener;

    public BloodPressureRecordAdapter(Context mContext, List<PressureResult.PressureBean> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        int resId = R.layout.item_blood_pressure_record;
    }

    @Override
    public void convert(MyViewHolder holder, PressureResult.PressureBean item, final int position) {

        TextView tv_date = holder.getView(R.id.tv_date);
        TextView tv_state = holder.getView(R.id.tv_state);
        TextView tv_bp_H = holder.getView(R.id.tv_bp_H);
        TextView tv_bp_L = holder.getView(R.id.tv_bp_L);
        TextView tv_blood_rate = holder.getView(R.id.tv_blood_rate);
        TextView tv_delete = holder.getView(R.id.tv_delete);
//        SwipeLayout mSwipeLayout = holder.getView(R.id.swipeLayout);

//        chileHolderControlSwipe(mSwipeLayout);

        try {
            String time = TimeUtil.formatStamp2Time(item.getMeasuretime(), "yyyy/MM/dd HH:mm");
            tv_date.setText(time);
            tv_bp_H.setText(item.getValueH() + "");
            tv_bp_L.setText(item.getValueL() + "");
            tv_blood_rate.setText(item.getBpm() + "");

            DealWithValue2.judgeBPColor(mContext, tv_state, item.getValueH(), item.getValueL(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.clickListener(position);
                }

            }
        });
    }

//
//    private void chileHolderControlSwipe(SwipeLayout mSwipeLayout) {
//        mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
//            @Override
//            public void onStartOpen(SwipeLayout layout) {
//                closeChildMenu(layout);
//                swipeLayoutChilds.add(layout);
//
//            }
//
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                closeChildMenu(layout);
//            }
//        });
//
//    }
//
//    private void closeChildMenu(SwipeLayout swipLayout) {
//        if (swipeLayoutChilds == null) {
//            swipeLayoutChilds = new ArrayList<SwipeLayout>();
//        }
//        for (SwipeLayout s : swipeLayoutChilds) {
//            if (s == swipLayout) {
//                continue;
//            }
//            s.close();
//        }
//    }

    public void setListener(ISimpleInterfaceInt listener) {
        mListener = listener;
    }
}
