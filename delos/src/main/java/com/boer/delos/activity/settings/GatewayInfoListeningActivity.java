package com.boer.delos.activity.settings;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;

public class GatewayInfoListeningActivity extends BaseListeningActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_gateway);
        initTopBar("网关信息", null, true, false);
        initView();
    }

    private void initView() {

    }
}
