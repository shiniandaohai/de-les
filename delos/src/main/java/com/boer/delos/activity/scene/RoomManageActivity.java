package com.boer.delos.activity.scene;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.boer.delos.R;
import com.boer.delos.adapter.SceneManageAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Area;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Gateway;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomProperty;
import com.boer.delos.model.RoomResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.AddScenePopUpWindow;
import com.boer.delos.view.popupWindow.DeleteScenePopUpWindow;
import com.boer.delos.view.popupWindow.SceneEditPopUpWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;

import static com.boer.delos.utils.sign.MD5Utils.MD5;

/**
 * Created by gaolong on 2017/4/1.
 */
public class RoomManageActivity extends CommonBaseActivity {
    @Bind(R.id.gvSceneManage)
    GridView gvSceneManage;


    public List<Room> datas = new ArrayList<>();
    private SceneManageAdapter adapter;
    private Room room;
    private String customName;
    private AddScenePopUpWindow mAddScenePopUp;
    private DeleteScenePopUpWindow mDeleteScenePopUpWindow;
    private boolean isEdit;
    private SceneEditPopUpWindow mEditPopUpWindow;
    private int REQ_EDIT_ROOM = 1000;
    private int REQ_ADD_ROOM = 1001;


    private Gateway gateway;

    private String entrance;

    private Room selectedRoom;
    @Override
    protected int initLayout() {
        return R.layout.acitivity_room_manage;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(R.string.room_manage);
        tlTitleLayout.setRightText(R.string.save);
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        entrance = intent.getStringExtra("entrance");

        getRooms();


        adapter = new SceneManageAdapter(RoomManageActivity.this, datas,
                new SceneManageAdapter.OnClickListener() {
                    @Override
                    public void click(final Room delRoom) {
//
                        final String title = "提示";
                        final String message = "删除房间时会解绑该房间中所有设备";
                        mDeleteScenePopUpWindow = new DeleteScenePopUpWindow(RoomManageActivity.this, title, message,
                                new DeleteScenePopUpWindow.ClickResultListener() {
                                    @Override
                                    public void ClickResult(int tag) {

                                        deleteRoom(delRoom);
                                        mDeleteScenePopUpWindow.dismiss();
                                    }
                                });
                        mDeleteScenePopUpWindow.showAtLocation(llayoutContent, Gravity.CENTER, 0, 0);
                    }
                });
        gvSceneManage.setAdapter(adapter);


        if (!TextUtils.isEmpty(entrance)) {
            if (entrance.equals("activity.scene.AddDeviceListeningActivity")||entrance.equals("activity.scene.AddBatchScanResultActivity")) {
                for(Room room:datas){
                    adapter.getIsSelected().add(false);
                }
            }
            else{
                for(Room room:datas){
                    adapter.getIsSelected().add(false);
                }
            }
        }
        else{
            for(Room room:datas){
                adapter.getIsSelected().add(false);
            }
        }

    }

    @Override
    protected void initAction() {

        gvSceneManage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                if (!NetUtil.checkNet(getApplicationContext())) {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_error_net));
                }
                if (isEdit) {

                } else {
                    Room room = datas.get(position);
                    String roomType = room.getType();
                    final int checkPosition = Constant.setPosition(roomType);
                    if (position != datas.size() - 1) {
                        if (!TextUtils.isEmpty(entrance)) {
                            if (entrance.equals("activity.scene.AddDeviceListeningActivity")||entrance.equals("activity.scene.AddBatchScanResultActivity")) {
//                                Intent intent = getIntent();
//                                intent.putExtra("room", room);
//                                Log.v("gl","room.id=="+room.getRoomId());
//                                setResult(RESULT_OK, intent);
//                                finish();
                                selectedRoom=room;

                                adapter.getIsSelected().clear();
                                for(int i=0;i<datas.size();i++){
                                    if(i==position){
                                        adapter.getIsSelected().add(true);
                                    }
                                    else{
                                        adapter.getIsSelected().add(false);
                                    }
                                }
                                adapter.notifyDataSetChanged();

                            }
                            else{
                                voidEditRoom(position);
                            }
                        }
                        else{
                            voidEditRoom(position);
                        }
                    } else {
//                        mAddScenePopUp = new AddScenePopUpWindow(RoomManageActivity.this, new AddScenePopUpWindow.ClickResultListener() {
//                            @Override
//                            public void ClickResult(int tag) {
//                                if (tag == 2) {
//                                    customName = mAddScenePopUp.getName();
//                                    addRoom(customName);
////                                mAddScenePopUp.dismiss();
//                                }
//                            }
//                        });
//                        mAddScenePopUp.showAtLocation(view, Gravity.CENTER, 0, 0);



                        if (!TextUtils.isEmpty(entrance)) {
                            if (entrance.equals("activity.scene.AddDeviceListeningActivity")||entrance.equals("activity.scene.AddBatchScanResultActivity")) {
                                startActivity(new Intent(RoomManageActivity.this, RoomAddActivity.class).
                                        putExtra("addRoomType",2));
                                finish();
                            }
                            else{
                                startActivityForResult(new Intent(RoomManageActivity.this, RoomAddActivity.class).
                                        putExtra("addRoomType",0), REQ_ADD_ROOM);
                            }
                        }
                        else{
                            startActivityForResult(new Intent(RoomManageActivity.this, RoomAddActivity.class).
                                    putExtra("addRoomType",0), REQ_ADD_ROOM);
                        }
                    }
                }
            }
        });

    }


    private void voidEditRoom(int position){
        Room room = datas.get(position);
        String roomType = room.getType();
        int checkPosition = Constant.setPosition(roomType);
        Loger.v("position=" + position);
        Loger.v("checkPosition=" + checkPosition);
        Intent intent = new Intent(RoomManageActivity.this, RoomAddActivity.class);
        intent.putExtra("room", room);
        intent.putExtra("checkPosition", checkPosition);
        intent.putExtra("position", position);
        intent.putExtra("addRoomType",1);

        startActivityForResult(intent, REQ_EDIT_ROOM);
    }

    /**
     * 设定房间信息
     */
    private void setRoomInfo() {
        isEdit = false;
        datas.clear();
        if (gateway != null) {
            List<Room> list = gateway.getRoom();
            if (list != null) {
                if (list.size() > 0) {
                    datas.addAll(list);
                }
            }
        }
        normalMode();
    }

    /**
     * 普通模式
     */
    private void normalMode() {
        if (!TextUtils.isEmpty(entrance)) {
            if (entrance.equals("activity.scene.AddDeviceListeningActivity")||entrance.equals("activity.scene.AddBatchScanResultActivity")) {
                tlTitleLayout.setRightText("保存");
                for (Room room : datas) {
                    room.setEdit(false);
                }
                if(datas.size()>0){
                    Room tempRoom=datas.get(datas.size()-1);
                    if(!tempRoom.getType().equals("")){
                        Room addRoom = new Room("");
                        addRoom.setType("");
                        datas.add(addRoom);
                    }
                }
                else{
                    Room addRoom = new Room("");
                    addRoom.setType("");
                    datas.add(addRoom);
                }
                adapter.notifyDataSetChanged();
            }
            else{
                isEdit = false;
                tlTitleLayout.setRightText("编辑");
                for (Room room : datas) {
                    room.setEdit(false);
                }

                if(datas.size()>0){
                    Room tempRoom=datas.get(datas.size()-1);
                    if(!tempRoom.getType().equals("")){
                        Room addRoom = new Room("");
                        addRoom.setType("");
                        datas.add(addRoom);
                    }
                }
                else{
                    Room addRoom = new Room("");
                    addRoom.setType("");
                    datas.add(addRoom);
                }
                adapter.notifyDataSetChanged();
            }
        }
        else{
            isEdit = false;
            tlTitleLayout.setRightText("编辑");
            for (Room room : datas) {
                room.setEdit(false);
            }

            if(datas.size()>0){
                Room tempRoom=datas.get(datas.size()-1);
                if(!tempRoom.getType().equals("")){
                    Room addRoom = new Room("");
                    addRoom.setType("");
                    datas.add(addRoom);
                }
            }
            else{
                Room addRoom = new Room("");
                addRoom.setType("");
                datas.add(addRoom);
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 编辑模式
     */
    private void editMode() {
        if (datas.size() == 0) {
            return;
        }


        isEdit = true;
        tlTitleLayout.setRightText("完成");
        for (Room room : datas) {
            room.setEdit(true);
        }
        //移除最后一个
        datas.remove(datas.size() - 1);
        adapter.notifyDataSetChanged();
    }


    /**
     * 删除房间
     *
     * @param delRoom
     */
    private void deleteRoom(final Room delRoom) {
        toastUtils.showProgress("");
        Log.i("gwq","size1="+Constant.GATEWAY.getRoom().size());
        RoomController.getInstance().removeRoom(this, 0, delRoom.getRoomId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {

                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }

                    toastUtils.dismiss();

                    for (Iterator<Room> roomIterator = datas.iterator(); roomIterator.hasNext(); ) {
                        Room room = roomIterator.next();
                        if (room.getRoomId().equals(delRoom.getRoomId())) {
                            roomIterator.remove();
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.i("gwq","size="+Constant.GATEWAY.getRoom().size());

                    Loger.v("datas==" + Constant.GATEWAY.getRoom().size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
            }
        });
    }

    @Override
    public void rightViewClick() {
        super.rightViewClick();

        if (!TextUtils.isEmpty(entrance)) {
            if (entrance.equals("activity.scene.AddDeviceListeningActivity")||entrance.equals("activity.scene.AddBatchScanResultActivity")) {
                if(selectedRoom==null){
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra("room", selectedRoom);
                setResult(RESULT_OK, intent);
                finish();
            }
            else{
                if (!isEdit) {
                    editMode();
                } else {
                    normalMode();
                }
            }
        }
        else{
            if (!isEdit) {
                editMode();
            } else {
                normalMode();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            getRooms();
        }
    }


    public void getRooms() {
        toastUtils.showProgress("");
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            return;
        }
        GatewayController.getInstance().getGatewayProperties(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                try {
                    GatewayResult result = new Gson().fromJson(Json, GatewayResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String md5Value = MD5(Json);
                    if (md5Value == null) {
                        return;
                    }

//                    Constant.IS_GATEWAY_ONLINE = true;
//                    if (!StringUtil.isEmpty(Constant.GATEWAY_MD5_VALUE)
//                            && Constant.GATEWAY_MD5_VALUE.equals(md5Value)) {
//                        return;
//                    }


                    gateway = result.getResponse();

                    Constant.GATEWAY = gateway;

                    setRoomInfo();


                } catch (Exception e) {
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();
            }
        });
    }


}
