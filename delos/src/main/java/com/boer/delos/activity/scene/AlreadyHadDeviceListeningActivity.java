package com.boer.delos.activity.scene;

import android.os.Bundle;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.boer.delos.R;
import com.boer.delos.adapter.AlreadyHadDeviceAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.Room;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.google.gson.Gson;

import java.util.List;

/**
 * @author PengJiYang
 * @Description: 点击"选择已有设备"按钮跳转后的设备管理界面
 * create at 2016/5/12 16:22
 */
public class AlreadyHadDeviceListeningActivity extends DeviceManageListeningActivity {

    private ExpandableListView elvDeviceManage;
    private AlreadyHadDeviceAdapter adapter;

    private List<AddDevice> deviceTypeList;

    public static AlreadyHadDeviceListeningActivity instance = null;
    private Room room;// 接收从上一个界面传递过来的Room对象
    private String areaId, areaName;// 接收从上一个界面传递过来的参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        room = (Room) getIntent().getSerializableExtra("RoomObject");
        areaId = getIntent().getStringExtra("AreaId");
        areaName = getIntent().getStringExtra("AreaName");

        deviceTypeList = Constant.blueDeviceList();
    }

    @Override
    protected void initData() {
        adapter = (AlreadyHadDeviceAdapter) getAdatper();
        this.elvDeviceManage.setAdapter(adapter);
        reloadDeviceList();
    }

    @Override
    protected void initListener() {
        elvDeviceManage.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (adapter == null) return;
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (i != groupPosition) {
                        elvDeviceManage.collapseGroup(i);
                    }
                }
            }
        });
    }

    @Override
    protected BaseExpandableListAdapter getAdatper() {
        return new AlreadyHadDeviceAdapter(this, deviceList,
                new AlreadyHadDeviceAdapter.ClickListener() {
                    @Override
                    public void rebindClick(Device device) {
                        rebindDevice(device);
                    }

                    @Override
                    public void unbindClick(Device device) {
                        dismissDevice(device);
                    }

                    @Override
                    public void deleteClick(Device device) {
                        removeDevice(device);
                    }
                });
    }

    @Override
    protected void setAdapter() {
        adapter.setDeviceList(deviceList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_already_had_device);
        initTopBar(R.string.select_device, null, true, false);
        this.elvDeviceManage = (ExpandableListView) findViewById(R.id.elvDeviceManage);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        this.elvDeviceManage.setIndicatorBounds(width - 120, width - 30);// 将ExpandListView的指示箭头放在屏幕右侧
    }

    public Room getRoom() {
        return room;
    }


    /**
     * 将列表中选中的设备，添加到指定房间的指定区域中
     *
     * @param device 被选中的设备
     */
    private void confirmAdd(Device device) {
        device.setRoomname(room.getName());
        device.setRoomId(room.getRoomId());
        device.setAreaname(areaName);
        device.setAreaId(areaId);

        DeviceController.getInstance().updateProp(this, device, "false", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("AlreadyHadDeviceListeningActivity updateProp onSuccess's json===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if (ret != null && "0".equals(ret)) {
                    finish();
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.showErrorWithStatus(json);
            }
        });
    }


    /**
     * 重新绑定设备
     *
     * @param device
     */
    public void rebindDevice(final Device device) {
        device.setDismiss(false);
        device.setRoomId(room.getRoomId());
        device.setRoomname(room.getName());
        device.setAreaId(areaId);
        device.setAreaname(areaName);
        toastUtils.showProgress("设备绑定中...");
        DeviceController.getInstance().updateProp(this, device, "true", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        getDeviceStatusInfo(true);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
////                                toastUtils.showSuccessWithStatus("绑定成功");
//                            }
//                        }, 1000);
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

    //    /**
//     * 获取设备列表
//     */
//    private void getDeviceList() {
//        WifiAirCleanController.getInstance().properties(this, new RequestResultListener() {
//            @Override
//            public void onSuccess(String Json) {
//                L.e("AlreadyHadDeviceListeningActivity properties json======" + Json);
//                String ret = JsonUtil.parseString(Json, "ret");
//                if (ret != null && "0".equals(ret)) {
//                    Constant.deviceList.clearAll();
//                    Constant.deviceList = JsonUtil.parseDataList(Json, Device.class, "response");
//                    reloadDeviceList();
//                } else {
//                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
//                }
//            }
//
//            @Override
//            public void onFailed(String json) {
//                toastUtils.showErrorWithStatus(json);
//            }
//        });
//    }

}
