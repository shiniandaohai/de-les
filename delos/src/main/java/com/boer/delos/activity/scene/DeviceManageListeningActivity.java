package com.boer.delos.activity.scene;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.boer.delos.R;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.activity.main.HomepageListeningActivity;
import com.boer.delos.adapter.DeviceManageAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceManage;
import com.boer.delos.model.DeviceManageChild;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.camera.MyCamera;
import com.boer.delos.view.popupWindow.DeleteScenePopUpWindow;
import com.google.gson.Gson;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "设备管理"界面
 * create at 2016/4/7 17:35
 */
public class DeviceManageListeningActivity extends BaseListeningActivity implements IRegisterIOTCListener {

    protected ExpandableListView elvDeviceManage;
    protected List<DeviceManage> deviceList = new ArrayList<>();
    protected List<DeviceManageChild> childList0, childList1, childList2, childList3, childList4,
            childList5, childList6, childList7, childList8, childList9;
    protected DeviceManageAdapter adapter;

    protected DeleteScenePopUpWindow deleteScenePopUpWindow, dismissScenePopUpWindow;

    public static DeviceManageListeningActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        initView();
        initData();
        initListener();
    }

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

    protected void initData() {
        adapter = (DeviceManageAdapter) getAdatper();
        elvDeviceManage.setAdapter(adapter);
        reloadDeviceList();
    }

    protected BaseExpandableListAdapter getAdatper() {
        return new DeviceManageAdapter(this, deviceList, new DeviceManageAdapter.ClickListener() {
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

    protected void initView() {
        setContentView(R.layout.activity_device_manage);
        initTopBar(R.string.device_manage, null, true, false);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        elvDeviceManage = (ExpandableListView) findViewById(R.id.elvDeviceManage);
        elvDeviceManage.setIndicatorBounds(width - 120, width - 30);// 将ExpandListView的指示箭头放在屏幕右侧
    }

    /**
     * 删除设备
     *
     * @param device 要删除的设备
     */
    protected void removeDevice(final Device device) {
        final String title = getString(R.string.warning_text);
        final String message = getString(R.string.delete_message);
        deleteScenePopUpWindow = new DeleteScenePopUpWindow(this, title, message,
                new DeleteScenePopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        deleteScenePopUpWindow.dismiss();
                        List<Device> devices = new ArrayList<>();
                        devices.add(device);
                        toastUtils.showProgress("设备删除中...");
                        DeviceController.getInstance().removeDevice(DeviceManageListeningActivity.this, devices, new RequestResultListener() {
                            @Override
                            public void onSuccess(String Json) {
                                try {
                                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                                    if (result.getRet() != 0) {
                                        toastUtils.showErrorWithStatus(result.getMsg());
                                    } else {
                                        getDeviceStatusInfo(true);
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
                });
        deleteScenePopUpWindow.showAtLocation(ivRight, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * 解绑设备
     *
     * @param device
     */
    protected void dismissDevice(final Device device) {
        final String title = getString(R.string.warning_text);
        final String message = getString(R.string.dismiss_message);
        dismissScenePopUpWindow = new DeleteScenePopUpWindow(this, title, message, new DeleteScenePopUpWindow.ClickResultListener() {
            @Override
            public void ClickResult(int tag) {
                dismissScenePopUpWindow.dismiss();
                toastUtils.showProgress("设备解绑中...");
                DeviceController.getInstance().dismissDevice(DeviceManageListeningActivity.this, device, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                getDeviceStatusInfo(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        L.e("dismissDevice onSuccess json======" + Json);
//                        String ret = JsonUtil.parseString(Json, "ret");
//                        if ("0".equals(ret)) {
//                            toastUtils.showSuccessWithStatus("\"" + device.getName() + "\"" + readString(R.string.had_unbind));
//                            getDeviceList();
//                            //如果是摄像头要多处理一步
//                            if(device.getType().equals("Camera")){
//                                for(Device dv:HomepageListeningActivity.mDeviceList){
//                                    if(dv.getAddr() == device.getAddr()){
//                                        dv.setDismiss(true);
//                                        break;
//                                    }
//                                }
//                            }
//                        } else {
//                            toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
//                        }
                    }

                    @Override
                    public void onFailed(String json) {
                    }
                });
            }
        });
        dismissScenePopUpWindow.showAtLocation(ivRight, Gravity.NO_GRAVITY, 0, 0);
    }

//    @Override
//    protected void getDeviceStatusInfo() {
//        super.getDeviceStatusInfo();
//
//    }

    /**
     * 重新获取数据
     */
    public void getDeviceStatusInfo(final boolean showToastUtils) {
        getDeviceStatusInfo();
        if (showToastUtils) {
            toastUtils.showSuccessWithStatus("操作成功");
        }
//        reloadDeviceList();
    }
//    /**
//     * 重新绑定设备
//     *
//     * @param device
//     */
//    public void rebindDevice(final Device device) {
//        device.setDismiss(false);
//        WifiAirCleanController.getInstance().updateProp(this, device, "true", new RequestResultListener() {
//            @Override
//            public void onSuccess(String Json) {
//                L.e("rebindDevice onSuccess json======" + Json);
//                String ret = JsonUtil.parseString(Json, "ret");
//                if ("0".equals(ret)) {
//                    toastUtils.showSuccessWithStatus("\"" + device.getName() + "\"" + readString(R.string.had_rebind));
//                    getDeviceList();
//                    //如果是摄像头再增加一步处理，后续会修改掉
//                    if(device.getType().equals("Camera")){
//                        for(Device dv:HomepageListeningActivity.mDeviceList){
//                            if(dv.getAddr() == device.getAddr()){
//                                dv.setDismiss(false);
//                                break;
//                            }
//                        }
//                    }
//                } else {
//                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
//                }
//            }
//
//            @Override
//            public void onFailed(String json) {
//            }
//        });
//    }

    /**
     * 刷新设备列表
     */
    protected void reloadDeviceList() {
        if (childList0 != null) {
            childList0.clear();
        } else {
            childList0 = new ArrayList<>();
        }

        if (childList1 != null) {
            childList1.clear();
        } else {
            childList1 = new ArrayList<>();
        }

        if (childList2 != null) {
            childList2.clear();
        } else {
            childList2 = new ArrayList<>();
        }

        if (childList3 != null) {
            childList3.clear();
        } else {
            childList3 = new ArrayList<>();
        }

        if (childList4 != null) {
            childList4.clear();
        } else {
            childList4 = new ArrayList<>();
        }

        if (childList5 != null) {
            childList5.clear();
        } else {
            childList5 = new ArrayList<>();
        }
        if (childList6 != null) {
            childList6.clear();
        } else {
            childList6 = new ArrayList<>();
        }

        if (childList7 != null) {
            childList7.clear();
        } else {
            childList7 = new ArrayList<>();
        }

        if (childList8 != null) {
            childList8.clear();
        } else {
            childList8 = new ArrayList<>();
        }

        if (childList9 != null) {
            childList9.clear();
        } else {
            childList9 = new ArrayList<>();
        }

        if (deviceList != null) {
            deviceList.clear();
        } else {
            deviceList = new ArrayList<>();
        }

        setDeviceClassifyList();
    }

    /**
     * 获取设备管理列表数据
     */
    protected void setDeviceClassifyList() {
        List<DeviceRelate> list = Constant.DEVICE_RELATE;
        for (DeviceRelate deviceRelate : list) {
            Device item = deviceRelate.getDeviceProp();
            switch (item.getType()) {
                case "Light1":
                case "Light2":
                case "Light3":
                case "Light4":
                case "LightAdjust":
                    if (childList0 == null) {
                        childList0 = new ArrayList<>();
                    }
                    switch (item.getType()) {
                        case "Light1":
                            childList0.add(new DeviceManageChild(R.drawable.ic_device_light_one, item));
                            break;
                        case "Light2":
                            childList0.add(new DeviceManageChild(R.drawable.ic_device_light_two, item));
                            break;
                        case "Light3":
                            childList0.add(new DeviceManageChild(R.drawable.ic_device_light_three, item));
                            break;
                        case "Light4":
                            childList0.add(new DeviceManageChild(R.drawable.ic_device_light_four, item));
                            break;
                        case "LightAdjust":
                            childList0.add(new DeviceManageChild(R.drawable.ic_device_light_adjust, item));
                            break;
                    }
                    break;
                case "Socket":
                    if (childList1 == null) {
                        childList1 = new ArrayList<>();
                    }
                    childList1.add(new DeviceManageChild(R.drawable.ic_device_socket, item));
                    break;
                case "AirCondition":
                case "CAC":
                    if (childList2 == null) {
                        childList2 = new ArrayList<>();
                    }
                    if ("AirCondition".equals(item.getType())) {
                        childList2.add(new DeviceManageChild(R.drawable.ic_device_air_condition_one, item));
                    }
                    if ("CAC".equals(item.getType())) {
                        childList2.add(new DeviceManageChild(R.drawable.ic_device_air_condition_two, item));
                    }
                    break;
                case "Camera":
                case "Guard":
                    if (childList3 == null) {
                        childList3 = new ArrayList<>();
                    }
                    if ("Camera".equals(item.getType())) {
                        childList3.add(new DeviceManageChild(R.drawable.ic_device_camera, item));
                    }
                    if ("Guard".equals(item.getType())) {
                        childList3.add(new DeviceManageChild(R.drawable.ic_graud_l, item));
                    }

                    break;
                case "Curtain":
                    if (childList4 == null) {
                        childList4 = new ArrayList<>();
                    }
                    childList4.add(new DeviceManageChild(R.drawable.ic_device_curtain, item));
                    break;
                case "Pannel":
                case "HGC":
                case "FloorHeating":
                    if (childList5 == null) {
                        childList5 = new ArrayList<>();
                    }
                    if ("Pannel".equals(item.getType())) {
                        childList5.add(new DeviceManageChild(R.drawable.ic_device_pannel, item));
                    }
                    if ("HGC".equals(item.getType())) {
                        childList5.add(new DeviceManageChild(R.drawable.ic_device_hgc, item));
                    }
                    // TODO 地暖的图片不正确
                    if ("FloorHeating".equals(item.getType())) {
                        childList5.add(new DeviceManageChild(R.drawable.ic_device_pannel, item));
                    }
                    break;
                case "DVD":
                case "IPTV":
                case "TV":
                case "Sound":
                case "Audio":
                case "Projector":
                    if (childList6 == null) {
                        childList6 = new ArrayList<>();
                    }
                    if ("DVD".equals(item.getType())) {
                        childList6.add(new DeviceManageChild(R.drawable.ic_device_dvd, item));
                    }
                    if ("IPTV".equals(item.getType())) {
                        childList6.add(new DeviceManageChild(R.drawable.ic_device_iptv, item));
                    }
                    if ("TV".equals(item.getType())) {
                        childList6.add(new DeviceManageChild(R.drawable.ic_device_tv, item));
                    }
                    if ("Sound".equals(item.getType())) {
                        childList6.add(new DeviceManageChild(R.drawable.ic_device_voice_control, item));
                    }
                    if ("Audio".equals(item.getType())) {
                        childList6.add(new DeviceManageChild(R.drawable.ic_living_room_audio_device, item));
                    }
                    if ("Projector".equals(item.getType())) {
                        childList6.add(new DeviceManageChild(R.drawable.ic_projector_device, item));
                    }
                    break;
                case "Ch4CO":
                case "O2CO2":
                case "Env":
                case "Smoke":
                    if (childList7 == null) {
                        childList7 = new ArrayList<>();
                    }
                    if ("Ch4CO".equals(item.getType())) {
                        childList7.add(new DeviceManageChild(R.drawable.ic_device_co, item));
                    }
                    if ("O2CO2".equals(item.getType())) {
                        childList7.add(new DeviceManageChild(R.drawable.ic_device_o2, item));
                    }
                    if ("Env".equals(item.getType())) {
                        childList7.add(new DeviceManageChild(R.drawable.ic_device_env, item));
                    }
                    if ("Smoke".equals(item.getType())) {
                        childList7.add(new DeviceManageChild(R.drawable.ic_device_gas_sensor, item));
                    }
                    break;
                case "Exist":
                case "Fall":
                case "Water":
                case "Sov":
                case "SOS":
                    if (childList8 == null) {
                        childList8 = new ArrayList<>();
                    }
                    if ("Exist".equals(item.getType())) {
                        childList8.add(new DeviceManageChild(R.drawable.ic_device_exist, item));
                    }
                    if ("Fall".equals(item.getType())) {
                        childList8.add(new DeviceManageChild(R.drawable.ic_device_tumble_sensor, item));
                    }
                    if ("Water".equals(item.getType())) {
                        childList8.add(new DeviceManageChild(R.drawable.ic_device_water, item));
                    }
                    if ("Sov".equals(item.getType())) {
                        childList8.add(new DeviceManageChild(R.drawable.ic_device_radiotube, item));
                    }
                    if ("SOS".equals(item.getType())) {
                        childList8.add(new DeviceManageChild(R.drawable.ic_device_alarm, item));
                    }
                    break;
                case "Lock":
                    if (childList9 == null) {
                        childList9 = new ArrayList<>();
                    }
                    childList9.add(new DeviceManageChild(R.drawable.ic_device_fingerprint_lock, item));
                    break;
            }
        }
        if (childList0 != null) {
            deviceList.add(new DeviceManage("照明控制", childList0));
        }
        if (childList1 != null) {
            deviceList.add(new DeviceManage("电源管理", childList1));
        }
        if (childList2 != null) {
            deviceList.add(new DeviceManage("空调控制", childList2));
        }
        if (childList3 != null) {
            deviceList.add(new DeviceManage(getString(R.string.manager_camera), childList3));
        }
        if (childList4 != null) {
            deviceList.add(new DeviceManage("窗帘控制", childList4));
        }
        if (childList5 != null) {
            deviceList.add(new DeviceManage("综合服务", childList5));
        }
        if (childList6 != null) {
            deviceList.add(new DeviceManage("影音控制", childList6));
        }
        if (childList7 != null) {
            deviceList.add(new DeviceManage("气体检测", childList7));
        }
        if (childList8 != null) {
            deviceList.add(new DeviceManage("安全告警", childList8));
        }
        if (childList9 != null) {
            deviceList.add(new DeviceManage("安全防护", childList9));
        }
        setAdapter();
    }

    protected void setAdapter() {
        adapter.setDeviceList(deviceList);
        adapter.notifyDataSetChanged();
    }

    //删除摄像头
    private void removeItemFromList(String deviceAddr) {
        int position = 0;
        Device deviceInfo = MainTabActivity.mDeviceList.get(position);
        for (int i = 0; i < MainTabActivity.mDeviceList.size(); i++) {
            deviceInfo = MainTabActivity.mDeviceList.get(i);
            if (deviceInfo.getAddr().equals(deviceAddr)) {
                position = i;
                break;
            }
        }
       /* DatabaseManager manager = new DatabaseManager(DeviceManageListeningActivity.this);
        SQLiteDatabase db = manager.getReadableDatabase();
        Cursor cursor = db.query(DatabaseManager.TABLE_SNAPSHOT, new String[]{"_id", "dev_uid", "file_path", "time"}, "dev_uid = '"
                + deviceInfo.UID + "'", null, null, null, "_id LIMIT " + HomepageListeningActivity.CAMERA_MAX_LIMITS);
        while (cursor.moveToNext()) {
            String file_path = cursor.readString(2);
            File file = new File(file_path);
            if (file.exists())
                file.delete();
        }
        cursor.close();
        db.close();
        manager.removeSnapshotByUID(deviceInfo.UID);
        manager.removeDeviceByUID(deviceInfo.UID);*/
        MyCamera myCamera = MainTabActivity.mCameraList.get(position);

        myCamera.stop(Camera.DEFAULT_AV_CHANNEL);
        myCamera.disconnect();
        myCamera.unregisterIOTCListener(instance);

        MainTabActivity.mDeviceList.remove(position);
        MainTabActivity.mCameraList.remove(position);
        //this.notifyDataSetChanged();
    }

//    //主机信息更新，重新加载数据
//    @Override
//    protected void gatewayUpdate() {
//        super.gatewayUpdate();
//        getDeviceStatusInfo(false);
//    }


    @Override
    protected void deviceStatusUpdate() {
        super.deviceStatusUpdate();
        reloadDeviceList();
    }


    @Override
    public void receiveFrameData(Camera camera, int i, Bitmap bitmap) {

    }

    @Override
    public void receiveFrameDataForMediaCodec(Camera camera, int i, byte[] bytes, int i1, int i2, byte[] bytes1, boolean b, int i3) {

    }

    @Override
    public void receiveFrameInfo(Camera camera, int i, long l, int i1, int i2, int i3, int i4) {

    }

    @Override
    public void receiveSessionInfo(Camera camera, int resultCode) {

    }

    @Override
    public void receiveChannelInfo(Camera camera, int i, int i1) {

    }

    @Override
    public void receiveIOCtrlData(Camera camera, int i, int i1, byte[] bytes) {

    }
}
