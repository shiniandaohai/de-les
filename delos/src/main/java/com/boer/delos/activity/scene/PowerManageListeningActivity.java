package com.boer.delos.activity.scene;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.PowerManageAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 电源管理界面
 * create at 2016/5/5 13:48
 */
public class PowerManageListeningActivity extends BaseListeningActivity {
    private ListView lvPowerList;
    private ImageView ivPowerSocketView;
    private PowerManageAdapter adapter;
    private List<DeviceRelate> powerList=new ArrayList<>();
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_manage);
        roomId = getIntent().getStringExtra("roomId");
        initView();
        initData();
    }

    @Override
    protected void deviceStatusUpdate() {
        setPowerInfo();
    }

    private void initView() {
        initTopBar(R.string.power_manage_title, null, true, false);
        lvPowerList = (ListView) findViewById(R.id.lvPowerList);
        ivPowerSocketView = (ImageView) findViewById(R.id.ivPowerSocket);
    }

    private void initData() {
        this.adapter=new PowerManageAdapter(this, new PowerManageAdapter.ClickResultListener() {
            @Override
            public void clickSwitch(int position) {
                DeviceRelate deviceRelate = (DeviceRelate) adapter.getItem(position);
                //发送控制命令
                sendDeviceControl(deviceRelate);

                //顶部大图更新
                socketImageViewUpdate();
            }
        });
        this.adapter.setList(this.powerList);
        this.lvPowerList.setAdapter(this.adapter);
        setPowerInfo();
    }

    /**
     * 设置插座数据
     */
    private void setPowerInfo(){
        List<DeviceRelate> deviceRelateList = Constant.DEVICE_RELATE;
        List<DeviceRelate> filterList = filterDismissDevice(deviceRelateList);
        if (filterList.size() > 0) {
            powerList.clear();
            powerList.addAll(filterList);
            adapter.notifyDataSetChanged();
        }

        //顶部大图更新
        socketImageViewUpdate();
    }

    /**
     * 发送插座控制命令
     * @param deviceRelate
     */
    private void sendDeviceControl(DeviceRelate deviceRelate){

        List<ControlDevice> controlDevices = new ArrayList<>();

        Device device = deviceRelate.getDeviceProp();
        ControlDevice controlDevice=new ControlDevice();
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setType(device.getType());
        ControlDeviceValue value=new ControlDeviceValue();
        DeviceStatusValue statusValue = deviceRelate.getDeviceStatus().getValue();
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

            }
        });
    }

    /**
     * 过滤解绑和非当前房间设备
     * @param list
     * @return
     */
    private List<DeviceRelate> filterDismissDevice(List<DeviceRelate> list){
        //过滤当前房间设备
        List<DeviceRelate> filterList = new ArrayList<>();
        for (DeviceRelate deviceRelate: list) {
            Device device = deviceRelate.getDeviceProp();
            if("Socket".equals(device.getType())) {
                //当前房间和未解绑设备
                if (roomId.equals(device.getRoomId()) && !device.getDismiss()) {
                    filterList.add(deviceRelate);
                }
            }
        }
        return filterList;
    }

    /**
     * 检查是否有插座打开
     * @param list
     * @return
     */
    private Boolean checkSocketIsOpen(List<DeviceRelate> list){
        for (DeviceRelate deviceRelate: list) {
            DeviceStatus deviceStatus = deviceRelate.getDeviceStatus();
            if(deviceStatus.getValue() != null && deviceStatus.getValue().getState() != null){
                String state = deviceStatus.getValue().getState();
                if("1".equals(state)){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     *  顶部大图更新
     */
    private void socketImageViewUpdate(){
        //判断是否有插座打开
        Boolean isSocketOpen = checkSocketIsOpen(powerList);
        if(isSocketOpen){
            ivPowerSocketView.setImageResource(R.drawable.ic_power_supply);
        }else{
            ivPowerSocketView.setImageResource(R.drawable.ic_power_supply_grey);
        }
    }
}
