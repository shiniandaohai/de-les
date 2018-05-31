package com.boer.delos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.DeviceHomeActivity;
import com.boer.delos.activity.scene.RoomManageActivity;
import com.boer.delos.adapter.ClassDeviceRoomAdapter;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.Room;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/1 0001 13:27
 * @Modify:
 * @ModifyDate:
 */


public class DeviceClassifyRoomFragment extends LazyFragment implements Observer {

    @Bind(R.id.tv_device_num)
    TextView mTvDeviceNum;
    @Bind(R.id.ll_all_device)
    LinearLayout mLlAllDevice;
    @Bind(R.id.iv_edit)
    ImageView mIvEdit;
    @Bind(R.id.ll_room_manager)
    LinearLayout mLlRoomManager;
    @Bind(R.id.gridView)
    GridView mGridView;

    private View rootView;
    private List<DeviceRelate> mDeviceRelates;
    private Map<Room, List<DeviceRelate>> mapDatas; //adapter 数据
    private ClassDeviceRoomAdapter mClassifyDeviceAdapter;
    private IObjectInterface mListener;
    private ISimpleInterfaceString mListenerRoomId;

    public IObjectInterface getListener() {
        return mListener;
    }

    public void setListener(IObjectInterface listener) {
        mListener = listener;
    }

    public void setmListenerRoomId(ISimpleInterfaceString mListenerRoomId) {
        this.mListenerRoomId = mListenerRoomId;
    }

    public static DeviceClassifyRoomFragment newInstance(List<DeviceRelate> deviceRelates) {

        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) deviceRelates);

        DeviceClassifyRoomFragment fragment = new DeviceClassifyRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_classify_room, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        initData();
        initListener();
        return rootView;
    }

    private void initView() {
//        mGridView = mPullToRefreshListView.getRefreshableView();
//        mGridView.setNumColumns(2);
//        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        mPullToRefreshListView.setRefreshing(true);

        ((DeviceHomeActivity) getActivity()).addListeners(this);
    }

    private void initData() {
        if (mDeviceRelates == null) mDeviceRelates = new ArrayList<>();
        List<DeviceRelate> list = (List<DeviceRelate>) getArguments().getSerializable("data");
        mDeviceRelates.addAll(list);
        mDeviceRelates.clear();
        mDeviceRelates.addAll(Constant.DEVICE_RELATE);

        mapDatas = getBindDevices(mDeviceRelates);
        mClassifyDeviceAdapter = new ClassDeviceRoomAdapter(getActivity(), mapDatas);
        mGridView.setAdapter(mClassifyDeviceAdapter);

    }

    private void initListener() {

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<DeviceRelate> mDeviceList = mClassifyDeviceAdapter.getItem(position);
                if (mListener != null) {
                    mListener.onClickListenerOK(mDeviceList, position,
                            mClassifyDeviceAdapter.getKeyList().get(position).getName());

                }
                if (mListenerRoomId != null) {
                    mListenerRoomId.clickListener(mClassifyDeviceAdapter.getKeyList().get(position).getRoomId());
                }
            }
        });
    }

    @OnClick({R.id.iv_edit, R.id.ll_room_manager, R.id.ll_all_device})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_edit:
            case R.id.ll_room_manager:
                permisionAccess();
                break;
            case R.id.ll_all_device:
                mDeviceRelates.clear();
                mDeviceRelates.addAll(Constant.DEVICE_RELATE);
                if (mListener != null) {
                    mListener.onClickListenerOK(mDeviceRelates, -1, getString(R.string.text_all_device));
                }
                break;
        }
    }

    private Map<Room, List<DeviceRelate>> getBindDevices(List<DeviceRelate> list) {
        Map<Room, List<DeviceRelate>> mapList = new ArrayMap<>();

        try {
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
                        break;
                    }
                    if (room == null) continue;
                    if (TextUtils.isEmpty(room.getRoomId())) continue;
                    if (TextUtils.isEmpty(room.getType())) continue;

                    if (room.getRoomId().equals(roomId)) {
                        List<DeviceRelate> l = mapList.get(room);
                        if (l == null) {
                            l = new ArrayList<>();
                        }
                        l.add(deviceRelate);
                        mapList.put(room, l);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Constant.GATEWAY != null && Constant.GATEWAY.getRoom() != null) {
            Map<Room, List<DeviceRelate>> tempMapList = new LinkedHashMap<>();
            for (Room room : Constant.GATEWAY.getRoom()) {
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

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void update(Observable o, Object arg) {
        Loger.d("我的错2");
        mapDatas.clear();
        mapDatas.putAll(getBindDevices(Constant.DEVICE_RELATE));
        mClassifyDeviceAdapter.setData(mapDatas);
    }

    private void permisionAccess(){
        if(Constant.LOGIN_USER.getName().equals("admin")){
            Intent intent = new Intent(getActivity(), RoomManageActivity.class);
            startActivityForResult(intent, 200);
            return;
        }
        FamilyManageController.getInstance().queryUserPermission(getActivity(), Constant.CURRENTHOSTID, Constant.USERID, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(json);
                    String ret=jsonObject.getString("ret");
                    if(!ret.equals("0")){
                        return;
                    }
                    String permision=jsonObject.getString("permissions");
                    int limitStatus=jsonObject.getInt("limitStatus");
                    String limitTime=jsonObject.getString("limitTime");

                    boolean isCanEnter=false;
                    if(permision.contains("房间管理")){
                        if(limitStatus==1){
                            long ms = Long.parseLong(limitTime) * 1000 - System.currentTimeMillis();
                            if(ms>0){
                                isCanEnter=true;
                            }
                            else{
                                isCanEnter=false;
                            }
                        }
                        else if(limitStatus==0||limitStatus==2){
                            isCanEnter=true;
                        }
                    }
                    else{
                        isCanEnter=false;
                    }
                    if(!isCanEnter){
                        ToastHelper.showShortMsg("请联系管理员获取权限！");
                    }
                    else{
                        Intent intent = new Intent(getActivity(), RoomManageActivity.class);
                        startActivityForResult(intent, 200);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                Log.d("NetGatePermision",json);
            }
        });
    }
}
