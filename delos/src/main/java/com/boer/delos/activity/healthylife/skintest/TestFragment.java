package com.boer.delos.activity.healthylife.skintest;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boer.delos.R;
import com.boer.delos.model.SkinDetailChart;
import com.boer.delos.utils.TimeUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.boer.delos.utils.TimeUtil.formatStamp2Time;

public final class TestFragment extends Fragment {
    private String[] CONTENT;
    @Bind(R.id.lcLineChartWater)
    LineChart lcLineChartWater;
    @Bind(R.id.lcLineChartOil)
    LineChart lcLineChartOil;
    private List<SkinDetailChart> sKinAreaDetails;
    private int pos;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_skin_chart, null);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            sKinAreaDetails = (List<SkinDetailChart>) bundle.getSerializable("list");
            pos = bundle.getInt("pos");
            Log.v("gl", "pos===" + pos);
        }


        CONTENT = getResources().getStringArray(R.array.listSkin);

        initAction();
        return view;

    }

    private void initAction() {


        initLineChartOil();
        initLineChartWater();
        setSkinData(sKinAreaDetails);

    }

    /**
     * 初始化折线图
     */
    private void initLineChartOil() {
        lcLineChartOil.setDrawGridBackground(false);
        // no description text
        lcLineChartOil.setDescription("油分");
        lcLineChartOil.setNoDataTextDescription("You need to provide data for the chart.");
        lcLineChartOil.setTouchEnabled(false);
        lcLineChartOil.setDragEnabled(false);
        lcLineChartOil.setScaleEnabled(false);


        XAxis xAxis = lcLineChartOil.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(3);
        xAxis.resetLabelsToSkip();


        YAxis leftAxis = lcLineChartOil.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(100);
        leftAxis.setAxisMinValue(0);

        //虚线
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        lcLineChartOil.getAxisRight().setEnabled(false);

        lcLineChartOil.animateY(1500, Easing.EasingOption.Linear);
        lcLineChartOil.getLegend().setEnabled(false);
    }


    /**
     * 初始化折线图
     */
    private void initLineChartWater() {
        lcLineChartWater.setDrawGridBackground(false);
        // no description text
        lcLineChartWater.setDescription("水分");

        lcLineChartWater.setNoDataTextDescription("You need to provide data for the chart.");
        lcLineChartWater.setTouchEnabled(true);
        lcLineChartWater.setDragEnabled(true);
        lcLineChartWater.setScaleEnabled(true);



        XAxis xAxis = lcLineChartWater.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(3);
        xAxis.resetLabelsToSkip();

        YAxis leftAxis = lcLineChartWater.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(100);
        leftAxis.setAxisMinValue(0);


        //虚线
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        lcLineChartWater.getAxisRight().setEnabled(false);

        lcLineChartWater.animateY(1500, Easing.EasingOption.Linear);
        lcLineChartWater.getLegend().setEnabled(false);
    }


    public void setSkinData(List<SkinDetailChart> list) {
        sKinAreaDetails = list;
        invalidateChar();

        setDesPosition(lcLineChartWater);
        setDesPosition(lcLineChartOil);
    }

    private void setDesPosition(LineChart lineChart){
        float left=lineChart.getViewPortHandler().offsetLeft();
        float top=lineChart.getViewPortHandler().offsetTop();
        Paint paint=lineChart.getPaint(LineChart.PAINT_DESCRIPTION);
        float textWidth=paint.measureText("水分");
        float x=left+textWidth;
        float y=top;
        lineChart.setDescriptionPosition(x, y);
    }

    public void invalidateChar() {

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<Entry> yValsOil = new ArrayList<>();

        if (sKinAreaDetails == null)
            return;

        Log.v("gl", "sKinAreaDetails===" + sKinAreaDetails.size());


        int i = 0;

        for (SkinDetailChart bean : sKinAreaDetails) {

            Log.v("gl", bean.getMeasuretime() + "==============" + bean.getSkin_brow().getWater());
            String time = formatStamp2Time(Long.parseLong(bean.getMeasuretime()), "MM.dd");

            xVals.add(time);

            switch (pos) {
                case 0:
                    yVals1.add(new Entry(Float.parseFloat(bean.getSkin_brow().getWater().contains("%") ? bean.getSkin_brow().getWater().replace("%", "") : bean.getSkin_brow().getWater()), i));
                    yValsOil.add(new Entry(Float.parseFloat(bean.getSkin_brow().getGrease().contains("%") ? bean.getSkin_brow().getGrease().replace("%", "") : bean.getSkin_brow().getGrease()), i));
                    break;
                case 1:
                    yVals1.add(new Entry(Float.parseFloat(bean.getSkin_eye().getWater().contains("%") ? bean.getSkin_eye().getWater().replace("%", "") : bean.getSkin_eye().getWater()), i));
                    yValsOil.add(new Entry(Float.parseFloat(bean.getSkin_eye().getGrease().contains("%") ? bean.getSkin_eye().getGrease().replace("%", "") : bean.getSkin_eye().getGrease()), i));
                    break;
                case 2:
                    yVals1.add(new Entry(Float.parseFloat(bean.getSkin_hand().getWater().contains("%") ? bean.getSkin_hand().getWater().replace("%", "") : bean.getSkin_hand().getWater()), i));
                    yValsOil.add(new Entry(Float.parseFloat(bean.getSkin_hand().getGrease().contains("%") ? bean.getSkin_hand().getGrease().replace("%", "") : bean.getSkin_hand().getGrease()), i));
                    break;
                case 3:
                    yVals1.add(new Entry(Float.parseFloat(bean.getSkin_left().getWater().contains("%") ? bean.getSkin_left().getWater().replace("%", "") : bean.getSkin_left().getWater()), i));
                    yValsOil.add(new Entry(Float.parseFloat(bean.getSkin_left().getGrease().contains("%") ? bean.getSkin_left().getGrease().replace("%", "") : bean.getSkin_left().getGrease()), i));
                    break;
                case 4:
                    yVals1.add(new Entry(Float.parseFloat(bean.getSkin_nose().getWater().contains("%") ? bean.getSkin_nose().getWater().replace("%", "") : bean.getSkin_nose().getWater()), i));
                    yValsOil.add(new Entry(Float.parseFloat(bean.getSkin_nose().getGrease().contains("%") ? bean.getSkin_nose().getGrease().replace("%", "") : bean.getSkin_nose().getGrease()), i));
                    break;
                case 5:
                    yVals1.add(new Entry(Float.parseFloat(bean.getSkin_right().getWater().contains("%") ? bean.getSkin_right().getWater().replace("%", "") : bean.getSkin_right().getWater()), i));
                    yValsOil.add(new Entry(Float.parseFloat(bean.getSkin_right().getGrease().contains("%") ? bean.getSkin_right().getGrease().replace("%", "") : bean.getSkin_right().getGrease()), i));
                    break;
            }
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

        setLineChartOffset(lcLineChartWater,xVals.size());


        LineData data = new LineData(xVals, dataSets);
        //数值颜色
        data.setValueTextColor(Color.parseColor("#9c9c9c"));
        data.setValueTextSize(9f);
        lcLineChartWater.setData(data);
        lcLineChartWater.invalidate();


        LineDataSet setOil = new LineDataSet(yValsOil, "DataSet 2");
        setOil.setAxisDependency(YAxis.AxisDependency.LEFT);
        setOil.setColor(Color.parseColor("#4CC578"));
        setOil.setCircleColor(Color.parseColor("#4CC578"));
        setOil.setLineWidth(2f);
        setOil.setCircleRadius(3f);
        setOil.setDrawCircleHole(true);
        ArrayList<ILineDataSet> dataSetsOil = new ArrayList<ILineDataSet>();
        dataSetsOil.add(setOil);


        setLineChartOffset(lcLineChartOil,xVals.size());


        LineData dataOil = new LineData(xVals, dataSetsOil);
        dataOil.setValueTextColor(Color.parseColor("#9c9c9c"));
        dataOil.setValueTextSize(9f);
        lcLineChartOil.setData(dataOil);
        lcLineChartOil.invalidate();

    }

    private void setLineChartOffset(LineChart lineChart,int xCount){
        if(xCount>6){
            lineChart.zoom(xCount/6.0f,1f,0f,0f);
        }
        lineChart.setDragOffsetX(20f);
        lineChart.calculateOffsets();
        ViewPortHandler viewPortHandler=lineChart.getViewPortHandler();
        viewPortHandler.refresh(viewPortHandler.translate(new float[]{0,0}), lineChart, false);
        lineChart.postInvalidate();
    }

    public void xDate(String date, ArrayList<String> xVals) {

        if (date.length() < 5)//201704||20174
            return;


        String strYear = date.substring(0, 3);
        String strMonth = date.substring(4);
        int daysOfMonth = TimeUtil.getDaysByYearMonth(Integer.parseInt(strYear), Integer.parseInt(strMonth));


        for (int i =0;i<daysOfMonth;i++){

            xVals.add(strMonth+"."+i+1);

        }



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
