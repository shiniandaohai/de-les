package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Addr;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.GreenSocketResult;
import com.boer.delos.model.Time;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.green.GreenLiveController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.MPandroidChartHelper;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.utils.ToastHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.id.list;
import static com.boer.delos.utils.GsonUtil.getObject;


/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/13 0013 11:05
 * @Modify:
 * @ModifyDate:
 */


public class SocketControlActivity extends CommonBaseActivity {

    @Bind(R.id.tvElectricYear)
    TextView mTvElectricYear;
    @Bind(R.id.view_year)
    View mViewYear;
    @Bind(R.id.tvElectricMonth)
    TextView mTvElectricMonth;
    @Bind(R.id.view_month)
    View mViewMonth;
    @Bind(R.id.tvElectricDay)
    TextView mTvElectricDay;
    @Bind(R.id.view_day)
    View mViewDay;
    @Bind(R.id.tv_date_select)
    TextView mTvDateSelect;
    @Bind(R.id.ctv_choice)
    CheckedTextView mCtvChoice;
    @Bind(R.id.lcElectricityHistory)
    LineChart mLineChart;
    @Bind(R.id.ll_day)
    LinearLayout mLlDay;

    @Bind(R.id.tv_energy_all)
    TextView mTvEnergyAll;
    @Bind(R.id.tv_ammeter)
    TextView mTvAmmeter;
    @Bind(R.id.tv_voltage)
    TextView mTvVoltage;
    @Bind(R.id.tv_power)
    TextView mTvPower;
    @Bind(R.id.tv_energy)
    TextView mTvEnergy;
    @Bind(R.id.ctv_socket_open)
    CheckedTextView mCtvSocketOpen;

    private List<String> xVals;
    private List<Float> yVals;
    private List<Addr> mAddrList;
    private HashMap<String, Float> listShowKwH;

    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    private DeviceStatusValue mValues;

    private TimePickerView timePickerView;
    private String mState = "day";//默认是日
    private List<Time> mTimes = new ArrayList<>();
    private List<Device> devices = new ArrayList<Device>();
    private String tag; //标记是否是常用设备

    @Override
    protected int initLayout() {
        return R.layout.activity_device_socket;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
        } else tlTitleLayout.setTitle(R.string.socket_name);
        if (mDeviceRelate != null) {
            mDevice = mDeviceRelate.getDeviceProp();
            mValues = mDeviceRelate.getDeviceStatus().getValue();
            if (mValues == null) {
                mValues = new DeviceStatusValue();
            }

            Addr addr = new Addr();
            addr.setName(mDevice.getName());
            addr.setAddr(mDevice.getAddr());
            mAddrList = new ArrayList<>();
            mAddrList.add(addr);
            tlTitleLayout.setTitle(mDevice.getName());
            tag = mDeviceRelate.getDeviceProp().getFavorite();
            if (!TextUtils.isEmpty(mDeviceRelate.getDeviceProp().getFavorite())
                    && tag.equals("1")) {
                tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
            } else
                tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
        } else tlTitleLayout.setTitle(R.string.socket_name);
    }

    @Override
    protected void initData() {
        xVals = new ArrayList<>();
        yVals = new ArrayList<>();

        MPandroidChartHelper.initLineChart(mLineChart, true,12);
        MPandroidChartHelper.setLineChart(this, mLineChart, xVals, yVals);
        mTimes.clear();

        initPicker();
        settingTimes(TimeUtil.getCurrentstamp(), mState);
        checkSocketIsOpen();
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void rightViewClick() {
        if (tag.equals("1")) {
            tag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
        } else if (tag.equals("0")) {
            tag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
        }
        //保存状态，并发送后台
        if (mDevice != null) {  devices.clear();
            mDevice.setFavorite(tag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

    @OnClick({R.id.ll_year, R.id.ll_month, R.id.ll_day,
            R.id.tv_date_select, R.id.ctv_choice, R.id.ctv_socket_open})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_year:
                changeView(0);
                settingTimes(TimeUtil.getCurrentstamp(), mState);
                break;
            case R.id.ll_month:
                changeView(1);
                settingTimes(TimeUtil.getCurrentstamp(), mState);
                break;
            case R.id.ll_day:
                changeView(2);
                settingTimes(TimeUtil.getCurrentstamp(), mState);
                break;
            case R.id.ctv_choice:
            case R.id.tv_date_select:
                timePickerShow();
                break;
            case R.id.ctv_socket_open:
                try {
                    if (mDeviceRelate.getDeviceStatus().getOffline() == 1) {
                        toastUtils.showInfoWithStatus(getString(R.string.toast_device_offline));
                        return;
                    }
                } catch (Exception e) {

                }
                mCtvSocketOpen.toggle();
                sendDeviceControl();
                break;
        }
    }

    private void changeView(int position) {
        mViewYear.setVisibility(View.GONE);
        mViewMonth.setVisibility(View.GONE);
        mViewDay.setVisibility(View.GONE);

        mTvElectricYear.setTextColor(getResources().getColor(R.color.gray_et_text));
        mTvElectricMonth.setTextColor(getResources().getColor(R.color.gray_et_text));
        mTvElectricDay.setTextColor(getResources().getColor(R.color.gray_et_text));
        switch (position) {
            case 0:
                mState = "year";
                mViewYear.setVisibility(View.VISIBLE);
                mTvElectricYear.setTextColor(getResources().getColor(R.color.blue_text_water));
                break;
            case 1:
                mState = "month";
                mViewMonth.setVisibility(View.VISIBLE);
                mTvElectricMonth.setTextColor(getResources().getColor(R.color.blue_text_water));
                break;
            case 2:
                mState = "day";
                mViewDay.setVisibility(View.VISIBLE);
                mTvElectricDay.setTextColor(getResources().getColor(R.color.blue_text_water));
                break;
        }

    }

    private void initPicker() {
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView.setCyclic(true);
        timePickerView.setCancelable(true);
        timePickerView.setRange(1900, 2100);
        timePickerView.setTime(new Date());
    }

    private void timePickerShow() {
        //时间选择器
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Date currentDate = new Date();
                if (date.after(currentDate)) {//如果选中时间比现在时间靠后，提示
                    ToastHelper.showShortMsg(getString(R.string.text_over_current_time));
                    return;
                }
                long time = TimeUtil.getTargetTimeStamp(date);

                settingTimes(time, mState);

            }
        });
        timePickerView.show();
    }

    private synchronized void settingTimes(long time, String state) {
        String showTime = TimeUtil.formatStamp2Time(time, "yyyy-MM-dd-HH-mm");
        String tempTime[] = showTime.split("-");

        int year = Integer.parseInt(tempTime[0]);
        int month = Integer.parseInt(tempTime[1]);
        int day = Integer.parseInt(tempTime[2]);
        int hour = Integer.parseInt(tempTime[3]);
        int min = Integer.parseInt(tempTime[4]);
        StringBuffer sb = new StringBuffer();
        sb.append(year).append("年").append(month).append("月").append(day).append("日");
        mTvDateSelect.setText(sb.toString());
        mTimes.clear();
        switch (state) {
            case "day":
                //判断是否是当天
                if (TimeUtil.isToday(time)) {
                    long stamp = TimeUtil.getTimesmorning(0);

                    for (int i = 0; i <= hour + 1; i++) {
                        if (min < 30 && i == hour + 1) {
                            continue;
                        }
                        mTimes.add(new Time(String.valueOf((stamp + i * 60 * 60))));

                    }

                } else { //非当前天
                    time = TimeUtil.getTargetTimeStamp(year, month, day, 0, 0, 0);
                    for (int i = 0; i <= 24; i++) {
                        mTimes.add(new Time(String.valueOf((time + i * 60 * 60))));

                    }
                }


                break;
            case "month":
                //判断是否是当月
                if (TimeUtil.isThisMonth(time)) {
                    long stamp = TimeUtil.getTimesMonthmorning(0);
                    mTimes.add(new Time(String.valueOf(stamp)));

                    for (int i = 1; i <= TimeUtil.getDaysByYearMonth(year, month); i++) {
                        if (i > day) {
                            break;
                        }
                        mTimes.add(new Time(String.valueOf(TimeUtil.getTargetTimeStamp(year, month, i, 23, 59, 59))));

                    }
                    mTimes.add(new Time(String.valueOf(TimeUtil.getCurrentstamp())));

                } else { //非当前月
                    long stamp = TimeUtil.getTargetTimeStamp(year, month, 0, 0, 0, 0);
                    mTimes.add(new Time(String.valueOf(stamp)));

                    for (int i = 1; i <= TimeUtil.getDaysByYearMonth(year, month); i++) {
                        mTimes.add(new Time(String.valueOf(TimeUtil.getTargetTimeStamp(year, month, i, 23, 59, 59))));

                    }
                }
                mTvDateSelect.setText(mTvDateSelect.getText().toString().split("月")[0] + "月");
                break;
            case "year":
                mTimes.add(new Time(String.valueOf(TimeUtil.getTargetTimeStamp(year, 1, 0, 0, 0, 0))));

                if (TimeUtil.isThisYear(time)) {
                    for (int i = 1; i < month; i++) {
                        mTimes.add(new Time(String.valueOf(TimeUtil.getTargetTimeStamp(year, i,
                                TimeUtil.getDaysByYearMonth(year, i), 23, 59, 59))));

                    }
                    mTimes.add(new Time(String.valueOf(TimeUtil.getCurrentstamp())));

                } else {
                    for (int i = 1; i <= 12; i++) {
                        mTimes.add(new Time(String.valueOf(TimeUtil.getTargetTimeStamp(year, i,
                                TimeUtil.getDaysByYearMonth(year, i), 23, 59, 59))));
                    }
                    mTimes.add(new Time(String.valueOf(TimeUtil.getTargetTimeStamp(year + 1, 1, 0, 0, 0, 0))));

                }

                mTvDateSelect.setText(mTvDateSelect.getText().toString().substring(0, 5));
                break;
            default:

                break;

        }
        querySocket(mTimes);
    }

    /**
     * 查询插座数据
     */

    private void querySocket(List<Time> timeList) {

        GreenLiveController.getInstance().querySocket(this, mAddrList, timeList, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.d("querySocket json======" + Json);
                GreenSocketResult result = getObject(Json, GreenSocketResult.class);
                if (result.getRet() == 0) {
                    // 设置表格数据
                    socketDataDealWith(result.getResponse());

//                    updateChart(xVals, yVals);

                } else {
                    // 设置柱形图barChart
                    yVals.add(0.0f);
                    updateChart(xVals, yVals);
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    /**
     * 处理插座的数据
     *
     * @param
     */
    private void socketDataDealWith(List<GreenSocketResult.ResponseBean> responseBean) {
        List<Float> mapResult = new ArrayList<>();
        int index = 0;
        for (GreenSocketResult.ResponseBean bean : responseBean) {
            String key = mTimes.get(index).getTime();
            if (bean == null || TextUtils.isEmpty(bean.getPayload())) {
                mapResult.add(0f);
                index++;
                continue;
            }
            String payload = bean.getPayload();
            GreenSocketResult.SocketData socket = GsonUtil.getObject(payload, GreenSocketResult.SocketData.class);
            if (socket.getValue() == null || TextUtils.isEmpty(socket.getValue().getEnergy())) {
                mapResult.add(0f);
                index++;
                continue;
            }
            mapResult.add(Float.valueOf(socket.getValue().getEnergy()));
            index++;
            if (index == responseBean.size() - 1) {
                updateUI(mapResult.get(mapResult.size() - 1) - mapResult.get(0), socket);
            }

        }
        socketDataDealWith2(mapResult);


    }

    private void socketDataDealWith2(List<Float> mapResult) {
        float preResult = 0.0f;
        String format = "HH:mm";
        switch (mState) {
            case "year":
                format = "MM";
                break;
            case "month":
                format = "MM.dd";
                break;
            case "day":
                format = "HH:mm";
                break;

        }

        xVals.clear();
        yVals.clear();
        int index = 0;
        for (float value : mapResult) {
            long time = Long.valueOf(mTimes.get(index).getTime());
            String xTemp = TimeUtil.formatStamp2Time(time, format);
            xVals.add(xTemp);
            float yTmep = value - preResult;
            yVals.add(decimal(yTmep, 3));
            preResult = value;
            index++;
        }
        xVals.remove(0);
        yVals.remove(0);
        updateChart(xVals, yVals);
    }

    private void updateChart(List<String> xVals, List<Float> yVals) {

        MPandroidChartHelper.setLineChart(this, mLineChart, xVals, yVals);

    }

    private void updateUI(float energy, GreenSocketResult.SocketData socket) {
        try {
            mTvEnergyAll.setText(String.format(getString(R.string.text_elect_amount),
                    saveValidDigits(String.valueOf(energy), "kWh")));

            mTvAmmeter.setText(socket.getValue().getI() + "A");
            mTvVoltage.setText(socket.getValue().getU() + "V");
            mTvPower.setText(socket.getValue().getP() + "W");
            mTvEnergy.setText(yVals.get(yVals.size() - 1) + "kWh");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否有插座打开
     *
     * @param
     * @return
     */
    private void checkSocketIsOpen() {
        try {
            DeviceStatus deviceStatus = mDeviceRelate.getDeviceStatus();
            if (deviceStatus.getValue() != null && deviceStatus.getValue().getState() != null) {
                if (deviceStatus.getOffline() == 1) {
                    mCtvSocketOpen.setEnabled(false);
                    return;
                }
                String state = deviceStatus.getValue().getState();
                if ("1".equals(state)) {
                    mCtvSocketOpen.setChecked(true);
                } else {
                    mCtvSocketOpen.setChecked(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送插座控制命令
     *
     * @param
     */
    private void sendDeviceControl() {

        List<ControlDevice> controlDevices = new ArrayList<>();

        Device device = mDeviceRelate.getDeviceProp();
        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        DeviceStatusValue statusValue = mValues;

        if (TextUtils.isEmpty(statusValue.getState()) || statusValue.getState().equals("0"))
            value.setState("1");
        else if (statusValue.getState().equals("1"))
            value.setState("0");
        controlDevice.setValue(value);

        controlDevices.add(controlDevice);

        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                BaseResult result = GsonUtil.getObject(Json, BaseResult.class);

                if (result.getRet() != 0) {
                    mCtvSocketOpen.toggle();

                } else {
                    mValues.setState(mValues.getState().equals("1") ? "0" : "1");
                }
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }

    private synchronized String saveValidDigits(String data, String type) {
        String result = "0";
        result += type;
        try {

            float oldTempData = Float.valueOf(data);
            float newTempData = 0f;

            if (data.contains(".")) {
                String[] tempString = data.split("\\.");
                if (tempString[0].length() >= 3) {
                    if (tempString[0].length() == 3) {
                        newTempData = decimal(oldTempData, 2);
                        return newTempData + type;
                    } else if (tempString[0].length() == 4) {
                        newTempData = decimal(oldTempData, 1);
                        return newTempData + type;
                    } else if (tempString[0].length() == 5) {
                        newTempData = decimal(oldTempData, 0);
                        return newTempData + type;
                    } else {
                        newTempData = oldTempData / 1000;
                        newTempData = decimal(newTempData, 1);
                    }
                    if (type.contains("k") || type.contains("K")) {
                        return newTempData + "mWh";
                    }
                    return newTempData + "k" + type;
                } else {
                    result = data + type;
                    return result;
                }

            } else {
                if (data.length() > 6) {
                    newTempData = decimal(oldTempData, 1);
                }
                if (type.contains("k") || type.contains("K")) {
                    return newTempData + "mWh";
                }
                return newTempData + "k" + type;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    /**
     * 保存小数点位数
     *
     * @param oldDouble
     * @param scale
     * @return
     */
    private float decimal(float oldDouble, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(oldDouble);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

}
