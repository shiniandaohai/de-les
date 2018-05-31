package com.boer.delos.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.view.popupWindow.ShowLimitTimePopupWindow;
import com.zhy.android.percent.support.PercentLinearLayout;

/**
 * @author PengJiYang
 * @Description: "关于我们"界面
 * create at 2016/4/6 09:21
 *
 */
public class AboutUsListeningActivity extends BaseListeningActivity implements View.OnClickListener{

    private TextView tvAboutUsVersion;
    private PercentLinearLayout llAboutJia;
    private PercentLinearLayout llFunctionIntroduce;
    private PercentLinearLayout llAdviceFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initView();
    }

    private void initView() {
        initTopBar(R.string.setting_about_us, null, true, false);
        this.llAdviceFeedback = (PercentLinearLayout) findViewById(R.id.llAdviceFeedback);
        this.llFunctionIntroduce = (PercentLinearLayout) findViewById(R.id.llFunctionIntroduce);
        this.llAboutJia = (PercentLinearLayout) findViewById(R.id.llAboutJia);
        this.tvAboutUsVersion = (TextView) findViewById(R.id.tvAboutUsVersion);

        this.llAboutJia.setOnClickListener(this);
        this.llFunctionIntroduce.setOnClickListener(this);
        this.llAdviceFeedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAboutJia:
                startActivity(new Intent(this, AboutJiaListeningActivity.class));
                break;
            case R.id.llFunctionIntroduce:
//                startActivity(new Intent(this, FunctionnListeningActivity.class));

                ShowLimitTimePopupWindow   showLimitTimePopupWindow = new ShowLimitTimePopupWindow(this, llAdviceFeedback);
                showLimitTimePopupWindow.showPopupWindow();

                break;
            case R.id.llAdviceFeedback:
                startActivity(new Intent(this, AdviceFeedbackListeningActivity.class));
                break;
        }
    }
}
