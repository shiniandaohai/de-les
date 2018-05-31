package com.boer.delos.activity.healthylife;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.pressure.BloodPressureListeningActivity;
import com.boer.delos.activity.healthylife.sugar.BloodSugarListeningActivity;
import com.boer.delos.activity.healthylife.tool.HealthyResultDealWith;
import com.boer.delos.activity.healthylife.urine.BaseUrineActivity;
import com.boer.delos.activity.healthylife.urine.UrinalysisHistoryActivity;
import com.boer.delos.activity.healthylife.urine.UrineResultListeningActivity;
import com.boer.delos.activity.healthylife.weight.ScaleDetailActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.interf.ISimpleInterface2;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.PressureResult;
import com.boer.delos.model.SugarResult;
import com.boer.delos.model.UrineResult;
import com.boer.delos.model.User;
import com.boer.delos.model.WeightBean;
import com.boer.delos.model.WeightResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.boer.delos.utils.TimeUtil.formatStamp2Time;

/**
 * @author wangkai
 * @Description:健康生活 界面
 * create at 2016/3/25 11:34
 */
public class HealthLifeListeningActivity extends BaseUrineActivity
        implements View.OnClickListener, ISimpleInterface2 {
    @Bind(R.id.tvTime1)
    TextView tvTime1;
    @Bind(R.id.tvNum1)
    TextView tvNum1;
    @Bind(R.id.tvTime2)
    TextView tvTime2;
    @Bind(R.id.tvNum2)
    TextView tvNum2;
    @Bind(R.id.tvTime3)
    TextView tvTime3;
    @Bind(R.id.tvNum3)
    TextView tvNum3;

    @Bind(R.id.tvUrineDate)
    TextView mTvUrineDate;
    @Bind(R.id.tvScore)
    TextView mTvScore;
    @Bind(R.id.iv_ph)
    ImageView mIvPh;
    @Bind(R.id.iv_pro)
    ImageView mIvPro;
    @Bind(R.id.iv_ubg)
    ImageView mIvUbg;
    @Bind(R.id.iv_nit)
    ImageView mIvNit;
    @Bind(R.id.iv_bld)
    ImageView mIvBld;
    @Bind(R.id.iv_leu)
    ImageView mIvLeu;
    @Bind(R.id.iv_weight)
    ImageView mIvWeight;
    @Bind(R.id.ic_urine_ketone)
    ImageView mIvUrineKetone;
    @Bind(R.id.iv_bilirubin)
    ImageView mIvBilirubin;
    @Bind(R.id.iv_glucose)
    ImageView mIvGlucose;
    @Bind(R.id.iv_vc)
    ImageView mIvVc;
    @Bind(R.id.tv_urineName)
    TextView mTvUrineName;

    @Bind(R.id.ic_avatar)
    ImageView mIcAvatar;
    @Bind(R.id.tv_userName)
    TextView mTvUserName;
    @Bind(R.id.user_drop)
    CheckedTextView mUserDrop;

    //https://github.com/PhilJay/MPAndroidChart
    private LineChart mChart1;
    private LineChart mChart2;
    private LineChart mChart3;

    private LinearLayout llBloodSugar, llBloodPressure, llHealthWeight, llUrineShow;
    private User user;
    private List<WeightBean> mWeightList;
    private List<PressureResult.PressureBean> mPressureList;
    private List<UrineResult.UrineBean> mUrineList;
    private List<SugarResult.SugarBean> mSugarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_life);
        ButterKnife.bind(this);
        initTopBar(R.string.health_life, R.string.health_device, true, true);
        initView();
        initData();
        initListener();

        queryHealthyShare();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSugarChart();
        initBPChart();
        initChartWeight();

        queryRecentHealth(HealthController.HEATHY_WEIGHT, "7");
        queryRecentHealth(HealthController.HEATHY_URINE, "1");
        queryRecentHealth(HealthController.HEATHY_PERSURE, "7");
        queryRecentHealth(HealthController.HEATHY_SUGAR, "7");
    }

    private void initData() {
        user = Constant.LOGIN_USER;
        if (user == null) {
            SharedPreferencesUtils.readUserInfoFromPreferences(this);
            user = Constant.LOGIN_USER;
        }

        mWeightList = new ArrayList<>();
        mPressureList = new ArrayList<>();
        mUrineList = new ArrayList<>();
        mSugarList = new ArrayList<>();

        showListPopup(mTvUserName);
        ImageLoader.getInstance().displayImage((URLConfig.HTTP + user.getAvatarUrl()).trim(),
                mIcAvatar, BaseApplication.getInstance().displayImageOptions);
    }

    private void initView() {
        mChart1 = (LineChart) findViewById(R.id.chart1);
        mChart2 = (LineChart) findViewById(R.id.chart2);
        mChart3 = (LineChart) findViewById(R.id.chart3);
        llBloodSugar = (LinearLayout) findViewById(R.id.llBloodSugar);
        llBloodPressure = (LinearLayout) findViewById(R.id.llBloodPressure);
        llHealthWeight = (LinearLayout) findViewById(R.id.llHealthWeight);
        llUrineShow = (LinearLayout) findViewById(R.id.ll_urine_show);
    }

    private void initListener() {
        tvRight.setOnClickListener(this);
        llBloodSugar.setOnClickListener(this);
        llBloodPressure.setOnClickListener(this);
        llHealthWeight.setOnClickListener(this);
        llUrineShow.setOnClickListener(this);

        setSimpleInterface2(this);

        mTvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharefamilyList.size() == 0) {
                    queryHealthyShare();
                    return;
                }
                mUserDrop.toggle();

                mListPop.show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tvRight:
                intent.setClass(this, IntelligentDeviceListeningActivity.class);
                startActivity(intent);
                break;
            case R.id.llBloodSugar:
                intent.setClass(this, BloodSugarListeningActivity.class);

                break;
            case R.id.llBloodPressure:
                intent.setClass(this, BloodPressureListeningActivity.class);
                break;
            case R.id.llHealthWeight:
                intent.setClass(this, ScaleDetailActivity.class);
                bundle.putSerializable("weight", (Serializable) mWeightList);
                intent.putExtras(bundle);
                break;
            case R.id.ll_urine_show:
                if (mUrineList.size() != 0) {
                    Map<String, String> dataMap = data2Map(mUrineList.get(mUrineList.size() - 1));
                    intent.putExtra("data", (Serializable) dataMap);
                    intent.setClass(this, UrineResultListeningActivity.class);
                } else {
                    intent.setClass(this, UrinalysisHistoryActivity.class);
                }
                break;
        }
        startActivity(intent);
    }

    private void initSugarChart() {
        mChart1.setDrawGridBackground(false);
        // no description text
        mChart1.setDescription("");
        mChart1.setNoDataTextDescription("You need to provide data for the chart.");
        mChart1.setTouchEnabled(false);
        mChart1.setDragEnabled(false);
        mChart1.setScaleEnabled(false);


        XAxis xAxis = mChart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mChart1.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(34);
        leftAxis.setAxisMinValue(0);

        //虚线
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        mChart1.getAxisRight().setEnabled(false);
        // add data
        setSugarData(mSugarList);
        mChart1.animateY(1500, Easing.EasingOption.Linear);
        mChart1.getLegend().setEnabled(false);
    }

    private void setSugarData(List<SugarResult.SugarBean> list) {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        if (list.size() == 0) {
            xVals.add("0");
            yVals1.add(new Entry(0, 1));
        }
        int i = 0;
        for (SugarResult.SugarBean bean : list) {
            String time = formatStamp2Time(bean.getMesuredate(), "MM.dd");
            xVals.add(time);
            yVals1.add(new Entry(bean.getValue(), i));
            i++;
        }
        LineDataSet set1 = new LineDataSet(yVals1, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.parseColor("#4CC578"));
        set1.setCircleColor(Color.parseColor("#4CC578"));
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        //数值颜色
        data.setValueTextColor(Color.parseColor("#9c9c9c"));
        data.setValueTextSize(9f);

        // set data
        mChart1.setData(data);
        mChart1.invalidate();
        if (list.size() != 0) {
            int d = list.get(list.size() - 1).getMesuredate();
            String time = formatStamp2Time(d, "yyyy/MM/dd");
            tvTime1.setText(time);
            tvNum1.setText(String.valueOf(list.get(list.size() - 1).getValue()));
        } else {
            tvTime1.setText("");
            tvNum1.setText("");
        }
    }

    private void initBPChart() {
        mChart2.setDrawGridBackground(false);
        // no description text
        mChart2.setDescription("");
        mChart2.setNoDataTextDescription("You need to provide data for the chart.");
        mChart2.setTouchEnabled(false);
        mChart2.setDragEnabled(false);
        mChart2.setScaleEnabled(false);

        XAxis xAxis = mChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mChart2.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(250);
        leftAxis.setAxisMinValue(0);

        LimitLine ll1 = new LimitLine(120, "");
        ll1.setLineWidth(2f);
        ll1.setLineColor(Color.parseColor("#FF3103"));
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(80f, "");
        ll2.setLineWidth(2f);
        ll2.setLineColor(Color.parseColor("#0183E1"));
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
        mChart2.getAxisRight().setEnabled(false);
        // add data
        setPressureData(new ArrayList[]{});
        mChart2.animateY(1500, Easing.EasingOption.Linear);
        mChart2.getLegend().setEnabled(false);
    }

    private void setPressureData(@NonNull ArrayList<String>[] pressureArray) {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yValsL = new ArrayList<Entry>();
        int i = 0;

        if (pressureArray.length != 0) {
            for (String s : pressureArray[1]) {
                String[] result = s.split(",");
                xVals.add(result[0]);
                float yH = Float.valueOf(result[1]);
                yValsL.add(new Entry(yH, i));
                i++;
            }
        }

        LineDataSet set1 = new LineDataSet(yValsL, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.parseColor("#4CC578"));
        set1.setCircleColor(Color.parseColor("#4CC578"));
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);
        ArrayList<Entry> yValsH = new ArrayList<Entry>();

        i = 0;
        if (pressureArray.length != 0) {
            for (String s : pressureArray[0]) {
                String[] result = s.split(",");
                float yL = Float.valueOf(result[1]);
                yValsH.add(new Entry(yL, i));
                i++;
            }
        }
        //没有数据时
        if (pressureArray.length == 0) {
            xVals.add("");
            yValsL.add(new Entry(0, 1));
            yValsH.add(new Entry(0, 1));
        }

        LineDataSet set2 = new LineDataSet(yValsH, "DataSet 2");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(Color.parseColor("#F55B4F"));
        set2.setCircleColor(Color.parseColor("#F55B4F"));
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
        if (pressureArray.length != 0 && pressureArray[0].size() != 0) {

            String[] bpH = pressureArray[0].get(pressureArray.length - 1).split(",");
            String[] bpL = pressureArray[1].get(pressureArray.length - 1).split(",");

            tvNum2.setText(bpH[1] + "/" + bpL[1]);
            String time = formatStamp2Time(mPressureList.get(mPressureList.size() - 1).getMeasuretime(), "yyyy/MM/dd");
            tvTime2.setText(time);
        } else {
            tvNum2.setText("0/0");
            tvTime2.setText("");
        }
        // set data
        mChart2.setData(data);
        mChart2.invalidate();
    }

    private void initChartWeight() {
        mChart3.setDrawGridBackground(false);
        // no description text
        mChart3.setDescription("");
        mChart3.setNoDataTextDescription("You need to provide data for the chart.");
        mChart3.setTouchEnabled(false);
        mChart3.setDragEnabled(false);
        mChart3.setScaleEnabled(false);

        XAxis xAxis = mChart3.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(0);
        YAxis leftAxis = mChart3.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinValue(0);
        //虚线
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        mChart3.getAxisRight().setEnabled(false);
        // add data
        setDataWeight(Collections.<String>emptyList());
        mChart3.animateY(1500, Easing.EasingOption.Linear);
        mChart3.getLegend().setEnabled(false);
    }

    /**
     * 体重
     */
    private void setDataWeight(List<String> weightList) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < weightList.size(); i++) {

            String[] temp = weightList.get(i).split(" ");
            String[] date = temp[0].split("-");
            xVals.add(date[1] + "/" + date[2]); //HH-MM
            yVals1.add(new Entry(Float.valueOf(temp[2]), i));
        }
        LineDataSet set1 = new LineDataSet(yVals1, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.parseColor("#4CC578"));
        set1.setCircleColor(Color.parseColor("#4CC578"));
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        final LineData data = new LineData(xVals, dataSets);
        //数值颜色
        data.setValueTextColor(Color.parseColor("#9c9c9c"));
        data.setValueTextSize(9f);

        mChart3.setData(data);
        mChart3.invalidate();

    }

    /**
     * 请求健康数据
     */
    private void queryRecentHealth(final String healthyType, String recent) {
        Calendar c = Calendar.getInstance();
        long millTime = c.getTimeInMillis() / 1000;
        if (user == null) {
            user = Constant.LOGIN_USER;
        }
        HealthController.getInstance().queryRecentHealth(this, millTime + "", healthyType, recent, user.getId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i(Json);
                try {
                    BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showInfoWithStatus("健康数据，请求错误");
                        return;
                    }
                    switch (healthyType) {
                        case HealthController.HEATHY_SUGAR:
                            SugarResult sugarResult = GsonUtil.getObject(Json, SugarResult.class);
                            mSugarList.clear();
                            mSugarList.addAll(sugarResult.getData());
                            Collections.reverse(mSugarList);

                            setSugarData(mSugarList);

                            break;
                        case HealthController.HEATHY_PERSURE:
                            PressureResult pressureResult = GsonUtil.getObject(Json, PressureResult.class);
                            mPressureList.clear();
                            mPressureList.addAll(pressureResult.getData());

                            ArrayList[] pressures = HealthyResultDealWith.dealWithPressureData(mPressureList);

                            if (pressures != null) {
                                setPressureData(pressures);
                            }
                            break;

                        case HealthController.HEATHY_WEIGHT:
                            WeightResult Weight = new Gson().fromJson(Json, WeightResult.class);
                            mWeightList.clear();
                            mWeightList.addAll(Weight.getData());
                            List<String> weightLists = HealthyResultDealWith.dealWeightData(mWeightList);
                            setDataWeight(weightLists);
                            if (mWeightList.size() == 0) {
                                tvNum3.setText("0");
                                tvTime3.setText("");
                            } else {
                                tvNum3.setText(String.valueOf(mWeightList.get(mWeightList.size() - 1).getWeight()));
                                String time = TimeUtil.formatStamp2Time(mWeightList.get(mWeightList.size() - 1).getMeasuretime(), "yyyy/MM/dd");
                                tvTime3.setText(time);
                            }
                            break;
                        case HealthController.HEATHY_URINE:
                            UrineResult urineResult = GsonUtil.getObject(Json, UrineResult.class);
                            mUrineList.clear();
                            mUrineList.addAll(urineResult.getData());

                            settingUrineShow(mUrineList);
                            break;

                    }

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


    private void settingUrineShow(List<UrineResult.UrineBean> list) {
        if (list == null || list.size() == 0) {
            judgeIndexMap.clear();
            mTvUrineDate.setText("");
            mTvScore.setText("0");
            mIvPh.setImageResource(R.color.white);
            mIvPro.setImageResource(R.color.white);
            mIvUbg.setImageResource(R.color.white);
            mIvNit.setImageResource(R.color.white);
            mIvBld.setImageResource(R.color.white);
            mIvLeu.setImageResource(R.color.white);
            mIvWeight.setImageResource(R.color.white);
            mIvUrineKetone.setImageResource(R.color.white);
            mIvBilirubin.setImageResource(R.color.white);
            mIvGlucose.setImageResource(R.color.white);
            mIvVc.setImageResource(R.color.white);
            return;
        }
        UrineResult.UrineBean bean = list.get(list.size() - 1);
        String time = formatStamp2Time(bean.getMeasuretime(), "yyyy/MM/dd");
        mTvUrineDate.setText(time);
        calculateData(data2Map(bean));
        mTvScore.setText(String.valueOf(getUrineNumber()));
        mTvUrineName.setTextColor(getResources().getColor(getUrineNumber() > SCORE_STANDARD
                ? R.color.healthy_normal : R.color.healthy_abnormal));

        // LEU = "白细胞（LEU）";
        // NIT = "亚硝酸盐（NIT）";
        // UBG = "尿胆原（UBG）";
        // PRO = "尿蛋白（PRO）";
        // PH = "PH值（PH）";
        // BLD = "潜血（BLD）";
        // SG = "比重（SG）";
        // KET = "酮体（KET）";
        // BIL = "胆红素（BIL）";
        // GLU = "葡萄糖（GLU）";
        // VC = "维生素C（VC）";
        settingUrineState2(PH, mIvPh);
        settingUrineState(PRO, mIvPro);
        settingUrineState(UBG, mIvUbg);
        settingUrineState(NIT, mIvNit);

        settingUrineState(BLD, mIvBld);
        settingUrineState(LEU, mIvLeu);
        settingUrineState2(SG, mIvWeight);
        settingUrineState(KET, mIvUrineKetone);

        settingUrineState(BIL, mIvBilirubin);
        settingUrineState(GLU, mIvGlucose);
        settingUrineState(VC, mIvVc);

    }

    private void settingUrineState(String type, ImageView imageView) {
        if (judgeIndexMap == null || judgeIndexMap.size() == 0) {
            imageView.setImageResource(R.color.white);
        }
        imageView.setImageResource(judgeIndexMap.get(type).equals("1")
                ? R.color.white : R.mipmap.up);
    }

    private void settingUrineState2(String type, ImageView imageView) {
        if (judgeIndexMap == null || judgeIndexMap.size() == 0) {
            imageView.setImageResource(R.color.white);
        }
        switch (judgeIndexMap.get(type)) {
            case "1":
                imageView.setImageResource(R.mipmap.down);
                break;
            case "2":
                imageView.setImageResource(R.color.white);
                break;
            case "3":
                imageView.setImageResource(R.mipmap.up);
                break;
        }
    }


    @Override
    public void clickListener(String tag) {

    }

    @Override
    public void clickListener2(int pos) {
        mUserDrop.toggle();

        if (user == mSharefamilyList.get(pos).getUser()) {
            return;
        }
        user = mSharefamilyList.get(pos).getUser();
        if (user.getId().equals(Constant.USERID)) {
            mTvUserName.setText("我");
        } else {
            String name = mSharefamilyList.get(pos).getUserAlias();
            if (StringUtil.isEmpty(name)) {
                name = user.getName();
                if (StringUtil.isEmpty(name)) {
                    name = user.getMobile();
                }
            }
            mTvUserName.setText(name);
        }
        ImageLoader.getInstance().displayImage((URLConfig.HTTP + user.getAvatarUrl()).trim(),
                mIcAvatar, BaseApplication.getInstance().displayImageOptions);

        queryRecentHealth(HealthController.HEATHY_WEIGHT, "7");
        queryRecentHealth(HealthController.HEATHY_URINE, "1");
        queryRecentHealth(HealthController.HEATHY_PERSURE, "7");
        queryRecentHealth(HealthController.HEATHY_SUGAR, "7");
    }

}


