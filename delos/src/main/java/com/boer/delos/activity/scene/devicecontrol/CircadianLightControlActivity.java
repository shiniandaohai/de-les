package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.boer.delos.R;
import com.boer.delos.adapter.SkinControlAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.AirClean;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.widget.LightControlView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CircadianLightControlActivity extends CommonBaseActivity {

    @Bind(R.id.lightControlView)
    LightControlView mLightControlView;
    @Bind(R.id.gv_skin_area)
    GridView gvSkinArea;

    private Device mDevice;
    private DeviceRelate mDeviceRelate;
    private List<Device> devices = new ArrayList<>();
    private String tag; //标记是否是常用设备
    //
    SkinControlAdapter airControlAdapter;
    List<AirClean> list;
    private int checkedPos = -1;
    DeviceController deviceController;

    private MyHandler myHandler;
    @Override
    protected int initLayout() {
        return R.layout.activity_device_control_circadian_light;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceRelate = (DeviceRelate) bundle.getSerializable("device");
            if (mDeviceRelate != null && mDeviceRelate.getDeviceProp() != null) {
                mDevice = mDeviceRelate.getDeviceProp();
                tlTitleLayout.setTitle(mDevice.getName());
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
    }

    @Override
    protected void initData() {
        deviceController = new DeviceController();
        list = new ArrayList<>();
        airControlAdapter = new SkinControlAdapter(this, list);
        gvSkinArea.setAdapter(airControlAdapter);
        for (int i = 0; i < 6; i++) {

            AirClean airClean = new AirClean();

            switch (i) {

                case 0:
                    airClean.setRes(R.mipmap.sin_sun);
                    airClean.setResSelector(R.mipmap.sin_sun_selector);
                    airClean.setName(getString(R.string.sin_sun));
                    break;
                case 1:
                    airClean.setRes(R.mipmap.sin_muscle);
                    airClean.setResSelector(R.mipmap.sin_muscle_selector);
                    airClean.setName(getString(R.string.sin_muscle));
                    break;
                case 2:
                    airClean.setRes(R.mipmap.sin_relax);
                    airClean.setResSelector(R.mipmap.sin_relax_selector);
                    airClean.setName(getString(R.string.sin_relax));
                    break;
                case 3:
                    airClean.setRes(R.mipmap.sin_happy);
                    airClean.setResSelector(R.mipmap.sin_happy_selector);
                    airClean.setName(getString(R.string.sin_happy));
                    break;
                case 4:
                    airClean.setRes(-1);
                    airClean.setResSelector(-1);
                    airClean.setName("");
                    break;
                case 5:
                    airClean.setRes(R.mipmap.sin_close);
                    airClean.setResSelector(R.mipmap.sin_close_selector);
                    airClean.setName(getString(R.string.sin_close));
                    break;
            }

            list.add(airClean);
        }
        airControlAdapter.notifyDataSetChanged();

        myHandler = new MyHandler();
        upDateUI();
    }

    @Override
    protected void initAction() {
        gvSkinArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (AirClean airClean : list) {

                    airClean.setCheck(false);

                }

                boolean isCheck = list.get(position).isCheck();
                isCheck = !isCheck;

                list.get(position).setCheck(isCheck);
                airControlAdapter.notifyDataSetChanged();

                checkedPos = position;


                if (position == 5)
                    mLightControlView.closeLight(0);
                else
                    mLightControlView.startLight(0);


                sendController(list.get(position).getName());
            }
        });


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


    private void sendController(String name) {
        List<ControlDevice> controlDevices = new ArrayList<>();

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(mDevice.getRoomname());
        controlDevice.setAddr(mDevice.getAddr());
        controlDevice.setAreaName(mDevice.getAreaname());
        controlDevice.setDeviceName(mDevice.getName());
        controlDevice.setType(mDevice.getType());

        ControlDeviceValue value = new ControlDeviceValue();
        value.setButtonName(name);
        value.setKeypadName(mDevice.getX2Name());
        value.setN4SerialNo(mDevice.getN4SerialNo());
        controlDevice.setValue(value);
        controlDevices.add(controlDevice);
        deviceLightControl(controlDevices);

    }

    public void deviceLightControl(List<ControlDevice> controlDevices) {
        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                Log.i("gwq", "json=" + Json);
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() == 0) {
                        //表明控制成功
                    } else {
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


    private void upDateUI() {

        if (mDevice != null && mDevice.getButtons() != null && mDevice.getButtons().size() > 0)
            for (int i = 0; i < mDevice.getButtons().size(); i++) {

                Device.ButtonsBean buttonsBean = mDevice.getButtons().get(i);

                Log.v("gl", "status===" + buttonsBean.isActive() + "====name===" + buttonsBean.getName());

                if (i == 4)
                    list.get(5).setCheck(buttonsBean.isActive());
                else
                    list.get(i).setCheck(buttonsBean.isActive());
            }

        airControlAdapter.notifyDataSetInvalidated();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void queryAllDevice() {
        DeviceController.getInstance().queryDeviceRelateInfo(this,"CircadianLight", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                json = StringUtil.deviceStateStringReplaceMap(json);
                DeviceRelateResult result = GsonUtil.getObject(json, DeviceRelateResult.class);
                if (result.getRet() != 0) {
                    return;
                }
                List<DeviceRelate> deviceRelates = result.getResponse();
                if(deviceRelates==null){
                    return;
                }
                for(DeviceRelate deviceRelate:deviceRelates){
                    if(deviceRelate.getDeviceProp().getAddr().equals(mDevice.getAddr())){
                        mDevice=deviceRelate.getDeviceProp();
                        upDateUI();
                        break;
                    }
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            queryAllDevice();
            myHandler.sendEmptyMessageDelayed(0, 3*1000);
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
}
