package com.boer.delos.constant;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.boer.delos.R;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ToastUtils;

import java.util.List;

/**
 * Created by apple on 17/5/10.
 */

public class DeviceUpdateStatus {
    public static void setCommonDevice(Context context, List<Device> devices, final ToastUtils toastUtils){
        toastUtils.showProgress("");
        DeviceController.getInstance().AddCommonDevice(context, devices, Constant.LOCAL_CONNECTION_IP, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                toastUtils.dismiss();
                BaseResult result = GsonUtil.getObject(json, BaseResult.class);
                if (result.getRet() != 0) {
                    return;
                }

            }

            @Override
            public void onFailed(String json) {
                toastUtils.dismiss();
                Loger.d(json);
            }
        });
    }
}
