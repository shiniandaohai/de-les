package com.boer.delos.activity.personal;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.settings.NetGatePermisionSettingActivity;
import com.boer.delos.adapter.MyHomeAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Family;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.User;
import com.boer.delos.model.VertifyAdminResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.ChangeAdminPopWindow;
import com.boer.delos.view.popupWindow.ResetGatewayCodePopUpWindow;
import com.boer.delos.view.popupWindow.ShowRequestEditPopupWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 我家
 * 管理员管理家庭成员权限 界面
 */

public class MyHomeListeningActivity extends CommonBaseActivity
        implements View.OnClickListener {
    private TextView tvGatewayidMyhome;
    private TextView tvGatewaynameMyhome;
    private TextView tvRemarkMyhome;
    private RelativeLayout rl_reset_gateway_password_myhome;
    private RelativeLayout rlChangeAdimn;
    private ListView lvAdimnFamilies;

    private ArrayList<Family> familyArrayList;
    private MyHomeAdapter adapter;
    private Host mHost;
    private String code, newCode;
    private boolean needLoading = false;
    private ChangeAdminPopWindow changeAdminPopWindow;
    private ResetGatewayCodePopUpWindow resetGatewayCodePopUpWindow;
    private Family hostAdminFamily = null; // 主机管理员

    private ShowRequestEditPopupWindow showRequestNameEditPopupWindow;
    private ShowRequestEditPopupWindow showRequestAliaEditPopupWindow;


    @Override
    protected int initLayout() {
        return R.layout.activity_myhome;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(R.string.family_manage_my_gateway);
        initView1();

    }

    @Override
    protected void initData() {

        initData1();

    }

    @Override
    protected void initAction() {
        initLisener();
    }


    private void initView1() {

        tvGatewayidMyhome = (TextView) findViewById(R.id.tv_gatewayid_myhome);
        tvGatewaynameMyhome = (TextView) findViewById(R.id.et_gatewayname_myhome);
        tvRemarkMyhome = (TextView) findViewById(R.id.editview_remark_myhome);

        rl_reset_gateway_password_myhome = (RelativeLayout) findViewById(R.id.rl_reset_gateway_password_myhome);
        rlChangeAdimn = (RelativeLayout) findViewById(R.id.rl_change_adimn);
        lvAdimnFamilies = (ListView) findViewById(R.id.lv_adimn_families);
        rlChangeAdimn.setOnClickListener(this);
        rl_reset_gateway_password_myhome.setOnClickListener(this);
        tvGatewaynameMyhome.setOnClickListener(this);
        tvRemarkMyhome.setOnClickListener(this);

    }

    private void initLisener() {

        lvAdimnFamilies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Family family = familyArrayList.get(position);
                User user = familyArrayList.get(position).getUser();
                Intent intent = new Intent(MyHomeListeningActivity.this, NetGatePermisionSettingActivity.class);
                User us = new User();
                us.setName(user.getName());
                us.setId(user.getId());
                us.setAvatarUrl(user.getAvatarUrl());
                intent.putExtra("type",1);
                intent.putExtra("user", us);
                intent.putExtra("host", family.getHostId());
                startActivityForResult(intent, 1000);
            }
        });
    }

    private void initData1() {

        showRequestNameEditPopupWindow = new ShowRequestEditPopupWindow(this, tlTitleLayout, getResources().getString(R.string.pop_family_net_gate_name_title));
        showRequestAliaEditPopupWindow = new ShowRequestEditPopupWindow(this, tlTitleLayout, getResources().getString(R.string.pop_family_net_gate_alia_title));

        familyArrayList = new ArrayList<>();
        adapter = new MyHomeAdapter(getLayoutInflater(), familyArrayList);
        lvAdimnFamilies.setAdapter(adapter);

        mHost = (Host)getIntent().getSerializableExtra("host");
        if(mHost != null){
            try{
            familyArrayList.clear();
            familyArrayList.addAll(mHost.getFamilies());
            familyArrayList.remove(0);//去掉管理员
            familyArrayList.remove(familyArrayList.size()-1);
            if (familyArrayList.size() == 0)
                return;
            adapter.setData(familyArrayList);
            adapter.notifyDataSetChanged();

            for (Family f : mHost.getFamilies()) {

                if (f.getAdmin() == 1) {
                    hostAdminFamily = f;
                    String hostAlias = f.getHostAlias();
                    if (!TextUtils.isEmpty(hostAlias))
                        tvRemarkMyhome.setText(hostAlias);
                    tvGatewaynameMyhome.setText(mHost.getName());
                    tvGatewayidMyhome.setText(mHost.getHostId());

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        //queryHostinfo();

    }

    private void queryHostinfo() {

        GatewayController.getInstance().queryAllHost(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    toastUtils.dismiss();
                    HostResult result = new Gson().fromJson(Json, HostResult.class);

                    List<Host> hostList = result.getHosts();
                    String currentHostId = result.getCurrentHostId();
                    for (Host host : hostList) {

                        if (host.getHostId().equals(currentHostId)) {
                            mHost = host;
                            break;
                        }

                    }

                    familyArrayList.clear();
                    familyArrayList.addAll(mHost.getFamilies());
                    familyArrayList.remove(0);//去掉管理员
                    if (familyArrayList.size() == 0)
                        return;
                    adapter.setData(familyArrayList);
                    adapter.notifyDataSetChanged();

                    for (Family f : mHost.getFamilies()) {

                        if (f.getAdmin() == 1) {
                            hostAdminFamily = f;
                            String hostAlias = f.getHostAlias();
                            if (!TextUtils.isEmpty(hostAlias))
                                tvRemarkMyhome.setText(hostAlias);
                            tvGatewaynameMyhome.setText(mHost.getName());
                            tvGatewayidMyhome.setText(mHost.getHostId());

                            break;
                        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_reset_gateway_password_myhome:
                hideInput();
                clickResetCode();
                break;
            case R.id.rl_change_adimn:
                hideInput();
                changeNewAdmin();
                break;

            case R.id.et_gatewayname_myhome:

                reName();

                break;

            case R.id.editview_remark_myhome:

                reAlia();
                break;

        }
    }

    private void clickResetCode() {
        resetGatewayCodePopUpWindow = new ResetGatewayCodePopUpWindow(this);
        resetGatewayCodePopUpWindow.setClickResultListener(new ResetGatewayCodePopUpWindow.ClickResultListener() {
            @Override
            public void ClickResult(String c1, String c2) {
                code = c1;
                newCode = c2;
                resetGatewayCode();
            }
        });
        resetGatewayCodePopUpWindow.showAtLocation(llayoutContent, Gravity.CENTER, 0, 0);
    }

    //重置主机安全码
    private void resetGatewayCode() {

        if (StringUtil.isEmpty(code)) {
            Toast.makeText(getApplicationContext(), "请输入旧安全码", Toast.LENGTH_LONG).show();
            return;
        }
        if (!code.equals(Constant.LOGIN_PASSWORD)) {
            Toast.makeText(getApplicationContext(), "旧密码错误", Toast.LENGTH_LONG).show();
            return;
        }
        if (StringUtil.isEmpty(newCode)) {
            Toast.makeText(getApplicationContext(), "请输入新安全码", Toast.LENGTH_LONG).show();
            return;
        }
        if (newCode.length() < 5) {
            Toast.makeText(getApplicationContext(), "新安全码不能少于5位", Toast.LENGTH_LONG).show();
            return;
        }

        List<GatewayInfo> gatewayInfos = Constant.gatewayInfos;
//        String ip = null;
//        for (GatewayInfo info : gatewayInfos) {
//            if (info.getHostId().equals(Constant.CURRENTHOSTID)) continue;
//            ip = info.getIp();
//        }
//        if (null == ip) return;
        //验证主机管理员密码
            setSecureCode(code, Constant.LOCAL_CONNECTION_IP);
    }
//

    /**
     * 验证主机安全码
     */
    private void setSecureCode(String code, String ip) {
        GatewayController.getInstance().verifyAdminPassword(this, ip,
                Md5Encrypt.stringMD5(code), new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            VertifyAdminResult result = new Gson().fromJson(Json, VertifyAdminResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus("主机安全码验证失败");
                            }
                            VertifyAdminResult.ResponseBean responseBean = result.getResponse();
                            if (responseBean == null) {
                                toastUtils.showErrorWithStatus("主机安全码验证失败");
                                return;
                            }
                            if (responseBean.getStatus() == 0) {//验证成功
                                String hostId = mHost.getHostId();
                                if (null == hostId) return;
                                changeNewAdminCode(hostId, newCode);
                            } else {
                                toastUtils.showErrorWithStatus(responseBean.getStatusinfo());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        L.i(json);
                    }
                });
    }

    /**
     * 更改新的安全码
     *
     * @param hostId
     * @param password
     */
    private void changeNewAdminCode(String hostId, String password) {

        GatewayController.getInstance().modifyAdminPassword(MyHomeListeningActivity.this, hostId, Md5Encrypt.stringMD5(password), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showInfoWithStatus(result.getMsg());
                    return;
                }
                resetGatewayCodePopUpWindow.dismiss();
                toastUtils.showSuccessWithStatus(getResources().getString(R.string.modify_admin_code_fail));
            }

            @Override
            public void onFailed(String Json) {
                if (toastUtils != null)
                    toastUtils.showInfoWithStatus(Json + "更改失败，请重试");
            }
        });
    }

    /**
     * 更改管理员
     */
    private void changeNewAdmin() {
        if (changeAdminPopWindow != null && changeAdminPopWindow.isShowing()) {
            changeAdminPopWindow.dismiss();
            changeAdminPopWindow = null;
        }
        if (familyArrayList.size()<1){
            toastUtils.showInfoWithStatus(getString(R.string.toast_nor_member_transfer));
            return; // add by sunzhibin
        }
        changeAdminPopWindow = new ChangeAdminPopWindow(this, mHost, new ChangeAdminPopWindow.ResultCallBack() {
            @Override
            public void resultCallBack(String newAdminId) {
//                toastUtils.showInfoWithStatus(newAdminId);
                if (Constant.USERID == null) return;
                FamilyManageController.getInstance().adminPessionTranfer(MyHomeListeningActivity.this, Constant.USERID, mHost.getId(), newAdminId, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            HostResult result = new Gson().fromJson(Json, HostResult.class);
                            if (result.getRet() == 0) {
                                toastUtils.showSuccessWithStatus(getResources().getString(R.string.pop_change_admin_success));
                                changeAdminPopWindow.dismiss();
//                                new Timer().schedule(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        toastUtils.showProgress("正在同步数据");
//                                        queryHostinfo();
//                                    }
//                                }, 1000);
                                ToastHelper.showShortMsg(getResources().getString(R.string.pop_change_admin_success));
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                toastUtils.showErrorWithStatus(result.getMsg());

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
        });
        changeAdminPopWindow.showAtLocation(llayoutContent, Gravity.CENTER, 0, 0);
    }

    /**
     * 更新主机别名
     *
     * @param host
     * @param hostAlias
     */
    private void upDataHostAlias(Host host, String hostAlias) {


        if (host == null) {
            return;
        }

        toastUtils.showProgress("");

        String hostId = host.getHostId();
        String userId = Constant.USERID;
        String userAlias = Constant.LOGIN_USER.getRemark();
        MemberController.getInstance().updateAlias(this, hostId, userId, userAlias, hostAlias, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() == 0) {
//                        toastUtils.showSuccessWithStatus("修改成功");
                        ToastHelper.showShortMsg("修改成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {
                toastUtils.dismiss();
            }
        });
    }

    private void upDataHostName(Host host, String string) {

        if (host == null) {
            return;
        }
        if (StringUtil.isEmpty(string)) {
            return;
        }


        toastUtils.showProgress("");

        String hostId = host.getHostId();
        MemberController.getInstance().updataGatewayName(this, hostId, string, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                LocalBroadcastManager.getInstance(MyHomeListeningActivity.this).sendBroadcast(new Intent()
                .setAction(FamilyManageListeningActivity.ACTION_MIDIFY_HOSTNAME_REMARK));
            }

            @Override
            public void onFailed(String Json) {
            }
        });
    }

    public void reName() {

        showRequestNameEditPopupWindow.setShowRequestPopupWindow(new ShowRequestEditPopupWindow.IShowRequest() {
                                                                     @Override
                                                                     public void rightButtonClick(String name) {

                                                                         hideInput();

                                                                         tvGatewaynameMyhome.setText(name);

                                                                         upDataHostName(mHost, tvGatewaynameMyhome.getText().toString());
                                                                     }
                                                                 }

        );
        showRequestNameEditPopupWindow.showPopupWindow();


    }


    public void reAlia() {

        showRequestAliaEditPopupWindow.setShowRequestPopupWindow(new ShowRequestEditPopupWindow.IShowRequest() {
            @Override
            public void rightButtonClick(String name) {

                hideInput();

                tvRemarkMyhome.setText(name);

                upDataHostAlias(mHost, tvRemarkMyhome.getText().toString());
            }
        });
        showRequestAliaEditPopupWindow.showPopupWindow();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {


        }

    }
}
