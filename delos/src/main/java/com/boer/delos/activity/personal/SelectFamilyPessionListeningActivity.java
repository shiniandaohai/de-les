package com.boer.delos.activity.personal;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Host;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentRelativeLayout;

/**
 *@Description: 分配权限界面
 */
public class SelectFamilyPessionListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private PercentRelativeLayout llComfortLive;
    private PercentRelativeLayout llHomeSecurity;
    private PercentRelativeLayout llHealthLive;
    private PercentRelativeLayout llGreenLive;
    private PercentRelativeLayout llDeviceManage;
    private PercentRelativeLayout llAreaManage;
    private PercentRelativeLayout llLinkManage;
    private ImageView ivComfortLiveChecked;
    private ImageView ivHomeSecurityChecked;
    private ImageView ivHealthLiveChecked;
    private ImageView ivGreenLiveChecked;
    private ImageView ivDeviceManageChecked;
    private ImageView ivAreaManageChecked;
    private ImageView ivLinkManageChecked;
    private ImageView ivSelectAll;
    private LinearLayout llSelectAll;

    private TextView tvConfirmLimitBtn;// "确定"按钮
    private TextView tvGateway;
    private User user;
    private Host host;
    private String hostId = "";
    private String userId = "";
    private String pession = "";
    private boolean isSelectAll, isComfortLive, isHomeSecurity, isHealthLive,
            isGreenLive, isDeviceManage, isAreaManage, isLinkManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_family_pession);
        initView();
        initData();
    }

    private void initView() {
        initTopBar(R.string.distribute_limit_title, null, true, false);
        user = (User) getIntent().getSerializableExtra(Constant.KEY_USER);
        host = (Host) getIntent().getSerializableExtra(Constant.KEY_HOST);
        tvGateway = (TextView) findViewById(R.id.tvGateway);
        ivSelectAll = (ImageView) findViewById(R.id.ivSelectAll);
        llSelectAll = (LinearLayout) findViewById(R.id.llSelectAll);
        this.tvConfirmLimitBtn = (TextView) findViewById(R.id.tvConfirmLimitBtn);
        this.llLinkManage = (PercentRelativeLayout) findViewById(R.id.llLinkManage);
        this.ivLinkManageChecked = (ImageView) findViewById(R.id.ivLinkManageChecked);
        this.llAreaManage = (PercentRelativeLayout) findViewById(R.id.llAreaManage);
        this.ivAreaManageChecked = (ImageView) findViewById(R.id.ivAreaManageChecked);
        this.llDeviceManage = (PercentRelativeLayout) findViewById(R.id.llDeviceManage);
        this.ivDeviceManageChecked = (ImageView) findViewById(R.id.ivDeviceManageChecked);
        this.llGreenLive = (PercentRelativeLayout) findViewById(R.id.llGreenLive);
        this.ivGreenLiveChecked = (ImageView) findViewById(R.id.ivGreenLiveChecked);
        this.llHealthLive = (PercentRelativeLayout) findViewById(R.id.llHealthLive);
        this.ivHealthLiveChecked = (ImageView) findViewById(R.id.ivHealthLiveChecked);
        this.llHomeSecurity = (PercentRelativeLayout) findViewById(R.id.llHomeSecurity);
        this.ivHomeSecurityChecked = (ImageView) findViewById(R.id.ivHomeSecurityChecked);
        this.llComfortLive = (PercentRelativeLayout) findViewById(R.id.llComfortLive);
        this.ivComfortLiveChecked = (ImageView) findViewById(R.id.ivComfortLiveChecked);

        this.llComfortLive.setOnClickListener(this);
        this.llHealthLive.setOnClickListener(this);
        this.llGreenLive.setOnClickListener(this);
        this.llHomeSecurity.setOnClickListener(this);
        this.llAreaManage.setOnClickListener(this);
        this.llDeviceManage.setOnClickListener(this);
        this.llLinkManage.setOnClickListener(this);
        this.tvConfirmLimitBtn.setOnClickListener(this);
        llSelectAll.setOnClickListener(this);
    }

    private void initData() {
        if (host != null) {
            tvGateway.setText(host.getName());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llComfortLive:
                if (isComfortLive) {
                    isComfortLive = false;
                    ivComfortLiveChecked.setVisibility(View.GONE);
                } else {
                    isComfortLive = true;
                    ivComfortLiveChecked.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.llHealthLive:
                if (isHealthLive) {
                    isHealthLive = false;
                    ivHealthLiveChecked.setVisibility(View.GONE);
                } else {
                    isHealthLive = true;
                    ivHealthLiveChecked.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.llGreenLive:
                if (isGreenLive) {
                    isGreenLive = false;
                    ivGreenLiveChecked.setVisibility(View.GONE);
                } else {
                    isGreenLive = true;
                    ivGreenLiveChecked.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.llHomeSecurity:
                if (isHomeSecurity) {
                    isHomeSecurity = false;
                    ivHomeSecurityChecked.setVisibility(View.GONE);
                } else {
                    isHomeSecurity = true;
                    ivHomeSecurityChecked.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.llAreaManage:
                if (isAreaManage) {
                    isAreaManage = false;
                    ivAreaManageChecked.setVisibility(View.GONE);
                } else {
                    isAreaManage = true;
                    ivAreaManageChecked.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.llDeviceManage:
                if (isDeviceManage) {
                    isDeviceManage = false;
                    ivDeviceManageChecked.setVisibility(View.GONE);
                } else {
                    isDeviceManage = true;
                    ivDeviceManageChecked.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.llLinkManage:
                if (isLinkManage) {
                    isLinkManage = false;
                    ivLinkManageChecked.setVisibility(View.GONE);
                } else {
                    isLinkManage = true;
                    ivLinkManageChecked.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tvConfirmLimitBtn:
                addFamilyUser();
                break;
            case R.id.llSelectAll:
                selectAll();
                break;
        }
    }

    private void addFamilyUser() {
        if (user != null && !StringUtil.isEmpty(user.getId())) {
            userId = user.getId();
        }
        if (host != null && !StringUtil.isEmpty(host.getId())) {
            hostId = host.getId();
        }
        if (isComfortLive) {
            pession = "舒适生活,";
        }
        if (isHomeSecurity) {
            pession += "家庭安全,";
        }
        if (isHealthLive) {
            pession += "绿色生活,";
        }
        if (isDeviceManage) {
            pession += "设备管理,";
        }
        if (isAreaManage) {
            pession += "场景管理,";
        }
        if (isLinkManage) {
            pession += "联动管理";
        }
        if (isSelectAll) {
            StringBuffer sb = new StringBuffer();
            sb.append("舒适生活,");
            sb.append("家庭安全,");
            sb.append("绿色生活,");
            sb.append("设备管理,");
            sb.append("场景管理,");
            sb.append("联动管理");
            pession = sb.toString();
        }
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(hostId)) {
            invatationUserAddIn(userId, hostId);
        }

    }

    private void selectAll() {
        if (isSelectAll) {
            isSelectAll = false;
            ivComfortLiveChecked.setVisibility(View.GONE);
            ivHomeSecurityChecked.setVisibility(View.GONE);
            ivHealthLiveChecked.setVisibility(View.GONE);
            ivGreenLiveChecked.setVisibility(View.GONE);
            ivDeviceManageChecked.setVisibility(View.GONE);
            ivAreaManageChecked.setVisibility(View.GONE);
            ivLinkManageChecked.setVisibility(View.GONE);
            ivSelectAll.setVisibility(View.GONE);
        } else {
            isSelectAll = true;
            ivComfortLiveChecked.setVisibility(View.VISIBLE);
            ivHomeSecurityChecked.setVisibility(View.VISIBLE);
            ivHealthLiveChecked.setVisibility(View.VISIBLE);
            ivGreenLiveChecked.setVisibility(View.VISIBLE);
            ivDeviceManageChecked.setVisibility(View.VISIBLE);
            ivAreaManageChecked.setVisibility(View.VISIBLE);
            ivLinkManageChecked.setVisibility(View.VISIBLE);
            ivSelectAll.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 邀请用户
     *
     * @param applyUserId
     * @param hostId
     */
    private void invatationUserAddIn(String applyUserId, String hostId) {
        FamilyManageController.getInstance().userApplyToAdmin(this,
                Constant.USERID, applyUserId, "","","", FamilyManageController.applyStatusShare,
                hostId, pession, FamilyManageController.statusNormal,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                toastUtils.showSuccessWithStatus("邀请信息已发送");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }, 2000);
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
}
