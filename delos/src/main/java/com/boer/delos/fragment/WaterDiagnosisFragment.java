package com.boer.delos.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.WaterCleanDetailActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.interf.IObjectInterface;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.WaterTDSResult;
import com.boer.delos.utils.GsonUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/12 0012 16:48
 * @Modify:
 * @ModifyDate:
 */


public class WaterDiagnosisFragment extends LazyFragment
        implements IObjectInterface<List<WaterTDSResult.WaterBean>> {
    @Bind(R.id.tv_amount_state)
    TextView mTvAmountState;
    @Bind(R.id.tv_location_state)
    TextView mTvLocationState;
    @Bind(R.id.tv_clean_state)
    TextView mTvCleanState;
    @Bind(R.id.tv_deWater_state)
    TextView mTvDeWaterState;
    @Bind(R.id.tv_machine_state)
    TextView mTvMachineState;
    private View rootView;
    private Device mDevice;
    private DeviceStatusValue mValue;
    private DeviceRelate mDeviceRelate;

    public static WaterDiagnosisFragment newInstance(DeviceRelate d) {

        Bundle args = new Bundle();
        args.putSerializable("device", d);
        WaterDiagnosisFragment fragment = new WaterDiagnosisFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_water_diagnosis, container, false);
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
    }

    private void initData() {

    }

    private void initListener() {

    }

    public void updateUI(DeviceStatusValue value) {
        if (value == null) {
            return;
        }
        if (value.getRawCisternLevel() != null) {


            mTvAmountState.setText(value.getRawCisternLevel() == 0 ?
                    getString(R.string.text_normal)
                    : getString(R.string.water_short));
            mTvAmountState.setTextColor(value.getRawCisternLevel() == 0 ?
                    getResources().getColor(R.color.gray_et_text)
                    : getResources().getColor(R.color.gray_text_delete));
        }
        if (value.getRawCisternLevel() != null) {

            mTvLocationState.setText(value.getRawCisternPos() == 0 ?
                    getString(R.string.text_normal)
                    : getString(R.string.water_move));
            mTvLocationState.setTextColor(value.getRawCisternPos() == 0 ?
                    getResources().getColor(R.color.gray_et_text)
                    : getResources().getColor(R.color.gray_text_delete));
        }

        if (value.getRawCisternLevel() != null) {

            mTvCleanState.setText(value.getPurifying() == 0 ?
                    getString(R.string.text_normal)
                    : getString(R.string.water_product));

            mTvCleanState.setTextColor(value.getPurifying() == 0 ?
                    getResources().getColor(R.color.gray_et_text)
                    : getResources().getColor(R.color.gray_text_delete));
        }

        if (value.getRawCisternLevel() != null) {

            mTvDeWaterState.setText(value.getDewatering() == 0 ?
                    getString(R.string.text_normal)
                    : getString(R.string.water_short));
            mTvDeWaterState.setTextColor(value.getDewatering() == 0 ?
                    getResources().getColor(R.color.gray_et_text)
                    : getResources().getColor(R.color.gray_text_delete));
        }

        if (value.getRawCisternLevel() != null) {


            mTvMachineState.setText(value.getMachineState() == 0 ?
                    getString(R.string.text_normal)
                    : getString(R.string.water_fault));
            mTvAmountState.setTextColor(value.getMachineState() == 0 ?
                    getResources().getColor(R.color.gray_et_text)
                    : getResources().getColor(R.color.gray_text_delete));
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClickListenerOK(List<WaterTDSResult.WaterBean> waterBeen, int pos, String tag) {
//        if (waterBeen != null && waterBeen.size() != 0) {
//            String payload = waterBeen.get(0).getPayload();
//            if (!TextUtils.isEmpty(payload)) {
//                WaterTDSResult.PayloadBean payloadBean = GsonUtil.getObject(payload,
//                        WaterTDSResult.PayloadBean.class);
//
//                if (payloadBean == null && payloadBean.getValue() == null) {
//                    return;
//                }
//
//                updateUI(payloadBean.getValue());
//            }
//
//        }


    }
}
