package com.boer.delos.activity.healthylife.pressure;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.PressureResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.TimeUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:血压
 * @CreateDate: 2017/4/17 0017 19:27
 * @Modify:
 * @ModifyDate:
 */


public class BloodPressureActivity extends CommonBaseActivity {
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.tv_bp_H)
    TextView mTvBpH;
    @Bind(R.id.tv_bp_L)
    TextView mTvBpL;
    @Bind(R.id.tv_blood_rate)
    TextView mTvHeartRate;
    @Bind(R.id.lineChart)
    LineChart mLineChart;
    @Bind(R.id.btn_auto_measure)
    Button mBtnAutoMeasure;
    @Bind(R.id.btn_hand_input)
    Button mBtnHandInput;
    @Bind(R.id.btn_history_record)
    Button mBtnHistoryRecord;
    @Bind(R.id.btn_major_info)
    Button mBtnMajorInfo;

    private User mUser; //所选用户
    private List<PressureResult.PressureBean> mPressureList;

    @Override
    protected int initLayout() {

        return R.layout.activity_blood_pressure_new;
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryRecentHealth(HealthController.HEATHY_PERSURE, "7");

    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.health_blood_pressure));
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }
    }

    @Override
    protected void initData() {
        mPressureList = new ArrayList<>();
        initLineChart();
    }

    @Override
    protected void initAction() {

    }

    @OnClick({R.id.btn_auto_measure, R.id.btn_hand_input, R.id.btn_history_record, R.id.btn_major_info})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_auto_measure:
                if (!TextUtils.isEmpty(mUser.getId())
                        && !TextUtils.isEmpty(Constant.USERID)
                        && mUser.getId().equals(Constant.USERID)) {
                    intent.setClass(this, BloodPressureConnActivity.class);
                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_change_to_current_user));
                    return;
                }

                break;
            case R.id.btn_hand_input:
                if (!TextUtils.isEmpty(mUser.getId())
                        && !TextUtils.isEmpty(Constant.USERID)
                        && mUser.getId().equals(Constant.USERID)) {
                    intent.setClass(this, BloodPressureInputActivity.class);
                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_change_to_current_user));
                    return;
                }

                break;
            case R.id.btn_history_record:
                intent.setClass(this, BloodPressuRecordActivity.class);
                break;
            case R.id.btn_major_info:
                intent.setClass(this, BloodPressureMajorActivity.class);
                break;
        }
        startActivity(intent);
    }


    /**
     * 请求健康数据
     */
    private void queryRecentHealth(final String healthyType, String recent) {
        Calendar c = Calendar.getInstance();
        long millTime = c.getTimeInMillis() / 1000;

        HealthController.getInstance().queryRecentHealth(this, millTime + "",
                healthyType, recent, mUser.getId(), new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.i(Json);
                        try {
                            BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showInfoWithStatus(getString(R.string.toast_error_net));
                                return;
                            }
                            PressureResult pressureResult = GsonUtil.getObject(Json, PressureResult.class);
                            mPressureList.clear();
                            mPressureList.addAll(pressureResult.getData());
                            Collections.reverse(mPressureList);
                            setPressureData(mPressureList);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        L.i(Json);
                    }
                });
    }


    /**
     * 初始化折线图
     */
    private void initLineChart() {
        mLineChart.setDrawGridBackground(false);
        // no description text
        mLineChart.setDescription("");
        mLineChart.setNoDataTextDescription("You need to provide data for the chart.");
        mLineChart.setTouchEnabled(false);
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);


        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(getResources().getColor(R.color.gray_et_text));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(250);
        leftAxis.setAxisMinValue(0);
        leftAxis.setTextColor(getResources().getColor(R.color.gray_et_text));
        leftAxis.setTextSize(10f);

        LimitLine ll1 = new LimitLine(120, "");
        ll1.setLineWidth(2f);
        ll1.setLineColor(getResources().getColor(R.color.gray_text_delete));
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(80f, "");
        ll2.setLineWidth(2f);
        ll2.setLineColor(getResources().getColor(R.color.blue_text_water));
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);

        //虚线
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        mLineChart.getAxisRight().setEnabled(false);


        setPressureData(null);

        mLineChart.animateY(1500, Easing.EasingOption.Linear);
        mLineChart.getLegend().setEnabled(false);
    }

    private void setPressureData(@NonNull List<PressureResult.PressureBean> mPressureList) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yValsL = new ArrayList<Entry>();
        ArrayList<Entry> yValsH = new ArrayList<Entry>();

        if (mPressureList == null) {
            mPressureList = new ArrayList<>();
        }
        int index = 0;
        xVals.add("");

        for (PressureResult.PressureBean bean : mPressureList) {
            String time = TimeUtil.formatStamp2Time(bean.getMeasuretime(), "MM.dd");
            xVals.add(time);
            index++;
            yValsH.add(new Entry(bean.getValueH(), index));
            yValsL.add(new Entry(bean.getValueL(), index));


            if (index == mPressureList.size()) {
                mTvDate.setText(TimeUtil.formatStamp2Time(bean.getMeasuretime(), "yyyy/MM/dd HH:mm"));
                mTvBpH.setText(bean.getValueH() + "");
                mTvBpL.setText(bean.getValueL() + "");
                mTvHeartRate.setText(bean.getBpm() + "");
                DealWithValue2.judgeBPColor(this, mTvState, bean.getValueH(), bean.getValueL(), false);
            }
        }

        if (mPressureList.size() == 0) {
            xVals.add("");
            yValsH.add(new Entry(0f, 0));
            yValsL.add(new Entry(0f, 0));
        }
        LineDataSet set1 = new LineDataSet(yValsL, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.parseColor("#4CC578"));
        set1.setCircleColor(Color.parseColor("#4CC578"));
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);

        LineDataSet set2 = new LineDataSet(yValsH, "DataSet 2");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(getResources().getColor(R.color.gray_text_delete));
        set2.setCircleColor(getResources().getColor(R.color.gray_text_delete));
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setDrawCircleHole(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set2); // add the datasets
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        //数值颜色
        data.setValueTextColor(Color.parseColor("#9c9c9c"));
        data.setValueTextSize(9f);


        // set data
        mLineChart.setData(data);
        mLineChart.invalidate();
    }
}
