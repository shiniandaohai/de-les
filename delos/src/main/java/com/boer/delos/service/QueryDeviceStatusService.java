package com.boer.delos.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.boer.delos.constant.Constant;
import com.boer.delos.interf.ISimpleInterfaceString;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceStatusResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 17/5/8.
 *
 * TODO : 暂时不用
 */

public class QueryDeviceStatusService extends Service {

    private boolean isRunning;
    private Timer mTimer;
    private long TIMER_TIME = 10;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final List<Device> list = (List<Device>) intent.getSerializableExtra("list");
        final ISimpleInterfaceString listener = (ISimpleInterfaceString) intent.getSerializableExtra("listener");

        if (isRunning) {
            return super.onStartCommand(intent, flags, startId);
        }
        isRunning = true;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                queryDeviceStatus(list,listener);

            }
        }, 0, TIMER_TIME);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 查询设备状态
     *
     * @param list
     */
    private void queryDeviceStatus(final List<Device> list, final ISimpleInterfaceString listener) {
        DeviceController.getInstance().queryDevicesStatus(this, list, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    BaseResult baseResult = GsonUtil.getObject(json, BaseResult.class);
                    if (baseResult.getRet() != 0) {
                        return;
                    }
                   if (listener!=null){
                       listener.clickListener(json);
                   }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    //此方法是为了可以在Acitity中获得服务的实例
    class MyBinder extends Binder {
        public Service getService() {
            return QueryDeviceStatusService.this;
        }
    }

}
