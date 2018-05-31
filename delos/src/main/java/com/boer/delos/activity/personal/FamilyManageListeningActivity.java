package com.boer.delos.activity.personal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;

import com.allen.expandablelistview.SwipeMenuExpandableCreator;
import com.allen.expandablelistview.SwipeMenuExpandableListView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.boer.delos.R;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.adapter.FamilyManageAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.fragment.MainFragment;
import com.boer.delos.model.ApplyInfosResult;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Family;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.service.PollService;
import com.boer.delos.udp.UDPUtils;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.TitleLayout;
import com.boer.delos.view.popupWindow.HealthSharePopUpWindow;
import com.boer.delos.view.popupWindow.RestorePopUpWindow;
import com.google.gson.Gson;

import org.linphone.squirrel.squirrelCallImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * @author PengJiYang
 * @Description: "家庭管理"界面
 * create at 2016/4/12 9:30
 */
public class FamilyManageListeningActivity extends CommonBaseActivity implements TitleLayout.titleLayoutClick {

    @Bind(R.id.tl_title_layout)
    TitleLayout tlTitleLayout;
    private SwipeMenuExpandableListView elvFamilyManage;
    private List<Host> hostList = new ArrayList<>();
    private FamilyManageAdapter adapter;
    //主机是否在线的hashMap
    private Map<String, String> onlineMap = new HashMap<>();

    private String hostId = "";// 主机Id
    private RestorePopUpWindow mRestorePopUpWindow;
    private static boolean IS_SHOW_PROGRESSBAR = false;
    private int COUNT = 0; //测试主机在不在线， 结果返回的次数
    private squirrelCallImpl squirrelCall = squirrelCallImpl.getInstance();
    private boolean isNeedCheckGateway = true;

    View view;


    @Override
    protected int initLayout() {
        return R.layout.activity_family_manage_listening;
    }

    @Override
    protected void initView() {
        initView1();
        mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_MIDIFY_HOSTNAME_REMARK);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void initData() {
        initData1();
    }

    @Override
    protected void initAction() {
        iniListener();
    }


    private void initView1() {
        tlTitleLayout.setTitle(R.string.family_manage_title);
        tlTitleLayout.setLinearRightImage(R.mipmap.ic_nav_home_add);
        view = tlTitleLayout;
//        tlTitleLayout.setLinearLeftImage(R.mipmap.back);


        elvFamilyManage = (SwipeMenuExpandableListView) findViewById(R.id.elvFamilyManage);
        SwipeMenuExpandableCreator creator = new SwipeMenuExpandableCreator() {
            @Override
            public void createGroup(SwipeMenu menu) {
//                createDismissMenu(menu);
                SwipeMenuItem item = new SwipeMenuItem(getApplicationContext());
//                item.setBackground(R.drawable.bg_stroke_red);
                item.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x00, 0x00)));
                item.setWidth(DensityUitl.dip2px(getApplicationContext(), 120));
                item.setTitle("解绑网关");
                item.setTitleSize(16);
                item.setTitleColor(Color.parseColor("#FFFFFF"));
                menu.addMenuItem(item);
            }

            @Override
            public void createChild(SwipeMenu menu) {
                createDismissMenu(menu);
            }

            //创建解绑按钮
            private void createDismissMenu(SwipeMenu menu) {
                SwipeMenuItem item = new SwipeMenuItem(getApplicationContext());
                item.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x00, 0x00)));
                item.setWidth(DensityUitl.dip2px(getApplicationContext(), 60));
                item.setTitle(getString(R.string.delete));
                item.setTitleSize(16);
                item.setTitleColor(Color.parseColor("#FFFFFF"));
                menu.addMenuItem(item);
            }
        };
        elvFamilyManage.setMenuCreator(creator);
    }

    private void initData1() {
        if (adapter == null) {
            adapter = new FamilyManageAdapter(FamilyManageListeningActivity.this, hostList, new FamilyManageAdapter.ClickListener() {
                @Override
                public void changeHostClick(Host host) {
                    //切换主机
                    final String isOnline = onlineMap.get(host.getHostId());
                    if (isOnline != null || "1".equals(isOnline)) {
                        changeHost(host);
                    }
                    else{
                        ToastHelper.showShortMsg("该主机不在线");
                    }
                }
            });

            adapter.setOnClick2ShareListener(new FamilyManageAdapter.OnClick2ShareListener() {

                @Override
                public void clickShareData(int flag) {
                    int childCount=adapter.getChildrenCount(flag);
                    if(childCount<=2){
                        ToastHelper.showShortMsg("暂无用户可分享");
                    }
                    else{
                        boolean hasUnconfirmFamily=false;
                        for(Family family:hostList.get(flag).getFamilies()){
                            if (0 == family.getApplyStatus()) {
                                hasUnconfirmFamily=true;
                                break;
                            }
                        }
                        if(hasUnconfirmFamily){
                            heathyShare(flag);
                        }
                        else{
                            ToastHelper.showShortMsg("暂无用户可分享");
                        }

                    }
                }
            });
        }
        elvFamilyManage.setAdapter(adapter);


        getFamilyData();

    }

    private void iniListener() {
        elvFamilyManage.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//

                return false;
            }
        });

        elvFamilyManage.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (childPosition == hostList.get(groupPosition).getFamilies().size() - 1) {
                    if (adapter.getChild(groupPosition, childPosition).getUserId().equals("-99"))
                        //新增家人
                        addUser(groupPosition);
                } else {

                    Intent intent = new Intent(FamilyManageListeningActivity.this, FamilyDataListeningActivity.class);
                    intent.putExtra("FamilyData", hostList.get(groupPosition).getFamilies().get(childPosition));
                    startActivityForResult(intent,10000);
//                    startActivity(intent);
                }
                return false;
            }
        });


        elvFamilyManage.setOnMenuItemClickListener(new SwipeMenuExpandableListView.OnMenuItemClickListenerForExpandable() {
            @Override
            public boolean onMenuItemClick(int groupPosition, final int childPosition, SwipeMenu menu, int index) {
                final Host host = hostList.get(groupPosition);
                //用户解绑或管理员解绑
                if (childPosition < 0) {
                    popUpDismissHost(host);
                }
                //管理员解绑用户
                else {
                    final Family family = host.getFamilies().get(childPosition);
                    if (!family.getUserId().equals("-99") && family.getAdmin() != 1) {//添加和管理员自己
                        mRestorePopUpWindow = new RestorePopUpWindow(getApplicationContext(), new RestorePopUpWindow.RestoreConfirmListener() {

                            @Override
                            public void result(boolean isConfirm) {
                                //确认删除
                                if (isConfirm) {
                                    if (family.getApplyStatus() == 0) {//删除已添加的用户

                                        host.getFamilies().remove(childPosition);
                                        adapter.notifyDataSetChanged();
                                        deleteUser(host.getHostId(), family.getUserId());

                                    } else {
                                        String updateStatus = "";
                                        updateStatus = FamilyManageController.statusAdminCancel;
                                        if (String.valueOf(family.getApplyStatus()).equals(FamilyManageController.applyStatusApply)) {
                                            updateStatus = FamilyManageController.statusAdminReject;
                                        }
                                        deleteNormalUser(Constant.USERID,
                                                family.getUserId(),
                                                family.getHostId(),
                                                family.getApplyStatus() + "",
                                                family.getShare() + "",
                                                updateStatus);
//                                    deleteApplyUser(Constant.USERID, family.getUserId());
                                    }
                                }
                            }
                        });
                        mRestorePopUpWindow.setTextViewContent("是否要移除该用户?");
                        mRestorePopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 弹出提示框
     *
     * @param host
     */
    private void popUpDismissHost(final Host host) {
        //如果是管理员
        if (Constant.userIsAdmin(host)) {
            mRestorePopUpWindow = new RestorePopUpWindow(getApplicationContext(), new RestorePopUpWindow.RestoreConfirmListener() {

                @Override
                public void result(boolean isConfirm) {
                    //确认删除
                    if (isConfirm) {
                        hostList.remove(host);
                        adapter.notifyDataSetChanged();
                        deleteHost(host.getHostId());
                    }
                }
            });
            mRestorePopUpWindow.setTextViewContent("是否要删除当前主机，当前主机下的用户会自动解绑");
            mRestorePopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
        //不是管理员
        else {
            mRestorePopUpWindow = new RestorePopUpWindow(getApplicationContext(), new RestorePopUpWindow.RestoreConfirmListener() {

                @Override
                public void result(boolean isConfirm) {
                    //确认删除
                    if (isConfirm) {
                        hostList.remove(host);
                        adapter.notifyDataSetChanged();
                        deleteUser(host.getHostId(), Constant.USERID);
                    }
                }
            });
            mRestorePopUpWindow.setTextViewContent("是否要解绑当前主机");
            mRestorePopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 解绑主机
     *
     * @param hostId
     */
    private void deleteHost(final String hostId) {
        toastUtils.showProgress("正在解绑");
        FamilyManageController.getInstance().deleteHost(this, hostId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    toastUtils.showSuccessWithStatus("解绑成功");

                    //如果是当前主机,需要清除数据
                    if (Constant.CURRENTHOSTID.equals(hostId)) {
                        clearStaticData();
                        Constant.CURRENTHOSTID = null;

                        //add by sunzhibin  12-27
                        Constant.IS_CHANGE_HOST = true; //摄像头有用到

                        //登出SIP服务
                        logoutFromSipServer();
                        //更新本地常量
                        Constant.GATEWAY = null;
                        Constant.GATEWAY_MD5_VALUE = "";
                        Constant.DEVICE_MD5_VALUE = "";
                        Constant.GLOBALMODE_MD5_VALUE = "";
                        Constant.IS_GATEWAY_ONLINE = false;
                        SharedPreferencesUtils.saveCurrentHostIdToPreferences(getApplicationContext());

                        Constant.IS_LOCAL_CONNECTION = Boolean.FALSE;
//                        Constant.CURRENTHOSTID="";
                        if (hostList != null) {
                            if(hostList.size()>0){
                                Constant.CURRENTHOSTID=hostList.get(0).getHostId();
                                for(Host host:hostList){
                                    final String isOnline = onlineMap.get(host.getHostId());
                                    if (isOnline != null|| "1".equals(isOnline)) {
                                        Constant.CURRENTHOSTID=host.getHostId();
                                        break;
                                    }
                                }
                            }
                            else{
                                Constant.CURRENTHOSTID="";
                            }
                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                                startActivity(intent);
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

    /**
     * 删除用户
     *
     * @param hostId
     * @param userId
     */
    private void deleteUser(final String hostId, String userId) {
        toastUtils.showProgress("正在删除");
        FamilyManageController.getInstance().deleteUser(this, hostId, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    toastUtils.showSuccessWithStatus("删除成功");

//                    Constant.CURRENTHOSTID = "";
                    if (Constant.CURRENTHOSTID.equals(hostId)) {
                        clearStaticData();
                        Constant.CURRENTHOSTID = null;

                        //add by sunzhibin  12-27
                        Constant.IS_CHANGE_HOST = true; //摄像头有用到

                        //登出SIP服务
                        logoutFromSipServer();
                        //更新本地常量
                        Constant.GATEWAY = null;
                        Constant.GATEWAY_MD5_VALUE = "";
                        Constant.DEVICE_MD5_VALUE = "";
                        Constant.GLOBALMODE_MD5_VALUE = "";
                        Constant.IS_GATEWAY_ONLINE = false;
                        SharedPreferencesUtils.saveCurrentHostIdToPreferences(getApplicationContext());


                        Constant.IS_LOCAL_CONNECTION = Boolean.FALSE;
//                        Constant.CURRENTHOSTID="";
                        if (hostList != null) {
                            if(hostList.size()>0){
                                Constant.CURRENTHOSTID=hostList.get(0).getHostId();
                                for(Host host:hostList){
                                    final String isOnline = onlineMap.get(host.getHostId());
                                    if (isOnline != null|| "1".equals(isOnline)) {
                                        Constant.CURRENTHOSTID=host.getHostId();
                                        break;
                                    }
                                }
                            }
                            else{
                                Constant.CURRENTHOSTID="";
                            }
                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                                startActivity(intent);
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

    /**
     * 更新申请状态
     */
    private void deleteNormalUser(String adminId, String applyUserId,
                                  String hostId, String applyStatus, String status,
                                  String updateStatus) {
        FamilyManageController.getInstance().updateUserApply(this,
                adminId,
                applyUserId,
                applyStatus,
                hostId,
                status,
                updateStatus,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        Loger.d(json);
                        BaseResult result = new Gson().fromJson(json, BaseResult.class);
                        if (result.getRet() == 0) {
                            getFamilyData();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        Loger.d(json);
                    }
                });

    }

    /**
     * 切换主机
     *
     * @param host
     */
    private void changeHost(final Host host) {
        toastUtils.showProgress("切换主机中...");
        GatewayController.getInstance().gatewayChange(this, host.getHostId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        toastUtils.showSuccessWithStatus("主机切换成功");
                        //add by sunzhibin  12-27
                        Constant.IS_CHANGE_HOST = true; //摄像头有用到

                        //登出SIP服务
                        logoutFromSipServer();
                        //更新本地常量
                        Constant.GATEWAY = null;
                        Constant.GATEWAY_MD5_VALUE = "";
                        Constant.DEVICE_MD5_VALUE = "";
                        Constant.GLOBALMODE_MD5_VALUE = "";
                        Constant.IS_GATEWAY_ONLINE = false;
                        Constant.CURRENTHOSTID = host.getHostId();
                        SharedPreferencesUtils.saveCurrentHostIdToPreferences(getApplicationContext());
//                        setResult(RESULT_OK);
//                        startService(new Intent(getApplicationContext(), PollService.class));


                        if (Constant.gatewayInfos != null) {
                            for(GatewayInfo gatewayInfo:Constant.gatewayInfos){
                                if(gatewayInfo.getHostId().equals(host.getHostId())){
                                    Constant.IS_LOCAL_CONNECTION=Boolean.TRUE;
                                    Constant.LOCAL_CONNECTION_IP = gatewayInfo.getIp();
                                    break;
                                }
                            }
                        }
                        else{
                            UDPUtils.startUDPBroadCast(null);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    toastUtils.dismiss();
                }
            }

            @Override
            public void onFailed(String Json) {
                if (toastUtils != null) {
                    toastUtils.dismiss();
                }
            }
        });
    }

    /**
     * 获取家人数据
     */
    private void getFamilyData() {
        toastUtils.showProgress("");
        FamilyManageController.getInstance().showFamilies(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                if (toastUtils != null)
                    toastUtils.dismiss();
                try {
                    HostResult result = new Gson().fromJson(Json, HostResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        hostList.clear();
                        hostList.addAll(sortHostList(result.getHosts()));
                        adapter.notifyDataSetChanged();
                        //有网
                        Log.v("gl", "checkNet===" + NetUtil.checkNet(getApplicationContext()) + "isNeedCheckGateway==" + isNeedCheckGateway);

                        if (NetUtil.checkNet(getApplicationContext())) {
                            isNeedCheckGateway = false;
                            requestWithHosts(hostList);

                        } else {
                            toastUtils.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();
            }
        });
    }

    /**
     * 对返回的host进行排序,当前使用的放在最上面
     *
     * @param hosts
     * @return
     */
    private List<Host> sortHostList(List<Host> hosts) {
        Host currentHost = null;
        //找到当前主机
        for (Host host : hosts) {
            if (Constant.CURRENTHOSTID.equals(host.getHostId())) {
                currentHost = host;
                break;
            }
        }
        if (currentHost != null) {
            hosts.remove(currentHost);
            hosts.add(0, currentHost);
        }
        return hosts;
    }

    /**
     * 通过云端判断 连接测试,判断主机是否在线
     */
    private void requestWithHosts(List<Host> hosts) {
        String hostIp = null;
        onlineMap.clear();
        COUNT = 0;
        for (Host host : hosts) {
            //连接测试
            connectTestWithHostId(host.getHostId(), hostIp, false, hosts.size());
            //请求用户申请信息
            requestApplyInfoWithHostId(host.getHostId());
        }
    }

    /**
     * 请求用户申请信息
     *
     * @param hostId
     */
    private void requestApplyInfoWithHostId(final String hostId) {
        toastUtils.showProgress("");
        FamilyManageController.getInstance().applyInfoWithHostId(this, hostId,
                FamilyManageController.statusNormal, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        toastUtils.dismiss();
                        try {
                            ApplyInfosResult result = new Gson().fromJson(Json, ApplyInfosResult.class);
                            if (result.getRet() == 0) {
                                applyInfosToFamilies(hostId, result.getApplies());
                            } else {
                                applyInfosToFamilies(hostId, null);
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

    /**
     * 检查当前用户是否为管理员,是的话可以添加用户
     */
    private void addLastRow(Host host) {

        Log.v("gl", "userIsAdmin(host)==" + Constant.userIsAdmin(host));
        if (Constant.userIsAdmin(host)) {
            //增加一项最后的+
            Family lastUser = new Family();
            User user = new User();
            lastUser.setUserId("-99");
            user.setAvatarUrl("-99");
            lastUser.setUser(user);
            host.getFamilies().add(lastUser);
            Log.v("gl", "addLastRow==" + hostList.get(0).getFamilies().size());
            adapter.setData(hostList);
        }
    }

    /**
     * 将申请用户转换成家人管理中的用户
     *
     * @param applies
     */
    private void applyInfosToFamilies(String hostId, List<ApplyInfosResult.AppliesBean> applies) {
        for (Host host : hostList) {
            if (hostId.equals(host.getHostId())) {
                Log.v("gl", "hostId===" + hostId + "hostList===" + host.getHostId());
                if (applies != null) {
                    List<Family> families = new ArrayList<>();
                    //将申请用户转换成家人
                    for (ApplyInfosResult.AppliesBean appliesBean : applies) {
                        User applyUser = appliesBean.getApplyUser();
                        Family family = new Family();
                        family.setApplyStatus(appliesBean.getApplyStatus());
                        family.setHostId(appliesBean.getHostId());
                        family.setUserId(applyUser.getId());
                        family.setUser(applyUser);
                        families.add(family);
                        host.getFamilies().add(family);
                    }
                }
                //检查当前用户是否为管理员
                addLastRow(host);
            }
        }
    }

    /**
     * 测试主机在不在线
     *
     * @param hostId 主机Id
     * @param hostIp 主机Ip
     * @param SIZE   需要测试的数量
     */
    private void connectTestWithHostId(final String hostId, String hostIp, boolean is_locatiion, final int SIZE) {
        L.d("FamilyManageListeningActivity connectTestWithHostId() " + hostId + "----" + hostIp);
        GatewayController.getInstance().getGatewayProperties(this, hostId, hostIp, is_locatiion, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                COUNT++;
                if (COUNT == SIZE) {
                    toastUtils.dismiss();
                }
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    L.d("FamilyManageListeningActivity connectTestWithHostId() onSuccess() ++++++++++++++++++");
                    if (result.getRet() == 0) {
                        onlineMap.put(hostId, "1");
                        adapter.setOnline(onlineMap);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {
                COUNT++;
                if (COUNT == SIZE) {
                    if (toastUtils != null) toastUtils.dismiss();
                }
                L.d("FamilyManageListeningActivity connectTestWithHostId() onFailed() " + Json);

            }
        });
    }


    public void heathyShare(int groupPosition) {
        if (hostList.get(groupPosition).getFamilies() != null
                && hostList.get(groupPosition).getFamilies().size() > 0) {
            hostId = hostList.get(groupPosition).getHostId();

            List<Family> tempFamilyList=new ArrayList<Family>();
            for(Family family:hostList.get(groupPosition).getFamilies()){
                if (0 == family.getApplyStatus()) {
                    tempFamilyList.add(family);
                }
            }
//            tempFamilyList.addAll(hostList.get(groupPosition).getFamilies());
//            List<Family> tempFamilyList = hostList.get(groupPosition).getFamilies();
            if (tempFamilyList == null) tempFamilyList = new ArrayList<>();
            if(tempFamilyList.size()>0&&Constant.userIsAdmin(hostList.get(groupPosition)))tempFamilyList.remove(tempFamilyList.size()-1);
            HealthSharePopUpWindow popUpWindow = new HealthSharePopUpWindow(this,
                    tempFamilyList, new HealthSharePopUpWindow.ClickResultListener() {
                @Override
                public void result(List<Family> families) {
                    Log.v("gl", "families.size()==" + families.size());

                    String sharedIds = "";
                    if (families.size() > 0) {
                        for (int i = 0; i < families.size(); i++) {
                            sharedIds = sharedIds + families.get(i).getUser().getId() + ",";
                        }


                        Log.v("gl", "sharedIds==" + sharedIds);
//                        sharedIds = sharedIds.substring(0, sharedIds.length() - 1);
                        updateShare(sharedIds);
                        return;
                    }
                    updateShare("");

                }
            });
            popUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        } else {
            toastUtils.showSuccessWithStatus("暂无用户可分享");
        }

    }

    /**
     * 增加用户
     *
     * @param groupPosition
     */
    public void addUser(int groupPosition) {
        if (hostList.get(groupPosition) != null) {
            Host host = hostList.get(groupPosition);
            startActivityForResult(new Intent(FamilyManageListeningActivity.this, AddFamilyListeningActivity.class)
                    .putExtra(Constant.KEY_HOST, host),1000);
        }
    }

    /**
     * 健康分享请求
     *
     * @param sharedIds 被分享用户Id,以","分隔
     */
    private void updateShare(String sharedIds) {
        toastUtils.showProgress("分享中...");
        FamilyManageController.getInstance().updateShare(FamilyManageListeningActivity.this,
                hostId, sharedIds, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        if (toastUtils != null)
                            toastUtils.dismiss();
//                        L.e("updateShare json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
//                            toastUtils.showSuccessWithStatus("分享成功");
                            ToastHelper.showShortMsg("分享成功");
                            getFamilyData();
                        } else {
//                            toastUtils.showInfoWithStatus(JsonUtil.ShowMessage(Json));
                            ToastHelper.showShortMsg("分享失败");
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        if (toastUtils != null)
                            toastUtils.dismiss();
                        ToastHelper.showShortMsg("分享失败");
                        L.e("updateShare json===" + json);
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 100){
                getFamilyData();
            }
            else if(requestCode==10000){
                getFamilyData();
            }
            else if(requestCode==1000){
                getFamilyData();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (toastUtils.isShowing()) {
                toastUtils.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void leftViewClick() {
        finish();
    }

    @Override
    public void rightViewClick() {
        List<String> hosts = new ArrayList<>();
        for (Host host : hostList) {
            hosts.add(host.getHostId());
        }
        Intent intent = new Intent(this, GatewayBindListeningActivity.class);
        intent.putExtra("hosts", (Serializable) hosts);
        startActivityForResult(intent, 100);

    }

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


    /**
     * 清理靜态数据
     */

    protected void clearStaticData() {
        Constant.DEVICE_MD5_VALUE = "";
        Constant.GATEWAY_MD5_VALUE = "";
        Constant.GLOBALMODE_MD5_VALUE = "";
        Constant.DEVICE_RELATE = new ArrayList<>();
        Constant.GATEWAY = null;
        Constant.GLOBAL_MODE = new ArrayList<>();
    }

    /**
     * SIP服务退出
     */
    protected void logoutFromSipServer() {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String username = preferences.getString("login_username", "");
        if (StringUtil.isEmpty(username)) {
            return;
        }
        squirrelCall.squirrelAccountExit(squirrelCallImpl.servername,
                squirrelCallImpl.serverport, username);
    }


    private LocalBroadcastManager mLocalBroadcastManager;
    public static String ACTION_MIDIFY_HOSTNAME_REMARK ="com.boer.delos.FamilyManageListeningActivity.modify.hostname.remark";
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MIDIFY_HOSTNAME_REMARK)){
                getFamilyData();
            }
        }
    };

}

