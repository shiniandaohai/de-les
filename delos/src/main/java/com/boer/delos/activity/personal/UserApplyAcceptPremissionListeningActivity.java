package com.boer.delos.activity.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.GatewayPermissAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Family;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.SystemMessage;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhukang on 16/8/1.
 */
public class UserApplyAcceptPremissionListeningActivity extends BaseListeningActivity implements View.OnClickListener {

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
    private String pession = "";
    private boolean isSelectAll, isComfortLive, isHomeSecurity, isHealthLive,
            isGreenLive, isDeviceManage, isAreaManage, isLinkManage;
    private ListView lvGateWay;
    private SystemMessage.MsgListBean message;
    private List<Map<String, String>> list = new ArrayList<>();
    private GatewayPermissAdapter adapter;
    private String selectHostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_select_premission);
        message = (SystemMessage.MsgListBean) getIntent().getSerializableExtra("message");
        selectHostId = message.getHostRealId();
        initView();
        initData();
    }

    private void initView() {
        initTopBar(R.string.distribute_limit_title, null, true, false);

        lvGateWay = (ListView) findViewById(R.id.lvGateWay);
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
        adapter = new GatewayPermissAdapter(this, list);
        lvGateWay.setAdapter(adapter);
        lvGateWay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clearChecked();
                Map<String, String> map = list.get(i);
                map.put("checked", "1");
                selectHostId = map.get("hostId");
                adapter.notifyDataSetChanged();
            }
        });
        getUserHosts();
    }

    /**
     * 清除所有选中的
     */
    private void clearChecked() {
        for (Map<String, String> map : list) {
            map.put("checked", "0");
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
                if (selectHostId == null) {
                    return;
                }
                //更新申请状态
                updateApplyStatus();
                //加入家人管理
                addFamilyUser();
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.llSelectAll:
                selectAll();
                break;
        }
    }

    /**
     * 更新申请状态
     */
    private void updateApplyStatus() {
        FamilyManageController.getInstance().updateUserApply(this,
                Constant.USERID,
                message.getToUser().getId(),
                message.getExtra().getApplyStatus() + "",
                message.getHostRealId(),
                message.getExtra().getStatus() + "",
                FamilyManageController.statusAdminApply,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Loger.d("UserApplyAcceptPermissionActivity " + Json);
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d("UserApplyAcceptPermissionActivity " + Json);
                    }
                });
    }

    private void addFamilyUser() {
        if (isComfortLive) {
            pession = "舒适生活,";
        }
        if (isHomeSecurity) {
            pession += "家庭安全,";
        }
        if (isHealthLive) {
            pession += "健康生活,";
        }
        if (isGreenLive) {
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
        FamilyManageController.getInstance().addUser(this, message.getToUser().getId(),
                selectHostId, pession,   "0","0", new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                        if (result.getRet() == 0) {
//                            Constant.PERMISSIONS = pession;
//                            updateUserPermission(getApplicationContext(), message.getToUser().getId(), selectHostId, pession);
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
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
        //全部选中
        if (isSelectAll) {
            isComfortLive = true;
            isHomeSecurity = true;
            isHealthLive = true;
            isGreenLive = true;
            isDeviceManage = true;
            isAreaManage = true;
            isLinkManage = true;
        } else {
            isComfortLive = false;
            isHomeSecurity = false;
            isHealthLive = false;
            isGreenLive = false;
            isDeviceManage = false;
            isAreaManage = false;
            isLinkManage = false;
        }
    }


    /**
     * 获取用户是管理员主机列表
     *
     * @return
     */
    public void getUserHosts() {
        FamilyManageController.getInstance().showFamilies(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    HostResult result = new Gson().fromJson(Json, HostResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        List<Map<String, String>> filterList = filterAdminList(result.getHosts());
                        if (filterList.size() > 0) {
                            //判断是否有选择的主机Id
                            //无选择主机
                            if (StringUtil.isEmpty(selectHostId)) {
                                filterList.get(0).put("checked", "1");
                                selectHostId = filterList.get(0).get("hostId");
                            }
                            //有选择的主机
                            else {
                                for (Map<String, String> map : filterList) {
                                    String hostId = map.get("hostId");
                                    if (hostId.equals(selectHostId)) {
                                        map.put("checked", "1");
                                        break;
                                    }
                                }
                            }
                            list.clear();
                            list.addAll(filterList);
                            adapter.notifyDataSetChanged();
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
//
//    private void updateUserPermission(Context context, String userId, String hostId, String permission) {
//        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(hostId)) {
//            return;
//        }
//        FamilyManageController.getInstance().updateUserPermission(context, hostId, userId, permission, new RequestResultListener() {
//            @Override
//            public void onSuccess(String json) {
//                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
//                if (result.getRet() == 0) {
//
//                }
//            }
//
//            @Override
//            public void onFailed(String json) {
//
//            }
//        });
//    }

    /**
     * 过滤当前用户为管理员的主机
     *
     * @param hosts
     * @return
     */
    private List<Map<String, String>> filterAdminList(List<Host> hosts) {
        List<Map<String, String>> list = new ArrayList<>();
        for (Host host : hosts) {
            for (Family family : host.getFamilies()) {
                //当前用户为管理员的主机
                if (family.getAdmin() == 1 && Constant.USERID.equals(family.getUserId())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("hostId", family.getHostId());
                    map.put("hostName", host.getName());
                    map.put("checked", "0");
                    list.add(map);
                    break;
                }
            }
        }
        return list;
    }
}
