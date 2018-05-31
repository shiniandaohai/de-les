package com.boer.delos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.weight.WeigthHomeActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.WeightBean;
import com.boer.delos.model.WeightResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.MPandroidChartHelper;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/20 0020 10:58
 * @Modify:
 * @ModifyDate:
 */


public class WeightFragment extends LazyFragment implements IObjectInterface<List<WeightBean>> {
    @Bind(R.id.lineChart)
    LineChart mLineChart;
    @Bind(R.id.tv_flag)
    TextView mTvFlag;

    private View mRootView;
    private int mPosition;
    private ArrayList<String> valsX1;
    private ArrayList<String> valsX2;

    private ArrayList<Float> valsY1;
    private ArrayList<Float> valsY2;
    private List<WeightBean> mWeightList;

    public static WeightFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("mPosition", position);
        WeightFragment fragment = new WeightFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_weight_detail, container, false);
        ButterKnife.bind(this, mRootView);

        initView();
        initData();
        initListener();
        return mRootView;
    }

    private void initView() {
        ((WeigthHomeActivity) getActivity()).addLlistener(this);
        mPosition = getArguments().getInt("mPosition");


        valsX1 = new ArrayList<>();
        valsX2 = new ArrayList<>();
        valsY1 = new ArrayList<>();
        valsY2 = new ArrayList<>();

    }

    private void initData() {
        MPandroidChartHelper.initLineChart(mLineChart, true,7);
        if (mPosition == 0) {
            mTvFlag.setText(getString(R.string.text_weight));
            MPandroidChartHelper.setLineChart(getActivity(), mLineChart, valsX1, valsY1);
        } else {
            mTvFlag.setText(getString(R.string.text_fat_rate));
            MPandroidChartHelper.setLineChart(getActivity(), mLineChart, valsX2, valsY2);
        }
    }

    private void initListener() {

    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void dealWithData(List<WeightBean> weightLists) {
        valsX1.clear();
        valsX2.clear();
        valsY1.clear();
        valsY2.clear();


        if (weightLists == null || weightLists.size() == 0) {
            if (mPosition == 0) {
                MPandroidChartHelper.setLineChart(getActivity(), mLineChart, valsX1, valsY1);
            } else {
                MPandroidChartHelper.setLineChart(getActivity(), mLineChart, valsX2, valsY2);
            }
            return;
        }
        for (WeightBean bean : weightLists) {
            String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "MM.dd");
            valsX1.add(time);
            valsX2.add(time);

            valsY1.add(bean.getWeight());
            valsY2.add(bean.getFatrate());
        }
        Collections.reverse(valsX1);
        Collections.reverse(valsX2);
        Collections.reverse(valsY1);
        Collections.reverse(valsY2);

        if (mPosition == 0) {
            MPandroidChartHelper.setLineChart(getActivity(), mLineChart, valsX1, valsY1);
        } else {
            MPandroidChartHelper.setLineChart(getActivity(), mLineChart, valsX2, valsY2);
        }

    }

    @Override
    public void onClickListenerOK(List<WeightBean> weightBeens, int pos, String tag) {

        mPosition = pos;
        dealWithData(weightBeens);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
