package com.boer.delos.activity.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.main.MainTabActivity;
import com.boer.delos.adapter.camera.DeviceAdapter;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.utils.ExternalStorageUtils;
import com.boer.delos.utils.camera.DatabaseManager;
import com.boer.delos.utils.camera.DeviceInfo;
import com.boer.delos.utils.camera.MyCamera;
import com.boer.delos.view.TitleLayout;

import java.util.ArrayList;
import java.util.List;

public class CameraListActivity extends BaseListeningActivity {
    private TitleLayout titleLayout;
    private ListView devicelv;
    private TextView addtv, titletv;
    public static final int REQUEST_CODE_CAMERA_ADD = 0;
    public static final int REQUEST_CAMERA_LIVE = 1;
    //public static final int CAMERA_MAX_LIMITS = 6;//最多个数限制
    public static int nShowMessageCount = 0;
    //public static final boolean SupportOnDropbox = true;
    //private static IRegisterIOTCListener IRegisterIOTCListener;
    private List<DeviceInfo> deviceInfoList = new ArrayList<DeviceInfo>();
    private List<String> strlist = new ArrayList<String>();
    private List<Bitmap> bitlist = new ArrayList<>();

    private DeviceAdapter deviceAdapter;
    private List<Device> deviceList = new ArrayList<Device>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        initView();
        //IRegisterIOTCListener = this;
        //devicelv.setFocusable(false);
        //devicelv.setFocusableInTouchMode(false);
        devicelv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("gwq", "dvice.size=" + MainTabActivity.mDeviceList.size());
                if (MainTabActivity.mDeviceList.size() == 0) {
                    return;
                }
                Device deviceInfo = deviceList.get(position);
//                Device deviceInfo = HomepageListeningActivity.mDeviceList.get(position);
                MyCamera camera = null;
                for (int i = 0; i < MainTabActivity.mCameraList.size(); i++) {
                    camera = MainTabActivity.mCameraList.get(i);
                    if (camera.getUID().equals(deviceInfo.getAddr())) {
                        break;
                    }
                }
                if (camera == null) {
//                    new ToastUtils(CameraListActivity.this).showErrorWithStatus("获取摄像头失败");
                    toastUtils.showErrorWithStatus("获取摄像头失败");
                }

                Bundle extras = new Bundle();

                extras.putString("dev_uid", deviceInfo.getAddr());
                extras.putString("dev_uuid", camera.getUUID());
                extras.putString("dev_nickname", deviceInfo.getName());
       /*         extras.saveString("conn_status", deviceInfo.Status);
                extras.saveString("view_acc",deviceInfo.View_Account);
                extras.saveString("view_pwd", deviceInfo.View_Password);
                extras.putInt("camera_channel",0);// mnSelChannelID[index]);*/
                extras.putInt("MonitorIndex", position);

               /* extras.saveString("OriginallyUID", deviceInfo.UID);
                extras.putInt("OriginallyChannelIndex", deviceInfo.ChannelIndex);*/

                Intent intent = new Intent();
                intent.putExtras(extras);
                intent.setClass(CameraListActivity.this, CameraMainActivity.class);
                startActivityForResult(intent, REQUEST_CAMERA_LIVE);
            }
        });
    }
    private void initView() {
        String roomId = getIntent().getStringExtra("roomId");
//        titleLayout = (TitleLayout)findViewById(R.id.title);
//        titleLayout.setTitle(R.string.camera);
//        //titleLayout.setRightText(R.string.add);
//        titleLayout.setImgbtnLeft();

        devicelv = (ListView) findViewById(R.id.dvlv);
//        Log.i("gwq","device="+HomepageListeningActivity.mDeviceList.size());
//        Log.i("gwq","camera="+HomepageListeningActivity.mCameraList.size());
        List<DeviceRelate> relateList = Constant.DEVICE_RELATE;

        if (roomId == null || roomId.equals("")) {
            deviceList = getDevice(relateList);
        } else {
            deviceList = getDevice(relateList, roomId);
        }
        /*for(Device deviceInfo:HomepageListeningActivity.mDeviceList){
            if(!deviceInfo.getDismiss()) {
                deviceList.add(deviceInfo);
                strlist.add(getResources().readString(R.string.camera) + ":  " + deviceInfo.getAddr());
            }
        }*/
        for (int i = 0; i < deviceList.size(); i++) {
            String name = deviceList.get(i).getAddr();
            String pathStr = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/" + "bitmap";
            Bitmap bmp = ExternalStorageUtils.getImageToBitmap(pathStr, name);
            bitlist.add(bmp);
        }
        deviceAdapter = new DeviceAdapter(this, deviceList, bitlist);
        devicelv.setAdapter(deviceAdapter);
        //deviceAdapter.notifyDataSetChanged();
    }

    /**
     * 过滤不满足条件的摄像头
     */
    private List<Device> getDevice(List<DeviceRelate> relateList) {
        List<Device> deviceList = new ArrayList<Device>();
        for (DeviceRelate deviceRelate : relateList) {
            Device device = deviceRelate.getDeviceProp();
            if (device.getType().equals("Camera") && !device.getDismiss()) {
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    private List<Device> getDevice(List<DeviceRelate> relateList, String roomId) {
        List<Device> deviceList = new ArrayList<Device>();
        for (DeviceRelate deviceRelate : relateList) {
            Device device = deviceRelate.getDeviceProp();
            if (device.getType().equals("Camera") && !device.getDismiss() && roomId.equals(device.getRoomId())) {
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA_ADD) {
            switch (resultCode) {
                case RESULT_OK:

                    Bundle extras = data.getExtras();
                    String dev_uuid = extras.getString("dev_uuid");
                    String dev_uid = extras.getString("dev_uid");
                    String dev_nickname = extras.getString("dev_nickname");
                    String OriginallyUID = extras.getString("OriginallyUID");

                    int OriginallyChannelIndex = extras.getInt("OriginallyChannelIndex");
                    int channelIndex = extras.getInt("camera_channel");
                    int MonitorIndex = extras.getInt("MonitorIndex");
                    strlist.add("摄像头： " + dev_uid);
                    DatabaseManager DatabaseManager = new DatabaseManager(this);
                    DatabaseManager.remove_Device_Channel_Allonation_To_MonitorByUID(OriginallyUID, OriginallyChannelIndex, MonitorIndex);
                    DatabaseManager.add_Device_Channel_Allonation_To_MonitorByUID(dev_uid, channelIndex, MonitorIndex);
                    DatabaseManager = null;
                    Log.i("gwq", "" + dev_uid + "," + dev_uuid + "," + dev_nickname + "," + channelIndex + "," + MonitorIndex);
                    //ChangeMutliMonitor(dev_uid, dev_uuid, dev_nickname, channelIndex, MonitorIndex);
                    deviceAdapter.notifyDataSetChanged();
                    break;
            }
        } else if (requestCode == REQUEST_CAMERA_LIVE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bmp = null;
                    try {
                        Bundle bundle = data.getExtras();
                        String name = bundle.getString("dev_uid");
                        String pathStr = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/" + "bitmap";
                        bmp = ExternalStorageUtils.getImageToBitmap(pathStr, name);
                        if (bmp == null) {
                            bmp = data.getParcelableExtra("bitmap");

                        }
                        int MonitorIndex = data.getIntExtra("MonitorIndex", 0);
                        bitlist.set(MonitorIndex, bmp);
                        deviceAdapter.setBitmaps(bitlist);
//                    deviceAdapter.notifyDataSetChanged();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    /* public static IRegisterIOTCListener getMultiViewActivityIRegisterIOTCListener() {
         return IRegisterIOTCListener;
     }*/
    public static void showAlert(Context context, CharSequence title, CharSequence message, CharSequence btnTitle) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
        dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        dlgBuilder.setTitle(title);
        dlgBuilder.setMessage(message);
        dlgBuilder.setPositiveButton(btnTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }



    @Override
    protected void onDestroy() {
        strlist.clear();
        deviceAdapter.notifyDataSetChanged();
        super.onDestroy();
    }
}
