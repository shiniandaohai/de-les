package com.boer.delos.request.family;

import android.content.Context;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author PengJiYang
 * @Description: 家庭管理接口实现类
 * create at 2016/5/26 15:27
 */
public class FamilyManageController extends BaseController {

    public static FamilyManageController instance = null;

    public static final String applyStatusApply = "" + 1;   //用户申请
    public static final String applyStatusShare = "" + 2;   //管理员分享

    public static final String statusNormal = "" + 0;     //待确认
    public static final String statusUserApply = "" + 1;  //用户同意
    public static final String statusUserReject = "" + 2; //用户拒绝
    public static final String statusUserCancel = "" + 3; //用户取消
    public static final String statusAdminApply = "" + 4; //管理员同意
    public static final String statusAdminReject = "" + 5;//管理员拒绝
    public static final String statusAdminCancel = "" + 6;//管理员取消

    /** applyStatus: 1、用户申请 2、管理员分享
     // status : 0、待确认     1、用户同意   2、用户拒绝
     //          3、用户取消  　4、管理员同意 5、管理员拒绝
     //          6、管理员取消
     */
    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static FamilyManageController getInstance() {
        if (instance == null) {
            synchronized (FamilyManageController.class) {
                if (instance == null) {
                    instance = new FamilyManageController();
                }
            }
        }
        return instance;
    }

    /**
     * 显示当前用户下的所有主机和对应主机下的家庭组成员
     *
     * @param context  上下文
     * @param listener 回调接口
     */

    public void showFamilies(Context context, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();

        String url = URLConfig.HTTP + "/host/show1";
        postWithInternetRequest(context, url, map, RequestTag.SHOW_FAMILIES, listener);
    }

    /**
     * 更新健康数据分享
     *
     * @param context  上下文
     * @param hostId   主机Id
     * @param shareIds 被分享用户Id,以","分隔
     * @param listener 回调接口
     */

    public void updateShare(Context context, String hostId, String shareIds, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.USERID) || StringUtil.isEmpty(hostId)) {
            return;
        }
        String url = URLConfig.HTTP + "/family/update_share?uid=" + Constant.USERID;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("userId", Constant.USERID);
        maps.put("hostId", hostId);
        maps.put("shareIds", shareIds);
        String json = new Gson().toJson(maps);

        L.e("updateShare params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.UPDATE_SHARE, json, listener);
    }

    /**
     * 查询用户分享的健康数据信息
     *
     * @param context
     * @param hostId
     * @param userId   查询自己“0”
     * @param listener
     */
    public void queryUserShare(Context context, String userId, String hostId, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("userId", userId);
        maps.put("hostId", hostId);
        String url = URLConfig.HTTP + "/heathyShare/user_share";
        postWithInternetRequest(context, url, maps, RequestTag.QUERY_USER_SHARE, listener);
    }

    /**
     * 查询是否为管理员
     *
     * @param context
     * @param mobile
     * @param listener
     */

    public void userIsAdmin(Context context, String mobile, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("mobile", mobile);

        String url = URLConfig.HTTP + "/family/user_isAdmin";
        postWithInternetRequest(context, url, maps, "userIsAdmin", listener);
    }

    /**
     * 添加家庭成员
     *
     * @param context
     * @param userId
     * @param hostId
     * @param permission
     * @param listener
     */

    public void addUser(Context context, String userId, String hostId, String permission, String limitStatus, String limitTime,  RequestResultListener listener) {
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(hostId)) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("hostId", hostId);
        map.put("permission", permission);
        map.put("limitStatus", limitStatus);
        map.put("limitTime", limitTime);

        String url = URLConfig.HTTP + "/family/add_user";
        postWithInternetRequest(context, url, map, "addUser", listener);
    }

    /**
     * 更改管理员权限
     *
     * @param context
     * @param userId
     * @param hostId
     * @param toUserId
     * @param listener
     */
    public void adminPessionTranfer(Context context, String userId, String hostId, String toUserId, RequestResultListener listener) {
        if (StringUtil.isEmpty(userId)
                || StringUtil.isEmpty(hostId)
                || StringUtil.isEmpty(toUserId)) {
            return;
        }
        String url = URLConfig.HTTP + "/family/admin_permission_transfer";
        HashMap<String, String> maps = new HashMap<>();
//        maps.put("token", Constant.TOKEN);
        maps.put("userId", userId);
        maps.put("hostId", hostId);
        maps.put("toUserId", toUserId);
        postWithInternetRequest(context, url, maps, "adminPessionTranfer", listener);
//        String json = new Gson().Object2Json(maps);
//        L.e("adminPessionTranfer params===" + json);
//        OKHttpRequest.postWithNoKey(context, url, "adminPessionTranfer", json, listener);
    }

    /**
     * 获取主机管理员的信息
     *
     * @param context
     * @param hostId
     * @param listener
     */
    public void adminInfoWithHostId(Context context, String hostId, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("hostId", hostId);

        String url = URLConfig.HTTP + "/family/host_admin";
        postWithInternetRequest(context, url, map, "adminInfoWithHostId", listener);
    }

    /**
     * 用户申请或管理员分享
     *
     * @param context
     * @param adminId
     * @param applyUserId
     * @param remark
     * @param applyStatus
     * @param hostId
     * @param permissions
     * @param status
     * @param listener
     */
    public void userApplyToAdmin(Context context, String adminId, String applyUserId, String remark,
                                 String applyStatus, String hostId, String permissions, String status,String limitStatus,String limitTime,
                                 RequestResultListener listener) {
        if (StringUtil.isEmpty(adminId) || StringUtil.isEmpty(applyUserId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", adminId);
        map.put("applyUserId", applyUserId);
        map.put("remark", remark);
        map.put("applyStatus", applyStatus);
        map.put("hostId", hostId);
        map.put("permissions", permissions);
        map.put("limitStatus", limitStatus);
        map.put("limitTime", limitTime);
        map.put("status", status);

        String url = URLConfig.HTTP + "/apply/user_apply_v1";
        postWithInternetRequest(context, url, map, "userApplyToAdmin", listener);
    }

    /**
     * 用户更新申请状态
     *
     * @param context
     * @param adminId
     * @param applyUserId
     * @param applyStatus
     * @param hostId
     * @param status
     * @param updateStatus
     * @param listener
     */
    public void updateUserApply(Context context, String adminId, String applyUserId,
                                 String applyStatus, String hostId, String status, String updateStatus, RequestResultListener listener) {
        if (StringUtil.isEmpty(adminId) || StringUtil.isEmpty(applyUserId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", adminId);
        map.put("applyUserId", applyUserId);
        map.put("applyStatus", applyStatus);
        map.put("hostId", hostId);
        map.put("status", status);
        map.put("updateStatus", updateStatus);

        String url = URLConfig.HTTP + "/apply/update_status_v1";
        postWithInternetRequest(context, url, map, "updateUserApply", listener);
    }

    public void joinBlackList(Context context, String userId,RequestResultListener listener) {
        if (StringUtil.isEmpty(userId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        String url = URLConfig.HTTP + "/user/add_black_list";
        postWithInternetRequest(context, url, map, "addBlackList", listener);
    }

    public void removeBlackList(Context context,String mobile,RequestResultListener listener) {
        if (StringUtil.isEmpty(mobile)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        String url = URLConfig.HTTP + "/user/remove_black_list";
        postWithInternetRequest(context, url, map, "removeBlackList", listener);
    }

    public void isInBlackList(Context context,String mobile,RequestResultListener listener) {
        if (StringUtil.isEmpty(mobile)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        String url = URLConfig.HTTP + "/user/query_in_black_list";
        postWithInternetRequest(context, url, map, "queryInBlackList", listener);
    }

    /**
     * 查询用户是否有未处理申请或分享
     *
     * @param context
     * @param userId
     * @param listener
     */
    public void isUserHaveApplyOrShare(Context context, String userId, RequestResultListener listener) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);

        String url = URLConfig.HTTP + "/apply/query_user_apply_share";
        postWithInternetRequest(context, url, map, "isUserHaveApplyOrShare", listener);
    }

    /**
     * 查询指定主机下的申请信息
     *
     * @param context
     * @param hostId
     * @param status
     * @param listener
     */
    public void applyInfoWithHostId(Context context, String hostId, String status, RequestResultListener listener) {
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("status", status);

        String url = URLConfig.HTTP + "/apply/host_show";
        postWithInternetRequest(context, url, map, "applyInfoWithHostId", listener);
    }

    /**
     * 主机删除
     *
     * @param context
     * @param hostId
     * @param listener
     */
    public void deleteHost(Context context, String hostId, RequestResultListener listener) {
        if (StringUtil.isEmpty(hostId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);

        String url = URLConfig.HTTP + "/family/delete_host";
        postWithInternetRequest(context, url, map, "deleteHost", listener);
    }


    /**
     * 删除指定用户
     *
     * @param context
     * @param hostId
     * @param userId
     * @param listener
     */
    public void deleteUser(Context context, String hostId, String userId, RequestResultListener listener) {
        if (StringUtil.isEmpty(hostId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("userId", userId);

        String url = URLConfig.HTTP + "/family/delete_user";
        postWithInternetRequest(context, url, map, "deleteUser", listener);
    }

    /**
     * 删除申请的用户
     *
     * @param context
     * @param userId
     * @param applyUserId
     * @param listener
     */
    public void deleteApply(Context context, String userId, String applyUserId, RequestResultListener listener) {
        if (StringUtil.isEmpty(applyUserId) || StringUtil.isEmpty(userId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("applyUserId", applyUserId);
        String url = URLConfig.HTTP + "/apply/user_delete";
        postWithInternetRequest(context, url, map, "deleteUser", listener);
    }

    /**
     * 《管理员》修改用户权限
     *
     * @param context
     * @param hostId
     * @param userId
     * @param userpermission
     * @param listener
     */
    public void updateUserPermission(Context context, String hostId, String userId, String userpermission,String limitTime,String limitStatus, RequestResultListener listener) {
        if (StringUtil.isEmpty(hostId) || StringUtil.isEmpty(userId)) {
            SharedPreferencesUtils.readUserInfoFromPreferences(context);
            SharedPreferencesUtils.readCurrentHostIdFromPreferences(context);

            userId = Constant.USERID;
            hostId = Constant.CURRENTHOSTID;
        }
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("userId", userId);
        map.put("userpermission", userpermission);
//        if(!limitTime.equals("0")){
//            map.put("limitTime", limitTime);
//        }
//        if(limitStatus.equals("0")||limitStatus.equals("1")){
//            map.put("limitStatus", limitStatus);
//        }
        map.put("limitTime", limitTime);
        map.put("limitStatus", limitStatus);
        //family/update_permission
        String url = URLConfig.HTTP + "/family/update_permission";
        postWithInternetRequest(context, url, map, "updateUserPerssion", listener);
    }

    /**
     * 《管理员》修改用户权限
     *
     * @param context
     * @param hostId
     * @param userId
     * @param userpermission
     * @param listener
     */
    public void updateUserPermission(Context context, String hostId, String userId, String userpermission, RequestResultListener listener) {
        if (StringUtil.isEmpty(hostId) || StringUtil.isEmpty(userId)) {
            SharedPreferencesUtils.readUserInfoFromPreferences(context);
            SharedPreferencesUtils.readCurrentHostIdFromPreferences(context);

            userId = Constant.USERID;
            hostId = Constant.CURRENTHOSTID;
        }
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("userId", userId);
        map.put("userpermission", userpermission);
        //family/update_permission
        String url = URLConfig.HTTP + "/family/update_permission";
        postWithInternetRequest(context, url, map, "updateUserPerssion", listener);
    }

    /**
     * 查询用户权限
     *
     * @param context
     * @param hostId
     * @param userId
     * @param listener
     */
    public void queryUserPermission(Context context, String hostId, String userId, RequestResultListener listener) {
        if (StringUtil.isEmpty(hostId) || StringUtil.isEmpty(userId)) {
            SharedPreferencesUtils.readUserInfoFromPreferences(context);
            SharedPreferencesUtils.readCurrentHostIdFromPreferences(context);

            userId = Constant.USERID;
            hostId = Constant.CURRENTHOSTID;
        }
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("userId", userId);
        String url = URLConfig.HTTP + "/user/host_permissions";
        postWithInternetRequest(context, url, map, "host_permissions", listener);
    }


    //分配权限(userid用户id, hostId主机id,limitTime限时时间,limitStatus限时状态)
    public void updateLimitTime(Context context, String adminId,
                                String limitStatus, String limitTime, String hostId,  RequestResultListener listener) {
        if (StringUtil.isEmpty(adminId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", adminId);
        map.put("limitStatus", limitStatus);
        map.put("hostId", hostId);
        map.put("limitTime", limitTime);

        String url = URLConfig.HTTP + "/family/update_limittime";
        postWithInternetRequest(context, url, map, "update_limittime", listener);
    }


    //检查权限(userid用户id)
    public void checkLimitTimeInfo(Context context, String adminId, RequestResultListener listener) {
        if (StringUtil.isEmpty(adminId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", adminId);

        String url = URLConfig.HTTP + "/family/check_user_limitstatus";
        postWithInternetRequest(context, url, map, "check_user_limitstatus", listener);
    }


}
