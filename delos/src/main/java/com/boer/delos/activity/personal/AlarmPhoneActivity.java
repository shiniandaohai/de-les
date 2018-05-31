package com.boer.delos.activity.personal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import com.allen.expandablelistview.SwipeMenuExpandableCreator;
import com.allen.expandablelistview.SwipeMenuExpandableListView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.boer.delos.R;
import com.boer.delos.adapter.APhoneAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Gateway;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.gateway.GatewayController;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.DensityUitl;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.TitleLayout;
import com.boer.delos.view.popupWindow.AddAlarmPhonePopUpWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlarmPhoneActivity extends CommonBaseActivity{
    @Bind(R.id.elvPhone)
    SwipeMenuListView elvPhone;
    private APhoneAdapter adapter;
    private List<String> phones = new ArrayList<String>();
    private AddAlarmPhonePopUpWindow phonePopUpWindow;
    private String alarmPhone;
    @Override
    protected int initLayout() {
        return R.layout.activity_alarm_phone2;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.my_center_alarm_mobile));
        isManager();
    }

    @Override
    protected void initData() {
        adapter = new APhoneAdapter(this,phones);
        elvPhone.setAdapter(adapter);
        getGatewayInfo();
        elvPhone.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Log.i("gwq","eee");
                deleteAlarmPhone(position);
                return false;
            }
        });
    }

    @Override
    protected void initAction() {

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
                    adapter.notifyDataSetChanged();
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
    @Override
    public void rightViewClick() {
        super.rightViewClick();
        if(phonePopUpWindow== null){
            phonePopUpWindow = new AddAlarmPhonePopUpWindow(this, new AddAlarmPhonePopUpWindow.ClickResultListener() {
                @Override
                public void ClickResult(int tag) {
                    alarmPhone = phonePopUpWindow.getAlarmPhone();
                    getAlarmPhone();
                }
            });
        }
        phonePopUpWindow.showAtLocation(elvPhone, Gravity.CENTER, 0, 0);
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
            Constant.GATEWAY.setNumbers(sb.toString());//.substring(0, sb.length() - 2));
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

    //判断当前用户是否是管理员
    private void isManager(){
        FamilyManageController.getInstance().userIsAdmin(this, Constant.LOGIN_USER.getMobile(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                String isAdim = JsonUtil.parseString(json, "isAdmin");
                if(!StringUtil.isEmpty(isAdim)&&isAdim.equals("1")){
                    tlTitleLayout.setLinearRightImage(R.mipmap.ic_nav_add);


                    SwipeMenuCreator creator = new SwipeMenuCreator() {
                        @Override
                        public void create(SwipeMenu menu) {
                            createDismissMenu(menu);
                        }

                        //创建解绑按钮
                        private void createDismissMenu(SwipeMenu menu) {
                            SwipeMenuItem item = new SwipeMenuItem(getApplicationContext());
                            item.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x00, 0x00)));
                            item.setWidth(DensityUitl.dip2px(getApplicationContext(), 80));
                            item.setIcon(R.mipmap.ic_nav_delete);
                            menu.addMenuItem(item);
                        }
                    };
                    elvPhone.setMenuCreator(creator);
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    //删除告警电话
    private void deleteAlarmPhone(int position) {
        if (position >= phones.size()) {
            return;
        }
        phones.remove(position);
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
}
