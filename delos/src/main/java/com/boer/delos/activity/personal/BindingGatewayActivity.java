package com.boer.delos.activity.personal;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.adapter.BindingGatewayAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.AdminInfo;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.User;
import com.boer.delos.model.UserResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.view.popupWindow.GatewayBindPopUpWindow;
import com.boer.delos.view.popupWindow.GatewayPassPopUpWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author: sunzhibin delos
 * @E-mail:
 * @Description: 添加主机 界面
 * @CreateDate: 2017/3/30 0030 16:05
 * @Modify:delos
 * @ModifyDate:
 */


public class BindingGatewayActivity extends CommonBaseActivity {
    @Bind(R.id.lv_gateway)
    ListView mLvGateway;

    private List<GatewayInfo> mGatewayLists;
    private BindingGatewayAdapter mAdapter;
    private GatewayInfo mSelectGatewayInfo;

    //选中主机的IP
    private String mGatewayIp = "";
    private GatewayPassPopUpWindow passPopUpWindow;
    private User adminInfo;

    private List<String> mGatewayExists;//用户已有的主机Id
    private GatewayBindPopUpWindow gatewayBindPopUpWindow;

    @Override
    protected int initLayout() {
        return R.layout.activity_add_gateway;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.text_add_gateway));
        tlTitleLayout.setLinearRightImage(R.mipmap.ic_nav_refresh);
    }

    @Override
    protected void initData() {
        mGatewayExists = new ArrayList<>();
        mGatewayLists = new ArrayList<>();
        List<String> tempList = getIntent().getStringArrayListExtra("hosts");
        if (tempList != null) {
            //用户已经加入的主机 传入的Ids
            mGatewayExists.addAll(tempList);

        }

        mAdapter = new BindingGatewayAdapter(this, mGatewayLists, R.layout.item_gateway_binding);
        mAdapter.setDatas(mGatewayLists);
        mLvGateway.setAdapter(mAdapter);

        toastUtils.showProgress(getString(R.string.toast_scan_gateway));
        startUDPGetGateway();
    }

    @Override
    protected void initAction() {
        mLvGateway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectGatewayInfo = mGatewayLists.get(position);
                mGatewayIp = mSelectGatewayInfo.getIp();

                requestHostAdmin(mSelectGatewayInfo.getHostId());

            }
        });
    }

    /**
     * 查询主机管理员
     */
    private void requestHostAdmin(String hostId) {
        FamilyManageController.getInstance().adminInfoWithHostId(this, hostId,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Loger.d("GatewayBindActivity requestHostAdmin " + Json);
                        try {
                            UserResult result = new Gson().fromJson(Json, UserResult.class);
                            if (result.getRet() != 0) {
                                //无管理员
                                if (result.getRet() == 20020) {
                                    showPINPopupWindow();
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
                        if (toastUtils != null) {
                            toastUtils.showErrorWithStatus(getString(R.string.toast_error_net));
                        }
                        Loger.d("GatewayBindActivity requestHostAdmin " + Json);
                    }
                });
    }

    /**
     * 弹出管理验证码输入框
     */
    private void showPINPopupWindow() {
        passPopUpWindow = new GatewayPassPopUpWindow(this,
                new GatewayPassPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(String code) {
                        verifySecureCode(code);
                    }
                });
        passPopUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        passPopUpWindow.etGatewaySecureCode.requestFocus();
        passPopUpWindow.update();

        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.showSoftInput(passPopUpWindow.etGatewaySecureCode, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 验证主机安全码
     */
    private void verifySecureCode(String PIN) {
        GatewayController.getInstance().verifyAdminPassword(this, mSelectGatewayInfo.getIp(),
                Md5Encrypt.stringMD5(PIN), new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        AdminInfo adminInfo = new Gson().fromJson(Json, AdminInfo.class);
                        if (adminInfo.getStatus() == 0) {

                            add2HostFirst(Constant.USERID, mSelectGatewayInfo.getHostId());
                        } else {
                            toastUtils.showErrorWithStatus(adminInfo.getStatusinfo());
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils != null) {
                            toastUtils.showErrorWithStatus(json);
                        }
                    }
                });
    }


    /**
     * 弹出向管理申请的对话框
     */
    private void popGateBindWindow() {
        if (gatewayBindPopUpWindow != null && gatewayBindPopUpWindow.isShowing()) {
            gatewayBindPopUpWindow.dismiss();
            gatewayBindPopUpWindow = null;
        }
        gatewayBindPopUpWindow = new GatewayBindPopUpWindow(this,
                new GatewayBindPopUpWindow.ClickListener() {
                    @Override
                    public void okClick() {
                        userApply();
                    }
                });
        gatewayBindPopUpWindow.setGatewayTipContent(adminInfo.getMobile());
        gatewayBindPopUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
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
                FamilyManageController.applyStatusApply,
                mSelectGatewayInfo.getHostId(),
                "","","",
                FamilyManageController.statusNormal,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
//                                toastUtils.showErrorWithStatus(getString(R.string.toast_apply_fail));

                            } else {
                                toastUtils.showSuccessWithStatus(getString(R.string.toast_apply_success));

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //判断是否从家人管理过来
                                        if (mGatewayExists == null) {
                                            //跳到首页
                                            startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
                                        }
                                        finish();
                                    }
                                }, 1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }

    private void add2HostFirst(String userId, String hostId) {
        StringBuffer stringBuffer = new StringBuffer();
        //权限
//        stringBuffer.append("舒适生活").append(",家庭安全").append(",健康生活").append(",绿色生活")
//                .append(",设备管理").append(",场景管理").append(",联动管理");
        FamilyManageController.getInstance().addUser(this, userId, hostId, stringBuffer.toString(),    "0","0",new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d(json);
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    bindGateway(mSelectGatewayInfo.getHostId(), Constant.USERID);
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d(json);
            }
        });

    }

    /**
     * 绑定主机
     *
     * @param hostId
     * @param userId
     */
    private void bindGateway(String hostId, String userId) {
        GatewayController.getInstance().bindGateway(this, hostId, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
//                        toastUtils.showErrorWithStatus(result.getMsg());
                        toastUtils.showErrorWithStatus(getString(R.string.toast_binding_fail));
                    } else {
                        toastUtils.showSuccessWithStatus(getString(R.string.toast_binding_success));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null) {
                    toastUtils.showErrorWithStatus(json);
                }
            }
        });
    }

    @Override
    public void rightViewClick() {
        startUDPGetGateway();
    }

    private void startUDPGetGateway() {
        UDPUtils.startUDPBroadCast(new UDPUtils.ScanCallback() {
            @Override
            public void callback() {
                mGatewayLists.clear();
                mGatewayLists.addAll(canAddGateways(Constant.gatewayInfos));
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (toastUtils != null && toastUtils.isShowing()) {
                toastUtils.dismiss();
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 将用户已经加入的的主机进行过滤
     *
     * @param list
     * @return
     */
    private List<GatewayInfo> canAddGateways(List<GatewayInfo> list) {
        List<GatewayInfo> gateways = new ArrayList<>();

        if (list == null || list.size() == 0) {
            return gateways;
        }
        if (mGatewayExists == null || mGatewayExists.size() == 0) {
            return list;
        }
        for (GatewayInfo info : list) {
            if (!mGatewayExists.contains(info.getHostId())) {
                gateways.add(info);
            }
        }
        return gateways;
    }

}
