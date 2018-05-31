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
import com.boer.delos.model.Addr;
import com.boer.delos.model.Time;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.green.GreenLiveController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.ToastUtils;
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
 * @Description: "绿色生活"中的气
 * create at 2016/4/8 13:16
 *
 */
public class GasFragment extends Fragment implements View.OnClickListener{

    View rootView;
    private TextView tvGasYear, tvGasMonth, tvGasDay, tvGasDate, tvChartRoomName, tvGasQuantity;
    private RecyclerView rvGasDay, rvGasMonth, rvGasYear;
    private Spinner spGasYear;
    private LineChart lcGasHistory;

    private RecyclerAdapter dayAdapter;// 日期适配器
    private RecyclerAdapter monthAdapter;// 月份适配器
    private GreenLiveAdapter yearAdapter;// 年份适配器
    private List<String> dayList = new ArrayList<>();// 日期的数据集合
    private List<String> monthList = new ArrayList<>();// 月份的数据集合
    private List<String> yearList = new ArrayList<>();// 年份的数据集合

    private int year,month, day;
    private SimpleDateFormat dateFormat;

    private int counter = 0;
    private TextView preSelectedMonth, preSelectedDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gas, null);

        initView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryGas();
    }

    private void initView() {
        this.tvGasYear = (TextView) rootView.findViewById(R.id.tvGasYear);
        this.tvGasMonth = (TextView) rootView.findViewById(R.id.tvGasMonth);
        this.tvGasDay = (TextView) rootView.findViewById(R.id.tvGasDay);
        this.rvGasDay = (RecyclerView) rootView.findViewById(R.id.rvGasDay);
        this.rvGasMonth = (RecyclerView) rootView.findViewById(R.id.rvGasMonth);
        this.rvGasYear = (RecyclerView) rootView.findViewById(R.id.rvGasYear);
        this.spGasYear = (Spinner) rootView.findViewById(R.id.spGasYear);
        this.tvGasDate = (TextView) rootView.findViewById(R.id.tvGasDate);
        this.tvChartRoomName = (TextView) rootView.findViewById(R.id.tvChartRoomName);
        this.tvGasQuantity = (TextView) rootView.findViewById(R.id.tvGasQuantity);
        this.lcGasHistory = (LineChart) rootView.findViewById(R.id.lcGasHistory);

        this.tvGasYear.setOnClickListener(this);
        this.tvGasMonth.setOnClickListener(this);
        this.tvGasDay.setOnClickListener(this);

        yearAdapter = new GreenLiveAdapter(getActivity());
        this.spGasYear.setAdapter(yearAdapter);
        this.spGasYear.setDropDownVerticalOffset(ScreenUtils.dip2px(getActivity(), 30));
        this.spGasYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = Calendar.getInstance().get(Calendar.YEAR) - position;

                if (counter != 0) {
                    preSelectedDay = null;
                    preSelectedMonth = null;
                    setYearMonthDay(rvGasDay, rvGasMonth, rvGasYear, tvGasDay, tvGasMonth, tvGasYear);
                }
            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spGasYear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter = 1;
                return false;
            }
        });

        LinearLayoutManager yearLinearLayoutManager = new LinearLayoutManager(getActivity());
        yearLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvGasYear.setLayoutManager(yearLinearLayoutManager);

        LinearLayoutManager monthLinearLayoutManager = new LinearLayoutManager(getActivity());
        monthLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvGasMonth.setLayoutManager(monthLinearLayoutManager);
        monthAdapter = new RecyclerAdapter(getActivity(), monthList);
        preSelectedMonth = monthAdapter.getLastItem();
//        preSelectedMonth.setTextColor(Color.parseColor("#128ce3"));
        this.rvGasMonth.setAdapter(monthAdapter);
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
        rvGasDay.setLayoutManager(dayLinearLayoutManager);
        dayAdapter = new RecyclerAdapter(getActivity(), dayList);
        preSelectedDay = dayAdapter.getLastItem();
//        preSelectedDay.setTextColor(Color.parseColor("#128ce3"));
        this.rvGasDay.setAdapter(dayAdapter);
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
//                tvGasDate.setText(year + "年" + (month + 1) + "月" + day + "日");
//                queryGas();
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
        tvGasDate.setText(year + "年" + (month + 1) + "月" + day + "日");
    }

    private void setDayList() {
        // 判断年份和月份是否是当前年份或月份
        dayList.clear();
        Calendar calendar = Calendar.getInstance();
        int days;
        if(year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH)) {
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
        for (int i = 1; i < days + 1; i ++) {
            dayList.add(i + "日");
        }

        setYearMonthDay(rvGasMonth, rvGasYear, rvGasDay, tvGasMonth, tvGasYear, tvGasDay);
        dayAdapter.setDatas(dayList);
        dayAdapter.notifyDataSetChanged();
        this.rvGasDay.scrollToPosition(dayList.size() - 1);
    }

    private void setMonthList() {
        monthList.clear();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if(year < currentYear) {
            for (int i = 1; i <= 12; i ++) {
                monthList.add(i + "月");
            }
        } else {
            int currentMonth = calendar.get(Calendar.MONTH);
            for (int i = 1; i <= currentMonth + 1; i ++) {
                monthList.add(i + "月");
            }
        }

        setYearMonthDay(rvGasDay, rvGasYear, rvGasMonth, tvGasDay, tvGasYear, tvGasMonth);
        monthAdapter.setDatas(monthList);
        monthAdapter.notifyDataSetChanged();
        this.rvGasMonth.scrollToPosition(monthList.size() - 1);
    }

    private void setYearList() {
        yearList.clear();
        Calendar calendar = Calendar.getInstance();
        for (int i = calendar.get(Calendar.YEAR); i >= 2015; i --) {
            yearList.add("" + i);
        }

        yearAdapter.setDatas(yearList);
        yearAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGasYear:
                preSelectedDay = null;
                preSelectedMonth = null;
                setYearMonthDay(rvGasDay, rvGasMonth, rvGasYear, tvGasDay, tvGasMonth, tvGasYear);
                break;
            case R.id.tvGasMonth:
                counter = 0;
                setMonthList();
                break;
            case R.id.tvGasDay:
                counter = 0;
                setDayList();
                break;
        }
    }

    private void setYearMonthDay(RecyclerView rvFirstUnchecked, RecyclerView rvSecondUnchecked, RecyclerView rvChecked, TextView tvFirstUnchecked, TextView tvSecondUnchecked,TextView tvChecked) {
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
        lcGasHistory.setDrawGridBackground(false);
        lcGasHistory.setDescription("");
        lcGasHistory.setNoDataTextDescription("You need to provide data for the chart."); // no description text
        lcGasHistory.setTouchEnabled(false);
        lcGasHistory.setDragEnabled(false);
        lcGasHistory.setScaleEnabled(false);


        XAxis xAxis = lcGasHistory.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setDrawLimitLinesBehindData(true);

        YAxis leftAxis = lcGasHistory.getAxisLeft();
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(6);
        leftAxis.setAxisMinValue(0);
//        leftAxis.setSpaceBottom(20f);
        //虚线
        leftAxis.enableGridDashedLine(0f, 100f, 0f);
        leftAxis.setDrawZeroLine(true);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        lcGasHistory.getAxisRight().setEnabled(false);
        // add data
        List<Float> yVal = new ArrayList<>();
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        yVal.add(0.0f);
        setData(5, yVal);
        lcGasHistory.animateY(1500, Easing.EasingOption.Linear);
        lcGasHistory.getLegend().setEnabled(false);
    }

    /**
     * 给表格设置数据
     * @param count  横坐标的个数
     * @param yVal   纵坐标集合
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
        lcGasHistory.setData(data);
    }

    /**
     * 查询气表数据
     */
    private void queryGas() {
        // TODO 设置插座地址
        List<Addr> addressList = new ArrayList<>();
        addressList.add(new Addr("85C62807004B12000000"));

        // TODO 设置时间
        List<Time> timeList = new ArrayList<>();
        timeList.add(new Time("1459440000"));
        timeList.add(new Time("1461137859"));
        GreenLiveController.getInstance().queryGas(getActivity(), addressList, timeList, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("queryGas json======" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if("0".equals(ret)) {

                } else {


                    List<Float> yVal = new ArrayList<>();
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    yVal.add(0.0f);
                    setData(5, yVal);
                    lcGasHistory.animateY(1500, Easing.EasingOption.Linear);
                    lcGasHistory.getLegend().setEnabled(false);
                }
            }

            @Override
            public void onFailed(String json) {
                new ToastUtils(getContext()).showErrorWithStatus(json);
            }
        });
    }
}
