package com.boer.delos.activity.scene;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.RoomDvicesPopAdapter;
import com.boer.delos.adapter.RoomManageAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomProperty;
import com.boer.delos.model.SceneManage;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.DensityUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by gaolong on 2017/4/1.
 */
public class RoomAddActivity extends CommonBaseActivity {
    @Bind(R.id.evCustomName)
    EditText evCustomName;
    @Bind(R.id.gvScenePopup)
    GridView gvScenePopup;
    @Bind(R.id.tv_devices_num)
    TextView tvDevicesNum;
    @Bind(R.id.gv_devices)
    GridView gvDevices;
    @Bind(R.id.hscrollView)
    HorizontalScrollView hscrollView;

    private List<SceneManage> sceneManages;
    private RoomManageAdapter roomManageAdapter;
    public int checkPosition = -1;
    public int pos = -1;


    List<DeviceRelate> deviceRelates;
    private List<Device> devices;
    private RoomDvicesPopAdapter roomDvicesPopAdapter;
    Room room;
    String str_devices_num = "已添加%s台设备";

    private int addRoomType;
    @Override
    protected int initLayout() {
        return R.layout.activity_room_add;
    }

    @Override
    protected void initView() {
        hindSoftKeyBoard();
        tlTitleLayout.setRightText(R.string.save);
        addRoomType =getIntent().getIntExtra("addRoomType",-1);
        if(addRoomType==0){
            tlTitleLayout.setTitle(R.string.room_add);
        }
        else if(addRoomType==1){
            tlTitleLayout.setTitle(R.string.room_edit);
        }
        else if(addRoomType==2){
            tlTitleLayout.setTitle("新建房间");
            gvDevices.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        room = (Room) getIntent().getSerializableExtra("room");
        checkPosition = getIntent().getIntExtra("checkPosition", -1);
        pos = getIntent().getIntExtra("position", -1);
        if (room != null) {
            evCustomName.setText(room.getName());
            evCustomName.setSelection(evCustomName.getText().length());
        }


        devices = new ArrayList<>();
        roomDvicesPopAdapter = new RoomDvicesPopAdapter(RoomAddActivity.this, 0);
        roomDvicesPopAdapter.setDatas(devices);
        gvDevices.setAdapter(roomDvicesPopAdapter);


        sceneManages = Constant.sceneTypeList();
        final DensityUtil desityUtil = new DensityUtil(this);
        int size = sceneManages.size();  //得到集合长度
        int width = getWindowManager().getDefaultDisplay().getWidth();
        final int pxClum = desityUtil.dip2px(10);
        final int itemWidth = (width - 4 * pxClum) / 3;
        Log.v("gl", "itemWidth==" + itemWidth);
        Log.v("gl", "size==" + size);
        int gridviewWidth = itemWidth * size + pxClum * (size + 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        gvScenePopup.setLayoutParams(params);
        gvScenePopup.setPadding(pxClum, pxClum, pxClum, pxClum);
        gvScenePopup.setColumnWidth(itemWidth);
        gvScenePopup.setHorizontalSpacing(desityUtil.dip2px(10));
        gvScenePopup.setStretchMode(GridView.NO_STRETCH);
        gvScenePopup.setNumColumns(size);
        roomManageAdapter = new RoomManageAdapter(0, -1, this);
        roomManageAdapter.setDatas(sceneManages);
        roomManageAdapter.setImageView(checkPosition);
        gvScenePopup.setAdapter(roomManageAdapter);

        getDevices();

        if(addRoomType==0||addRoomType==2){
            checkPosition = 0;
            SceneManage sceneManage = sceneManages.get(0);
            evCustomName.setText(getResources().getString(sceneManage.getItemName()));
            evCustomName.setSelection(evCustomName.getText().length());
            roomManageAdapter.setImageView(checkPosition);
            roomManageAdapter.notifyDataSetChanged();
        }

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                hscrollView.smoothScrollTo((gvScenePopup.getChildAt(checkPosition)).getLeft()-pxClum-itemWidth-desityUtil.dip2px(10),0);
            }
        }),1000);
    }

    @Override
    protected void initAction() {

        gvScenePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkPosition = position;
                SceneManage sceneManage = sceneManages.get(position);
                evCustomName.setText(getResources().getString(sceneManage.getItemName()));
                evCustomName.setSelection(evCustomName.getText().length());
                roomManageAdapter.setImageView(checkPosition);
                roomManageAdapter.notifyDataSetChanged();
            }
        });

        gvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Device device = devices.get(position);

                device.setChecked(!device.isChecked());
                //标记未绑定（false）&&选中状态（true）
                if (room != null) {   //解绑和绑定房间

                    if (!TextUtils.isEmpty(device.getRoomId())) {
                        if (device.getRoomId().equals(room.getRoomId())) {
                            if (!device.getDismiss() == device.isChecked())  //无变化（double钩选）
                                device.setUpdateStatus(2);
                            else if (device.isChecked()) {     //正在进行绑定
                                device.setUpdateStatus(1);
                                device.setDismiss(false);
                            } else {                            //正在进行解绑
                                device.setRoomId("");
                                device.setUpdateStatus(0);
                                device.setDismiss(true);
                            }
                        } else {
                            device.setUpdateStatus(1);          //重新绑定房间
                            device.setDismiss(false);
                            device.setRoomId(room.getRoomId());
                        }
                    } else {
                        if (device.isChecked()) {
                            device.setUpdateStatus(1);//绑定
                            device.setDismiss(false);
                            if (TextUtils.isEmpty(room.getRoomId()))
                                device.setRoomId("");//新增
                            else
                                device.setRoomId(room.getRoomId());//重新绑定
                        }
                    }
                } else {                //新增房间

                    if (device.isChecked()) {
                        device.setUpdateStatus(1);//绑定
                        device.setDismiss(false);
                        device.setRoomId("");    //新增
                    }
                }


                Log.v("gl", "getDismiss==" + device.getDismiss() + "==isChecked==" + device.isChecked() + "==isUpdateStatus==" + device.getUpdateStatus() + "==getRoomId===" + device.getRoomId());


                devices.set(position, device);


                int count = 0;
                for (Device device_temp : devices) {

                    if (device_temp.isChecked()) {

                        count++;

                    }
                }
                tvDevicesNum.setText(String.format(str_devices_num, count));


                roomDvicesPopAdapter.notifyDataSetChanged();
            }
        });


    }

    public void hindSoftKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void getRoomBindDevices(Room room) {


        List<Device> list = getBindDevices(room);


        tvDevicesNum.setText(String.format(str_devices_num, list.size()));

        roomDvicesPopAdapter.notifyDataSetChanged();

    }

    private List<Device> getBindDevices(Room currentRoom) {

        List<Device> bindDevices = new ArrayList<>();


        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            if (!TextUtils.isEmpty(device.getRoomId()) && !device.getDismiss() && device.getRoomId().equals(currentRoom.getRoomId())) {

                device.setChecked(true);
                devices.set(i, device);


                bindDevices.add(device);

            }

        }

        return bindDevices;


    }


    @Override
    public void rightViewClick() {
        super.rightViewClick();

        final RoomProperty roomProperty = new RoomProperty();
        final RoomProperty.RoomBean roomBean = new RoomProperty.RoomBean();
        List<RoomProperty.DeviceListBean> listBeen = new ArrayList<>();


        if (room != null)
            roomBean.setRoomId(room.getRoomId());
        else
            roomBean.setRoomId("");

        if (!TextUtils.isEmpty(evCustomName.getText()))
            roomBean.setName(evCustomName.getText().toString());
        else {
            ToastHelper.showShortMsg(getString(R.string.text_empty_room_name));
            return;
        }

        if (checkPosition != -1)
            roomBean.setType(getString(sceneManages.get(checkPosition).getItemName()));
        else {
            ToastHelper.showShortMsg(getString(R.string.text_empty_room));
            return;
        }


        roomProperty.setRoom(roomBean);


        for (Device chooseDevice : devices) {

            if (chooseDevice.getUpdateStatus() != 2) {

                RoomProperty.DeviceListBean deviceListBean = new RoomProperty.DeviceListBean();
                deviceListBean.setType(chooseDevice.getType());
                if (!TextUtils.isEmpty(chooseDevice.getRoomId()))
                    deviceListBean.setRoomId(chooseDevice.getRoomId());
                else
                    deviceListBean.setRoomId("");
                deviceListBean.setName(chooseDevice.getName());
                deviceListBean.setAddr(chooseDevice.getAddr());
                deviceListBean.setDismiss(chooseDevice.getDismiss());//选中状态true==未绑定true
                deviceListBean.setRoomname(evCustomName.getText().toString());
                deviceListBean.setNote(chooseDevice.getNote());
                deviceListBean.setFavorite(chooseDevice.getFavorite());

                listBeen.add(deviceListBean);

            }
        }

        roomProperty.setDeviceList(listBeen);

        RoomController roomController = new RoomController();
        toastUtils.showProgress("");

        String params = new Gson().toJson(roomProperty);
        Log.v("gl", "params==" + params);

        roomController.updateRoomProperty(this, roomProperty, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                toastUtils.dismiss();


                if(addRoomType==2){
                    Intent intent=new Intent();
                    intent.putExtra("room", roomProperty.getRoom());
                    intent.setAction("com.boer.delos.AddDevice");
                    LocalBroadcastManager.getInstance(RoomAddActivity.this).sendBroadcast(intent);
                    finish();
                }
                else{
                    Intent intent = getIntent();
                    intent.putExtra("room", roomProperty.getRoom());
                    intent.putExtra("position", pos);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();

            }
        });


    }


    private void getDevices() {

        toastUtils.showProgress("");
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {

                Log.v("gl", "getDevices==" + Json);

                toastUtils.dismiss();

                try {
                    if (!Constant.IS_DEVICE_STATUS_UPDATE) {
                        return;
                    }
                    Json = StringUtil.deviceStateStringReplaceMap(Json);


                        DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
//                    //判断设备信息是否有变更
//                    String md5Value = MD5(Json);
//                    if (!StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
//                            && Constant.DEVICE_MD5_VALUE.equals(md5Value)
//                            && Constant.GATEWAY != null) {
//                        return;
//                    }
                    deviceRelates = result.getResponse();


                    initDevices();


                } catch (Exception e) {

                    Log.v("gl","e.getMessage()==="+e.getMessage().toString());
                }
            }

            @Override
            public void onFailed(String json) {

                Loger.v("toastUtils===" + toastUtils + "json==" + json);
                toastUtils.dismiss();
            }
        });
    }

    private void initDevices() {

        for (DeviceRelate deviceRelate : deviceRelates) {
            Log.v("gl", "初始化话getDismiss==" + deviceRelate.getDeviceProp().getDismiss());
            devices.add(deviceRelate.getDeviceProp());
        }


        //初始化状态
        for (Device de : devices) {
            de.setChecked(false);
            de.setUpdateStatus(2);
        }


        if (room != null)
            getRoomBindDevices(room);
        else
            roomDvicesPopAdapter.notifyDataSetChanged();


    }


}
