package com.boer.delos.activity.scene.scenemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.DeviceHomeActivity;
import com.boer.delos.adapter.ModeTimerExListViewAdapter;
import com.boer.delos.adapter.SceneModeAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.interf.ISimpleInterfaceInt;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.Link;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.ModeActionResult;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.model.TimeTask;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.EditModelNamePopUpWindow;
import com.google.gson.Gson;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/8 0008 16:36
 * @Modify:
 * @ModifyDate:
 */


public class SceneTimerActivity extends CommonBaseActivity implements ISimpleInterfaceInt, ModeTimerExListViewAdapter.ICloseOrOpenListener {
    @Bind(R.id.tv_mode_name)
    TextView mTvModeName;
    @Bind(R.id.tv_manual)
    TextView mTvManual;
    @Bind(R.id.tv_timer)
    TextView mTvTimer;
    @Bind(R.id.tv_timer_show)
    TextView mTvTimerShow;
    @Bind(R.id.rl_timer_setting)
    RelativeLayout mRlTimerSetting;
    //    @Bind(R.id.pullToRefreshListView)
//    PullToRefreshExpandableListView mPullToRefreshListView;
    @Bind(R.id.expandableListView)
    ExpandableListView mExpandableListView;
    @Bind(R.id.ctv_loading)
    CheckedTextView ctv_loading;

    private Link mLinkMode;
    private ModeAct mModeAct;
    private List<ModeDevice> modeDeviceList; //模式下的设备
    private List<ModeDevice> mQueryList; // 请求到的模式下设备提交时对比
    private List<Map<String, Object>> modeMapDeviceLists;
    private SceneModeAdapter mSceneModeAdapter;
    private EditModelNamePopUpWindow editModelNamePopUpWindow;
//    private ListView mListView;

//    private ExpandableListView mExpandableListView;

//    //需要过滤设备
//    private String[] filterDeviceTypes = new String[]{"HGC", "ElecMeter", "WaterMeter", "Water",
//            "Env", "Fall", "Smoke", "Ch4CO", "SOS", "O2CO2", "Camera", "Pannel", "Guard", "Lock",
//            "TableWaterFilter", "AirFilter", "FloorWaterFilter", "Exist", "Water", "Sov", "Gsm",
//            "CurtainSensor","N4"};

    private ModeTimerExListViewAdapter mModeTimerExListViewAdapter;

    private ModeAct mRoomModeAct; // 房间模式
    private int type;
    @Override
    protected int initLayout() {
        return R.layout.activity_scene_timer;
    }

    @Override
    protected void initView() {

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        }
        mLinkMode = (Link) bundle.getSerializable("link");
        mRoomModeAct = (ModeAct) bundle.getSerializable("room");
        type=bundle.getInt("type");
        // 全剧模式
        if (mLinkMode != null) {
            String name=TextUtils.isEmpty(mLinkMode.getTag()) ? mLinkMode.getName() : mLinkMode.getTag();
            mTvModeName.setText(name);
            if(!name.endsWith("模式")){
                name+="模式";
            }
            tlTitleLayout.setTitle(name);

        }
        //房间模式
        if (mRoomModeAct != null) {
            String name=TextUtils.isEmpty(mRoomModeAct.getTag()) ? mRoomModeAct.getName() : mRoomModeAct.getTag();
            mTvModeName.setText(name);
            if(!name.endsWith("模式")){
                name+="模式";
            }
            tlTitleLayout.setTitle(name);

        }
        tlTitleLayout.setRightText(getString(R.string.text_save));
    }

    @Override
    protected void initData() {
        modeMapDeviceLists = new ArrayList<>();
        modeDeviceList = new ArrayList<>();
        mQueryList = new ArrayList<>();

        mModeTimerExListViewAdapter = new ModeTimerExListViewAdapter(this, modeMapDeviceLists, this);
        mModeTimerExListViewAdapter.setmCloseListener(this);
        mExpandableListView.setAdapter(mModeTimerExListViewAdapter);
        mExpandableListView.setGroupIndicator(null);
        if (mLinkMode != null) {
            requestWithRoomModel(mLinkMode.getName(), null);
        }
        if (mRoomModeAct != null) {
//            requestWithRoomModel(null, mRoomModeAct.getRoomId());
            dealWithData(mRoomModeAct);

        }
    }

    @Override
    protected void initAction() {
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                return !mModeTimerExListViewAdapter.getFlagList().get(i);
            }
        });
    }

    @OnClick({R.id.iv_mode_name_edit, R.id.tv_manual,
            R.id.tv_timer, R.id.tv_timer_show,
            R.id.rl_timer_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_mode_name_edit:
                changeModeName();
                break;
            case R.id.tv_manual:

                if (mModeAct != null) {
                    closeOrOpenMode(false, mModeAct);
                    updateTriggerView(false, mModeAct);
                }
                if (mRoomModeAct != null) {
                    closeOrOpenMode(false, mRoomModeAct);
                    updateTriggerView(false, mRoomModeAct);
                }



                break;
            case R.id.tv_timer:

                if (mModeAct != null) {
                    updateTriggerView(true, mModeAct);
                    closeOrOpenMode(true, mModeAct);
                }
                if (mRoomModeAct != null) {
                    updateTriggerView(true, mRoomModeAct);
                    closeOrOpenMode(true, mRoomModeAct);
                }


                Intent intent = new Intent(this, SceneModeTimerActivity.class);
                Bundle bundle = new Bundle();
                if (mModeAct != null) {
                    if (mModeAct.getTimeTask() != null) {
                        bundle.putSerializable("task", mModeAct.getTimeTask());
                    }

                    bundle.putString("modeId", mModeAct.getModeId() + "");

                }
                if (mRoomModeAct != null) {
                    if (mRoomModeAct.getTimeTask() != null) {
                        bundle.putSerializable("task", mRoomModeAct.getTimeTask());
                    }
                    if (!TextUtils.isEmpty(mRoomModeAct.getModeId())) {
                        bundle.putString("modeId", mRoomModeAct.getModeId() + "");
                    }

                }

                intent.putExtras(bundle);
                startActivityForResult(intent, 200);
                break;
            case R.id.tv_timer_show:
            case R.id.rl_timer_setting:
                //TODO 设置定时时间
//                Intent intent = new Intent(this, SceneModeTimerActivity.class);
//                Bundle bundle = new Bundle();
//                if (mModeAct != null) {
//                    if (mModeAct.getTimeTask() != null) {
//                        bundle.putSerializable("task", mModeAct.getTimeTask());
//                    }
//
//                    bundle.putString("modeId", mModeAct.getModeId() + "");
//
//                }
//                if (mRoomModeAct != null) {
//                    if (mRoomModeAct.getTimeTask() != null) {
//                        bundle.putSerializable("task", mRoomModeAct.getTimeTask());
//                    }
//                    if (!TextUtils.isEmpty(mRoomModeAct.getModeId())) {
//                        bundle.putString("modeId", mRoomModeAct.getModeId() + "");
//                    }
//
//                }
//
//                intent.putExtras(bundle);
//                startActivityForResult(intent, 200);

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            TimeTask timeTask = (TimeTask) data.getSerializableExtra("TIME_TASK");
            if (mModeAct != null) {
                mModeAct.setTimeTask(timeTask);
                updateTriggerView(true, mModeAct);
                return;
            }
            if (mRoomModeAct != null) {
                mRoomModeAct.setTimeTask(timeTask);
                updateTriggerView(true, mRoomModeAct);
            }
        }
    }

    private void changeModeName() {
        if (editModelNamePopUpWindow != null && editModelNamePopUpWindow.isShowing()) {
            editModelNamePopUpWindow.dismiss();
        }
        editModelNamePopUpWindow = new EditModelNamePopUpWindow(SceneTimerActivity.this);
        editModelNamePopUpWindow.setStringListener(new ISimpleInterfaceString() {
            @Override
            public void clickListener(String tag) {
                if (mLinkMode != null)
                    updateModelName(String.valueOf(mLinkMode.getModeId()), tag, null);
                if (mRoomModeAct != null && !TextUtils.isEmpty(mRoomModeAct.getModeId())) {
                    updateModelName(String.valueOf(mRoomModeAct.getModeId()),
                            tag, mRoomModeAct.getSerialNo());
                }

            }
        });
        editModelNamePopUpWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        editModelNamePopUpWindow.etModelName.requestFocus();
        editModelNamePopUpWindow.update();

        InputMethodManager imm = (InputMethodManager) getSystemService(SceneTimerActivity.this.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editModelNamePopUpWindow.etModelName, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    private void updateModelName(final String modeId, final String modelName, String serialNo) {
        toastUtils.showProgress(getString(R.string.progress_commit_ing));

        LinkManageController.getInstance().modifyModelName(this, modeId, modelName, serialNo, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                Log.i("gwq", "modifyModelName：" + Json);
                BaseResult result = GsonUtil.getObject(Json, BaseResult.class);

                if (result.getRet() == 0) {
                    toastUtils.showSuccessWithStatus(getString(R.string.edit_success));
                    String name=modelName;
                    mTvModeName.setText(name);
                    if(!name.endsWith("模式")){
                        name+="模式";
                    }
                    tlTitleLayout.setTitle(name);
                    if (mLinkMode != null) {
                        mLinkMode.setTag(modelName);
                        mModeAct.setTag(modelName);
                        for(Link link:Constant.GLOBAL_MODE){
                            if((link.getModeId()+"").equals(modeId)){
                                link.setTag(modelName);
                                break;
                            }
                        }
                    }
                    if (mRoomModeAct != null) {
                        mRoomModeAct.setTag(modelName);
                        LocalBroadcastManager.getInstance(SceneTimerActivity.this).sendBroadcast(new Intent()
                                .setAction(DeviceHomeActivity.ACTION_MODIFY_ROOM_MODE)
                        .putExtra("modeact",mRoomModeAct));
                    }

                } else
                    toastUtils.showErrorWithStatus(getString(R.string.text_modify_success));
            }

            @Override
            public void onFailed(String json) {
                Log.i("gwq", "modifyModelName error：" + json);
                if (toastUtils != null) {
                    toastUtils.dismiss();
                }
//                toastUtils.showErrorWithStatus(json);
            }
        });


    }

    private void closeOrOpenMode(boolean openMode, ModeAct modeAct) {
        mTvManual.setEnabled(openMode);
        mTvTimer.setEnabled(!openMode);
        updateTriggerView(openMode, modeAct);

        if (modeAct.getTimeTask() != null && modeAct.getModeId() != null) {
            openOrCloseTimerTask(modeAct.getTimeTask().getId() + "",
                    modeAct.getModeId(), openMode ? "on" : "off");

        } else if (modeAct.getTimeTask() == null) {
            // 模式ID 没有定时任务 不做处理
            return;
        }
    }

    /**
     * 开启或者关闭定时任务
     *
     * @param id     定时任务Id
     * @param modeId 模式ID
     * @param OFF    开关 on 、off
     */
    private void openOrCloseTimerTask(String id, final String modeId, final String OFF) {
        LinkManageController.getInstance().switchTimeTask(this, id, modeId, OFF, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                //TODO　mModeAct 更新
                L.i(Json);
                BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showErrorWithStatus(result.getMsg());
                    return;
                }
                if (mModeAct != null) {
                    mModeAct.getTimeTask().setOn(OFF);
                }
                if (mRoomModeAct != null) {
                    mModeAct.getTimeTask().setOn(OFF);
                }
            }

            @Override
            public void onFailed(String Json) {
                L.i(Json);
            }
        });
    }

    /**
     * 查询模式
     *
     * @param name   全剧模式
     * @param roomId 房间模式
     */
    private void requestWithRoomModel(String name, String roomId) {
        LinkManageController.getInstance().showRoomModel(this, name, roomId,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {

                        Log.d("requestWithRoomModel ", Json);
                        toastUtils.dismiss();
                        try {
                            // 全剧模式
                            if (mLinkMode != null) {

                                ModeActionResult result = new Gson().fromJson(Json, ModeActionResult.class);
                                if (result.getRet() != 0) {
                                    toastUtils.showErrorWithStatus(result.getMsg());
                                    return;
                                }
                                mModeAct = result.getResponse();
                                if (mModeAct == null) {
                                    mModeAct = new ModeAct();
                                }
                                dealWithData(mModeAct);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mHandler != null)
                                mHandler.sendEmptyMessageDelayed(1, 2000);
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (mHandler != null)
                            mHandler.sendEmptyMessageDelayed(1, 2000);
                        if (toastUtils != null)
                            toastUtils.dismiss();
                    }
                });
    }


    private void dealWithData(ModeAct modeAct) {
        //判断是否有定时任务
        if (modeAct.getTimeTask() != null && !TextUtils.isEmpty(modeAct.getTimeTask().getOn())) {
            if ("on".equals(modeAct.getTimeTask().getOn())) {
                if (mModeAct != null) {
                    updateTriggerView(true, mModeAct);
                }
                if (mRoomModeAct != null) {
                    updateTriggerView(true, mRoomModeAct);
                }
            } else {
                if (mModeAct != null) {
                    updateTriggerView(false, mModeAct);
                }
                if (mRoomModeAct != null) {
                    updateTriggerView(false, mRoomModeAct);
                }
            }
        }
        else{
            //默认手动
//            if (mModeAct != null) {
//                updateTriggerView(false, mModeAct);
//            }
//            if (mRoomModeAct != null) {
//                updateTriggerView(false, mRoomModeAct);
//            }
        }
        modeDeviceList.clear();
        mQueryList.clear();
        if (modeAct.getDevicelist() == null || modeAct.getDevicelist().size() == 0) {
            if (modeAct.getDeviceList() != null) {
                modeDeviceList.addAll(modeAct.getDeviceList());
                //保存用
                mQueryList.addAll(modeAct.getDeviceList());
            }

        } else {

            if (modeAct.getDevicelist() != null) {
                modeDeviceList.addAll(modeAct.getDevicelist());
                //保存用

                mQueryList.addAll(modeAct.getDevicelist());
            }
        }


        for (ModeDevice modeDevice : modeDeviceList) {
            modeDevice.setHave(true);

        }
        queryAllDevice();

    }

    //    /**
//     * 更新触发条件文本
//     */
    private void updateTriggerView(boolean isTimer, ModeAct modeAct) {
        if (isTimer) {
            mTvManual.setTextColor(getResources().getColor(R.color.gray_et_hint));
            mTvTimer.setTextColor(getResources().getColor(R.color.gray_et_text));
//            mRlTimerSetting.setVisibility(View.VISIBLE);

            if (modeAct.getTimeTask() == null) {
                return;
            }

            String weekStr=Constant.getTimerWeekSetting(modeAct.getTimeTask());
            if(weekStr.equals("周一|二|三|四|五|六|日")||weekStr.equals("周日|一|二|三|四|五|六")){
                weekStr="每天";
            }
            String tempTime=Constant.getTimeSetting(modeAct.getTimeTask()) + " " +
                    weekStr;
            mTvTimerShow.setText(tempTime.trim().equals("null")?"":tempTime);
            if(mTvTimerShow.getText().toString().trim().equals("")){
                mRlTimerSetting.setVisibility(View.GONE);
            }
            else{
                mRlTimerSetting.setVisibility(View.VISIBLE);
            }
        } else {
            mTvManual.setTextColor(getResources().getColor(R.color.gray_et_text));
            mTvTimer.setTextColor(getResources().getColor(R.color.gray_et_hint));
            mRlTimerSetting.setVisibility(View.GONE);
            mTvTimerShow.setText("");
        }
    }

    @Override
    public void rightViewClick() {

        try {
            if (mLinkMode != null) {
                addSceneModeDevice(mModeAct);
                return;
            }

            if (mRoomModeAct != null) {
                addSceneModeDevice(mRoomModeAct);
            }
            if (true) return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模式中添加device
     */
    private void addSceneModeDevice(ModeAct modeAct) {
        List<ModeDevice> modeDevices = new ArrayList<>();
//        List<Boolean> list = mSceneModeAdapter.getFlagList();
        List<Boolean> list = mModeTimerExListViewAdapter.getFlagList();


        int index = 0;
        for (boolean a : list) {
            if (a) {
                modeDevices.add(modeDeviceList.get(index));

            }
            index++;
        }
        if (modeDevices.containsAll(mQueryList) && modeDevices.size() == mQueryList.size()) {
            modeAct.setUpdateTask("0");
        }
        modeAct.setDeviceList(null);
        modeAct.setDevicelist(modeDevices);
        modeAct.setUpdateTask("1");
//        if (true) {
//            return;
//        }
        if (modeAct.getTimeTask() != null) {
            modeAct.getTimeTask().setOn(mRlTimerSetting.getVisibility()==View.GONE? "off" : "on");
            if (modeAct.getTimeTask().getRepeat().size() == 0
                    && TextUtils.isEmpty(modeAct.getTimeTask().getTriggerTime())) {
                modeAct.getTimeTask().setOn("off");
            }
        }
        LinkManageController.getInstance().updateRoomMode(this, modeAct, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(getString(R.string.link_modeActivity_updataMode_fail));
                        mModeTimerExListViewAdapter.setListData(modeMapDeviceLists);
                        mHandler.sendEmptyMessageDelayed(1, 2000);
                        return;
                    }
                    toastUtils.showSuccessWithStatus(getString(R.string.save_success));

                    mHandler.sendEmptyMessageDelayed(-1, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastUtils.dismiss();
                }
            }

            @Override
            public void onFailed(String Json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(Json);
            }
        });
    }

    /**
     * 查询所有设备
     */

    public void queryAllDevice() {
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                json = StringUtil.deviceStateStringReplaceMap(json);
                DeviceRelateResult result = GsonUtil.getObject(json, DeviceRelateResult.class);
                if (result.getRet() != 0) {
                    mHandler.sendEmptyMessageDelayed(1, 2000);
                    return;
                }
                if (null == Constant.DEVICE_RELATE) {
                    Constant.DEVICE_RELATE = new ArrayList<>();
                }
                //判断设备信息是否有变更
//                String md5Value = MD5(json);
//                if (!StringUtil.isEmpty(Constant.DEVICE_MD5_VALUE)
//                        && Constant.DEVICE_MD5_VALUE.equals(md5Value)
//                        && Constant.GATEWAY != null) {
//                    return;
//                }
                Constant.DEVICE_RELATE = result.getResponse();
                if (result.getResponse() == null || result.getResponse().size() == 0) {
                    return;
                }
                updateUI(Constant.DEVICE_RELATE);
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onFailed(String json) {
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        });
    }

    private void updateUI(List<DeviceRelate> deviceRelates) {
        if (deviceRelates != null && deviceRelates.size() != 0) {
            List<DeviceRelate> list = new ArrayList<>();
            for (DeviceRelate deviceRelate : deviceRelates) {
                Device device = deviceRelate.getDeviceProp();
                if (device == null) {
                    continue;
                }

                if(type==1){
                    if(mRoomModeAct!=null){
                        String tempRoomId=mRoomModeAct.getRoomId();
                        if(!(!TextUtils.isEmpty(tempRoomId)&&!TextUtils.isEmpty(device.getRoomId())
                                &&device.getRoomId().equals(tempRoomId))){
                            continue;
                        }
                    }
                }


                //过滤设备
                List<String> filterTypes = Arrays.asList(ConstantDeviceType.FILTER_DEVICE_TYPES_1);
                if (filterTypes.contains(device.getType())) {
                    continue;
                }
                ModeDevice m = new ModeDevice();
                m.setDeviceAddr(device.getAddr());
                m.setDevicetype(device.getType());
                if (modeDeviceList.contains(m)) {
                    continue;
                }
                list.add(deviceRelate);
            }
            List<ModeDevice> listModeDevice = Constant.modeDeviceListFromDeviceRelateList(list);
            modeDeviceList.addAll(listModeDevice);
        }
        modeMapDeviceLists.clear();
        modeMapDeviceLists.addAll(Constant.convertModeDeviceToList(modeDeviceList));

        mModeTimerExListViewAdapter.setListData(modeMapDeviceLists);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                case 1:
//                    if (mPullToRefreshListView != null)
//                        mPullToRefreshListView.onRefreshComplete();

                    break;
                case -1:
                    finish();
                    break;
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    // 点击展开分组
    @Override
    public void clickListener(int tag) {
        if (mExpandableListView != null) {
            try {
                mExpandableListView.collapseGroup(tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closeOrOpen(int position, boolean open) {
        if (mExpandableListView != null) {
            try {
                if (!open)
                    mExpandableListView.collapseGroup(position);
                else
                    mExpandableListView.expandGroup(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
