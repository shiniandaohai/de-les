package com.boer.delos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.WaterCleanDetailActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.WaterTDSResult;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.MPandroidChartHelper;
import com.boer.delos.utils.TimeUtil;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/12 0012 15:59
 * @Modify:
 * @ModifyDate:
 */


public class WaterStatisticsFragment extends LazyFragment
        implements IObjectInterface<List<WaterTDSResult.WaterBean>> {

    @Bind(R.id.tv_use_all)
    TextView mTvUseAll;
    @Bind(R.id.tv_user_normal)
    TextView mTvUserNormal;
    @Bind(R.id.tv_use_hot)
    TextView mTvUseHot;
    @Bind(R.id.chart_raw)
    LineChart mChartRaw;
    @Bind(R.id.chart_clean)
    LineChart mChartClean;

    private View rootView;
    private DeviceRelate mDeviceRelate;
    private Device mDevice;
    private DeviceStatusValue mValue;
    private List<String> xValues;
    private List<Float> yValuesRaw;
    private List<Float> yValuesCean;

    public static WaterStatisticsFragment newInstance(DeviceRelate d) {

        Bundle args = new Bundle();
        args.putSerializable("device", d);
        WaterStatisticsFragment fragment = new WaterStatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_water_statistics, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        initData();
        return rootView;
    }


    private void initView() {
        mDeviceRelate = (DeviceRelate) getArguments().getSerializable("device");
        mValue=mDeviceRelate.getDeviceStatus().getValue();
        if (mValue != null) {
            mTvUseAll.setText(mValue.getTotalWater() + "");
            mTvUseHot.setText(mValue.gettHotWater() + "");
            mTvUserNormal.setText(mValue.gettWarmWater() + "");
        }

        MPandroidChartHelper.initLineChart(mChartRaw, true,30);
        MPandroidChartHelper.initLineChart(mChartClean, true,30);
    }

    private void initData() {

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClickListenerOK(List<WaterTDSResult.WaterBean> waterBeen, int pos, String tag) {
        if (waterBeen != null
                && waterBeen.size() != 0
                && !TextUtils.isEmpty(tag)
                && tag.equals("0")) {
            updateUI(waterBeen);
        }
        else{
            MPandroidChartHelper.setLineChart(getActivity(), mChartRaw, null, null);
            MPandroidChartHelper.setLineChart(getActivity(), mChartClean, null, null);
        }
    }

    private void updateUI(List<WaterTDSResult.WaterBean> list) {
        if (list != null && list.size() != 0) {
            String payload = list.get(0).getPayload();
            if (!TextUtils.isEmpty(payload)) {
                WaterTDSResult.PayloadBean payloadBean = GsonUtil.getObject(payload,
                        WaterTDSResult.PayloadBean.class);

                if (payloadBean == null && payloadBean.getValue() == null) {
                    return;
                }
                mValue=payloadBean.getValue();
                if (mValue != null) {
                    mTvUseAll.setText(mValue.getTotalWater() + "");
                    mTvUseHot.setText(mValue.gettHotWater() + "");
                    mTvUserNormal.setText(mValue.gettWarmWater() + "");
                }
            }
        }


        if (list == null || list.size() == 0) {
            return;
        }
        if (xValues == null) {
            xValues = new ArrayList<>();
        }
        if (yValuesRaw == null) {
            yValuesRaw = new ArrayList<>();
        }
        if (yValuesCean == null) {
            yValuesCean = new ArrayList<>();
        }
        xValues.clear();
        yValuesCean.clear();
        yValuesRaw.clear();

        int index = 0;
        for (WaterTDSResult.WaterBean waterBean : list) {//前面几条数据的时间重复了
            if (!TextUtils.isEmpty(waterBean.getPayload())) {
                WaterTDSResult.PayloadBean payloadBean = GsonUtil.getObject(waterBean.getPayload(),
                        WaterTDSResult.PayloadBean.class);
                String time = TimeUtil.formatStamp2Time(waterBean.getTime(), "MM-dd");
                xValues.add(time);

                if (payloadBean.getValue() == null) {
                    index++;
                    continue;
                }
//                if (index == 0) {
//                    if (payloadBean.getValue().getTotalWater() == null) {
//                        mTvUseAll.setText(payloadBean.getValue().getTotalWater().intValue() + "");
//                    }
//                    if (payloadBean.getValue().gettHotWater() == null) {
//                        mTvUseHot.setText(payloadBean.getValue().gettHotWater().intValue() + "");
//                    }
//                    if (payloadBean.getValue().gettWarmWater() == null) {
//                        mTvUserNormal.setText(payloadBean.getValue().gettWarmWater().intValue() + "");
//                    }
//                }

                if (payloadBean.getValue().gettHotWater() != null) {
                    yValuesRaw.add(Float.valueOf(payloadBean.getValue().gettHotWater().intValue()));
                }
                if (payloadBean.getValue().gettWarmWater() != null) {
                    yValuesCean.add(Float.valueOf(payloadBean.getValue().gettWarmWater().intValue()));
                }

                index++;
            }
        }

        Collections.reverse(xValues);
        Collections.reverse(yValuesRaw);
        Collections.reverse(yValuesCean);
        MPandroidChartHelper.setLineChart(getActivity(), mChartRaw, xValues, yValuesRaw);
        MPandroidChartHelper.setLineChart(getActivity(), mChartClean, xValues, yValuesCean);

    }

}
