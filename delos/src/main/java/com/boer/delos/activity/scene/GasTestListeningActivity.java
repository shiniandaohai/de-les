package com.boer.delos.activity.scene;

import android.os.Bundle;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.GasTestAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "气体检测测"界面
 * create at 2016/6/7 9:37
 *
 */
public class GasTestListeningActivity extends BaseListeningActivity {
    private ListView lvGasMonitor;
    private List<DeviceRelate> list = new ArrayList<>();
    private GasTestAdapter adapter;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_test);
        roomId = getIntent().getStringExtra("roomId");
        initView();
        initData();
    }

    @Override
    protected void deviceStatusUpdate() {
        setGasData();
    }

    private void initData() {
        adapter = new GasTestAdapter(this, list);
        this.lvGasMonitor.setAdapter(adapter);
        setGasData();
    }

    /**
     * 设置气体信息
     */
    private void setGasData(){
        List<DeviceRelate> deviceRelateList = Constant.DEVICE_RELATE;
        List<DeviceRelate> filterList = filterDismissDevice(deviceRelateList);
        if (filterList.size() > 0) {
            list.clear();
            list.addAll(filterList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 过滤解绑和非当前房间设备
     * @param list
     * @return
     */
    private List<DeviceRelate> filterDismissDevice(List<DeviceRelate> list){
        List<DeviceRelate> filterList = new ArrayList<>();
        for (DeviceRelate deviceRelate: list) {
            Device device = deviceRelate.getDeviceProp();
            //Ch4CO, Smoke, Env, O2CO2
            if("Ch4CO".equals(device.getType()) || "O2CO2".equals(device.getType()) ||
                    "Env".equals(device.getType()) || "Smoke".equals(device.getType())){
                //未解绑设备和当前房间设备
                if(!device.getDismiss() && roomId.equals(device.getRoomId())){
                    filterList.add(deviceRelate);
                }
            }
        }
        return filterList;
    }

    private void initView() {
        initTopBar(getString(R.string.gas_test_title),null,true,false);
        this.lvGasMonitor = (ListView) findViewById(R.id.lvGasMonitor);
    }

}
