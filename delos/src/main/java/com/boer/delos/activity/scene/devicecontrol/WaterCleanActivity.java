package com.boer.delos.activity.scene.devicecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Addr;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusResult;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.Time;
import com.boer.delos.model.WaterTDSResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.green.GreenLiveController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:净水器
 * @CreateDate: 2017/4/12 0012 11:15
 * @Modify:
 * @ModifyDate:
 */


public class WaterCleanActivity extends CommonBaseActivity {

    @Bind(R.id.tv_raw_TDS)
    TextView mTvRawTDS;
    @Bind(R.id.tv_raw_quality)
    TextView mTvRawQuality;
    @Bind(R.id.tv_clean_TDS)
    TextView mTvCleanTDS;
    @Bind(R.id.tv_clean_quality)
    TextView mTvCleanQuality;
    @Bind(R.id.tv_real_temp)
    TextView mTvRealTemp;
    @Bind(R.id.iv_water_level)
    ImageView mIvWaterLevel;
    @Bind(R.id.tv_setting_temp)
    TextView mTvSettingTemp;

    @Bind(R.id.tv_water_state)
    TextView tvWaterState;

    @Bind(R.id.ctv_diagnose_test)
    CheckedTextView mCtvDiagnoseTest;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeLayout;

    private Device mDevice;
    private DeviceStatus mStatus;
    private static final int TIMER_DELAY = 0;
    private static final int TIMER_PETIOR = 3 * 1000;
    private List<Device> devices = new ArrayList<Device>();
    private String tag; //标记是否是常用设备
    private List<Device> lists;
    private DeviceRelate mDeviceRelate;
    @Override
    protected int initLayout() {
        return R.layout.activity_device_water_clearn;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            if (mDeviceRelate != null) {
                mDevice = mDeviceRelate.getDeviceProp();
//            mValue = mDeviceRelate.getDeviceStatus().getValue();
                tag = mDevice.getFavorite();
                if (mDevice != null && !TextUtils.isEmpty(mDevice.getFavorite())
                        && mDevice.getFavorite().equals("1")) {
                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_sel_white);
                } else
                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
                mStatus = mDeviceRelate.getDeviceStatus();
                updateUI(mStatus);
            }
        }
        tlTitleLayout.setTitle(mDevice.getName(), R.color.white);
        tlTitleLayout.setTitleBackgroundColor(getResources().getColor(R.color.green_water));
        tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back_white);
    }

    @Override
    protected void initData() {

        lists = new ArrayList<>();

        lists.add(mDevice);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                queryDeviceStatus(lists);
                mHandler.sendEmptyMessageDelayed(0, TIMER_PETIOR);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler != null)
            mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void rightViewClick() {
        if (tag.equals("1")) {
            tag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor_white);
        } else if (tag.equals("0")) {
            tag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_sel_white);
        }
        //保存状态，并发送后台
        if (mDevice != null) {
            devices.clear();
            mDevice.setFavorite(tag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

    @OnClick({R.id.ll_TDS, R.id.ll_water_amount, R.id.ll_filter_cartridge,
            R.id.ll_diagnose_test, R.id.ctv_diagnose_test})
    public void onClick(View view) {
        int postion = 0;
        switch (view.getId()) {
            case R.id.ll_TDS:
                postion = 0;
                break;
            case R.id.ll_water_amount:
                postion = 1;
                break;
            case R.id.ll_filter_cartridge:
                postion = 2;
                break;
            case R.id.ctv_diagnose_test:
            case R.id.ll_diagnose_test:
                postion = 3;
                break;
        }

        Intent intent = new Intent(getApplication(), WaterCleanDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("device", mDeviceRelate);
        bundle.putInt("position", postion);
        intent.putExtras(bundle);
        startActivity(intent);

    }


    private void updateUI(DeviceStatus status) {

        if (status == null) {
            return;
        }
        DeviceStatusValue value = status.getValue();
        if (value == null) {
            return;
        }
        mTvRawTDS.setText(value.getRawTDS() + "");
        mTvCleanTDS.setText(value.getPureTDS() + "");

        mTvRealTemp.setText(value.getActualTemp() + "");
        mTvSettingTemp.setText(value.getSetTemp() + "");

        mIvWaterLevel.setImageResource(waterLevelResId(value.getWaterLevel())); //水位
//        mTvWaterCleanState.setText(waterCleanState(mValue.getState()) + "");


        //TODO 水质
//        mTvRawQuality.setText();
//        mTvCleanQuality.setText();


//        mCtvDiagnoseTest.setChecked(waterCleanState());
        mCtvDiagnoseTest.setBackgroundResource(waterCleanState()?R.mipmap.ic_water_clean_error:R.mipmap.ic_water_diagnose_test);

        tvWaterState.setText(waterCleanState(value.getState()) + "");
    }

    /**
     * 判断水位
     *
     * @param waterLevel
     * @return
     */
    private int waterLevelResId(int waterLevel) {
        switch (waterLevel) {
            case 0x00:
                return R.mipmap.ic_water_level_0;    //缺水状态
            case 0x01:                           //	1格水位
                return R.mipmap.ic_water_level_1;
            case 0x02:
                return R.mipmap.ic_water_level_2;//2格水位
            case 0x03:
                return R.mipmap.ic_water_level_3;//3格水位
            case 0x04:
                return R.mipmap.ic_water_level_4;//4格水位
            default:
                return R.mipmap.ic_water_level_5;
        }

    }


    private boolean waterCleanState() {
        if (mStatus == null || mStatus.getValue() == null) {
            return false;
        }
        return mStatus.getValue().getRawCisternLevel() == 1
                || mStatus.getValue().getRawCisternPos() == 1
                || mStatus.getValue().getDewatering() == 1
                || mStatus.getValue().getMachineState() == 1;
    }


    /**
     * 净水器的状态
     *
     * @param stateStr
     */
    private String waterCleanState(String stateStr) {
        String state=Integer.toHexString(Integer.valueOf(stateStr));
        state=state.toUpperCase();
        StringBuilder sb=new StringBuilder("0x");
        if(state.length()==1){
            sb.append("0").append(state);
        }
        else{
            sb.append(state);
        }
        state=sb.toString();
        if(state==null)state="";
        switch (state) {
            case "0x00":
                return "加热进行中";
            case "0x01":
                return "保温进行中";
            case "0x02":
                return "节能进行中";
            case "0x03":
                return "取温水进行中";
            case "0x04":
                return "取热水进行中";
            case "0x05":
                return "低温故障";
            case "0x06":
                return "高温故障";
            case "0x07":
                return "净水箱防溢中";
            case "0x08":
                return "进水异常中";
            case "0x09":
                return "取45度热水进行中";
            case "0x0A":
                return "取55度热水进行中";
            case "0x0B":
                return "取65度热水进行中";
            case "0x0C":
                return "取75度热水进行中";
            case "0x0D":
                return "取85度热水进行中";
            case "0x0E":
                return "取95度热水进行中";
            case "0x0F":
                return "原水箱缺水中";
            case "0x10":
                return "原水箱被移走";
            case "0x11":
                return "进水温度低温异常";
            case "0x12":
                return "进水温度高温异常";
            case "0x13":
                return "出水温度低温异常";
            case "0x14":
                return "出水温度高温异常";
            case "0x15":
                return "市电压低压异常";
            case "0x16":
                return "市电压高压异常";
            case "0x17":
                return "增压泵空吸值未标定异常";
            case "0x18":
                return "出水口检测不到有水异常";
            case "0x19":
                return "整机待机中";
            default:
                return "";
        }
    }

    /**
     * 查询设备状态
     *
     * @param list
     */
    private void queryDeviceStatus(List<Device> list) {
        DeviceController.getInstance().queryDevicesStatus(this, list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    mSwipeLayout.setRefreshing(false);
                    DeviceStatusResult relateResult = GsonUtil.getObject(json, DeviceStatusResult.class);
                    if (relateResult.getRet() != 0) {
                        return;
                    }
                    mStatus = relateResult.getResponse().getDevices().get(0);
                    /*离线状态*/
                    if (mStatus.getOffline() == 1) {

                        return;
                    }

                    updateUI(mStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                    mSwipeLayout.setRefreshing(false);
                }


            }

            @Override
            public void onFailed(String json) {
                if (mSwipeLayout != null) {
                    mSwipeLayout.setRefreshing(false);
                }
            }
        });
    }

    private void queryRawData(List<Addr> addrs, List<Time> times) {

        GreenLiveController.getInstance().queryWater(this, addrs, times, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {

                    WaterTDSResult result = GsonUtil.getObject(json, WaterTDSResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    List<WaterTDSResult.WaterBean> list = result.getResponse();


                } catch (Exception e) {
                    e.printStackTrace();
                    Loger.d("我的天 " + e.toString());
                }


            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });
    }


}
