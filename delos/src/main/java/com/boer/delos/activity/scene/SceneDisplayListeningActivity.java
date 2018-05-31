package com.boer.delos.activity.scene;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.camera.CameraListActivity;
import com.boer.delos.activity.link.LinkModelListeningActivity;
import com.boer.delos.activity.main.HomepageListeningActivity;
import com.boer.delos.activity.remotectler.AirConditionControllerListeningActivity;
import com.boer.delos.activity.remotectler.TVControllerListeningActivity;
import com.boer.delos.activity.scene.devicecontrol.CurtainControlActivity;
import com.boer.delos.activity.scene.devicecontrol.LockControlActivity;
import com.boer.delos.adapter.SceneDisplayAdapter;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.dao.DAO;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.model.RCTVCmd;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomModeActionResult;
import com.boer.delos.model.SceneDisplay;
import com.boer.delos.model.Weather;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.request.room.RoomController;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.ModeRoomPopUpWindow;
import com.boer.delos.widget.MyGridView;
import com.example.colorarcprogressbarlibrary.main.ColorArcProgressBar;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.linphone.squirrel.squirrelCallImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.semtec.www.nebular.CallListeningActivity;


/**
 * @author XieQingTing
 * @Description: 场景显示界面
 * create at 2016/4/12 9:39
 */
public class SceneDisplayListeningActivity extends BaseListeningActivity implements View.OnClickListener {

    private List<SceneDisplay> sceneDisplays;
    //    private ColorArcProgressBar progressbar;
    private ColorArcProgressBar progressbar;
    private TextView tvOutdoorTemp;
    private ImageView ivModel, ivError;
    private TextView tvModel;
    private TextView tvHumidity;
    private TextView tvAirQuality;
    private MyGridView gvLivingRoom;
    private ScrollView scrollView;
    private PercentLinearLayout pllScene;
    private SceneDisplayAdapter adapter;
    private List<Map<String, Object>> typeList = new ArrayList<>();
    private boolean isEdit;// 当前界面是否处于可编辑状态
    //    private boolean isOpenModeLayout = false;//

    private squirrelCallImpl squirrelCall = squirrelCallImpl.getInstance();

    public static SceneDisplayListeningActivity instance = null;
    private Room intentRoom;


    private SharedPreferences preferences;
    private SharedPreferences.Editor pref_editor;
    private DAO mDao;
    private List<ModeAct> modeActList = new ArrayList<>();
    private ModeRoomPopUpWindow roomModePopupWindows;
    private static final int LINK_MODE_ACTIVITY = 1000;
    private String mTempPer = "--";
//    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_living_room, null);
        setContentView(view);

        preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        pref_editor = preferences.edit();

        instance = this;
        if (getIntent() != null) {
            intentRoom = (Room) getIntent().getSerializableExtra("RoomObject");
        }

        initView();
        //让progressbar获得焦点，滚动条到顶部
        progressbar.setFocusable(true);
        progressbar.setFocusableInTouchMode(true);
        progressbar.requestFocus();

//        progressbar.setDiameter(200);

        initData();
        initListener();


    }

    @Override
    protected void onResume() {
        super.onResume();
        tvOutdoorTemp.setText("获取中...");
        //获得室外温度
        new DeviceController().getWeatherTemperature(new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Weather weather = new Gson().fromJson(Json, Weather.class);
                    String temp = weather.getResults().get(0).getWeather_data().get(0).getDate().split("实时：")[1].split("\\)")[0];
                    tvOutdoorTemp.setText(temp);
                } catch (Exception e) {
                    e.printStackTrace();
                    tvOutdoorTemp.setText("--℃");
                }
            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }

    @Override
    protected void deviceStatusUpdate() {
        setDeviceInfo();
    }

    private void initView() {
        initTopBar(intentRoom.getName(), R.string.edit_button, true, false);
        gvLivingRoom = (MyGridView) findViewById(R.id.gvLivingRoom);
        tvAirQuality = (TextView) findViewById(R.id.tvAirQuality);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        tvModel = (TextView) findViewById(R.id.tvModel);
        ivModel = (ImageView) findViewById(R.id.ivModel);
        tvOutdoorTemp = (TextView) findViewById(R.id.tvOutdoorTemp);
//     progressbar = (ColorArcProgressBar) findViewById(R.id.progressbar);
        progressbar = (ColorArcProgressBar) findViewById(R.id.progressbar);
        ivError = (ImageView) findViewById(R.id.ivError);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        pllScene = (PercentLinearLayout) findViewById(R.id.pllScene);

//        this.progressbar.setCurrentValues(26);
    }

    private void initData() {
        adapter = new SceneDisplayAdapter(SceneDisplayListeningActivity.this, typeList);
        gvLivingRoom.setAdapter(adapter);
        setDeviceInfo();
    }

    /**
     * 设定所有设备信息
     */
    private void setDeviceInfo() {
        List<DeviceRelate> list = Constant.DEVICE_RELATE;
        setTemperature(list);
        //过滤没有解绑和当间房间设备
        List<DeviceRelate> filterList = filterDismissDeviceList(list);
        //合并设备
        List<Map<String, Object>> combinList = Constant.combinDeviceList(filterList);
        if (combinList.size() > 0) {
            typeList.clear();
            typeList.addAll(combinList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置温度
     *
     * @param response
     */
    private void setTemperature(List<DeviceRelate> response) {
        String temperature = "--";
        int pm25 = 0;
        String humid = "0";
        for (DeviceRelate deviceRelate : response) {
            if ("Env".equals(deviceRelate.getDeviceProp().getType())) {
                DeviceStatus status = deviceRelate.getDeviceStatus();
                DeviceStatusValue value = status.getValue();
                if (value != null && value.getTemp() != null) {
                    temperature = value.getTemp();
                    pm25 = value.getPm25();
                    humid = value.getHumid();
                    break;
                }
            }
        }
        if (StringUtil.isEmpty(temperature)) {
            temperature = "--";
            mTempPer = temperature;

        }
        if ((!mTempPer.equals("--") || !mTempPer.equals(temperature))) {
            progressbar.setCurrentValues(temperature + "℃");
        }

//        tvHumidity.setText(String.format("湿度:%s%%", humid));
//        tvAirQuality.setText(String.format("空气质量:%s",Constant.getAirQuality(pm25)));
        tvHumidity.setText(humid);
        tvAirQuality.setText(Constant.getAirQuality(pm25));

    }

    /**
     * //过滤解绑设备和非当前房间设备
     *
     * @param relateList
     * @return
     */
    private List<DeviceRelate> filterDismissDeviceList(List<DeviceRelate> relateList) {
        List<DeviceRelate> list = new ArrayList<>();
        for (DeviceRelate deviceRelate : relateList) {
            Boolean dismiss = deviceRelate.getDeviceProp().getDismiss();
            String roomId = deviceRelate.getDeviceProp().getRoomId();
            if (!dismiss && roomId != null && roomId.equals(intentRoom.getRoomId())) {
                list.add(deviceRelate);
            }
        }
        return list;
    }


    private void initListener() {
        tvRight.setOnClickListener(this);
        pllScene.setOnClickListener(this);
        ivModel.setOnClickListener(this);
        gvLivingRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) adapter.getItem(position);
                String name = (String) map.get("name");
                switch (name) {
                    case "照明控制":
                        Intent lightIntent = new Intent(SceneDisplayListeningActivity.this, LightingControlListeningActivity.class);
                        lightIntent.putExtra("roomId", intentRoom.getRoomId());
                        startActivity(lightIntent);
                        break;
                    case "电源管理":
                        Intent powerIntent = new Intent(SceneDisplayListeningActivity.this, PowerManageListeningActivity.class);
                        powerIntent.putExtra("roomId", intentRoom.getRoomId());
                        startActivity(powerIntent);
                        break;
                    case "空调控制":
                        Device device = getAirDevice(intentRoom.getRoomId());
                        if (device == null) {
                            toastUtils.showErrorWithStatus("获得设备数据失败");
                            break;
                        }
                        Intent airintent = new Intent(SceneDisplayListeningActivity.this, AirConditionControllerListeningActivity.class);
                        airintent.putExtra("device", device);
                        startActivity(airintent);
                        break;
                    case "监控控制":
                        Intent intent = new Intent();
                        intent.setClass(SceneDisplayListeningActivity.this, CameraListActivity.class);
                        intent.putExtra("roomId", intentRoom.getRoomId());
                        startActivity(intent);
                        break;
                    case "窗帘控制":
                        Intent curtainIntent = new Intent(SceneDisplayListeningActivity.this, CurtainControlActivity.class);
                        curtainIntent.putExtra("roomId", intentRoom.getRoomId());
                        startActivity(curtainIntent);
                        break;
//                    case "综合服务":
//                        toastUtils.showInfoWithStatus("综合服务");
//                        break;
                    case "影音控制":
//                        toastUtils.showInfoWithStatus("影音控制");
                        //toastUtils.showInfoWithStatus("功能开发中,敬请期待...");
                        //startActivity(new Intent(SceneDisplayListeningActivity.this, ProjectorListListeningActivity.class));
                        RCTVCmd rctvCmd = getDevice(intentRoom.getRoomId());
                        if (rctvCmd == null) {
                            toastUtils.showErrorWithStatus("获取设备数据失败");
                            break;
                        }
                        Intent tvintent = new Intent(SceneDisplayListeningActivity.this, TVControllerListeningActivity.class);
                        //tvintent.putExtra("roomId",intentRoom.getRoomId());
                        tvintent.putExtra(TVControllerListeningActivity.BUNDLE_ACITON_DEVICE_CONTROL_CMD, rctvCmd);
                        startActivity(tvintent);
                        break;
                    case "气体检测":
                        Intent gasIntent = new Intent(SceneDisplayListeningActivity.this, GasTestListeningActivity.class);
                        gasIntent.putExtra("roomId", intentRoom.getRoomId());
                        startActivity(gasIntent);
                        break;
                    case "安全告警":
                        Intent alarmIntent = new Intent(SceneDisplayListeningActivity.this, SecurityAlarmListeningActivity.class);
                        alarmIntent.putExtra("roomId", intentRoom.getRoomId());
                        startActivity(alarmIntent);
                        break;
                    case "安全防护":
                        toastUtils.showInfoWithStatus("功能开发中,敬请期待...");
                        break;
                    case "门禁控制":
                       /* if(!SIP.equals("")){*/
                        OnCall(HomepageListeningActivity.SIP);
                        /*}else{
                            Toast.makeText(SceneDisplayListeningActivity.this,"正在获取数据...",Toast.LENGTH_SHORT).show();
                            getDeviceGuardInfo();
                        }*/
                        break;
                    case "指纹锁控制":
                        Intent lockIntent = new Intent(SceneDisplayListeningActivity.this, LockControlActivity.class);
                        lockIntent.putExtra("roomId", intentRoom.getRoomId());
                        startActivity(lockIntent);
                        break;
                }

            }
        });
    }

    private RCTVCmd getDevice(String roomId) {
        RCTVCmd rctvCmd = new RCTVCmd();
        List<DeviceRelate> relateList = Constant.DEVICE_RELATE;
        for (DeviceRelate deviceRelate : relateList) {
            Device device = deviceRelate.getDeviceProp();
//            Log.i("gwq", "d=" + device.getType());
            if (device.getType().equals("TV") && device.getRoomId().equals(roomId) && !device.getDismiss()) {
                /*mDao = DAO.getSingleton(this);
                mDao.connectDb();
                mDeviceTypeID= ConstantDeviceType.getDeviceIdByType(device.getType());*/
                rctvCmd.setAddr(device.getAddr());
                rctvCmd.setDeviceName(device.getName());
                rctvCmd.setAreaName(device.getAreaname());
                rctvCmd.setRoomName(device.getRoomname());
                // Log.i("gwq","dd="+device.getRemoteCMatchData().getM_key_squency());
                rctvCmd.setValue(device.getRemoteInfo());
                return rctvCmd;
            }
        }
        return null;
    }

    /**
     * 获取空调设备
     *
     * @param roomId
     * @return
     */
    private Device getAirDevice(String roomId) {
        for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
            Device device = deviceRelate.getDeviceProp();
            if ("AirCondition".equals(device.getType()) && device.getRoomId().equals(roomId)
                    && !device.getDismiss()) {
                return device;
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRight:
                isEdit = !isEdit;
                updateRightView();
                break;
            case R.id.ivModel:
                getRoomMode();
                break;
        }
    }

    /**
     * 模式配置
     *
     * @param modeAct
     */
    private void startLinkModeActivity(ModeAct modeAct) {
        Intent intent = new Intent(this, LinkModelListeningActivity.class);
        intent.putExtra("modeAct", modeAct);
        intent.putExtra("ActivityName", Constant.MODE_SETTING_LINKMODE_ROOM);
        startActivityForResult(intent, LINK_MODE_ACTIVITY);
    }

    /**
     * 处理模式列表,如果数据不足4个,补足4个
     *
     * @param list
     * @return
     */
    private List<ModeAct> initModeActionList(List<ModeAct> list) {
        List<ModeAct> modeActList = new ArrayList<>();
        modeActList.addAll(list);
        List<String> tempSerialNos = new ArrayList<>();
        for (ModeAct tempModeAct : modeActList) {
            String serialNo = tempModeAct.getSerialNo();
            tempSerialNos.add(serialNo);
        }
        for (int i = 0; i < 4; i++) {
            if (tempSerialNos.contains(String.valueOf(i + 1))) {
                continue;
            }
            ModeAct act = new ModeAct();
            act.setRoomId(intentRoom.getRoomId());
            act.setSerialNo((i + 1) + "");
            act.setDeviceList(new ArrayList<ModeDevice>());
            act.setTag("新增");
            modeActList.add(act);
        }
        //对数据做排序处理，使正常显示 add by sunzhibin
        List<ModeAct> tempModeActList = new ArrayList<>();
        tempModeActList.addAll(modeActList);
        for (ModeAct tempMA : modeActList) {
            String temp = tempMA.getSerialNo();
            int index = Integer.valueOf(temp) - 1;
            tempModeActList.remove(index);
            tempModeActList.add(index, tempMA);
        }
        tempModeActList.add(3, tempModeActList.remove(2)); //交换 顺序变为 1、2、4、3
        modeActList.clear();
        modeActList.addAll(tempModeActList);
        return modeActList;
    }

    /**
     * 激活房间模式
     *
     * @param modeAct
     */
    private void activeMode(ModeAct modeAct) {
        LinkManageController.getInstance().activate(this, Integer.valueOf(modeAct.getModeId()),
                Integer.valueOf(modeAct.getRoomId()), new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        Loger.d("activeMode " + Json);
                    }

                    @Override
                    public void onFailed(String Json) {
                        Loger.d("activeMode " + Json);
                    }
                });
    }

    /**
     * 更新视图右边按钮
     */
    private void updateRightView() {
        if (isEdit) {
            tvRight.setText("完成");
            tvModel.setText("编辑模式");
            tvModel.setTextColor(Color.RED);
        } else {
            tvRight.setText("编辑");
            tvModel.setText("房间模式");
            tvModel.setTextColor(Color.BLACK);
        }
    }

    /**
     * 查询房间模式
     */
    private void getRoomMode() {
        RoomController.getInstance().showMode(this, null, intentRoom.getRoomId(), new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                try {
                    RoomModeActionResult result = new Gson().fromJson(Json, RoomModeActionResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                        return;
                    }
                    modeActList.clear();
                    modeActList.addAll(result.getResponse());
                    settingRoomMode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(json);
            }
        });
    }

    private void OnCall(String SIP) {
        if (squirrelCallImpl.login_state == 0) {
            //showAlertDialog();
            squirrelCall.squirrelSetFirewall(0);
            Intent intent = new Intent(SceneDisplayListeningActivity.this, CallListeningActivity.class);
            startActivity(intent);
            squirrelCall.squirrelCall("sip:13823211714@192.168.1.103", 1);
        } else {
            //LoginToServer();
            //squirrelCall.squirrelCall("9005", 1);
            squirrelCall.squirrelSetFirewall(2);
            Intent intent = new Intent(SceneDisplayListeningActivity.this, CallListeningActivity.class);
            startActivity(intent);

            squirrelCall.squirrelCall(SIP, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) { //LinkModelListeningActivity
            case LINK_MODE_ACTIVITY:
                if (resultCode != RESULT_OK) {
                    return;
                }
                if (roomModePopupWindows != null && roomModePopupWindows.isShowing()) {
                    roomModePopupWindows.dismiss();
                }
                getRoomMode();
                break;

        }
    }

    private void settingRoomMode() {
        if (isEdit) {
            final List<ModeAct> list = initModeActionList(modeActList);
            roomModePopupWindows = new ModeRoomPopUpWindow(this, list,
                    new ModeRoomPopUpWindow.ModeSelectListener() {
                        @Override
                        public void result(int position) {
                            startLinkModeActivity(list.get(position));
                        }

                        @Override
                        public void modelmanager() {

                        }
                    });
            roomModePopupWindows.refreshData(list);
            roomModePopupWindows.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        } else {
            //判断当前房间是否配置了房间模式
            if (modeActList != null && modeActList.size() > 0) {
                roomModePopupWindows = new ModeRoomPopUpWindow(this, modeActList,
                        new ModeRoomPopUpWindow.ModeSelectListener() {
                            @Override
                            public void result(int position) {
                                activeMode(modeActList.get(position));
                            }

                            @Override
                            public void modelmanager() {

                            }
                        });
                roomModePopupWindows.refreshData(modeActList);
                roomModePopupWindows.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
            }
        }
    }


}
