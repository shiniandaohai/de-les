package com.boer.delos.activity.healthylife.pressure;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.healthylife.BaseHealthyLifeActivity;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.activity.healthylife.tool.HealthyResultDealWith;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.interf.ISimpleInterface2;
import com.boer.delos.model.PressureResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.boer.delos.utils.TimeUtil.formatStamp2Time;

/**
 * @author PengJiYang
 * @Description:血压 界面
 * create at 2016/5/26 17:30
 */
public class BloodPressureListeningActivity extends BaseHealthyLifeActivity
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener
        , ISimpleInterface2 {

    private TextView tvBloodPressure;
    private TextView tvBloodDate;
    private ImageView ivBloodHelp;
    private LineChart lcLineChart;
    private PieChart bloodChart;

    // 饼状图各个部分的颜色
    final int[] VORDIPLOM_COLORS = {
            Color.rgb(255, 48, 0), Color.rgb(76, 173, 16), Color.rgb(227, 150, 15)
    };

    // 显示在饼状图上的内容
    protected List<String> mPartiesX = new ArrayList<>();
    //    {
//        "偏高" + 0 + "次", "正常" + 0 + "次", "偏低" + 0 + "次"
//    };
    // 显示在饼状图上的内容
    protected List<Float> mPartiesY = new ArrayList<>();


    private List<PressureResult.PressureBean> mPressureLists;
    private ImageView mIcAvatar;
    private CheckedTextView mUserDrop;
    private TextView mTvUserName;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        mPressureLists = new ArrayList<>();

        initView();
        initLineChart();
        initPieChart();
        mPressureLists.addAll(HealthyResultDealWith.getmPressureList());
        calclatePressureData2Pie(mPressureLists);

        user = Constant.LOGIN_USER;
        queryMonthData(TimeUtil.getTimesMonthmorning(0) + "",
                TimeUtil.getCurrentstamp() + "", HealthController.HEATHY_PERSURE);
        settingHeader();
    }

    private void initView() {
        initTopBar(R.string.health_xue_ya, null, true, true);
        ivRight.setImageResource(R.drawable.ic_health_live_more);
        tvBloodPressure = (TextView) findViewById(R.id.tvBloodPressure);
        ivBloodHelp = (ImageView) findViewById(R.id.ivBloodHelp);
        lcLineChart = (LineChart) findViewById(R.id.lcLineChart);
        bloodChart = (PieChart) findViewById(R.id.bloodChart);

        mIcAvatar = (ImageView) findViewById(R.id.ic_avatar);
        mUserDrop = (CheckedTextView) findViewById(R.id.user_drop);
        mTvUserName = (TextView) findViewById(R.id.tv_userName);
        tvBloodDate = (TextView) findViewById(R.id.tvBloodDate);


        ivRight.setOnClickListener(this);
        ivBloodHelp.setOnClickListener(this);
    }

    //TODO 设置已分享健康给自己的用户 展示
    private void settingHeader() {
        showListPopup(mTvUserName);
        ImageLoader.getInstance().displayImage((URLConfig.HTTP + user.getAvatarUrl()).trim(),
                mIcAvatar, BaseApplication.getInstance().displayImageOptions);

        setSimpleInterface2(this);

        mTvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharefamilyList.size() == 0) {
                    return;
                }
                mUserDrop.toggle();
                mListPop.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRight:
//                toastUtils.showInfoWithStatus("更多");
                startActivity(new Intent(BloodPressureListeningActivity.this, BloodPressRecordListeningActivity.class));
                break;
            case R.id.ivBloodHelp:
//                toastUtils.showInfoWithStatus("帮助");
                Intent intent = new Intent(this, CurrentBloodPressListeningActivity.class);
                if (mPressureLists.size() != 0) {
                    PressureResult.PressureBean bean = mPressureLists.get(mPressureLists.size() - 1);

                    intent.putExtra("PressureH", bean.getValueH());
                    intent.putExtra("PressureL", bean.getValueL());
                    intent.putExtra("PressureP", bean.getBpm());
                    intent.putExtra("PressureT", bean.getMeasuretime());
                }
                startActivity(intent);

                break;
        }
    }

    /**
     * 初始化折线图
     */
    private void initLineChart() {
        lcLineChart.setDrawGridBackground(false);
        // no description text
        lcLineChart.setDescription("");
        lcLineChart.setNoDataTextDescription("You need to provide data for the chart.");
        lcLineChart.setTouchEnabled(false);
        lcLineChart.setDragEnabled(false);
        lcLineChart.setScaleEnabled(false);


        XAxis xAxis = lcLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = lcLineChart.getAxisLeft();
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
        lcLineChart.getAxisRight().setEnabled(false);

        setData2(HealthyResultDealWith.getmPressureData());

        lcLineChart.animateY(1500, Easing.EasingOption.Linear);
        lcLineChart.getLegend().setEnabled(false);
    }

    private void setData2(@NonNull ArrayList<String>[] pressureArray) {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yValsL = new ArrayList<Entry>();
        int i = 0;

        if (pressureArray != null && pressureArray.length != 0) {
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
        if (pressureArray != null && pressureArray.length != 0) {
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

        if (pressureArray.length != 0 && pressureArray[0].size() != 0 && mPressureLists.size() != 0) {

            String[] bpH = pressureArray[0].get(pressureArray.length - 1).split(",");
            String[] bpL = pressureArray[1].get(pressureArray.length - 1).split(",");

            tvBloodDate.setText(bpH[1] + "/" + bpL[1]);
            String time = formatStamp2Time(mPressureLists.get(mPressureLists.size() - 1).getMeasuretime(), "yyyy/MM/dd");
            tvBloodDate.setText(time);
        } else {
            tvBloodPressure.setText("0/0");
            tvBloodPressure.setText("");
        }

        // set data
        lcLineChart.setData(data);
        lcLineChart.invalidate();
    }

    /**
     * 初始化饼状图
     */
    private void initPieChart() {
        bloodChart.setUsePercentValues(true);
        bloodChart.setDescription("");
        bloodChart.setExtraOffsets(5, 10, 5, 5);

        bloodChart.setDragDecelerationFrictionCoef(0.95f);

//        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        bloodChart.setDrawHoleEnabled(true);
        bloodChart.setHoleColor(Color.WHITE);

        bloodChart.setTransparentCircleColor(Color.WHITE);
        bloodChart.setTransparentCircleAlpha(110);

        bloodChart.setHoleRadius(30f);
        bloodChart.setTransparentCircleRadius(61f);

        bloodChart.setDrawCenterText(true);

        bloodChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        bloodChart.setRotationEnabled(true);
        bloodChart.setHighlightPerTapEnabled(true);

        // lcLineChart.setUnit(" €");
        // lcLineChart.setDrawUnitsInChart(true);

        // add a selection listener
        bloodChart.setOnChartValueSelectedListener(this);


        bloodChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // lcLineChart.spin(2000, 0, 360);

        Legend l = bloodChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        l.setTextSize(12f);
        l.setTextColor(R.color.light_gray_line);
        l.setFormToTextSpace(5.f);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    private void calclatePressureData2Pie(List<PressureResult.PressureBean> list) {
        mPartiesX.clear();
        mPartiesY.clear();

        if (list == null || list.size() == 0) {
            mPartiesX.add("偏高" + 0 + "次");
            mPartiesX.add("正常" + 0 + "次");
            mPartiesX.add("偏低" + 0 + "次");

            mPartiesY.add(1 / 3f);
            mPartiesY.add(1 / 3f);
            mPartiesY.add(1 / 3f);

            setPieChartData(mPartiesX, mPartiesY);
            return;
        }
        float levelH = 0f;
        float levelN = 0f;
        float levelL = 0f;
        for (PressureResult.PressureBean bean : list) {
            int level = DealWithValues.judgeBloodPressureState(bean.getValueH(), bean.getValueL());
            switch (level) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    levelH++;
                    break;
                case 5:
                    levelN++;
                    break;
                case 6:
                    levelL++;
                    break;
            }
        }
        //"偏高" + 0 + "次"
        mPartiesX.add("偏高" + levelH + "次");
        mPartiesX.add("正常" + levelN + "次");
        mPartiesX.add("偏低" + levelL + "次");
        //默认7天
        mPartiesY.add(levelH / list.size());
        mPartiesY.add(levelN / list.size());
        mPartiesY.add(levelL / list.size());

        setPieChartData(mPartiesX, mPartiesY);

        tvBloodPressure.setText(list.get(list.size() - 1).getValueH()
                + "/" + list.get(list.size() - 1).getValueL());
    }

    private void setPieChartData(@NonNull List<String> mPartiesX, @NonNull List<Float> mPartiesY) {

        ArrayList<String> xVals = new ArrayList<>();
        for (String s : mPartiesX) {
            xVals.add(s);
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();
        int i = 0;
        for (float f : mPartiesY) {
            yVals1.add(new Entry(f, i));
            i++;
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        L.e("dataSet_Json===" + new Gson().toJson(dataSet));
        dataSet.setSliceSpace(0f);// 设置饼状图各个部分的间距
        dataSet.setSelectionShift(5f);
//        dataSet.setHighlightEnabled(true);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : VORDIPLOM_COLORS) {
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);// 设置饼状图各个部分的数据的颜色
//        data.setValueTypeface(tf);
        bloodChart.setData(data);

        // undo all highlights
        bloodChart.highlightValues(null);

        bloodChart.invalidate();
    }

    private void queryMonthData(String fromTime, final String toTime, String healthyType) {
        HealthController.getInstance().queryHealthyTime2TimeData(this, fromTime, toTime,
                healthyType, user.getId(), new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            Loger.d(json);
                            PressureResult result = GsonUtil.getObject(json, PressureResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg())
                                        ? "查询本月数据失败，请重试" : result.getMsg());
                                return;
                            }
                            mPressureLists = result.getData();
                            calclatePressureData2Pie(mPressureLists);
                            setData2(HealthyResultDealWith.dealWithPressureData(mPressureLists));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        Loger.d(json);
                    }
                });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

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
        queryMonthData(TimeUtil.getTimesMonthmorning(0) + "",
                TimeUtil.getCurrentstamp() + "", HealthController.HEATHY_PERSURE);
    }
}
