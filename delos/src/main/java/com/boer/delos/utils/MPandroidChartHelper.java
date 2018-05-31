package com.boer.delos.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/21 0021 14:16
 * @Modify:
 * @ModifyDate:
 */


public class MPandroidChartHelper {
    /**
     * 初始化LineChart
     *
     * @param mLineChart
     */
    public static void initLineChart(LineChart mLineChart, boolean isScale,int xCount) {
        mLineChart.setDrawGridBackground(false);
        // no description text
        mLineChart.setDescription("");
        mLineChart.setNoDataTextDescription("You need to provide data for the chart.");
        if (!isScale) {
            mLineChart.setTouchEnabled(false);
            mLineChart.setDragEnabled(false);
            mLineChart.setScaleEnabled(false);
        } else {
            mLineChart.setTouchEnabled(true);
            mLineChart.setDragEnabled(true);
//        barChart.setPinchZoom(true); //手势缩放
            mLineChart.setScaleXEnabled(true);
            mLineChart.setScaleYEnabled(false);
        }

        if(xCount>6){
            mLineChart.zoom(xCount/6.0f,1f,0f,0f);
        }

        mLineChart.setDragOffsetX(20f);
        mLineChart.calculateOffsets();
        ViewPortHandler viewPortHandler=mLineChart.getViewPortHandler();
        viewPortHandler.refresh(viewPortHandler.translate(new float[]{0,0}), mLineChart, false);
        mLineChart.postInvalidate();

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);


        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.setAxisMaxValue(34);
        leftAxis.setAxisMinValue(0);

        //虚线
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
//        leftAxis.setXOffset(20f);

        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.animateY(1500, Easing.EasingOption.Linear);
        mLineChart.getLegend().setEnabled(false);

    }

    public static void setLineChart(Context context, LineChart mLineChart, List<String> xVals,
                                    List<Float> yVals) {


        if (xVals == null) xVals = new ArrayList<>();
        if (yVals == null) yVals = new ArrayList<>();

        if (xVals.size() == 0 && yVals.size() == 0) {
            xVals.add("");
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();
        int key = 0;
        for (Float f : yVals) {
            yVals1.add(new Entry(f, key));
            key++;
        }

        LineDataSet set1 = new LineDataSet(yVals1, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(context.getResources().getColor(R.color.blue_text_water));
        set1.setCircleColor(context.getResources().getColor(R.color.blue_text_water));

        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(true);

        final DecimalFormat decimalFormat=new DecimalFormat("0.0");
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return decimalFormat.format(value);
            }
        });
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        //数值颜色
        data.setValueTextColor(context.getResources().getColor(R.color.gray_et_text));
        data.setValueTextSize(9f);

        // set data
        mLineChart.setData(data);
        mLineChart.invalidate();
    }

    /**
     * 初始化柱形图
     */
    public static void initBarChart(BarChart barChart) {
        barChart.setDrawGridBackground(false);
        barChart.setDescription("");
        barChart.setNoDataTextDescription("You need to provide data for the chart.");
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
//        barChart.setPinchZoom(true); //手势缩放
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
    }

    /**
     * 设置柱形图的数据
     */
    public static void setBarChartData(BarChart barChart, List<String> xVals, List<Float> yBarVal) {
        if (xVals == null) xVals = new ArrayList<>();
        if (yBarVal == null) yBarVal = new ArrayList<>();

        if (xVals.size() == 0 && yBarVal.size() == 0) {
            xVals.add("");
            yBarVal.add(0f);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        int key = 0;
        for (Float f : yBarVal) {
            yVals1.add(new BarEntry(f, key));
            key++;
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);
        //柱状图颜色
        set1.setColor(BaseApplication.getAppContext().getResources().getColor(R.color.green_weight_barchart));

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


}
