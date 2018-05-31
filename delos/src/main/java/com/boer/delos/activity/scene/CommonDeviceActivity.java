package com.boer.delos.activity.scene;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.adapter.CommonDeviceAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:常用设备界面
 * @CreateDate: 2017/3/31 0031 13:49
 * @Modify:
 * @ModifyDate:
 */
public class CommonDeviceActivity extends CommonBaseActivity implements ISimpleInterfaceInt {

    @Bind(R.id.tv_gateway_name)
    TextView mTvGatewayName;
    @Bind(R.id.tv_device_num)
    TextView mTvDeviceNum;
    @Bind(R.id.pullToRefreshListView)
    PullToRefreshListView mPullToRefreshListView;
    private List<DeviceRelate> mDeviceLists; //全部设备
    private CommonDeviceAdapter mDeviceAdapter;
    private ListView mLvDevices;
    private String[] filterDevice = new String[]{"Ch4CO", "O2CO2", "Env", "Smoke", "Exist", "Fall",
            "Water", "Sov", "SOS", "Gsm", "CurtainSensor", "N4","HGC","AcoustoOpticAlarm","LaserEgg"};

    @Override
    protected int initLayout() {
        return R.layout.activity_add_common_device;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_common_device));
        tlTitleLayout.setRightText(getString(R.string.text_certain));
        tlTitleLayout.setTag("back");
        mLvDevices = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    protected void initData() {

        if (Constant.GATEWAY != null) {
            mTvGatewayName.setText(Constant.GATEWAY.getName());
        }
        if (mDeviceLists == null) mDeviceLists = new ArrayList<>();

        mDeviceAdapter = new CommonDeviceAdapter(this, mDeviceLists,
                R.layout.item_common_device);
        mLvDevices.setAdapter(mDeviceAdapter);

        if (Constant.DEVICE_RELATE != null) {
            dealWithData(Constant.DEVICE_RELATE, "");//不显示气体检测和安全告警下的设备
        }

        getDeviceStatusInfo();
        mPullToRefreshListView.setRefreshing();
    }

    @Override
    protected void initAction() {
        if (mDeviceAdapter != null) {
            mDeviceAdapter.setListener(this);
        }

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDeviceStatusInfo();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    @Override
    public void leftViewClick() {
//        if (mDeviceAdapter != null
//                && !tlTitleLayout.getTag().toString().equals("back")) {
//            mDeviceAdapter.clearAll();
//            tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back);
//            tlTitleLayout.setTag("back");
//        } else {
        super.leftViewClick();
//        }


    }

    @Override
    public void rightViewClick() {
        toastUtils.showSuccessWithStatus("更新中...");
        settingCommonDevice();
    }

    private void updateUI(List<DeviceRelate> mDeviceLists) {
        int num = 0;

        for (DeviceRelate deviceRelate : mDeviceLists) {
            if (deviceRelate == null || deviceRelate.getDeviceProp() == null) {
                continue;
            }
            Device d = deviceRelate.getDeviceProp();
            if (d == null) {
                continue;
            }
            if (!TextUtils.isEmpty(d.getFavorite()) && d.getFavorite().equals("1")) {
                num++;
            }

        }
        mTvDeviceNum.setText(String.valueOf(num));

    }

    private void dealWithData(List<DeviceRelate> deviceRelates, String json) {
        if (deviceRelates == null) {
            return;
        }
       /* //判断设备信息是否有变更
        String md5Value = MD5(json);
        if (mDeviceRelates.size() != 0 &&
                !StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
                && Constant.DEVICE_MD5_VALUE.equals(md5Value)
                && Constant.GATEWAY != null) {
            return;
        }*/
        //过滤掉气体检测和安全告警下的设备
        mDeviceLists.clear();
        List<String> list = Arrays.asList(filterDevice);
        for (DeviceRelate deviceRelate : deviceRelates) {
            if (list.contains(deviceRelate.getDeviceProp().getType())) {
                continue;
            }
            mDeviceLists.add(deviceRelate);
        }
        mDeviceAdapter.setDatas(mDeviceLists);

        updateUI(mDeviceLists);
    }

    /**
     * 设置常用设备
     */
    private void settingCommonDevice() {
        List<DeviceRelate> list = mDeviceAdapter.getSelectData();
        List<Device> devices = new ArrayList<>(); //选中的
        List<Device> deviceAdd = new ArrayList<>();
        for (DeviceRelate deviceRelate2 : list) {
            if (deviceRelate2 == null || deviceRelate2.getDeviceProp() == null) {
                continue;
            }
            Device device = deviceRelate2.getDeviceProp();
            devices.add(newObject(device));
        }

        for (DeviceRelate deviceRelate1 : mDeviceLists) {

            if (deviceRelate1 == null || deviceRelate1.getDeviceProp() == null) {
                continue;
            }
            Device device1 = deviceRelate1.getDeviceProp();
            if (device1.getFavorite() == null) {
                device1.setFavorite("0");
            }

            if (device1.getFavorite().equals("1")) {
                if (devices.contains(device1)) {
                    devices.remove(device1);

                } else {
                    device1.setFavorite("0");
                    deviceAdd.add(device1);
                }
            } else {
                if (devices.contains(device1)) {
                    device1.setFavorite("1");
                    deviceAdd.add(device1);
                } else {

                }
            }

        }

        if (deviceAdd.size() == 0) {
            toastUtils.dismiss();
            finish();
            return;
        }

        DeviceController.getInstance().AddCommonDevice(this, deviceAdd, Constant.LOCAL_CONNECTION_IP, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() != 0) {
                    String toastString = result.getMsg();
                    toastUtils.showErrorWithStatus(TextUtils.isEmpty(toastString) ? "error" : toastString);
                    return;
                }
                //提示
//                toastUtils.showSuccessWithStatus(getString(R.string.text_add_success));
                mDeviceAdapter.clearAll();
                tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back);
                //TODO 更新
                getDeviceStatusInfo();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toastUtils.dismiss();
                        finish();
                    }
                }, 2000);
            }

            @Override
            public void onFailed(String json) {
                toastUtils.showSuccessWithStatus("更新失败");
                Loger.d(json);
            }
        });

    }

    private void getDeviceStatusInfo() {
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Loger.d("哈哈 " + Json);
                    Json = StringUtil.deviceStateStringReplaceMap(Json);
                    DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);
                    if (result.getRet() != 0) {
                        return;
                    }
                    Constant.DEVICE_RELATE.clear();
                    Constant.DEVICE_RELATE.addAll(result.getResponse());

                    dealWithData(Constant.DEVICE_RELATE, Json);
                    mHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    L.e("queryDeviceRelateInfo:" + e);
                    mHandler.sendEmptyMessageDelayed(1, 2000);
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.e("getDeviceStatusInfo()" + json);
                mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        });
    }

    @Override
    public void clickListener(int tag) {
        if (mDeviceAdapter != null && mDeviceAdapter.getSelectData().size() != 0) {
            tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back);
            tlTitleLayout.setTag("clear");
        } else {
            tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back);
            tlTitleLayout.setTag("back");
        }
    }


    private Device newObject(Device device) {
        //创建对象
        Device tempDevice = new Device();
        tempDevice.setAddr(device.getAddr());
//        tempDevice.setType(device.getType());
//        tempDevice.setDismiss(device.getDismiss());
//        tempDevice.setRoomId(device.getRoomId());
//        tempDevice.setRoomname(device.getRoomname());
//        tempDevice.setName(device.getName());
//        tempDevice.setFavorite("1");
//        tempDevice.setId(device.getId());
//        tempDevice.setKeyId(device.getKeyId());
//        tempDevice.setButtons(device.getButtons());
//        tempDevice.setAcData(device.getAcData());
//        tempDevice.setGuardInfo(device.getGuardInfo());
//        tempDevice.setIsVirtual(device.getIsVirtual());

        return tempDevice;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    if (mPullToRefreshListView != null)
                        mPullToRefreshListView.onRefreshComplete();

                    break;
                case 1:
                    if (mPullToRefreshListView != null)
                        mPullToRefreshListView.onRefreshComplete();
                    break;
            }


        }
    };

}
