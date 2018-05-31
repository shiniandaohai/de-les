package com.boer.delos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.boer.delos.R;
import com.boer.delos.activity.scene.AddBatchScanDeviceActivity;
import com.boer.delos.activity.scene.DeviceHomeActivity;
import com.boer.delos.adapter.recycleview.RecycleViewAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.SpaceItemDecoration;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:功能分类
 * @CreateDate: 2017/3/31 0031 09:39
 * @Modify:
 * @ModifyDate:
 */


public class CommomFunctionFragment extends InterfaceFragment
        implements IObjectInterface<List<DeviceRelate>> {

    @Bind(R.id.ll_device_nor)
    RelativeLayout mLlDeviceNor;
    @Bind(R.id.iv_1)
    ImageView mIv1;
    @Bind(R.id.recycleView)
    RecyclerView mRecycleView;

    private List<Map<String, Object>> mListDatas;
    private View rootView;
    private boolean isVisible;

    private RecycleViewAdapter mRecycleViewAdapter;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init(inflater, container);
//        ButterKnife.bind(this, rootView);
        return rootView;
    }


    private void init(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(initLayout(), container, false);
        ButterKnife.bind(this, rootView);
        initView();
        initData();
        initAction();
    }

    protected int initLayout() {
        return R.layout.fragment_comment_function;
    }

    protected void initView() {
        mListDatas = new ArrayList<>();
        mRecycleViewAdapter = new RecycleViewAdapter(getActivity(), mListDatas);
        mRecycleViewAdapter.setListener(this);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        mRecycleView.setLayoutManager(manager);
        mRecycleView.setAdapter(mRecycleViewAdapter);
        mRecycleView.addItemDecoration(new SpaceItemDecoration(10));
    }

    protected void initData() {
//        getDeviceStatusInfo();
        ((MainFragment) getParentFragment()).addSimpleListener(this);
    }

    protected void initAction() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        ((MainFragment) getParentFragment()).deleteObserver(this);
    }

    @OnClick(R.id.ll_device_nor)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_device_nor:
                startActivity(new Intent(getActivity(), AddBatchScanDeviceActivity.class));
                break;
        }
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


                    Constant.DEVICE_RELATE.clear();
                    Constant.DEVICE_RELATE.addAll(mDeviceRelate);
                    dealwithData(mDeviceRelate, Json);

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

    private void dealwithData(List<DeviceRelate> mDevicRelate, String json) {
        //判断设备信息是否有变更
      /*  String md5Value = MD5(json);
        if (mListDatas.size() != 0 &&
                !StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
                && Constant.DEVICE_MD5_VALUE.equals(md5Value)
                && Constant.GATEWAY != null) {
            return;
        }
        Constant.DEVICE_RELATE.clear();
        Constant.DEVICE_RELATE.addAll(mDevicRelate);*/
        mListDatas.clear();
        List<Map<String, Object>> mapList = Constant.combinDeviceList2(Constant.DEVICE_RELATE);
        if (mapList != null) {
            mListDatas.addAll(mapList);
        }
//        /*编辑*/
//        Map<String, Object> map = new ArrayMap<>();
//        map.put("name", "-99");
//        mListDatas.add(map);
        if (Constant.DEVICE_RELATE.size() != 0) {
            mLlDeviceNor.setVisibility(View.GONE);
            mRecycleView.setVisibility(View.VISIBLE);
            mRecycleViewAdapter.setList(mapList);
        }
        else{
            mLlDeviceNor.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClickListenerOK(List<DeviceRelate> list, int pos, String tag) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) list);
        bundle.putString("key", tag);
        if (list.size() == 0) {
            return;
        }
        Intent intent = new Intent(getActivity(), DeviceHomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //刷新数据
    @Override
    public void clickListener(String tag) {
        Loger.d(" 刷新数据2 ");
        dealwithData(Constant.DEVICE_RELATE, null);
    }


    private boolean hasCommonDevice() {
//        List<DeviceRelate> list = new ArrayList<>();
//        if (Constant.DEVICE_RELATE != null) {
//            list.addAll(Constant.DEVICE_RELATE);
//        }
//        if (list == null || list.size() == 0) {
//            return false;
//        }
//        List<DeviceRelate> listDevice = new ArrayList<>();
//        for (DeviceRelate deviceRelate : list) {
//            if (deviceRelate == null || deviceRelate.getDeviceProp() == null) {
//                continue;
//            }
//            Device d = deviceRelate.getDeviceProp();
//            if (!TextUtils.isEmpty(d.getFavorite()) && d.getFavorite().equals("1")) {
//                listDevice.add(deviceRelate);
//            }
//
//        }
//        return listDevice.size()>0;

        return Constant.DEVICE_RELATE.size()>0;
    }

}
