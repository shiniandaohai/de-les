package com.boer.delos.activity.healthylife.pressure;

import android.view.KeyEvent;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/17 0017 21:57
 * @Modify:
 * @ModifyDate:
 */


public class BloodPressureMajorActivity extends CommonBaseActivity {
    @Override
    protected int initLayout() {
        return R.layout.activity_blood_pressure_major;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_major_info));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}
