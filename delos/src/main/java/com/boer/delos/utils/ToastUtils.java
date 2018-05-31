package com.boer.delos.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.boer.delos.R;

/**
 * @author wangkai
 * @Description: 吐司工具类，二次封装SVProgressHUD，
 * Url:https://github.com/saiwu-bigkoo/Android-SVProgressHUD
 * create at 16/4/25 下午2:34
 */
public class ToastUtils {
    private SVProgressHUD svp = null;
    private Context context;
//    private static ToastUtils toastUtils = null;

    public ToastUtils(Context context) {
        try {
            this.context = context;
            svp = new SVProgressHUD(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    public static ToastUtils getInstance(Context context) {
//        if (toastUtils == null) {
//            toastUtils = new ToastUtils(context);
//        }
//        return toastUtils;
//    }

    /**
     * 加载提示框
     *
     * @param message 加载信息，默认加载中...
     */
    public void showProgress(String message) {
        if (!(context instanceof Activity)) {
            ToastHelper.showShortMsg(message);
            return;
        }
        try {
            if (svp == null) {
                svp = new SVProgressHUD(context);
            }
            dismiss();
            if (TextUtils.isEmpty(message)) {
                svp.showWithStatus(context.getString(R.string.toast_loading));
            } else {
                svp.showWithStatus(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgress(String message, int type) {

        if (!(context instanceof Activity)) {
            ToastHelper.showShortMsg(message);
            return;
        }
        try {
            if (svp == null) {
                svp = new SVProgressHUD(context);
            }
            dismiss();
            if (TextUtils.isEmpty(message)) {
                svp.showWithStatus(context.getString(R.string.toast_loading), SVProgressHUD.SVProgressHUDMaskType.None);
            } else {
                svp.showWithStatus(message, SVProgressHUD.SVProgressHUDMaskType.None);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示信息
     *
     * @param message 提示信息，默认未知异常
     */
    public void showInfoWithStatus(final String message) {
        if (!(context instanceof Activity)) {
            ToastHelper.showShortMsg(message);
            return;
        }
        try {
            if (svp == null) {
                svp = new SVProgressHUD(context);
            }
            dismiss();

            if (TextUtils.isEmpty(message)) {
                // svp.showInfoWithStatus(context.readString(R.string.toast_unknow_error), SVProgressHUD.SVProgressHUDMaskType.None);
            } else {
                svp.showInfoWithStatus(message, SVProgressHUD.SVProgressHUDMaskType.None);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示成功
     *
     * @param message 提示信息，默认未知异常
     */
    public void showSuccessWithStatus(String message) {
        if (!(context instanceof Activity)) {
            ToastHelper.showShortMsg(message);
            return;
        }
        try {
            if (svp == null) {
                svp = new SVProgressHUD(context);
            }
            dismiss();
            if (TextUtils.isEmpty(message)) {
                svp.showSuccessWithStatus(context.getString(R.string.toast_upload_success));
            } else {
                svp.showSuccessWithStatus(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示错误
     *
     * @param message 提示信息，默认未知异常
     */
    public void showErrorWithStatus(final String message) {
        if (!(context instanceof Activity)) {
            ToastHelper.showShortMsg(message);
            return;
        }
        try {
            if (svp == null) {
                svp = new SVProgressHUD(context);
            }
            dismiss();

            if (TextUtils.isEmpty(message)) {
                //svp.showErrorWithStatus(context.readString(R.string.toast_unknow_error), SVProgressHUD.SVProgressHUDMaskType.GradientCancel);
            } else {
                svp.showErrorWithStatus(message, SVProgressHUD.SVProgressHUDMaskType.GradientCancel);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
            if (svp != null && svp.isShowing()) {
                svp.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add by sunzhibin
     *
     * @return
     */
    public boolean isShowing() {
        try {
            if (svp == null) {
                return false;
            }
            if (svp.isShowing()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
