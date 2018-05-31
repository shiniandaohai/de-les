package com.boer.delos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.WaterCleanDetailActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Addr;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.Time;
import com.boer.delos.model.WaterTDSResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.green.GreenLiveController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.MPandroidChartHelper;
import com.boer.delos.utils.TimeUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/12 0012 14:51
 * @Modify:
 * @ModifyDate:
 */


public class WaterTDSFragment extends LazyFragment
        implements IObjectInterface<List<WaterTDSResult.WaterBean>> {
    @Bind(R.id.chart_raw)
    LineChart mChartRaw;
    @Bind(R.id.chart_clean)
    LineChart mChartClean;

    private View rootView;
    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    private List<String> xVals1;
    private List<Float> yVals1;

    private List<String> xVals2;
    private List<Float> yVals2;
    private List<Addr> mAddrList;

    public static WaterTDSFragment newInstance(DeviceRelate d) {

        Bundle args = new Bundle();
        args.putSerializable("device", d);
        WaterTDSFragment fragment = new WaterTDSFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_water_tds, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        return rootView;
    }


    private void initView() {
        mDeviceRelate = (DeviceRelate) getArguments().getSerializable("device");

        xVals1 = new ArrayList<>();
        yVals1 = new ArrayList<>();
        xVals2 = new ArrayList<>();
        yVals2 = new ArrayList<>();


        MPandroidChartHelper.initLineChart(mChartRaw, true,7);
        MPandroidChartHelper.initLineChart(mChartClean, true,7);
    }

    private void updateUI(List<WaterTDSResult.WaterBean> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        List<String> xValues = new ArrayList<>();
        List<Float> yValuesRaw = new ArrayList<>();
        List<Float> yValuesCean = new ArrayList<>();
        for (int i=0;i<7;i++) {
            WaterTDSResult.WaterBean waterBean = list.get(i);
            if (!TextUtils.isEmpty(waterBean.getPayload())) {
                WaterTDSResult.PayloadBean payloadBean = GsonUtil.getObject(waterBean.getPayload(),
                        WaterTDSResult.PayloadBean.class);
                String time = TimeUtil.formatStamp2Time(waterBean.getTime(), "MM-dd");
                xValues.add(time);

                if (payloadBean.getValue() == null) {
                    continue;
                }
                if (payloadBean.getValue().getRawTDS() != null) {
                    yValuesRaw.add(Float.valueOf(payloadBean.getValue().getRawTDS().intValue()));
                }
                if (payloadBean.getValue().getPureTDS() != null) {
                    yValuesCean.add(Float.valueOf(payloadBean.getValue().getPureTDS().intValue()));
                }
            }
        }

        Collections.reverse(xValues);
        Collections.reverse(yValuesRaw);
        Collections.reverse(yValuesCean);
        MPandroidChartHelper.setLineChart(getActivity(), mChartRaw, xValues, yValuesRaw);
        MPandroidChartHelper.setLineChart(getActivity(), mChartClean, xValues, yValuesCean);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onClickListenerOK(List<WaterTDSResult.WaterBean> waterBeen, int pos, String tag) {
        if (!TextUtils.isEmpty(tag) && tag.equals("0")) {
            updateUI(waterBeen);
        } else {
            MPandroidChartHelper.setLineChart(getActivity(), mChartRaw, null, null);
            MPandroidChartHelper.setLineChart(getActivity(), mChartClean, null, null);
        }
    }
}
