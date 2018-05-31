package com.boer.delos.activity.scene.devicecontrol.airclean;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.LaserEggActivity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.AirCleanData;
import com.boer.delos.model.AirCleanDevice;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaolong on 2017/4/14.
 */
public class AirCleanActivity extends CommonBaseActivity implements AirControlFragment.OnControlFragmentClickItem, AirDataFragment.OnDataFragmentClickItem {
    @Bind(R.id.vp_air_operation)
    ViewPager vpAirOperation;
    @Bind(R.id.tv_temperature)
    TextView tvTemperature;
    @Bind(R.id.tv_humidity)
    TextView tvHumidity;
    @Bind(R.id.tv_pm25)
    TextView tvPm25;
    @Bind(R.id.tv_tvoc)
    TextView tvTvoc;
    @Bind(R.id.tv_air_health)
    TextView tvAirHealth;
    @Bind(R.id.tv_wind_speed)
    public TextView tvWindSpeed;
    @Bind(R.id.llayout_data)
    LinearLayout llayoutData;
    @Bind(R.id.tv_status)
    TextView tvStatus;

    private ViewPagerAdapter viewPagerAdapter;
    List<Fragment> fragmentList;
    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    AirControlFragment fragmentControl;
    AirDataFragment fragmentData;
    private AirCleanDevice airCleanDevice;
    DeviceController deviceController;
    public AirCleanData.ValueBean valueBean;

    //    极差：#f24949
//    差：#f26549
//    中：#f29d49
//    良：#48b38a
//    优：#49b0f2
//    超优：#1796e5
    static int colorworst;
    static int colorworse;
    static int colorbad;
    static int colorgood;
    static int colorbetter;
    static int colorbest;
    //页面皮肤
    static int skinColor;

    private List<Device> devices = new ArrayList<>();
    private String tag = "0"; //标记是否是常用设备


    private MyHandler myHandler;
    private String[] airModeStr;

    @Override
    protected int initLayout() {
        return R.layout.activity_air_clean;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(R.string.air_clean_machine, R.color.white);
        tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back_white);
        colorworst = Color.parseColor("#f24949");
        colorworse = Color.parseColor("#f26549");
        colorbad = Color.parseColor("#f29d49");
        colorgood = Color.parseColor("#48b38a");
        colorbetter = Color.parseColor("#49b0f2");
        colorbest = Color.parseColor("#1796e5");

    }

    @Override
    protected void initData() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            if (mDeviceRelate != null && mDeviceRelate.getDeviceProp() != null) {
                mDevice = mDeviceRelate.getDeviceProp();
                if (mDevice != null && !TextUtils.isEmpty(mDevice.getFavorite())
                        && mDevice.getFavorite().equals("1")) {

                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_sel_white);
                } else {
                    tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
                }
            }
        }

        Log.v("gl", "mDevice==" + mDevice);

        airCleanDevice = new AirCleanDevice();
        fragmentList = new ArrayList<>();

        fragmentControl = new AirControlFragment();
        fragmentData = new AirDataFragment();
        fragmentList.add(fragmentControl);
        fragmentList.add(fragmentData);
        fragmentControl.setControlFragmentListener(this);
        fragmentData.setDataFragmentListener(this);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpAirOperation.setAdapter(viewPagerAdapter);

        deviceController = new DeviceController();


        myHandler = new MyHandler();


        initSkin();

        airModeStr = getResources().getStringArray(R.array.air_mode);

    }

    @Override
    public void rightViewClick() {
        if (tag.equals("1")) {
            tag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
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

    private void getAirData() {

        List<Device> list = new ArrayList<>();
        list.add(mDevice);

//        toastUtils.showProgress("");
        deviceController.queryDevicesStatus(AirCleanActivity.this, list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
//                toastUtils.dismiss();

                String response = JsonUtil.parseString(json, "response");
                String devices = JsonUtil.parseString(response, "devices");
                Gson gson = new Gson();
                List<AirCleanData> list = gson.fromJson(devices, new TypeToken<List<AirCleanData>>() {
                }.getType());
                if (list != null && list.size() > 0) {
                    AirCleanData airCleanData = new AirCleanData();
                    airCleanData.setAddr(list.get(0).getAddr());
                    airCleanData.setName(list.get(0).getName());
                    airCleanData.setTime(list.get(0).getTime());
                    airCleanData.setOffline(list.get(0).getOffline());
                    airCleanData.setType(list.get(0).getType());
                    valueBean = list.get(0).getValue();
                    airCleanData.setValue(valueBean);

                    if (valueBean != null) {
                        if (!TextUtils.isEmpty(valueBean.getHumidity()))
                            tvHumidity.setText(valueBean.getHumidity() + "%");
                        if (!TextUtils.isEmpty(valueBean.getTemp()))
                            tvTemperature.setText(valueBean.getTemp() + "℃");
                        tvPm25.setText(valueBean.getPm25() + "μg/m3");
                        tvTvoc.setText(valueBean.getTVOC() + "ppm");
                        tvWindSpeed.setText(valueBean.getRate() + "");
                    }

                    initSkin();

                    tvStatus.setText(airModeStr[getModeIndex(valueBean.getMode())]);

                    if (airCleanData.getValue() != null)
                        fragmentControl.getMod(airCleanData.getValue().getMode());

                    fragmentData.setValueBean(airCleanData);
                    fragmentData.setDeviceValueBean(mDevice);

                }
            }

            @Override
            public void onFailed(String json) {
//                if (toastUtils != null)
//                    toastUtils.dismiss();
            }
        });


    }

    private void initSkin() {

        switch (getAirLevel()) {


            case 0:
                tvAirHealth.setText(R.string.air_best);
                skinColor = colorbest;
                break;
            case 1:
                tvAirHealth.setText(R.string.air_better);
                skinColor = colorbetter;
                break;
            case 2:
                tvAirHealth.setText(R.string.air_good);
                skinColor = colorgood;
                break;
            case 3:
                tvAirHealth.setText(R.string.air_bad);
                skinColor = colorbad;
                break;
            case 4:
                tvAirHealth.setText(R.string.air_worse);
                skinColor = colorworse;
                break;
            case 5:
                tvAirHealth.setText(R.string.air_worst);
                skinColor = colorworst;
                break;

        }


        statusBarTheme(true, skinColor);
        llayoutStatus.setBackgroundColor(skinColor);
        tlTitleLayout.setTitleBackgroundColor(skinColor);
        llayoutData.setBackgroundColor(skinColor);


        fragmentControl.setSkin();
//        fragmentData.setSkin();
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void posCmd(AirCleanDevice.ValueBean bean) {

        sendController(bean);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }


    private void sendController(AirCleanDevice.ValueBean bean) {

        List<AirCleanDevice> controlDevices = new ArrayList<>();

        if (mDevice != null) {
            if (!TextUtils.isEmpty(mDevice.getAddr()))
                airCleanDevice.setAddr(mDevice.getAddr());
            if (!TextUtils.isEmpty(mDevice.getAreaname()))
                airCleanDevice.setAreaname(mDevice.getAreaname());
            if (!TextUtils.isEmpty(mDevice.getName()))
                airCleanDevice.setDeviceName(mDevice.getName());
            if (!TextUtils.isEmpty(mDevice.getType()))
                airCleanDevice.setType(mDevice.getType());
        }

        airCleanDevice.setValue(bean);
        controlDevices.add(airCleanDevice);
        deviceLightControl(controlDevices);

    }


    public void deviceLightControl(List<AirCleanDevice> controlDevices) {
        DeviceController.getInstance().deviceAirCleanControl(this, controlDevices, new RequestResultListener() {
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


    public int getAirLevel() {

        int pm2f5_level;
        int tvoc_level;
        int pm2f5_ug = 0;
        int tvoc_val = 0;


        if (valueBean != null) {
            pm2f5_ug = valueBean.getPm25();
            tvoc_val = valueBean.getTVOC();
        }

        if (pm2f5_ug > 250) {
            pm2f5_level = 5;
        } else if (pm2f5_ug > 150) {
            pm2f5_level = 4;
        } else if (pm2f5_ug > 115) {
            pm2f5_level = 3;
        } else if (pm2f5_ug > 75) {
            pm2f5_level = 2;
        } else if (pm2f5_ug > 35) {
            pm2f5_level = 1;
        } else {
            pm2f5_level = 0;
        }

        //((Value/64*2V)/3.3V)*1024=Value*9.696969697f
        if (tvoc_val > 485) {
            tvoc_level = 5;
        }        //>50
        else if (tvoc_val > 388) {
            tvoc_level = 4;
        }    //>40
        else if (tvoc_val > 291) {
            tvoc_level = 3;
        }    //>30
        else if (tvoc_val > 155) {
            tvoc_level = 2;
        }    //>16
        else if (tvoc_val > 87) {
            tvoc_level = 1;
        }    //>9
        else {
            tvoc_level = 0;
        }

        if (tvoc_level > pm2f5_level) {
            return tvoc_level;
        } else {
            return pm2f5_level;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        myHandler.removeCallbacksAndMessages(null);

    }



//    0 	0x00	离线模式（未使用）
//            1 	0x01	深度待机
//    2 	0x02	睡眠模式
//    3 	0x03	自动模式
//    4 	0x04	手动控制
//    5 	0x05	狂风模式
//    129	0x81	儿童场景
//    130	0x82	全天智能场景
//    255	0xFF	设备关机
    private int getModeIndex(int mode){
        switch (mode){
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 129:
                return 6;
            case 130:
                return 7;
            case 255:
                return 8;
            default:
                return 9;
        }
    }
}
