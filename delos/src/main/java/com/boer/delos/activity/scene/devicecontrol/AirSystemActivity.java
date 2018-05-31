package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.recycleview.AirSystemStatusAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.AirClean;
import com.boer.delos.model.AirSystemControlData;
import com.boer.delos.model.AirSystemData;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.ToastHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaolong on 2018/1/25.
 */

public class AirSystemActivity extends CommonBaseActivity {

    DeviceController deviceController;
    @Bind(R.id.tv_CO2)
    TextView tvCO2;
    @Bind(R.id.tv_pm25)
    TextView tvPm25;
    @Bind(R.id.tv_voc)
    TextView tvVoc;
    @Bind(R.id.tv_jiaquan)
    TextView tvJiaquan;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.tv_humidity)
    TextView tvHumidity;
    @Bind(R.id.ryc_control)
    RecyclerView rycControl;
    @Bind(R.id.tv_co2_grade)
    TextView tvCo2Grade;
    @Bind(R.id.tv_pm25_grade)
    TextView tvPm25Grade;
    @Bind(R.id.tv_voc_grade)
    TextView tvVocGrade;
    @Bind(R.id.tv_jiaquan_grade)
    TextView tvJiaquanGrade;
    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    public AirSystemData.ValueBean valueBean;
    private AirSystemStatusAdapter airSystemStatusAdapter;
    private List<AirClean> list;
    private String tag; //标记是否是常用设备
    private MyHandler myHandler;


    @Override
    protected int initLayout() {
        return R.layout.activity_air_system;
    }


    @Override
    protected void initView() {
        tlTitleLayout.setTitle(R.string.air_sys_title);
    }

    @Override
    protected void initData() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            if (mDeviceRelate != null && mDeviceRelate.getDeviceProp() != null) {
                mDevice = mDeviceRelate.getDeviceProp();
                if (!TextUtils.isEmpty(mDeviceRelate.getDeviceProp().getFavorite())) {
                    tag = mDeviceRelate.getDeviceProp().getFavorite();
                    if (tag.equals("1")) {
                        tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
                    } else {
                        tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
                    }
                }
            }
        }


        airSystemStatusAdapter = new AirSystemStatusAdapter(this);
        GridLayoutManager layoutManage = new GridLayoutManager(this, 3);

        rycControl.setLayoutManager(layoutManage);
        rycControl.setAdapter(airSystemStatusAdapter);
        airSystemStatusAdapter.setData(buildAirSystemDisplayData());
//        rycControl.addItemDecoration(new GridSpaceItemDecoration(this));
        deviceController = new DeviceController();
        myHandler = new MyHandler();
    }


    public List<AirClean> buildAirSystemDisplayData() {

        list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            AirClean airClean = new AirClean();
            airClean.setCheck(false);
            switch (i) {
                case 0:
                    airClean.setName(getString(R.string.turnon));
                    airClean.setResSelector(R.drawable.air_sys_turnon);
                    airClean.setRes(R.drawable.air_sys_turnon_white);
                    break;
                case 1:
                    airClean.setName(getString(R.string.auto));
                    airClean.setResSelector(R.drawable.air_sys_auto);
                    airClean.setRes(R.drawable.air_sys_auto_white);
                    break;
                case 2:
                    airClean.setName(getString(R.string.speed1));
                    airClean.setResSelector(R.drawable.air_sys_speed1);
                    airClean.setRes(R.drawable.air_sys_speed1_white);
                    break;
                case 3:
                    airClean.setName(getString(R.string.speed2));
                    airClean.setResSelector(R.drawable.air_sys_speed2);
                    airClean.setRes(R.drawable.air_sys_speed2_white);
                    break;
                case 4:
                    airClean.setName(getString(R.string.speed3));
                    airClean.setResSelector(R.drawable.air_sys_speed3);
                    airClean.setRes(R.drawable.air_sys_speed3_white);
                    break;
                case 5:
                    airClean.setName(getString(R.string.speed4));
                    airClean.setResSelector(R.drawable.air_sys_speed4);
                    airClean.setRes(R.drawable.air_sys_speed4_white);
                    break;
                case 6:
                    airClean.setName(getString(R.string.dust));
                    airClean.setResSelector(R.drawable.air_sys_dust);
                    airClean.setRes(R.drawable.air_sys_dust_white);
                    break;
                default:
                    break;

            }

            list.add(airClean);

        }

        return list;


    }

    private void getAirData() {


        deviceController.queryDevicesStatus(AirSystemActivity.this, mDevice, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {

                String response = JsonUtil.parseString(json, "response");
                String devices = JsonUtil.parseString(response, "devices");
                Gson gson = new Gson();
                List<AirSystemData> list = gson.fromJson(devices, new TypeToken<List<AirSystemData>>() {
                }.getType());
                if (list != null && list.size() > 0) {
                    AirSystemData airSystemData = new AirSystemData();
                    airSystemData.setAddr(list.get(0).getAddr());
                    airSystemData.setName(list.get(0).getName());
                    airSystemData.setTime(list.get(0).getTime());
                    airSystemData.setOffline(list.get(0).getOffline());
                    airSystemData.setType(list.get(0).getType());
                    valueBean = list.get(0).getValue();
                    airSystemData.setValue(valueBean);

                    if (valueBean != null) {
                        if (!TextUtils.isEmpty(valueBean.getHumidity()))
                            tvHumidity.setText(valueBean.getHumidity());
                        if (!TextUtils.isEmpty(valueBean.getTemperature()))
                            tvTemp.setText(valueBean.getTemperature());
                        tvPm25.setText(valueBean.getDust() + "");
                        tvVoc.setText(valueBean.getVoc() + "");
                        tvCO2.setText(valueBean.getCo2() + "");


                        initControlLayout();


                        getAirLevel(valueBean.getDust() + "", valueBean.getCo2() + "", valueBean.getVoc() + "");
                    }
                }


            }

            @Override
            public void onFailed(String json) {
            }
        });
    }


    //"state" -- 开关状态
    //"runState" -- 运行模式 0停止1运行
    //"mode" -- 运行模式 0自动1手动
    //"fanRelay" -- 风扇继电器 0关1开
    //"dedusting" -- 除尘开关 0关1开
    //"setSpeed" -- 转速 1-低速，2-中速1,3-中速2,4-高速
    private void initControlLayout() {

        for (int i = 0; i < 7; i++) {
            list.get(i).setCheck(false);
        }


        if (valueBean.getRunState() == 1) {
            list.get(0).setCheck(true);

            if (valueBean.getMode() == 0)
                list.get(1).setCheck(true);
            else
                list.get(1).setCheck(false);

            if (valueBean.getSetSpeed() == 1) {
                list.get(2).setCheck(true);
            } else if (valueBean.getSetSpeed() == 2) {
                list.get(3).setCheck(true);
            } else if (valueBean.getSetSpeed() == 3) {
                list.get(4).setCheck(true);
            } else if (valueBean.getSetSpeed() == 4) {
                list.get(5).setCheck(true);
            }


            if (valueBean.getDedusting() == 1) {
                list.get(6).setCheck(true);
            } else {
                list.get(6).setCheck(false);
            }


        } else {
            list.get(0).setCheck(false);
        }

        airSystemStatusAdapter.notifyDataSetChanged();

    }


    private void sendController(AirSystemControlData.ValueBean bean) {

        List<AirSystemControlData> controlDevices = new ArrayList<>();

        AirSystemControlData controlDevice = new AirSystemControlData();

        if (mDevice != null) {
            if (!TextUtils.isEmpty(mDevice.getAddr()))
                controlDevice.setAddr(mDevice.getAddr());
            if (!TextUtils.isEmpty(mDevice.getAreaname()))
                controlDevice.setAreaName(mDevice.getAreaname());
            if (!TextUtils.isEmpty(mDevice.getName()))
                controlDevice.setDeviceName(mDevice.getName());
            if (!TextUtils.isEmpty(mDevice.getType()))
                controlDevice.setType(mDevice.getType());
        }

        controlDevice.setValue(bean);
        controlDevices.add(controlDevice);
        deviceLightControl(controlDevices);

    }


    public void deviceLightControl(List<AirSystemControlData> controlDevices) {
        DeviceController.getInstance().deviceAirSystemControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() == 0) {
                        //表明控制成功
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }


    @Override
    protected void initAction() {

        airSystemStatusAdapter.setOnItemClickListener(new AirSystemStatusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                initMod(position);
                airControlValue(position);

            }
        });

    }


    private void airControlValue(int pos) {


//        1-开关{0-关,1-开}
//        2-手动自动{0-自动,1-手动}
//        3-除尘开关{0-关,1-开}
//        4-设置目标速度{1-低速,2-中速1,3-中速2,4-高速}
        String cmd = "";
        String data = "";


        switch (pos) {

            case 0:

                if (list.get(pos).isCheck()) {

                    cmd = "1";
                    data = "1";

                } else {
                    cmd = "1";
                    data = "0";

                }


                break;


            case 1:


                if (list.get(0).isCheck()) {
                    if (list.get(pos).isCheck()) {

                        cmd = "2";
                        data = "0";

                    } else {

                        cmd = "2";
                        data = "1";

                    }
                }


                break;
            case 2:

                if (list.get(0).isCheck() && !list.get(1).isCheck() && list.get(pos).isCheck()) {

                    cmd = "4";
                    data = "1";

                }


                break;
            case 3:

                if (list.get(0).isCheck() && !list.get(1).isCheck() && list.get(pos).isCheck()) {

                    cmd = "4";
                    data = "2";


                }


                break;

            case 4:

                if (list.get(0).isCheck() && !list.get(1).isCheck() && list.get(pos).isCheck()) {

                    cmd = "4";
                    data = "3";

                }


                break;
            case 5:

                if (list.get(0).isCheck() && !list.get(1).isCheck() && list.get(pos).isCheck()) {

                    cmd = "4";
                    data = "4";

                }


                break;
            case 6:

                if (list.get(0).isCheck()) {
                    if (list.get(pos).isCheck()) {

                        cmd = "3";
                        data = "1";

                    } else {

                        cmd = "3";
                        data = "0";

                    }
                }


                break;
        }


        if (!cmd.equals("") && !data.equals("")) {
            AirSystemControlData.ValueBean bean = new AirSystemControlData.ValueBean();


            bean.setCmd(cmd);
            bean.setData(data);


            sendController(bean);
        }

    }


    private void initMod(int position) {

        boolean toggle = list.get(0).isCheck();
        boolean isCheck = list.get(position).isCheck();


        if (position != 0 && position != 6 && position != 1) {
            if (!isCheck && !list.get(1).isCheck())
                isCheck = !isCheck;
        } else {
            isCheck = !isCheck;
        }


        if (position == 0) {

            list.get(position).setCheck(isCheck);
        } else {

            list.get(position).setCheck(isCheck && toggle);
        }

        if (!list.get(0).isCheck()) {
            list.get(1).setCheck(false);
            list.get(2).setCheck(false);
            list.get(3).setCheck(false);
            list.get(4).setCheck(false);
            list.get(5).setCheck(false);
            list.get(6).setCheck(false);
            if (position != 0)
                ToastHelper.showShortMsg(getString(R.string.tip_take_on));
        } else {
            if (list.get(1).isCheck() && position == 1) {
                list.get(2).setCheck(false);
                list.get(3).setCheck(false);
                list.get(4).setCheck(false);
                list.get(5).setCheck(false);
                list.get(6).setCheck(false);
            }
            if (list.get(2).isCheck() && position == 2) {
                list.get(1).setCheck(false);
                list.get(3).setCheck(false);
                list.get(4).setCheck(false);
                list.get(5).setCheck(false);
            }
            if (list.get(3).isCheck() && position == 3) {
                list.get(1).setCheck(false);
                list.get(2).setCheck(false);
                list.get(4).setCheck(false);
                list.get(5).setCheck(false);
            }

            if (list.get(4).isCheck() && position == 4) {
                list.get(1).setCheck(false);
                list.get(2).setCheck(false);
                list.get(3).setCheck(false);
                list.get(5).setCheck(false);
            }
            if (list.get(5).isCheck() && position == 5) {
                list.get(1).setCheck(false);
                list.get(2).setCheck(false);
                list.get(3).setCheck(false);
                list.get(4).setCheck(false);
            }
            if (list.get(6).isCheck() && position == 6) {
                list.get(1).setCheck(false);
            }
        }
        airSystemStatusAdapter.setData(list);
    }


    @Override
    public void rightViewClick() {
        List<Device> devices = new ArrayList<>();
        if (tag.equals("1")) {
            tag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
        } else if (tag.equals("0")) {
            tag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
        }
        //保存状态，并发送后台
        if (mDevice != null) {
            devices.clear();
            mDevice.setFavorite(tag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }


    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (mDevice != null) {

                getAirData();

            }
            myHandler.sendEmptyMessageDelayed(0, 6000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myHandler.sendEmptyMessage(0);

    }

    private void getAirLevel(String pm25, String co2, String voc) {
        Log.v("gl", "co2S==" + co2 + "vocS==" + voc + "pm25S==" + pm25);

        String pm25S = "", co2S = "", vocS = "";

        double pm25D = Double.parseDouble(pm25);
        double co2D = Double.parseDouble(co2);
        double vocD = Double.parseDouble(voc);

        double[] pm25Range = {0, 35, 75, 115, 150, 200, 500};
        double[] co2Range = {0, 450, 550, 1000, 2000, 5000};
        double[] vocRange = {0, 0.044, 0.045, 0.075, 0.081, 3.744};


        String[] pm25Grade = getResources().getStringArray(R.array.aqi_des);
        String[] co2Grade = getResources().getStringArray(R.array.co2_grade);
        String[] vocGrade = getResources().getStringArray(R.array.voc_grade);


        for (int i = 0; i < pm25Range.length; i++) {

            if (pm25D < pm25Range[0] || pm25D > pm25Range[pm25Range.length - 1]) {


            } else {

                if (pm25D > pm25Range[i]) {

                    continue;

                } else {

                    if (i > 0) {
                        pm25S = pm25Grade[i - 1];
                        break;
                    } else {
                        pm25S = pm25Grade[0];
                        break;
                    }

                }


            }

        }


        for (int i = 0; i < vocRange.length; i++) {

            if (vocD < vocRange[0] || vocD > vocRange[vocRange.length - 1]) {


            } else {

                if (vocD > vocRange[i]) {

                    continue;

                } else {


                    if (i > 0) {
                        vocS = vocGrade[i - 1];
                        break;
                    } else {
                        vocS = vocGrade[0];
                        break;
                    }


                }


            }

        }


        for (int i = 0; i < co2Range.length; i++) {

            if (co2D < co2Range[0] || co2D > co2Range[co2Range.length - 1]) {


            } else {

                if (co2D > co2Range[i]) {

                    continue;

                } else {

                    if (i > 0) {
                        co2S = co2Grade[i - 1];
                        break;
                    } else {
                        co2S = co2Grade[0];
                        break;
                    }

                }


            }

        }


        tvCo2Grade.setText(co2S);
        tvPm25Grade.setText(pm25S);
        tvVocGrade.setText(vocS);
        Log.v("gl", "co2S==" + co2S + "vocS==" + vocS + "pm25S==" + pm25S);


    }

    @Override
    protected void onPause() {
        super.onPause();
        myHandler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
