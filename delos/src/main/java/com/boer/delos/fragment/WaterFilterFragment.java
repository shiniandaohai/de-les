package com.boer.delos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.WaterCleanDetailActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusResult;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.WaterTDSResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/12 0012 14:58
 * @Modify:
 * @ModifyDate:
 */


public class WaterFilterFragment extends LazyFragment
        implements IObjectInterface<List<WaterTDSResult.WaterBean>> {
    @Bind(R.id.progressbar1)
    ProgressBar mProgressbar1;
    @Bind(R.id.tv_filter_residual1)
    TextView mTvFilterResidual1;
    @Bind(R.id.progressbar2)
    ProgressBar mProgressbar2;
    @Bind(R.id.tv_filter_residual2)
    TextView mTvFilterResidual2;
    @Bind(R.id.progressbar3)
    ProgressBar mProgressbar3;
    @Bind(R.id.tv_filter_residual3)
    TextView mTvFilterResidual3;

    private View rootView;
    private Device mDevice;

    private DeviceRelate mDeviceRelate;
    private DeviceStatus mStatus;

    public static WaterFilterFragment newInstance(DeviceRelate d) {

        Bundle args = new Bundle();
        args.putSerializable("device", d);
        WaterFilterFragment fragment = new WaterFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_water_filter, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        initData();
        initListener();
        return rootView;
    }

    @Override
    protected void lazyLoad() {

    }

    private void initView() {
        mDeviceRelate = (DeviceRelate) getArguments().getSerializable("device");
        clearUI();
    }

    private void initData() {
//        List<Device> list = new ArrayList<>();
//        list.add(mDevice);
//        queryDeviceStatus(list);

    }

    private void initListener() {

    }

    /**
     * 查询设备状态
     *
     * @param list
     */
    private void queryDeviceStatus(List<Device> list) {
        DeviceController.getInstance().queryDevicesStatus(getActivity(), list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    DeviceStatusResult relateResult = GsonUtil.getObject(json, DeviceStatusResult.class);
                    if (relateResult.getRet() != 0) {
                        return;
                    }
                    mStatus = relateResult.getResponse().getDevices().get(0);
                    /*离线状态*/
                    if (mStatus.getOffline() == 1) {

                        return;
                    }

//                    updateUI(mStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    private void updateUI(List<WaterTDSResult.WaterBean> waterBeens) {
        if (waterBeens != null && waterBeens.size() != 0) {
            String payload = waterBeens.get(0).getPayload();
            WaterTDSResult.PayloadBean payloadBean = GsonUtil.getObject(payload, WaterTDSResult.PayloadBean.class);
            if (payloadBean.getValue() == null) {
                clearUI();
                return;
            }
            DeviceStatusValue value = payloadBean.getValue();
            if (value == null) {
                clearUI();
                return;
            }
            int pos1 = value.getFilterLevel1();
            int pos2 = value.getFilterLevel2();
            int pos3 = value.getFilterLevel3();
            int pos4 = value.getFilterLevel4();

            mProgressbar1.setProgress(pos1);
            mProgressbar2.setProgress(pos2);
            mProgressbar3.setProgress(pos3);
//            mProgressbar3.setProgress(pos4);

            mTvFilterResidual1.setText(pos1 + "%");
            mTvFilterResidual2.setText(pos2 + "%");
            mTvFilterResidual3.setText(pos3 + "%");


        } else {
            clearUI();

        }

    }

    private void clearUI() {

        mProgressbar1.setProgress(0);
        mProgressbar2.setProgress(0);
        mProgressbar3.setProgress(0);
        mProgressbar3.setProgress(0);

        mTvFilterResidual1.setText(0 + "%");
        mTvFilterResidual2.setText(0 + "%");
        mTvFilterResidual3.setText(0 + "%");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClickListenerOK(List<WaterTDSResult.WaterBean> waterBeens, int pos, String tag) {
        updateUI(waterBeens);
    }
}
