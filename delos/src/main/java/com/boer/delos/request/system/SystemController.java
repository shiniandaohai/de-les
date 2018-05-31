package com.boer.delos.request.system;

import android.content.Context;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.utils.StringUtil;

import java.util.HashMap;

/**
 * @Description: 系统设置，一键备份，一键还原等
 */
public class SystemController extends BaseController implements SystemInterface {
    public static SystemController instance = null;

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static SystemController getInstance() {
        if (instance == null) {
            synchronized (SystemController.class) {
                if (instance == null) {
                    instance = new SystemController();
                }
            }
        }
        return instance;
    }

    /**
     * 一键备份
     *
     * @param context
     * @param listener
     */
    public void oneTouchBackup(Context context, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        String url = URLConfig.HTTP + "/wrapper/request?uid=" + Constant.USERID + "&cmdType=1042";
        postWithInternetRequest(context, url, maps, "oneTouchBackup", listener);

    }

    /**
     * 一键还原
     *
     * @param context
     * @param listener
     */
    public void vistaGhost(Context context, String currentHostId, RequestResultListener listener) {
        if (StringUtil.isEmpty(currentHostId) || StringUtil.isEmpty(Constant.USERID)) {
            return;
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("hostId", currentHostId);
        String url = URLConfig.HTTP + "/host/restoreproperty?uid=" + Constant.USERID;
        postWithInternetRequest(context, url, maps, "vistaGhost", listener);
    }

    /**
     * 软件更新
     *
     * @param context
     * @param listener
     */
    public void softwareUpgrade(Context context, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        String url = URLConfig.HTTP + "/host/software_upgrade";
        postWithInternetRequest(context, url, maps, "software_upgrade", listener);
    }

    /**
     * 软件是否需要更新
     *
     * @param context
     * @param listener
     */
    public void softwareNeedUpgrade(Context context, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        String url = URLConfig.HTTP + "/upgrade/latest";
        postWithInternetRequest(context, url, maps, "upgrade_latest", listener);
    }
}
