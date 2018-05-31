package com.boer.delos.activity.healthylife.smartmirror;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.SmartMirror;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.BindEmailPopUpWindow;
import com.boer.delos.view.popupWindow.ShowRequestEditPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/4/22.
 */
public class SmartMirrorLoginActivity extends CommonBaseActivity {

    @Bind(R.id.img_mirror)
    ImageView imgMirror;
    @Bind(R.id.tv_mirro_standard)
    TextView tvMirroStandard;
    @Bind(R.id.tv_mirro_type)
    TextView tvMirroType;
    @Bind(R.id.tv_mirro_id)
    TextView tvMirroId;
    @Bind(R.id.tv_mirro_rename)
    TextView tvMirroRename;
    @Bind(R.id.btn_rename)
    Button btnRename;
    @Bind(R.id.btn_off_line)
    Button btnOffLine;
    @Bind(R.id.btn_login)
    Button btnLogin;
    private String result;

    private String mirror_standard;
    private String mirror_type;
    private String mirror_id;
    private String mirror_client_id;
    private String mirror_name = "";

    private ShowRequestEditPopupWindow showRequestEditPopupWindow;

    private SmartMirror smartMirror;
    private HealthController healthController;

    @Override
    protected int initLayout() {
        return R.layout.activity_smart_mirror_login;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.mirror_title);

    }

    @Override
    protected void initData() {


        Intent intent = getIntent();
        result = intent.getStringExtra("result");


        smartMirror = new SmartMirror();
        healthController = new HealthController();


        String[] info = result.split(",");

        if (info.length > 3) {
            mirror_standard = info[1];
            mirror_type = info[0];
            mirror_id = info[2];
            mirror_client_id = info[3];
        }

        tvMirroStandard.setText(mirror_standard);
        tvMirroType.setText(mirror_type);
        tvMirroId.setText(mirror_id);
        tvMirroRename.setText(mirror_name);


        smartMirror.setId(mirror_id);
        smartMirror.setSpecification(mirror_standard);
        smartMirror.setModel(mirror_type);
        smartMirror.setClientId(mirror_client_id);
        smartMirror.setRemark(mirror_name);


        showRequestEditPopupWindow = new ShowRequestEditPopupWindow(this, tlTitleLayout, getResources().getString(R.string.mirror_bind_name_title));

    }

    @Override
    protected void initAction() {

        showRequestEditPopupWindow.setShowRequestPopupWindow(new ShowRequestEditPopupWindow.IShowRequest() {
            @Override
            public void rightButtonClick(String result) {

                tvMirroRename.setText(result);

                smartMirror.setRemark(result);

            }
        });

    }


    @OnClick({R.id.btn_rename, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rename:
                showRequestEditPopupWindow.showPopupWindow();
                break;
            case R.id.btn_login:

                loginMirror();

                break;
        }
    }

    private void loginMirror() {

        toastUtils.showProgress("");
        healthController.loginSmartMirror(this, smartMirror, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                toastUtils.dismiss();

                Log.v("gl", "result++++===" + result);

                int ret = 0;

                try {
                    JSONObject jsonObject = new JSONObject(json);

                    if (jsonObject.has("ret")) {

                        ret = jsonObject.getInt("ret");


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (ret == 0) {

                    setResult(RESULT_OK);
                    finish();


                } else if (ret == 20038) {


                    ToastHelper.showShortMsg(getResources().getString(R.string.mirror_rebind));

                } else {

                    finish();

                }


            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();

            }
        });


    }
}
