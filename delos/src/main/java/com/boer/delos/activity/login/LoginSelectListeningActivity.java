package com.boer.delos.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.activity.personal.GatewayBindListeningActivity;
import com.boer.delos.utils.ActivityStack;
import com.boer.delos.utils.ToastHelper;

/**
 * @author PengJiYang
 * @Description: "选择登陆"界面
 * create at 2016/3/9 10:56
 */
public class LoginSelectListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private android.widget.ImageView ivLoginSelectGateway;
    private android.widget.ImageView ivLoginSelectFamilyApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_select);
        initView();

    }

    private void initView() {
        initTopBar(R.string.login_select_title, null, true, false);
        this.ivLoginSelectFamilyApply = (ImageView) findViewById(R.id.ivLoginSelectFamilyApply);
        this.ivLoginSelectGateway = (ImageView) findViewById(R.id.ivLoginSelectGateway);

        this.ivLoginSelectGateway.setOnClickListener(this);
        this.ivLoginSelectFamilyApply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLoginSelectGateway:
                startActivity(new Intent(LoginSelectListeningActivity.this, GatewayBindListeningActivity.class));
                ActivityStack.getInstance().addActivity(LoginSelectListeningActivity.this);
                break;
            case R.id.ivLoginSelectFamilyApply:
                startActivity(new Intent(LoginSelectListeningActivity.this, FamilyApplyListeningActivity.class));
                break;
            case R.id.ivBack:
                ToastHelper.showShortMsg("haha ");
                break;
        }
    }

    @Override
    public void initTopBar(Integer centerTitle, Integer rightTitle, boolean leftId, boolean rightId) {

        super.initTopBar(centerTitle, rightTitle, leftId, rightId);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSelectListeningActivity.this, LoginActivity.class));
            }
        });
    }
}
