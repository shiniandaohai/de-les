package com.boer.delos.activity.scene;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.activity.camera.CameraListActivity;
import com.boer.delos.activity.camera.zxing.activity.CaptureActivity;
import com.boer.delos.activity.main.HomepageListeningActivity;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.activity.remotectler.DeviceModeListeningActivity;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.IWirseUdpCMD;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.SendMsgPeriod;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.UdpClientListener;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.UdpMsg;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.XUdp;
import com.boer.delos.adapter.AddDeviceAdapter;
import com.boer.delos.adapter.AddDeviceTitleAdapter;
import com.boer.delos.adapter.MySpinnerAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.ConstantDeviceType;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceType;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomProperty;
import com.boer.delos.model.ScanDevice;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.ResultRetDeal;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.activity.smartdoorbell.BindSmartDoorbellActivity;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.NetUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.camera.MyCamera;
import com.boer.delos.utils.camera.ThreadTPNS;
import com.boer.delos.view.customDialog.CustomFragmentDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.st_LanSearchInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 单设备添加
 */
public class AddDeviceListeningActivity extends CommonBaseActivity implements IRegisterIOTCListener, View.OnClickListener {

    @Bind(R.id.tv_room)
    TextView tvRoom;
    @Bind(R.id.rvAddDeviceTitle)
    RecyclerView rvAddDeviceTitle;
    @Bind(R.id.ivSelectedDevice)
    ImageView ivSelectedDevice;
    @Bind(R.id.rvAddDevice)
    RecyclerView rvAddDevice;
    @Bind(R.id.etDeviceName)
    EditText etDeviceName;
    @Bind(R.id.llDeleteContent)
    LinearLayout llDeleteContent;
    @Bind(R.id.etDeviceAddress)
    EditText etDeviceAddress;
    @Bind(R.id.etDeviceRemark)
    EditText etDeviceRemark;
    @Bind(R.id.tvSelectedConfirm)
    TextView tvSelectedConfirm;
    @Bind(R.id.tv_scan)
    TextView tvScan;
    @Bind(R.id.spinner_brand)
    Spinner spinner_brand;
    @Bind(R.id.ll_device_brand)
    LinearLayout ll_device_brand;//展示选择品牌

    private boolean isAddDevice = true;

    private LinearLayoutManager layoutManager;
    private AddDeviceAdapter adapter;
    private AddDeviceTitleAdapter adapterTitle;
    private Map<String, List<AddDevice>> deviceMap = new HashMap<>();//根据分类名称，显示的设备列表
    private Map<String, List<AddDevice>> deviceMapSelect = new HashMap<>();//点击显示的列表，需要在界面上显示的蓝色图标
    private Integer selectedPosition = null;
    private int deviceTitleSelect = 0;
    private String mDeviceType;
    private static String TAG = "gwq";
    private String VIEW_ACC = "admin";
    private String scanDeviceId;
    private List<String> list = new ArrayList<>();//界面设备列表
    private final static int SCANNIN_GREQUEST_CODE = 1;
    //门禁二维码分割数组
    private String[] mStringQR = null;
    private static final int ADD_SUCCESS_DEVICE_DELAY_TIME = 1000;
    //添加摄像头时扫描到的list
    private static List<String> uidList = new ArrayList<>();

    //
    private final int REQ_DISTRIBUTION_ROOM = 1001;
    Room room;
    private final int REQ_SCAN = 1000;
    private static final int REQ_DOOR_BELL=1002;
    private MyTimer mTimer; //定时扫描设备任务

    //选择品牌 add by sunzhibin
    private MySpinnerAdapter mySpinnerAdapter;
    private List<String> mSpinnerData;
    private String mDeviceBrand = "";
    private MusicUDPListener mWiseMusicListener;
    private String mDeviceProtocol = ""; //场景面板的
    private SendMsgPeriod sendMsgPeriod;

    //TODO 空调选择品牌
    @Override
    protected int initLayout() {
        return R.layout.activity_add_device;
    }

    protected void initView() {
        tlTitleLayout.setTitle(R.string.text_add_device_one);
        tlTitleLayout.setRightText(R.string.text_add_batch_device);
        tlTitleLayout.hideRight();

        deviceMap = Constant.whiteDeviceMap();
        deviceMapSelect = Constant.blueStrokeDeviceMap();

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.rvAddDevice.setLayoutManager(layoutManager);
        rvAddDevice.setHasFixedSize(true);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.rvAddDeviceTitle.setLayoutManager(layoutManager2);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rvAddDeviceTitle.setHasFixedSize(true);

     /*   for(String s : deviceMap.keySet()){
            list.add(s);
        }*/
        list.clear();
        list.add(getString(R.string.air_clean));//
        list.add(getString(R.string.water_clean));
        list.add(getString(R.string.light_control));//"照明控制"
        list.add(getString(R.string.air_condition_control));//"空调控制"
        list.add(getString(R.string.camera_control));//"监控控制"
        list.add(getString(R.string.curtain_control));//"窗帘控制"
        list.add(getString(R.string.elec_control));//"电源管理"
        list.add(getString(R.string.general_service));//"综合服务"
        list.add(getString(R.string.video_control));//"影音控制"
        list.add(getString(R.string.air_check));//"气体检测"
        list.add(getString(R.string.safety_alarm));//"安全告警"
        list.add(getString(R.string.safety_protect));//"安全防护"


        //设置设备分类RecyclerView
        adapterTitle = new AddDeviceTitleAdapter(this, list);
        rvAddDeviceTitle.setAdapter(adapterTitle);


        //设置设备RecyclerView
        adapter = new AddDeviceAdapter(this);
        adapter.setDatas(deviceMap.get(list.get(0)));
        this.rvAddDevice.setAdapter(adapter);


//        this.deliverList = Constant.blueDeviceList();// 图标为蓝色的设备类型列表
        this.tvSelectedConfirm.setOnClickListener(this);
        this.llDeleteContent.setOnClickListener(this);



        mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.boer.delos.AddDevice");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void initData() {
        mWiseMusicListener = new MusicUDPListener();
        sendMsgPeriod = new SendMsgPeriod();
    }


    @Override
    public void rightViewClick() {
        startActivity(new Intent(this, AddBatchScanDeviceActivity.class));
    }

    @Override
    protected void initAction() {
        initListener();

        adapterTitle.setOnItemClickLitener(new AddDeviceTitleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                deviceTitleSelect = position;
                //根据点击的设备类型，刷新设备名称
                adapter.setDatas(deviceMap.get(list.get(position)));
                adapter.notifyDataSetChanged();
            }
        });
        //默认选中第一个设备
        //  doClick(0);
        adapter.setOnItemClickLitener(new AddDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                doClick(position);
                etDeviceAddress.setText("");
                uidList.clear();
                etDeviceRemark.setText("");
            }
        });
    }

    private void initListener() {
        this.etDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    llDeleteContent.setVisibility(View.VISIBLE);
                } else {
                    llDeleteContent.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    //进入界面，默认选中第一个设备，点击按钮切换
    private void doClick(int position) {
        AddDevice itemData = deviceMapSelect.get(list.get(deviceTitleSelect)).get(position);
        ivSelectedDevice.setImageResource(itemData.getResId());
        etDeviceName.setText(itemData.getItemText());
        llDeleteContent.setVisibility(View.GONE);
        selectedPosition = position;
        mDeviceType = itemData.getType();

        initShowBrand();
        if (mDeviceType.equals("N4"))
            tvScan.setText("扫描二维码");
        else
            tvScan.setText("扫描设备地址");


        if (R.string.intercom_name == itemData.getItemText()) {
            tvScan.setVisibility(View.INVISIBLE);
            tvSelectedConfirm.setText(getString(R.string.text_scan_two_dimensional_code));
            isAddDevice = false;
        } else {
            tvScan.setVisibility(View.VISIBLE);
            tvSelectedConfirm.setText(getString(R.string.text_confirm));
            isAddDevice = true;
        }
    }

    /**
     * 设备品牌型号的选择
     */
    private void initShowBrand() {
        //TODO 空调、电视、等 选择品牌
        switch (mDeviceType) {
            case ConstantDeviceType.AUDIO:
                ll_device_brand.setVisibility(View.VISIBLE);
                selectSpinner(mDeviceType);
                break;
            case ConstantDeviceType.PANNEL:
                ll_device_brand.setVisibility(View.VISIBLE);
                selectSpinner(mDeviceType);
                break;

            default:
                ll_device_brand.setVisibility(View.GONE);
                break;
        }


    }

    /**
     * 开始扫描设备
     */
    private void startScanDevice() {
//        toastUtils.showProgress("正在扫描主机,请稍候...");
        uidList.clear();
        GatewayInfo info = null;
        if(NetUtil.isNetWorkMobileConnect(this)){
            info=null;
        }
        else{
            for (GatewayInfo gatewayInfo : Constant.gatewayInfos) {
                if (gatewayInfo.getHostId().equals(Constant.CURRENTHOSTID)) {
                    info = gatewayInfo;
                    Constant.LOCAL_CONNECTION_IP = info.getIp();
                    break;
                }
            }
        }

        //不在一个局域网内
        if (info == null) {
            toastUtils.showErrorWithStatus(getString(R.string.gateway_bind_hint));
        } else {
            //去当前主机查询需添加设备信息
            mTimer = new MyTimer(1000 * 60, 1000 * 3);
            mTimer.weakReference = new WeakReference<>(this);
            mTimer.start();
        }
    }

    private void scanDevice() {
        if (!TextUtils.isEmpty(Constant.LOCAL_CONNECTION_IP)) {
            if (!TextUtils.isEmpty(mDeviceBrand)
                    && mDeviceBrand.equals(ConstantDeviceType.MUSIC_WISE)) {
                sendMsgPeriod.start(mWiseMusicListener, IWirseUdpCMD.TCPCMD_PALPITANT);
            } else {
                scanDevice(Constant.LOCAL_CONNECTION_IP);
            }
        } else
            toastUtils.showErrorWithStatus(getString(R.string.gateway_bind_hint));

    }

    /**
     * 去主机查询指定设备
     *
     * @param ip
     */

    private void scanDevice(String ip) {
        if (null == ip) {
            return;
        }
        DeviceController.getInstance().scanDevice(this, ip, mDeviceType, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {

                String response = "";
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.has("response"))
                        response = jsonObject.getString("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                List<ScanDevice> scanDevices = gson.fromJson(response, new TypeToken<List<ScanDevice>>() {
                }.getType());

                if (null != scanDevices && scanDevices.size() > 0) {
                    out:for (ScanDevice scanDevice : scanDevices) {
                        for(int i=0;i<Constant.DEVICE_RELATE.size();i++){
                            Device device=Constant.DEVICE_RELATE.get(i).getDeviceProp();
                            if(scanDevice.getId().equals(device.getAddr())){
                                continue out;
                            }
                        }
                        uidList.add(scanDevice.getId());
                    }
                    if(uidList.size()>0){
                        scanDeviceId = scanDevices.get(0).getId();
                        etDeviceAddress.setText(scanDeviceId);
                    }
                    else{
                        scanDeviceId = "";
                        etDeviceAddress.setText(scanDeviceId);
                    }

                    toastUtils.dismiss();
                    if (mTimer != null)
                        mTimer.onFinish();
                } else {
//                    toastUtils.showInfoWithStatus(getString(R.string.text_please_check_hardWard));
                }
            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    /**
     * 确认新增设备
     * isAddGuard 是否增加门禁
     */
    private void confirmAdd(boolean isAddGuard) {

        if (selectedPosition == null) {
            toastUtils.showErrorWithStatus(getString(R.string.select_device_type));
            return;
        }
        if (StringUtil.isEmpty(etDeviceName.getText().toString())) {
            toastUtils.showErrorWithStatus(getString(R.string.device_name_cannot_null));
            return;
        }
        //不是添加门禁
        if (!isAddGuard) {
            if (StringUtil.isEmpty(etDeviceAddress.getText().toString())) {
                toastUtils.showErrorWithStatus(getString(R.string.device_address_cannot_null));
                return;
            }
        }


        //添加门禁
       /* if(isAddGuard){
            if(mStringQR!=null){
                GuardInfo guardInfo = new GuardInfo();
                guardInfo.setAccount(mStringQR[0]);
                guardInfo.setPwd(mStringQR[1]);
                guardInfo.setSIP(mStringQR[2]);
                guardInfo.setHostID(mStringQR[3]);
                device.setGuardInfo(guardInfo);
            }else{
                toastUtils.dismiss();
                return;
            }
        }
        etDeviceAddress.setText(mStringQR[2]);//设备地址设置为SIP地址*/

        final Device device = new Device();

        device.setRoomId("-1");
        if (room != null) {
            device.setRoomId(room.getRoomId());
            device.setRoomname(room.getName());
        }

        device.setDismiss(false);
        device.setProtocol(mDeviceProtocol);
        device.setBrand(mDeviceBrand);
        device.setName(etDeviceName.getText().toString());
        device.setAddr(etDeviceAddress.getText().toString());
        device.setNote(etDeviceRemark.getText().toString());
        device.setTimestamp(System.currentTimeMillis());
        device.setAreaId("1");
        device.setType(mDeviceType);//deviceMap.get(list.get(deviceTitleSelect)).get(selectedPosition).getType());


        DeviceController.getInstance().updateProp(this, device, "false", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
//                toastUtils.dismiss();
                L.d("updateProp onSuccess's json===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                String dealResult = ResultRetDeal.getInstance().dealWith(Json);
                if (!StringUtil.isEmpty(dealResult)) {
                    toastUtils.showErrorWithStatus(dealResult);

                    return;
                }
                String response = JsonUtil.parseString(Json, "response");
                final Device deviceRes = new Gson().fromJson(response, Device.class);

                if (ret != null && "0".equals(ret)) {
                    toastUtils.showSuccessWithStatus(getString(R.string.text_add_success));
                    //如果是摄像头，要再进行一步
                    if (mDeviceType != null && mDeviceType.equals("Camera")) {
                        addCamera(device);
                    } else if (mDeviceType != null && (mDeviceType.equals(DeviceType.AIRCONTIDION) || mDeviceType.equals(DeviceType.TV))) {
                        //add ac remote controller
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(DeviceModeListeningActivity.BUNDLE_ACITON_DEVICE_INFO, deviceRes);
                        Intent intent = new Intent(AddDeviceListeningActivity.this, DeviceModeListeningActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("ADD_DEVICE", deviceRes);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }, ADD_SUCCESS_DEVICE_DELAY_TIME);
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String json) {
                Loger.v("json==" + json);
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(json);
            }
        });
    }

    //搜索摄像头
    private void doLanSearch() {
        uidList.clear();
        st_LanSearchInfo[] arrResp = Camera.SearchLAN();
        if (arrResp == null || arrResp.length == 0) {
            toastUtils.showInfoWithStatus(getString(R.string.text_scan_camera_nothing));
            return;
        }


        if (arrResp != null && arrResp.length > 0) {
            for (st_LanSearchInfo resp : arrResp) {
                //过滤条件
                filterExistCamera(uidList, resp);
            }
        }
        if (uidList.size() == 0) {
            toastUtils.showInfoWithStatus(getString(R.string.text_scan_device_nothing));
        } else {
            toastUtils.dismiss();
        }
    }

    /**
     * 過濾已經綁定的Camera
     *
     * @param uidList
     * @param resp
     */
    private void filterExistCamera(List<String> uidList, st_LanSearchInfo resp) {
        String dev_uid = new String(resp.UID).trim();
        for (MyCamera camera : MainTabActivity.mCameraList) {
            if (dev_uid.equalsIgnoreCase(camera.getUID())) {
                return;
            }

        }
        uidList.add(dev_uid);
    }

    //添加摄像头
    private void addCamera(final Device device) {
        String dev_uid = etDeviceAddress.getText().toString();
        String dev_nickname = getString(R.string.camera_name);
        String view_pwd = VIEW_ACC;
        ThreadTPNS TPNSThread = new ThreadTPNS(AddDeviceListeningActivity.this, dev_uid, 0);
        TPNSThread.start();

        boolean duplicated = false;
        for (MyCamera camera : MainTabActivity.mCameraList) {

            if (dev_uid.equalsIgnoreCase(camera.getUID())) {
                duplicated = true;
                break;
            }
        }

        if (duplicated) {
            CameraListActivity.showAlert(AddDeviceListeningActivity.this, getText(R.string.tips_warning), getText(R.string.tips_add_camera_duplicated),
                    getText(R.string.ok));
            toastUtils.showErrorWithStatus("该监控已添加过");
            return;
        }

        int video_quality = 0;
        int channel = 0;
        /*DatabaseManager manager = new DatabaseManager(AddDeviceListeningActivity.this);
        long db_id = manager.addDevice(dev_nickname, dev_uid, "", "", "admin", view_pwd, 3, channel, false);*/

        Toast.makeText(AddDeviceListeningActivity.this, getText(R.string.tips_add_camera_ok).toString(), Toast.LENGTH_SHORT).show();
        int event_notification = 3;
        MyCamera camera = new MyCamera(dev_nickname, dev_uid, VIEW_ACC, view_pwd);
        /*DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(), dev_nickname, dev_uid, VIEW_ACC, view_pwd, "", event_notification, channel,
                null);*/
        MainTabActivity.mDeviceList.add(device);

        camera.registerIOTCListener(this);
        camera.registerIOTCListener(HomepageListeningActivity.getMultiViewActivityIRegisterIOTCListener());
        camera.connect(dev_uid);
        camera.start(MyCamera.DEFAULT_AV_CHANNEL, VIEW_ACC, view_pwd);
        camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
        camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
        camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
        camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
        camera.LastAudioMode = 1;

        MainTabActivity.mCameraList.add(camera);
    }

    @Override
    public void receiveFrameData(Camera camera, int i, Bitmap bitmap) {

    }


    @Override
    public void receiveFrameDataForMediaCodec(Camera camera, int i, byte[] bytes, int i1, int i2, byte[] bytes1, boolean b, int i3) {

    }

    @Override
    public void receiveFrameInfo(Camera camera, int i, long l, int i1, int i2, int i3, int i4) {

    }

    @Override
    public void receiveSessionInfo(Camera camera, int i) {

    }

    @Override
    public void receiveChannelInfo(Camera camera, int i, int i1) {

    }

    @Override
    public void receiveIOCtrlData(Camera camera, int i, int i1, byte[] bytes) {

    }

    //处理二维码扫描结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;


        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String s = bundle.getString("result");
                    if (s != null) {
                        if (s.contains(",")) {
                            mStringQR = s.split(",");//account、密码、SIP、hostID
                        }
                    }
                    if (mStringQR.length != 4) {
                        ToastHelper.showShortMsg(getString(R.string.text_guard_code_error));
                    } else {
                        toastUtils.showProgress(getString(R.string.progress_add_ing));
                        confirmAdd(true);
                    }
                }

            case REQ_DISTRIBUTION_ROOM:
                if (resultCode == RESULT_OK) {

                    room = (Room) data.getSerializableExtra("room");
                    tvRoom.setText(room.getName());

                }
                break;
            case REQ_SCAN:
                String result = data.getStringExtra("result");

                if (result.contains("SN:"))
                    result = result.replace("SN:", "");

                if (!TextUtils.isEmpty(result))
                    etDeviceAddress.setText(result);

                break;
            case REQ_DOOR_BELL:
                String addr=data.getStringExtra("addr");
                if (!TextUtils.isEmpty(addr))
                    etDeviceAddress.setText(addr);
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvSelectedConfirm, R.id.tv_scan, R.id.llDeleteContent,
            R.id.tv_room})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSelectedConfirm:
                if (isAddDevice) {
                    toastUtils.showProgress(getString(R.string.progress_add_ing));
                    confirmAdd(false);
                } else {
                    boolean isAllowAddGuard = true;
                    for (DeviceRelate deviceRelate : Constant.DEVICE_RELATE) {
                        Device device = deviceRelate.getDeviceProp();
                        //判断是否要门禁,如果包含门禁，则提示只能添加一个门禁
                        if ("Guard".equals(device.getType())) {
                            isAllowAddGuard = false;
                            break;
                        }
                    }
                    if (isAllowAddGuard) {
                        //打开扫描界面扫描条形码或二维码
                        Intent openCameraIntent = new Intent(AddDeviceListeningActivity.this, CaptureActivity.class);
                        startActivityForResult(openCameraIntent, SCANNIN_GREQUEST_CODE);
                    } else {
                        ToastHelper.showShortMsg(getString(R.string.text_add_door_guard_one));
                    }


                }

                break;
            case R.id.tv_scan:
                if (selectedPosition == null) {
                    toastUtils.showErrorWithStatus(getString(R.string.select_device_type));
                    return;
                }
                switch (mDeviceType) {
                    case "Doorbell":
                        startActivityForResult(new Intent(this, BindSmartDoorbellActivity.class),REQ_DOOR_BELL);
                        break;
                    case "N4":
                        Intent intent = new Intent(this, CaptureActivity.class);
                        startActivityForResult(intent, REQ_SCAN);
                        break;

                    default:
                        toastUtils.showProgress(getString(R.string.progress_scan_ing));
                        startScanDevice();
                }
                //添加摄像头的处理方法
//                if (mDeviceType != null && mDeviceType.equals("Camera")) {
//                    Log.i(TAG, "position=" + selectedPosition);
//                    etDeviceName.setText(R.string.camera_name);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            doLanSearch();
//                        }
//                    }, 1000);
//                    return;
//                }


                break;
            case R.id.llDeleteContent:
                etDeviceName.setText("");// 清空已编辑的设备名称
                break;
            case R.id.tv_room:
                Intent intent = new Intent(this, RoomManageActivity.class);
                intent.putExtra("entrance", this.getLocalClassName());
                Loger.v("getLocalClassName()==" + this.getLocalClassName());
                startActivityForResult(intent, REQ_DISTRIBUTION_ROOM);
                break;

        }
    }


    static class MyTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        private WeakReference<AddDeviceListeningActivity> weakReference;
        private AddDeviceListeningActivity mContext;

        @Override
        public void onTick(long millisUntilFinished) {
            mContext = weakReference.get();
            if (mContext != null)
                mContext.scanDevice();

            Log.d("扫描", millisUntilFinished + " onTick");
        }

        @Override
        public void onFinish() {
            cancel();
            Log.d("扫描", "onFinish");
            mContext = weakReference.get();
            if (mContext != null) {
                if (mContext.toastUtils != null && mContext.toastUtils.isShowing()) {
                    if(uidList.size()==0)
                    mContext.toastUtils.showInfoWithStatus(mContext.getString(R.string.text_please_check_hardWard));
                }
            }
        }
    }


    private void selectSpinner(final String mDeviceType) {
        if (mSpinnerData == null)
            mSpinnerData = new ArrayList<>();
        else mSpinnerData.clear();
        switch (mDeviceType) {
            case ConstantDeviceType.AUDIO:
                mSpinnerData.add("Wise");
                mSpinnerData.add("Wise485");
                break;
            case ConstantDeviceType.PANNEL:
                mSpinnerData.add(getString(R.string.device_panel_2));
                mSpinnerData.add(getString(R.string.device_panel_3));
                mSpinnerData.add(getString(R.string.device_panel_4));

                break;

        }
        if (mySpinnerAdapter == null)
            mySpinnerAdapter = new MySpinnerAdapter(this, mSpinnerData, R.layout.item_textview_center);
        spinner_brand.setAdapter(mySpinnerAdapter);
        spinner_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (mDeviceType) {
                    case ConstantDeviceType.AUDIO:
                        mDeviceBrand = mySpinnerAdapter.getItem(position);
                        break;
                    case ConstantDeviceType.PANNEL:
                        switch (position) {
                            case 0:
                                mDeviceProtocol = "2";
                                break;
                            case 1:
                                mDeviceProtocol = "3";
                                break;
                            case 2:
                                mDeviceProtocol = "4";
                                break;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDeviceBrand = mySpinnerAdapter.getItem(spinner_brand.getSelectedItemPosition());
            }
        });
    }

    class MusicUDPListener extends UdpClientListener.SimpleUdpClientListener {
        @Override
        public void onSended(XUdp XUdp, UdpMsg udpMsg) {
            super.onSended(XUdp, udpMsg);
        }

        @Override
        public void onReceive(XUdp client, UdpMsg udpMsg) {
            super.onReceive(client, udpMsg);
            if (!TextUtils.isEmpty(udpMsg.getSourceDataString())
                    && udpMsg.getSourceDataString().contains("BOER")) {//BOER-7C-TFANBTR
                if (etDeviceAddress != null) {
                    etDeviceAddress.setText(udpMsg.getTarget().getIp());
                    if (toastUtils.isShowing())
                        toastUtils.dismiss();
                }
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (toastUtils.isShowing())
//                toastUtils.dismiss();
//            else
//                finish();
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null)
            mTimer.cancel();
        if (sendMsgPeriod != null)
            sendMsgPeriod.stopAll();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }


    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.boer.delos.AddDevice")){
                RoomProperty.RoomBean roomBean = (RoomProperty.RoomBean) intent.getSerializableExtra("room");
                if(room==null){
                    room=new Room(roomBean.getName());
                }
                room.setName(roomBean.getName());
                room.setType(roomBean.getType());
                room.setRoomId(roomBean.getRoomId());
                tvRoom.setText(room.getName());
            }
        }
    };


    @Override
    public void leftViewClick() {
        goBack();
    }

    private CustomFragmentDialog deleteDialog;
    private void goBack(){
        if (selectedPosition == null) {
            finish();
            return;
        }
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        if(deleteDialog==null){
            deleteDialog = CustomFragmentDialog.newInstanse(
                    getString(R.string.text_prompt) ,
                    "确定要取消设备添加？", false);
        }
        deleteDialog.show(getSupportFragmentManager(), null);
        deleteDialog.setListener(new CustomFragmentDialog.EditComfireDialogListener() {
            @Override
            public void onComfire(String inputText) {
                deleteDialog.dismiss();
                if (toastUtils.isShowing())
                    toastUtils.dismiss();
                else
                    finish();
            }
        });
    }
}
