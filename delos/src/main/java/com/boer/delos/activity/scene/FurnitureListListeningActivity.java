package com.boer.delos.activity.scene;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.FurnitureListAdapter;
import com.boer.delos.commen.ActivityCustomManager;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.EventCode;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.Area;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.Furniture;
import com.boer.delos.model.FurnitureChild;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.DeleteScenePopUpWindow;
import com.boer.delos.view.popupWindow.EditAreaPopupWindow;
import com.boer.delos.view.popupWindow.SceneEditPopUpWindow;
import com.boer.delos.view.popupWindow.SingleEditTextPopUpWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 场景中的家具列表界面，添加新设备或选择已有设备，并显示在界面上
 * create at 2016/4/15 11:50
 */
public class FurnitureListListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private ExpandableListView elvFurnitureList;
    private List<Furniture> furnitureList = new ArrayList<>();
    private List<FurnitureChild> childList;
    private FurnitureListAdapter adapter;
    private Room room;
    private Room currentRoom;
    private SceneEditPopUpWindow editPopUpWindow;
    private List<AddDevice> deliverList = new ArrayList<>();// 待传递的设备列表
    private List<Device> deviceList = new ArrayList<>();

    public static FurnitureListListeningActivity instance = null;
    private int checkPosition;
    private String customName;
    private String checkRoomType;
    private FurnitureChild child;
    private DeleteScenePopUpWindow dismissScenePopUpWindow;
    private SingleEditTextPopUpWindow mEditTextPopWindow;
//    private static final int TIMEOUTS = 15; //进度条超时条件

    private static final int DISMISS_DEVICE_DELAY_TIME = 1000;
    private int tempCount = 0;
    private boolean isFirst = true;
    private EditAreaPopupWindow areaPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_furniture_list, null);
        setContentView(view);
        instance = this;
        room = (Room) getIntent().getSerializableExtra("room");

        Intent intent = getIntent();
        checkPosition = intent.getIntExtra("checkPosition", -1);

        initView();
        initData();
        initListener();
        roomShow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCustomManager.getAppManager().getCurrentActivity() instanceof AlreadyHadDeviceListeningActivity) {
            getDeviceStatusInfo();
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void deviceStatusUpdate() {
//        if (adapter != null) { //展开不更新
//            for (int i = 0; i < adapter.getGroupCount(); i++) {
//                if (selvFurnitureList.isGroupExpanded(i)) {
//                    return;
//                }
//            }
//            setDatas();
//        }
        if (currentRoom == null) return;
        setData();

    }

    private void initData() {
        adapter = new FurnitureListAdapter(this, furnitureList,
                new FurnitureListAdapter.ClickListener() {
                    @Override
                    public void editClick(Device device) {
                        editDevice(device);
                    }

                    @Override
                    public void dismissClick(Device device) {
                        dismissDevice(device);
                    }
                });
        this.elvFurnitureList.setAdapter(adapter);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        this.elvFurnitureList.setIndicatorBounds(width - DensityUitl.dip2px(this, 114), width - DensityUitl.dip2px(this, 24));// 将ExpandListView的指示箭头放在屏幕右侧

    }

    private void initView() {
        initTopBar(room.getName(), null, true, false);
        ivRight.setImageResource(R.drawable.ic_family_title_add);
        ivRight.setOnClickListener(this);

        elvFurnitureList = (ExpandableListView) findViewById(R.id.elvFurnitureList);
    }

    // add by sunzhibin  只展开用户点击的组
    private void initListener() {
        elvFurnitureList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (adapter == null) return;
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (i != groupPosition) {
                        elvFurnitureList.collapseGroup(i);
                    }
                }
            }
        });

        adapter.setListner(new FurnitureListAdapter.GroupEditListener() {
            @Override
            public void areaEditClick(String areaId, int groupPos) {
                showEditAreaDialog(areaId, groupPos);
            }

        });
    }

    private void showEditAreaDialog(final String areaId, int groupPos) {
        if (areaPopupWindow != null && areaPopupWindow.isShowing()) {
            areaPopupWindow.dismiss();
            areaPopupWindow = null;
        }
        areaPopupWindow = new EditAreaPopupWindow(this, new EditAreaPopupWindow.AreaEditClickListener() {
            @Override
            public void editAreaName() {
                editArea(areaId);
            }

            @Override
            public void deleteAreaName() {
                areaDelete(areaId);
            }

            @Override
            public void areaAddNewDevice() {
                addDevice(areaId, getAreaByAreaId(areaId).getName());
            }

            @Override
            public void areaAddHaveDevice() {
                selectExistDevice(areaId, getAreaByAreaId(areaId).getName());
            }
        });
        int yOffset = ScreenUtils.getStatusHeight(this) + DensityUitl.dip2px(this, 50)
                + (1 + groupPos) * DensityUitl.dip2px(this, 44 + 10);

        areaPopupWindow.showAtLocation(getWindow().getDecorView(),
                Gravity.RIGHT | Gravity.TOP, DensityUitl.dip2px(this, 10), yOffset);


    }

    /**
     * 判断页面右边按钮是否显示
     */
    private void rightButtonHideOrShow() {
        //5个区域就不让添加了
        if (currentRoom != null && currentRoom.getAreas() != null
                && currentRoom.getAreas().size() >= 5) {
            ivRight.setVisibility(View.GONE);
        } else {
            ivRight.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 查询房间属性
     */
    private void roomShow() {

        RoomController.getInstance().roomShow(this, room.getRoomId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                if (toastUtils != null) toastUtils.dismiss();
                try {
                    RoomResult result = new Gson().fromJson(Json, RoomResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        if (result.getResponse() == null) {
                            return;
                        }
                        currentRoom = result.getResponse();
                        //右边按钮是否显示
                        rightButtonHideOrShow();
                        setData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils == null) return;
                toastUtils.showErrorWithStatus(json);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ivRight:
                mEditTextPopWindow = new SingleEditTextPopUpWindow(this,
                        R.string.new_area,
                        R.string.area_hint,
                        new SingleEditTextPopUpWindow.ClickListener() {

                            @Override
                            public void okClick(String text) {
                                if (!checkText(text)) {
                                    return;
                                }
                                addArea(text);
                            }
                        });
                mEditTextPopWindow.showAtLocation(ivRight, Gravity.NO_GRAVITY, 0, 0);
                break;
        }
    }

    /**
     * 检查输入的字符
     *
     * @param text
     * @return
     */
    private boolean checkText(String text) {
        if (text.length() == 0) {
            toastUtils.showErrorWithStatus("请输入内容");
            return false;
        } else if (text.contains(" ")) {
            toastUtils.showErrorWithStatus("不能包含空格");
        } else if (text.length() < 3) {
            toastUtils.showErrorWithStatus("请输入最少3个字符");
            return false;
        } else if (text.length() > 10) {
            toastUtils.showErrorWithStatus("超过限制");
            return false;
        }
        return true;
    }

    /**
     * _
     * 新增区域
     *
     * @param text
     */
    private void addArea(String text) {
        Room room = currentRoom;
        Area area = new Area();
        area.setName(text);
        area.setAreaId(getMaxAreaIdAdd());
        room.getAreas().add(area);
        //是否显示右边添加按钮
        rightButtonHideOrShow();

        updateRoom(room);
    }

    /**
     * 获取最大区域并加1
     *
     * @return
     */
    private String getMaxAreaIdAdd() {
        int max = 0;
        if (currentRoom.getAreas() != null) {
            for (Area area : currentRoom.getAreas()) {
                int areaId = Integer.parseInt(area.getAreaId());
                if (max < areaId) {
                    max = areaId;
                }
            }
        }
        max++;
        return String.valueOf(max);
    }


    /**
     * 获取设备管理列表数据
     */
    private void setData() {
        try {
            furnitureList.clear();
            List<DeviceRelate> list = Constant.DEVICE_RELATE;
            LinkedHashMap<String, List<Device>> map = new LinkedHashMap<>();
            for (Area area : currentRoom.getAreas()) {
                map.put(area.getAreaId(), new ArrayList<Device>());
            }

            for (DeviceRelate deviceRelate : list) {
                Device device = deviceRelate.getDeviceProp();
                //排除解绑和不是当前房间设备
                if (device == null || device.getDismiss() || device.getRoomId() == null
                        || device.getAreaId() == null || !device.getRoomId().equals(currentRoom.getRoomId())) {
                    continue;
                }
                List<Device> devices = map.get(device.getAreaId());
                if (devices != null) {
                    devices.add(device);
                }
            }

            for (String key : map.keySet()) {
                Furniture furniture = new Furniture();
                String areaName = getAreaNameAndId(key)[0];
                furniture.setGroupTitle(areaName);
                furniture.setAreaId(getAreaNameAndId(key)[1]);

                List<FurnitureChild> childList = new ArrayList<>();
                for (Device device : map.get(key)) {
                    FurnitureChild child = new FurnitureChild();
                    child.setIsData(true);
                    child.setAreaId(device.getAreaId());
                    child.setAreaName(device.getAreaname());
                    child.setResId(getResId(device.getType()));
                    child.setChildTitle(device.getName());
                    child.setComment(device.getNote());
                    child.setIsOnline(true);
                    child.setDevice(device);
                    childList.add(child);
                }
                furniture.setChildList(childList);
                furnitureList.add(furniture);
            }
            if (isFirst) {
                adapter.setFurnitureList(furnitureList);
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    elvFurnitureList.collapseGroup(i);//listView关闭
                    elvFurnitureList.expandGroup(adapter.getGroupCount() - 1);//listView展开
                }
                isFirst = false;
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片Id
     *
     * @param type
     * @return
     */
    private int getResId(String type) {
        int resId = 0;
        for (AddDevice addDevice : Constant.blueDeviceList()) {
            if (type.equals(addDevice.getType())) {
                resId = addDevice.getResId();
                break;
            }
        }
        return resId;
    }

    /**
     * 获取区域名称
     *
     * @param key
     * @return
     */
    private String[] getAreaNameAndId(String key) {
        for (Area area : currentRoom.getAreas()) {
            if (key.equals(area.getAreaId())) {
                return new String[]{area.getName(), area.getAreaId()};
            }
        }
        return new String[]{null};
    }

    /**
     * 根据区域Id 得到Area
     *
     * @param areaId
     * @return
     */
    private Area getAreaByAreaId(String areaId) {
        for (Area area : currentRoom.getAreas()) {
            if (areaId.equals(area.getAreaId())) {
                return area;
            }
        }
        return null;
    }

    /**
     * 新增设备
     * 为当前界面二级列表中，"添加新设备"按钮添加的跳转到新增设备界面的方法
     */
    public void addDevice(String areaId, String areaName) {
        Intent intent = new Intent(this, AddDeviceListeningActivity.class);
        intent.putExtra("FurnitureList", room);
        intent.putExtra("AreaId", areaId);
        intent.putExtra("AreaName", areaName);
        startActivityForResult(intent, EventCode.ADD_DEVICE_ACTIVITY);
//        Intent intent = new Intent(this, ModifyDeviceListeningActivity.class);
//        intent.putExtra("FurnitureList", room);
//        intent.putExtra("AreaId", areaId);
//        intent.putExtra("AreaName", areaName);
//        startActivity(intent);
    }

    /**
     * 选择已有设备
     * 为当前界面二级列表中，"选择已有设备"按钮添加的跳转到设备管理界面的方法
     */
    public void selectExistDevice(String areaId, String areaName) {
        Intent intent = new Intent(this, AlreadyHadDeviceListeningActivity.class);
        intent.putExtra("RoomObject", room);
        intent.putExtra("AreaId", areaId);
        intent.putExtra("AreaName", areaName);
        startActivity(intent);
//        startActivityForResult(intent, EventCode.ADD_DEVICE_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EventCode.ADD_DEVICE_ACTIVITY && resultCode == -1) {
            getDeviceStatusInfo();

        }
    }

    /**
     * 修改设备
     *
     * @param device
     */
    public void editDevice(Device device) {
        Intent intent = new Intent(FurnitureListListeningActivity.this, ModifyDeviceListeningActivity.class);
        intent.putExtra("FurnitureListRoom", room);
        intent.putExtra("CurrentDevice", device);
        String addr = device.toString();
//        intent.putExtra("childAreaId", areaId);
//        intent.putExtra("childAreaName", areaName);
        startActivityForResult(intent, EventCode.ADD_DEVICE_ACTIVITY);
    }

    /**
     * 解绑设备
     *
     * @param device
     */
    public void dismissDevice(final Device device) {
        final String title = getString(R.string.warning_text);
        final String message = getString(R.string.dismiss_message);
        dismissScenePopUpWindow = new DeleteScenePopUpWindow(this, title, message, new DeleteScenePopUpWindow.ClickResultListener() {
            @Override
            public void ClickResult(int tag) {
                dismissScenePopUpWindow.dismiss();
                toastUtils.showProgress("正在解绑...");
                DeviceController.getInstance().dismissDevice(FurnitureListListeningActivity.this, device,
                        new RequestResultListener() {
                            @Override
                            public void onSuccess(String Json) {
                                try {
                                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                                    if (result.getRet() != 0) {
                                        toastUtils.showErrorWithStatus(result.getMsg());
                                    } else {
//                                        roomShow();
                                        getDeviceStatusInfo();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
//                                                setDatas();
                                                if (toastUtils == null) return;
                                                toastUtils.showSuccessWithStatus("解绑成功");
                                            }
                                        }, DISMISS_DEVICE_DELAY_TIME);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                L.e("dismissDevice onSuccess json======" + Json);
//                                String ret = JsonUtil.parseString(Json, "ret");
//                                if ("0".equals(ret)) {
//                                    toastUtils.showSuccessWithStatus("\"" + device.getName() + "\"" + readString(R.string.had_unbind));
//                                    getData();
//                                } else {
//                                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
//                                }
                            }

                            @Override
                            public void onFailed(String json) {
                                toastUtils.showErrorWithStatus("解绑失败");
                            }
                        });
            }
        });
        dismissScenePopUpWindow.showAtLocation(ivRight, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * 修改区域名字
     *
     * @param areaId
     */
    private void editArea(final String areaId) {
        final SingleEditTextPopUpWindow editAreaNamePopUpWindow = new SingleEditTextPopUpWindow(this,
                R.string.area_name_edit, R.string.area_hint,
                new SingleEditTextPopUpWindow.ClickListener() {
                    @Override
                    public void okClick(String text) {
                        if (StringUtil.isEmpty(text)) {
                            return;
                        }
                        if (currentRoom == null) return;

                        Area tempArea = getAreaByAreaId(areaId);
                        tempArea.setName(text);
                        tempArea.setRoomId(currentRoom.getRoomId());
                        areaEditName(tempArea);
                    }
                });
        editAreaNamePopUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

    /**
     * 编辑区域名
     *
     * @param area
     */
    private void areaEditName(final Area area) {
        //TODO
        if (StringUtil.isEmpty(area.getAreaId())) {
            return;
        }
        RoomController.getInstance().areaUpdate(this, Constant.CURRENTHOSTID, area, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showErrorWithStatus(result.getMsg());
                } else {
                    //更新房间信息
                    for (Area tmp : currentRoom.getAreas()) {
                        if (tmp.getAreaId().equals(area.getAreaId())) {
                            tmp.setName(area.getName());
                            break;
                        }
                    }
                    updateRoom(currentRoom);
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });
    }

    private void areaDelete(final String areaId) {
        //TODO
        RoomController.getInstance().areaDelete(this, currentRoom.getRoomId(), areaId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                for (Area tmp : currentRoom.getAreas()) {
                    if (tmp.getAreaId().equals(areaId)) {
                        currentRoom.getAreas().remove(tmp);
                        break;
                    }
                }
                updateRoom(currentRoom);
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });
    }

    private void updateRoom(Room room) {
        RoomController.getInstance().updateRoom(this, room,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                roomShow();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }
}
