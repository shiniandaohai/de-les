package com.boer.delos.activity.scene;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.SceneManageAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Area;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Gateway;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.AddScenePopUpWindow;
import com.boer.delos.view.popupWindow.DeleteScenePopUpWindow;
import com.boer.delos.view.popupWindow.SceneEditPopUpWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieQingTing
 * @Description: 房间管理界面
 * create at 2016/4/12 9:46
 */
public class SceneManageListeningActivity extends BaseListeningActivity {
    private GridView gvSceneManage;
    public List<Room> datas = new ArrayList<>();
    private View view;
    private SceneManageAdapter adapter;
    private Room room;
    private String customName;
    private AddScenePopUpWindow mAddScenePopUp;
    private Handler handler = new Handler();
    public int tag;
    public static SceneManageListeningActivity instance = null;
    private DeleteScenePopUpWindow mDeleteScenePopUpWindow;
    private boolean isEdit;
    private SceneEditPopUpWindow mEditPopUpWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_scence_manage, null);
        setContentView(view);

        tag = getIntent().getIntExtra("tag", -1);

        initView();
        initData();
        initListener();
    }

    @Override
    protected void gatewayUpdate() {
        if (isEdit) {
            return;
        }
        setRoomInfo();
    }

    private void initView() {

        initTopBar(R.string.room_manage, R.string.edit_button, true, false);
        tvRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isEdit) {
                    editMode();
                } else {
                    normalMode();
                }
            }
        });
        gvSceneManage = (GridView) findViewById(R.id.gvSceneManage);
    }

    /**
     * 普通模式
     */
    private void normalMode() {
        isEdit = false;
        tvRight.setText("编辑");
        for (Room room : datas) {
            room.setEdit(false);
        }
        //最后 "+" 房间
        Room addRoom = new Room("");
        addRoom.setType("");
        datas.add(addRoom);
        adapter.notifyDataSetChanged();
    }

    /**
     * 编辑模式
     */
    private void editMode() {
        isEdit = true;
        tvRight.setText("完成");
        for (Room room : datas) {
            room.setEdit(true);
        }
        //移除最后一个
        datas.remove(datas.size() - 1);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (tag == 1) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAddScenePopUp = new AddScenePopUpWindow(SceneManageListeningActivity.this, new AddScenePopUpWindow.ClickResultListener() {
                        @Override
                        public void ClickResult(int tag) {
                            customName = mAddScenePopUp.getName();
                            addRoom(customName);
//                    mAddScenePopUp.dismiss();
//                    SceneManageListeningActivity.this.finish();
                        }
                    });
                    mAddScenePopUp.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
            }, 200);
        }
//        queryRecentData();
//        getGatewayInfo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isEdit) {
            setRoomInfo();
        }
    }

    public Room getRoom() {
        return room;
    }

    /**
     * 添加房间
     * 新建时没有roomId
     */
    private void addRoom(String customName) {
        if (StringUtil.isEmpty(customName)) {
            BaseApplication.showToast("请输入房间名称");
            return;
        }
        if (customName.length() > 4) {
            BaseApplication.showToast("长度不能大于4");
            return;
        }
        if (mAddScenePopUp.getCheckPosition() == -1) {
            BaseApplication.showToast("请选择房间类型");
            return;
        }
        //new Room
        room = new Room(customName);
        room.setType(getResources().getString(mAddScenePopUp.getSceneManage().getType()));
        List<Area> areas = new ArrayList();
        Area area = new Area();
        area.setAreaId("1");
        area.setName(getString(R.string.new_area));
        areas.add(area);
        room.setAreas(areas);

        mAddScenePopUp.dismiss();
        toastUtils.showProgress("正在提交");
        RoomController.getInstance().updateRoom(this, room, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {

                    RoomResult result = new Gson().fromJson(Json, RoomResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {

                        if (datas.size() == 0) {
                            return;
                        }
                        datas.remove(datas.size() - 1);
                        datas.add(result.getResponse());
                        normalMode();
                        toastUtils.showSuccessWithStatus("添加成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils!=null)
                toastUtils.showErrorWithStatus(json);
            }
        });

    }


    private void initData() {
        adapter = new SceneManageAdapter(SceneManageListeningActivity.this, datas,
                new SceneManageAdapter.OnClickListener() {
                    @Override
                    public void click(final Room room) {
                        if (datas.size() == 1) {
                            toastUtils.showErrorWithStatus("最后一个房间不允许删除");
                            return;
                        }
                        final String title = getString(R.string.scene_edit_delete_title);
                        final String message = getString(R.string.scene_edit_hint);
                        mDeleteScenePopUpWindow = new DeleteScenePopUpWindow(SceneManageListeningActivity.this, title, message,
                                new DeleteScenePopUpWindow.ClickResultListener() {
                                    @Override
                                    public void ClickResult(int tag) {
                                        if (datas.size() == 1) {
                                            toastUtils.showErrorWithStatus("最后一个房间不允许删除");
                                            return;
                                        }
                                        datas.remove(room);
                                        adapter.notifyDataSetChanged();
                                        deleteRoom(room);
                                        mDeleteScenePopUpWindow.dismiss();
                                    }
                                });
                        mDeleteScenePopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    }
                });
        gvSceneManage.setAdapter(adapter);
        setRoomInfo();

    }

    /**
     * 设定房间信息
     */
    private void setRoomInfo() {
        isEdit = false;
        tvRight.setText("编辑");
        Gateway gateway = Constant.GATEWAY;
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
     * GridView条目点击事件
     */
    private void initListener() {
        gvSceneManage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                if (!NetUtil.checkNet(getApplicationContext())) {
                    toastUtils.showInfoWithStatus(getString(R.string.toast_error_net));
                }
                if (isEdit) {
                    final Room room = datas.get(position);
                    if (mEditPopUpWindow != null && mEditPopUpWindow.isShowing()) {
                        mEditPopUpWindow.dismiss();
                    }
                    mEditPopUpWindow = new SceneEditPopUpWindow(SceneManageListeningActivity.this, room,
                            new SceneEditPopUpWindow.ClickResultListener() {
                                @Override
                                public void ClickResult(int tag) {
                                    getRoomInfo(room);
                                }
                            });
                    mEditPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                } else {
                    Room room = datas.get(position);
                    String roomType = room.getType();
                    final int checkPosition = Constant.setPosition(roomType);
                    if (position != datas.size() - 1) {
                        Intent intent = new Intent(SceneManageListeningActivity.this, FurnitureListListeningActivity.class);
                        intent.putExtra("room", room);
                        intent.putExtra("checkPosition", checkPosition);
                        startActivity(intent);
                    } else {
                        mAddScenePopUp = new AddScenePopUpWindow(SceneManageListeningActivity.this, new AddScenePopUpWindow.ClickResultListener() {
                            @Override
                            public void ClickResult(int tag) {
                                if (tag == 2) {
                                    customName = mAddScenePopUp.getName();
                                    addRoom(customName);
//                                mAddScenePopUp.dismiss();
                                }
                            }
                        });
                        mAddScenePopUp.showAtLocation(view, Gravity.CENTER, 0, 0);

                    }
                }
            }
        });
    }

    /**
     * 删除房间
     *
     * @param room
     */
    private void deleteRoom(Room room) {
        RoomController.getInstance().removeRoom(this, 0, room.getRoomId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    setRoomInfo();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils!= null)
                toastUtils.showErrorWithStatus(json);
            }
        });
    }


    /**
     * 查询房间信息
     */
    private void getRoomInfo(Room room) {
        final String customName = mEditPopUpWindow.getcustomName();
        final String checkRoomType = mEditPopUpWindow.getCheckType();
        if (StringUtil.isEmpty(customName)) {
            BaseApplication.showToast("请输入房间名称");
            return;
        }

        mEditPopUpWindow.dismiss();
        if (customName.length() > 4) {
            ToastHelper.showShortMsg("房间名长度不能大于4");
            return;
        }
        if (StringUtil.isEmpty(checkRoomType)) {
            ToastHelper.showShortMsg("请选择房间类型");
            return;
        }
        //没做修改,不提交
        if (room.getType().equals(checkRoomType) && room.getName().equals(customName)) {
            return;
        }

        //本地修改
        room.setType(checkRoomType);
        room.setName(customName);
        RoomController.getInstance().roomShow(this, room.getRoomId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    RoomResult result = new Gson().fromJson(Json, RoomResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        Room tempRoom = result.getResponse();
                        tempRoom.setType(checkRoomType);
                        tempRoom.setName(customName);
                        updateRoom(tempRoom);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {
                if (toastUtils!=null)
                toastUtils.showErrorWithStatus(Json);
            }
        });

    }

    /**
     * 更新房间信息
     *
     * @param room
     */
    private void updateRoom(final Room room) {
        RoomController.getInstance().updateRoom(this, room, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        adapter.notifyDataSetChanged();
                        setRoomInfo();
                        toastUtils.showSuccessWithStatus("修改成功");
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

}
