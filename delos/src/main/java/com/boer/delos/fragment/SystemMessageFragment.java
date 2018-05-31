package com.boer.delos.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.main.InformationCenterListeningActivity;
import com.boer.delos.activity.settings.NetGatePermisionSettingActivity;
import com.boer.delos.activity.settings.SettingsFamilyApplyActivity;
import com.boer.delos.adapter.SystemMessageAdapter;
import com.boer.delos.commen.BaseActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.SystemMessage;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.alarm.AlarmController;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.view.popupWindow.DeleteScenePopUpWindow;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SystemMessageFragment extends LazyFragment {
    private PullToRefreshListView mPullRefreshListView;
    private SystemMessageAdapter adapter;

    private List<SystemMessage.MsgListBean> alarms = new ArrayList<>();

    //刷新数据的页数
    private int dataPage = 0;
    //是否显示加载对话框，如果是上拉下拉列表加载数据，则不显示加载对话框
    private boolean isShowLoadDialog = true;
    //每页显示数据的条数
    private int pageSize = 10;
    //临时保存刷新数据前后数据的条数，如果前后数据条数一致，则说明没有新数据，关闭上拉刷新功能
    private int tmpDataSize = 0;
    private int tmpPosition = -1;//临时变量，记录点击的筛选条件的位置
    private static boolean isloadingData = false;//是否正在加载数据，防止在调用数据的同时重复调用方法
    private ListView actualListView;

    //接口需要的参数
    private String dateString = "";//时间
    private String hostID = "";//主机
    private String type = "0";//报警类型
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private ToastUtils toastUtils;
    private int index;

    private String adminId; //获取到的网管的管理员ID
    private DeleteScenePopUpWindow mCancleApplyPopUpWindow;//取消申请


    ///
    SystemMessage.MsgListBean acceptMsg;
    private String msgTyp;
    String limitStatus;
    String limitTime;
    String pession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        toastUtils = ((BaseActivity) getActivity()).toastUtils;
        View view = inflater.inflate(R.layout.fragment_system_message, container, false);
        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lvAlarm);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        textView.setText("没有查询到相应数据");
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        textView.setVisibility(View.GONE);
        ((ViewGroup) mPullRefreshListView.getParent()).addView(textView);
        mPullRefreshListView.setEmptyView(textView);

        final ILoadingLayout startLabels = mPullRefreshListView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
        tmpDataSize = alarms.size();

        //设置默认值
        Date date = new Date();
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
        dateString = myFmt.format(date);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                isShowLoadDialog = false;
                // Do work to refresh the list here.
                // new GetDataTask().execute();
                dataPage = 0;
                isShowLoadDialog = false;
                mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                getSystemInformation(dateString, hostID, type);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                isShowLoadDialog = false;
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getSystemInformation(dateString, hostID, type);
            }
        });

        actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //减去头文件位置，有头文件，position从1开始
                SystemMessage.MsgListBean message=alarms.get(position - actualListView.getHeaderViewsCount());
                if(isApplyJoinNetGate(message)){
                    startActivityForResult(new Intent(getActivity(), SettingsFamilyApplyActivity.class)
                    .putExtra("message",message),100);
                }
            }
        });
        adapter = new SystemMessageAdapter(getActivity(), alarms, false, new SystemMessageAdapter.ClickListener() {
            @Override
            public void accept(SystemMessage.MsgListBean message) {
                acceptMsg = message;

                if (!TextUtils.isEmpty(acceptMsg.getLimitStatus()))
                    limitStatus = acceptMsg.getLimitStatus();
                if (!TextUtils.isEmpty(acceptMsg.getLimitTime()))
                    limitTime = acceptMsg.getLimitTime();

                acceptMessage();
            }

            @Override
            public void reject(SystemMessage.MsgListBean message) {
                rejectMessge(message);
            }
        });
        this.mPullRefreshListView.setAdapter(adapter);
        isPrepared = true;
        lazyLoad();

        //用户取消申请
        UserCancleAppaly();
        return view;
    }

    /**
     * 同意申请
     */
    private void acceptMessage() {


        //用户申请,管理员同意
        if (Constant.USERID.equals(acceptMsg.getFromUser().getId())) {

            msgTyp = "admin_accept";
            SystemMessage.MsgListBean.ToUserBean user = acceptMsg.getToUser();
            Intent intent = new Intent(getActivity(),
                    NetGatePermisionSettingActivity.class);
            User us = new User();
            us.setName(user.getName());
            us.setId(user.getId());
            if (user.getAvatarUrl() != null)
                us.setAvatarUrl(user.getAvatarUrl().toString());
            intent.putExtra("type",0);
            intent.putExtra("user", us);
            intent.putExtra("host", acceptMsg.getHostRealId());
            intent.putExtra("message",acceptMsg);
            startActivityForResult(intent, 1000);


            index = alarms.indexOf(acceptMsg);
        }
        //管理员分享,用户同意
        else {

            msgTyp = "user_accept";
            queryUserPermission(getActivity(), acceptMsg.getToUser().getId(), acceptMsg.getHostRealId());

        }


    }

    /**
     * 拒绝申请
     *
     * @param message
     */
    private void rejectMessge(SystemMessage.MsgListBean message) {
        /** applyStatus: 1、用户申请 2、管理员分享
         // status : 0、待确认     1、用户同意   2、用户拒绝
         //          3、用户取消  　4、管理员同意 5、管理员拒绝
         //          6、管理员取消
         */

        String adminId = message.getFromUser().getId();
        String applayUserId = message.getToUser().getId();

        String applyStatus = message.getExtra().getApplyStatus() + "";
        String hostId = message.getHostRealId();
        String status = message.getExtra().getStatus() + "";

        String updateStatus = FamilyManageController.statusUserReject;

        //管理员拒绝
        if (Constant.USERID.equals(message.getFromUser().getId())) {

            updateStatus = FamilyManageController.statusAdminReject;
//            adminId = message.getFromUser().getId();
//            applayUserId = message.getToUser().getId();
            message.getExtra().setStatus(5);

        } else
            message.getExtra().setStatus(2);

        updateApplyStatus(adminId, applayUserId, applyStatus, hostId, status, updateStatus);
    }

    /**
     * 将用户加入家人
     */
    private void addUserInFamily(final String userId, final String hostId, final String permission) {


        FamilyManageController.getInstance().addUser(getActivity(), userId,
                hostId, permission, limitStatus, limitTime, new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        BaseResult result = new Gson().fromJson(Json, BaseResult.class);

                        if (result.getRet() == 0) {

                            toastUtils.showSuccessWithStatus(getString(R.string.save_success));


                            String adminId = acceptMsg.getFromUser().getId();
                            String applayUserId = acceptMsg.getToUser().getId();
                            String applyStatus = acceptMsg.getExtra().getApplyStatus() + "";
                            String hostId = acceptMsg.getHostRealId();
                            String status = acceptMsg.getExtra().getStatus() + "";
                            String updateStatus = FamilyManageController.statusAdminApply;
                            updateApplyStatus(adminId, applayUserId, applyStatus, hostId, status, updateStatus);

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

    private void updateApplyStatus(String adminId, String applayUserId, final String applyStatus,
                                   String hostId, final String status, final String updateStatus) {
        FamilyManageController.getInstance().updateUserApply(getActivity(),
                adminId,
                applayUserId,
                applyStatus,
                hostId,
                status,
                updateStatus,
                new RequestResultListener() {

                    @Override
                    public void onSuccess(String Json) {
                        Loger.d("updateApplyStatus" + Json);

                        refreshData();
                        adapter.notifyDataSetChanged();
                        pushNotification();
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d("SystemMessageFragment" + Json);
                    }
                });
    }

    private void refreshData(){
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

        // Update the LastUpdatedLabel
        mPullRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        isShowLoadDialog = false;
        // Do work to refresh the list here.
        // new GetDataTask().execute();
        dataPage = 0;
        isShowLoadDialog = false;
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        getSystemInformation(dateString, hostID, type);
    }

    private void pushNotification() {
        AlarmController.getInstance().pushNotification(getActivity(), "", new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
            }
            @Override
            public void onFailed(String json) {
            }
        });
    }

    /**
     * 查询用户权限
     *
     * @param context
     * @param userId
     * @param hostId
     * @param
     */
    private void queryUserPermission(Context context, String userId, String hostId) {
        FamilyManageController.getInstance().queryUserPermission(context, hostId, userId, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() == 0) {
                    Constant.PERMISSIONS = JsonUtil.parseString(json, "permissions");


                    addUserInFamily(acceptMsg.getToUser().getId(), acceptMsg.getHostRealId(), Constant.PERMISSIONS);

                } else {


                    addUserInFamily(acceptMsg.getToUser().getId(), acceptMsg.getHostRealId(), pession);


                }

            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    /**
     * 用户主动发送取消等待申请
     */
    private void UserCancleAppaly() {
        adapter.setUserCancleApplayListener(new SystemMessageAdapter.UserCancleApplayListener() {
            @Override
            public void userCancleApplay(final String hostId, final String adminId) {
                //加提示PopWindow
                String title = getString(R.string.text_prompt);
                String content = getString(R.string.is_cancle_gateway_apply);
                final String successConcent = getString(R.string.success_cancle_gateway_apply);
                mCancleApplyPopUpWindow = new DeleteScenePopUpWindow(getActivity(), title, content, new DeleteScenePopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(int tag) {
                        mCancleApplyPopUpWindow.dismiss();
                        FamilyManageController.getInstance().updateUserApply(getActivity(), adminId, Constant.USERID, "1", hostId, "0", "3", new RequestResultListener() {
                            @Override
                            public void onSuccess(String Json) {
                                L.i("AAAAB onSuccess" + Json);
                                try {
                                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                                    if (result.getRet() != 0) {
                                        return;
                                    }
                                    if (StringUtil.isEmpty(Constant.CURRENTHOSTID)) {
//                                        startActivity(new Intent(getActivity(), LoginSelectListeningActivity.class));
//                                        getActivity().finish();

                                        toastUtils.showSuccessWithStatus(successConcent);
                                        adapter.notifyDataSetChanged();
                                        mPullRefreshListView.setRefreshing();
                                        mPullRefreshListView.onRefreshComplete();

                                    } else {
                                        toastUtils.showSuccessWithStatus(successConcent);
                                        adapter.notifyDataSetChanged();
                                        mPullRefreshListView.setRefreshing();
                                        mPullRefreshListView.onRefreshComplete();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailed(String Json) {
                                L.i("AAAAB onSuccess" + Json);
                            }
                        });
                    }
                });
                if (getView() == null)
                    return;
                mCancleApplyPopUpWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

            }
        });

    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

        if(alarms.size()>0){
            ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(true);
        }
        else{
            ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(false);
        }

        if (Constant.IS_INTERNET_CONN && alarms.size() == 0) {
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
            mPullRefreshListView.setRefreshing(true);
        }

    }

    loadUiHandler myHandler = new loadUiHandler();

    private class loadUiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            if(adapter.getCount()>0){
                ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(true);
            }
            else{
                ((InformationCenterListeningActivity)getActivity()).refreshDelBtn(false);
            }

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();
        }
    }

    private void getSystemInformation(String date, String hostId, String type) {
        //没外网
        if (!NetUtil.checkNet(getContext()) || !Constant.IS_INTERNET_CONN) {
            myHandler.sendEmptyMessageDelayed(0, 1000);
        }
        AlarmController.getInstance().getSystemMessage(getActivity(), hostId, date, pageSize + "", dataPage + "", type, new RequestResultListener() {
            @Override
            public void onSuccess(String Json1) {
                toastUtils.dismiss();
                String Json = Json1.replace("\"extra\":\"\"", "\"extra\":null").replace("\"toUserExtra\":\"\"", "\"toUserExtra\":null");
                L.i("getSystemInformation===" + Json);

                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {

                    if (dataPage == 0) {//第一页清空数据
                        alarms.clear();
                        tmpDataSize = 0;
                    }
                    String kkk=Json.replace(" \"extra\": \"\"", "\"extra\": null").replace("\"toUserExtra\": \"\"", "\"toUserExtra\": null");
                    SystemMessage systemMessage=null;
                    try{
                        systemMessage = new Gson().fromJson(kkk, SystemMessage.class);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                    List<SystemMessage.MsgListBean> list = systemMessage.getMsgList();
                    for (SystemMessage.MsgListBean msg : list) { // 过滤多余的管理员提示消息
                        if (msg.getMsgType() == 20006
                                && Constant.USERID.equals(msg.getFromUser().getId())
                                && !Constant.USERID.equals(msg.getToUser().getId())) {
                            continue;
                        }
                        alarms.add(msg);
                    }
                    //新加数据之前的ckheckBox是否选中数值不变，将新加数据设置为默认值false
                    for (int i = adapter.getIsSelected().size(); i < alarms.size(); i++) {
                        adapter.getIsSelected().put(i, false);
                    }
                    for (int i = adapter.getMsgIdSelected().size(); i < alarms.size(); i++) {
                        adapter.getMsgIdSelected().put(i, "");
                    }

                    //如果获得的数据的条数是pageSize的倍数，说明没有到达最后一页,继续刷新
                    if (alarms.size() % pageSize == 0) {
                        //防止最后一页正好是pageSize的倍数，则根据刷新后有没有新数据来判断是否关闭上拉刷新功能
                        //保存刷新前后的数据
                        if (tmpDataSize != alarms.size()) {
                            tmpDataSize = alarms.size();
                            dataPage++;
                        } else {//如果刷新前后的数据条数一致，说明刷新不出新数据，则关闭上拉刷新功能
                            mPullRefreshListView.onRefreshComplete();
                            mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                    } else {//数据不是pageSize的倍数，说明已到最后一页
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    Message message = new Message();
                    message.what = 0;
                    myHandler.sendMessage(message);
                }
            }

            @Override
            public void onFailed(String json) {
                L.e("getSystemInformation:" + json);
            }
        });
    }

    public void refreshData(String mDate, String mHostid, String mType) {
        dateString = mDate;
        hostID = mHostid;
        type = mType;
        dataPage = 0;
        getSystemInformation(dateString, hostID, type);
    }

    //得到数据
    public List getDatas() {
        return alarms;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == getActivity().RESULT_OK) {


            limitStatus = data.getStringExtra("limitStatus");

            limitTime = data.getStringExtra("limitTime");

            pession = data.getStringExtra("pession");


            Log.v("gl", "limitStatus==" + limitStatus + "limitTime==" + limitTime + "pession===" + pession);


            queryUserPermission(getActivity(), acceptMsg.getToUser().getId(), acceptMsg.getHostRealId());

        }

        if (requestCode == 100 && resultCode == getActivity().RESULT_OK) {
            refreshData();
        }
    }

    public boolean isApplyJoinNetGate(SystemMessage.MsgListBean message) {
        if (Constant.USERID == null || message == null) {
            return false;
        }
        if (message.getFromUser() != null && !StringUtil.isEmpty(message.getFromUser().getId())
                && Constant.USERID.equals(message.getFromUser().getId())) { //主动
            //消息类型(1001:“创建家人申请”,1002:“转让管理员”, 2001:“分享数据”,2002:“取消分享数据”,
            // 2003:“家人授权”,2004:“取消家人授权”,20005:“用户解绑主机”, 20006:“管理员解绑主机”)
            switch (message.getMsgType()) {
                case 1001:
                    if (message.getExtra() != null) {  //applyStatus: 1、主动 2、被动
                        if (message.getExtra().getStatus() == 0) {
                            if (message.getExtra().getApplyStatus() == 1) { //用户申请
                                return true;
                            } else {
                                return false;
                            }

                        } else {
                            return false;
                        }
                    }
                    break;
            }
        } else if (message.getFromUser() != null) { //被动
            //消息类型(1001:“创建家人申请”,1002:“转让管理员”, 2001:“分享数据”,2002:“取消分享数据”,
            // 2003:“家人授权”,2004:“取消家人授权”,20005:“用户解绑主机”, 20006:“管理员解绑主机”)
            switch (message.getMsgType()) {
                case 1001:
                    if (message.getExtra() != null) {
                        if (message.getExtra().getStatus() == 0) {
                            if (message.getExtra().getApplyStatus() == 1) {
                                return false;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                    break;
            }
        } else {
            return false;
        }
        return false;
    }


    private LocalBroadcastManager mLocalBroadcastManager;
    public static String ACTION_REFRESH ="com.boer.delos.SystemMessageFragment.refresh";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLocalBroadcastManager=LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent data) {
            if(data.getAction().equals(ACTION_REFRESH)){
                limitStatus = data.getStringExtra("limitStatus");

                limitTime = data.getStringExtra("limitTime");

                pession = data.getStringExtra("pession");


                Log.v("gl", "limitStatus==" + limitStatus + "limitTime==" + limitTime + "pession===" + pession);

                SystemMessage.MsgListBean message = (SystemMessage.MsgListBean) data.getSerializableExtra("message");
                if(acceptMsg==null){
                    acceptMsg=message;
                }
                queryUserPermission(getActivity(), acceptMsg.getToUser().getId(), acceptMsg.getHostRealId());
            }
        }
    };
}
