package com.boer.delos.activity.scene;

import android.os.Bundle;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.SecurityAlarmAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "安全告警"界面
 * create at 2016/6/8 11:33
 */
public class SecurityAlarmListeningActivity extends BaseListeningActivity {
    private ListView lvSecurityAlarm;
    private List<DeviceRelate> list = new ArrayList<>();
    private SecurityAlarmAdapter adapter;
    private String roomId;
    private static List<String> ALARM_DEVICES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_alarm);
        roomId = getIntent().getStringExtra("roomId");
        ALARM_DEVICES = new ArrayList<>();
        //"Exist", "Fall", "Water", "Sov", "SOS", "Gsm", "CurtainSensor"
        ALARM_DEVICES.add("Exist");
        ALARM_DEVICES.add("Fall");
        ALARM_DEVICES.add("Sov");
        ALARM_DEVICES.add("SOS");
        ALARM_DEVICES.add("Gsm");
        ALARM_DEVICES.add("CurtainSensor");

        initView();
        initData();
    }

    @Override
    protected void deviceStatusUpdate() {
        setAlarmInfo();
    }

    private void initView() {
        initTopBar("安全告警", null, true, false);
        this.lvSecurityAlarm = (ListView) findViewById(R.id.lvSecurityAlarm);
    }

    private void initData() {
        adapter = new SecurityAlarmAdapter(this, list,
                new SecurityAlarmAdapter.ClickResultListener() {

                    @Override
                    public void clickSwitch(int position) {
                        DeviceRelate deviceRelate = list.get(position);
                        sendDeviceControl(deviceRelate);
                    }
                });
        this.lvSecurityAlarm.setAdapter(adapter);
        setAlarmInfo();
    }

    /**
     * 设置报警信息
     */
    private void setAlarmInfo() {
        List<DeviceRelate> deviceRelateList = Constant.DEVICE_RELATE;
        List<DeviceRelate> filterList = filterDismissDevice(deviceRelateList);
        if (filterList.size() > 0) {
            list.clear();
            list.addAll(filterList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 过滤解绑和非当间房间设备
     *
     * @param list
     * @return
     */
    private List<DeviceRelate> filterDismissDevice(List<DeviceRelate> list) {
        List<DeviceRelate> alarmDeviceList = new ArrayList<>();
        try {
            //查找告警设备和未解绑设备
            for (DeviceRelate deviceRelate : list) {
                Device device = deviceRelate.getDeviceProp();
                //"Exist","Fall","Water","Sov","SOS"
                if (ALARM_DEVICES.contains(device.getType())) {
                    //未解绑设备和当前房间设备
                    if (!device.getDismiss() && roomId.equals(device.getRoomId())) {
                        alarmDeviceList.add(deviceRelate);
                    }
                }
            }
        } catch (Exception e) {
            Loger.d("告警界面 设备类型为空");

        }
        return alarmDeviceList;
    }

    /**
     * 发送插座控制命令
     *
     * @param deviceRelate
     */
    private void sendDeviceControl(DeviceRelate deviceRelate) {

        List<ControlDevice> controlDevices = new ArrayList<>();

        Device device = deviceRelate.getDeviceProp();
        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value = new ControlDeviceValue();
        DeviceStatusValue statusValue = deviceRelate.getDeviceStatus().getValue();
        if (statusValue.getSet() != null) {
            value.setSet(String.valueOf(statusValue.getSet()));
        }
        value.setState(statusValue.getState());
        controlDevice.setValue(value);

        controlDevices.add(controlDevice);

        DeviceController.getInstance().deviceControl(this, controlDevices, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i("deviceControl_Json===" + Json);

            }

            @Override
            public void onFailed(String json) {
                Loger.d("deviceControl_Json===" + json);
            }
        });
    }
}
