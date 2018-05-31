package com.boer.delos.request;

/**
 * author: sunzhibin
 * E-mail:
 * Description:
 * CreateDate: 2017/1/20 0020 08:36
 * Modify:
 * ModifyDate:
 */


public interface Api {
    /**{"response": {"zbsVer": 0, "hostId": "001207C40173", "room": [{"type": "\u603b\u7ecf\u7406", "roomId": "322", "name": "\u603b\u7ecf\u7406"}, {"roomId": "350", "type": "\u4e3b\u5367", "name": "\u4e3b\u5367"}, {"type": "\u53a8\u623f", "roomId": "366", "name": "\u5475\u5475"}, {"roomId": "381", "type": "\u53a8\u623f", "name": "\u53a8\u623f"}, {"roomId": "382", "type": "\u9910\u5385", "name": "\u9910\u5385"}], "softver": 104, "timestamp": 1501838857, "zbhVer": 0, "lastbackup": 1499826785, "pandId": 101, "firmver": 217, "numbers": "15905214137,15252089063,18912345678,15263985632,15152208332", "channelNo": "f", "registerHost": false, "name": "40173"}, "ret": 0, "md5": "a92c08fdf3a4b4f9aef04b3ce102df32"}

     * 云端接口
     */
    String wrapper_request = "/wrapper/request";
    //
//  CloudInterface.h
//  ihome
//
//  Created by  on 3/13/15.
//  Copyright (c) 2015  Co.ltd. All rights reserved.

    //测试
    String urlCloud = "/https://api2.boericloud.com";
    //正式
//     String urlCloud = "/https://api.boericloud.com";

    String urlResetMobile = "/auth/resetMobile";       //重置手机号码
    String urlNegotiate = "/auth/negotiate";
    String urlRegister = "/auth/register";            //注册
    String urlSignin = "/auth/login";               //登录
    String urlSignOff = "/auth/logout";              //登出

    String urlResetCloudPassword = "/auth/resetCloudpassword";  //重置密码
    String urlSmsVerify = "/auth/sms_verify";          //短信验证
    String urlResetPWD = "/auth/reset_password";      //忘记密码
    String urlMobileVerify = "/auth/mobile_verify";       //验证手机号

    String urlUserUpdate = "/user/update";
    String urlUserUpdateToken = "/user/update_token";

    String urlUserPermissions = "/user/host_permissions";    //查询用户权限
    String urlUserUpdateExtend = "/user/update_extend"; //更新铃声和震动
    String urlUserShowExtend = "/user/show_extend";  //获取铃声和震动

    String urlHostBind = "/host/bind";
    String urlHostShow = "/host/show";

    String urlHostverifyadminpassword = "/host/verifyadminpassword";
    String urlHostSwitch = "/host/switch";

    String urlWrapperRequest = "/wrapper/request";//透传接口，一键备份
    String urlHostRestoreproperty = "/host/restoreproperty";//透传接口，一键还原

    String urlHostUpgrade = "/upgrade/latest";
    String urlHostUpgradeSoftware = "/host/software_upgrade";

    String urlSystemMessageShow = "/systemMessage/show"; //请求某天某类型的系统消息

    String urlWarningShow = "/alarm/show1"; //请求某天某类型的历史告警
    String urlAlarmDelete = "/alarm/delete1";//删除单条或多条历史告警
    String urlAlarmBatchDelete = "/alarm/batch_delete";//删除某天某类型的所有告警

    String urlSystemMessageDelete = "/systemMessage/remove";//删除某天某类型的系统消息
    String urlSystemBatchDelete = "/systemMessage/batch_delete";//删除某天某类型的所有系统消息

    String urlAlarmRead = "/alarm/confirm_read";//确认某条系统告警
    String urlSystemMessageRead = "/systemMessage/confirmRead";//确认某条系统消息

    String urlAddBlackList = "/user/add_black_list";//加入黑名单
    String urlRemoveBlackList = "/user/remove_black_list";//移除黑名单
    String urlQueryBlackList = "/user/query_in_black_list";//查询用户是否在黑名单内

    /*****************************/
//主机直连 -> 设备相关
    String urlQueryWiseMediaList = "/device/queryWiseMediaList";//查询华尔斯背景音乐的歌曲列表
    String urlDeviceScan = "/device/scan";//扫描设备
    String urlDeviceLink = "/device/link";//关联设备->（新增）
    String urlDeviceQuerylink = "/device/querylink";//查询关联设备->（新增）
    String urlDeviceCmd = "/device/cmd";//控制设备
    String urlDeviceRemove = "/device/remove";//删除设备
    String urlDeviceDismiss = "/device/dismiss";//解绑设备
    String urlDeviceUpdateProp = "/device/updateprop";//更新设备属性
    String urlDevicesProperties = "/devices/properties";//查询设备属性
    String urlDeviceStatus = "/device/status";//查询设备状态
    String urlDeviceQueryOneProp = "/device/queryOneProp";//查询某一设备的属性->（新增）
    String urlDeviceConfigHgc = "/device/configHgc";//配置中控->（新增）
    String urlDeviceDeleteHgcConfig = "/device/deleteHgcConfig";//删除中控配置->（新增）
    String urlDeviceQueryHgcConfig = "/device/queryHgcConfig";//查询中控配置->（新增）
    String urlDeviceQueryMeterAddr = "/device/queryMeterAddr";//查询水电表的地址->（新增）
    String urlDeviceModifyMeterName = "/device/modifyMeterName";//修改水电表的名称->（新增）
    String urlDeviceQueryAllDevices = "/device/queryAllDevices";//查询所有设备的属性和状态->（新增）
    String urlDeviceSetFloorHeatingTimeTask = "/device/setFloorHeatingTimeTask";//设置地暖的定时任务->（新增）
    String urlDeviceSwitchFloorHeatingTimeTask = "/device/switchFloorHeatingTimeTask";//开启或者关闭地暖的定时任务->（新增）

    //主机直连 -> 主机相关
    String urlHostShowProperty = "/host/showproperty";//查询主机信息
    String urlHostModifyProperty = "/host/modifyproperty";//修改主机信息
    String urlHostQueryglobaldata = "/host/queryglobaldata";//查询全局信息->（新增）
    String urlHostModifyHostName = "/host/modifyHostName";//修改主机名称->（新增）

    //主机直连 -> 联动模式相关
    String urlPlanShow = "/plan/show";//查询指定的联动预案或模式
    String urlPlanUpdate = "/plan/update";//更新联动预案或模式
    String urlPlanQueryGlobalModes = "/plan/queryGlobalModes";//查询全局模式->（新增）
    String urlPlanModifyModeName = "/plan/modifyModeName";//修改模式名称->（新增）
    String urlPlanSetTimeTask = "/plan/setTimeTask";//设置模式定时
    String urlPlanTimeTaskSwitch = "/plan/switchTimeTask";//开启或关闭模式定时
    String urlPlanAllModes = "/plan/allModes";//查询当前主机下所有模式

    //主机直连 -> 房间区域相关
    String urlRoomsRemove = "/room/remove";//删除房间
    String urlRoomsUpdate = "/room/update";//更新房间
    String urlRoomsShow = "/room/show";//查询房间
    String urlAreaRemove = "/room/removearea";//删除区域
    String urlAreaUpdate = "/room/updatearea";//更新区域
    String urlAreaShow = "/room/showarea";//查询区域
    String urlRoomsUpdateMode = "/room/updatemode";//更新房间模式
    String urlRoomsShowMode = "/room/showmode";//查询房间模式
    String urlRoomsActiveMode = "/room/activemode";//激活房间模式

//主机直连 -> 告警相关


    //主机直连 -> 用户相关
    String urlUserLogin = "/user/login";//直连登录->（新增）
    String urlUserAuthorizedLogin = "/user/authorizedLogin";//授权后的直连登陆（已在云端登陆）->（新增）
    String urlUserLogout = "/user/logout";//退出登录->（新增）
    String urlUserSaveUserInfo = "/user/saveUserInfo";//直连登录->（新增）
    /*****************************/

    String urlReportBloodsugar = "/health/report_bloodsugar";//上报血糖值
    String urlDeleteBloodsugar = "/health/delete_bloodsugar";//删除血糖值
    String urlUpdateBloodsugar = "/health/update_bloodsugar";//修改血糖值

    String urlReportBloodPressure = "/health/report_bloodpressure";//上报血压值
    String urlDeleteBloodPressure = "/health/delete_bloodpressure";//删除血压值

    String urlReportBodyWeight = "/health/report_bodyweight";//上报体重值
    String urlDeleteBodyWeight = "/health/delete_bodyweight";//删除体重值

    String urlReportUrine = "/health/report_urine";  //上报尿检值
    String urlDeleteUrine = "/health/delete_urine";//删除某一条尿检记录

    String urlDownHealthCache = "/data/health_down";//下载缓存数据
    String urlUploadHealthCache = "/data/health_upload";//上传缓存数据
    String urlUploadBloodPressureCache = "/data/health_upload_blood_pressure";//上传血压缓存数据
    String urlUploadBloodGlucoseCache = "/data/health_upload_blood_glucose";//上传血糖缓存数据
    String urlUploadBodyWeightCache = "/data/health_upload_body_weight";//上传体重缓存数据
    String urlUploadUrineCache = "/data/health_upload_urine";//上传尿检缓存数据

    String urlQueryElec = "/energy/query_elec"; //查询电能数据
    String urlQuerySocket = "/energy/query_socket"; //查询插座数据
    String urlQueryWater = "/energy/query_water"; //查询水表数据
    String urlQueryGas = "/energy/query_gas"; //查询气表数据


    String urlHostShow1 = "/host/show1";                       //家庭管理中的主机属性

    String urlUserInfo = "/user/userInfo";                    //查找用户信息
    String urlShowInviteCode = "/user/show_invitecode";             //查看邀请码
    String urlInvitationConvert = "/integral/exchange_integral";       //兑换邀请码
    String urlFamilyAddUser = "/family/add_user";                  //增加家庭成员
    String urlFamilyTransPermission = "/family/admin_permission_transfer"; //转让管理员权限
    String urlFamilyUpdateAlias = "/family/update_alias";              //更新主机别名或用户别名
    String urlFamilyDeleteUser = "/family/delete_user";               //主机删除用户
    String urlFamilyDeleteHost = "/family/delete_host";               //管理员删除主机
    String urlFamilyUserIsAdmin = "/family/user_isAdmin";              //当前用户是否为管理员
    String urlFamilyUpdatePermissaion = "/family/update_permission";         //更新用户权限
    String urlFamilyUpdateShare = "/family/update_share";              //家庭分享健康数据
    String urlFamilyHostAdmin = "/family/host_admin";                //查询主机管理员

    String urlApplyUserApply = "/apply/user_apply";                 //主机用户申请

    String urlApplyUserApplyV1 = "/apply/user_apply_v1";              //主机用户申请接口V1（将判断和推送放在后台）
    String urlApplyUserShow = "/apply/user_show";                  //查询主机用户申请
    String urlApplyUserDelete = "/apply/user_delete";                //删除用户申请
    String urlApplyUserUpdateStatus = "/apply/user_update_state";          //更新用户状态

    String urlApplyUserUpdateStatusV1 = "/apply/update_status_v1";           //更新用户申请状态（将当前状态发送给后台进行筛选)
    String urlApplyUserHost = "/apply/host_show";                  //查询主机下申请用户
    String urlApplyUserApplyIsExists = "/apply/user_applyuserid_exist";     //判断当前用户是否已经申请过
    String urlApplyUserReapply = "/family/user_reapply";                                 //用户重新申请
    String urlApplyQueryUserApplyOrAdnimReject = "/apply/query_user_apply_reject";                           //查询用户申请或管理员拒绝记录

    String urlQueryUserApplyOrShare = "/apply/query_user_apply_share";    //查询用户是否有未处理申请或分享

    String urlNotificationPush = "/notification/notification_push";   //推送通知

    String notification_updateMsg = "/notification_updateMsg";          //报警通知
    String notification_updateScence = "/notification_updateScence";       //场景更新
    String notification_startHomeTimer = "/notification_startHomeTimer";     //开启主页轮询
    String notification_stopHomeTimer = "/notification_stopHomeTimer";      //关闭主页轮询
    String notification_startDeviceTimer = "/notification_startDeviceTimer";     //开启设备轮询
    String notification_stopDeviceTimer = "/notification_stopDeviceTimer";      //关闭设备轮询
    String notification_changeHost = "/notification_changeHost";         //切换主机
    String notification_updateCity = "/notification_updateCity";         //更新城市信息
    String notification_updateFamilyMembers = "/notification_updateFamilyMembers";         //更新家庭成员信息
    String notification_removeHomeNotifications = "/notification_removeHomeNotifications"; //移除所有通知
    String notification_familyRefresh = "/notification_familyRefresh";      //家庭成员刷新

    String urlShareUser = "/share/user_share";        //查询家庭的接口
    String urlQueryHealth = "/health/query_health";        //查询选定日期区间健康数据接口
    String urlQueryRecentHealth = "/health/query_recent_health";        //查询最近的健康数据分享接口

    String urlQueryRecentNotification = "/notification/query_recent_notification";        //查询最近的通知消息

    String urlFeedback = "/feedback/feedback_push";        //客户端的意见反馈

    String urlHostGuard = "/host/guard";//门禁对讲接听推送请求


    String urlStartScanBatch = "/device/startScanBatch";//开始批量添加
    String urlStopScanBatch = "/device/stopScanBatch";//停止批量添加

    String urlQueryDeviceBatch = "/device/queryDeviceBatch";//查询设备
    String urlSaveDeviceBatch = "/device/saveDeviceBatch";//保存

    String urlCommonDevice = "/device/commondevice";//设置、取消常用设备

    String urlHostShowOnline = "/host/showonline"; // 主机是否在线
    String urlgetMsgSettings = "/settings/find_message_settings_by_mobile";//获取消息设置
    String urlsetMsgSettings = "/settings/save_message_settings";//设置消息设置
    String urlsetPushMsg = "/notification/notification_push";//消息推送

    String urlQueryAirFilter = "/energy/query_airFilter";// 查询 历史记录
    String urlQueryTableWaterFilter = "/energy/query_tableWaterFilter";//
    String urlQueryFloorWaterFilter = "/energy/query_floorWaterFilter";//


}
