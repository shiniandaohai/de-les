package com.boer.delos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.boer.delos.commen.BaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.TimeUtil;

import static android.R.attr.button;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/9 0009 11:38
 * @Modify:
 * @ModifyDate:
 */


public class TestActivity extends BaseActivity {
    private int heathyType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    /**
     * 查询历史记录
     */
    private void initData(String fromTime, String healthyType, String recent, String userId) {

        HealthController.getInstance().queryRecentHealth(this,
                fromTime, healthyType, recent, userId, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        Loger.d("健康 "+json);

                    }

                    @Override
                    public void onFailed(String json) {
                        Loger.d("健康 "+json);

                    }
                });

    }


    /**************************************/



}
