package com.boer.delos.activity.healthylife.sugar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.activity.healthylife.BaseHealthyLifeActivity;
import com.boer.delos.activity.healthylife.tool.DealWithValues;
import com.boer.delos.adapter.BloodSugarGridAdapter;
import com.boer.delos.adapter.BloodSugarListAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.interf.ISimpleInterface2;
import com.boer.delos.model.SugarResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.popupWindow.InputBloodValuePopupWindow;
import com.boer.delos.widget.MyGridView;
import com.boer.delos.widget.MyListView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.boer.delos.R.id.bloodChart;

/**
 * @Desciprion:血糖 界面 ---手动输入+饼状图
 * Created by wangkai on 16/5/16.
 */
public class BloodSugarListeningActivity extends BaseHealthyLifeActivity
        implements View.OnClickListener, ISimpleInterface2 {

    @Bind(R.id.ic_avatar)
    CircleImageView mIcAvatar;
    @Bind(R.id.user_drop)
    CheckedTextView mUserDrop;
    @Bind(R.id.tv_userName)
    TextView mTvUserName;

    private TextView tvBloodDate;
    private TextView tvBloodData;
    private ImageView ivBloodHelp;
    private TextView tvBloodMonth;
    private MyListView lvMonth;
    private MyGridView gvBloodDetail;
    private PieChart mChart;
    private SeekBar mSeekBarY;
    private SeekBar mSeekBarX;
    private TextView tvX;
    private TextView tvY;

    /*头部*/
    private ImageView ivAvatar;
    private CheckedTextView ctv_user_drop;
    private TextView tvUserName;
    private List<String> mSpinnerList;

    private Typeface tf;
    final int[] VORDIPLOM_COLORS = {
            Color.rgb(255, 48, 0), Color.rgb(76, 173, 16), Color.rgb(227, 150, 15),
    };
    protected List<String> mPartiesX;
    protected List<Float> mPartiesY;
    //饼状图的展示数据
    private float sugatH = 0;
    private float sugatN = 0;
    private float sugatL = 0;

    private SimpleDateFormat dateFormat, dateFormatSecond;
    private int currentYear, currentMonth, day;
    private TimePickerView timePickerView;
    private String stringDate;
    private Date currentDate;
    private List<String> dateList = new ArrayList<>();
    private List<String> gridList = new ArrayList<>();
    private BloodSugarListAdapter listAdapter;
    private BloodSugarGridAdapter gridAdapter;
    private String stringDay;
    private int checkYear;
    private int checkMonth;
    private int checkDay;
    private InputBloodValuePopupWindow popupWindow;
    private View view;
    private int checkPosition;
    private User user;
    private List<SugarResult.SugarBean> mSugarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_blood_details, null);
        setContentView(view);
        ButterKnife.bind(this);

        initTopBar(R.string.homepage_blood_glucose, null, true, false);
        initView();
        initChart1();
        initData();
        initListener();
        user = Constant.LOGIN_USER;
        settingHeader();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int fromTime = TimeUtil.getTimesMonthmorning(0);
        int toTime = TimeUtil.getTimesMonthnight(0);

        querySugarData(String.valueOf(fromTime), String.valueOf(TimeUtil.getCurrentstamp()),
                HealthController.HEATHY_SUGAR, user.getId());
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

    private void initView() {
        tvY = (TextView) findViewById(R.id.tvYMax);
        tvX = (TextView) findViewById(R.id.tvXMax);
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        mChart = (PieChart) findViewById(bloodChart);

        gvBloodDetail = (MyGridView) findViewById(R.id.gvBloodDetail);
        lvMonth = (MyListView) findViewById(R.id.lvMonth);
        tvBloodMonth = (TextView) findViewById(R.id.tvBloodMonth);
        ivBloodHelp = (ImageView) findViewById(R.id.ivBloodHelp);
        tvBloodData = (TextView) findViewById(R.id.tvBloodData);
        tvBloodDate = (TextView) findViewById(R.id.tvBloodDate);

        /*----头部----------*/
        ivAvatar = (ImageView) findViewById(R.id.ic_avatar);
        tvUserName = (TextView) findViewById(R.id.tv_userName);
//        ctv_user_drop = (CheckedTextView) findViewById(R.id.user_drop);
        /*------头部结束-----*/

        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        timePickerView.setCyclic(true);
        timePickerView.setCancelable(true);

        mSugarList = new ArrayList<>();
        mPartiesX = new ArrayList<>();
        mPartiesY = new ArrayList<>();

        this.ivBloodHelp.setOnClickListener(this);
        this.tvBloodMonth.setOnClickListener(this);
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        currentDate = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatSecond = new SimpleDateFormat("yyyy-MM");
        stringDate = dateFormat.format(currentDate);
//        this.tvBloodDate.setText(stringDate);
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;//月份从0开始
        if (currentMonth < 9) {
            this.tvBloodMonth.setText(String.valueOf(currentYear) + "-0" + String.valueOf(currentMonth));

        } else {
            this.tvBloodMonth.setText(String.valueOf(currentYear) + "-" + String.valueOf(currentMonth));

        }

        stringDay = stringDate.substring(8);
        setListAdapter((currentMonth), Integer.parseInt(stringDay));
    }

    private void initListener() {
        gvBloodDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                checkPosition = position;
                popupWindow = new InputBloodValuePopupWindow(BloodSugarListeningActivity.this, new InputBloodValuePopupWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(String input) {
                        int day = position / 7;
                        int period = position % 7;
                        if (period == 0 && position != 0) {
                            period = 7 - 1;
                        }
                        int hour = 7;
                        if (period <= 3) {
                            hour += period * 2; //手动输入的时间为对应天的0点
                        } else {
                            hour += period * 2 + 2;
                        }
                        long measureTime = TimeUtil.getTargetTimeStamp(currentYear, currentMonth, day + 1, hour, 0, 0);

                        if (gridList.get(position).contains("--")) {
                            reportBloodSugar("0", String.valueOf(period), String.valueOf(measureTime), input, position);
                        } else
                            updateBloodSugar("0", String.valueOf(period), String.valueOf(measureTime), input, position);
//                   //直接赋值
                        gridList.set(position, input); //list有值
                        gridAdapter.notifyDataSetChanged();
//
                    }
                });
                popupWindow.showAtLocation(mChart, Gravity.CENTER, 0, 0);
                popupWindow.getEtBloodValue().requestFocus();
                popupWindow.update();

                InputMethodManager imm = (InputMethodManager) getSystemService(BloodSugarListeningActivity.this.INPUT_METHOD_SERVICE);
                imm.showSoftInput(popupWindow.getEtBloodValue(), InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
    }

    private void reportBloodSugar(String id, String timePeriod, String measureDate,
                                  String input, final int position) {
        if (StringUtil.isEmpty(input)) {
            BaseApplication.showToast("请输入血糖值");
        }
        HealthController.getInstance().reportBloodSugar(this, id, timePeriod, measureDate,
                input, "", Constant.CURRENTHOSTID,new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.e("reportBloodSugar_Json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            popupWindow.dismiss();
                            toastUtils.showSuccessWithStatus("上传成功");
                            //TODO 修改对应的item的值
                        } else {
                            String msg = JsonUtil.parseString(Json, "msg");
                            toastUtils.showErrorWithStatus(msg);
                            gridList.set(position, "--");
                            gridAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (toastUtils != null) {
                            toastUtils.showErrorWithStatus("上传失败，请重新上传");
                            gridList.set(position, "--");
                            gridAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void updateBloodSugar(String id, String timePeriod,
                                  String measureDate, String input, final int position) {
        HealthController.getInstance().updateSugar(this, id, timePeriod,
                measureDate, input, "", Constant.CURRENTHOSTID,new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        String ret = JsonUtil.parseString(json, "ret");
                        if ("0".equals(ret)) {
                            popupWindow.dismiss();
                            toastUtils.showSuccessWithStatus("更新成功");
                            //TODO 修改对应的item的值
                        } else {
                            String msg = JsonUtil.parseString(json, "msg");
                            toastUtils.showErrorWithStatus(msg);
                            gridList.set(position, "--");
                            gridAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils != null) {
                            toastUtils.showErrorWithStatus("更新失败，请重新上传");
                            gridList.set(position, "--");
                            gridAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    private void setListAdapter(int month, int day) {
        dateList.clear();
        for (int i = 1; i <= day; i++) {
//                  temp+=i+",";
            dateList.add(String.valueOf(month) + "." + i);
        }

        if (listAdapter == null) {
            listAdapter = new BloodSugarListAdapter(this);
            listAdapter.setDatas(dateList);
            lvMonth.setAdapter(listAdapter);
        } else {
            listAdapter.setDatas(dateList);
            listAdapter.notifyDataSetChanged();
        }

        setGridAdapter();
    }

    private void setGridAdapter() {
        gridList.clear();
        for (int i = 0; i < dateList.size() * 7; i++) {
            gridList.add("--");
        }
        if (gridAdapter == null) {
            gridAdapter = new BloodSugarGridAdapter(this);
            gridAdapter.setList(gridList);
            gvBloodDetail.setAdapter(gridAdapter);
        } else {
            gridAdapter.setList(gridList);
            gridAdapter.notifyDataSetChanged();
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBloodHelp:
//                toastUtils.showInfoWithStatus("显示当前血糖");
                Intent intent = new Intent(BloodSugarListeningActivity.this, BloodSugarMajorActivity.class);
                Bundle bundle = new Bundle();

                if (mSugarList != null || mSugarList.size() != 0) {
                    SugarResult.SugarBean SugarBean = mSugarList.get(mSugarList.size() - 1);
                    bundle.putInt("period", SugarBean.getMeasuretime());
                    bundle.putLong("measureDate", SugarBean.getMesuredate());
                    bundle.putFloat("value", SugarBean.getValue());
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                break;
            case R.id.tvBloodMonth:
                showTimePicker(new Date(), tvBloodMonth);
                break;
        }
    }

    private void showTimePicker(final Date date1, final TextView tv) {
        timePickerView.setTime(date1);
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                if (currentDate.getTime() < date.getTime()) {
                    toastUtils.showErrorWithStatus("日期无效");
                } else {
                    tvBloodMonth.setText(dateFormat.format(date).substring(0, 7));
                    tvBloodDate.setText(dateFormat.format(date));
                    checkYear = Integer.parseInt(dateFormat.format(date).substring(0, 4));
                    checkMonth = Integer.parseInt(dateFormat.format(date).substring(5, 7));
                    checkDay = Integer.parseInt(dateFormat.format(date).substring(8));
                    int days = setDateList(checkYear, checkMonth);
                    setListAdapter(checkMonth, days);

                    long fromTime = TimeUtil.getTargetTimeStamp(currentYear, currentMonth, 0, 0, 0, 0);
                    long toTime = TimeUtil.getTargetTimeStamp(currentYear, currentMonth, 0, 0, 0, 0);
                    querySugarData(String.valueOf(fromTime), String.valueOf(toTime),
                            HealthController.HEATHY_SUGAR, Constant.USERID);

                }
            }
        });
        timePickerView.show();
    }

    private int setDateList(int year, int month) {
        L.e("传递进来的year===" + year + "   month===" + month);
        Calendar calendar = Calendar.getInstance();
        int days;
        //此处的month由滚筒日期控件传递过来的值，是从1开始，而calendar.get(Calendar.Month)是从0开始的
        if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH) + 1) {
            days = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            try {
                Date date = dateFormatSecond.parse(year + "-" + month);
                calendar.setTime(date);
            } catch (ParseException e) {
                L.e("日期解析异常");
                e.printStackTrace();
            }
            days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        }
        return days;
    }


    private void initChart1() {
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(30f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        setData(mPartiesX, mPartiesY);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        l.setTextSize(14f);
        l.setTextColor(R.color.light_gray_line);
        l.setFormToTextSpace(5.f);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    private void setData(List<String> mPartiesX, List<Float> mPartiesY) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (String s : mPartiesX) {
            xVals.add(s);
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        int i = 0;
        for (float f : mPartiesY) {
            yVals1.add(new Entry(f, i));
            i++;
        }

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        L.e("dataSet_Json===" + new Gson().toJson(dataSet));
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
        mChart.setData(data);
        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }


    private void querySugarData(String fromTime, String toTime, String healthyType, String userId) {
        HealthController.getInstance().queryHealthyTime2TimeData(this, fromTime, toTime,
                healthyType, userId, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            SugarResult result = GsonUtil.getObject(json, SugarResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(StringUtil.isEmail(result.getMsg())
                                        ? "查询数据失败，请重试" : result.getMsg());
                                return;
                            }
                            mSugarList.clear();
                            mSugarList.addAll(result.getData());
                            dealWithData(mSugarList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            gridList.clear();
                            for (int i = 0; i < dateList.size() * 7; i++) {
                                gridList.add("--");
                            }
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        gridList.clear();
                        for (int i = 0; i < dateList.size() * 7; i++) {
                            gridList.add("--");
                        }
                    }
                });
    }

    private void dealWithData(List<SugarResult.SugarBean> list) {
        gridList.clear();
        for (int i = 0; i < dateList.size() * 7; i++) {
            gridList.add("--");
        }
        mPartiesX.clear();
        mPartiesY.clear();
        if (list == null || list.size() == 0) {
            mPartiesX.add("偏高" + 0 + "次");
            mPartiesX.add("正常" + 0 + "次");
            mPartiesX.add("偏低" + 0 + "次");
            mPartiesY.add(1 / 3f);
            mPartiesY.add(1 / 3f);
            mPartiesY.add(1 / 3f);

            setData(mPartiesX, mPartiesY);
            gridAdapter.notifyDataSetChanged();

            return;
        }
        for (SugarResult.SugarBean bean : list) {
            int period = bean.getMeasuretime();
            String time = TimeUtil.formatStamp2Time(bean.getMesuredate(), "yyyy-MM-dd- HH:mm");

            String[] times = time.split("-");
            //年月相同
            if (times[0].equals(currentYear + "")
                    && (times[1].equals("" + currentMonth)
                    || times[1].equals("0" + currentMonth))) {
                int day = Integer.valueOf(times[2]);
                int pos = day * period;
                gridList.set(pos, String.valueOf(bean.getValue()));
            }

            int i = DealWithValues.judgeBloodSugarState(bean.getValue());
            switch (i) {
                case 0:
                    sugatH++;
                    break;
                case 1:
                    sugatN++;
                    break;
                case 2:
                    sugatL++;
                    break;
            }
        }
        mPartiesX.add("偏高" + sugatH + "次");
        mPartiesY.add(sugatH / list.size());

        mPartiesX.add("正常" + sugatN + "次");
        mPartiesY.add(sugatN / list.size());

        mPartiesX.add("偏低" + sugatL + "次");
        mPartiesY.add(sugatL / list.size());

        setData(mPartiesX, mPartiesY);
        gridAdapter.notifyDataSetChanged();
        SugarResult.SugarBean bean = list.get(list.size() - 1);
        String time = TimeUtil.formatStamp2Time(bean.getMesuredate(), "yyyy-MM-dd");
        tvBloodDate.setText(time);
        tvBloodDate.setVisibility(View.VISIBLE);
        tvBloodData.setText(String.valueOf(bean.getValue()));
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


        long fromTime = TimeUtil.getTargetTimeStamp(currentYear, currentMonth, 1, 0, 0, 0);
        long toTime = TimeUtil.getTargetTimeStamp(currentYear, currentMonth + 1, 1, 0, 0, 0);
        querySugarData(String.valueOf(fromTime), String.valueOf(toTime),
                HealthController.HEATHY_SUGAR, user.getId());

    }
}
