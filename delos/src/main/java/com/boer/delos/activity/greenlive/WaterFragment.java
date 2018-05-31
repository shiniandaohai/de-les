package com.boer.delos.activity.greenlive;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.GreenLiveAdapter;
import com.boer.delos.adapter.RecyclerAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Addr;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.Time;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.green.GreenLiveController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.TimeUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "绿色生活"中的水
 * create at 2016/4/8 13:13
 */
public class WaterFragment extends Fragment implements View.OnClickListener {

    View rootView;
    private TextView tvWaterYear, tvWaterMonth, tvWaterDay, tvWaterDate, tvWaterMeterName, tvWaterQuantity;
    private RecyclerView rvWaterDay, rvWaterMonth, rvWaterYear;
    private Spinner spWaterYear;
    private LineChart lcWaterHistory;

    private RecyclerAdapter dayAdapter;// 日期适配器
    private RecyclerAdapter monthAdapter;// 月份适配器
    private GreenLiveAdapter yearAdapter;// 年份适配器
    private List<String> dayList = new ArrayList<>();// 日期的数据集合
    private List<String> monthList = new ArrayList<>();// 月份的数据集合
    private List<String> yearSpinnerList = new ArrayList<>();// 年份的数据集合

    private int year, month, day;
    private SimpleDateFormat dateFormat;

    private int counter = 0;
    private TextView preSelectedMonth, preSelectedDay;
    private List<Addr> addressList; //水表的addr

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_water, null);

        initView();
        addressList = settingWaterAddr();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryWater();
    }

    private void initView() {
        this.tvWaterYear = (TextView) rootView.findViewById(R.id.tvWaterYear);
        this.tvWaterMonth = (TextView) rootView.findViewById(R.id.tvWaterMonth);
        this.tvWaterDay = (TextView) rootView.findViewById(R.id.tvWaterDay);
        this.rvWaterDay = (RecyclerView) rootView.findViewById(R.id.rvWaterDay);
        this.rvWaterMonth = (RecyclerView) rootView.findViewById(R.id.rvWaterMonth);
        this.rvWaterYear = (RecyclerView) rootView.findViewById(R.id.rvWaterYear);
        this.spWaterYear = (Spinner) rootView.findViewById(R.id.spWaterYear);
        this.tvWaterDate = (TextView) rootView.findViewById(R.id.tvWaterDate);
        this.tvWaterMeterName = (TextView) rootView.findViewById(R.id.tvWaterMeterName);
        this.tvWaterQuantity = (TextView) rootView.findViewById(R.id.tvWaterQuantity);
        this.lcWaterHistory = (LineChart) rootView.findViewById(R.id.lcWaterHistory);

        this.tvWaterYear.setOnClickListener(this);
        this.tvWaterMonth.setOnClickListener(this);
        this.tvWaterDay.setOnClickListener(this);

        yearAdapter = new GreenLiveAdapter(getActivity());
        this.spWaterYear.setAdapter(yearAdapter);
        this.spWaterYear.setDropDownVerticalOffset(ScreenUtils.dip2px(getActivity(), 30));
        this.spWaterYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = Calendar.getInstance().get(Calendar.YEAR) - position;

                if (counter != 0) {
                    preSelectedDay = null;
                    preSelectedMonth = null;
                    setYearMonthDay(rvWaterDay, rvWaterMonth, rvWaterYear, tvWaterDay, tvWaterMonth, tvWaterYear);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spWaterYear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 1;
                return false;
            }
        });

        LinearLayoutManager yearLinearLayoutManager = new LinearLayoutManager(getActivity());
        yearLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWaterYear.setLayoutManager(yearLinearLayoutManager);// 设置布局管理器

        LinearLayoutManager monthLinearLayoutManager = new LinearLayoutManager(getActivity());
        monthLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWaterMonth.setLayoutManager(monthLinearLayoutManager);// 设置布局管理器
        monthAdapter = new RecyclerAdapter(getActivity(), monthList);
        preSelectedMonth = monthAdapter.getLastItem();
//        preSelectedMonth.setTextColor(Color.parseColor("#128ce3"));
        this.rvWaterMonth.setAdapter(monthAdapter);
//        monthAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
////                TextView selectedItem = (TextView) view.findViewById(R.id.tvItemText);
//                TextView selectedItem = (TextView) view;
//                selectedItem.setTextColor(Color.parseColor("#128ce3"));
//                if (preSelectedMonth == null) {
//                    preSelectedMonth = selectedItem;
//                } else {
//                    preSelectedMonth.setTextColor(Color.parseColor("#919191"));
//                    preSelectedMonth = selectedItem;
//                }
//                month = position;
//            }
//        });

        LinearLayoutManager dayLinearLayoutManager = new LinearLayoutManager(getActivity());
        dayLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWaterDay.setLayoutManager(dayLinearLayoutManager);// 设置布局管理器
        dayAdapter = new RecyclerAdapter(getActivity(), dayList);
        preSelectedDay = dayAdapter.getLastItem();
//        preSelectedDay.setTextColor(Color.parseColor("#128ce3"));
        this.rvWaterDay.setAdapter(dayAdapter);
//        dayAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
////                TextView selectedItem = (TextView) view.findViewById(R.id.tvItemText);
//                TextView selectedItem = (TextView) view;
//                selectedItem.setTextColor(Color.parseColor("#128ce3"));
//                if (preSelectedDay == null) {
//                    preSelectedDay = selectedItem;
//                } else {
//                    preSelectedDay.setTextColor(Color.parseColor("#919191"));
//                    preSelectedDay = selectedItem;
//                }
//                day = position;
//                tvWaterDate.setText(year + "年" + month + "月" + day + "日");
//                queryWater();
//            }
//        });
        initDate();
        setYearList();
        setDayList();
        initLineChart();
    }

    private void initDate() {
        Calendar cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM");
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);// 月份从0开始
        day = cal.get(Calendar.DAY_OF_MONTH);
        tvWaterDate.setText(year + "年" + (month + 1) + "月" + day + "日");
    }

    private void setDayList() {
        // 判断年份和月份是否是当前年份或月份
        dayList.clear();
        Calendar calendar = Calendar.getInstance();

        int days;
        if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH)) {
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

        setYearMonthDay(rvWaterMonth, rvWaterYear, rvWaterDay, tvWaterMonth, tvWaterYear, tvWaterDay);
        dayAdapter.setDatas(dayList);
        dayAdapter.notifyDataSetChanged();
        this.rvWaterDay.scrollToPosition(dayList.size() - 1);
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

        setYearMonthDay(rvWaterDay, rvWaterYear, rvWaterMonth, tvWaterDay, tvWaterYear, tvWaterMonth);
        monthAdapter.setDatas(monthList);
        monthAdapter.notifyDataSetChanged();
        this.rvWaterMonth.scrollToPosition(monthList.size() - 1);
    }

    private void setYearList() {
        yearSpinnerList.clear();
        Calendar calendar = Calendar.getInstance();
        for (int i = calendar.get(Calendar.YEAR); i >= 2015; i--) {
            yearSpinnerList.add("" + i);
        }

        yearAdapter.setDatas(yearSpinnerList);
        yearAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvWaterYear:
                preSelectedDay = null;
                preSelectedMonth = null;
                setYearMonthDay(rvWaterDay, rvWaterMonth, rvWaterYear, tvWaterDay, tvWaterMonth, tvWaterYear);
                break;
            case R.id.tvWaterMonth:
//                counter = 0;
                setMonthList();
                break;
            case R.id.tvWaterDay:
//                counter = 0;
                setDayList();
                break;
        }
    }

    /**
     * 设置"年","月","日"三个按钮的选中状态和样式
     *
     * @param rvFirstUnchecked  年份滚动列表是否显示
     * @param rvSecondUnchecked 月份滚动列表是否显示
     * @param rvChecked         天滚动列表是否显示
     * @param tvFirstUnchecked  "年"按钮是否显示
     * @param tvSecondUnchecked "月"按钮是否显示
     * @param tvChecked         "天"按钮是否显示
     */
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
        lcWaterHistory.setDrawGridBackground(false);
        lcWaterHistory.setDescription("");
        lcWaterHistory.setNoDataTextDescription("You need to provide data for the chart."); // no description text
        lcWaterHistory.setTouchEnabled(false);
        lcWaterHistory.setDragEnabled(false);
        lcWaterHistory.setScaleEnabled(false);


        XAxis xAxis = lcWaterHistory.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setDrawLimitLinesBehindData(true);

        YAxis leftAxis = lcWaterHistory.getAxisLeft();
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(6);
        leftAxis.setAxisMinValue(0);
//        leftAxis.setSpaceBottom(20f);
        //虚线
        leftAxis.enableGridDashedLine(0f, 100f, 0f);
        leftAxis.setDrawZeroLine(true);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        lcWaterHistory.getAxisRight().setEnabled(false);
        // add data
        List<Float> yVal = new ArrayList<>();
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        setData(5, yVal);
        lcWaterHistory.animateY(1500, Easing.EasingOption.Linear);
        lcWaterHistory.getLegend().setEnabled(false);
    }

    /**
     * 给表格设置数据
     *
     * @param count 横坐标的个数
     * @param yVal  纵坐标集合
     */
    private void setData(int count, List<Float> yVal) {

        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("");
        xVals.add("0:30");
        xVals.add("1:00");
        xVals.add("1:30");
        xVals.add("2:00");
        xVals.add("2:30");

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

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
        lcWaterHistory.setData(data);
    }

    /**
     * 查询水表数据
     */
    private void queryWater() {
        // TODO 设置插座地址
        List<Addr> addressList = new ArrayList<>();
        addressList.add(new Addr("85C62807004B12000000"));

        // 设置时间
        List<Time> timeList = getMyTimeMillis();
        timeList.add(new Time("1459440000"));
        timeList.add(new Time("1461137859"));
        GreenLiveController.getInstance().queryWater(getActivity(), addressList, timeList, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("queryWater json======" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {

                } else {
                    List<Float> yVal = new ArrayList<>();
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    setData(5, yVal);
                }
            }

            @Override
            public void onFailed(String json) {
                ((GreenLiveListeningActivity) getActivity()).toastUtils.showErrorWithStatus(json);
            }
        });
    }

    /**
     * 获取时间戳集合
     *
     * @return 时间戳集合
     */
    private List<Time> getMyTimeMillis() {
        List<Time> timeList = new ArrayList<>();
        List<String> halfHours;
        String temp;
        Date tempDate;
        int count;// 半小时时间点的个数
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        if (year == cal.get(Calendar.YEAR) && month == cal.get(Calendar.MONTH) + 1 && day == cal.get(Calendar.DAY_OF_MONTH)) {
            // 如果时间是今天，则取当前时间点以前的半小时时间点
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            if (minute > 30) {
                count = hour * 2 + 1;
            } else {
                count = hour * 2;
            }
        } else {
            // 如果时间不是今天，则取一整天的48个半小时时间点
            count = 48;
        }
        halfHours = TimeUtil.getHalfHourList(count);
        for (int i = 0; i < count; i++) {
            temp = year + "-" + month + "-" + day + " " + halfHours.get(i);
            tempDate = TimeUtil.getTimeFromString(temp, "yyyy-MM-dd HH:mm");
            timeList.add(new Time(TimeUtil.getTargetTimeStamp(tempDate) + ""));
        }
        return timeList;
    }

    /**
     * //  设置插座地址
     *
     * @return List<Addr>
     */
    private List<Addr> settingWaterAddr() {
        try {  //初始化插座设备
            addressList.clear();

            List<DeviceRelate> listConstans = Constant.DEVICE_RELATE;
            for (DeviceRelate deviceRelate : listConstans) {
                Device device = deviceRelate.getDeviceProp();
                if (device == null) continue;
                if (!device.getType().equals("")) { // TODO
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

}
