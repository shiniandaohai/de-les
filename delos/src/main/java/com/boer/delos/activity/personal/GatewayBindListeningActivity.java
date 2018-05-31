package com.boer.delos.activity.personal;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.adapter.GatewayBindAdapter;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.AdminInfo;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.User;
import com.boer.delos.model.UserResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.alarm.AlarmController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.GatewayBindPopUpWindow;
import com.boer.delos.view.popupWindow.GatewayPassPopUpWindow;
import com.google.gson.Gson;

import org.linphone.mediastream.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: "主机绑定"界面
 * create at 2016/3/28 10:54
 */
public class GatewayBindListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private android.widget.ListView lvGatewayList;
    private ImageView ivBindRefresh;
    private View view;
    private GatewayBindAdapter adapter;
    private List<GatewayInfo> gatewayList = new ArrayList<>();
    private GatewayPassPopUpWindow passPopUpWindow;
    private GatewayInfo gatewayInfo;
    private User adminInfo;
    private GatewayBindPopUpWindow gatewayBindPopUpWindow;
    private List<String> existsHosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_gateway_bind, null);
        setContentView(view);

        //用户已经加入的主机
        existsHosts = (List<String>) getIntent().getSerializableExtra("hosts");

        initView();
        initData();
    }

    private void initData() {
        adapter = new GatewayBindAdapter(this);
        adapter.setDatas(gatewayList);
        this.lvGatewayList.setAdapter(adapter);
        adapter.setOnBindClickLisenter(new GatewayBindAdapter.OnBindClickLisenter() {
            @Override
            public void onBindClick(int position) {
                gatewayInfo = gatewayList.get(position);
                requestHostAdmin();
            }
        });
        //扫描主机
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toastUtils != null) toastUtils.dismiss();
                        gatewayList.clear();
                        //过滤用户已加入的主机
                        List<GatewayInfo> gateways = canAddGateways(Constant.gatewayInfos);
                        gatewayList.addAll(gateways);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /**
     * 将用户已经加入的的主机进行过滤
     *
     * @param list
     * @return
     */
    private List<GatewayInfo> canAddGateways(List<GatewayInfo> list) {
        if (existsHosts == null) {
            return list;
        }
        List<GatewayInfo> gateways = new ArrayList<>();
        for (GatewayInfo info : list) {
            if (!existsHosts.contains(info.getHostId())) {
                gateways.add(info);
            }
        }
        return gateways;
    }


    private void initView() {
        initTopBar("添加主机", null, true, true);
        ivRight.setImageResource(R.drawable.ic_gateway_bind_refresh);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUDPScan();
            }
        });
        this.ivBindRefresh = (ImageView) findViewById(R.id.ivBindRefresh);
        this.lvGatewayList = (ListView) findViewById(R.id.lvGatewayList);
        this.ivBindRefresh.setOnClickListener(this);

//        this.lvGatewayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                gatewayInfo = gatewayList.get(position);
//                requestHostAdmin();
//            }
//        });
    }

    /**
     * 查询主机管理员
     */
    private void requestHostAdmin() {
        if(Constant.LOGIN_USER.getName().equals("admin")){
            popPasswodWindow();
            return;
        }
        FamilyManageController.getInstance().adminInfoWithHostId(this, gatewayInfo.getHostId(),
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Loger.d("GatewayBindActivity requestHostAdmin " + Json);
                        try {
                            UserResult result = new Gson().fromJson(Json, UserResult.class);
                            if (result.getRet() != 0) {
                                //无管理员
                                if (result.getRet() == 20020) {
                                    popPasswodWindow();
                                } else {
                                    toastUtils.showErrorWithStatus(result.getMsg());
                                }
                            } else {
                                adminInfo = result.getUser();
                                //有管理员,需要申请
                                popGateBindWindow();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d("GatewayBindActivity requestHostAdmin " + Json);
                    }
                });
    }

    /**
     * 弹出向管理申请的对话框
     */
    private void popGateBindWindow() {
        gatewayBindPopUpWindow = new GatewayBindPopUpWindow(this,
                new GatewayBindPopUpWindow.ClickListener() {
                    @Override
                    public void okClick() {
                        userApply();
                    }
                });
        gatewayBindPopUpWindow.setGatewayTipContent(adminInfo.getMobile());
        gatewayBindPopUpWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * 用户申请
     */
    private void userApply() {
        /** applyStatus: 1、用户申请 2、管理员分享
         // status : 0、待确认     1、用户同意   2、用户拒绝
         //          3、用户取消  　4、管理员同意 5、管理员拒绝
         //          6、管理员取消
         */
        toastUtils.showProgress("正在申请...");
        FamilyManageController.getInstance().userApplyToAdmin(this,
                adminInfo.getId(),
                Constant.LOGIN_USER.getId(),
                "",
                "1",
                gatewayInfo.getHostId(),
                "",
                "0", "", "",
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                toastUtils.showSuccessWithStatus("申请成功");
                            }

                            pushNotification();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }

    private void pushNotification() {
        AlarmController.getInstance().pushNotification(GatewayBindListeningActivity.this, "", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
            }
            @Override
            public void onFailed(String json) {
            }
        });
    }

    /**
     * 弹出管理验证码输入框
     */
    private void popPasswodWindow() {
        passPopUpWindow = new GatewayPassPopUpWindow(this,
                new GatewayPassPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(String code) {
                        setSecureCode(code);
                    }
                });
        passPopUpWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        passPopUpWindow.etGatewaySecureCode.requestFocus();
        passPopUpWindow.update();

        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.showSoftInput(passPopUpWindow.etGatewaySecureCode, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 验证主机安全码
     */
    private void setSecureCode(String code) {
        GatewayController.getInstance().verifyAdminPassword(this, gatewayInfo.getIp(),
                Md5Encrypt.stringMD5(code), new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            //后台代码才有
//                            JSONObject jsonObject=new JSONObject(Json);
//                            int ret=jsonObject.getInt("ret");
//                            String msg=jsonObject.getString("msg");
//                            if(ret!=0){
//                                ToastHelper.showShortMsg(msg);
//                                return;
//                            }

                            AdminInfo adminInfo = new Gson().fromJson(Json, AdminInfo.class);
                            if (adminInfo.getStatus() == 0) {
                                add2HostFirst(Constant.USERID, gatewayInfo.getHostId());
                            } else {
                                toastUtils.showErrorWithStatus(adminInfo.getStatusinfo());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        Log.d("json",json);
                    }
                });
    }

    /**
     * 绑定主机
     */
    private void bindGateway() {
        toastUtils.showProgress("正在绑定...");
        GatewayController.getInstance().bindGateway(this, gatewayInfo.getHostId(),
                Constant.USERID, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                ToastHelper.showShortMsg("恭喜您成为管理员");
                                toastUtils.dismiss();
//                                LocalBroadcastManager.getInstance(GatewayBindListeningActivity.this).sendBroadcast(new Intent()
//                                        .setAction(FamilyManageListeningActivity.ACTION_MIDIFY_HOSTNAME_REMARK));
                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (Exception e) {
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

    private void add2HostFirst(String userId, String hostId) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("舒适生活").append(",家庭安全").append(",健康生活").append(",绿色生活")
                .append(",设备管理").append(",场景管理").append(",联动管理");
        FamilyManageController.getInstance().addUser(this, userId, hostId, stringBuffer.toString(), "0", "0", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    bindGateway();
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });

    }
}
