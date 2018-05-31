package com.boer.delos.activity.link;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.LinkPlanListAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 联动预案配置界面
 * create at 2016/4/12 9:42
 */
public class LinkPlanListeningActivity extends BaseListeningActivity {
    private View view;
    private android.widget.ListView lvLinkPlanList;
    private List<Device> planList = new ArrayList<>();
    private LinkPlanListAdapter adapter;
    private String[] deviceTypes = new String[]{"Exist", "SOS", "Smoke", "Lock", "Fall", "Water",
            "Env", "Ch4CO", "O2CO2", "Gsm", "CurtainSensor"}; // add "Gsm", "CurtainSensor" 2017/2/20

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_link_plan, null);
        setContentView(view);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        initTopBar(R.string.alarm_link_plan_title, null, true, false);
        this.lvLinkPlanList = (ListView) findViewById(R.id.lvLinkPlanList);
    }

    private void initData() {
        adapter = new LinkPlanListAdapter(this);
        adapter.setDatas(planList);
        this.lvLinkPlanList.setAdapter(adapter);
        List<DeviceRelate> deviceRelateList = Constant.DEVICE_RELATE;
        List<String> deviceTypeList = Arrays.asList(deviceTypes);
        for (DeviceRelate deviceRelate : deviceRelateList) {
            Device device = deviceRelate.getDeviceProp();
            //判断是否在传感器列表里
            if (deviceTypeList.contains(device.getType())) {
                planList.add(device);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initListener() {
        this.lvLinkPlanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device planDevice = planList.get(position);
                //存在传感器需要报警时和恢复后的操作
                if ("Exist".equals(planDevice.getType())) {
                    Intent intent = new Intent(LinkPlanListeningActivity.this, LinkActionWithAlarmActivity.class);
                    intent.putExtra("ActivityName", Constant.MODE_SETTING_LINKMODE_DEVICE);
                    intent.putExtra("device", planDevice);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LinkPlanListeningActivity.this, LinkModelListeningActivity.class);
                    intent.putExtra("ActivityName", Constant.MODE_SETTING_LINKMODE_DEVICE);
                    intent.putExtra("device", planDevice);
                    startActivity(intent);
                }
            }
        });
    }
}
