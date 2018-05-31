package com.boer.delos.utils.userPermissionCheck;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 用来检查所需的权限是否具有，没有则提示用户
 * @CreateDate: 2016/12/6 0006 16:46
 * @Modify:
 * @ModifyDate:
 */

public class UserPermissionCheck {
    private static final String PACKAGE_NAME = "com.boer.jiaweishi";

    /**
     * 获取应用的权限列表
     */
    public static void getApplicationAllPermissionList(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pack = null;
        try {
            pack = pm.getPackageInfo(PACKAGE_NAME, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String[] permissionStrings = pack.requestedPermissions;

        checkUserPermission(context, permissionStrings);

    }


    public static boolean checkUserPermission(Context context, String permissionName) {
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permissionName, PACKAGE_NAME));
    }

    /**
     * 查询所有的生命的权限是否获得
     *
     * @param permission
     * @return
     */
    private static Map<String, Boolean> checkUserPermission(Context context, String[] permission) {
        Map<String, Boolean> map = new HashMap<>();
        for (String s : permission) {
            map.put(s, checkUserPermission(context, s));
        }
        System.out.print("permission " + map);
        return map;

    }

}


