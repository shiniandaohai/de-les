package com.boer.delos.activity.scene.devicecontrol.airclean;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.Addr;
import com.boer.delos.model.AirCleanDetailChart;
import com.boer.delos.model.Device;
import com.boer.delos.model.SkinDetailChart;
import com.boer.delos.model.Time;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.MPandroidChartHelper;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.TitleLayout;
import com.boer.delos.view.popupWindow.ShowYearMonthPopupWindow;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.boer.delos.utils.TimeUtil.formatStamp2Time;

/**
 * Created by gaolong on 2017/4/15.
 */
public class AirCleanHistoryActivity extends CommonBaseActivity {
    @Bind(R.id.ctv_time)
    CheckedTextView ctvTime;


    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.ll_anchor)
    LinearLayout llAnchor;
    @Bind(R.id.lcLineChartPm25)
    LineChart lcLineChartPm25;
    @Bind(R.id.lcLineChartTVOC)
    LineChart lcLineChartTVOC;
    private ShowYearMonthPopupWindow showYearMonthPopupWindow;

    private int year;
    private int month;
    private List<AirCleanDetailChart> airCleanDetailCharts;
    public Device mDevice;

    List<Addr> addresses;
    List<Time> times;


    @Override
    protected int initLayout() {

        return R.layout.activity_air_clean_history;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setVisibility(View.GONE);

    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        mDevice = (Device) intent.getSerializableExtra("deviceValueBean");

        if (mDevice == null)
            return;


        airCleanDetailCharts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        ctvTime.setText(year + getString(R.string.pick_year) + month + getString(R.string.pick_month));
        getTime(year, month);


        queryMonthData(times, addresses);


        showYearMonthPopupWindow = new ShowYearMonthPopupWindow(this, tlTitleLayout);
        showYearMonthPopupWindow.setShowTimePopupWindowInterface(new ShowYearMonthPopupWindow.ShowTimePopupWindowInterface() {
            @Override
            public void popupDismiss(int position) {

            }

            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String startTime, String endTime) {

                year = Integer.parseInt(startTime);
                month = Integer.parseInt(endTime);

                int tempMonth=Calendar.getInstance().get(Calendar.MONTH);
                if(tempMonth<month){
                    ToastHelper.showShortMsg("没有此时间段的数据");
                    return;
                }
                ctvTime.setText(startTime + getString(R.string.pick_year) + endTime + getString(R.string.pick_month));
                getTime(year, month);
                queryMonthData(times, addresses);

            }
        });


    }

    @Override
    protected void initAction() {


        initLineChartPm25();
        initLineChartTVOC();


    }


    private void initLineChartPm25() {
        MPandroidChartHelper.initLineChart(lcLineChartPm25, true,daysOfMonth);
    }


    /**
     * 初始化折线图
     */
    private void initLineChartTVOC() {
        MPandroidChartHelper.initLineChart(lcLineChartTVOC, true,daysOfMonth);
    }

    private int daysOfMonth;
    private void getTime(int y, int m) {

        daysOfMonth = TimeUtil.getDaysByYearMonth1(y, m);


        times = new ArrayList<>();
        for (int i = 1; i <= daysOfMonth; i++) {
            Time time = new Time();
            time.setTime(TimeUtil.getTargetTimeStamp(y, m, i, 23, 59, 59) + "");
            times.add(time);
        }

        addresses = new ArrayList<>();
        Addr addr = new Addr();

//        addr.setAddr("C342F50B004B12000000");
        addr.setAddr(mDevice.getAddr());
        addresses.add(addr);


    }

    private void queryMonthData(List times, List addresses) {
        airCleanDetailCharts.clear();
        toastUtils.showProgress("");
        DeviceController.getInstance().deviceAirCleanHistoryData(this, times,
                addresses, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {

                            toastUtils.dismiss();

                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray data = jsonObject.getJSONArray("response");
//
                            for (int i = 0; i < data.length(); i++) {

                                AirCleanDetailChart airCleanDetailChart = new AirCleanDetailChart();

                                JSONObject bean = data.getJSONObject(i);

                                String payload = bean.getString("payload");

                                int measuretime = bean.getInt("time");

                                AirCleanDetailChart.PayloadBean payloadBean = new Gson().fromJson(payload, new TypeToken<AirCleanDetailChart.PayloadBean>() {
                                }.getType());

                                airCleanDetailChart.setTime(measuretime);

                                airCleanDetailChart.setPayload(payloadBean);

                                airCleanDetailCharts.add(airCleanDetailChart);

                                invalidateChar();

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils != null)
                            toastUtils.dismiss();
                    }
                });
    }


    @OnClick({R.id.iv_back, R.id.ctv_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.ctv_time:

                showYearMonthPopupWindow.showPopupWindow();

                break;
        }
    }


    public void invalidateChar() {

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<Entry> yValsOil = new ArrayList<>();

        if (airCleanDetailCharts == null)
            return;

        int i = 0;

        for (AirCleanDetailChart bean : airCleanDetailCharts) {

            String time = formatStamp2Time(bean.getTime(), "MM.dd");

            xVals.add(time);

            AirCleanDetailChart.PayloadBean payloadBean=bean.getPayload();
            if(payloadBean!=null&&payloadBean.getValue()!=null){
                yVals1.add(new Entry(payloadBean.getValue().getPm25(), i));
                yValsOil.add(new Entry(payloadBean.getValue().getTVOC(), i));
            }
            else{
                yVals1.add(new Entry(0, i));
                yValsOil.add(new Entry(0, i));
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
        LineData data = new LineData(xVals, dataSets);
        //数值颜色
        data.setValueTextColor(Color.parseColor("#9c9c9c"));
        data.setValueTextSize(9f);
        lcLineChartPm25.setData(data);
        lcLineChartPm25.invalidate();


        LineDataSet setOil = new LineDataSet(yValsOil, "DataSet 2");
        setOil.setAxisDependency(YAxis.AxisDependency.LEFT);
        setOil.setColor(Color.parseColor("#4CC578"));
        setOil.setCircleColor(Color.parseColor("#4CC578"));
        setOil.setLineWidth(2f);
        setOil.setCircleRadius(3f);
        setOil.setDrawCircleHole(true);
        ArrayList<ILineDataSet> dataSetsOil = new ArrayList<ILineDataSet>();
        dataSetsOil.add(setOil);
        LineData dataOil = new LineData(xVals, dataSetsOil);
        dataOil.setValueTextColor(Color.parseColor("#9c9c9c"));
        dataOil.setValueTextSize(9f);
        lcLineChartTVOC.setData(dataOil);
        lcLineChartTVOC.invalidate();

    }


}
