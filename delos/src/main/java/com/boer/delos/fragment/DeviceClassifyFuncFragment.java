package com.boer.delos.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.DeviceHomeActivity;
import com.boer.delos.adapter.ClassifyDeviceAdapter;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.boer.delos.constant.ConstantDeviceType.groupAllDeviceByType;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:按功能分类
 * @CreateDate: 2017/4/1 0001 13:26
 * @Modify:
 * @ModifyDate:
 */


public class DeviceClassifyFuncFragment extends LazyFragment implements Observer {
    @Bind(R.id.tv_device_num)
    TextView mTvDeviceNum;
    @Bind(R.id.pullToRefreshListView)
    PullToRefreshGridView mPullToRefreshListView;
    @Bind(R.id.tv_airclean)
    TextView tvAirclean;
    @Bind(R.id.tv_waterclean)
    TextView tvWaterclean;
    @Bind(R.id.tv_light_control)
    TextView tvLightControl;
    @Bind(R.id.tv_curtain_control)
    TextView tvCurtainControl;
    @Bind(R.id.tv_CAC_control)
    TextView tvCACControl;
    @Bind(R.id.tv_camera_control)
    TextView tvCameraControl;
    @Bind(R.id.tv_elec_control)
    TextView tvElecControl;
    @Bind(R.id.tv_general_control)
    TextView tvGeneralControl;
    @Bind(R.id.tv_air_check)
    TextView tvAirCheck;
    @Bind(R.id.tv_video_control)
    TextView tvVideoControl;
    @Bind(R.id.tv_alarm_control)
    TextView tvAlarmControl;
    @Bind(R.id.tv_protect_control)
    TextView tvProtectControl;
    @Bind(R.id.gridView)
    GridView gridView;

    private GridView mGridView;

    private View rootView;
    private List<DeviceRelate> mDeviceList;
    private ClassifyDeviceAdapter mFunctionAdapter;
    private ArrayMap<String, List<DeviceRelate>> mapDatas;
    private IObjectInterface mListener;
    private List<String> keyList;

    public static DeviceClassifyFuncFragment newInstance(List<DeviceRelate> deviceRelates) {

        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) deviceRelates);

        DeviceClassifyFuncFragment fragment = new DeviceClassifyFuncFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_classify_function, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        initData();
        initListener();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDeviceList == null) mDeviceList = new ArrayList<DeviceRelate>();

        mDeviceList.clear();
        mDeviceList.addAll(Constant.DEVICE_RELATE);
        mapDatas = ConstantDeviceType.groupAllDeviceByType(mDeviceList);
        if (mFunctionAdapter != null) {
            mFunctionAdapter.setData(mapDatas);
            mFunctionAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void lazyLoad() {
        Loger.d("哈哈");

    }

    private void initView() {
        mGridView = mPullToRefreshListView.getRefreshableView();
        mGridView.setNumColumns(2);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshListView.setRefreshing(true);

        ((DeviceHomeActivity) getActivity()).addListeners(this);
    }

    private void initData() {
        if (mDeviceList == null) mDeviceList = new ArrayList<>();
        List<DeviceRelate> list = (List<DeviceRelate>) getArguments().getSerializable("data");
        mDeviceList.addAll(list);

        mapDatas = groupAllDeviceByType(mDeviceList);
        mFunctionAdapter = new ClassifyDeviceAdapter(getContext(), mapDatas);
        mGridView.setAdapter(mFunctionAdapter);
        gridView.setAdapter(mFunctionAdapter);
//        mTvDeviceNum.setText();
    }


    private void initListener() {
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                queryAllDevice();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<DeviceRelate> mDeviceList = mFunctionAdapter.getItem(position);
                if (mListener != null) {
                    mListener.onClickListenerOK(mDeviceList, position, mFunctionAdapter.getKeyList().get(position));
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<DeviceRelate> mDeviceList = mFunctionAdapter.getItem(position);
                if (mListener != null) {
                    mListener.onClickListenerOK(mDeviceList, position, mFunctionAdapter.getKeyList().get(position));
                }
            }
        });

    }

    /**
     * 查询所有设备
     */

    public void queryAllDevice() {
        DeviceController.getInstance().queryDeviceRelateInfo(getActivity(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                json = StringUtil.deviceStateStringReplaceMap(json);
                DeviceRelateResult result = GsonUtil.getObject(json, DeviceRelateResult.class);
                if (result.getRet() != 0) {
                    return;
                }
                if (null == Constant.DEVICE_RELATE) {
                    Constant.DEVICE_RELATE = new ArrayList<>();
                }
               /* //判断设备信息是否有变更
                String md5Value = MD5(json);
                if (!StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
                        && Constant.DEVICE_MD5_VALUE.equals(md5Value)
                        && Constant.GATEWAY != null) {
                    mHandler.sendEmptyMessageDelayed(2, 2 * 1000);
                    return;
                }*/
                Constant.DEVICE_RELATE = result.getResponse();
                if (result.getResponse() == null || result.getResponse().size() == 0) {
                    return;
                }
                mDeviceList.clear();
                mDeviceList.addAll(result.getResponse());
                mHandler.sendEmptyMessage(2);
            }

            @Override
            public void onFailed(String json) {
                mHandler.sendEmptyMessageDelayed(2, 2 * 1000);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    break;
                case 1:
                    break;
                case 2:

                    if (mPullToRefreshListView != null) {
                        mPullToRefreshListView.onRefreshComplete();
                        mapDatas.clear();
                        mapDatas.putAll(ConstantDeviceType.groupAllDeviceByType(mDeviceList));

                        mFunctionAdapter.setData(mapDatas);
                        mFunctionAdapter.notifyDataSetChanged();

                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public void setListener(IObjectInterface listener) {
        mListener = listener;
    }

    @Override
    public void update(Observable o, Object arg) {
        Loger.d("我的错1");
        mDeviceList.clear();
        mDeviceList.addAll(Constant.DEVICE_RELATE);
        mHandler.sendEmptyMessage(2);
    }

    @OnClick(R.id.ll_all_device)
    public void onClick() {
        mDeviceList.clear();
        mDeviceList.addAll(Constant.DEVICE_RELATE);
        if (mListener != null) {
            mListener.onClickListenerOK(mDeviceList, -1, getString(R.string.text_all_device));
        }
    }

    private void setDatas() {
        keyList.clear();
        keyList = getKeyfromMap();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            List<DeviceRelate> list = mapDatas.get(key);
            switch (key) {
                case "空气净化":
                    tvAirclean.setText(getDeviceStatus(list));
                    break;
                case "水质净化":
                    tvWaterclean.setText(getDeviceStatus(list));
                    break;
                case "照明控制":
                    tvLightControl.setText(getDeviceStatus(list));
                    break;
                case "窗帘控制":
                    tvCurtainControl.setText(getDeviceStatus(list));
                    break;
                case "空调控制":
                    tvCACControl.setText(getDeviceStatus(list));
                    break;
                case "监控控制":
                    tvCameraControl.setText(getDeviceStatus(list));
                    break;
                case "电源控制":
                    tvElecControl.setText(getDeviceStatus(list));
                    break;
                case "综合服务":
                    tvGeneralControl.setText(getDeviceStatus(list));
                    break;
                case "气体检测":
                    tvAirCheck.setText(getDeviceStatus(list));
                    break;
                case "影音控制":
                    tvVideoControl.setText(getDeviceStatus(list));
                    break;
                case "安全告警":
                    tvAlarmControl.setText(getDeviceStatus(list));
                    break;
                case "安全防护":
                    tvProtectControl.setText(getDeviceStatus(list));
                    break;
            }
        }
    }

    private List<String> getKeyfromMap() {
        List<String> keyList = new ArrayList<>();
        Set<String> keySet = mapDatas.keySet();
        if (keySet == null) {
            return Collections.emptyList();
        }
        for (String s : keySet) {
            keyList.add(s);
        }
        return keyList;
    }

    private StringBuffer getDeviceStatus(List<DeviceRelate> list) {
        int open = 0;
        for (DeviceRelate deviceRelate : list) {
            if (deviceRelate == null || deviceRelate.getDeviceStatus() == null) {
                continue;
            }
            DeviceStatus status = deviceRelate.getDeviceStatus();
            if (status == null) {
                continue;
            }
            if (status.getOffline() == 1) {
                continue;
            }
            DeviceStatusValue value = status.getValue();
            if (value == null) {
                continue;
            }
            if (status.getType().contains("Light")) {
                if (TextUtils.isEmpty(value.getState()) || value.getState().equals("0")) {
                    //一联灯

                } else if (TextUtils.isEmpty(value.getState2()) || value.getState2().equals("0")) {
                    //二联灯

                } else if (TextUtils.isEmpty(value.getState3()) || value.getState3().equals("0")) {
                    //三联灯

                } else if (TextUtils.isEmpty(value.getState4()) || value.getState4().equals("0")) {
                    //四联灯

                } else {
                    open++;
                }

                value.getState();
            } else if (!TextUtils.isEmpty(value.getState()) && value.getState().equals("1")) {
                open++;
            }

        }
        StringBuffer sb = new StringBuffer();
        sb.append("(" + open + "/" + list.size() + ")");
        return sb;
    }

    @OnClick({R.id.ll_airclean, R.id.ll_waterclean, R.id.ll_light, R.id.ll_curtaincontrol,
            R.id.ll_aircondition, R.id.ll_camera, R.id.ll_elec, R.id.ll_general,
            R.id.ll_aircheck, R.id.ll_video, R.id.ll_safety_alarm, R.id.ll_protect})
    public void onClick(View view) {
        int position = 0;
        switch (view.getId()) {
            case R.id.ll_airclean:
                position = 0;
                break;
            case R.id.ll_waterclean:
                break;
            case R.id.ll_light:
                break;
            case R.id.ll_curtaincontrol:
                break;
            case R.id.ll_aircondition:
                break;
            case R.id.ll_camera:
                break;
            case R.id.ll_elec:
                break;
            case R.id.ll_general:
                break;
            case R.id.ll_aircheck:
                break;
            case R.id.ll_video:
                break;
            case R.id.ll_safety_alarm:
                break;
            case R.id.ll_protect:
                break;
        }
        if (mListener != null) {
            mListener.onClickListenerOK(mDeviceList, position, mFunctionAdapter.getKeyList().get(position));
        }
    }
}

