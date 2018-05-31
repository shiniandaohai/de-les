package com.boer.delos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.commen.MyBaseAdapter;
import com.boer.delos.commen.MyViewHolder;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.WeightBean;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.TimeUtil;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/20 0020 13:26
 * @Modify:
 * @ModifyDate:
 */


public class WeigthRecordAdapter extends MyBaseAdapter<WeightBean> {


    private List<SwipeLayout> swipeLayoutChilds;
    private ISimpleInterfaceInt mListener;

    public WeigthRecordAdapter(Context mContext, List<WeightBean> listData, int itemLayoutId) {
        super(mContext, listData, itemLayoutId);
        int resId = R.layout.item_weight_history;
    }

    @Override
    public void convert(MyViewHolder holder, WeightBean item, final int position) {
        TextView mTvDelete = holder.getView(R.id.tv_delete);
        TextView mTvDate = holder.getView(R.id.tv_date);
        TextView mTvState = holder.getView(R.id.tv_state);
        TextView tv_weight_kg = holder.getView(R.id.tv_weight_kg);
        TextView tv_fate_rate = holder.getView(R.id.tv_fate_rate);

        TextView mTvMuscle = holder.getView(R.id.tv_muscle);
        TextView mTvWater = holder.getView(R.id.tv_water);
        TextView mTvBone = holder.getView(R.id.tv_bone);
        TextView mTvBmr = holder.getView(R.id.tv_bmr);

        TextView mTvBmi = holder.getView(R.id.tv_bmi);
//        SwipeLayout swipeLayout = holder.getView(R.id.swipeLayout);
        try {

            String time = TimeUtil.formatStamp2Time(item.getMeasuretime(), "yyyy/MM/dd HH:mm");
            mTvDate.setText(time);

            if (!TextUtils.isEmpty(item.getDetail())) {
                WeightBean.WeightDetailBean weightDetailBean = GsonUtil.getObject(item.getDetail(), WeightBean.WeightDetailBean.class);
                if (!TextUtils.isEmpty(weightDetailBean.getBMI())) {
                    DealWithValue2.judgeWeightColor(mContext, mTvState, weightDetailBean.getBMI(), true);
                }
            }

            tv_weight_kg.setText(" " + item.getWeight() + " kg");
            tv_fate_rate.setText(" " + item.getFatrate() + " %");

            WeightBean.WeightDetailBean bean = GsonUtil.getObject(item.getDetail(), WeightBean.WeightDetailBean.class);
            if (bean == null) {
                return;
            }

            mTvBmi.setText(bean.getBMI() + " kg/m³");
            mTvWater.setText(bean.getWater() + " %");
            mTvMuscle.setText(bean.getMuscle() + " %");
            mTvBone.setText(bean.getBone() + " %");
            mTvBmr.setText(bean.getKcal() + " Kcal");

        } catch (Exception e) {
            e.printStackTrace();
        }

        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.clickListener(position);
                }

            }
        });

    }


    private void chileHolderControlSwipe(SwipeLayout mSwipeLayout) {
        mSwipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                closeChildMenu(layout);
                swipeLayoutChilds.add(layout);

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                closeChildMenu(layout);
            }
        });

    }

    private void closeChildMenu(SwipeLayout swipLayout) {
        if (swipeLayoutChilds == null) {
            swipeLayoutChilds = new ArrayList<SwipeLayout>();
        }
        for (SwipeLayout s : swipeLayoutChilds) {
            if (s == swipLayout) {
                continue;
            }
            s.close();
        }
    }

    public void setListener(ISimpleInterfaceInt listener) {
        mListener = listener;
    }
}
