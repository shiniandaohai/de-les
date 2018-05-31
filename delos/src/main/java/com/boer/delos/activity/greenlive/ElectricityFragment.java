package com.boer.delos.activity.greenlive;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.GreenLiveAdapter;
import com.boer.delos.adapter.RecyclerAdapter;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Addr;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.GreenSocketResult;
import com.boer.delos.model.Time;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.green.GreenLiveController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.TimeUtil;
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
import java.util.Map;

/**
 * @author PengJiYang
 * @Description: "绿色生活"中的电
 * create at 2016/4/8 13:17
 */
public class ElectricityFragment extends LazyFragment implements View.OnClickListener {

    private View rootView;
    private BarChart bcGreenElectricity;
    private LineChart lcElectricityHistory;
    private RelativeLayout rlUnfold;
    private LinearLayout llElectricityMore;
    private ImageView ivUnfold;
    private TextView tvElectricYear, tvElectricMonth, tvElectricDay, tvElectricDate, tvElectricMeterName, tvElectricQuantity;
    private RecyclerView rvElectricityDay, rvElectricityMonth, rvElectricityYear;
    private Spinner spElectricYear;

    private RecyclerAdapter dayAdapter;// 日期适配器
    private RecyclerAdapter monthAdapter;// 月份适配器
    private GreenLiveAdapter yearAdapter;// 年份适配器
    private List<String> dayList = new ArrayList<>();// 日期的数据集合
    private List<String> monthList = new ArrayList<>();// 月份的数据集合
    private List<String> yearList = new ArrayList<>();// 年份的数据集合

    private int year, month, day;
    private SimpleDateFormat dateFormat;

    private boolean isUnfold = false;
    private int counter = 0;
    private TextView preSelectedMonth, preSelectedDay;
    private LinearLayoutManager monthLinearLayoutManager, dayLinearLayoutManager;

    private List<Addr> addressList = new ArrayList<>(); //插座的addr name
    private ArrayMap<String, Float> listShowKwH = new ArrayMap<>();//查询到的展示电量
    private float startY = 0;
    private float lastY = 0;
    private long startTime;
    private BarDataSet set1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_eletricity, null);

        initView();
        initViewData();
        /**/
        addressList = settingSocketAddr();
        initDate();
        setYearList();
        setDayList();

        initBarChart();
        initLineChart();
        /**/
        initListener();

        return rootView;
    }


    @Override
    protected void lazyLoad() {
        if (listShowKwH.size() == 0) {
            addressList = settingSocketAddr();
            //当天时间0-24
            querySocket(TimeUtil.getTimesmorning(0) + "", TimeUtil.getCurrentstamp() + "");
            startTime = TimeUtil.getTimesmorning(0);
            queryElectricity();
        }
    }

    private void initView() {
        rlUnfold = (RelativeLayout) rootView.findViewById(R.id.rlUnfold);
        ivUnfold = (ImageView) rootView.findViewById(R.id.ivUnfold);
        llElectricityMore = (LinearLayout) rootView.findViewById(R.id.llElectricityMore);
        tvElectricYear = (TextView) rootView.findViewById(R.id.tvElectricYear);
        tvElectricMonth = (TextView) rootView.findViewById(R.id.tvElectricMonth);
        tvElectricDay = (TextView) rootView.findViewById(R.id.tvElectricDay);
        rvElectricityDay = (RecyclerView) rootView.findViewById(R.id.rvElectricityDay);
        rvElectricityMonth = (RecyclerView) rootView.findViewById(R.id.rvElectricityMonth);
        rvElectricityYear = (RecyclerView) rootView.findViewById(R.id.rvElectricityYear);
        spElectricYear = (Spinner) rootView.findViewById(R.id.spElectricYear);
        tvElectricDate = (TextView) rootView.findViewById(R.id.tvElectricDate);
        tvElectricMeterName = (TextView) rootView.findViewById(R.id.tvElectricMeterName);
        tvElectricQuantity = (TextView) rootView.findViewById(R.id.tvElectricQuantity);
        lcElectricityHistory = (LineChart) rootView.findViewById(R.id.lcElectricityHistory);

        rlUnfold.setOnClickListener(this);
        tvElectricYear.setOnClickListener(this);
        tvElectricMonth.setOnClickListener(this);
        tvElectricDay.setOnClickListener(this);

    }

    private void initViewData() {
        /*Spinner*/
        yearAdapter = new GreenLiveAdapter(getActivity());
        spElectricYear.setAdapter(yearAdapter);
        spElectricYear.setDropDownVerticalOffset(ScreenUtils.dip2px(getActivity(), 30));
        /*年*/
        LinearLayoutManager yearLinearLayoutManager = new LinearLayoutManager(getActivity());
        yearLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvElectricityYear.setLayoutManager(yearLinearLayoutManager);
        /*月*/
        monthLinearLayoutManager = new LinearLayoutManager(getActivity());
        monthLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvElectricityMonth.setLayoutManager(monthLinearLayoutManager);

        monthAdapter = new RecyclerAdapter(getActivity(), monthList);
//        preSelectedMonth.setTextColor(Color.parseColor("#128ce3"));
        rvElectricityMonth.setAdapter(monthAdapter);

        /*日*/
        dayLinearLayoutManager = new LinearLayoutManager(getActivity());
        dayLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvElectricityDay.setLayoutManager(dayLinearLayoutManager);

        dayAdapter = new RecyclerAdapter(getActivity(), dayList);
        rvElectricityDay.setAdapter(dayAdapter);

        rvElectricityDay.scrollToPosition(dayList.size() - 1);
        dayAdapter.settingChoise(dayList.size() - 1);
        monthAdapter.notifyDataSetChanged();

    }

    private void initListener() {
        spElectricYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = Calendar.getInstance().get(Calendar.YEAR) - position;
                if (counter != 0) {
                    preSelectedDay = null;
                    preSelectedMonth = null;
                    setYearMonthDay(rvElectricityDay, rvElectricityMonth, rvElectricityYear, tvElectricDay, tvElectricMonth, tvElectricYear);
                    //全年
                    querySocket(TimeUtil.getTargetTimeStamp(year, 1, 1, 0, 0, 0) + "",
                            TimeUtil.getTargetTimeStamp(year + 1, 1, 1, 0, 0, 0) + "");
                    startTime = TimeUtil.getTargetTimeStamp(year, 1, 1, 0, 0, 0);
                }
                counter++;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                month = position;
                //月
                querySocket(TimeUtil.getTargetTimeStamp(year, month, 1, 0, 0, 0) + "",
                        TimeUtil.getTargetTimeStamp(year, month + 1, 1, 0, 0, 0) + "");

                startTime = TimeUtil.getTargetTimeStamp(year, month, 1, 0, 0, 0);
            }
        });
        dayAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                day = position;

                querySocket(TimeUtil.getTargetTimeStamp(year, month, day, 0, 0, 0) + "",
                        TimeUtil.getTargetTimeStamp(year, month, day + 1, 0, 0, 0) + "");

                startTime = TimeUtil.getTargetTimeStamp(year, month, day, 0, 0, 0);
            }
        });


    }

    private void initDate() {
        Calendar cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM");
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);// 月份从0开始
        day = cal.get(Calendar.DAY_OF_MONTH);
        tvElectricDate.setText(year + "年" + (month + 1) + "月" + day + "日");
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

        setYearMonthDay(rvElectricityMonth, rvElectricityYear, rvElectricityDay, tvElectricMonth, tvElectricYear, tvElectricDay);
        dayAdapter.setDatas(dayList);
        dayAdapter.settingChoise(dayList.size() - 1);
        rvElectricityDay.scrollToPosition(dayList.size() - 1);
        dayAdapter.notifyDataSetChanged();

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

        setYearMonthDay(rvElectricityDay, rvElectricityYear, rvElectricityMonth, tvElectricDay, tvElectricYear, tvElectricMonth);
        monthAdapter.setDatas(monthList);
        monthAdapter.settingChoise(monthList.size() - 1);
        monthAdapter.notifyDataSetChanged();
        rvElectricityMonth.scrollToPosition(monthList.size() - 1);
    }

    private void setYearList() {
        yearList.clear();
        Calendar calendar = Calendar.getInstance();
        for (int i = calendar.get(Calendar.YEAR); i >= 2015; i--) {
            yearList.add("" + i);
        }

        yearAdapter.setDatas(yearList);
        yearAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlUnfold:
                if (!isUnfold) {
                    ivUnfold.setImageResource(R.drawable.ic_green_live_pack_up);
                    llElectricityMore.setVisibility(View.VISIBLE);
                    isUnfold = true;
                } else {
                    ivUnfold.setImageResource(R.drawable.ic_green_live_unfold);
                    llElectricityMore.setVisibility(View.GONE);
                    isUnfold = false;
                }
                break;
            case R.id.tvElectricYear:
                preSelectedDay = null;
                preSelectedMonth = null;
                setYearMonthDay(rvElectricityDay, rvElectricityMonth, rvElectricityYear, tvElectricDay, tvElectricMonth, tvElectricYear);
                break;
            case R.id.tvElectricMonth:
                setMonthList();
                querySocket(TimeUtil.getTargetTimeStamp(year, month, 0, 0, 0, 0) + "",
                        TimeUtil.getTargetTimeStamp(year, month + 1, 0, 0, 0, 0) + "");
                break;
            case R.id.tvElectricDay:
                setDayList();
                querySocket(TimeUtil.getTargetTimeStamp(year, month, day, 0, 0, 0) + "",
                        TimeUtil.getTargetTimeStamp(year, month, day + 1, 0, 0, 0) + "");
                break;
        }
    }

    /**
     * 初始化柱形图
     */
    private void initBarChart() {
        bcGreenElectricity = (BarChart) rootView.findViewById(R.id.bcGreenElectricity);
        bcGreenElectricity.setDrawGridBackground(false);
        // no description text
        bcGreenElectricity.setDescription("");
        bcGreenElectricity.setNoDataTextDescription("You need to provide data for the chart.");
        bcGreenElectricity.setTouchEnabled(true);
        bcGreenElectricity.setDragEnabled(true);
        bcGreenElectricity.setPinchZoom(true); //手势缩放
        bcGreenElectricity.setScaleXEnabled(true);
        bcGreenElectricity.setScaleYEnabled(false);
        bcGreenElectricity.setDrawBarShadow(true); // 灰色背景


        XAxis xAxis = bcGreenElectricity.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#000000"));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = bcGreenElectricity.getAxisLeft();
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
        bcGreenElectricity.getAxisRight().setEnabled(false);
        // add data
//        bcGreenElectricity.animateY(1500, Easing.EasingOption.Linear);
        bcGreenElectricity.getLegend().setEnabled(false);

        List<Float> yBarVal = new ArrayList<>();
        yBarVal.add(0.0f);
        yBarVal.add(0.0f);
        yBarVal.add(0.0f);
        setBarChartData(new ArrayList<String>(), yBarVal);
    }

    /**
     * 设置柱形图的数据
     */
    private void setBarChartData(List<String> xVals, List<Float> yBarVal) {
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

        bcGreenElectricity.setData(data);
        bcGreenElectricity.getData().notifyDataChanged();
        bcGreenElectricity.notifyDataSetChanged();
        bcGreenElectricity.invalidate();
        bcGreenElectricity.animateY(1400);
    }

    private void setYearMonthDay(RecyclerView rvFirstUnchecked, RecyclerView rvSecondUnchecked, RecyclerView rvChecked, TextView tvFirstUnchecked, TextView tvSecondUnchecked, TextView tvChecked) {
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
    private void initLineChart() {
        lcElectricityHistory.setDrawGridBackground(false);
        lcElectricityHistory.setDescription("");
        lcElectricityHistory.setNoDataTextDescription("You need to provide data for the chart."); // no description text
        lcElectricityHistory.setTouchEnabled(false);
        lcElectricityHistory.setDragEnabled(false);
        lcElectricityHistory.setScaleEnabled(false);


        XAxis xAxis = lcElectricityHistory.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setDrawLimitLinesBehindData(true);

        YAxis leftAxis = lcElectricityHistory.getAxisLeft();
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(6);
        leftAxis.setAxisMinValue(0);
//        leftAxis.setSpaceBottom(20f);
        //虚线
        leftAxis.enableGridDashedLine(0f, 100f, 0f);
        leftAxis.setDrawZeroLine(true);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        lcElectricityHistory.getAxisRight().setEnabled(false);
        // add data
        List<Float> yVal = new ArrayList<>();
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        setLineChartData(5, yVal);
        lcElectricityHistory.animateY(1500, Easing.EasingOption.Linear);
        lcElectricityHistory.getLegend().setEnabled(false);
    }

    /**
     * 给表格设置数据
     *
     * @param count 横坐标的个数
     * @param yVal  纵坐标集合
     */
    private void setLineChartData(int count, List<Float> yVal) {

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
        lcElectricityHistory.setData(data);
    }

    /**
     * 查询插座数据
     */
    private void querySocket(String startTime, String lastTime) {
        //设置地址
        //设置时间
        List<Time> timeList = new ArrayList<>();
        timeList.add(new Time(startTime));
        timeList.add(new Time(lastTime));
        GreenLiveController.getInstance().querySocket(getContext(), addressList, timeList, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("querySocket json======" + Json);
                GreenSocketResult result = GsonUtil.getObject(Json, GreenSocketResult.class);


                if (result.getRet() == 0) {
                    // 设置表格数据
                    socketDataDealWith(result);
                    barChartShowYBarVal();
                } else {
                    // 设置柱形图barChart
                    List<Float> yBarVal = new ArrayList<>();
                    yBarVal.add(0.0f);
                    yBarVal.add(0.0f);
                    yBarVal.add(0.0f);
                    setBarChartData(new ArrayList<String>(), yBarVal);

                    // 设置折线图lineChart
                    List<Float> yVal = new ArrayList<>();
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    setLineChartData(5, yVal);
                }
            }

            @Override
            public void onFailed(String json) {
                if (((BaseActivity) getActivity()).toastUtils == null) return;
                ((BaseActivity) getActivity())
                        .toastUtils.showErrorWithStatus(json);
            }
        });
    }

    /**
     * 查询电表数据
     */
    private void queryElectricity() {
        // TODO 设置插座地址
        List<Addr> addressList = new ArrayList<>();
        addressList.add(new Addr("85C62807004B12000000"));

        // TODO 设置时间
        List<Time> timeList = new ArrayList<>();
        timeList.add(new Time("1459440000"));
        timeList.add(new Time("1461137859"));
        GreenLiveController.getInstance().queryElectricity(getActivity(), addressList, timeList, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("queryElectricity json======" + Json);
            }

            @Override
            public void onFailed(String json) {


            }
        });
    }

    /**
     * //  设置插座地址
     *
     * @return List<Addr>
     */
    private List<Addr> settingSocketAddr() {
        try {  //初始化插座设备
            addressList.clear();

            List<DeviceRelate> listConstans = Constant.DEVICE_RELATE;
            for (DeviceRelate deviceRelate : listConstans) {
                Device device = deviceRelate.getDeviceProp();
                if (device == null) continue;
                if (!device.getType().equals("Socket")) {
                    continue;
                }
                Addr addrTemp = new Addr();
                addrTemp.setAddr(device.getAddr());
                addrTemp.setName(device.getName());

                addressList.add(addrTemp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Loger.d("nullPoint Exception ");
        }
        return addressList;
    }

    /**
     * 处理插座的数据
     *
     * @param result
     */
    private void socketDataDealWith(GreenSocketResult result) {

        ArrayMap<String, GreenSocketResult.SocketData> mapPreKwH = new ArrayMap<>();
        ArrayMap<String, GreenSocketResult.SocketData> mapLastKwH = new ArrayMap<>();

        List<GreenSocketResult.ResponseBean> responseList = result.getResponse();
        for (GreenSocketResult.ResponseBean data : responseList) {
            String temp = data.getPayload();
            GreenSocketResult.SocketData socketData = GsonUtil.getObject(data.getPayload(),
                    GreenSocketResult.SocketData.class);

            if (data.getTime() == startTime) {
                mapPreKwH.put(data.getAddr(), socketData);
            } else {
                mapLastKwH.put(data.getAddr(), socketData);
            }
        }
        for (Addr addrTemp : addressList) {
            String pre = "0";
            String last = "0";

            //上传的有数据
            if (mapPreKwH.get(addrTemp.getAddr()) != null && mapLastKwH.size() != 0) {
                pre = mapPreKwH.get(addrTemp.getAddr()).getValue().getEnergy();
                last = mapLastKwH.get(addrTemp.getAddr()).getValue().getEnergy();
                listShowKwH.put(addrTemp.getAddr(), Float.valueOf(last) - Float.valueOf(pre));
            } else {
                listShowKwH.put(addrTemp.getAddr(), 0f);
            }

        }
    }

    /**
     * 柱状图的数据
     */
    private void barChartShowYBarVal() {
        List<Float> yBarVal = new ArrayList<>();
        List<String> xBarVal = new ArrayList<>();
        /*addr kwh*/
        for (Map.Entry<String, Float> entry : listShowKwH.entrySet()) {
            yBarVal.add(entry.getValue());
            xBarVal.add(getSocketName(entry.getKey(), addressList));
        }
        setBarChartData(xBarVal, yBarVal);
    }

    private String getSocketName(String key, List<Addr> list) {

        for (Addr a : list) {
            if (a.getAddr().equals(key)) {
                return a.getName();
            }
        }
        return "插座";
    }

}
