package com.boer.delos.activity.healthylife.sugar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.activity.healthylife.pressure.BloodPressureConnActivity;
import com.boer.delos.activity.healthylife.tool.DealWithValue2;
import com.boer.delos.activity.healthylife.weight.WeightInputActivity;
import com.boer.delos.adapter.BloodSugarGridAdapter;
import com.boer.delos.adapter.BloodSugarListAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.SugarResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.popupWindow.InputBloodValuePopupWindow;
import com.boer.delos.view.popupWindow.ShowYearMonthPopupWindow;
import com.boer.delos.widget.MyGridView;
import com.boer.delos.widget.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.boer.delos.R.id.multiply;
import static com.boer.delos.R.id.null_layout;
import static com.boer.delos.R.id.pullToRefreshListView;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/19 0019 09:00
 * @Modify:
 * @ModifyDate:
 */


public class BloodSugarActivity extends CommonBaseActivity {
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_state)
    TextView mTvState;
    @Bind(R.id.tv_blood_sugar)
    TextView mTvBloodSugar;
    @Bind(R.id.iv_sugar_standard)
    ImageView mIvSugarStandard;
    @Bind(R.id.tvBloodMonth)
    TextView mTvBloodMonth;

    @Bind(R.id.lvMonth)
    MyListView mLvMonth;
    @Bind(R.id.gvBloodDetail)
    MyGridView mGvBloodDetail;
    @Bind(R.id.btn_commit)
    Button mBtnCommit;
    @Bind(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    private SimpleDateFormat dateFormat, dateFormatSecond;
    private int currentYear, currentMonth, day;
    private TimePickerView timePickerView;
    private String stringDate;
    private Date currentDate;

    private String stringDay;
    private int checkYear;
    private int checkMonth;
    private int checkDay;
    private InputBloodValuePopupWindow popupWindow;
    private User mUser;

    private List<SugarResult.SugarBean> mDataLLists;
    protected List<String> mxVals;
    protected List<Float> myVals;

    private List<String> dateList;
    private List<String> gridList;

    private BloodSugarListAdapter listAdapter;
    private BloodSugarGridAdapter gridAdapter;
    private int checkPosition;
    private ShowYearMonthPopupWindow showYearMonthPopupWindow;
    private long mFromTime;
    private long mToTime;

    @Override
    protected int initLayout() {
        return R.layout.activity_blood_sugar;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.health_blood_sugar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable("user");
        }

    }

    @Override
    protected void initData() {
        dateList = new ArrayList<>();
        gridList = new ArrayList<>();
        mDataLLists = new ArrayList<>();
        initDate();
        setListAdapter((currentMonth), Integer.parseInt(stringDay));
        setGridAdapter();
//        initTimerPicker();
        int fromTime = TimeUtil.getTimesMonthmorning(0);
        long toTime = TimeUtil.getCurrentstamp();
        if (mUser != null) {
            querySugarData(String.valueOf(fromTime), String.valueOf(toTime),
                    HealthController.HEATHY_SUGAR, mUser.getId());
            queryRecentSugarData();
        }
        timePickerShow(mTvBloodMonth);
        hideInput();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int fromTime = TimeUtil.getTimesMonthmorning(0);
        long toTime = TimeUtil.getCurrentstamp();
        if (mUser != null) {
            querySugarData(String.valueOf(fromTime), String.valueOf(toTime),
                    HealthController.HEATHY_SUGAR, mUser.getId());
            queryRecentSugarData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nestedScrollView.smoothScrollBy(0, 0);
    }

    @Override
    protected void initAction() {
        initListener();
    }

    @OnClick({R.id.iv_sugar_standard, R.id.ll_month, R.id.btn_commit})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_sugar_standard:
                Intent intent = new Intent();
                intent.setClass(this, BloodSugarMajorActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_month:
                showYearMonthPopupWindow.showPopupWindow();
                break;
            case R.id.btn_commit:
                if (!TextUtils.isEmpty(mUser.getId())
                        && !TextUtils.isEmpty(Constant.USERID)
                        && mUser.getId().equals(Constant.USERID)) {

                    Intent intent1 = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", mUser);
                    intent1.setClass(this, SugarConn2Activity.class);
                    startActivity(intent1);

                } else {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_change_to_current_user));
                    return;
                }
                break;

        }
    }

    private void initChart() {


    }

    //    private void initTimerPicker() {
//        //时间选择器
//        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
//        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
//        timePickerView.setCyclic(true);
//        timePickerView.setCancelable(true);
//        timePickerView.setRange(1900, 2100);
//        timePickerView.setTime(new Date());
//
//    }
    private void timePickerShow(final TextView editText) {

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

                int year = Integer.parseInt(startTime);
                int month = Integer.parseInt(endTime);

                mFromTime = TimeUtil.getTargetTimeStamp(year, month, 1, 0, 0, 0);
                int daysOfMonth = TimeUtil.getDaysByYearMonth(year, month);
                mToTime = TimeUtil.getTargetTimeStamp(year, month, daysOfMonth, 23, 59, 59);

                editText.setText(startTime + "-" + endTime);
                querySugarData(mFromTime + "", mToTime + "", HealthController.HEATHY_SUGAR, mUser.getId());

                currentMonth=month;
                stringDay=daysOfMonth+"";
                setListAdapter((currentMonth), Integer.parseInt(stringDay));
            }
        });

    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        currentDate = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatSecond = new SimpleDateFormat("yyyy-MM");
        stringDate = dateFormat.format(currentDate);
//        this.tvBloodDate.setText(stringDate);
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;//月份从0开始
        if (currentMonth < 9) {
            mTvBloodMonth.setText(String.valueOf(currentYear) + "-0" + String.valueOf(currentMonth));

        } else {
            mTvBloodMonth.setText(String.valueOf(currentYear) + "-" + String.valueOf(currentMonth));

        }

        stringDay = stringDate.substring(8);
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
            mLvMonth.setAdapter(listAdapter);
        } else {
            listAdapter.setDatas(dateList);
            listAdapter.notifyDataSetChanged();
        }

    }

    private void setGridAdapter() {
        gridList.clear();
        for (int i = 0; i < dateList.size() * 7; i++) {
            gridList.add("--");
        }
        if (gridAdapter == null) {
            gridAdapter = new BloodSugarGridAdapter(this);
            gridAdapter.setList(gridList);
            mGvBloodDetail.setAdapter(gridAdapter);
        } else {
            gridAdapter.setList(gridList);
            gridAdapter.notifyDataSetChanged();
        }


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

    private void initListener() {
        mGvBloodDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                checkPosition = position;
                if (TextUtils.isEmpty(mUser.getId())
                        || TextUtils.isEmpty(Constant.USERID)
                        || !mUser.getId().equals(Constant.USERID)) {

                    toastUtils.showInfoWithStatus(getString(R.string.toast_change_to_current_user));
                    return;
                }

                int day = position / 7;
                int period = position % 7;
                if (period == 0 && position % 7 != 0) {
                    period = 7 - 1;
                }
                int hour = 7;
                if (period <= 3) {
                    hour += period * 2; //手动输入的时间为对应天的0点
                } else {
                    hour += period * 2 + 2;
                }
                long measureTime = TimeUtil.getTargetTimeStamp(currentYear, currentMonth,
                        day + 1, hour, 0, 0);

                if (measureTime > TimeUtil.getCurrentstamp()) {
//                                    toastUtils.showInfoWithStatus(getString(R.string.text_over_current_time));
                    ToastHelper.showShortMsg(getString(R.string.text_over_current_time));
                    return;
                }

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                popupWindow = new InputBloodValuePopupWindow(BloodSugarActivity.this,
                        new InputBloodValuePopupWindow.ClickResultListener() {
                            @Override
                            public void ClickResult(String input) {
                                int day = position / 7;
                                int period = position % 7;
                                if (period == 0 && position % 7 != 0) {
                                    period = 7 - 1;
                                }
                                int hour = 7;
                                if (period <= 3) {
                                    hour += period * 2; //手动输入的时间为对应天的0点
                                } else {
                                    hour += period * 2 + 2;
                                }
                                long measureTime = TimeUtil.getTargetTimeStamp(currentYear, currentMonth,
                                        day + 1, hour, 0, 0);

                                if (measureTime > TimeUtil.getCurrentstamp()) {
//                                    toastUtils.showInfoWithStatus(getString(R.string.text_over_current_time));
                                    ToastHelper.showShortMsg(getString(R.string.text_over_current_time));
                                    return;
                                }
                                if (gridList.get(position).contains("--")) {
                                    reportBloodSugar((mUser == null || mUser.getId().equals(Constant.USERID)) ? "0" : mUser.getId(),
                                            String.valueOf(period), String.valueOf(measureTime), input, position);
                                } else
                                    updateBloodSugar((mUser == null || mUser.getId().equals(Constant.USERID)) ? "0" : mUser.getId(),
                                            String.valueOf(period), String.valueOf(measureTime), input, position);
                                //直接赋值
                                gridList.set(position, input); //list有值
                                gridAdapter.notifyDataSetChanged();

                            }
                        });
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                popupWindow.getEtBloodValue().requestFocus();
                popupWindow.update();

                InputMethodManager imm = (InputMethodManager) getSystemService(BloodSugarActivity.this.INPUT_METHOD_SERVICE);
                imm.showSoftInput(popupWindow.getEtBloodValue(), InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
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
                                        ? getString(R.string.toast_query_failure) : result.getMsg());
                                return;
                            }
                            mDataLLists.clear();
                            mDataLLists.addAll(result.getData());
                            dealWithData(mDataLLists);
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

    private void reportBloodSugar(String id, String timePeriod, String measureDate,
                                  final String input, final int position) {
        if (StringUtil.isEmpty(input)) {
            BaseApplication.showToast(getString(R.string.toast_input_please)
                    + getString(R.string.health_blood_sugar));

        }
        HealthController.getInstance().reportBloodSugar(this, id, timePeriod, measureDate,
                input, "", Constant.CURRENTHOSTID, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.e("reportBloodSugar_Json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            toastUtils.showSuccessWithStatus(getString(R.string.toast_update_success));
                            gridList.set(position, input);
                            gridAdapter.notifyDataSetChanged();
                        } else {
                            String msg = JsonUtil.parseString(Json, "msg");
                            toastUtils.showErrorWithStatus(msg);
                            gridList.set(position, "--");
                            gridAdapter.notifyDataSetChanged();
                        }
                        popupWindow.dismiss();
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (toastUtils != null) {
                            toastUtils.showErrorWithStatus(getString(R.string.toast_update_failure) + Json);
                            gridList.set(position, "--");
                            gridAdapter.notifyDataSetChanged();
                        }
                        popupWindow.dismiss();
                    }
                });
    }

    private void updateBloodSugar(String id, String timePeriod,
                                  String measureDate, String input, final int position) {
        HealthController.getInstance().updateSugar(this, id, timePeriod,
                measureDate, input, "", Constant.CURRENTHOSTID, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        String ret = JsonUtil.parseString(json, "ret");
                        if ("0".equals(ret)) {
                            popupWindow.dismiss();
                            toastUtils.showSuccessWithStatus(getString(R.string.toast_update_success));

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
                            toastUtils.showSuccessWithStatus(getString(R.string.toast_update_success));
                            gridList.set(position, "--");
                            gridAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void dealWithData(List<SugarResult.SugarBean> list) {
        gridList.clear();
        for (int i = 0; i < dateList.size() * 7; i++) {
            gridList.add("--");
        }
        for (SugarResult.SugarBean bean : list) {
            int period = bean.getMeasuretime();
            String time = TimeUtil.formatStamp2Time(bean.getMesuredate(), "yyyy-MM-dd- HH:mm");

            String[] times = time.split("-");
            //年月相同
            if (times[0].equals(currentYear + "")
                    && (times[1].equals("" + currentMonth) || times[1].equals("0" + currentMonth))) {
                int day = Integer.valueOf(times[2]);
                int pos = 7 * (day - 1) + period;
                gridList.set(pos, String.valueOf(bean.getValue()));
            }
        }

        gridAdapter.notifyDataSetChanged();

//        SugarResult.SugarBean bean = list.get(0);
//        String time = TimeUtil.formatStamp2Time(bean.getMesuredate(), "yyyy-MM-dd HH:mm:ss");
//        String time2 = DealWithValue2.judgeBeforeOrAfterDinner(this, bean.getMeasuretime());
//        mTvDate.setText(time + " " + time2);
//        mTvBloodSugar.setText(String.valueOf(bean.getValue()) + " ");
//        DealWithValue2.judgeBSColor(this, mTvState, bean.getValue(), false);
    }

    private void queryRecentSugarData(){
        long fromTime = TimeUtil.getCurrentstamp();
        HealthController.getInstance().queryRecentHealth(this, fromTime+"", HealthController.HEATHY_SUGAR
                , "1", mUser.getId(), new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            SugarResult result = GsonUtil.getObject(json, SugarResult.class);
                            if (result.getRet() != 0) {
                                ToastHelper.showShortMsg(getString(R.string.toast_query_failure));
                                return;
                            }
                            SugarResult.SugarBean bean=result.getData().get(0);
                            String time = TimeUtil.formatStamp2Time(bean.getMesuredate(), "yyyy-MM-dd HH:mm:ss");
                            String time2 = DealWithValue2.judgeBeforeOrAfterDinner(BloodSugarActivity.this, bean.getMeasuretime());
                            mTvDate.setText(time + " " + time2);
                            mTvBloodSugar.setText(String.valueOf(bean.getValue()) + " ");
                            DealWithValue2.judgeBSColor(BloodSugarActivity.this, mTvState, bean.getValue(), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {

                    }
                });
    }

}
