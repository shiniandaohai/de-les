package com.boer.delos.activity.greenlive;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

import static com.bigkoo.pickerview.view.WheelTime.dateFormat;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/2 0002 15:02
 * @Modify:
 * @ModifyDate:
 */


public abstract class GreenLiveBaseActivity extends BaseActivity {
    protected int year, month, day;
    protected List<String> dayList, monthList, yearList;

    protected RecyclerView rvElectricityMonth, rvElectricityYear, rvElectricityDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initDate();

    }

    private void initDate() {
        Calendar cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM");
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);// 月份从0开始
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    private void setDayList() {
        // 判断年份和月份是否是当前年份或月份
        dayList.clear();
        Calendar calendar = Calendar.getInstance();
        int days;
        if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH) + 1) {
            days = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            try {
                Date date = dateFormat.parse(year + "-" + (month + 1));
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        for (int i = 1; i < days + 1; i++) {
            dayList.add(i + "日");
        }

//        setYearMonthDay(rvElectricityMonth, rvElectricityYear, rvElectricityDay,
//                tvElectricMonth, tvElectricYear, tvElectricDay);
//        dayAdapter.setDatas(dayList);
//        dayAdapter.settingChoise(dayList.size() - 1);
//        rvElectricityDay.scrollToPosition(dayList.size() - 1);
//        dayAdapter.notifyDataSetChanged();

    }

    private void setMonthList() {
        monthList.clear();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if (year < currentYear) {
            for (int i = 1; i <= 12; i++) {
                monthList.add(i + "月");
            }
        } else {
            int currentMonth = calendar.get(Calendar.MONTH);
            for (int i = 1; i <= currentMonth + 1; i++) {
                monthList.add(i + "月");
            }
        }

//        setYearMonthDay(rvElectricityDay, rvElectricityYear, rvElectricityMonth, tvElectricDay, tvElectricYear, tvElectricMonth);
//        monthAdapter.setDatas(monthList);
//        monthAdapter.settingChoise(monthList.size() - 1);
//        monthAdapter.notifyDataSetChanged();
        rvElectricityMonth.scrollToPosition(monthList.size() - 1);
    }

    private void setYearList() {
        yearList.clear();
        Calendar calendar = Calendar.getInstance();
        for (int i = calendar.get(Calendar.YEAR); i >= 2015; i--) {
            yearList.add("" + i);
        }

//        yearAdapter.setDatas(yearList);
//        yearAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化柱形图
     */
    private void initBarChart(BarChart barChart) {
        barChart.setDrawGridBackground(false);
        // no description text
        barChart.setDescription("");
        barChart.setNoDataTextDescription("You need to provide data for the chart.");
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setPinchZoom(true); //手势缩放
        barChart.setScaleXEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setDrawBarShadow(true); // 灰色背景


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#000000"));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setLabelCount(4, false);
        leftAxis.setAxisMinValue(0);
        leftAxis.setTextColor(Color.parseColor("#000000"));
        leftAxis.setDrawGridLines(false);
        //虚线
//        leftAxis.enableGridDashedLine(0f, 0f, 0f);
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        barChart.getAxisRight().setEnabled(false);
        // add data
//        barChart.animateY(1500, Easing.EasingOption.Linear);
        barChart.getLegend().setEnabled(false);

        List<Float> yBarVal = new ArrayList<>();
        yBarVal.add(0.0f);
        yBarVal.add(0.0f);
        yBarVal.add(0.0f);
        setBarChartData(barChart, new ArrayList<String>(), yBarVal);
    }

    /**
     * 设置柱形图的数据
     */
    private void setBarChartData(BarChart barChart, List<String> xVals, List<Float> yBarVal) {
        if (xVals == null) xVals = new ArrayList<>();
        if (xVals.size() == 0) {
            xVals.add("");
            xVals.add("");
            xVals.add("");
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        int key = 0;
        for (Float f : yBarVal) {
            yVals1.add(new BarEntry(f, key));
            key++;
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);
        set1.setColor(getResources().getColor(R.color.blue_text));
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.parseColor("#9c9c9c"));
        set1.setBarShadowColor(Color.parseColor("#109d9d9d"));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        barChart.setData(data);
        barChart.getData().notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
        barChart.animateY(1400);
    }

    private void setYearMonthDay(RecyclerView rvFirstUnchecked, RecyclerView rvSecondUnchecked,
                                 RecyclerView rvChecked, TextView tvFirstUnchecked,
                                 TextView tvSecondUnchecked, TextView tvChecked) {
        rvFirstUnchecked.setVisibility(View.GONE);
        rvSecondUnchecked.setVisibility(View.GONE);
        rvChecked.setVisibility(View.VISIBLE);
        tvFirstUnchecked.setBackgroundResource(R.drawable.shape_circle_blue);
        tvFirstUnchecked.setTextColor(Color.parseColor("#128ce3"));
        tvSecondUnchecked.setBackgroundResource(R.drawable.shape_circle_blue);
        tvSecondUnchecked.setTextColor(Color.parseColor("#128ce3"));
        tvChecked.setBackgroundResource(R.drawable.shape_double_circle_blue);
        tvChecked.setTextColor(Color.parseColor("#ffffff"));
    }

    /**
     * 初始化折线表格
     */
    private void initLineChart(LineChart lineChart) {
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("You need to provide data for the chart."); // no description text
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setDrawLimitLinesBehindData(true);

        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(6);
        leftAxis.setAxisMinValue(0);
//        leftAxis.setSpaceBottom(20f);
        //虚线
        leftAxis.enableGridDashedLine(0f, 100f, 0f);
        leftAxis.setDrawZeroLine(true);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        lineChart.getAxisRight().setEnabled(false);
        // add data
        List<Float> yVal = new ArrayList<>();
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        setLineChartData(lineChart, 5, yVal);
        lineChart.animateY(1500, Easing.EasingOption.Linear);
        lineChart.getLegend().setEnabled(false);
    }

    /**
     * 给表格设置数据
     *
     * @param count 横坐标的个数
     * @param yVal  纵坐标集合
     */
    private void setLineChartData(LineChart lineChart, int count, List<Float> yVal) {

        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("");
        xVals.add("0:30");
        xVals.add("1:00");
        xVals.add("1:30");
        xVals.add("2:00");
        xVals.add("2:30");

        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            yVals1.add(new Entry(yVal.get(i), i + 1));
        }

        LineDataSet set1 = new LineDataSet(yVals1, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.parseColor("#128CE3"));
        set1.setCircleColor(Color.parseColor("#128CE3"));
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
        lineChart.setData(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
