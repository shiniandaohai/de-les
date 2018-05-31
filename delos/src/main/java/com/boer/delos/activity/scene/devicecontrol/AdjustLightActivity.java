package com.boer.delos.activity.scene.devicecontrol;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.Loger;
import com.boer.delos.widget.SeekBarView;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/11 0011 21:34
 * @Modify:
 * @ModifyDate:
 */


public class AdjustLightActivity extends CommonBaseActivity implements SeekBarView.OnSeekArcChangeListener {

    @Override
    protected int initLayout() {
        return R.layout.activity_device_control_adjust;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    public void onProgressChanged(SeekBarView seekArc, int progress, boolean fromUser) {
        Loger.d("onProgressChanged() " + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBarView seekArc) {

    }

    @Override
    public void onStopTrackingTouch(SeekBarView seekArc) {

    }
}
