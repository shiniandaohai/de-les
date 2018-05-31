package com.boer.delos.activity.personal;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.GatewayBindAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.AdminInfo;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.view.popupWindow.GatewayPassPopUpWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddHostActvity extends BaseListeningActivity implements View.OnClickListener {

    private ListView lvGatewayList;
    private ImageView ivBindRefresh;
    private View view;
    private GatewayBindAdapter adapter;
    private List<GatewayInfo> gatewayList = new ArrayList<>();
    private GatewayPassPopUpWindow passPopUpWindow;
    private String ip;
    private GatewayInfo gatewayInfo;
    private AdminInfo adminInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_gateway_bind, null);
        setContentView(view);
        initView();
        initData();
    }

    private void initData() {
        adapter = new GatewayBindAdapter(this);
        adapter.setDatas(gatewayList);
        this.lvGatewayList.setAdapter(adapter);

        startUDPScan();
    }


    /**
     * 开始扫描主机
     */
    private void startUDPScan() {
        toastUtils.showProgress("正在扫描主机,请稍候...");

        UDPUtils.startUDPBroadCast(new UDPUtils.ScanCallback() {
            @Override
            public void callback() {
                toastUtils.dismiss();
                gatewayList.clear();
                gatewayList.addAll(Constant.gatewayInfos);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    private void initView() {
        initTopBar(R.string.bind_wifi_link_name, null, true, false);
        ivRight.setImageResource(R.drawable.ic_gateway_bind_refresh);
        this.ivBindRefresh = (ImageView) findViewById(R.id.ivBindRefresh);
        this.lvGatewayList = (ListView) findViewById(R.id.lvGatewayList);
        this.ivBindRefresh.setOnClickListener(this);

//        initGatewayList();
        this.lvGatewayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gatewayInfo = gatewayList.get(position);
                ip = gatewayInfo.getIp();
                L.e("选中主机的ip===========" + ip);
                passPopUpWindow = new GatewayPassPopUpWindow(AddHostActvity.this, new GatewayPassPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(String code) {
                        setSecureCode(code);
                    }
                });
                passPopUpWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
                passPopUpWindow.etGatewaySecureCode.requestFocus();
                passPopUpWindow.update();

                InputMethodManager imm = (InputMethodManager) getSystemService(AddHostActvity.this.INPUT_METHOD_SERVICE);
                imm.showSoftInput(passPopUpWindow.etGatewaySecureCode, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                // 用户输入主机安全码
//                GatewayPassPopUpWindow passPopUpWindow = new GatewayPassPopUpWindow(AddHostActvity.this);
//                passPopUpWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);

//                // 如果当前用户不是主机的管理者,则弹出对话框
//                GatewayBindPopUpWindow offLinePopUpWindow = new GatewayBindPopUpWindow(AddHostActvity.this);
//                offLinePopUpWindow.setGatewayTipContent("123456789");// 设置管理者的手机号码(用户名)
//                offLinePopUpWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
            }
        });
    }

    /**
     * 主机安全码
     */
    private void setSecureCode(String code) {
        GatewayController.getInstance().verifyAdminPassword(this, gatewayInfo.getIp(),
                Md5Encrypt.stringMD5(code), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                    adminInfo = new Gson().fromJson(Json, AdminInfo.class);
                    if (adminInfo.getStatus() == 0) {
                        bindGateway();
                    } else {
                        toastUtils.showErrorWithStatus(adminInfo.getStatusinfo());
                    }
            }

            @Override
            public void onFailed(String json) {

            }
        });

    }

    private void bindGateway() {
        GatewayController.getInstance().bindGateway(this, adminInfo.getHostId(), Constant.USERID, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try{
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if(result.getRet() != 0){
                        toastUtils.showErrorWithStatus(result.getMsg());
                    }else{
                        toastUtils.showSuccessWithStatus("绑定成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivBindRefresh:
                startUDPScan();
                break;
        }
    }


}
