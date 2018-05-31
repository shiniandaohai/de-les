package com.boer.delos.activity.remotectler;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.RCTVCmd;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/7/15.
 */
public class TVControllerListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    //传入数据指令的参数。
    public static final String BUNDLE_ACITON_DEVICE_CONTROL_CMD = "bundle_aciton_device_control_cmd";
    private RCTVCmd mCmd = new RCTVCmd();
    private String roomId;

    Button mRbTvSwitch;
    Button mBt_tv_rc_ok;
    Button mBt_tv_rc_up;
    Button mBt_tv_rc_down;
    Button mBt_tv_rc_left;
    Button mBt_tv_rc_right;
    Button mBt_tv_rc_tv_record;
    Button mBt_tv_rc_voladd;
    Button mBt_tv_rc_volsub;
    Button mBt_tv_rc_menu;
    Button mBt_tv_rc_main;
    Button mBt_tv_rc_back;

    private Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_controller_tv);

        try {
            mCmd = (RCTVCmd) getIntent().getExtras().getSerializable(BUNDLE_ACITON_DEVICE_CONTROL_CMD);
            //deviceWithRemoteCtlInfo = (DeviceWithRemoteCtlInfo) getIntent().getExtras().getSerializable(BUNDLE_ACITON_DEVICE_CONTROL_CMD);
            //roomId = getIntent().getExtras().readString("roomId");
            //Log.i("gwq","addr="+deviceWithRemoteCtlInfo.getAddr());
        }catch (Exception e){

        }

        mDevice = ((DeviceRelate) getIntent().getSerializableExtra("device")).getDeviceProp();

        initView();
        //queryRecentData();
    }

    private List<Device> devices = new ArrayList<>();
    private void initView() {
        initTopBar(mDevice.getName(), null, true, true);
        findViewById(R.id.rlTitle).setBackgroundColor(getResources().getColor(R.color.layout_title_bg));
        boolean favorite = mDevice.getFavorite().equals("1");
        ivRight.setImageResource(!favorite ? R.mipmap.nav_collect_nor
                : R.mipmap.nav_red_collect);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = mDevice.getFavorite();
                if (tag.equals("1")) {
                    tag = "0";
                    ivRight.setImageResource(R.mipmap.nav_collect_nor);
                } else if (tag.equals("0")) {
                    tag = "1";
                    ivRight.setImageResource(R.mipmap.nav_red_collect);
                }
                //保存状态，并发送后台
                if (mDevice != null) {
                    devices.clear();
                    mDevice.setFavorite(tag);
                    devices.add(mDevice);
                    DeviceUpdateStatus.setCommonDevice(TVControllerListeningActivity.this, devices, toastUtils);
                }
            }
        });
        /*
         * 电视遥控 按键 Index
         * @"1",@"1",//开/关键
           @"26",@"2",//方向键上
           @"28",@"3",//方向键左
           @"27",@"4",//方向键下
           @"29",@"5",//方向键右
           @"33",@"6",//OK键
           @"32",@"7",//TV/AV键
           @"34",@"8",//VOL+
           @"35",@"9",//VOL-
           @"30",@"10",//菜单
           @"39",@"11",//主页
         * @"40",@"12",//后退
         */
        Integer indexSwitch = 1;
        Integer indexUp = 26;
        Integer indexDown = 27;
        Integer indexLeft = 28;
        Integer indexRight = 29;
        Integer indexMenu = 30;
        Integer indexTVAV = 32;
        Integer indexOK = 33;
        Integer indexVoladd = 34;
        Integer indexVolsub = 35;
        Integer indexBack = 39;
        Integer indexMain = 40;

        mRbTvSwitch = (Button) findViewById(R.id.bt_tv_switch);
        mRbTvSwitch.setTag(indexSwitch);
        mRbTvSwitch.setOnClickListener(this);

        mBt_tv_rc_ok = (Button) findViewById(R.id.bt_tv_rc_ok);
        mBt_tv_rc_ok.setTag(indexOK);
        mBt_tv_rc_ok.setOnClickListener(this);

        mBt_tv_rc_up = (Button) findViewById(R.id.bt_tv_rc_up);
        mBt_tv_rc_up.setTag(indexUp);
        mBt_tv_rc_up.setOnClickListener(this);

        mBt_tv_rc_down = (Button) findViewById(R.id.bt_tv_rc_down);
        mBt_tv_rc_down.setTag(indexDown);
        mBt_tv_rc_down.setOnClickListener(this);

        mBt_tv_rc_left = (Button) findViewById(R.id.bt_tv_rc_left);
        mBt_tv_rc_left.setTag(indexLeft);
        mBt_tv_rc_left.setOnClickListener(this);

        mBt_tv_rc_right = (Button) findViewById(R.id.bt_tv_rc_right);
        mBt_tv_rc_right.setTag(indexRight);
        mBt_tv_rc_right.setOnClickListener(this);

        mBt_tv_rc_tv_record = (Button) findViewById(R.id.bt_tv_rc_tv_record);
        mBt_tv_rc_tv_record.setTag(indexTVAV);
        mBt_tv_rc_tv_record.setOnClickListener(this);

        mBt_tv_rc_voladd = (Button) findViewById(R.id.bt_tv_rc_voladd);
        mBt_tv_rc_voladd.setTag(indexVoladd);
        mBt_tv_rc_voladd.setOnClickListener(this);

        mBt_tv_rc_volsub = (Button) findViewById(R.id.bt_tv_rc_volsub);
        mBt_tv_rc_volsub.setTag(indexVolsub);
        mBt_tv_rc_volsub.setOnClickListener(this);

        mBt_tv_rc_menu = (Button) findViewById(R.id.bt_tv_rc_menu);
        mBt_tv_rc_menu.setTag(indexMenu);
        mBt_tv_rc_menu.setOnClickListener(this);

        mBt_tv_rc_main = (Button) findViewById(R.id.bt_tv_rc_main);
        mBt_tv_rc_main.setTag(indexMain);
        mBt_tv_rc_main.setOnClickListener(this);

        mBt_tv_rc_back = (Button) findViewById(R.id.bt_tv_rc_back);
        mBt_tv_rc_back.setTag(indexBack);
        mBt_tv_rc_back.setOnClickListener(this);
    }

//    /**
//     * 过滤解绑和不是当前房间设备
//     * @param list
//     * @return
//     */
//    private List<DeviceRelate> filterDismissDeviceList(List<DeviceRelate> list){
//        List<DeviceRelate> filterList = new ArrayList<>();
//        //过滤灯,未解绑, 当前房间
//        for (DeviceRelate deviceRelate : list) {
//            Device device = deviceRelate.getDeviceProp();
//            if (device.getType().contains("TV")
//                    && !device.getDismiss()
//                    && roomId.equals(device.getRoomId())) {
//                filterList.add(deviceRelate);
//            }
//        }
//        return filterList;
//    }

    @Override
    public void onClick(View v) {
//        mCmd.getValue().setIndex(String.valueOf(v.getTag()));
//        WifiAirCleanController.getInstance().deviceControl(this, mCmd, new RequestResultListener() {
//            @Override
//            public void onSuccess(String Json) {
//
//            }
//
//            @Override
//            public void onFailed(String json) {
//            }
//        });

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

}
