package com.boer.delos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.boer.delos.R;
import com.boer.delos.activity.scene.CommonDeviceActivity;
import com.boer.delos.activity.scene.devicecontrol.CircadianLightControlActivity;
import com.boer.delos.activity.scene.devicecontrol.CurtainControlActivity;
import com.boer.delos.activity.scene.devicecontrol.LightControlActivity;
import com.boer.delos.activity.scene.devicecontrol.LightingManualControlActivity;
import com.boer.delos.activity.scene.devicecontrol.SensorControlActivity;
import com.boer.delos.activity.scene.devicecontrol.SocketControlActivity;
import com.boer.delos.activity.scene.devicecontrol.WaterCleanActivity;
import com.boer.delos.activity.scene.devicecontrol.WaterFloorCleanActivity;
import com.boer.delos.activity.scene.devicecontrol.airclean.AirCleanActivity;
import com.boer.delos.adapter.recycleview.CommonDeviceRvAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 设备
 * @CreateDate: 2017/3/31 0031 09:39
 * @Modify:
 * @ModifyDate:
 */


public class CommomDeviceFragment extends InterfaceFragment
        implements IObjectInterface<DeviceRelate> {

    @Bind(R.id.ll_device_nor)
    RelativeLayout mLlDeviceNor;
    @Bind(R.id.recycler_devices)
    RecyclerView mRecycleView;

    private List<DeviceRelate> mDeviceRelates;
    private View rootView;
    private CommonDeviceRvAdapter mRvAdapter;


    public static CommomDeviceFragment newInstance(List<DeviceRelate> deviceRelate) {

        Bundle args = new Bundle();
        args.putSerializable("device", (Serializable) deviceRelate);
        CommomDeviceFragment fragment = new CommomDeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CommomDeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_commen_device, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        initData();
        initListener();
        return rootView;
    }


    protected void initView() {
        Loger.d("CommonDeviceFragment initView()");
        if (Constant.DEVICE_RELATE == null || Constant.DEVICE_RELATE.size() == 0) {
            mLlDeviceNor.setVisibility(View.VISIBLE);
        }

    }

    protected void initData() {
        mDeviceRelates = new ArrayList<>();

        if (Constant.DEVICE_RELATE != null) {
            mDeviceRelates.addAll(Constant.DEVICE_RELATE);
            mDeviceRelates.add(addDevice());
        }
        if (filterCommonDevice(mDeviceRelates).size() != 0) {
            mLlDeviceNor.setVisibility(View.GONE);
        }
        mRvAdapter = new CommonDeviceRvAdapter(getActivity(), filterCommonDevice(mDeviceRelates));
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        mRecycleView.setLayoutManager(manager);
        mRecycleView.setAdapter(mRvAdapter);

//        getDeviceStatusInfo();
        ((MainFragment) getParentFragment()).addSimpleListener(this);

    }

    //刷新数据
    @Override
    public void clickListener(String tag) {
        Loger.d(" 刷新数据1 ");
        if (filterCommonDevice(mDeviceRelates).size() != 0) {
            mLlDeviceNor.setVisibility(View.GONE);
        }
        dealWithData(Constant.DEVICE_RELATE, null);
    }

    /**
     * 添加一个设备 -99
     */
    private DeviceRelate addDevice() {
        DeviceRelate deviceRelate = new DeviceRelate();
        Device device = new Device();
        device.setAddr("-99");
        device.setType("-99");
        device.setFavorite("1");
        deviceRelate.setDeviceProp(device);
        return deviceRelate;
    }

    private void initListener() {
        mRvAdapter.setListener(this);
    }

    @OnClick({R.id.ll_device_nor})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_device_nor: //跳转添加设备界面, 有设备隐藏

                startActivity(new Intent(getActivity(), CommonDeviceActivity.class));
                break;
        }
    }

    private void dealWithData(List<DeviceRelate> list, String json) {
      /*  //判断设备信息是否有变更
        String md5Value = MD5(json);
        if (list.size() != 0 &&
                !StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
                && Constant.DEVICE_MD5_VALUE.equals(md5Value)
                && Constant.GATEWAY != null) {
            return;
        }
        Constant.DEVICE_RELATE.clear();
        Constant.DEVICE_RELATE.addAll(list);
*/
        mDeviceRelates.clear();


        mDeviceRelates.addAll(filterCommonDevice(list));
        mDeviceRelates.add(addDevice());

        if (mDeviceRelates.size() != 1) {
            mLlDeviceNor.setVisibility(View.GONE);
            mRecycleView.setVisibility(View.VISIBLE);
        } else {
            mLlDeviceNor.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.GONE);
        }

        mRvAdapter.setList(mDeviceRelates);

    }

    /**
     * 过滤常用设备
     *
     * @param list
     */
    private List<DeviceRelate> filterCommonDevice(List<DeviceRelate> list) {
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        List<DeviceRelate> listDevice = new ArrayList<>();
        for (DeviceRelate deviceRelate : list) {
            if (deviceRelate == null || deviceRelate.getDeviceProp() == null) {
                continue;
            }
            Device d = deviceRelate.getDeviceProp();
            if (!TextUtils.isEmpty(d.getFavorite()) && d.getFavorite().equals("1")) {
                listDevice.add(deviceRelate);
            }

        }


        return listDevice;
    }

    @Override
    public void onPause() {
        super.onPause();
        Loger.d("哈哈 1" + Constant.DEVICE_RELATE.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        ((MainFragment) getParentFragment()).deleteObserver(this);
    }


    @Override
    public void onClickListenerOK(DeviceRelate deviceRelate, int pos, String tag) {
        if (deviceRelate.getDeviceProp().getAddr().equals("-99")) {
            startActivity(new Intent(getActivity(), CommonDeviceActivity.class));
        } else {
            //设置房间
            Bundle bundle = new Bundle();
            bundle.putSerializable("device", deviceRelate);
            String type = deviceRelate.getDeviceProp().getType()
                    + (TextUtils.isEmpty(deviceRelate.getDeviceProp().getBrand())
                    ? ""
                    : deviceRelate.getDeviceProp().getBrand());
            Intent intent = ConstantDeviceType.startActivityByType(getActivity(), type);
            if (intent != null) {
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        }
    }

    private void startActivityByType(String type, Bundle bundle) {
        Intent intent = null;
        if (type.contains("LightAdjust")) {
            intent = new Intent(getActivity(), LightingManualControlActivity.class);
        } else if (type.contains("CircadianLight")) {
            intent = new Intent(getActivity(), CircadianLightControlActivity.class);
        } else if (type.contains("Light")) {
            intent = new Intent(getActivity(), LightControlActivity.class);
        } else {
            switch (type) {
                case "Socket":
                    intent = new Intent(getActivity(), SocketControlActivity.class);
                    break;
                case "Curtain":
                    intent = new Intent(getActivity(), CurtainControlActivity.class);
                    break;
                case "Exist":
                case "Fall":
                case "Water":
                case "Sov":
                case "SOS":
                case "Gsm":
                    intent = new Intent(getActivity(), SensorControlActivity.class);
                    break;
                case "TableWaterFilter":
                    intent = new Intent(getActivity(), WaterCleanActivity.class);
                    break;
                case "FloorWaterFilter":
                    intent = new Intent(getActivity(), WaterFloorCleanActivity.class);
                    break;
                case "AirFilter":
                    intent = new Intent(getActivity(), AirCleanActivity.class);
                    break;
                default:
                    ToastHelper.showShortMsg("开发中。。。");
                    return;
            }
        }

    }

}
