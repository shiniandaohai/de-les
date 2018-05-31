package com.boer.delos.activity.healthylife.wifiAirClean;

import android.content.Intent;
import android.view.View;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;

import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/27.
 */

public class WifiAirCleanChoiceTypeActivity extends CommonBaseActivity {
    @Override
    protected int initLayout() {
        return R.layout.activity_wifi_air_clean_choice_type;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("选择型号");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @OnClick({R.id.tv_t66, R.id.tv_t30s})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_t66:
                startActivity(new Intent(mContext,WifiAirCleanAddNewDeviceActivity.class).putExtra("type","T66"));
                break;
            case R.id.tv_t30s:
                startActivity(new Intent(mContext,WifiAirCleanAddNewDeviceActivity.class).putExtra("type","T30"));
                break;
        }
    }
}
