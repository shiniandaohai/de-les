package com.boer.delos.activity.link;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.adapter.LinkAlarmSettingAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by apple on 17/5/4.
 */

public class AlarmLinkPlanSettingActivity extends CommonBaseActivity {
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;

    private List<DeviceRelate> mlistData;
    private LinkAlarmSettingAdapter mAdapter;

    // 有两个联动配置的报警设备
    private static final String[] deviceType2 = new String[]{
            "Exist", "Gsm", "CurtainSensor", "Smoke", "Ch4CO", "O2CO2", "Env","LaserEgg"
            , "Water"
    };
    // 单个联动配置的报警设备
    private static final String[] deviceType1 = new String[]{
            "Fall", "Water", "Lock", "SOS"
    };

    @Override
    protected int initLayout() {
        return R.layout.activity_scene_manager;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_alarm_link_plan_setting));
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
        mListView = mPullToRefreshListView.getRefreshableView();

        mlistData = new ArrayList<>();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    protected void initData() {
        mAdapter = new LinkAlarmSettingAdapter(this, mlistData, R.layout.item_alarm_link_plan);
        mListView.setAdapter(mAdapter);

        getDeviceStatusInfo();
    }

    @Override
    protected void initAction() {
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getDeviceStatusInfo();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(AlarmLinkPlanSettingActivity.this,
                        LinkActionWithAlarmActivity.class);
                intent.putExtra("device", mlistData.get(i - 1));
                List<String> list = Arrays.asList(deviceType1);
                if (list.contains(mlistData.get(i - 1).getDeviceProp().getType())) {
                    intent.putExtra("type", "1");

                } else {
                    intent.putExtra("type", "2");

                }

                intent.putExtras(intent);
                startActivity(intent);

            }
        });
    }


    private void getDeviceStatusInfo() {
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    mPullToRefreshListView.onRefreshComplete();
                    Json = StringUtil.deviceStateStringReplaceMap(Json);
                    DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    List<DeviceRelate> mDeviceRelate = new ArrayList<>();
                    mDeviceRelate.addAll(result.getResponse());

                    Constant.DEVICE_RELATE = result.getResponse();
                    if (null == Constant.DEVICE_RELATE) {
                        Constant.DEVICE_RELATE = new ArrayList<>();
                    }


                    filterAlarmDevice(Constant.DEVICE_RELATE);

                    //如果是无外网本地连接
                    if (Constant.IS_LOCAL_CONNECTION && !Constant.IS_INTERNET_CONN) {
//                        L.i("getDeviceStatusInfo alarmList:" + result.getNewAlarmList());
                        //取本地通知
                        List<String> list = result.getNewAlarmList();
                        if (list.size() > 0) {
                            Intent intent = new Intent();
                            intent.putExtra("alarmList", (Serializable) list);
                            intent.setAction(Constant.ACTION_ALARM);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                mPullToRefreshListView.onRefreshComplete();
            }
        });
    }

    private void filterAlarmDevice(List<DeviceRelate> listData) {
        mlistData.clear();
        List<String> listType1 = Arrays.asList(deviceType1);
        List<String> listType2 = Arrays.asList(deviceType2);
//        listType1.addAll(listType2);
        for (DeviceRelate deviceRelate : listData) {
            Device device = deviceRelate.getDeviceProp();
            if (!listType1.contains(device.getType())
                    && !listType2.contains(device.getType())) {
                continue;
            }

//            if(device.getDismiss()||device.getRoomId().equals("")){
//                continue;
//            }

            mlistData.add(deviceRelate);
        }
        mAdapter.setDatas(mlistData);
    }
}
