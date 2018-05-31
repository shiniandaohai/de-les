package com.boer.delos.activity.link;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.ChooseDeviceAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XieQingTing
 * @Description: 选择设备界面
 * create at 2016/4/12 14:27
 */
public class ChooseDeviceActivity extends CommonBaseActivity {

    private ListView lvChooseDeviceList;
    private List<Map<String, Object>> datas = new ArrayList<>();
    private ChooseDeviceAdapter adapter;

    private List<ModeDevice> modeDeviceList;
    private String linkType = "";

//    //需要过滤设备
//    private String[] filterDeviceTypes = new String[]{"HGC", "ElecMeter", "WaterMeter", "Water",
//            "Env", "Fall", "Smoke", "Ch4CO", "SOS", "O2CO2",
//            "Camera", "Pannel", "Guard", "Lock", "N4"};

    @Override
    protected int initLayout() {
        return R.layout.activity_choose_device;
    }


    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.select_device));
        tlTitleLayout.setRightText(getString(R.string.text_save));

        lvChooseDeviceList = (ListView) findViewById(R.id.lvChooseDeviceList);

        modeDeviceList = (List<ModeDevice>) getIntent().getSerializableExtra("modeDeviceList");


        //处理报警联动预案配置传入的值
        try {
            linkType = getIntent().getStringExtra("linkType");
        } catch (Exception e) {
            linkType = "";
            e.printStackTrace();
        }

    }

    @Override
    public void rightViewClick() {
        super.rightViewClick();
        //保存
        Intent intent = new Intent();
        intent.putExtra("selectDeviceList", (Serializable) getSelectList());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateAlarmLinkPlan() {

    }


    protected void initData() {
        adapter = new ChooseDeviceAdapter(this, datas);
        lvChooseDeviceList.setAdapter(adapter);
        //房间模式配置
//        if(fromActivity == Constant.MODE_SETTING_LINKMODE_ROOM){
//            for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
//                Device device = deviceRelate.getDeviceProp();
//                if (!device.getDismiss() && ) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("device", device);
//                    map.put("checked", checked(device));
//                    datas.add(map);
//                }
//            }
//        }
//        //全局模式和设备模式配置
//        else {
        List<String> filterDeviceTypeList = Arrays.asList(ConstantDeviceType.FILTER_DEVICE_TYPES_2);

        for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
            Device device = deviceRelate.getDeviceProp();
            //并且不在过滤设备范围中
            if (!filterDeviceTypeList.contains(device.getType())) {
                Map<String, Object> map = new HashMap<>();
                map.put("device", device);
                map.put("checked", checked(device));
                datas.add(map);
            }
        }
//        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initAction() {
        lvChooseDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = datas.get(position);
                Boolean checked = (Boolean) map.get("checked");
                if (checked) {
                    map.put("checked", Boolean.FALSE);
                } else {
                    map.put("checked", Boolean.TRUE);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 判断当前设备是否已选中
     *
     * @param device
     * @return
     */
    private Boolean checked(Device device) {
        if (modeDeviceList == null) {
            return Boolean.FALSE;
        }
        for (ModeDevice modeDevice : modeDeviceList) {
            if (modeDevice.getDeviceAddr().equals(device.getAddr())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 取得选择过的列表
     *
     * @return
     */
    private List<Device> getSelectList() {
        List<Device> list = new ArrayList<>();
        for (Map<String, Object> map : datas) {
            Boolean checked = (Boolean) map.get("checked");
            if (checked) {
                list.add((Device) map.get("device"));
            }
        }
        return list;
    }


}
