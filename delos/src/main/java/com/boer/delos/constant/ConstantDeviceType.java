package com.boer.delos.constant;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.boer.delos.activity.camera.CameraMainActivity;
import com.boer.delos.activity.remotectler.AirConditionControllerListeningActivity;
import com.boer.delos.activity.remotectler.CACControllerListeningActivity;
import com.boer.delos.activity.remotectler.TVControllerListeningActivity;
import com.boer.delos.activity.scene.AddBatchScanResultActivity;
import com.boer.delos.activity.scene.devicecontrol.AirSystemActivity;
import com.boer.delos.activity.scene.devicecontrol.CircadianLightControlActivity;
import com.boer.delos.activity.scene.devicecontrol.CurtainControlActivity;
import com.boer.delos.activity.scene.devicecontrol.LightControlActivity;
import com.boer.delos.activity.scene.devicecontrol.LightingManualControlActivity;
import com.boer.delos.activity.scene.devicecontrol.LockControlActivity;
import com.boer.delos.activity.scene.devicecontrol.music.MusicWiseActivity;
import com.boer.delos.activity.scene.devicecontrol.music.MusicWise485Activity;
import com.boer.delos.activity.scene.devicecontrol.LaserEggActivity;
import com.boer.delos.activity.scene.devicecontrol.PanelSettingActivity;
import com.boer.delos.activity.scene.devicecontrol.SensorControlActivity;
import com.boer.delos.activity.scene.devicecontrol.SocketControlActivity;
import com.boer.delos.activity.scene.devicecontrol.WaterCleanActivity;
import com.boer.delos.activity.scene.devicecontrol.WaterFloorCleanActivity;
import com.boer.delos.activity.scene.devicecontrol.airclean.AirCleanActivity;
import com.boer.delos.activity.smartdoorbell.SmartDoorbellActivity;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.utils.ToastHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:设备图片
 * @CreateDate: 2017/3/31 0031 14:42
 * @Modify:
 * @ModifyDate:
 */


public class ConstantDeviceType extends Constant {

    public static final int DEVICE_TYPE_COUNT = 10;
    public static final int DEVICE_QUERY_PERIOY = 1000 * 5;//设备状态查询周期

    public static final String MUSIC_WISE = "Wise";

    /*灯光管理*/

    public static final String LIGHT_1 = "Light1";
    public static final String LIGHT_2 = "Light2";
    public static final String LIGHT_3 = "Light3";
    public static final String LIGHT_4 = "Light4";
    public static final String LIGHT_ADJUDST = "LightAdjust";
    public static final String N4 = "N4";
    public static final String CIRCADIANLIGHT = "CircadianLight";

    /*电源*/
    public static final String SOCKET = "Socket";

    /*空调*/
    public static final String AIR_CONDITION = "AirCondition";
    public static final String CAC = "CAC";
    /*监控*/
    public static final String CAMERA = "Camera";
    public static final String GUARD = "Guard"; //门禁
    public static final String DOOR_BELL="Doorbell";
    /*窗帘*/
    public static final String CURTAIN = "Curtain";

    /*综合*/
    public static final String PANNEL = "Pannel";
    public static final String HGC = "HGC";
    //new add
    public static final String TAB_WATER_FILTER = "TableWaterFilter";
    public static final String AIR_FILTRER = "AirFilter";
    public static final String FLOOR_WATER_FILTER = "FloorWaterFilter";

    /*影音*/
    public static final String DVD = "DVD";
    public static final String IPTV = "IPTV";
    public static final String TV = "TV";

    public static final String SOUND = "Sound";
    public static final String PEOJECTOR = "Projector";
    public static final String AUDIO = "Audio"; //背景音乐

    /*气体检测*/
    public static final String CH4CO = "Ch4CO";
    public static final String O2CO2 = "O2CO2";
    public static final String ENV = "Env";
    public static final String SMOKE = "Smoke";
    public static final String LASEREGG = "LaserEgg";
    public static final String AIR_SYSTEM = "AirSystem"; //新风系统
    /*安全告警*/
    public static final String EXIST = "Exist";
    public static final String FALL = "Fall";
    public static final String WATER = "Water";

    public static final String SOV = "Sov";
    public static final String SOS = "SOS";
    public static final String GSM = "Gsm";
    public static final String CURTAIN_SENOR = "CurtainSensor";//红外
    public static final String ACOUSTO_OPTIC_ALARM = "AcoustoOpticAlarm";

    /*安全防护*/
    public static final String LOCK = "Lock";


    /*功能分组*/
    public static final String AIR_CLEAN = "空气净化";
    public static final String WATER_CLEAN = "水质净化";
    public static final String LIGHT_CONTROL = "照明控制";
    public static final String CAC_CONTROL = "空调控制";
    public static final String CAMERA_CONTROL = "监控控制";
    public static final String CURTAIN_CONTROL = "窗帘控制";
    public static final String ELEC_CONTROL = "电源控制";
    public static final String GENERAL_CONTROL = "综合服务";
    public static final String AIR_CHECK = "气体检测";
    public static final String DVD_CONTROL = "影音控制";
    public static final String SAFTY_ALARM = "安全告警";
    public static final String SAFTY_PROTECT = "安全防护";


    public static String[] group_types = new String[]{AIR_CLEAN,
            WATER_CLEAN,
            LIGHT_CONTROL
            , CURTAIN_CONTROL
            , CAC_CONTROL
            , CAMERA_CONTROL
            , ELEC_CONTROL
            , GENERAL_CONTROL
            , AIR_CHECK
            , DVD_CONTROL
            , SAFTY_ALARM
            , SAFTY_PROTECT};

    //房间情景模式
    public static String[] FILTER_DEVICE_TYPES_1 = new String[]{
            "Camera", "Guard", "Doorbell", "Heating", "Pannel", "HGCFloor", "HGC", "Ch4CO",
            "O2CO2", "Smoke", "AirSystem", "Env", "Exist", "Fall", "Water", "Sov", "SOS",
            "Gsm", "CurtainSensor", "AcoustoOpticAlarm", "Lock", "FloorWaterFilter", "N4", "LaserEgg"
            , "Water", "TableWaterFilter"
    };

    //联动
    public static String[] FILTER_DEVICE_TYPES_2 = new String[]{
            "N4", "Exist", "Fall", "Water", "Sov", "SOS", "Gsm", "CurtainSensor", "Ch4CO",
            "O2CO2", "Env", "Smoke", "AirSystem", "AcoustoOpticAlarm", "Lock", "FloorWaterFilter",
            "HGC", "TableWaterFilter", "LaserEgg"};

    /**
     * 设备分组
     *
     * @param deviceRelates
     * @return
     */
    public static synchronized ArrayMap<String, List<DeviceRelate>>
    groupAllDeviceByType(List<DeviceRelate> deviceRelates) {
        ArrayMap<String, List<DeviceRelate>> mapDeviceRelate = new ArrayMap<>();
        List<DeviceRelate> deviceLists = null;
        for (DeviceRelate deviceRelate : deviceRelates) {
            if (deviceRelate == null || deviceRelate.getDeviceProp() == null) {
                continue;
            }
            Device device = deviceRelate.getDeviceProp();
            String key = "";
            switch (device.getType()) {
                case ConstantDeviceType.N4:
                case ConstantDeviceType.LIGHT_1:
                case ConstantDeviceType.LIGHT_2:
                case ConstantDeviceType.LIGHT_3:
                case ConstantDeviceType.LIGHT_4:
                case ConstantDeviceType.LIGHT_ADJUDST:
                case ConstantDeviceType.CIRCADIANLIGHT:

                    key = ConstantDeviceType.LIGHT_CONTROL;
                    break;

                case ConstantDeviceType.AIR_CONDITION:
                case ConstantDeviceType.CAC:

                    key = ConstantDeviceType.CAC_CONTROL;
                    break;
                case ConstantDeviceType.CAMERA:
                case ConstantDeviceType.GUARD:
                case ConstantDeviceType.DOOR_BELL:
                    key = ConstantDeviceType.CAMERA_CONTROL;
                    break;
                case ConstantDeviceType.CURTAIN:
                    key = ConstantDeviceType.CURTAIN_CONTROL;
                    break;
                case ConstantDeviceType.SOCKET:
                    key = ConstantDeviceType.ELEC_CONTROL;
                    break;
                case ConstantDeviceType.TAB_WATER_FILTER:
                case ConstantDeviceType.FLOOR_WATER_FILTER:
                    key = ConstantDeviceType.WATER_CLEAN;
                    break;
                case ConstantDeviceType.AIR_FILTRER:
                    key = ConstantDeviceType.AIR_CLEAN;
                    break;
                case ConstantDeviceType.PANNEL:
                case ConstantDeviceType.HGC:
                    key = ConstantDeviceType.GENERAL_CONTROL;
                    break;
                case ConstantDeviceType.CH4CO:
                case ConstantDeviceType.O2CO2:
                case ConstantDeviceType.ENV:
                case ConstantDeviceType.SMOKE:
                case ConstantDeviceType.LASEREGG:
                case ConstantDeviceType.AIR_SYSTEM:
                    key = ConstantDeviceType.AIR_CHECK;
                    break;

                case ConstantDeviceType.DVD:
                case ConstantDeviceType.IPTV:
                case ConstantDeviceType.TV:
                case ConstantDeviceType.SOUND:
                case ConstantDeviceType.PEOJECTOR:
                case ConstantDeviceType.AUDIO:
                    key = ConstantDeviceType.DVD_CONTROL;
                    break;

                case ConstantDeviceType.EXIST:
                case ConstantDeviceType.FALL:
                case ConstantDeviceType.WATER:
                case ConstantDeviceType.SOV:
                case ConstantDeviceType.SOS:
                case ConstantDeviceType.GSM:
                case ConstantDeviceType.CURTAIN_SENOR:
                case ConstantDeviceType.ACOUSTO_OPTIC_ALARM:
                    key = ConstantDeviceType.SAFTY_ALARM;
                    break;

                case ConstantDeviceType.LOCK:
                    key = ConstantDeviceType.SAFTY_PROTECT;
                    break;
            }
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            deviceLists = mapDeviceRelate.get(key);
            if (deviceLists == null) {
                deviceLists = new ArrayList<>();
            }
            deviceLists.add(deviceRelate);
            mapDeviceRelate.put(key, deviceLists);
        }
        for (int i = 0; i < group_types.length; i++) {
            if (!mapDeviceRelate.containsKey(group_types[i])) {
                mapDeviceRelate.put(group_types[i], new ArrayList<DeviceRelate>());
            }
        }
        return mapDeviceRelate;

    }

    /**
     * 根据roomId获取设备列表
     *
     * @param deviceRelates
     * @param roomId
     * @return
     */
    public static List<Device> getDeviceByRoomId(List<DeviceRelate> deviceRelates, String roomId) {
        if (TextUtils.isEmpty(roomId)) {
            return Collections.emptyList();
        }
        List<Device> deviceLists = new ArrayList<>();

        for (DeviceRelate deviceRelate : deviceRelates) {
            if (deviceRelate == null || deviceRelate.getDeviceProp() == null) {
                continue;
            }
            Device device = deviceRelate.getDeviceProp();
            if (device == null || TextUtils.isEmpty(device.getRoomId())) {
                continue;
            }

            if (device.getRoomId().equals(roomId)) {

                deviceLists.add(device);
            }
        }

        return deviceLists;
    }

    /**
     * 设备类型的跳转控制界面
     *
     * @param mContext
     * @param type     设备类型
     * @return
     */
    public static Intent startActivityByType(Context mContext, String type) {

        Intent intent = null;
        if (type.contains("LightAdjust")) {
            intent = new Intent(mContext, LightingManualControlActivity.class);
        } else if (type.contains("CircadianLight")) {
            intent = new Intent(mContext, CircadianLightControlActivity.class);
        } else if (type.contains("Light")) {
            intent = new Intent(mContext, LightControlActivity.class);
        } else {
            switch (type) {
                case "Socket":
                    intent = new Intent(mContext, SocketControlActivity.class);
                    break;
                case "Curtain":
                    intent = new Intent(mContext, CurtainControlActivity.class);
                    break;
                case "Exist":
                case "Fall":
                case "Water":
                case "Sov":
                case "SOS":
                case "Gsm":
                case "CurtainSensor":
                    intent = new Intent(mContext, SensorControlActivity.class);
                    break;
                case "TableWaterFilter":
                    intent = new Intent(mContext, WaterCleanActivity.class);
                    break;
                case "FloorWaterFilter":
                    intent = new Intent(mContext, WaterFloorCleanActivity.class);
                    break;
                case "AirFilter":
                    intent = new Intent(mContext, AirCleanActivity.class);
                    break;
                case "N4":
                    intent = new Intent(mContext, AddBatchScanResultActivity.class);
                    break;
                case "Lock":
                    intent = new Intent(mContext, LockControlActivity.class);
                    break;
                case "Pannel":
                    intent = new Intent(mContext, PanelSettingActivity.class);
                    break;
                case "Camera":
                    intent = new Intent(mContext, CameraMainActivity.class);
                    break;
                case "AudioWise":
                    intent = new Intent(mContext, MusicWiseActivity.class);
                    break;
                case "AudioWise485":
                    intent = new Intent(mContext, MusicWise485Activity.class);
                    break;
                case "LaserEgg":
                    intent = new Intent(mContext, LaserEggActivity.class);
                    break;
                case "AirCondition":
                    intent = new Intent(mContext, AirConditionControllerListeningActivity.class);
                    break;
                case "CAC":
                    intent = new Intent(mContext, CACControllerListeningActivity.class);
                    break;
                case "TV":
                    intent = new Intent(mContext, TVControllerListeningActivity.class);
                    break;
                case "AirSystem":
                    intent = new Intent(mContext, AirSystemActivity.class);
                    break;
                case "Doorbell":
                    intent =new Intent(mContext,SmartDoorbellActivity.class);
                    break;
                default:
                    ToastHelper.showShortMsg("开发中。。。");
                    return null;
            }
        }
        return intent;
    }

}
