package com.boer.delos.constant;

import android.os.Environment;
import android.text.TextUtils;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.model.AddDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceStatusValue;
import com.boer.delos.model.Family;
import com.boer.delos.model.Gateway;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.model.Host;
import com.boer.delos.model.LightName;
import com.boer.delos.model.Link;
import com.boer.delos.model.LinkModel;
import com.boer.delos.model.LinkModelDevicelist;
import com.boer.delos.model.LinkPlan;
import com.boer.delos.model.ModeAct;
import com.boer.delos.model.ModeDevice;
import com.boer.delos.model.RelateDevice;
import com.boer.delos.model.RemoteCMatchData;
import com.boer.delos.model.Room;
import com.boer.delos.model.SceneManage;
import com.boer.delos.model.ThirdUser;
import com.boer.delos.model.TimeTask;
import com.boer.delos.model.User;
import com.boer.delos.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangkai
 * @Description: 常量
 * create at 2015/11/21 10:59
 */
public class Constant {
    //用户是否是管理员
    public static boolean ISMANAGER = false;

    public static final String ACTION_EXIT = "com.boer.exit";
    public static final String ACTION_EXCEPTION = "com.boer.exception";
    public static final String ACTION_DEVICE_UPDATE = "com.boer.device.update";
    public static final String ACTION_GATEWAY_UPDATE = "com.boer.host.update";
    public static final String ACTION_GLOBAL_MODE_UPDATE = "com.boer.globalmode.update";
    public static final String ACTION_ALARM = "com.boer.alarm";
    public static final String ACTION_CAMERA_UPDATE = "com.boer.camera.update";

    public static final int MODE_SETTING_LINKMODE_DEVICE = 1;
    public static final int MODE_SETTING_LINKMODE_GLOBAL = 2;
    public static final int MODE_SETTING_LINKMODE_ROOM = 3;

    /*START delos登录方式*/
    public static final String LOGIN_TYPE_QQ = "qq";
    public static final String LOGIN_TYPE_WEBCHAT = "webchat";
    public static final String LOGIN_TYPE_SINA = "sina";

    /*END*/

    public static String SESSION_KEY = "";
    public static String AES_KEY = "";
    public static byte[] KEY;
    public static String TOKEN = "";// 系统当前令牌
    public static String NEGOTIATE_TOKEN = "";// 握手令牌
    public static String PERMISSIONS = "";
    public static String CURRENTHOSTID = "";
    public static String CURRENTUID = "";
    public static String USERID = "";// 用户Id
    public static String CURRENTPHONE = "";//当前登录的账号

    public static boolean IS_START_POLL_SERVICE = false;
    //是否无线
    public static boolean IS_WIFI = false;
    //是否有网络
    public static boolean IS_INTERNET_CONN = false;
    //是否有直连主机
    public static boolean IS_LOCAL_GATEWAY = false;
    /**
     * 主机是否在线 HomePageActivity 显示主机状态 add by sunzhibin
     */
    public static boolean IS_GATEWAY_ONLINE = true;
    //定位的经度纬度，用于获取城市室外温度
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    //用户主机列表
    public static List<Host> USER_HOSTS = new ArrayList();

    //记录是否已登录
    public static boolean isLogin = false;

    //登录用户
    public static User LOGIN_USER;

    //设备状态的MD5值
    public static String DEVICE_MD5_VALUE = "";
    //主机信息MD5值
    public static String GATEWAY_MD5_VALUE = "";
    //全局模式MD5值
    public static String GLOBALMODE_MD5_VALUE = "";

    //用户登录账号
    public static String LOGIN_USERNNME;
    public static String LOGIN_PASSWORD;

    //全局模式
    public static List<Link> GLOBAL_MODE = new ArrayList<>();
    //主机信息
    public static Gateway GATEWAY;
    //所有设备状态值
    public static List<DeviceRelate> DEVICE_RELATE = new ArrayList<>();

    //本地直连IPdevice
    public static String LOCAL_CONNECTION_IP = "";
    //直连标记
    public static Boolean IS_LOCAL_CONNECTION = Boolean.FALSE;
    //是否切换主机
    public static boolean IS_CHANGE_HOST = false;
    //设备状态是否更新
    public static Boolean IS_DEVICE_STATUS_UPDATE = Boolean.TRUE;

    public static final String NEGOTIATE_TOKEN_TAG = "NEGOTIATE_TOKEN_TAG";// 握手令牌tag
    public static final String LOGIN_TOKEN_TAG = "LOGIN_TOKEN_TAG";// 登录令牌tag
    public static final String REGISTER_TOKEN_TAG = "REGISTER_TOKEN_TAG";// 注册令牌tag
    public static List<Room> roomList = new ArrayList<>();
    public static List<Device> deviceList = new ArrayList<>();
    public static Gateway gateway = new Gateway();
    public static List<RelateDevice> relateList = new ArrayList<>();
    public static String currentChannel = "";
    public static String relateChannel = "";
    //    public static List<GatewayInfo> gatewayInfoList = new ArrayList<>();
    public static List<HashMap<String, String>> relateDevices = new ArrayList<>();

    public static List<LinkModelDevicelist> modelCheck = new ArrayList<>();
    public static List<LinkModelDevicelist> planCheck = new ArrayList<>();
    public static List<LinkModelDevicelist> roomCheck = new ArrayList<>();
    public static LinkPlan linkPlan = new LinkPlan();
    public static LinkModel model = new LinkModel();
    public static String KEY_PATH = "path";
    public static final String PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/jiaweishi/";
    public static final String IMAGE_PATH = PATH + "image/";
    public static String KEY_HOST = "host";
    public static String KEY_USER = "user";
    //主机列表
    public static List<GatewayInfo> gatewayInfos = new ArrayList<>();

    //房间中是否有灯亮 add 2017/3/6
    public static Map<String, Object> sRoomLighting = new HashMap<>();
    public static String sRoomLightingMd5;

    /******************** app 更新*****************************/
    public static final String UPDATE_URL = "http://h5.boericloud.com:18082/delos/download/index.html";
    public static final String UPDATE_FILE_TYPE = "apk";
    public static final String UPDATE_FILE_PATH = "/sdcard/";

    public static class RoomType {
        static final String LIVING_ROOM = "客厅";
        static final String MASTER_BEDROOM = "主卧";
        static final String DINING_ROOM = "餐厅";
        static final String BEDROOM = "次卧";
        static final String BATH_ROOM = "卫生间";
        static final String KITCHEN = "厨房";
        static final String STUDY = "书房";

        static final String OFFICE = "办公室";
        static final String TEA_ROOM = "茶水间";
        static final String BOARD_ROOM = "会议室";
        static final String GUEST_ROOM = "客房";
        static final String SICK_ROOM = "病房";
        static final String GENERAL_MANAGER = "总经理";
        static final String ICU = "ICU";
        static final String VIP_SICK = "VIP病";
        static final String VIP_GUEST = "VIP客";

        static final String ADD = "";

    }


    public static class NotificationEvent {
        public static int NotificationEventNormal = 0;
        static int NotificationEventNewApply = 1;  //新申请
        static int NotificationEVentNewShare = 2;  //新分享
        static int NotificationEventApplyRefresh = 3;  //申请刷新
        static int NotificationEventShareRefresh = 4;  //分享刷新
        static int NotificationEventUserDeleteByAdmin = 5; //用户被管理员删除
    }

    /**
     * 根据roomType设置首页和区域管理界面的图片
     *
     * @param type
     * @param tag
     * @return
     */
    public static int setImage(String type, int tag) {
        if (tag == 0) {
            switch (type) {
                case RoomType.LIVING_ROOM:
                    return R.drawable.ic_living_room;
                case RoomType.MASTER_BEDROOM:
                    return R.drawable.ic_master_bedroom;
                case RoomType.DINING_ROOM:
                    return R.drawable.ic_scence_popup_diningroom;
                case RoomType.BEDROOM:
                    return R.drawable.ic_bedroom;
                case RoomType.BATH_ROOM:
                    return R.drawable.ic_bathroom;
                case RoomType.KITCHEN:
                    return R.drawable.ic_kitchen;
                case RoomType.STUDY:
                    return R.drawable.ic_study;
                case RoomType.OFFICE:
                    return R.drawable.ic_scence_popup_office;
                case RoomType.TEA_ROOM:
                    return R.drawable.ic_scence_popup_tearoom;
                case RoomType.BOARD_ROOM:
                    return R.drawable.ic_boardroom;
                case RoomType.GUEST_ROOM:
                    return R.drawable.ic_guest_room;
                case RoomType.SICK_ROOM:
                    return R.drawable.ic_sickroom;
                case RoomType.GENERAL_MANAGER:
                    return R.drawable.ic_general_manager;
                case RoomType.ICU:
                    return R.drawable.ic_icu;
                case RoomType.VIP_SICK:
                    return R.drawable.ic_vip_sickroom;
                case RoomType.VIP_GUEST:
                    return R.drawable.ic_vip_guest_room;
                case "":
                    return R.mipmap.room_add;
                default:
                    return R.drawable.ic_homepage_study;

            }
        } else {
            switch (type) {
                case RoomType.LIVING_ROOM:
                    return R.drawable.ic_scene_manage_living_room;
                case RoomType.MASTER_BEDROOM:
                    return R.drawable.ic_scene_manage_master_bedroom;
                case RoomType.DINING_ROOM:
                    return R.drawable.ic_scene_manage_diningroom;
                case RoomType.BEDROOM:
                    return R.drawable.ic_scene_manage_bedroom;
                case RoomType.BATH_ROOM:
                    return R.drawable.ic_scene_manage_bath_room;
                case RoomType.KITCHEN:
                    return R.drawable.ic_scene_manage_kitchen;
                case RoomType.STUDY:
                    return R.drawable.ic_scene_manage_study;
                case RoomType.OFFICE:
                    return R.drawable.ic_scene_manage_office;
                case RoomType.TEA_ROOM:
                    return R.drawable.ic_scene_manage_tearoom;
                case RoomType.BOARD_ROOM:
                    return R.drawable.ic_scene_manage_boardroom;
                case RoomType.GUEST_ROOM:
                    return R.drawable.ic_scene_manage_guest_room;
                case RoomType.SICK_ROOM:
                    return R.drawable.ic_scene_manage_sick_room;
                case RoomType.GENERAL_MANAGER:
                    return R.drawable.ic_scene_manage_general_manager;
                case RoomType.ICU:
                    return R.drawable.ic_scene_manage_icu;
                case RoomType.VIP_SICK:
                    return R.drawable.ic_scene_manage_vip_sickroom;
                case RoomType.VIP_GUEST:
                    return R.drawable.ic_scene_manage_vip_guest;
                case "":
                    return R.drawable.ic_scence_manage_add;
                default:
                    return R.drawable.ic_scence_manage_add;

            }
        }

    }

    /**
     * 带蓝圈背景的设备类型列表
     *
     * @return 设备类型集合
     */
    public static List<AddDevice> blueStrokeDeviceList() {
        List<AddDevice> list = new ArrayList<>();
        list.add(new AddDevice(R.drawable.ic_circle_light_n4, R.string.n4, "N4"));
        list.add(new AddDevice(R.drawable.ic_circle_circadian_light, R.string.circadianLight_name, "CircadianLight"));
        list.add(new AddDevice(R.drawable.ic_circle_light_one, R.string.light1_name, "Light1"));
        list.add(new AddDevice(R.drawable.ic_circle_light_two, R.string.light2_name, "Light2"));
        list.add(new AddDevice(R.drawable.ic_circle_light_three, R.string.light3_name, "Light3"));
        list.add(new AddDevice(R.drawable.ic_circle_light_four, R.string.light4_name, "Light4"));
        list.add(new AddDevice(R.drawable.ic_circle_light_adjust, R.string.device_light_adjust, "LightAdjust"));
        list.add(new AddDevice(R.drawable.ic_circle_panel, R.string.pannel_name, "Pannel"));
        list.add(new AddDevice(R.drawable.ic_circle_curtain, R.string.curtain_name, "Curtain"));
        list.add(new AddDevice(R.drawable.ic_circle_tv, R.string.tv_name, "TV"));
        list.add(new AddDevice(R.drawable.ic_circle_dvd, R.string.dvd_name, "DVD"));
        list.add(new AddDevice(R.drawable.ic_circle_iptv, R.string.iptv_name, "IPTV"));
        list.add(new AddDevice(R.drawable.ic_circle_air_condition, R.string.air_condition_name, "AirCondition"));
        list.add(new AddDevice(R.drawable.ic_circle_socket, R.string.socket_name, "Socket"));
        list.add(new AddDevice(R.drawable.ic_circle_monitor, R.string.camera_name, "Camera"));
//        list.add(new AddDevice(R.drawable.ic_circle_key, "钥匙", ""));// TODO 没有此设备类型
        list.add(new AddDevice(R.drawable.ic_circle_exist_sensor, R.string.exist_name, "Exist"));
        list.add(new AddDevice(R.drawable.ic_circle_danger_gas, R.string.ch4_co_name, "Ch4CO"));// 危险气体
        list.add(new AddDevice(R.drawable.ic_circle_gas_sensor, R.string.smoke_name, "Smoke"));// 烟雾传感器
//        list.add(new AddDevice(R.drawable.ic_circle_bracelet, "智能手环", ""));// TODO 没有此设备类型
//        list.add(new AddDevice(R.drawable.ic_circle_medical_mointor, "医疗监护仪", ""));// TODO 没有此设备类型
        list.add(new AddDevice(R.drawable.ic_circle_health_gas, R.string.o2_co2_name, "O2CO2"));// 健康气体
        list.add(new AddDevice(R.drawable.ic_circle_water_logging, R.string.water_name, "Water"));// 	水浸
//        list.add(new AddDevice(R.drawable.ic_circle_illumination_sensor, R.string.floor_heating_name, "FloorHeating"));// TODO 地暖的图片没有切图
        list.add(new AddDevice(R.drawable.ic_circle_tumble_sensor, R.string.fall_name, "Fall"));
        list.add(new AddDevice(R.drawable.ic_circle_environment_sensor, R.string.env_name, "Env"));
        list.add(new AddDevice(R.drawable.ic_circle_alarm, R.string.sos_name, "SOS"));
        list.add(new AddDevice(R.drawable.ic_circle_voice_control, R.string.sound_name, "Sound"));
        list.add(new AddDevice(R.drawable.ic_circle_radiotube, R.string.sov_name, "Sov"));
        list.add(new AddDevice(R.drawable.ic_circle_fingerprint_lock, R.string.lock_name, "Lock"));
        list.add(new AddDevice(R.drawable.ic_circle_background_music, R.string.audio_name, "Audio"));
        list.add(new AddDevice(R.drawable.ic_circle_center_control, R.string.hgc_name, "HGC"));
        list.add(new AddDevice(R.drawable.ic_circle_projector, R.string.projector_name, "Projector"));
        list.add(new AddDevice(R.drawable.ic_circle_main_air_condition, R.string.cac_name, "CAC"));
        list.add(new AddDevice(R.drawable.ic_device_intercom, R.string.intercom_name, "Guard"));
        //new add
        list.add(new AddDevice(R.drawable.ic_circle_table_water_filter, R.string.device_tableWaterFilter, "TableWaterFilter"));
        list.add(new AddDevice(R.drawable.ic_circle_air_cleaner, R.string.device_airFilter, "AirFilter"));
        list.add(new AddDevice(R.drawable.ic_circle_floor_water_filter, R.string.device_floorWaterFilter, "FloorWaterFilter"));

        list.add(new AddDevice(R.drawable.ic_circle_laser_egg_filter, R.string.device_laserEggFilter, "LaserEgg"));

        list.add(new AddDevice(R.drawable.ic_circle_door_bell_filter, R.string.device_doorBellFilter, "Doorbell"));

        list.add(new AddDevice(R.drawable.ic_circle_acoustooptic_alarm_filter, R.string.device_AcoustoOpticAlarm, "AcoustoOpticAlarm"));
        list.add(new AddDevice(R.drawable.ic_circle_air_system, R.string.air_system_name, "AirSystem"));

        return list;
    }

    /**
     * 取得设备类型的图片
     *
     * @param type
     * @return
     */
    public static int getResIdWithType(String type) {
        for (AddDevice addDevice : Constant.blueStrokeDeviceList()) {
            if (addDevice.getType().equals(type)) {
                return addDevice.getResId();
            }
        }
        return R.drawable.ic_circle_light_one;
    }


    /**
     * 带蓝圈背景的设备类型集合
     *
     * @return 设备类型集合
     */
    public static Map<String, List<AddDevice>> blueStrokeDeviceMap() {

        Map<String, List<AddDevice>> map = new HashMap<>();

        List<AddDevice> deviceList11 = new ArrayList<>();
        deviceList11.add(new AddDevice(R.drawable.ic_circle_air_cleaner, R.string.device_airFilter, "AirFilter"));
        map.put("空气净化", deviceList11);

        List<AddDevice> deviceList12 = new ArrayList<>();
        deviceList12.add(new AddDevice(R.drawable.ic_circle_table_water_filter, R.string.device_tableWaterFilter, "TableWaterFilter"));
        deviceList12.add(new AddDevice(R.drawable.ic_circle_floor_water_filter, R.string.device_floorWaterFilter, "FloorWaterFilter"));
        map.put("水质净化", deviceList12);

        List<AddDevice> deviceList1 = new ArrayList<>();
        deviceList1.add(new AddDevice(R.drawable.ic_circle_light_n4, R.string.n4, "N4"));
        deviceList1.add(new AddDevice(R.drawable.ic_circle_light_one, R.string.light1_name, "Light1"));
        deviceList1.add(new AddDevice(R.drawable.ic_circle_light_two, R.string.light2_name, "Light2"));
        deviceList1.add(new AddDevice(R.drawable.ic_circle_light_three, R.string.light3_name, "Light3"));
        deviceList1.add(new AddDevice(R.drawable.ic_circle_light_four, R.string.light4_name, "Light4"));
        deviceList1.add(new AddDevice(R.drawable.ic_circle_light_adjust, R.string.device_light_adjust, "LightAdjust"));
        map.put("照明控制", deviceList1);

        List<AddDevice> deviceList2 = new ArrayList<>();
        deviceList2.add(new AddDevice(R.drawable.ic_circle_air_condition, R.string.air_condition_name, "AirCondition"));
        deviceList2.add(new AddDevice(R.drawable.ic_circle_main_air_condition, R.string.cac_name, "CAC"));
        map.put("空调控制", deviceList2);

        List<AddDevice> deviceList3 = new ArrayList<>();
        deviceList3.add(new AddDevice(R.drawable.ic_circle_monitor, R.string.camera_name, "Camera"));
        deviceList3.add(new AddDevice(R.drawable.ic_device_intercom, R.string.intercom_name, "Guard"));
        deviceList3.add(new AddDevice(R.drawable.ic_circle_door_bell_filter, R.string.doorbell_name, "Doorbell"));
        map.put("监控控制", deviceList3);

        List<AddDevice> deviceList4 = new ArrayList<>();
        deviceList4.add(new AddDevice(R.drawable.ic_circle_curtain, R.string.curtain_name, "Curtain"));
        map.put("窗帘控制", deviceList4);

        List<AddDevice> deviceList5 = new ArrayList<>();
        deviceList5.add(new AddDevice(R.drawable.ic_circle_socket, R.string.socket_name, "Socket"));
        map.put("电源管理", deviceList5);


        List<AddDevice> deviceList6 = new ArrayList<>();
        deviceList6.add(new AddDevice(R.drawable.ic_circle_panel, R.string.pannel_name, "Pannel"));
        deviceList6.add(new AddDevice(R.drawable.ic_circle_center_control, R.string.hgc_name, "HGC"));
        // new add
        map.put("综合服务", deviceList6);

        List<AddDevice> deviceList7 = new ArrayList<>();
        deviceList7.add(new AddDevice(R.drawable.ic_circle_tv, R.string.tv_name, "TV"));
        deviceList7.add(new AddDevice(R.drawable.ic_circle_dvd, R.string.dvd_name, "DVD"));
        deviceList7.add(new AddDevice(R.drawable.ic_circle_iptv, R.string.iptv_name, "IPTV"));
        deviceList7.add(new AddDevice(R.drawable.ic_circle_voice_control, R.string.sound_name, "Sound"));
        deviceList7.add(new AddDevice(R.drawable.ic_circle_background_music, R.string.audio_name, "Audio"));
        deviceList7.add(new AddDevice(R.drawable.ic_circle_projector, R.string.projector_name, "Projector"));
        map.put("影音控制", deviceList7);


        List<AddDevice> deviceList8 = new ArrayList<>();
        deviceList8.add(new AddDevice(R.drawable.ic_circle_danger_gas, R.string.ch4_co_name, "Ch4CO"));
        deviceList8.add(new AddDevice(R.drawable.ic_circle_gas_sensor, R.string.smoke_name, "Smoke"));
        deviceList8.add(new AddDevice(R.drawable.ic_circle_health_gas, R.string.o2_co2_name, "O2CO2"));
        deviceList8.add(new AddDevice(R.drawable.ic_circle_environment_sensor, R.string.env_name, "Env"));
        deviceList8.add(new AddDevice(R.drawable.ic_circle_laser_egg_filter, R.string.device_laserEggFilter, "LaserEgg"));
        deviceList8.add(new AddDevice(R.drawable.ic_circle_air_system, R.string.air_system_name, "AirSystem"));
        map.put("气体检测", deviceList8);

        List<AddDevice> deviceList9 = new ArrayList<>();
        deviceList9.add(new AddDevice(R.drawable.ic_circle_exist_sensor, R.string.exist_name, "Exist"));
        deviceList9.add(new AddDevice(R.drawable.ic_circle_gsm, R.string.gsm_name, "Gsm"));
        deviceList9.add(new AddDevice(R.drawable.ic_circle_curtain_sensor, R.string.curtainSensor_name, "CurtainSensor"));
        deviceList9.add(new AddDevice(R.drawable.ic_circle_tumble_sensor, R.string.fall_name, "Fall"));
        deviceList9.add(new AddDevice(R.drawable.ic_circle_water_logging, R.string.water_name, "Water"));
        deviceList9.add(new AddDevice(R.drawable.ic_circle_radiotube, R.string.sov_name, "Sov"));
        deviceList9.add(new AddDevice(R.drawable.ic_circle_alarm, R.string.sos_name, "SOS"));
        deviceList9.add(new AddDevice(R.drawable.ic_circle_acoustooptic_alarm_filter, R.string.device_AcoustoOpticAlarm, "AcoustoOpticAlarm"));

        map.put("安全告警", deviceList9);

        List<AddDevice> deviceList10 = new ArrayList<>();
        deviceList10.add(new AddDevice(R.drawable.ic_circle_fingerprint_lock, R.string.lock_name, "Lock"));
        map.put("安全防护", deviceList10);

        return map;
    }

    /**
     * 图标为白色的设备类型集合
     *
     * @return 设备类型集合
     */
    public static Map<String, List<AddDevice>> whiteDeviceMap() {
        Map<String, List<AddDevice>> map = new HashMap<>();

        List<AddDevice> deviceList11 = new ArrayList<>();
        deviceList11.add(new AddDevice(R.drawable.ic_white_air_cleaner, R.string.device_airFilter, "AirFilter"));
        map.put("空气净化", deviceList11);

        List<AddDevice> deviceList12 = new ArrayList<>();
        deviceList12.add(new AddDevice(R.drawable.ic_white_table_water_filter, R.string.device_tableWaterFilter, "TableWaterFilter"));
        deviceList12.add(new AddDevice(R.drawable.ic_white_floor_water_filter, R.string.device_floorWaterFilter, "FloorWaterFilter"));
        map.put("水质净化", deviceList12);

        List<AddDevice> deviceList1 = new ArrayList<>();
        deviceList1.add(new AddDevice(R.drawable.ic_white_light_n4, R.string.n4, "N4"));
        deviceList1.add(new AddDevice(R.drawable.ic_white_light_one, R.string.light1_name, "Light1"));
        deviceList1.add(new AddDevice(R.drawable.ic_white_light_two, R.string.light2_name, "Light2"));
        deviceList1.add(new AddDevice(R.drawable.ic_white_light_three, R.string.light3_name, "Light3"));
        deviceList1.add(new AddDevice(R.drawable.ic_white_light_four, R.string.light4_name, "Light4"));
        deviceList1.add(new AddDevice(R.drawable.ic_white_light_adjust, R.string.device_light_adjust, "LightAdjust"));
        map.put("照明控制", deviceList1);

        List<AddDevice> deviceList2 = new ArrayList<>();
        deviceList2.add(new AddDevice(R.drawable.ic_white_air_condition, R.string.air_condition_name, "AirCondition"));
        deviceList2.add(new AddDevice(R.drawable.ic_white_main_air_condition, R.string.cac_name, "CAC"));
        map.put("空调控制", deviceList2);

        List<AddDevice> deviceList3 = new ArrayList<>();
        deviceList3.add(new AddDevice(R.drawable.ic_white_monitor, R.string.camera_name, "Camera"));
        deviceList3.add(new AddDevice(R.drawable.ic_white_intercom, R.string.intercom_name, "Guard"));
        deviceList3.add(new AddDevice(R.drawable.ic_white_door_bell, R.string.doorbell_name, "Doorbell"));
        map.put("监控控制", deviceList3);

        List<AddDevice> deviceList4 = new ArrayList<>();
        deviceList4.add(new AddDevice(R.drawable.ic_white_curtain, R.string.curtain_name, "Curtain"));
        map.put("窗帘控制", deviceList4);

        List<AddDevice> deviceList5 = new ArrayList<>();
        deviceList5.add(new AddDevice(R.drawable.ic_white_socket, R.string.socket_name, "Socket"));
        map.put("电源管理", deviceList5);


        List<AddDevice> deviceList6 = new ArrayList<>();
        deviceList6.add(new AddDevice(R.drawable.ic_white_panel, R.string.pannel_name, "Pannel"));
        deviceList6.add(new AddDevice(R.drawable.ic_white_center_control, R.string.hgc_name, "HGC"));
        //new add


        map.put("综合服务", deviceList6);

        List<AddDevice> deviceList7 = new ArrayList<>();
        deviceList7.add(new AddDevice(R.drawable.ic_white_tv, R.string.tv_name, "TV"));
        deviceList7.add(new AddDevice(R.drawable.ic_white_dvd, R.string.dvd_name, "DVD"));
        deviceList7.add(new AddDevice(R.drawable.ic_white_iptv, R.string.iptv_name, "IPTV"));
        deviceList7.add(new AddDevice(R.drawable.ic_white_voice_control, R.string.sound_name, "Sound"));
        deviceList7.add(new AddDevice(R.drawable.ic_white_background_music, R.string.audio_name, "Audio"));
        deviceList7.add(new AddDevice(R.drawable.ic_white_projector, R.string.projector_name, "Projector"));
        map.put("影音控制", deviceList7);


        List<AddDevice> deviceList8 = new ArrayList<>();
        deviceList8.add(new AddDevice(R.drawable.ic_white_danger_gas, R.string.ch4_co_name, "Ch4CO"));
        deviceList8.add(new AddDevice(R.drawable.ic_white_gas_sensor, R.string.smoke_name, "Smoke"));
        deviceList8.add(new AddDevice(R.drawable.ic_white_health_gas, R.string.o2_co2_name, "O2CO2"));
        deviceList8.add(new AddDevice(R.drawable.ic_white_environment_sensor, R.string.env_name, "Env"));
        deviceList8.add(new AddDevice(R.drawable.ic_white_laser_egg_filter, R.string.device_laserEggFilter, "LaserEgg"));
        deviceList8.add(new AddDevice(R.drawable.ic_white_air_system, R.string.air_system_name, "AirSystem"));
        map.put("气体检测", deviceList8);

        List<AddDevice> deviceList9 = new ArrayList<>();
        deviceList9.add(new AddDevice(R.drawable.ic_white_exist_sensor, R.string.exist_name, "Exist"));
        deviceList9.add(new AddDevice(R.drawable.ic_white_gsm, R.string.gsm_name, "Gsm")); //add
        deviceList9.add(new AddDevice(R.drawable.ic_white_curtain_sensor, R.string.curtainSensor_name, "CurtainSensor"));
        deviceList9.add(new AddDevice(R.drawable.ic_white_tumble_sensor, R.string.fall_name, "Fall"));
        deviceList9.add(new AddDevice(R.drawable.ic_white_water_logging, R.string.water_name, "Water"));
        deviceList9.add(new AddDevice(R.drawable.ic_white_radiotube, R.string.sov_name, "Sov"));
        deviceList9.add(new AddDevice(R.drawable.ic_white_alarm, R.string.sos_name, "SOS"));
        deviceList9.add(new AddDevice(R.drawable.ic_white_acoustooptic_alarm, R.string.device_AcoustoOpticAlarm, "AcoustoOpticAlarm"));

        map.put("安全告警", deviceList9);

        List<AddDevice> deviceList10 = new ArrayList<>();
        deviceList10.add(new AddDevice(R.drawable.ic_white_fingerprint_lock, R.string.lock_name, "Lock"));
        map.put("安全防护", deviceList10);

        return map;
    }

    /**
     * 图标为蓝色的设备类型列表
     *
     * @return 设备类型集合
     */
    public static List<AddDevice> blueDeviceList() {
        List<AddDevice> deviceList = new ArrayList<>();
        deviceList.add(new AddDevice(R.drawable.ic_device_light_n4, R.string.n4, "N4"));
        deviceList.add(new AddDevice(R.drawable.ic_device_circadian_light, R.string.circadianLight_name, "CircadianLight"));
        deviceList.add(new AddDevice(R.drawable.ic_device_light_one, R.string.light1_name, "Light1"));
        deviceList.add(new AddDevice(R.drawable.ic_device_light_two, R.string.light2_name, "Light2"));
        deviceList.add(new AddDevice(R.drawable.ic_device_light_three, R.string.light3_name, "Light3"));
        deviceList.add(new AddDevice(R.drawable.ic_device_light_four, R.string.light4_name, "Light4"));
        deviceList.add(new AddDevice(R.drawable.ic_device_light_adjust, R.string.device_light_adjust, "LightAdjust"));
        deviceList.add(new AddDevice(R.drawable.ic_device_pannel, R.string.pannel_name, "Pannel"));
        deviceList.add(new AddDevice(R.drawable.ic_device_curtain, R.string.curtain_name, "Curtain"));
        deviceList.add(new AddDevice(R.drawable.ic_device_tv, R.string.tv_name, "TV"));
        deviceList.add(new AddDevice(R.drawable.ic_device_dvd, R.string.dvd_name, "DVD"));
        deviceList.add(new AddDevice(R.drawable.ic_device_iptv, R.string.iptv_name, "IPTV"));
        deviceList.add(new AddDevice(R.drawable.ic_device_air_condition, R.string.air_condition_name, "AirCondition"));
        deviceList.add(new AddDevice(R.drawable.ic_device_socket, R.string.socket_name, "Socket"));
        deviceList.add(new AddDevice(R.drawable.ic_device_monitor, R.string.camera_name, "Camera"));
        deviceList.add(new AddDevice(R.drawable.ic_device_exist_sensor, R.string.exist_name, "Exist"));
        deviceList.add(new AddDevice(R.drawable.ic_device_co, R.string.ch4_co_name, "Ch4CO"));
        deviceList.add(new AddDevice(R.drawable.ic_device_gas_sensor, R.string.smoke_name, "Smoke"));
        deviceList.add(new AddDevice(R.drawable.ic_device_guard, R.string.intercom_name, "Guard"));
//        deviceList.add(new AddDevice(R.drawable.ic_device_bracelet, "智能手环"));
//        deviceList.add(new AddDevice(R.drawable.ic_device_medical_mointor, "医疗监护仪"));
        deviceList.add(new AddDevice(R.drawable.ic_device_o2, R.string.o2_co2_name, "O2CO2"));
        deviceList.add(new AddDevice(R.drawable.ic_device_water, R.string.water_name, "Water"));
//        deviceList.add(new AddDevice(R.drawable.ic_device_illumination_sensor, R.string.floor_heating_name, "FloorHeating"));// TODO 地暖的图片没有切图
        deviceList.add(new AddDevice(R.drawable.ic_device_tumble_sensor, R.string.fall_name, "Fall"));
        deviceList.add(new AddDevice(R.drawable.ic_device_env, R.string.env_name, "Env"));
        deviceList.add(new AddDevice(R.drawable.ic_device_alarm, R.string.sos_name, "SOS"));
        deviceList.add(new AddDevice(R.drawable.ic_device_voice_control, R.string.sound_name, "Sound"));
        deviceList.add(new AddDevice(R.drawable.ic_device_radiotube, R.string.sov_name, "Sov"));
        deviceList.add(new AddDevice(R.drawable.ic_device_fingerprint_lock, R.string.lock_name, "Lock"));
        deviceList.add(new AddDevice(R.drawable.ic_device_background_music, R.string.audio_name, "Audio"));
        deviceList.add(new AddDevice(R.drawable.ic_device_center_control, R.string.hgc_name, "HGC"));
        deviceList.add(new AddDevice(R.drawable.ic_device_projector, R.string.projector_name, "Projector"));
        deviceList.add(new AddDevice(R.drawable.ic_device_air_condition_two, R.string.cac_name, "CAC"));

        deviceList.add(new AddDevice(R.drawable.ic_device_gsm, R.string.gsm_name, "Gsm"));
        deviceList.add(new AddDevice(R.drawable.ic_device_curtain_sensor, R.string.curtainSensor_name, "CurtainSensor"));

        //台上净化器  空气净化器 台下净水器
        deviceList.add(new AddDevice(R.drawable.ic_device_table_water_filter, R.string.device_tableWaterFilter, "TableWaterFilter"));
        deviceList.add(new AddDevice(R.drawable.ic_device_air_cleaner, R.string.device_airFilter, "AirFilter"));
        deviceList.add(new AddDevice(R.drawable.ic_device_floor_water_filter, R.string.device_floorWaterFilter, "FloorWaterFilter"));
        deviceList.add(new AddDevice(R.drawable.ic_device_laser_egg_filter, R.string.device_laserEggFilter, "LaserEgg"));

        deviceList.add(new AddDevice(R.drawable.ic_device_door_bell_filter, R.string.device_doorBellFilter, "Doorbell"));

        deviceList.add(new AddDevice(R.drawable.ic_device_acoustooptic_alarm_filter, R.string.device_AcoustoOpticAlarm, "AcoustoOpticAlarm"));
        deviceList.add(new AddDevice(R.drawable.ic_device_air_system, R.string.air_system_name, "AirSystem"));


        return deviceList;
    }


    /**
     * 取得设备类型的图片
     *
     * @param type
     * @return
     */
    public static int getNonCircleBlueResIdWithType(String type) {
        for (AddDevice addDevice : Constant.blueDeviceList()) {
            if (addDevice.getType().equals(type)) {
                return addDevice.getResId();
            }
        }
        return R.drawable.ic_device_light_one;
    }

    /**
     * 新建场景popup中的场景类型
     *
     * @return 场景类型集合
     */
    public static List<SceneManage> sceneTypeList() {
        List<SceneManage> sceneManageList = new ArrayList<>();
        sceneManageList.add(new SceneManage(R.drawable.ic_living_room, R.string.living_room, R.string.living_room));
        sceneManageList.add(new SceneManage(R.drawable.ic_master_bedroom, R.string.master_bedroom, R.string.master_bedroom));
        sceneManageList.add(new SceneManage(R.drawable.ic_scence_popup_diningroom, R.string.dining_room, R.string.dining_room));
        sceneManageList.add(new SceneManage(R.drawable.ic_bedroom, R.string.bedroom, R.string.bedroom));
        sceneManageList.add(new SceneManage(R.drawable.ic_bathroom, R.string.bath_room, R.string.bath_room));
        sceneManageList.add(new SceneManage(R.drawable.ic_kitchen, R.string.kitchen, R.string.kitchen));
        sceneManageList.add(new SceneManage(R.drawable.ic_study, R.string.study, R.string.study));
        sceneManageList.add(new SceneManage(R.drawable.ic_scence_popup_office, R.string.office, R.string.office));
        sceneManageList.add(new SceneManage(R.drawable.ic_scence_popup_tearoom, R.string.tea_room, R.string.tea_room));
        sceneManageList.add(new SceneManage(R.drawable.ic_boardroom, R.string.boardroom, R.string.boardroom));
        sceneManageList.add(new SceneManage(R.drawable.ic_guest_room, R.string.guest_room, R.string.guest_room));
//        sceneManageList.add(new SceneManage(R.drawable.ic_sickroom, R.string.sick_room, R.string.sick_room));
        sceneManageList.add(new SceneManage(R.drawable.ic_general_manager, R.string.general_manager, R.string.general_manager));
//        sceneManageList.add(new SceneManage(R.drawable.ic_icu, R.string.icu, R.string.icu));
//        sceneManageList.add(new SceneManage(R.drawable.ic_vip_sickroom, R.string.vip_sickroom, R.string.vip_sickroom));
//        sceneManageList.add(new SceneManage(R.drawable.ic_vip_guest_room, R.string.vip_guest, R.string.vip_guest));
        return sceneManageList;
    }

    public static int setPosition(String roomType) {
        switch (roomType) {
            case RoomType.LIVING_ROOM:
                return 0;
            case RoomType.MASTER_BEDROOM:
                return 1;
            case RoomType.DINING_ROOM:
                return 2;
            case RoomType.BEDROOM:
                return 3;
            case RoomType.BATH_ROOM:
                return 4;
            case RoomType.KITCHEN:
                return 5;
            case RoomType.STUDY:
                return 6;
            case RoomType.OFFICE:
                return 7;
            case RoomType.TEA_ROOM:
                return 8;
            case RoomType.BOARD_ROOM:
                return 9;
            case RoomType.GUEST_ROOM:
                return 10;
            case RoomType.SICK_ROOM:
                return 11;
            case RoomType.GENERAL_MANAGER:
                return 11;
            case RoomType.ICU:
                return 13;
            case RoomType.VIP_SICK:
                return 14;
            case RoomType.VIP_GUEST:
                return 15;
            default:
                return 0;
        }
    }

    /**
     * 根据类型返回类型名称
     *
     * @param type
     * @return
     */
    public static String getDeviceTypeNameWithType(String type) {
        switch (type) {
            case "Light1":
//                return "单联灯";
                return BaseApplication.getAppContext().getString(R.string.light1_name);
            case "Light2":
//                return "双联灯";
                return BaseApplication.getAppContext().getString(R.string.light2_name);
            case "Light3":
//                return "三联灯";
                return BaseApplication.getAppContext().getString(R.string.light3_name);
            case "Light4":
//                return "四联灯";
                return BaseApplication.getAppContext().getString(R.string.light4_name);
            case "LightAdjust":
//                return "调光灯";
                return BaseApplication.getAppContext().getString(R.string.device_light_adjust);
            case "Pannel":
//                return "场景面板";
                return BaseApplication.getAppContext().getString(R.string.pannel_name);
            case "Curtain":
//                return "窗帘";
                return BaseApplication.getAppContext().getString(R.string.curtain_name);
            case "TV":
//                return "电视";
                return BaseApplication.getAppContext().getString(R.string.tv_name);
            case "DVD":
//                return "DVD";
                return BaseApplication.getAppContext().getString(R.string.dvd_name);
            case "IPTV":
//                return "IPTV";
                return BaseApplication.getAppContext().getString(R.string.iptv_name);
            case "AirCondition":
//                return "空调";
                return BaseApplication.getAppContext().getString(R.string.air_condition_name);
            case "Socket":
//                return "插座";
                return BaseApplication.getAppContext().getString(R.string.socket_name);
            case "Camera":
//                return "监控";
                return BaseApplication.getAppContext().getString(R.string.camera_name);
            case "Exist":
//                return "存在传感器";
                return BaseApplication.getAppContext().getString(R.string.exist_name);
            case "Ch4CO":
//                return "危险气体";
                return BaseApplication.getAppContext().getString(R.string.ch4_co_name);
            case "Smoke":
//                return "烟雾检测";
                return BaseApplication.getAppContext().getString(R.string.smoke_name);
            case "O2CO2":
//                return "健康气体";
                return BaseApplication.getAppContext().getString(R.string.o2_co2_name);
            case "Water":
//                return "水浸传感器";
                return BaseApplication.getAppContext().getString(R.string.water_name);
            case "FloorHeating":
//                return "地暖";
                return BaseApplication.getAppContext().getString(R.string.floor_heating_name);
            case "Fall":
//                return "跌倒传感器";
                return BaseApplication.getAppContext().getString(R.string.fall_name);
            case "Env":
//                return "环境监测器";
                return BaseApplication.getAppContext().getString(R.string.env_name);
            case "SOS":
//                return "报警器";
                return BaseApplication.getAppContext().getString(R.string.sos_name);
            case "Sound":
//                return "音频控制";
                return BaseApplication.getAppContext().getString(R.string.sound_name);
            case "Sov":
//                return "电磁阀";
                return BaseApplication.getAppContext().getString(R.string.sov_name);
            case "Lock":
//                return "指纹锁";
                return BaseApplication.getAppContext().getString(R.string.lock_name);
            case "Audio":
//                return "背景音乐";
                return BaseApplication.getAppContext().getString(R.string.audio_name);
            case "HGC":
//                return "中控面板";
                return BaseApplication.getAppContext().getString(R.string.hgc_name);
            case "Projector":
//                return "投影仪";
                return BaseApplication.getAppContext().getString(R.string.projector_name);
            case "CAC":
//                return "中央空调";
                return BaseApplication.getAppContext().getString(R.string.cac_name);
            case "Gsm": // add by sunzhibin
//                return "门窗磁";
                return BaseApplication.getAppContext().getString(R.string.gsm_name);
            case "CurtainSensor":
//                return "红外入侵";
                return BaseApplication.getAppContext().getString(R.string.curtainSensor_name);
            case "TableWaterFilter"://台上净化器
//                return "台上净化器";
                return BaseApplication.getAppContext().getString(R.string.device_tableWaterFilter);
            case "AirFilter": //空气净化器
//                return "空气净化器";
                return BaseApplication.getAppContext().getString(R.string.device_airFilter);
            case "FloorWaterFilter"://台下净水器
//                return "台下净水器";
                return BaseApplication.getAppContext().getString(R.string.device_floorWaterFilter);
            case "CircadianLight":
//                return  "节律灯";
                return BaseApplication.getAppContext().getString(R.string.circadianLight_name);
            case "N4":
                return BaseApplication.getAppContext().getString(R.string.n4);
            case "LaserEgg":
                return BaseApplication.getAppContext().getString(R.string.device_laserEggFilter);
            case "Doorbell":
                return BaseApplication.getAppContext().getString(R.string.device_doorBellFilter);
            case "AcoustoOpticAlarm":
                return BaseApplication.getAppContext().getString(R.string.device_AcoustoOpticAlarm);
            case "AirSystem":
                return BaseApplication.getAppContext().getString(R.string.air_system_name);
            default:
                return "";
        }
    }


    /**
     * 根据模式名称返回模式图片
     *
     * @param name
     * @return
     */
    public static int modeImageWithModeName(String name) {
        switch (name) {
            case "回家模式配置":
                return R.mipmap.scene_home_small;
            case "离家模式配置":
                return R.mipmap.scene_leave_small;
            case "会客模式配置":
                return R.mipmap.scene_parlor_small;
            case "就餐模式配置":
                return R.mipmap.scene_meal_small;
            case "撤防模式配置":
                return R.mipmap.scene_withdraw_small;
            case "布防模式配置":
                return R.mipmap.scene_counter_small;

            // TODO add by sunzhibin
            case "晨起模式":
                return R.mipmap.ic_room_morning;

            case "睡眠模式":
                return R.mipmap.ic_room_seelp;

            case "阅读模式":
                return R.mipmap.ic_room_read;

            case "娱乐模式":
                return R.mipmap.ic_room_play;

            default:
                return R.mipmap.scene_home;
        }
    }

    /**
     * 根据模式名称返回模式图片
     *
     * @param name
     * @return
     */
    public static int modeImageWithModeName2(String name) {
        switch (name) {
            case "回家模式配置":
                return R.mipmap.scene_home;
            case "离家模式配置":
                return R.mipmap.scene_leave;
            case "会客模式配置":
                return R.mipmap.scene_parlor;
            case "就餐模式配置":
                return R.mipmap.scene_meal;
            case "撤防模式配置":
                return R.mipmap.scene_withdraw;
            case "布防模式配置":
                return R.mipmap.scene_counter;

            // TODO add by sunzhibin
            case "晨起模式":
                return R.mipmap.ic_room_morning;

            case "睡眠模式":
                return R.mipmap.ic_room_seelp;

            case "阅读模式":
                return R.mipmap.ic_room_read;

            case "娱乐模式":
                return R.mipmap.ic_room_play;

            default:
                return R.mipmap.scene_home;
        }
    }

    /**
     * 根据模式名称获取模式别名
     *
     * @param modeName
     * @return
     */
    public static Link getTagFromModeName(String modeName) {
        if (Constant.GLOBAL_MODE == null) {
            return null;
        }
        for (Link mode : Constant.GLOBAL_MODE) {
            if (modeName.equals(mode.getName())) {
                return mode;
            }
        }
        return null;
    }

    /**
     * 根据设备type拿到类型
     *
     * @param deviceType
     * @return <string name="air_clean">空气净化</string>
     * <string name="water_clean">水质净化</string>
     * <string name="light_control">照明控制</string>
     * <string name="air_condition_control">空调控制</string>
     * <string name="camera_control">监控控制</string>
     * <string name="curtain_control">窗帘控制</string>
     * <string name="elec_control">电源管理</string>
     * <string name="general_service">综合服务</string>
     * <string name="air_check">气体检测</string>
     * <string name="video_control">影音控制</string>
     * <string name="safety_alarm">安全告警</string>
     * <string name="safety_protect">安全防护</string>
     */
    public static String getControlTypeByType(String deviceType) {
        switch (deviceType) {
            case "N4":
            case "CircadianLight":
            case "Light1":
            case "Light2":
            case "Light3":
            case "Light4":
            case "LightAdjust":
//                return "照明控制";
                return BaseApplication.getAppContext().getString(R.string.light_control);
            case "AirCondition":
            case "CAC":
//                return "空调控制";
                return BaseApplication.getAppContext().getString(R.string.air_condition_control);
            case "Camera":
            case "Guard":
            case "Doorbell":
//                return "监控控制";
                return BaseApplication.getAppContext().getString(R.string.camera_control);
            case "Curtain":
//                return "窗帘控制";
                return BaseApplication.getAppContext().getString(R.string.curtain_control);
            case "Socket":
//                return "电源管理";
                return BaseApplication.getAppContext().getString(R.string.elec_control);
            case "TableWaterFilter"://台上净化器
            case "FloorWaterFilter"://台下净水器
//                return "水质净化";
                return BaseApplication.getAppContext().getString(R.string.water_clean);
            case "AirFilter": //空气净化器
//                return "空气净化";
                return BaseApplication.getAppContext().getString(R.string.air_clean);
            case "Pannel":
            case "HGC":
//                return "综合服务";
                return BaseApplication.getAppContext().getString(R.string.general_service);

            case "Audio":
            case "Projector":
            case "DVD":
            case "IPTV":
            case "TV":
            case "Sound":
//                return "影音控制";
                return BaseApplication.getAppContext().getString(R.string.video_control);

            case "Ch4CO":
            case "O2CO2":
            case "Env":
            case "Smoke":
            case "LaserEgg":
            case "AirSystem":
                return BaseApplication.getAppContext().getString(R.string.air_check);
            case "Exist":
            case "Fall":
            case "Water":
            case "Sov":
            case "Gsm":
            case "SOS":
            case "CurtainSensor":
            case "AcoustoOpticAlarm":
//                return "安全告警";
                return BaseApplication.getAppContext().getString(R.string.safety_alarm);
            case "Lock":
//                return "安全防护";
                return BaseApplication.getAppContext().getString(R.string.safety_protect);
            case "FloorHeating":
                return "地暖控制";
//                return BaseApplication.getAppContext().getString(R.string.light_control);

            default:
                return "";
        }
    }

    /**
     * 获取'舒适生活'中控制设备名称
     *
     * @param key
     * @return
     */
    public static String getControlTypeName(String key) {
        switch (key) {
            case "1_Light":
                return "照明控制";
            case "2_Socket":
                return "电源管理";
            case "3_Air":
                return "空调控制";
            case "4_Camera":
                return "监控控制";
            case "5_Curtain":
                return "窗帘控制";
            case "6_Service":
                return "综合服务";
            case "7_Vedio":
                return "影音控制";
            case "8_Gas":
                return "气体检测";
            case "9_Env":
                return "安全告警";
            case "91_SOS":
                return "安全防护";
//            case "92_Guard":
//                return "门禁控制";
//            case "93_Lock":
//                return "指纹锁控制";
//            case "94_Floor":
//                return "地暖控制";
//            case "95_Projector":
//                return "投影仪控制";
//            case "96_Audio":
//                return "背景音乐控制";
            case "97_Water": //水质净化
                return "水质净化";
            case "98_AirFilter": //空气净化
                return "空气净化";

        }
        return "";
    }


    /**
     * 获取'舒适生活'中控制设备图片
     *
     * @param key
     * @return
     */
    public static int getControlImageId(String key) {
        switch (key) {
            case "1_Light":
                return R.drawable.ic_light_l;
            case "2_Socket":
                return R.drawable.ic_socket_l;
            case "3_Air":
                return R.drawable.ic_air_l;
            case "4_Camera":
                return R.drawable.ic_camera_l;
            case "5_Curtain":
                return R.drawable.ic_curtain_l;
            case "6_Service":
                return R.drawable.ic_panel_l;
            case "7_Vedio":
                return R.drawable.ic_vedio_l;
            case "8_Gas":
                return R.drawable.ic_gas_l;
            case "9_Env":
                return R.drawable.ic_alarm_l;
            case "91_SOS":
                return R.drawable.ic_device_exist;
            case "92_Guard":
                return R.drawable.ic_graud_l;
            case "93_Lock":
                return R.drawable.ic_lock_l;
            case "94_Floor":
                return R.drawable.ic_floorheart;
            case "95_Projector":
                return R.drawable.ic_project_l;
            case "96_Audio":
                return R.drawable.ic_audio_l;
            case "97_Water": //水质净化
                return R.drawable.ic_device_table_water_filter;

            case "98_AirFilter": //空气净化
                return R.drawable.ic_device_air_cleaner;
        }
        return 0;
    }


    /**
     * '舒适生活'中设备合并处理
     *
     * @param relateList
     */
    public static List<Map<String, Object>> combinDeviceList(List<DeviceRelate> relateList) {
        if (relateList == null)
            return Collections.emptyList();

        //统计每种设备数量
        Map<String, Integer> map = new HashMap<>();
        map.put("1_Light", 0);    //照明控制
        map.put("2_Socket", 0);   //电源管理
        map.put("3_Air", 0);      //空调控制
        map.put("4_Camera", 0);   //监控控制
        map.put("5_Curtain", 0);  //窗帘控制
        map.put("6_Service", 0);  //综合服务
        map.put("7_Vedio", 0);    //影音控制
        map.put("8_Gas", 0);      //气体检测
        map.put("9_Env", 0);      //安全告警
        map.put("91_SOS", 0);     //安全防护
        map.put("92_Guard", 0);   //门禁控制
//        map.put("93_Lock", 0);    //指纹锁控制
//        map.put("94_Floor", 0);   //地暖控制
//        map.put("95_Projector", 0); //投影仪
//        map.put("96_Audio", 0); //背景音乐
        map.put("97_Water", 0); //水质净化
        map.put("98_AirFilter", 0); //空气净化
        //统计数据
        for (DeviceRelate deviceRelate : relateList) {
            String deviceType = deviceRelate.getDeviceProp().getType();
            switch (deviceType) {
                case "Light1":
                case "Light2":
                case "Light3":
                case "Light4":
                case "LightAdjust":
                case "CircadianLight":
                    Integer count = map.get("1_Light");
                    map.put("1_Light", ++count);
                    break;
                case "Socket":
                    count = map.get("2_Socket");
                    map.put("2_Socket", ++count);
                    break;
                case "AirCondition":
                case "CAC":
                    count = map.get("3_Air");
                    map.put("3_Air", ++count);
                    break;
                case "Camera":
                case "Guard":
                    count = map.get("4_Camera");
                    map.put("4_Camera", ++count);
                    break;
                case "Curtain":
                    count = map.get("5_Curtain");
                    map.put("5_Curtain", ++count);
                    break;
                case "Pannel":
                case "HGC":
                    count = map.get("6_Service");
                    map.put("6_Service", ++count);
                    break;
                case "DVD":
                case "IPTV":
                case "TV":
                case "Sound":
                case "Audio":
                case "Projector":

                    count = map.get("7_Vedio");
                    map.put("7_Vedio", ++count);
                    break;
                case "Ch4CO":
                case "O2CO2":
                case "Env":
                case "Smoke":
                case "LaserEgg":
                case "AirSystem":
                    count = map.get("8_Gas");
                    map.put("8_Gas", ++count);
                    break;

                case "Exist":
                case "Fall":
                case "Water":
                case "Sov":
                case "SOS":
                case "Gsm":
                case "CurtainSensor":
                case "AcoustoOpticAlarm":
                    count = map.get("9_Env");
                    map.put("9_Env", ++count);
                    break;

                case "Lock":
                    count = map.get("91_SOS");
                    map.put("91_SOS", ++count);
                    break;

                case "TableWaterFilter"://台上净化器
                case "FloorWaterFilter"://台下净水器
                    count = map.get("97_Water");
                    map.put("97_Water", ++count);
                    break;
                case "AirFilter"://空气净化器
                    count = map.get("98_AirFilter");
                    map.put("98_AirFilter", ++count);
                    break;
            }
        }

        //将统计数据进行转换
        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : map.keySet()) {
            Integer count = map.get(key);
            if (count == 0) {
                continue;
            }
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("key", key);
            itemMap.put("image", Constant.getControlImageId(key));
            itemMap.put("name", Constant.getControlTypeName(key));
            itemMap.put("count", String.valueOf(count));

            list.add(itemMap);
        }
        //根据key重新排序
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> object1, Map<String, Object> object2) {
                return ((String) object1.get("key")).compareTo((String) object2.get("key"));
            }
        });
        return list;
    }

    /**
     * 设备类型的合并处理
     * key
     * image
     * name"
     * child
     *
     * @param relateList
     */
    public static List<Map<String, Object>> combinDeviceList2(List<DeviceRelate> relateList) {
        if (relateList == null)
            return Collections.emptyList();
        //统计每种设备数量
        Map<String, List<DeviceRelate>> map = new HashMap<>();
        map.put("98_AirFilter", new ArrayList<DeviceRelate>());     //空气净化
        map.put("97_Water", new ArrayList<DeviceRelate>());     //水质净化
        map.put("1_Light", new ArrayList<DeviceRelate>());    //照明控制
        map.put("5_Curtain", new ArrayList<DeviceRelate>());  //窗帘控制

        map.put("2_Socket", new ArrayList<DeviceRelate>());   //电源管理
        map.put("3_Air", new ArrayList<DeviceRelate>());      //空调控制
        map.put("4_Camera", new ArrayList<DeviceRelate>());   //监控控制

        map.put("6_Service", new ArrayList<DeviceRelate>());  //综合服务
        map.put("7_Vedio", new ArrayList<DeviceRelate>());    //影音控制
        map.put("8_Gas", new ArrayList<DeviceRelate>());      //气体检测
        map.put("9_Env", new ArrayList<DeviceRelate>());      //安全告警
        map.put("91_SOS", new ArrayList<DeviceRelate>());     //安全防护

//      //  map.put("92_Guard", new ArrayList<DeviceRelate>());   //门禁控制

        List<DeviceRelate> child = Collections.emptyList();
        //统计数据
        for (DeviceRelate deviceRelate : relateList) {
            String deviceType = deviceRelate.getDeviceProp().getType();
            switch (deviceType) {
                case "AirFilter": //空气净化器
                    child = map.get("98_AirFilter");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("98_AirFilter", child);
                    break;
                case "TableWaterFilter"://台上净化器
                case "FloorWaterFilter"://台下净水器
                    child = map.get("97_Water");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("97_Water", child);
                    break;
                case "Light1":
                case "Light2":
                case "Light3":
                case "Light4":
                case "LightAdjust":
                case "CircadianLight":
                    child = map.get("1_Light");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("1_Light", child);
                    break;
                case "Socket":
                    child = map.get("2_Socket");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("2_Socket", child);
                    break;
                case "AirCondition":
                case "CAC":
                    child = map.get("3_Air");
                    if (child == null) child = new ArrayList<>();

                    child.add(deviceRelate);
                    map.put("3_Air", child);
                    break;
                case "Camera":
                case "Guard":
                case "Doorbell":
                    child = map.get("4_Camera");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("4_Camera", child);
                    break;
                case "Curtain":
                    child = map.get("5_Curtain");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("5_Curtain", child);
                    break;
                case "Pannel":
                case "HGC":
                    child = map.get("6_Service");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("6_Service", child);
                    break;
                case "DVD":
                case "IPTV":
                case "TV":
                case "Sound":
                case "Audio":
                case "Projector":

                    child = map.get("7_Vedio");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("7_Vedio", child);
                    break;
                case "Ch4CO":
                case "O2CO2":
                case "Env":
                case "Smoke":
                case "LaserEgg":
                case "AirSystem":
                    child = map.get("8_Gas");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("8_Gas", child);
                    break;

                case "Exist":
                case "Fall":
                case "Water":
                case "Sov":
                case "SOS":
                case "Gsm":
                case "CurtainSensor":
                case "AcoustoOpticAlarm":
                    child = map.get("9_Env");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("9_Env", child);
                    break;

                case "Lock":
                    child = map.get("91_SOS");
                    if (child == null) child = new ArrayList<>();
                    child.add(deviceRelate);
                    map.put("91_SOS", child);
                    break;

//                case "FloorHeating":
//                    count = map.get("94_Floor");
//                    map.put("94_Floor", ++count);
//                    break;
//
            }
        }


        //将统计数据进行转换
        List<Map<String, Object>> resultList = new ArrayList<>();
        String[] keys = {"98_AirFilter", "97_Water", "1_Light", "5_Curtain"};

        for (String key : keys) {
            child = map.get(key);
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("key", key);
            itemMap.put("image", Constant.getControlImageId(key));
            itemMap.put("name", Constant.getControlTypeName(key));
            itemMap.put("child", child);
            resultList.add(itemMap);
        }


        List<Map<String, Object>> list = new ArrayList<>();
        out:
        for (String key : map.keySet()) {
            for (String tempKey : keys) {
                if (tempKey.equals(key)) {
                    continue out;
                }
            }
            child = map.get(key);

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("key", key);
            itemMap.put("image", Constant.getControlImageId(key));
            itemMap.put("name", Constant.getControlTypeName(key));
            itemMap.put("child", child);

            list.add(itemMap);
        }
        //根据key重新排序
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> object1, Map<String, Object> object2) {
                return ((String) object1.get("key")).compareTo((String) object2.get("key"));
            }
        });
        resultList.addAll(list);
        return resultList;
    }


    /**
     * 将设备状态转换成列表
     *
     * @param device
     * @return
     */
    public static List<Map<String, String>> convertDeviceStatusToList(ModeDevice device) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map;
        switch (device.getDevicetype()) {
            case "Light1":
                map = new HashMap<>();
                LightName lightName = device.getLightName();
                String name = "灯1";
                if (lightName != null && lightName.getName1() != null) {
                    name = lightName.getName1();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState());
                list.add(map);
                break;
            case "Light2":
                map = new HashMap<>();
                lightName = device.getLightName();
                name = "灯1";
                if (lightName != null && lightName.getName1() != null) {
                    name = lightName.getName1();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState());
                list.add(map);

                map = new HashMap<>();
                name = "灯2";
                if (lightName != null && lightName.getName2() != null) {
                    name = lightName.getName2();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState2());
                list.add(map);
                break;
            case "Light3":
                map = new HashMap<>();
                lightName = device.getLightName();
                name = "灯1";
                if (lightName != null && lightName.getName1() != null) {
                    name = lightName.getName1();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState());
                list.add(map);

                map = new HashMap<>();
                name = "灯2";
                if (lightName != null && lightName.getName2() != null) {
                    name = lightName.getName2();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState2());
                list.add(map);

                map = new HashMap<>();
                name = "灯3";
                if (lightName != null && lightName.getName3() != null) {
                    name = lightName.getName3();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState3());
                list.add(map);
                break;
            case "Light4":
                map = new HashMap<>();
                lightName = device.getLightName();
                name = "灯1";
                if (lightName != null && lightName.getName1() != null) {
                    name = lightName.getName1();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState());
                list.add(map);

                map = new HashMap<>();
                name = "灯2";
                if (lightName != null && lightName.getName2() != null) {
                    name = lightName.getName2();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState2());
                list.add(map);

                map = new HashMap<>();
                name = "灯3";
                if (lightName != null && lightName.getName3() != null) {
                    name = lightName.getName3();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState3());
                list.add(map);

                map = new HashMap<>();
                name = "灯4";
                if (lightName != null && lightName.getName4() != null) {
                    name = lightName.getName4();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState4());
                list.add(map);
                break;
            case "LightAdjust":
                map = new HashMap<>();
                lightName = device.getLightName();
                name = "调光灯";
                if (lightName != null && lightName.getName1() != null) {
                    name = lightName.getName1();
                }
                map.put("name", name);
                map.put("status", device.getParams().getState());
                map.put("coeff", device.getParams().getCoeff());
                list.add(map);
                break;
            case "Exist":
                String status = "0";
                if (device.getParams() != null && device.getParams().getSet() != null) {
                    status = device.getParams().getSet();
                }
                map = new HashMap<>();
                map.put("name", "");
                map.put("status", status);
                list.add(map);
                break;
            default:
                status = "0";
                if (device.getParams() != null && !TextUtils.isEmpty(device.getParams().getState())) {
                    status = device.getParams().getState();
                }
                map = new HashMap<>();
                map.put("name", "");
                map.put("status", status);

                if (device.getParams() != null && TextUtils.isEmpty(device.getParams().getSet())) {
                    map.put("set", device.getParams().getSet());
                }
                list.add(map);
                break;
        }
        return list;
    }

    /**
     * 将设备列表中的状态值进行modeDevice中的params转换
     *
     * @param deviceList
     */
    public static List<ModeDevice> deviceStatusHandlerWithModeDeviceList(List<ModeDevice> modeDeviceList, List<Map<String, Object>> deviceList) {
        for (ModeDevice modeDevice : modeDeviceList) {
            ControlDeviceValue cdv = new ControlDeviceValue();
            for (Map<String, Object> map : deviceList) {
                ModeDevice device = (ModeDevice) map.get("device");
                List<Map<String, String>> list = (List<Map<String, String>>) map.get("list");
                //判断是否为同一设备
                if (modeDevice.getDeviceAddr().equals(device.getDeviceAddr())) {
                    switch (modeDevice.getDevicetype()) {
                        case "Exist":
                            cdv.setSet(list.get(0).get("status"));
                            break;
                        case "Curtain":
                        case "Projector":
                            String status = list.get(0).get("status");
                            if (status.equals("0")) {
                                cdv.setState("2");
                            } else {
                                cdv.setState("1");
                            }
                            break;
                        //空调,电视
                        case "AirCondition":
                        case "TV":
                            status = list.get(0).get("status");
                            if (status.equals("0")) {
                                cdv.setState("2");
                            } else {
                                cdv.setState("1");
                            }
                            //控制信息
                            RemoteCMatchData remoteInfo = getRemoteInfo(device);
                            if (remoteInfo != null) {
                                cdv.setValue(remoteInfo);
                            }
                            break;
                        default:
                            switch (list.size()) {
                                case 1:
                                    cdv.setState(list.get(0).get("status"));
                                    break;
                                case 2:
                                    cdv.setState(list.get(0).get("status"));
                                    cdv.setState2(list.get(1).get("status"));
                                    break;
                                case 3:
                                    cdv.setState(list.get(0).get("status"));
                                    cdv.setState2(list.get(1).get("status"));
                                    cdv.setState3(list.get(2).get("status"));
                                    break;
                                case 4:
                                    cdv.setState(list.get(0).get("status"));
                                    cdv.setState2(list.get(1).get("status"));
                                    cdv.setState3(list.get(2).get("status"));
                                    cdv.setState3(list.get(3).get("status"));
                                    break;
                            }
                            break;
                    }
                }
            }
            modeDevice.setParams(cdv);
        }
        return modeDeviceList;
    }

    /**
     * 获取红外设备的控制信息
     *
     * @param device
     * @return
     */
    private static RemoteCMatchData getRemoteInfo(ModeDevice device) {
        DeviceRelate relate = getDeviceRelate(device);
        if (relate != null) {
            return relate.getDeviceProp().getRemoteInfo();
        }
        return null;
    }

    /**
     * 将device列表转换成modeDevice列表
     *
     * @param list
     * @param modeDeviceOld
     * @return
     */
    public static List<ModeDevice> modeDeviceListFromDeviceList(List<Device> list, List<ModeDevice> modeDeviceOld) {
        List<ModeDevice> modeDeviceList = new ArrayList<>();
        for (Device device : list) {
            if (modeDeviceOld != null) {
                int size = modeDeviceList.size();
                for (ModeDevice modeDevice : modeDeviceOld) {
                    if (modeDevice.getDeviceAddr().equals(device.getAddr())
                            && modeDevice.getDevicetype().equals(device.getType())) {
                        modeDeviceList.add(modeDevice);
                        break;
                    }
                }
                if (size + 1 == modeDeviceList.size()) {
                    continue;
                }
            }

            ModeDevice modeDevice = new ModeDevice();
            modeDevice.setAreaid(device.getAreaId());
            modeDevice.setAreaname(device.getAreaname());
            modeDevice.setRoomId(device.getRoomId());
            modeDevice.setRoomname(device.getRoomname());
            modeDevice.setDeviceAddr(device.getAddr());
            modeDevice.setDevicetype(device.getType());
            modeDevice.setDevicename(device.getName());
            modeDevice.setBrand(device.getBrand());
            modeDevice.setDeviceid(device.getId());

            modeDevice.setParams(new ControlDeviceValue());
            switch (device.getType()) {
                case "Light1":
                    ControlDeviceValue value = modeDevice.getParams();
                    value.setState("0");
                    modeDevice.setParams(value);
                    break;
                case "Light2":
                    value = modeDevice.getParams();
                    value.setState("0");
                    value.setState2("0");
                    modeDevice.setParams(value);
                    break;
                case "Light3":
                    value = modeDevice.getParams();
                    value.setState("0");
                    value.setState2("0");
                    value.setState3("0");
                    modeDevice.setParams(value);
                    break;
                case "Light4":
                    value = modeDevice.getParams();
                    value.setState("0");
                    value.setState2("0");
                    value.setState3("0");
                    value.setState4("0");
                    modeDevice.setParams(value);
                    break;
                case "CircadianLight":
                    value = modeDevice.getParams();
                    List<Device.ButtonsBean> buttonsBeens = device.getButtons();
                    for (Device.ButtonsBean bean : buttonsBeens) {
                        if (bean.isActive()) {
                            value.setButtonName(bean.getName());
                            value.setState("0");
                        }
                    }
                    value.setName(device.getName());
                    value.setKeypadName(device.getSerialNumber());
                    value.setN4SerialNo(device.getN4SerialNo());
                    modeDevice.setParams(value);
                    break;
//                case ConstantDeviceType.AUDIO:
//                case ConstantDeviceType.GSM:
//                case ConstantDeviceType.CURTAIN_CONTROL:
//                    break;
                default:
                    value = modeDevice.getParams();
                    value.setState("0");
                    value.setBrand(device.getBrand());
                    modeDevice.setParams(value);
                    break;
            }
            modeDeviceList.add(modeDevice);
        }
        return modeDeviceList;
    }

    /**
     * 将device列表转换成modeDevice列表
     *
     * @param list
     * @return
     */
    public static List<ModeDevice> modeDeviceListFromDeviceList(List<Device> list) {
//        List<ModeDevice> modeDeviceList = new ArrayList<>();
//        for (Device device : list) {
//            ModeDevice modeDevice = new ModeDevice();
//            modeDevice.setAreaid(device.getAreaId());
//            modeDevice.setAreaname(device.getAreaname());
//            modeDevice.setRoomId(device.getRoomId());
//            modeDevice.setRoomname(device.getRoomname());
//            modeDevice.setDeviceAddr(device.getAddr());
//            modeDevice.setDevicetype(device.getType());
//            modeDevice.setDevicename(device.getName());
//            modeDevice.setBrand(device.getBrand());
//
//            modeDevice.setParams(new ControlDeviceValue());
//            switch (device.getType()) {
//                case "Light1":
//                    ControlDeviceValue value = modeDevice.getParams();
//                    value.setState("0");
//                    modeDevice.setParams(value);
//                    break;
//                case "Light2":
//                    value = modeDevice.getParams();
//                    value.setState("0");
//                    value.setState2("0");
//                    modeDevice.setParams(value);
//                    break;
//                case "Light3":
//                    value = modeDevice.getParams();
//                    value.setState("0");
//                    value.setState2("0");
//                    value.setState3("0");
//                    modeDevice.setParams(value);
//                    break;
//                case "Light4":
//                    value = modeDevice.getParams();
//                    value.setState("0");
//                    value.setState2("0");
//                    value.setState3("0");
//                    value.setState4("0");
//                    modeDevice.setParams(value);
//                    break;
//                case "CircadianLight":
//                    value = modeDevice.getParams();
//                    List<Device.ButtonsBean> buttonsBeens = device.getButtons();
//                    for (Device.ButtonsBean bean : buttonsBeens) {
//                        if (bean.isActive()) {
//                            value.setButtonName(bean.getName());
//                            value.setState("0");
//                        }
//                    }
//                    modeDevice.setParams(value);
//                    break;
////                case ConstantDeviceType.AUDIO:
////                case ConstantDeviceType.GSM:
////                case ConstantDeviceType.CURTAIN_CONTROL:
////                    break;
//                default:
//                    value = modeDevice.getParams();
//                    value.setState("0");
//                    value.setBrand(device.getBrand());
//                    modeDevice.setParams(value);
//                    break;
//            }
//            modeDeviceList.add(modeDevice);
//        }
        return modeDeviceListFromDeviceList(list, null);
    }

    /**
     * 将DeviceRelate列表转换成modeDevice列表
     *
     * @param list
     * @return
     */
    public static List<ModeDevice> modeDeviceListFromDeviceRelateList(List<DeviceRelate> list) {
        List<ModeDevice> modeDeviceList = new ArrayList<>();
        for (DeviceRelate deviceRelate : list) {

            Device device = deviceRelate.getDeviceProp();
            DeviceStatusValue statusValue = deviceRelate.getDeviceStatus().getValue();

            ModeDevice modeDevice = new ModeDevice();
            modeDevice.setAreaid(device.getAreaId());
            modeDevice.setAreaname(device.getAreaname());
            modeDevice.setRoomId(device.getRoomId());
            modeDevice.setRoomname(device.getRoomname());
            modeDevice.setDeviceAddr(device.getAddr());
            modeDevice.setDevicetype(device.getType());
            modeDevice.setDevicename(device.getName());
            modeDevice.setBrand(device.getBrand());

            switch (device.getType()) {
                case "Light1":
                case "Socket":
                    ControlDeviceValue value = new ControlDeviceValue();
                    if (statusValue != null) {
                        value.setState(statusValue.getState());
                    } else {
                        value.setState("0");
                    }
                    modeDevice.setParams(value);
                    break;
                case "Light2":
                    value = new ControlDeviceValue();
                    if (statusValue != null) {
                        value.setState(statusValue.getState());
                        value.setState2(statusValue.getState2());
                    } else {
                        value.setState("0");
                        value.setState2("0");
                    }

                    modeDevice.setParams(value);
                    break;
                case "Light3":
                    value = new ControlDeviceValue();
                    if (statusValue != null) {
                        value.setState(statusValue.getState());
                        value.setState2(statusValue.getState2());
                        value.setState3(statusValue.getState3());
                    } else {
                        value.setState("0");
                        value.setState2("0");
                        value.setState3("0");
                    }

                    modeDevice.setParams(value);
                    break;
                case "Light4":
                    value = new ControlDeviceValue();
                    if (statusValue != null) {
                        value.setState(statusValue.getState());
                        value.setState2(statusValue.getState2());
                        value.setState3(statusValue.getState3());
                        value.setState4(statusValue.getState4());
                    } else {
                        value.setState("0");
                        value.setState2("0");
                        value.setState3("0");
                        value.setState4("0");
                    }
                    modeDevice.setParams(value);
                    break;
                case "LightAdjust":
                    value = new ControlDeviceValue();
                    if (statusValue != null) {
                        value.setState(statusValue.getState());
                        value.setCoeff(statusValue.getCoeff());
                        value.setLightingTime(value.getLightingTime());
                    } else {
                        value.setState("0");
                        value.setCoeff("0");
                    }
                    modeDevice.setParams(value);
                    break;
                case "CircadianLight":
                    value = new ControlDeviceValue();
                    if (statusValue != null) {
                        value.setState(statusValue.getState());
                    } else {
                        value.setState("0");
                    }
                    value.setButtonName("");
                    value.setKeypadName(device.getX2Name());
                    value.setN4SerialNo(device.getN4SerialNo());
                    modeDevice.setParams(value);
                    break;
                default:
                    value = new ControlDeviceValue();
                    if (statusValue != null) {
                        value.setState(statusValue.getState());
                        value.setBrand(device.getBrand());
                        if (statusValue.getSet() != null) {
                            value.setSet(statusValue.getSet().intValue() + "");
                        }

                    } else {
                        value.setState("0");
                    }
                    modeDevice.setParams(value);
            }
            modeDeviceList.add(modeDevice);
        }
        return modeDeviceList;
    }

    /**
     * 设备列表转换
     *
     * @param modeDevices
     * @return
     */
    public static List<Map<String, Object>> convertModeDeviceToList(List<ModeDevice> modeDevices) {
        List<Map<String, Object>> arrayList = new ArrayList<>();
        for (ModeDevice device : modeDevices) {
            Map<String, Object> map = new HashMap<>();
            map.put("device", device);

            map.put("list", Constant.convertDeviceStatusToList(device));

            arrayList.add(map);
        }
        return arrayList;
    }

    //房间模式

    /**
     * 返回房间模式背影图
     *
     * @param serialNo
     * @return
     */
    public static int roomModeBg(String serialNo) {
        int[] bgs = new int[]{R.drawable.ic_mode_bg_red, R.drawable.ic_mode_bg_green,
                R.drawable.ic_mode_bg_orange, R.drawable.ic_mode_bg_blue};
        int serial = Integer.valueOf(serialNo);
        if (serial < 0) {
            serial = 0;
        }
        return bgs[serial % bgs.length];
    }


    /**
     * 返回房间模式图片
     *
     * @param serialNo
     * @return
     */
    public static int roomModeImage(String serialNo) {
        int[] images = new int[]{R.drawable.ic_mode_clock_white, R.drawable.ic_mode_sleep_white, R.drawable.ic_mode_read_white,
                R.drawable.ic_mode_tv_white};
        int serial = Integer.valueOf(serialNo);
        if (serial < 0) {
            serial = 0;
        }
        return images[serial % images.length];
    }

    /**
     * 房间模式转换成Map列表
     *
     * @param modeActList
     * @return
     */
    public static List<Map<String, Object>> modeActionListToMapList(List<ModeAct> modeActList) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (ModeAct mode : modeActList) {
            Map<String, Object> map = new HashMap<>();
            map.put("bg", Constant.roomModeBg(mode.getSerialNo()));
//            map.put("image", Constant.roomModeImage(mode.getSerialNo()));
            if(mode.getName()==null){
                map.put("image", R.mipmap.scene_home);
            }
            else
            if (mode.getName().contains("晨起")) {
                map.put("image", R.mipmap.ic_room_morning);
            } else if (mode.getName().contains("睡眠")) {
                map.put("image", R.mipmap.ic_room_seelp);
            } else if (mode.getName().contains("阅读")) {
                map.put("image", R.mipmap.ic_room_read);
            } else if (mode.getName().contains("娱乐")) {
                map.put("image", R.mipmap.ic_room_play);
            } else {
                map.put("image", R.mipmap.scene_home);
            }
            String name = mode.getTag();
            map.put("text", name);
            list.add(map);
        }
        return list;
    }

    //消息中心

    /**
     * 获取申请状态
     *
     * @param status 状态值：0：待确认，1：用户同意，2：用户拒绝，3：用户取消，4：管理员同意，5:管理员拒绝，6：管理员取消
     * @return
     */
    public static String getApplyStatus(int status) {
        switch (status) {
            case 1:
                return "已加入";
            case 4:
                return "已通过";
            case 2:
            case 5:
                return "已拒绝";
            case 3:
            case 6:
                return "已取消";
            case 0:
                return "待确认";
        }
        return "";
    }

    /**
     * 根据pm25值获取空气质量
     *
     * @param pm25
     * @return
     */
    public static String getAirQuality(int pm25) {
        if (pm25 > 0 && pm25 < 35) {
            return "优";
        } else if (pm25 >= 35 && pm25 < 75) {
            return "良";
        } else if (pm25 >= 75 && pm25 < 115) {
            return "轻度污染";
        } else if (pm25 >= 115 && pm25 < 150) {
            return "中度污染";
        } else if (pm25 >= 150 && pm25 < 250) {
            return "重度污染";
        } else if (pm25 >= 250) {
            return "严重污染";
        }
        return "优";
    }

    /**
     * 用户名掩码处理
     *
     * @param name
     * @return
     */
    public static String userNameMask(String name) {
        if (StringUtil.isEmpty(name)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if (name.length() > 1) {
            sb.append(name.substring(0, 1));
            for (int i = 0; i < name.length() - 1; i++) {
                sb.append("*");
            }
        }
        return sb.toString();
    }

    //家人管理

    /**
     * 判断当前用户是否是当前主机管理员
     *
     * @param host
     * @return
     */
    public static boolean userIsAdmin(Host host) {
        for (Family family : host.getFamilies()) {
            //判断当前用户是否该主机管理员
            if (1 == family.getAdmin()
                    && Constant.USERID.equals(family.getUserId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 星座图片
     *
     * @param name
     * @return
     */
    public static int constellationImageId(String name) {
        if (name == null) {
            name = "";
        }
        switch (name) {
            case "白羊座":
                return R.drawable.ic_3;
            case "金牛座":
                return R.drawable.ic_4;
            case "双子座":
                return R.drawable.ic_5;
            case "巨蟹座":
                return R.drawable.ic_6;
            case "狮子座":
                return R.drawable.ic_7;
            case "处女座":
                return R.drawable.ic_8;
            case "天秤座":
                return R.drawable.ic_9;
            case "天蝎座":
                return R.drawable.ic_10;
            case "射手座":
                return R.drawable.ic_11;
            case "摩羯座":
                return R.drawable.ic_12;
            case "水瓶座":
                return R.drawable.ic_1;
            case "双鱼座":
                return R.drawable.ic_2;
            default:
                return R.drawable.ic_1;
        }
    }


    /**
     * 性别图片
     *
     * @param sex
     * @return
     */
    public static int sexImageId(String sex) {
        if (sex == null) {
            sex = "";
        }
        if (sex.equals("0")) {
            return R.drawable.ic_personal_data_male;
        } else if (sex.equals("1")) {
            return R.drawable.ic_personal_data_female_check;
        } else {
            return R.drawable.ic_personal_data_male;
        }

    }

    /**
     * 获取指定设备关联信息
     *
     * @param device
     * @return
     */
    public static DeviceRelate getDeviceRelate(Device device) {
        for (DeviceRelate relate : Constant.DEVICE_RELATE) {
            Device tempDevice = relate.getDeviceProp();
            if (tempDevice.getAddr().equals(device.getAddr())) {
                return relate;
            }
        }
        return null;
    }

    /**
     * 获取指定设备关联信息
     *
     * @param device
     * @return
     */
    public static DeviceRelate getDeviceRelate(ModeDevice device) {
        for (DeviceRelate relate : Constant.DEVICE_RELATE) {
            Device tempDevice = relate.getDeviceProp();
            if (tempDevice.getAddr().equals(device.getDeviceAddr())) {
                return relate;
            }
        }
        return null;
    }

    //灯光关联设备

    /**
     * 获取灯光关联设备信息
     *
     * @param deviceType
     * @return
     */
    public static List<Map<String, Object>> lightDeviceRelate(String deviceType) {
        List<Map<String, Object>> list = new ArrayList<>();
        switch (deviceType) {
            case "Light1":
            case "LightAdjust":
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", "灯1");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                break;
            case "Light2":
                map = new HashMap<>();
                map.put("name", "灯1");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                map = new HashMap<>();
                map.put("name", "灯2");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                break;
            case "Light3":
                map = new HashMap<>();
                map.put("name", "灯1");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                map = new HashMap<>();
                map.put("name", "灯2");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                map = new HashMap<>();
                map.put("name", "灯3");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                break;
            case "Light4":
                map = new HashMap<>();
                map.put("name", "灯1");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                map = new HashMap<>();
                map.put("name", "灯2");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                map = new HashMap<>();
                map.put("name", "灯3");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                map = new HashMap<>();
                map.put("name", "灯4");
                map.put("isOpen", false);
                map.put("isSelected", false);
                list.add(map);
                break;
        }
        return list;
    }


    /**
     * 取得时间设置
     *
     * @param timeTask
     * @return
     */
    public static String getTimeSetting(TimeTask timeTask) {
        if (timeTask != null && timeTask.getType() != null) {
            if ("delay".equals(timeTask.getType())) {
                if (timeTask.getTriggerTime() == null) return null;
                return timeTask.getTriggerTime();
            } else {
                List<Map<String, String>> list = timeTask.getRepeat();
                if (list.size() == 0) return null;
                Map<String, String> map = list.get(0);
                String[] keys = new String[map.keySet().size()];
                map.keySet().toArray(keys);
                return map.get(keys[0]);
            }
        }
        return "";
    }

    /**
     * 获取设置定时字符串
     *
     * @return
     */
    public static String getTimerWeekSetting(TimeTask timeTask) {
        if (timeTask != null && timeTask.getRepeat() != null) {
            List<Map<String, String>> list = timeTask.getRepeat();
            if (list.size() == 0) return "";
            StringBuffer sb = new StringBuffer();
            if (list.size() > 0) {
                sb.append(BaseApplication.getAppContext().getString(R.string.text_week));
            }
            for (Map<String, String> map : list) {
                for (int i = 0; i < 7; i++) {
                    String value = map.get(String.valueOf(i));
                    if (value != null) {
                        sb.append(String.format("%s|", getWeekString(String.valueOf(i))));
                    }
                }
            }
            return sb.substring(0, sb.length() - 1);
        }
        return "";
    }


    /**
     * 获取星期字符串
     *
     * @param num
     * @return
     */
    public static String getWeekString(String num) {
        switch (num) {
            case "0":
                return "一";
            case "1":
                return "二";
            case "2":
                return "三";
            case "3":
                return "四";
            case "4":
                return "五";
            case "5":
                return "六";
            case "6":
                return "日";
        }
        return null;
    }

    //中控面板界面，根据模式名称处理对应的图片,返回一组图标，因为界面显示需要根据不同条件切换图片
    public static int[] getModeImages(String modeName) {
        int[] arrays = new int[3];
        if ("布防".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_bufang;
            arrays[1] = R.drawable.control_canclick_bufang;
            arrays[2] = R.drawable.control_click_bufang;
        } else if ("插座".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_chazuo;
            arrays[1] = R.drawable.control_canclick_chazuo;
            arrays[2] = R.drawable.control_click_chazuo;
        } else if ("撤防".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_chefang;
            arrays[1] = R.drawable.control_canclick_chefang;
            arrays[2] = R.drawable.control_click_chefang;
        } else if ("晨起".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_chenqi;
            arrays[1] = R.drawable.control_canclick_chenqi;
            arrays[2] = R.drawable.control_click_chenqi;
        } else if ("窗帘".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_chuanglian;
            arrays[1] = R.drawable.control_canclick_chuanglian;
            arrays[2] = R.drawable.control_click_chuanglian;
        } else if ("灯".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_dengguang;
            arrays[1] = R.drawable.control_canclick_dengguang;
            arrays[2] = R.drawable.control_click_dengguang;
        } else if ("回家".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_huijia;
            arrays[1] = R.drawable.control_canclick_huijia;
            arrays[2] = R.drawable.control_click_huijia;
        } else if ("会客".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_huike;
            arrays[1] = R.drawable.control_canclick_huike;
            arrays[2] = R.drawable.control_click_huike;
        } else if ("就餐".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_jiucan;
            arrays[1] = R.drawable.control_canclick_jiucan;
            arrays[2] = R.drawable.control_click_jiucan;
        } else if ("空调".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_kongtiao;
            arrays[1] = R.drawable.control_canclick_kongtiao;
            arrays[2] = R.drawable.control_click_kongtiao;
        } else if ("离家".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_lijia;
            arrays[1] = R.drawable.control_canclick_lijia;
            arrays[2] = R.drawable.control_click_lijia;
        } else if ("三联灯".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_sanliandeng;
            arrays[1] = R.drawable.control_canclick_sanliandeng;
            arrays[2] = R.drawable.control_click_sanliandeng;
        } else if ("睡觉".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_shuijiao;
            arrays[1] = R.drawable.control_canclick_shuijiao;
            arrays[2] = R.drawable.control_click_shuijiao;
        } else if ("娱乐".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_yule;
            arrays[1] = R.drawable.control_canclick_yule;
            arrays[2] = R.drawable.control_click_yule;
        } else if ("阅读".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_yuedu;
            arrays[1] = R.drawable.control_canclick_yuedu;
            arrays[2] = R.drawable.control_click_yuedu;
        } else if ("中央空调".equals(modeName)) {
            arrays[0] = R.drawable.control_nomal_zykongtiao;
            arrays[1] = R.drawable.control_canclick_zykongtiao;
            arrays[2] = R.drawable.control_click_zykongtiao;
        } else {//默认图标
            arrays[0] = R.drawable.control_nomal_bufang;
            arrays[1] = R.drawable.control_canclick_bufang;
            arrays[2] = R.drawable.control_click_bufang;
        }
        return arrays;
    }


    /**
     * 根据房间Id 查找房间名，由于更新房间名设备的房间属性不能更新
     *
     * @param roomId
     * @return
     */
    public static String getRoomByRoomId(String roomId) {
        String roomName = "";
        if (StringUtil.isEmpty(roomId)) {
            return roomName;
        }
        List<Room> roomList = Constant.GATEWAY.getRoom();
        for (Room room : roomList) {
            if (roomId.equals(room.getRoomId())) {
                roomName = room.getName();
                return roomName;
            }
        }
        return roomName;
    }

    public static AddDevice getDeviceNonCircleResWithType(String type) {
        for (AddDevice addDevice : Constant.blueDeviceList()) {
            if (addDevice.getType().equals(type)) {
                return addDevice;
            }
        }
        AddDevice addDevice = new AddDevice();
        addDevice.setResId(R.mipmap.ic_launcher);
        addDevice.setItemText(R.string.add);
        addDevice.setType("");
        return addDevice;
    }

    //首页默认的三个布局显示顺序
    //public static List<String> getFragmentList = new ArrayList<String>();


    public static String WIFI_AIR_CLEAN_UID;
    public static ThirdUser THIRDUSER;
}