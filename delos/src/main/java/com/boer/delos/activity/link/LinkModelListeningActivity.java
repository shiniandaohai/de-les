package com.boer.delos.activity.link;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.scenemanager.SceneModeTimerActivity;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.LinkModelAdapter;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.EventCode;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.Link;
import com.boer.delos.model.LinkPlan;
import com.boer.delos.model.LinkPlanAlarmAct;
import com.boer.delos.model.LinkPlanResult;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.ModeActionResult;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.model.RoomModeActionResult;
import com.boer.delos.model.TimeTask;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.TimeUtil;
import com.boer.delos.view.popupWindow.EditModelNamePopUpWindow;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XieQingTing
 * @Description: 模式配置界面
 * create at 2016/4/12 9:42
 */
public class LinkModelListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    public TextView tvModelName;
    private ImageView ivModelEdit;
    private ListView lvDeviceList;
    private TextView tvConfirm;
    private ExpandableListView elvModelDevice;
    private EditModelNamePopUpWindow editModelNamePopUpWindow;
    private PercentRelativeLayout rlEditName;
    private String title;
    private View view;
    private LinkModelAdapter adapter;
    private List<Map<String, Object>> deviceList = new ArrayList<>();

    private int fromActivity;
    private String roomModelId;
    private Intent intent;
    private Link mode;
    private Device device;
    private ModeAct modeAct;
    private List<ModeDevice> modeDeviceList;
    private LinkPlan plan;
    private TextView tvManual;
    private TextView tvTimer;
    private PercentRelativeLayout rlTimer;
    private TextView tvTimerSetting;
    //手动
    private boolean isTimer;
    private int timerSetting;
    private PercentRelativeLayout rlTiggerCondition;
    private boolean isBtnCommit = false;

    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_link_modle, null);
        setContentView(view);

        intent = getIntent();
        fromActivity = intent.getIntExtra("ActivityName", -1);
        //全局模式配置
        mode = (Link) intent.getSerializableExtra("mode");
        //设备模式配置
        device = (Device) intent.getSerializableExtra("device");
        //房间模式配置
        modeAct = (ModeAct) intent.getSerializableExtra("modeAct");

        switch (fromActivity) {
            case Constant.MODE_SETTING_LINKMODE_DEVICE:
                title = device.getName() + "联动配置";
                break;
            case Constant.MODE_SETTING_LINKMODE_GLOBAL:
                title = mode.getName();
                break;
            case Constant.MODE_SETTING_LINKMODE_ROOM:
                title = modeAct.getTag();
                break;
        }

        initView();
        initData();
    }


    private void initView() {
        initTopBar(title, null, true, true);
        mTvTitle = (TextView) findViewById(R.id.tvTitle);
        tvConfirm = (TextView) findViewById(R.id.tvConfirm);
//        this.elvModelDevice= (ExpandableListView) findViewById(R.id.elvModelDevice);
//        this.elvModelDevice.setGroupIndicator(null);
        lvDeviceList = (ListView) findViewById(R.id.lvDeviceList);
        ivModelEdit = (ImageView) findViewById(R.id.ivModelEdit);
        tvModelName = (TextView) findViewById(R.id.tvModelName);
        rlEditName = (PercentRelativeLayout) findViewById(R.id.rlEditName);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        ivRight.setImageResource(R.drawable.ic_link_model_add);
        ivRight.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        ivModelEdit.setOnClickListener(this);

        //触发条件
        rlTiggerCondition = (PercentRelativeLayout) findViewById(R.id.rlTiggerCondition);

        //手动
        tvManual = (TextView) findViewById(R.id.tvManual);
        tvManual.setOnClickListener(this);
        //自动
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvTimer.setOnClickListener(this);

        //定时层
        rlTimer = (PercentRelativeLayout) findViewById(R.id.rlTimer);
        rlTimer.setOnClickListener(this);
        //定时显示
        tvTimerSetting = (TextView) findViewById(R.id.tvTimerSetting);

        if (mode != null && mode.getTag() != null) {
            this.tvModelName.setText(mode.getTag());
        } else if (mode != null && mode.getTag() == null) {
            int index = mode.getName().indexOf("模式配置");
            String name = mode.getName().substring(0, index);
            tvModelName.setText(name);
        }


        switch (fromActivity) {
            case Constant.MODE_SETTING_LINKMODE_DEVICE:
                rlEditName.setVisibility(View.GONE);
                break;
            case Constant.MODE_SETTING_LINKMODE_GLOBAL:
                rlTiggerCondition.setVisibility(View.VISIBLE);
                break;
            case Constant.MODE_SETTING_LINKMODE_ROOM:
                if (modeAct.getModeId() == null) {
                    rlTiggerCondition.setVisibility(View.GONE);
                } else {
                    rlTiggerCondition.setVisibility(View.VISIBLE);
                }
                if (modeAct != null && modeAct.getTag() != null) {
                    this.tvModelName.setText(modeAct.getTag());
                }
                break;

        }
    }

    private void initData() {
        adapter = new LinkModelAdapter(this, deviceList);
        lvDeviceList.setAdapter(adapter);
        switch (fromActivity) {
            case Constant.MODE_SETTING_LINKMODE_DEVICE:
                if ("Lock".equals(device.getType())) {
                    requestWithRoomModel("开门模式", null);
                } else {
                    requestModeWithDevice();
                }
                break;
            case Constant.MODE_SETTING_LINKMODE_GLOBAL:
                requestWithRoomModel(mode.getName(), null);
                break;
            case Constant.MODE_SETTING_LINKMODE_ROOM:
                if (modeAct.getTimeTask() != null) {
                    if ("on".equals(modeAct.getTimeTask().getOn())) {
                        isTimer = true;
                    } else {
                        isTimer = false;
                    }
                }
                updateTiggerView();
                //直接获取传入的设备列表
                modeDeviceList = modeAct.getDeviceList();
                if (modeDeviceList.size() > 0) {
                    deviceList.addAll(Constant.convertModeDeviceToList(modeDeviceList));
                    adapter.notifyDataSetChanged();
                }
                if (modeDeviceList == null) {
                    modeDeviceList = new ArrayList<>();
                }
                requestWithModeUpdate(Constant.MODE_SETTING_LINKMODE_ROOM);

                break;
        }
    }


    /**
     * 查询模式
     *
     * @param name
     * @param modeId
     */
    private void requestWithRoomModel(String name, String modeId) {
        toastUtils.showProgress("正在载入....");
        LinkManageController.getInstance().showRoomModel(this, name, modeId,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        toastUtils.dismiss();
                        try {
                            ModeActionResult result = new Gson().fromJson(Json, ModeActionResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                modeAct = result.getResponse();
                                if (modeAct.getTag() != null) {
                                    tvModelName.setText(modeAct.getTag());
                                    tvTitle.setText(modeAct.getTag());
                                }
                                //判断是否有定时任务
                                if (modeAct.getTimeTask() != null && modeAct.getTimeTask().getOn() != null) {
                                    if ("on".equals(modeAct.getTimeTask().getOn())) {
                                        isTimer = true;
                                    } else {
                                        isTimer = false;
                                    }
                                    updateTiggerView();
                                }
                                modeDeviceList = modeAct.getDeviceList();
                                if (modeDeviceList.size() > 0) {
                                    deviceList.addAll(Constant.convertModeDeviceToList(modeDeviceList));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (toastUtils != null)
                            toastUtils.dismiss();
                    }
                });
    }


    /**
     * 请求联动模式设置
     */
    private void requestModeWithDevice() {
        toastUtils.showProgress("正在载入....");
        LinkManageController.getInstance().planShow(this, device.getAddr(), device.getType(),
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        toastUtils.dismiss();
                        try {
                            LinkPlanResult result = new Gson().fromJson(Json, LinkPlanResult.class);
                            if (result.getRet() != 0) {
                                toastUtils.showErrorWithStatus(result.getMsg());
                            } else {
                                plan = result.getResponse();
                                modeDeviceList = plan.getAlarmAct().getDevicelist();
                                if (modeDeviceList.size() > 0) {
                                    deviceList.addAll(Constant.convertModeDeviceToList(modeDeviceList));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {
                        if (toastUtils != null)
                            toastUtils.dismiss();
                    }
                });
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            //添加设备
            case R.id.ivRight:
                Intent intent = new Intent(this, ChooseDeviceActivity.class);
                intent.putExtra("ActivityName", fromActivity);
                intent.putExtra("modeDeviceList", (Serializable) modeDeviceList);
                intent.putExtra("title", title);
                startActivityForResult(intent, EventCode.CHOOSE_DEVICE_ACTIVITY);
                break;
            case R.id.tvConfirm:
                switch (fromActivity) {
                    case Constant.MODE_SETTING_LINKMODE_DEVICE:
                        //联动计划
                        if (plan == null) {
                            requestWithModeUpdate(Constant.MODE_SETTING_LINKMODE_DEVICE);
                            return;
                        }
                        requestWithPlanUpdate();
                        break;
                    case Constant.MODE_SETTING_LINKMODE_GLOBAL:
//                        //添加设备
                        requestWithModeUpdate(Constant.MODE_SETTING_LINKMODE_GLOBAL);
                        break;
                    case Constant.MODE_SETTING_LINKMODE_ROOM:
                        if (modeAct.getModeId() == null) {
                            return;
                        }
                        requestWithModeUpdate(Constant.MODE_SETTING_LINKMODE_ROOM);
                        break;
                }

                finish();
                break;
            case R.id.ivModelEdit:
                editModelNamePopUpWindow = new EditModelNamePopUpWindow(LinkModelListeningActivity.this, new EditModelNamePopUpWindow.ClickResultListener() {

                    @Override
                    public void ClickResult(int tag) {
                        if (modeAct != null) {
                            updateModelName(modeAct.getModeId());
                        }
                        editModelNamePopUpWindow.dismiss();
                    }
                });
                editModelNamePopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                editModelNamePopUpWindow.etModelName.requestFocus();
                editModelNamePopUpWindow.update();

                InputMethodManager imm = (InputMethodManager) getSystemService(LinkModelListeningActivity.this.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editModelNamePopUpWindow.etModelName, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            case R.id.rlTimer:
                intent = new Intent(this, SceneModeTimerActivity.class);
                Bundle bundle = new Bundle();
                if (modeAct.getTimeTask() != null) {
                    bundle.putSerializable("task", modeAct.getTimeTask());
                }
                bundle.putString("modeId", modeAct.getModeId() + "");
//                    bundle.saveString("modeId", mode.getModeId() + "");

                intent.putExtras(bundle);
                startActivityForResult(intent, EventCode.LINK_MODE_TIMER_ACTIVITY);
                break;
            //手动
            case R.id.tvManual:
                if (!isTimer) {
                    return;
                }
                isTimer = false;
                updateTiggerView();
                tvManual.setEnabled(false);
                tvTimer.setEnabled(true);
                if (modeAct.getTimeTask() != null && modeAct.getModeId() != null) {
                    openOrcCloseTimerTask(modeAct.getTimeTask().getId() + "", modeAct.getModeId(), "off");
                } else if (modeAct.getTimeTask() == null && modeAct.getModeId() != null) {
                    // 模式ID 没有定时任务 不做处理
                    return;
                } else if (modeAct.getTimeTask() == null) {
                    //查询modeActId
                    LinkManageController.getInstance().showRoomModel(this, mode.getName(), null,
                            new RequestResultListener() {

                                @Override
                                public void onSuccess(String Json) {
                                    try {
                                        ModeActionResult result = new Gson().fromJson(Json, ModeActionResult.class);
                                        if (result.getRet() != 0) {
                                            toastUtils.showErrorWithStatus(result.getMsg());
                                        } else {
                                            modeAct = result.getResponse();

                                            openOrcCloseTimerTask(modeAct.getTimeTask().getId() + "", modeAct.getModeId(), "off");
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
                break;
            //定时
            case R.id.tvTimer:
                if (isTimer) {
                    return;
                }
                isTimer = true;
                updateTiggerView();
                tvManual.setEnabled(true);
                tvTimer.setEnabled(false);
                if (modeAct.getTimeTask() != null) {
                    switch (fromActivity) {
                        case Constant.MODE_SETTING_LINKMODE_DEVICE:

                            break;
                        case Constant.MODE_SETTING_LINKMODE_GLOBAL:
                            if (mode == null) {
                                return;
                            }
                            break;
                        case Constant.MODE_SETTING_LINKMODE_ROOM:

                            break;
                    }
                    openOrcCloseTimerTask(modeAct.getTimeTask().getId() + "", modeAct.getModeId(), "on");
                }
                break;
        }

    }

    /**
     * 更新触发条件文本
     */
    private void updateTiggerView() {
        if (isTimer) {
            tvManual.setTextColor(getResources().getColor(R.color.light_gray_text));
            tvTimer.setTextColor(Color.BLACK);
            rlTimer.setVisibility(View.VISIBLE);

            if (modeAct.getTimeTask() == null) {
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTimerSetting.setText(Constant.getTimeSetting(modeAct.getTimeTask()) + " " +
                            Constant.getTimerWeekSetting(modeAct.getTimeTask()));
                }
            });

        } else {
            tvManual.setTextColor(Color.BLACK);
            tvTimer.setTextColor(getResources().getColor(R.color.light_gray_text));
            rlTimer.setVisibility(View.GONE);
            tvTimerSetting.setText("");
        }
    }

    /**
     * 开启或者关闭定时任务
     *
     * @param id     定时任务Id
     * @param modeId 模式ID
     * @param OFF    开关 on 、off
     */
    private void openOrcCloseTimerTask(String id, final String modeId, final String OFF) {
        LinkManageController.getInstance().switchTimeTask(this, id, modeId, OFF, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                //modeAct 更新
                L.i(Json);
                BaseResult result = GsonUtil.getObject(Json, BaseResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showErrorWithStatus(result.getMsg());
                    return;
                }

                modeAct.getTimeTask().setOn(OFF);

            }

            @Override
            public void onFailed(String Json) {
                L.i(Json);
            }
        });
    }

    /**
     * 联动更新
     */
    private void requestWithPlanUpdate() {
        List<ModeDevice> list = Constant.deviceStatusHandlerWithModeDeviceList(modeDeviceList, deviceList);
        LinkPlanAlarmAct alarmAct = new LinkPlanAlarmAct();
        alarmAct.setDevicelist(list);
        plan.setAlarmAct(alarmAct);
        LinkManageController.getInstance().planUpdate(this, plan, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {

            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }


    /**
     * 模式更新
     */
    private void requestWithModeUpdate(final int fromActivity) {
        //新建模式
        if (modeAct.getModeId() == null) {
            long currentTime = new Date().getTime() / 1000;
            modeAct.setName(TimeUtil.formatStamp2Time(currentTime, TimeUtil.FORMAT_DATE_TIME2));
            modeAct.setTimestamp((int) currentTime);
        }
        List<ModeDevice> list = Constant.deviceStatusHandlerWithModeDeviceList(modeDeviceList, deviceList);
        modeAct.setDeviceList(list);
        LinkManageController.getInstance().updateRoomMode(this, modeAct, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(getString(R.string.link_modeActivity_updataMode_fail));
                        return;
                    }
                    dealWithModeUpdateResult(fromActivity, Json);

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

    private void dealWithModeUpdateResult(int fromActivity, String Json) {
        switch (fromActivity) {
            case Constant.MODE_SETTING_LINKMODE_DEVICE:

                break;
            case Constant.MODE_SETTING_LINKMODE_GLOBAL:

                break;
            case Constant.MODE_SETTING_LINKMODE_ROOM:
                ModeActionResult result = new Gson().fromJson(Json, ModeActionResult.class);
                modeAct = result.getResponse();
                setResult(RESULT_OK);
                if (!isBtnCommit) {
//                    toastUtils.showSuccessWithStatus("创建成功，可以操作"); //房间模式时
                    rlTiggerCondition.setVisibility(View.VISIBLE);
                    return;
                }
                finish();
                break;

        }
    }

    /**
     * 修改模式名称《TAG》
     */

    private void updateModelName(String modeId) {
        final String modelName = editModelNamePopUpWindow.getModelName();
        if (StringUtil.isEmpty(modelName)) {
            BaseApplication.showToast("请输入模式名称");
            return;
        }
        if (modelName.length() > 4) {
            BaseApplication.showToast("长度不能大于4");
            return;
        }
        Pattern pa = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pa.matcher(modelName);
        if (!matcher.find()) {
            BaseApplication.showToast("模式名称应为中文");
            return;
        }

        toastUtils.showProgress("正在提交...");
        String serialNo = null;
        if (modeAct != null) {
            serialNo = modeAct.getSerialNo();
        }

        LinkManageController.getInstance().modifyModelName(this, modeId, modelName, serialNo, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i("modifyModelName：" + Json);
                BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showErrorWithStatus("修改失败");
                    return;
                }
                modeAct.setTag(modelName);
                tvModelName.setText(modelName);
                toastUtils.showSuccessWithStatus("修改成功");
                mTvTitle.setText(modelName);

            }

            @Override
            public void onFailed(String json) {
                L.e("modifyModelName error：" + json);
//                toastUtils.showErrorWithStatus(json);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                List<Device> selectDeviceList = (List<Device>) data.getSerializableExtra("selectDeviceList");
                modeDeviceList = Constant.modeDeviceListFromDeviceList(selectDeviceList);
                deviceList.clear();
                deviceList.addAll(Constant.convertModeDeviceToList(modeDeviceList));
                adapter.notifyDataSetChanged();
                break;
//            case EventCode.LINK_MODE_ACTIVITY:
//
//                break;
        }
        //定时任务设置完成，刷新界面
        if (requestCode == EventCode.LINK_MODE_TIMER_ACTIVITY && resultCode == EventCode.LINK_MODE_ACTIVITY) { // SceneModeTimerActivity
//            getRoomMode();
            refreshTimerTaskByLinkModeTimerLA(data);
        }
    }

    private void refreshTimerTaskByLinkModeTimerLA(Intent data) {//SceneModeTimerActivity
        TimeTask timeTask = (TimeTask) data.getSerializableExtra("TIME_TASK");
        modeAct.setTimeTask(timeTask);
        updateTiggerView();
    }

    /**
     * 查询房间模式
     */
    private void getRoomMode() {
        toastUtils.showProgress("正在同步数据");
        RoomController.getInstance().showMode(this, null, modeAct.getRoomId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    RoomModeActionResult result = new Gson().fromJson(Json, RoomModeActionResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    for (ModeAct modeActTemp : result.getResponse()) {
                        if (modeActTemp.getSerialNo().equals(modeAct.getSerialNo())) {
                            modeAct = modeActTemp;
                            toastUtils.showSuccessWithStatus("数据同步完成");
                            updateTiggerView();
                        }
                    }
                } catch (Exception e) {
                    toastUtils.showSuccessWithStatus("数据同步失败");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }


}
