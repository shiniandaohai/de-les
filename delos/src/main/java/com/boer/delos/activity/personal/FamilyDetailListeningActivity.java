package com.boer.delos.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.MyHomeAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.ChangeAdminPopWindow;
import com.boer.delos.view.popupWindow.ResetGatewayCodePopUpWindow;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.List;

/**
 * My Home 目前未用到
 */
public class FamilyDetailListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private Host host;
    private TextView tvGateway;
    private TextView tvGatewayName;
    private EditText etRemark;
    private String code, newCode;
    private com.zhy.android.percent.support.PercentLinearLayout llResetCode;
    private com.zhy.android.percent.support.PercentLinearLayout llAdmin;
    private ListView lvMember;
    private ChangeAdminPopWindow changeAdminPopWindow;
    private ResetGatewayCodePopUpWindow phonePopUpWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_family_detail, null);
        setContentView(view);

        initView();
        initData();
    }

    private void initView() {
        host = (Host) getIntent().getSerializableExtra(Constant.KEY_HOST);
        if (host != null) {
            initTopBar(host.getName(), null, true, false);
        }
        tvGateway = (TextView) findViewById(R.id.tvGateway);
        tvGatewayName = (TextView) findViewById(R.id.tvGatewayName);
        etRemark = (EditText) findViewById(R.id.etRemark);
        llResetCode = (PercentLinearLayout) findViewById(R.id.llResetCode);
        lvMember = (ListView) findViewById(R.id.lvMember);
        llAdmin = (PercentLinearLayout) findViewById(R.id.llAdmin);

        llAdmin.setOnClickListener(this);
        llResetCode.setOnClickListener(this);
    }

    private void initData() {
        if (host != null) {
            tvGateway.setText(host.getHostId());
            tvGatewayName.setText(host.getName());
            if (host.getFamilies() != null && host.getFamilies().size() > 0) {
//                MemberAdapter adapter = new MemberAdapter(FamilyDetailListeningActivity.this,host.getFamilies());
                MyHomeAdapter adapter = new MyHomeAdapter(getLayoutInflater(), host.getFamilies());
                lvMember.setAdapter(adapter);
            }

//            tvRemark.setText(host.getName());
        }
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("family", host.getFamilies().get(position));
                Intent intent = new Intent(FamilyDetailListeningActivity.this, AdminChangeAuthorityListeningActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llResetCode:
                clickResetadminCode();
                break;
            case R.id.llAdmin:

                changeNewAdmin();
                break;
        }
    }

    
    private void clickResetadminCode() {
        if (phonePopUpWindow ==null)
        phonePopUpWindow = new ResetGatewayCodePopUpWindow(this, new ResetGatewayCodePopUpWindow.ClickResultListener() {
            @Override
            public void ClickResult(String c1, String c2) {

                code = c1;
                newCode = c2;

                resetAdminCode();
            }
        });

        phonePopUpWindow.clearEditTextView();
        phonePopUpWindow.showAtLocation(llResetCode, Gravity.CENTER, 0, 0);
    }
    private void resetAdminCode() {
        if (StringUtil.isEmpty(code)) {
            Toast.makeText(getApplicationContext(), "请输入旧安全码", Toast.LENGTH_LONG).show();
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
        String ip = null;
        for (GatewayInfo info : gatewayInfos) {
            if (info.getHostId().equals(Constant.CURRENTHOSTID)) continue;
            ip = info.getIp();
        }
        if (null == ip) return;
        toastUtils.showProgress("正在进行中");

        //验证主机管理员密码
        modifyAdminSecureCode(code, ip);

    }

    private void modifyAdminSecureCode(final String code, final String ip) {

        GatewayController.getInstance().verifyAdminPassword(this, ip,
                Md5Encrypt.stringMD5(code), new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            L.i("FamilyDetailListeningActivity modifyAdminSecureCode onSuccess" + code + "=" + ip + "== " + Json);

                            HostResult result = new Gson().fromJson(Json, HostResult.class);
                            if (result.getRet() == 0) {
                                //重置安全码接口
                                String hostId = host.getHostId();
                                if (null == hostId) {
                                    toastUtils.showErrorWithStatus("更改失败");
                                    return;
                                }
                                changeNewAdminCode(hostId, newCode);

                            } else {
                                toastUtils.showErrorWithStatus(result.getMsg());
//                                phonePopUpWindow.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils!=null)
                        toastUtils.showErrorWithStatus(json);
//                        toastUtils.dismiss();
//                        phonePopUpWindow.dismiss();
                        L.i("FamilyDetailListeningActivity modifyAdminSecureCode onFailed " + code + "=" + ip + "== " + json);
                    }
                });
    }

    private void changeNewAdminCode(String hostId, String password) {

        GatewayController.getInstance().modifyAdminPassword(this, hostId, Md5Encrypt.stringMD5(password), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i("FamilyDetailListeningActivity changeNewAdminCode onSuccess " + Json);

                BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showInfoWithStatus(result.getMsg());
                    return;
                }
                phonePopUpWindow.dismiss();
                toastUtils.showSuccessWithStatus(getResources().getString(R.string.modify_admin_code_fail));
            }

            @Override
            public void onFailed(String Json) {

                L.i("FamilyDetailListeningActivity changeNewAdminCode onFailed " + Json);
            }
        });
    }

    /**
     * 修改管理员权限
     */
    private void changeNewAdmin() {
        if (null == host)
            return;
//            toastUtils.showErrorWithStatus("请稍等");
        if (changeAdminPopWindow ==null)
         changeAdminPopWindow = new ChangeAdminPopWindow(this, host, new ChangeAdminPopWindow.ResultCallBack() {
            @Override
            public void resultCallBack(String newAdminId) {
//                toastUtils.showInfoWithStatus("哈哈哈");

                toastUtils.showProgress("正在进行中");
                if (Constant.USERID == null) return;
                FamilyManageController.getInstance().adminPessionTranfer(FamilyDetailListeningActivity.this, Constant.USERID, host.getId(), newAdminId, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        HostResult result = new Gson().fromJson(Json, HostResult.class);
                        changeAdminPopWindow.dismiss();
                        toastUtils.dismiss();

                        if (result.getRet() == 0) {
                            toastUtils.showSuccessWithStatus(getResources().getString(R.string.pop_change_admin_success));
                        } else {
                            toastUtils.showErrorWithStatus(result.getMsg());

                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
            }
        });
        changeAdminPopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
