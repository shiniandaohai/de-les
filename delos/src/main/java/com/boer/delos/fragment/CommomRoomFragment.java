package com.boer.delos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.boer.delos.R;
import com.boer.delos.activity.scene.DeviceHomeActivity;
import com.boer.delos.activity.scene.RoomManageActivity;
import com.boer.delos.activity.scene.SceneDisplayListeningActivity;
import com.boer.delos.adapter.recycleview.CommonDeviceRoomRvAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.GatewayResult;
import com.boer.delos.model.Room;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.boer.delos.constant.Constant.GATEWAY;
import static com.boer.delos.utils.sign.MD5Utils.MD5;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 常用房间
 * @CreateDate: 2017/3/31 0031 09:39
 * @Modify:
 * @ModifyDate:
 */


public class CommomRoomFragment extends InterfaceFragment {

    @Bind(R.id.iv_1)
    ImageView mIv1;
    @Bind(R.id.ll_device_nor)
    RelativeLayout mLlDeviceNor;
    @Bind(R.id.recycler)
    RecyclerView mRecycler;

    private CommonDeviceRoomRvAdapter mRoomRvAdapter;
    private List<DeviceRelate> mRelateList;
    private ArrayMap<String, Room> mapRoomId2Type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment_room, container, false);
        ButterKnife.bind(this, view);
        intView();
        initData();
        initListener();
        return view;
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        getGatewayInfo();

    }

    private void intView() {
        if (Constant.GATEWAY != null && Constant.GATEWAY.getRoom() != null && Constant.GATEWAY.getRoom().size() > 0) {
            mRecycler.setVisibility(View.VISIBLE);
            mLlDeviceNor.setVisibility(View.GONE);
        } else {
            mLlDeviceNor.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }
        ((MainFragment) getParentFragment()).addSimpleListener(this);

    }

    private void initData() {

        mRelateList = new ArrayList<>();
        if (Constant.DEVICE_RELATE != null) {
            mRelateList.addAll(Constant.DEVICE_RELATE);
        }
        mRoomRvAdapter = new CommonDeviceRoomRvAdapter(getActivity(), getBindDevices(mRelateList));
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        mRecycler.setLayoutManager(manager);

        mRecycler.setAdapter(mRoomRvAdapter);

    }

    private void initListener() {
        mRoomRvAdapter.setListener(new IObjectInterface<List<DeviceRelate>>() {
            @Override
            public void onClickListenerOK(List<DeviceRelate> list, int pos, String tag) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable) list);
                bundle.putString("key", tag);
                bundle.putString("roomId", mRoomRvAdapter.getmKeyList().get(pos).getRoomId());
                Intent intent = new Intent(getActivity(), DeviceHomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
    }


    @OnClick({R.id.iv_1, R.id.ll_device_nor})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_1:

            case R.id.ll_device_nor:
                startActivity(new Intent(getActivity(), RoomManageActivity.class));

                break;
        }
    }

    private Map<Room, List<DeviceRelate>> getBindDevices(List<DeviceRelate> list) {
        Map<Room, List<DeviceRelate>> mapList = new LinkedHashMap<>();
//        mapRoomId2Type = new ArrayMap<>();

        try {
            if (Constant.GATEWAY == null) {
                return mapList;
            }

            List<Room> rooms = Constant.GATEWAY.getRoom();
            if (rooms == null) {
                return Collections.emptyMap();
            }
            for (DeviceRelate deviceRelate : list) {
                if (deviceRelate == null) continue;
                if (deviceRelate.getDeviceProp() == null) continue;
                if (deviceRelate.getDeviceProp().getType() == null) continue;

                Device device = deviceRelate.getDeviceProp();
                String roomId = device.getRoomId();
                for (Room room : rooms) {
                    if (StringUtil.isEmpty(roomId)) {
                        continue;
                    }
                    if (room == null) continue;
                    if (TextUtils.isEmpty(room.getRoomId())) continue;
                    if (TextUtils.isEmpty(room.getType())) continue;

                    if (room.getRoomId().equals(roomId)) {
                        List<DeviceRelate> l = mapList.get(room);
                        if (l == null) {
                            l = new ArrayList<>();
                        }
                        if (TextUtils.isEmpty(device.getRoomname())) {
                            device.setRoomname(room.getName());
                            deviceRelate.setDeviceProp(device);
                        }
                        l.add(deviceRelate);
                        mapList.put(room, l);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //整理map
        if (GATEWAY != null) {
            List<Room> roomList = Constant.GATEWAY.getRoom();
            if (roomList == null) {
                return Collections.emptyMap();
            }

//            Set<Room> keySet2 = mapList.keySet();
//            for (Room s : keySet2) {
//                Loger.d(" sunzhibin 3 " + "name: " + s.getName() + " id: " + s.getRoomId());
//            }
            Map<Room, List<DeviceRelate>> tempMapList = new LinkedHashMap<>();

            for (Room room : roomList) {
                if (mapList.containsKey(room)) {
                    tempMapList.put(room, mapList.get(room));
                } else {
                    tempMapList.put(room, new ArrayList<DeviceRelate>());
                }
            }

            return tempMapList;
        }
        return mapList;
    }

    /**
     * 取得主机信息
     */
    public void getGatewayInfo() {
        if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
            return;
        }
        GatewayController.getInstance().getGatewayProperties(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    GatewayResult result = new Gson().fromJson(Json, GatewayResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    String md5Value = MD5(Json);
                    if (md5Value == null) {
                        return;
                    }
                    getDeviceStatusInfo();
//                    Constant.IS_GATEWAY_ONLINE = true; // add by sunzhibin
//                    if (!StringUtil.isEmpty(Constant.GATEWAY_MD5_VALUE)
//                            && Constant.GATEWAY_MD5_VALUE.equals(md5Value)) {
//                        return;
//                    }
                    Constant.GATEWAY = result.getResponse();
//
//                    if (!BaseApplication.isForeground) {
//                        return;
//                    }
                    Constant.GATEWAY_MD5_VALUE = md5Value;
                } catch (Exception e) {
                    L.e("getGatewayInfo:" + e);
                }
            }

            @Override
            public void onFailed(String json) {
//
            }
        });
    }

    private void getDeviceStatusInfo() {
        DeviceController.getInstance().queryDeviceRelateInfo(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Loger.d("哈哈 " + Json);
                    Json = StringUtil.deviceStateStringReplaceMap(Json);
                    DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    List<DeviceRelate> mDeviceRelate = new ArrayList<>();
                    mDeviceRelate.addAll(result.getResponse());

                    dealWithData(mDeviceRelate, Json);

                } catch (Exception e) {
                    L.e("queryDeviceRelateInfo:" + e);
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.e("getDeviceStatusInfo()" + json);
            }
        });
    }

    private void dealWithData(List<DeviceRelate> list, String json) {
       /* //判断设备信息是否有变更
        String md5Value = MD5(json);
        if (list.size() != 0 &&
                !StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
                && Constant.DEVICE_MD5_VALUE.equals(md5Value)
                && Constant.GATEWAY != null) {
            return;
        }
        Constant.DEVICE_RELATE.clear();
        Constant.DEVICE_RELATE.addAll(list);*/
        mRelateList.clear();
        mRelateList.addAll(Constant.DEVICE_RELATE);
        if (Constant.GATEWAY != null && Constant.GATEWAY.getRoom() != null && Constant.GATEWAY.getRoom().size() > 0) {
            mLlDeviceNor.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        } else {
            mLlDeviceNor.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }
        mRoomRvAdapter.setList(getBindDevices(mRelateList));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        ((MainFragment) getParentFragment()).deleteObserver(this);

    }

    //刷新数据
    @Override
    public void clickListener(String tag) {
        Loger.d(" 刷新数据3 ");
        if (Constant.GATEWAY != null && Constant.GATEWAY.getRoom() != null && Constant.GATEWAY.getRoom().size() > 0) {
            mLlDeviceNor.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        } else {
            mLlDeviceNor.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }
        dealWithData(Constant.DEVICE_RELATE, null);

    }


}
