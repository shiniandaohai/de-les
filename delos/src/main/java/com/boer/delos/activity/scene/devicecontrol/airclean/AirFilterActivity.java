package com.boer.delos.activity.scene.devicecontrol.airclean;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.AirCleanData;
import com.boer.delos.model.AirCleanDevice;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;
import com.boer.delos.view.popupWindow.ShowRequestPopupWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/4/15.
 */
public class AirFilterActivity extends CommonBaseActivity {
    @Bind(R.id.tv_day)
    TextView tvDay;
    @Bind(R.id.btn_reset)
    Button btnReset;
    ShowRequestPopupWindow popupWindow;
    public AirCleanData valueBean;
    public Device mDevice;

    @Override
    protected int initLayout() {
        return R.layout.activity_air_filter;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.air_txt_data_hepa, R.color.black);

    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        valueBean = (AirCleanData) intent.getSerializableExtra("valueBean");
        mDevice = (Device) intent.getSerializableExtra("deviceValueBean");


        popupWindow = new ShowRequestPopupWindow(this, tlTitleLayout, getString(R.string.request_title), getString(R.string.hepa_content));
        popupWindow.setShowRequestPopupWindow(new ShowRequestPopupWindow.IShowRequest() {
            @Override
            public void rightButtonClick() {
                sendController();
            }
        });


        if (valueBean != null && valueBean.getValue() != null)
            tvDay.setText(computeRevertHour(valueBean.getValue().getHEPA()));


    }

    @Override
    protected void initAction() {


    }

    @OnClick(R.id.btn_reset)
    public void onClick() {
        popupWindow.showPopupWindow();
    }

    public String computeRevertHour(int h) {
//
        int day = h / 24;
        int hour = h % 24;


        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(getString(R.string.hepa_day) + day + getString(R.string.pick_day));
        }


        return sb.toString();
    }


    private void sendController() {

        List<AirCleanDevice> controlDevices = new ArrayList<>();
        AirCleanDevice airCleanDevice = new AirCleanDevice();

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


        AirCleanDevice.ValueBean value = new AirCleanDevice.ValueBean();
        value.setCmd("3");
        value.setData("0");
        value.setDataLen("1");


        airCleanDevice.setValue(value);
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
//                        finish();
                    } else {


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                L.i("LightingControlChildAdapter sendController() onSuccess()" + Json);
            }

            @Override
            public void onFailed(String json) {
                L.d("LightingControlChildAdapter sendController() onFailed():" + json);
            }
        });
    }


}
