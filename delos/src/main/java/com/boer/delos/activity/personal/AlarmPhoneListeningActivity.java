package com.boer.delos.activity.personal;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.AlarmPhoneAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.AlarmPhone;
import com.boer.delos.model.Gateway;
import com.boer.delos.model.Host;
import com.boer.delos.model.HostResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.AddAlarmPhonePopUpWindow;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnItemLongClick;

/**
 * @author XieQingTing
 * @Description: 报警电话界面
 * create at 2016/5/17 10:34
 */
public class AlarmPhoneListeningActivity extends BaseListeningActivity implements View.OnClickListener{

    private ListView lvAlarmPhone;
    private List<String> phones = new ArrayList<>();
    private AlarmPhoneAdapter adapter;
    private List<AlarmPhone> datas = new ArrayList<>();
    private AddAlarmPhonePopUpWindow phonePopUpWindow;
    private String alarmPhone;
    private LayoutInflater inflater;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        view = inflater.inflate(R.layout.activity_alarm_phone, null);
        setContentView(view);

        initView();
        adapter = new AlarmPhoneAdapter(this, new AlarmPhoneAdapter.ItemClckListener() {
            @Override
            public void deleteItem(int position) {
                deleteAlarmPhone(position);
            }
        });
        lvAlarmPhone.setAdapter(adapter);
        getGatewayInfo();
//        getFamilyData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        queryRecentData();
        isAdmin(Constant.LOGIN_USER.getMobile());
    }


    private void initView() {
        initTopBar(R.string.alarm_phone, null, true, true);
        ivRight.setVisibility(View.GONE);
        ivRight.setOnClickListener(this);
        this.lvAlarmPhone = (ListView) findViewById(R.id.lvAlarmPhone);
    }

    private void initData() {
        datas.clear();
        if (phones.size() > 0) {

            for (String phone : phones) {
                L.e("phone======" + phone);
                if (!phone.equals("")) {
                    datas.add(new AlarmPhone(phone));
                }
            }

        } else {
            L.e("phones 为空======" + phones.size());

        }
        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
    }

    public void getGatewayInfo() {
        GatewayController.getInstance().getGatewayProperties(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.d("getGatewayInfo_Json ===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    Constant.GATEWAY = JsonUtil.parseDataObject(Json, Gateway.class, "response");
                    String numbers = Constant.GATEWAY.getNumbers();
//                        numbers="13400000000,13400000001,13400000002";
                    List<String> phoneTmp = StringUtil.convertStrToList(numbers);
                    phones.clear();
                    for (String s : phoneTmp) {
                        if (!s.equals("")) {
                            phones.add(s);
                        }
                    }
//                        for (String phone:phones){
//                            L.e("每个手机号==="+phone);
//                        }
//                        L.e("报警手机号码=====" + numbers + "    转换为list" + phones.size());
                    initData();
//                        List<Room> roomList = gateway.getRoom();
//                        Constant.roomList.clearAll();
//                        Constant.roomList.addAll(roomList);
                } else {
                    toastUtils.showInfoWithStatus(JsonUtil.parseString(Json, "msg"));
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    private void isAdmin(String mobile) {
        FamilyManageController.getInstance().userIsAdmin(this, mobile, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    int ret = jsonObject.getInt("ret");
                    int isAdmin = jsonObject.getInt("isAdmin");
                    if (ret == 0 && isAdmin == 1) {
                        ivRight.setVisibility(View.VISIBLE);
                    } else
                        ivRight.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Log.d("isAdmin", json);
//                ivRight.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

//    /**
//     * 获取家人数据
//     */
//    private void getFamilyData() {
//        FamilyManageController.getInstance().showFamilies(this, new RequestResultListener() {
//            @Override
//            public void onSuccess(String Json) {
//                try {
//                    L.i("getFamilyData ===" + Json);
//                    HostResult result = new Gson().fromJson(Json, HostResult.class);
//                    if (result.getRet() != 0) {
//                        toastUtils.showErrorWithStatus(result.getMsg());
//                    } else {
//                        List<Host> hostList = new ArrayList<>();
//                        hostList.addAll(result.getHosts());
//                        judgeUserIsManager(hostList);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailed(String json) {
//                if (toastUtils == null) return;
//                toastUtils.dismiss();
//            }
//        });
//    }

//    /**
//     * 判斷是否是當前網關的管理員
//     * 管理员：“+”显示，可以添加报警电话
//     * 非管理员：“+”隐藏
//     */
//    private void judgeUserIsManager(List<Host> hostList) {
//        boolean isManager = false;
//
//        for (Host host : hostList) {
//            if (!Constant.userIsAdmin(host)) {
//                continue;
//            }
//            if (host.getHostId().equals(Constant.CURRENTHOSTID)) {
//                isManager = true;
//            }
//        }
//        final boolean finalIsManager = isManager;
//        if (adapter != null) {
//            adapter.setAdmin(isManager);
//            adapter.notifyDataSetChanged();
//
//        }
//        if (finalIsManager) {
//            ivRight.setVisibility(View.VISIBLE);
//            //TODO 管理员可以添加报警手机
//        }
//    }

    //长按删除告警电话
    private void deleteAlarmPhone(int position) {
        if (position >= phones.size()) {
            return;
        }
        phones.remove(position);
        adapter.notifyDataSetChanged();
        StringBuffer sb = new StringBuffer();
        for (String s : phones) {//将手机列表拼成一个字符串
            sb.append(s).append(",");
        }
        Constant.GATEWAY.setNumbers(sb.toString());

        toastUtils.showProgress("删除中...");
        MemberController.getInstance().modifyHostProperty(this, Constant.GATEWAY, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("AlarmPhoneActivity_getAlarmPhone_Json===" + Json);
                toastUtils.dismiss();
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.showSuccessWithStatus("删除成功");
                    getGatewayInfo();
                } else {
                    String msg = JsonUtil.parseString(Json, "msg");
                    toastUtils.showInfoWithStatus(msg);
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
                L.e("modifyHostProperty:" + json);
                toastUtils.showSuccessWithStatus(json);
            }
        });
    }

    private void getAlarmPhone() {
        if (StringUtil.isEmpty(alarmPhone)) {
            BaseApplication.showToast(getResources().getString(R.string.input_alarm_phone));
            return;
        } else if (!StringUtil.isMobile(alarmPhone)) {

            BaseApplication.showToast("手机号码格式不正确");
            return;
        }
        boolean isAlreadyAdd = false;
        for (String s : phones) {//如果添加的手机号已有，则提示
            if (s.equals(alarmPhone)) {
                isAlreadyAdd = true;
                break;
            }
        }

        if (isAlreadyAdd) {
            BaseApplication.showToast("手机号码已添加过");
            return;
        }

        StringBuffer sb = new StringBuffer();
        for (String s : phones) {//将手机列表拼成一个字符串
            sb.append(s).append(",");
        }
        sb.append(alarmPhone);
        if (Constant.GATEWAY == null) {
            BaseApplication.showToast("主机不在线");
            phonePopUpWindow.dismiss();
            return;
        } else {

            Constant.GATEWAY.setNumbers(sb.toString());
        }

        phonePopUpWindow.dismiss();
        toastUtils.showProgress("请稍后...");
        MemberController.getInstance().modifyHostProperty(this, Constant.GATEWAY, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("AlarmPhoneActivity_getAlarmPhone_Json===" + Json);
                toastUtils.dismiss();
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.showSuccessWithStatus("添加成功");
                    getGatewayInfo();
                } else {
                    String msg = JsonUtil.parseString(Json, "msg");
                    toastUtils.showInfoWithStatus(msg);
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
                L.e("modifyHostProperty:" + json);
                toastUtils.showSuccessWithStatus(json);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRight:
                phonePopUpWindow = new AddAlarmPhonePopUpWindow(this, new AddAlarmPhonePopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        alarmPhone = phonePopUpWindow.getAlarmPhone();
                        getAlarmPhone();
                    }
                });
                phonePopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//                toastUtils.showInfoWithStatus("添加报警电话");
                break;
        }

    }
}
