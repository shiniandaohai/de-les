package com.boer.delos.activity.scene;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.PanelSettingActivity;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.activity.remotectler.DeviceModeListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.RelateDevice;
import com.boer.delos.model.RelateDevices;
import com.boer.delos.model.Room;
import com.boer.delos.model.ScanDevice;
import com.boer.delos.model.ScanDeviceResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.ResultRetDeal;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.RelateDevicePopUpWindow;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 修改设备界面
 * create at 2016/6/6 17:22
 */
public class ModifyDeviceListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private android.widget.ImageView ivSelectedDevice;
    private android.widget.EditText etModifyDeviceName;
    private PercentLinearLayout llDeleteContent;
    private PercentLinearLayout llModifyScan;
    private android.widget.EditText etModifyAddress;
    private android.widget.EditText etModifyRemark;
    private PercentLinearLayout llRelate;
    private PercentLinearLayout llRelateDevice;
    private android.widget.TextView tvModifyConfirm;
    private InputMethodManager inputMethodManager;
    private View view;
    private Device device;
    private String lightType = "Light1Light2Light3Light4";
    private List<Device> deviceList = new ArrayList<>();
    private RelateDevicePopUpWindow popUpWindow;
    public static ModifyDeviceListeningActivity instance = null;
    private int dbId, timestamp;
    private List<List<Map<String, String>>> parentList;
    private List<RelateDevices> evlList;
    private RelateDevices light1, light2, light3, light4;
    private List<Map<String, String>> childList0, childList1, childList2, childList3;
    private boolean switchOn1 = false, switchOn2 = false, switchOn3 = false, switchOn4 = false;
    private LinearLayout llLightSwitch, llLight1, llLight2, llLight3, llLight4;
    private ToggleButton tbItemSwitch1, tbItemSwitch2, tbItemSwitch3, tbItemSwitch4;
    private Room room;
    private String scanDeviceId;
    private RelateDevice mRelateDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        view = LayoutInflater.from(this).inflate(R.layout.activity_modify_device, null);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(view);
        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("FurnitureListRoom");
        device = (Device) intent.getSerializableExtra("CurrentDevice");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        initTopBar("修改设备", null, true, false);
        tvModifyConfirm = (TextView) findViewById(R.id.tvModifyConfirm);
        llRelate = (PercentLinearLayout) findViewById(R.id.llRelate);
        llRelateDevice = (PercentLinearLayout) findViewById(R.id.llRelateDevice);
        etModifyRemark = (EditText) findViewById(R.id.etModifyRemark);
        etModifyAddress = (EditText) findViewById(R.id.etModifyAddress);
        llModifyScan = (PercentLinearLayout) findViewById(R.id.llModifyScan);
        llDeleteContent = (PercentLinearLayout) findViewById(R.id.llDeleteContent);
        etModifyDeviceName = (EditText) findViewById(R.id.etModifyDeviceName);
        ivSelectedDevice = (ImageView) findViewById(R.id.ivSelectedDevice);
        ivRight.setImageResource(R.drawable.ic_edit_white);

        llLightSwitch = (LinearLayout) findViewById(R.id.llLightSwitch);
        llLight1 = (LinearLayout) findViewById(R.id.llLight1);
        llLight2 = (LinearLayout) findViewById(R.id.llLight2);
        llLight3 = (LinearLayout) findViewById(R.id.llLight3);
        llLight4 = (LinearLayout) findViewById(R.id.llLight4);
        tbItemSwitch1 = (ToggleButton) findViewById(R.id.tbItemSwitch1);
        tbItemSwitch2 = (ToggleButton) findViewById(R.id.tbItemSwitch2);
        tbItemSwitch3 = (ToggleButton) findViewById(R.id.tbItemSwitch3);
        tbItemSwitch4 = (ToggleButton) findViewById(R.id.tbItemSwitch4);
    }

    private void initData() {
        String type = device.getType();
        if (type.contains("Light")) {
            //设定开关状态
            setSwitchOn(device);
        }
        if ("AirCondition".equals(type) || "TV".equals(type)) {
            ivRight.setVisibility(View.VISIBLE);
        } else if ("Light1".equals(type) || "LightAdjust".equals(type)) {
            llLightSwitch.setVisibility(View.VISIBLE);
            llLight1.setVisibility(View.VISIBLE);
            llRelate.setVisibility(View.VISIBLE);
        } else if ("Light2".equals(type)) {
            llLightSwitch.setVisibility(View.VISIBLE);
            llLight1.setVisibility(View.VISIBLE);
            llLight2.setVisibility(View.VISIBLE);
            llRelate.setVisibility(View.VISIBLE);
        } else if ("Light3".equals(type)) {
            llLightSwitch.setVisibility(View.VISIBLE);
            llLight1.setVisibility(View.VISIBLE);
            llLight2.setVisibility(View.VISIBLE);
            llLight3.setVisibility(View.VISIBLE);
            llRelate.setVisibility(View.VISIBLE);
        } else if ("Light4".equals(type)) {
            llLightSwitch.setVisibility(View.VISIBLE);
            llLight1.setVisibility(View.VISIBLE);
            llLight2.setVisibility(View.VISIBLE);
            llLight3.setVisibility(View.VISIBLE);
            llLight4.setVisibility(View.VISIBLE); // add by sunzhibin
            llRelate.setVisibility(View.VISIBLE);
        } else if ("HGC".equals(type)) {
            llLightSwitch.setVisibility(View.GONE);
            llLight1.setVisibility(View.GONE);
            llLight2.setVisibility(View.GONE);
            llLight3.setVisibility(View.GONE);
            llRelate.setVisibility(View.VISIBLE);
        } else if ("Pannel".equals(type)) {
            llLightSwitch.setVisibility(View.GONE);
            llLight1.setVisibility(View.GONE);
            llLight2.setVisibility(View.GONE);
            llLight3.setVisibility(View.GONE);
            llRelate.setVisibility(View.VISIBLE);

        } else {
            this.llRelate.setVisibility(View.GONE);
        }
        //开关事件
        this.tbItemSwitch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchOn1 = !switchOn1;
                tbItemSwitch1.setChecked(switchOn1);
                sendControl(1, switchOn1 ? "1" : "0");
            }
        });
        this.tbItemSwitch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchOn2 = !switchOn2;
                tbItemSwitch2.setChecked(switchOn2);
                sendControl(2, switchOn2 ? "1" : "0");
            }
        });
        this.tbItemSwitch3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchOn3 = !switchOn3;
                tbItemSwitch3.setChecked(switchOn3);
                sendControl(3, switchOn3 ? "1" : "0");
            }
        });
        this.tbItemSwitch4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchOn4 = !switchOn4;
                tbItemSwitch4.setChecked(switchOn4);
                sendControl(4, switchOn4 ? "1" : "0");
            }
        });
        ivSelectedDevice.setImageResource(Constant.getResIdWithType(type));
        etModifyDeviceName.setText(device.getName());
        etModifyAddress.setText(device.getAddr());
        etModifyRemark.setText(device.getNote());
    }

    /**
     * 设定开关状态
     *
     * @param device
     */
    private void setSwitchOn(Device device) {
        DeviceRelate relate = Constant.getDeviceRelate(device);
        if (relate == null) {
            return;
        }
        DeviceStatus status = relate.getDeviceStatus();
        if (status.getValue() != null && status.getValue().getState() != null) {
            switchOn1 = status.getValue().getState().equals("1");
        }
        if (status.getValue() != null && status.getValue().getState2() != null) {
            switchOn2 = status.getValue().getState2().equals("1");
        }
        if (status.getValue() != null && status.getValue().getState3() != null) {
            switchOn3 = status.getValue().getState3().equals("1");
        }
        if (status.getValue() != null && status.getValue().getState4() != null) {
            switchOn4 = status.getValue().getState4().equals("1");
        }
        tbItemSwitch1.setChecked(switchOn1);
        tbItemSwitch2.setChecked(switchOn2);
        tbItemSwitch3.setChecked(switchOn3);
        tbItemSwitch4.setChecked(switchOn4);
    }

    /**
     * 发送控制命令
     */
    private void sendControl(int position, String flag) {
        List<ControlDevice> controlDevices = new ArrayList<>();

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        if (position == 1) {
            value.setState(flag);
        } else if (position == 2) {
            value.setState2(flag);
        } else if (position == 3) {
            value.setState3(flag);
        } else {
            value.setState4(flag);
        }
        controlDevice.setValue(value);
        controlDevices.add(controlDevice);
        DeviceController.getInstance().deviceControl(this, controlDevices, false,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.d("ModifyDeviceListeningActivity sendControl() onSuccess() " + Json);
                    }

                    @Override
                    public void onFailed(String Json) {
                        L.d("ModifyDeviceListeningActivity sendControl() onFailed() " + Json);
                    }
                });
    }

    private void initListener() {
        tvModifyConfirm.setOnClickListener(this);
        llModifyScan.setOnClickListener(this);
        llRelate.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                view.requestFocus();
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                llDeleteContent.setVisibility(View.GONE);
                return false;
            }
        });
        this.etModifyDeviceName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (etModifyDeviceName.getText().length() > 0) {
                    llDeleteContent.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        this.etModifyDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    llDeleteContent.setVisibility(View.VISIBLE);
                } else {
                    llDeleteContent.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        llDeleteContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etModifyDeviceName.setText("");

            }
        });
    }

    /**
     * 开始扫描设备
     */
    private void startScanDevice() {
        toastUtils.showProgress("正在扫描主机,请稍候...");
        GatewayInfo info = null;
        for (GatewayInfo gatewayInfo : Constant.gatewayInfos) {
            if (gatewayInfo.getHostId().equals(Constant.CURRENTHOSTID)) {
                info = gatewayInfo;
                break;
            }
        }
        //不在一个局域网内
        if (info == null) {
            toastUtils.showInfoWithStatus(getString(R.string.scan_hint));
            llModifyScan.setBackgroundResource(R.drawable.shape_login_btn_bg);
            llModifyScan.setClickable(true);
        } else {
            //去当前主机查询需添加设备信息
            scanDevice(info.getIp());
        }
    }

    /**
     * 去主机查询指定设备
     *
     * @param ip
     */
    private void scanDevice(String ip) {
        if (null == ip) {
            return;
        }
        DeviceController.getInstance().scanDevice(this, ip, device.getType(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                final ScanDeviceResult result = new Gson().fromJson(Json, ScanDeviceResult.class);
                if (result.getRet() != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            llModifyScan.setBackgroundColor(getResources().getColor(R.color.blue_login_btn));
                            llModifyScan.setClickable(true);
                            toastUtils.showInfoWithStatus(result.getMsg());
                        }
                    });
                    return;
                }
                List<ScanDevice> list = result.getResponse();
                if (null != list && list.size() > 0) {
                    scanDeviceId = list.get(0).getId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastUtils.dismiss();
                            llModifyScan.setBackgroundColor(getResources().getColor(R.color.blue_login_btn));
                            llModifyScan.setClickable(true);
                            etModifyAddress.setText(scanDeviceId);
                        }
                    });
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            llModifyScan.setBackgroundColor(getResources().getColor(R.color.blue_login_btn));
                            llModifyScan.setClickable(true);
                            toastUtils.showInfoWithStatus("请检查硬件设备");
                        }
                    });

                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llModifyScan:
                this.llModifyScan.setBackgroundColor(getResources().getColor(R.color.gray_line));
                this.llModifyScan.setClickable(false);
                startScanDevice();
                break;
            case R.id.llRelate:
                String type = device.getType();
                if (type.equals("HGC")) {//如果是中控面板，跳转到中控配置界面
                    Intent i = new Intent(ModifyDeviceListeningActivity.this, DeviceSettingListeningActivity.class);
                    Bundle bundle = new Bundle();//TODO 重写 HGC
                    bundle.putString("HGCADDR", device.getAddr());
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (type.equals("Pannel")) {
//                    toastUtils.showInfoWithStatus("开发过程中，敬请期待");
                    Intent intent = new Intent(this, PanelSettingActivity.class);
                    intent.putExtra("device", device);
                    intent.putExtra("room", room);
                    startActivity(intent);

//                    ModeRoomPopUpWindow modeRoomPopUpWindow = new ModeRoomPopUpWindow(this, null, new ModeRoomPopUpWindow.ModeSelectListener() {
//                        @Override
//                        public void result(int position) {
//
//                        }
//                    });

                } else {
                    popUpWindow = new RelateDevicePopUpWindow(this, device, mRelateDevice,
                            new RelateDevicePopUpWindow.ClickResultListener() {
                                @Override
                                public void clickResult(int tag) {

                                }
                            });
                    popUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.tvModifyConfirm:
                updateDevice();
                break;
            case R.id.ivRight:
                Intent intent = new Intent(this, DeviceModeListeningActivity.class);
                intent.putExtra(DeviceModeListeningActivity.BUNDLE_ACITON_DEVICE_INFO, device);
                startActivity(intent);
                break;

        }

    }


    /**
     * 修改设备
     */
    private void updateDevice() {
        if (StringUtil.isEmpty(etModifyDeviceName.getText().toString())) {
            toastUtils.showErrorWithStatus(getString(R.string.device_name_cannot_null));
            return;
        }
        if (StringUtil.isEmpty(etModifyAddress.getText().toString())) {
            toastUtils.showErrorWithStatus(getString(R.string.device_address_cannot_null));
            return;
        }
        device.setName(etModifyDeviceName.getText().toString());
        device.setAddr(etModifyAddress.getText().toString());
        device.setNote(etModifyRemark.getText().toString());

        String addr = device.toString();

        //TODO 需要传递进来的Device  处理数据
        DeviceController.getInstance().updateProp(ModifyDeviceListeningActivity.this, device, "true", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        if (StringUtil.isEmpty(result.getMsg())) {
                            String msg = ResultRetDeal.getInstance().dealWith(Json);
                            toastUtils.showErrorWithStatus(msg);
                        } else
                            toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        toastUtils.showSuccessWithStatus("修改成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK, new Intent());
                                finish();
                            }
                        }, 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                L.e("ModifyDeviceActivity_updateProp_Json===" + Json);//{"ret":"0","response":{"roomname":"xxxx","areaId":"1","keyId":35,"addr":"AABB2507004B12000000","timestamp":1465354933,"dismiss":false,"note":"","type":"Light2","Y":"124.5","X":"216.0","roomId":"3","areaname":"新区域","name":"二联灯"}}

//                String ret = JsonUtil.parseString(Json, "ret");
//                if ("0".equals(ret)) {
//                    toastUtils.showSuccessWithStatus("修改成功");
//                } else {
//                    String msg = JsonUtil.parseString(Json, "msg");
//                    toastUtils.showErrorWithStatus(msg);
//                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null) {
                    toastUtils.showErrorWithStatus(json);

                }
                Loger.d(json);
            }
        });
    }

}
