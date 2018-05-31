package com.boer.delos.activity.scene.devicecontrol;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusResult;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:台下净水器
 * @CreateDate: 2017/4/14 0014 15:46
 * @Modify:
 * @ModifyDate:
 */


public class WaterFloorCleanActivity extends CommonBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tv_raw_TDS)
    TextView mTvRawTDS;
    @Bind(R.id.tv_raw_quality)
    TextView mTvRawQuality;
    @Bind(R.id.tv_clean_TDS)
    TextView mTvCleanTDS;
    @Bind(R.id.tv_clean_quality)
    TextView mTvCleanQuality;
    @Bind(R.id.tv_shortage_state)
    TextView mTvShortageState;
    @Bind(R.id.tv_leakage_state)
    TextView mTvLeakageState;
    @Bind(R.id.tv_flush_state)
    TextView mTvFlushState;
    @Bind(R.id.tv_filter_residual1)
    TextView mTvFilterResidual1;
    @Bind(R.id.tv_filter_residual2)
    TextView mTvFilterResidual2;
    @Bind(R.id.tv_filter_residual3)
    TextView mTvFilterResidual3;
    @Bind(R.id.tv_filter_residual4)
    TextView mTvFilterResidual4;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeLayout;


    private Device mDevice;
    private DeviceStatus mStatus;
    private Timer mTimer;
    private String mMD5 = "0";
    private List<Device> devices = new ArrayList<Device>();
    private String tag; //标记是否是常用设备

    @Override
    protected int initLayout() {
        return R.layout.activity_floor_water_clean;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            DeviceRelate deviceRelate = (DeviceRelate) bundle.getSerializable("device");
            mDevice = deviceRelate.getDeviceProp();
            if (mDevice != null)
                tag = mDevice.getFavorite();
            if (mDevice != null && !TextUtils.isEmpty(mDevice.getFavorite())
                    && mDevice.getFavorite().equals("1")) {
                tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_sel_white);
            } else
                tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor_white);
            mStatus = deviceRelate.getDeviceStatus();
        }
        tlTitleLayout.setTitle(mDevice.getName(), R.color.white);
        tlTitleLayout.setTitleBackgroundColor(getResources().getColor(R.color.green_water));
        mSwipeLayout.setOnRefreshListener(this);
        tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back_white);
    }

    @Override
    protected void initData() {
        final List<Device> list = new ArrayList<>();
        Device device = new Device();
        device.setAddr(mDevice.getAddr());
        list.add(device);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                queryDeviceStatus(list);
            }
        }, 0, 5 * 1000);
        updateUI(mStatus);
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void rightViewClick() {
        if (tag.equals("1")) {
            tag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor_white);
        } else if (tag.equals("0")) {
            tag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_sel_white);
        }
        //保存状态，并发送后台
        if (mDevice != null) {  devices.clear();
            mDevice.setFavorite(tag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

    @Override
    public void onRefresh() {
        final List<Device> list = new ArrayList<>();
        Device device = new Device();
        device.setAddr(mDevice.getAddr());
        list.add(device);
        queryDeviceStatus(list);
    }

    /**
     * 查询设备状态
     *
     * @param list
     */
    private void queryDeviceStatus(List<Device> list) {
        DeviceController.getInstance().queryDevicesStatus(this, list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    mSwipeLayout.setRefreshing(false);
                    DeviceStatusResult relateResult = GsonUtil.getObject(json, DeviceStatusResult.class);
                    if (relateResult.getRet() != 0) {
                        return;
                    }
                    if (!TextUtils.isEmpty(mMD5)
                            && !TextUtils.isEmpty(relateResult.getMd5())
                            && relateResult.getMd5().equals(mMD5)) {
                        return;
                    }
                    mMD5 = relateResult.getMd5();
                    Loger.d("我的天    " + json);
                    mStatus = relateResult.getResponse().getDevices().get(0);
                    /*离线状态*/
                    if (mStatus.getOffline() == 1) {
                        return;
                    }

                    updateUI(mStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                    mSwipeLayout.setRefreshing(false);
                }


            }

            @Override
            public void onFailed(String json) {
                if (mSwipeLayout != null) {
                    mSwipeLayout.setRefreshing(false);
                }
            }
        });
    }

    private void updateUI(DeviceStatus status) {
        if (status == null) {
            return;
        }
        DeviceStatusValue value = status.getValue();
        if (value == null) {
            return;
        }
        mTvRawTDS.setText(value.getRawTDS() + "");
        mTvCleanTDS.setText(value.getPureTDS() + "");

        //TODO 水质
//        mTvRawQuality.setText();
//        mTvCleanQuality.setText();

        mTvShortageState.setText(value.getLackWater() == 1
                ? getString(R.string.water_short)
                : getString(R.string.text_normal));
        if (!TextUtils.isEmpty(mTvShortageState.getText())
                && mTvShortageState.getText().equals(getString(R.string.water_short))) {
            mTvShortageState.setTextColor(getResources().getColor(R.color.gray_text_delete));
        } else {
            mTvShortageState.setTextColor(getResources().getColor(R.color.white));
        }
        //  2-表示长时间连续制水报警，1-表示漏水故障，0-表示正常无故障
        mTvLeakageState.setText(leakStatus(value.getLeakWater()));
        if (!TextUtils.isEmpty(mTvLeakageState.getText())
                && mTvLeakageState.getText().equals(getString(R.string.state_water_leakage))) {
            mTvLeakageState.setTextColor(getResources().getColor(R.color.gray_text_delete));
        } else {
            mTvLeakageState.setTextColor(getResources().getColor(R.color.white));
        }


        //冲洗状态，0-未冲洗，1-自动冲洗，2-手动冲洗
        mTvFlushState.setText(filterFlushLevel(value.getRinse()));

        mTvFilterResidual1.setText(filterLevel(value.getFilterLevel1())[0] + "%");
        mTvFilterResidual2.setText(filterLevel(value.getFilterLevel2())[0] + "%");
        mTvFilterResidual3.setText(filterLevel(value.getFilterLevel3())[0] + "%");
        mTvFilterResidual4.setText(filterLevel(value.getFilterLevel4())[0] + "%");

        mTvFilterResidual1.setBackgroundResource(filterLevel(value.getFilterLevel1())[1]);
        mTvFilterResidual2.setBackgroundResource(filterLevel(value.getFilterLevel2())[1]);
        mTvFilterResidual3.setBackgroundResource(filterLevel(value.getFilterLevel3())[1]);
        mTvFilterResidual4.setBackgroundResource(filterLevel(value.getFilterLevel4())[1]);

    }

    private String leakStatus(int status) {
        switch (status) {
            case 0:
                return getString(R.string.text_normal_fault);
            case 1:
                return getString(R.string.state_water_leakage);
            case 2:
                return getString(R.string.water_product_always);
            default:
                return getString(R.string.text_normal_fault);
        }
    }

    private int[] filterLevel(int level) {
        switch (level) {
            case 0:
                return new int[]{0, R.mipmap.ic_floor_filter_0};
            case 1:
                return new int[]{25, R.mipmap.ic_floor_filter_1};
            case 2:
                return new int[]{50, R.mipmap.ic_floor_filter_2};
            case 3:
                return new int[]{75, R.mipmap.ic_floor_filter_3};
            case 4:
                return new int[]{100, R.mipmap.ic_floor_filter_4};
            default:
                return new int[]{100, R.mipmap.ic_floor_filter_4};
        }
    }

    /**
     * 冲洗状态
     *
     * @param rinse
     * @return
     */
    private String filterFlushLevel(int rinse) {
        String result = "";
        switch (rinse) {
            case 0:
                result = getString(R.string.text_nor_flush);
                break;
            case 1:
                result = getString(R.string.text_auto_flush);
                break;
            case 2:
                result = getString(R.string.text_hand_flush);
                break;


        }

        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
    }
}
