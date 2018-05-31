package com.boer.delos.activity.scene;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.AddBatchDeviceAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.CircandianLight;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.Room;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.animation.Animation.RELATIVE_TO_PARENT;
import static android.view.animation.Animation.RELATIVE_TO_SELF;


public class AddBatchScanResultActivity extends CommonBaseActivity implements AddBatchDeviceAdapter.OnControlListener {
    @Bind(R.id.tv_select_all)
    CheckedTextView mTvSelectAll;
    @Bind(R.id.listView_addBatch)
    ListView mListViewAddBatch;

    @Bind(R.id.btn_save)
    Button mBtnSave;
    @Bind(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @Bind(R.id.btn_refresh)
    Button mBtnRefresh;
    @Bind(R.id.tv_device_num)
    TextView tvDeviceNum;
    @Bind(R.id.tv_tip)
    TextView tvTip;

    private AddBatchDeviceAdapter mAdapter;

    private List<Device> mDeviceLists;// 扫描的设备
    List<CircandianLight> mCircandianLightList;
    private final int REQ_DISTRIBUTION_ROOM = 1001;
    private int pos_room_device = -1;

    private DeviceRelate device;
    private String n4Ads;

    @Override
    protected int initLayout() {
        return R.layout.activity_add_batch_scan;
    }

    protected void initView() {
        tlTitleLayout.setTitle("新增设备");
        hideInput();
    }


    protected void initData() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            device = (DeviceRelate) bundle.getSerializable("device");


        if (device != null) {
            n4Ads = device.getDeviceProp().getAddr();
//                n4Ads = "KP00001622";
            startScanN4Device(Constant.LOCAL_CONNECTION_IP);
        } else {
            mDeviceLists = (List<Device>) getIntent().getSerializableExtra("list");
        }

        if (mDeviceLists == null)
            mDeviceLists = new ArrayList<>();


        mAdapter = new AddBatchDeviceAdapter(this, mDeviceLists, R.layout.item_addbatch_device);
        mListViewAddBatch.setAdapter(mAdapter);
        mAdapter.setListener(this);
        mAdapter.notifyDataSetChanged();
        if(mAdapter.getCount()>0){
            tvTip.setVisibility(View.GONE);
            mListViewAddBatch.setVisibility(View.VISIBLE);
        }
        else{
            tvTip.setVisibility(View.VISIBLE);
            mListViewAddBatch.setVisibility(View.GONE);
        }
    }


    @Override
    protected void initAction() {

    }

    private void startScanN4Device(final String ip) {

        toastUtils.showProgress("");
        DeviceController.getInstance().startAddBatchScanN4Device(this, ip, n4Ads, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String response = jsonObject.getString("response");
                    Gson gson = new Gson();
                    mCircandianLightList = gson.fromJson(response, new TypeToken<List<CircandianLight>>() {
                    }.getType());

                    if(mCircandianLightList.size()>0){
                        tvTip.setVisibility(View.GONE);
                        mListViewAddBatch.setVisibility(View.VISIBLE);
                    }
                    else{
                        tvTip.setVisibility(View.VISIBLE);
                        mListViewAddBatch.setVisibility(View.GONE);
                    }

                    for (CircandianLight circandianLight : mCircandianLightList) {

                        Device device = new Device();
                        device.setName(circandianLight.getname());
                        device.setAddr(circandianLight.getAddr());
                        device.setType(circandianLight.getType());
                        mDeviceLists.add(device);

                    }
                    mAdapter.notifyDataSetChanged();

                    if(mAdapter.getCount()>0){
                        tvTip.setVisibility(View.GONE);
                        mListViewAddBatch.setVisibility(View.VISIBLE);
                    }
                    else{
                        tvTip.setVisibility(View.VISIBLE);
                        mListViewAddBatch.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
                if (toastUtils != null)
                    toastUtils.dismiss();
            }
        });

    }


    float y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (y - event.getAction() < 0) {
                    //开启动画
                    startAnimationUp();
                }
                startAnimationDown();
                y = event.getRawY();

                break;
        }

        return super.onTouchEvent(event);
    }

    @OnClick({R.id.tv_select_all, R.id.btn_refresh, R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_all:
                mTvSelectAll.toggle();
                for (Device device : mDeviceLists) {
                    device.setChecked(mTvSelectAll.isChecked());
                }

                setNumSelect();

                mAdapter.notifyDataSetChanged();
                if(mAdapter.getCount()>0){
                    tvTip.setVisibility(View.GONE);
                    mListViewAddBatch.setVisibility(View.VISIBLE);
                }
                else{
                    tvTip.setVisibility(View.VISIBLE);
                    mListViewAddBatch.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_refresh:
                if (device != null) {

                    mDeviceLists.clear();
                    startScanN4Device(Constant.LOCAL_CONNECTION_IP);

                } else {

                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();

                }
                break;
            case R.id.btn_save:
                //保存
                saveDevices();
                break;
        }
    }

    private void setNumSelect() {

        int num = 0;
        for (Device device : mDeviceLists) {

            if (device.isChecked()) {

                num++;
            }


        }
        tvDeviceNum.setText(num + "");

    }

    private Animation startAnimationUp() {
        TranslateAnimation animation = new TranslateAnimation(RELATIVE_TO_PARENT, 0,
                RELATIVE_TO_PARENT, 0,
                RELATIVE_TO_PARENT, 1,
                RELATIVE_TO_PARENT, 0);
        animation.setDuration(500);
        return animation;
    }

    private Animation startAnimationDown() {
        TranslateAnimation animation = new TranslateAnimation(RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, 0,
                RELATIVE_TO_SELF, 1,
                RELATIVE_TO_SELF, 0);
        animation.setDuration(500);
        return animation;
    }


    @Override
    public void clickListener(int pos) {
        //刷新数据 修改设备区域、 showRoom
        pos_room_device = pos;
        Intent intent = new Intent(this, RoomManageActivity.class);
        intent.putExtra("entrance", this.getLocalClassName());
        Loger.v("getLocalClassName()==" + this.getLocalClassName());
        intent.putExtra("name",mDeviceLists.get(pos).getRoomId());
        startActivityForResult(intent, REQ_DISTRIBUTION_ROOM);
    }

    @Override
    public void clickListener2(int pos, String flag) {
        //开关灯
        ArrayList<ControlDevice> list = new ArrayList<>();
        Device device = mDeviceLists.get(pos);

        ControlDevice controlDevice = new ControlDevice();
        controlDevice.setAddr(device.getAddr());
        controlDevice.setAreaName(device.getAreaname());
        controlDevice.setDeviceName(device.getName());
        controlDevice.setRoomName(device.getRoomname());
        controlDevice.setType(device.getType());

        //TODO
        ControlDeviceValue value = new ControlDeviceValue();

        controlDevice.setValue(value);
        list.add(controlDevice);
        controlDevice(list, pos);
    }

    private void controlDevice(List<ControlDevice> list, int postion) {
        DeviceController.getInstance().deviceControl(this, list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {

            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

//    private void showRoomPopupWindow(final Device device) {
//        Gateway gateWay = Constant.GATEWAY;
//        List<Room> rooms = gateWay.getRoom();
//
//        addBatchChoiseRoom = new AddBatchChoiseRoom(this, R.layout.popup_addbatch_add_room);
//        addBatchChoiseRoom.setRoomList(rooms);
//        addBatchChoiseRoom.setListener(new IObjectInterface<Room>() {
//            @Override
//            public void onClickListenerOK(Room room, int pos, String tag) {
//                //开启区域popup
//                if (room.getName().equals("-99")) {
//                    changeDeviceAreaName(device);
//                } else
//                    showAreaPopup(room, device);
//            }
//        });
//
//    }

//    private void showAreaPopup(Room room, final Device device) {
//        listPopupWindow listPopupWindow = new listPopupWindow(this, R.layout.popup_listview);
//        listPopupWindow.setRoom(room);
//        listPopupWindow.setListener(new IObjectInterface<Area>() {
//            @Override
//            public void onClickListenerOK(Area area, int pos, String tag) {
//                device.setRoomId(area.getRoomId());
//                device.setAreaId(area.getAreaId());
//                device.setAreaname(area.getName());
//                updateDevice(device);
//
//            }
//        });
//        listPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//    }
//    private void changeDeviceAreaName(final Device device) {
//        if (popupWindow != null && popupWindow.isShowing()) {
//            popupWindow.dismiss();
//        }
//        if (popupWindow == null)
//            popupWindow = new AddBatchAddRoomPopupWindow(this, R.layout.popup_addbatch_room);
//        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//        popupWindow.setListener(new IObjectInterface<Room>() {
//            @Override
//            public void onClickListenerOK(Room room, int pos, String tag) {
//                device.setRoomId(room.getRoomId());
//                device.setAreaId(room.getAreas().get(pos).getAreaId());
//                updateDevice(device);
//            }
//        });
//    }

    private void updateDevice(Device device) {
        DeviceController.getInstance().updateProp(this, device, "true", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                //修改成功，刷新数据
                String ret = JsonUtil.parseString(json, "ret");
                if (ret != null && "0".equals(ret)) {
                    finish();
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(json));
                }
            }

            @Override
            public void onFailed(String json) {

            }

        });
    }

    private void saveDevices() {


        if (device != null) {//N4

            List<CircandianLight> list = new ArrayList<>();
            for (int j = 0; j < mDeviceLists.size(); j++) {

                if (mDeviceLists.get(j).isChecked()) {

                    CircandianLight circandianLight = mCircandianLightList.get(j);
                    Device device = mDeviceLists.get(j);
                    if (!TextUtils.isEmpty(device.getRoomId())) {
                        circandianLight.setRoomname(device.getRoomname());
                        circandianLight.setRoomId(device.getRoomId());
                    }
                    circandianLight.setname(device.getName());
                    list.add(circandianLight);
                }
            }


            if (list.size() > 0) {
                DeviceController.getInstance().saveAddBatchScanN4Device(this, list, Constant.LOCAL_CONNECTION_IP, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                        if (result.getRet() != 0) {
                            toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg()) ? getString(R.string.save_fail) : result.getMsg());
                            return;
                        }

                        finish();

                    }

                    @Override
                    public void onFailed(String json) {

                    }
                });

            } else {

                ToastHelper.showShortMsg("请选择设备");


            }

        } else {

            List<Device> list = new ArrayList<>();
            for (Device device : mDeviceLists) {

                if (device.isChecked()) {
                    list.add(device);
                }
            }

            if (list.size() > 0) {
                DeviceController.getInstance().saveAddBatchScanDevice(this, list, Constant.LOCAL_CONNECTION_IP, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                        if (result.getRet() != 0) {
                            toastUtils.showErrorWithStatus(StringUtil.isEmpty(result.getMsg()) ? getString(R.string.save_fail) : result.getMsg());
                            return;
                        }

                        finish();

                    }

                    @Override
                    public void onFailed(String json) {

                    }
                });

            } else {

                ToastHelper.showShortMsg("请选择设备");


            }


        }

    }


    @Override
    public void onCheck(int pos, boolean check) {
        Log.v("gl", "pos==" + pos + "check==" + check);
        Device device = mDeviceLists.get(pos);
        device.setChecked(check);
        mDeviceLists.set(pos, device);

        setNumSelect();

    }

    @Override
    public void onEditChange(int pos, String name) {
        Log.v("gl", "pos==" + pos + "name==" + name);
        Device device = mDeviceLists.get(pos);
        device.setName(name);
        mDeviceLists.set(pos, device);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_DISTRIBUTION_ROOM:
                if (resultCode == RESULT_OK) {
                    Room room = (Room) data.getSerializableExtra("room");
                    Log.v("gl", "room===" + room.getRoomId());
                    mDeviceLists.get(pos_room_device).setRoomname(room.getName());
                    mDeviceLists.get(pos_room_device).setRoomId(room.getRoomId());
                    mAdapter.notifyDataSetChanged();
                    if(mAdapter.getCount()>0){
                        tvTip.setVisibility(View.GONE);
                        mListViewAddBatch.setVisibility(View.VISIBLE);
                    }
                    else{
                        tvTip.setVisibility(View.VISIBLE);
                        mListViewAddBatch.setVisibility(View.GONE);
                    }
                }
        }
    }
}
