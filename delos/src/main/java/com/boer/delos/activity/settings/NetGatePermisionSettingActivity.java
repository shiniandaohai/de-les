package com.boer.delos.activity.settings;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.personal.FamilyManageListeningActivity;
import com.boer.delos.adapter.GatewayPermissAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.fragment.SystemMessageFragment;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Family;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.model.SystemMessage;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.popupWindow.ShowLimitChoicePopupWindow;
import com.boer.delos.view.popupWindow.ShowLimitTimePopupWindow;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/4/10.
 */
public class NetGatePermisionSettingActivity extends CommonBaseActivity {


    @Bind(R.id.ivAvatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tvUserName)
    TextView tvUserName;
    @Bind(R.id.llUserInfo)
    LinearLayout llUserInfo;
    @Bind(R.id.lv_gateway)
    ListView lvGateway;
    @Bind(R.id.tv_select_all)
    CheckedTextView tvSelectAll;
    @Bind(R.id.iv_device)
    ImageView ivDevice;
    @Bind(R.id.llayout_devices)
    LinearLayout llayoutDevices;
    @Bind(R.id.iv_scence)
    ImageView ivScence;
    @Bind(R.id.llayout_scence)
    LinearLayout llayoutScence;
    @Bind(R.id.iv_health)
    ImageView ivHealth;
    @Bind(R.id.llayout_health)
    LinearLayout llayoutHealth;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.llayout_permision_time)
    LinearLayout llayoutPermisionTime;
    @Bind(R.id.tv_ok)
    TextView tvOk;

    @Bind(R.id.tv_permision_title)
    TextView tvPermisionTitle;

    /////
    private List<Map<String, String>> list = new ArrayList<>();
    private GatewayPermissAdapter adapter;
    private String selectHostId;
    private String pession = "";
    private boolean isSelectAll, isHealthLive = false, isDeviceManage = false, isLinkManage = false;
    private ShowLimitTimePopupWindow showLimitTimePopupWindow;
    private ShowLimitChoicePopupWindow showLimitChoicePopupWindow;
    private long limitTime = 0;//second秒
    private String limitStatus = "0";
    private User user;

    private int mType;

    @Override
    protected int initLayout() {
        return R.layout.activity_net_gate_permision_setting;
    }

    @Override
    protected void initView() {
        mType=getIntent().getIntExtra("type",0);
        if(mType==0||mType==2){
            tlTitleLayout.setTitle(R.string.text_charge_permision);
            tvPermisionTitle.setText(R.string.text_charge_permision);
//            tvOk.setText(R.string.text_charge_permision);
        }
        else if(mType==1){
            tlTitleLayout.setTitle(R.string.text_change_permision);
//            tvPermisionTitle.setText(R.string.text_change_permision);
            tvOk.setText(R.string.text_change_permision);
        }
    }

    @Override
    protected void initData() {

        Log.v("gl", "initData===");

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        selectHostId = intent.getStringExtra("host");

        if (user != null) {
            if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            } else {
                ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            }
            tvUserName.setText(user.getName());
        }


        showLimitTimePopupWindow = new ShowLimitTimePopupWindow(this, llayoutPermisionTime);
        showLimitChoicePopupWindow = new ShowLimitChoicePopupWindow(this, llayoutPermisionTime);


        adapter = new GatewayPermissAdapter(this, list);
        lvGateway.setAdapter(adapter);
        lvGateway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        getUserPermision();
    }

    @Override
    protected void initAction() {

        showLimitTimePopupWindow.setShowLimitTimePopupWindowInterface(new ShowLimitTimePopupWindow.ShowLimitTimePopupWindowInterface() {
            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String limitDay, String limitHour, String status) {

                Log.v("gl", "limitDay==" + limitDay + "limitHour==" + limitHour + "status==" + status);
                limitTime = computeMillis(limitDay, limitHour);

                limitStatus = status;

                if (limitStatus.equals("1"))
                    tvTime.setText(limitDay + getString(R.string.pick_day) + limitHour + getString(R.string.pick_hour));
                if (limitStatus.equals("0"))
                    tvTime.setText(getString(R.string.family_text_unlimit));

            }
        });

        showLimitChoicePopupWindow.setShowChoicePopupWindowInterface(new ShowLimitChoicePopupWindow.ShowLimitChoicePopupWindowInterface() {
            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String result) {

                if (result.equals("update")) {


                    showLimitTimePopupWindow.showPopupWindow();


                } else if (result.equals("stop")) {


                    limitStatus = "2";
                    limitTime = 0;
                    tvTime.setText(getString(R.string.family_unlimit_time));


                }

            }
        });

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
//                        setUpPermision(result.getHosts());
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

    private void clearChecked() {
        for (Map<String, String> map : list) {
            map.put("checked", "0");
        }
    }


    private void selectType(String which) {

        if (which.equals("all")) {
            if (isSelectAll) {
                isHealthLive = true;
                isDeviceManage = true;
                isLinkManage = true;
                llayoutPermisionTime.setVisibility(View.VISIBLE);
                ivDevice.setVisibility(View.VISIBLE);
                ivHealth.setVisibility(View.VISIBLE);
                ivScence.setVisibility(View.VISIBLE);
            } else {
                isHealthLive = false;
                isDeviceManage = false;
                isLinkManage = false;
                llayoutPermisionTime.setVisibility(View.GONE);
                ivDevice.setVisibility(View.GONE);
                ivHealth.setVisibility(View.GONE);
                ivScence.setVisibility(View.GONE);
                limitTime = 0;
                limitStatus = "0";
                tvTime.setText(getString(R.string.family_limit_time));

            }
        } else if (which.equals("device")) {

            if (isDeviceManage) {
                llayoutPermisionTime.setVisibility(View.VISIBLE);
                ivDevice.setVisibility(View.VISIBLE);

            } else {
                ivDevice.setVisibility(View.GONE);
            }
        } else if (which.equals("health")) {


            if (isHealthLive) {
                llayoutPermisionTime.setVisibility(View.VISIBLE);
                ivHealth.setVisibility(View.VISIBLE);

            } else {
                ivHealth.setVisibility(View.GONE);

            }
        } else if (which.equals("scence")) {

            if (isLinkManage) {
                llayoutPermisionTime.setVisibility(View.VISIBLE);
                ivScence.setVisibility(View.VISIBLE);
            } else {

                ivScence.setVisibility(View.GONE);
            }
        }

        boolean choose = isDeviceManage || isHealthLive || isLinkManage;
        if (!choose) {
            llayoutPermisionTime.setVisibility(View.GONE);
            limitTime = 0;
            limitStatus = "0";
            tvTime.setText(getString(R.string.family_limit_time));
        }

        if(isDeviceManage && isHealthLive && isLinkManage){
            tvSelectAll.setChecked(true);
        }
        else{
            tvSelectAll.setChecked(false);
        }

    }

    private void addPermisionType() {

        if (isDeviceManage) {
            pession = getString(R.string.family_device_manage) + ",";
        }
        if (isLinkManage) {
            pession += getString(R.string.family_scene_manage) + ",";
        }
        if (isHealthLive) {
            pession += getString(R.string.family_health_manage)+",";
        }

        if(!pession.equals("")){
            pession=pession.substring(0,pession.length()-1);
        }

        if(mType==0){
            addUserInFamily(user.getId(),selectHostId,pession);
        }
        else if(mType==1){
            updatePermision(user.getId());
        }
        else if(mType==2){
            isInBlackList();
        }
    }


    @OnClick({R.id.tv_select_all, R.id.llayout_devices, R.id.llayout_scence, R.id.llayout_health, R.id.llayout_permision_time, R.id.tv_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_all:
                tvSelectAll.toggle();
                isSelectAll = tvSelectAll.isChecked();
                selectType("all");
                break;

            case R.id.llayout_devices:
                isDeviceManage = !isDeviceManage;
                selectType("device");
                break;

            case R.id.llayout_scence:
                isLinkManage = !isLinkManage;
                selectType("scence");
                break;

            case R.id.llayout_health:
                isHealthLive = !isHealthLive;
                selectType("health");
                break;
            case R.id.llayout_permision_time:

                if (tvTime.getText().equals(getString(R.string.family_unlimit_time)))
                    showLimitTimePopupWindow.showPopupWindow();
                else {
                    showLimitChoicePopupWindow.showPopupWindow();
                }

                break;
            case R.id.tv_ok:
                if (selectHostId == null) {
                    return;
                }

                addPermisionType();

                break;
        }
    }

    public long computeMillis(String strDay, String strHour) {

        //秒

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        int mi = 60;
        int hh = mi * 60;
        int dd = hh * 24;
        long milliSecond = Integer.parseInt(strDay) * dd + Integer.parseInt(strHour) * hh;

        return System.currentTimeMillis() / 1000 + milliSecond;

    }

    private void updatePermision(final String userId){
        FamilyManageController.getInstance().updateUserPermission(this, selectHostId, userId, pession,limitTime+"",limitStatus, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
//                updateLimitTime(userId);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    int ret=jsonObject.getInt("ret");
                    if(ret==0){
//                        Intent intent = getIntent();
//                        intent.putExtra("limitTimeTxt", tvTime.getText().toString());
//                        intent.putExtra("limitStatus", limitStatus);
//                        intent.putExtra("limitTime", limitTime + "");
//                        intent.putExtra("pession", pession);
//                        setResult(RESULT_OK, intent);
                        ToastHelper.showShortMsg("更改权限成功");
                        finish();
                    }
                    else{
                        String msg=jsonObject.getString("msg");
                        ToastHelper.showShortMsg(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                Intent intent = getIntent();
//                intent.putExtra("limitTimeTxt", tvTime.getText().toString());
//                intent.putExtra("limitStatus", limitStatus);
//                intent.putExtra("limitTime", limitTime + "");
//                intent.putExtra("pession", pession);
//                SystemMessage.MsgListBean message = (SystemMessage.MsgListBean) getIntent().getSerializableExtra("message");
//                intent.putExtra("message",message);
//                setResult(RESULT_CANCELED, intent);
//                LocalBroadcastManager.getInstance(NetGatePermisionSettingActivity.this).sendBroadcast(intent.setAction(SystemMessageFragment.ACTION_REFRESH));

            }

            @Override
            public void onFailed(String json) {
                Log.d("NetGatePermision",json);
            }
        });
    }

    public String computeRevertMillis(String second) {


        //转为毫秒
        long ms = Long.parseLong(second) * 1000 - System.currentTimeMillis();

        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute=(ms-day*dd-hour*hh)/mi;

        StringBuffer sb = new StringBuffer();
        if (day >= 0) {
            sb.append(day + getString(R.string.pick_day));
        }
        if (hour >= 0) {
            sb.append(hour + getString(R.string.pick_hour));
        }

        if(minute>=0){
            sb.append(minute+"分");
        }

        return sb.toString();


    }

    private void getUserPermision(){
        if(Constant.LOGIN_USER.getName().equals("admin")){
            tvSelectAll.setChecked(true);
            llayoutPermisionTime.setVisibility(View.VISIBLE);
            tvTime.setText(getString(R.string.family_text_unlimit));
            return;
        }
        FamilyManageController.getInstance().queryUserPermission(this, selectHostId, user.getId(), new RequestResultListener(){

            @Override
            public void onSuccess(String json) {
                Log.d("json",json);
                try {
                    JSONObject jsonObject=new JSONObject(json);
                    String ret=jsonObject.getString("ret");
                    if(!ret.equals("0")){
                        return;
                    }
                    String permision=jsonObject.getString("permissions");
                    limitStatus=jsonObject.getString("limitStatus");
                    limitTime=Long.valueOf(jsonObject.getString("limitTime"));

                    if(permision.contains("设备管理")){
                        ivDevice.setVisibility(View.VISIBLE);
                        isDeviceManage=true;
                    }
                    else{
                        ivDevice.setVisibility(View.GONE);
                        isDeviceManage=false;
                    }
                    if(permision.contains("场景管理")){
                        ivScence.setVisibility(View.VISIBLE);
                        isLinkManage=true;
                    }
                    else{
                        ivScence.setVisibility(View.GONE);
                        isLinkManage=false;
                    }
                    if(permision.contains("房间管理")){
                        ivHealth.setVisibility(View.VISIBLE);
                        isHealthLive=true;
                    }
                    else{
                        ivHealth.setVisibility(View.GONE);
                        isHealthLive=false;
                    }

                    if(permision.contains("设备管理")&&permision.contains("场景管理")&&permision.contains("房间管理")){
                        tvSelectAll.setChecked(true);
                    }
                    else{
                        tvSelectAll.setChecked(false);
                    }

                    if(!permision.equals("")){
                        llayoutPermisionTime.setVisibility(View.VISIBLE);
                        if (limitStatus.equals("1")) {
                            tvTime.setText(computeRevertMillis(limitTime + ""));
                        } else if (limitStatus.equals("0")||limitStatus.equals("2")) {
                            tvTime.setText(getString(R.string.family_text_unlimit));
                        }
                    }
                    else{
                        llayoutPermisionTime.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                Log.d("json",json);
            }
        });
    }


    private void addUserInFamily(final String userId, final String hostId, final String permission) {
        FamilyManageController.getInstance().addUser(this, userId,
                hostId, permission, limitStatus, limitTime+"", new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                        if (result.getRet() == 0) {
                            toastUtils.showSuccessWithStatus(getString(R.string.save_success));
                            Intent intent = getIntent();
                            intent.putExtra("limitTimeTxt", tvTime.getText().toString());
                            intent.putExtra("limitStatus", limitStatus);
                            intent.putExtra("limitTime", limitTime + "");
                            intent.putExtra("pession", pession);
                            SystemMessage.MsgListBean message = (SystemMessage.MsgListBean) getIntent().getSerializableExtra("message");
                            intent.putExtra("message",message);
//                setResult(RESULT_CANCELED, intent);
                            LocalBroadcastManager.getInstance(NetGatePermisionSettingActivity.this).sendBroadcast(intent.setAction(SystemMessageFragment.ACTION_REFRESH));
                            finish();

                        } else {
                            toastUtils.showErrorWithStatus(result.getMsg());
                        }
                        Loger.d("SystemMessageFragment" + Json);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
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
        toastUtils.showProgress("");
        FamilyManageController.getInstance().userApplyToAdmin(this,
                Constant.LOGIN_USER.getId(),
                user.getId(),
                "",
                "2",
                selectHostId,
                pession,
                "0", limitStatus, limitTime+"",
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try {
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                LocalBroadcastManager.getInstance(NetGatePermisionSettingActivity.this).sendBroadcast(new Intent().
                                        setAction(FamilyManageListeningActivity.ACTION_MIDIFY_HOSTNAME_REMARK));
                                toastUtils.showSuccessWithStatus(getResources().getString(R.string.save_success));
                                finish();
                            }
//                            setResult(RESULT_OK);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }


    private void isInBlackList() {
        String mobile = user.getMobile();
        FamilyManageController.getInstance().isInBlackList(this, mobile, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d("isInBlackList" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int ret = jsonObject.getInt("ret");
                    if (ret == 0) {
                        boolean isInBlackList=jsonObject.getBoolean("isInBlackList");
                        if(isInBlackList){
                            removeBlackList();
                        }
                        else{
                            userApply();
                        }
                    } else {
                        ToastHelper.showShortMsg("邀请加入失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastHelper.showShortMsg("邀请加入失败");
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d("doJoinBlackList" + json);
                ToastHelper.showShortMsg("网络错误");
            }
        });
    }

    private void removeBlackList() {
        String mobile = user.getMobile();
        FamilyManageController.getInstance().removeBlackList(this, mobile, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Loger.d("isInBlackList" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int ret = jsonObject.getInt("ret");
                    if (ret == 0) {
                        userApply();
                    } else {
                        ToastHelper.showShortMsg("邀请加入失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastHelper.showShortMsg("邀请加入失败");
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.d("doJoinBlackList" + json);
                ToastHelper.showShortMsg("网络错误");
            }
        });
    }

}
