package com.boer.delos.activity.healthylife.skintest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.boer.delos.utils.ToastHelper;

import org.shake.bluetooth.conn.ScaleConn;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/10/16.
 */

public class BLEClient {
    private static BLEClient instance=new BLEClient();
    private BLEClient(){}
    public static BLEClient getInstance(){
        return instance;
    }

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes
            .HEART_RATE_MEASUREMENT);
    public final static String ACTION_DATA_AVAILABLE = "com.kier.bluetooth.le" + "" +
            ".ACTION_DATA_AVAILABLE";
    private boolean mScanning = false;
    private boolean mIsConnect = false;
    BluetoothDevice device;
    private Activity mContext;
    private Handler mHandler;
    public void initBLE(Activity context){
        mContext=context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context
                .BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!context.getApplication().getPackageManager().hasSystemFeature(PackageManager
                .FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "当前手机不支持BLE蓝牙", Toast.LENGTH_SHORT).show();
            context.finish();
        } else {
            scanBLE(true);
        }
    }

    public void scanBLE(boolean enable){
        mHandler.removeCallbacks(runnable);
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivityForResult(enableBtIntent, 1);
        } else {
            if (enable) {
                device = null;
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mHandler.removeCallbacks(runnable);
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice d, int rssi, byte[] scanRecord) {

            if (d != null && !TextUtils.isEmpty(d.getName()))
                Log.v("gl", "dname==" + d.getName());

            if (d != null && !TextUtils.isEmpty(d.getName())) {

                if (d.getName().equals("XYL-BT") || d.getName().equals("het-31-8")) {
                    device = d;
                    mHandler.post(runnable);
                }
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBluetoothGatt = device.connectGatt(mContext.getApplication(), false, mGattCallback);
        }
    };

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Message message = mHandler.obtainMessage();
                message.what = 2;
                mHandler.sendMessage(message);
                mIsConnect = false;
//                scanBLE(true);
            } else if(newState == BluetoothProfile.STATE_CONNECTED){
                scanBLE(false);
                mBluetoothGatt.discoverServices();
                mIsConnect = true;
                Message message = mHandler.obtainMessage();
                message.what = 1;
                if (mBluetoothGatt != null) {
                    if (device != null && !TextUtils.isEmpty(device.getName()))
                        message.obj=device.getName();
                    else
                        message.obj="";
                }
                mHandler.sendMessage(message);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {

            System.out.println("onDescriptorWriteonDescriptorWrite = " + status
                    + ", descriptor =" + descriptor.getUuid().toString());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                displayGattServices(gatt.getServices());
            }
        }
    };

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic
            characteristic) {
        WriteLog(characteristic.getValue());

        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            final int heartRate = characteristic.getIntValue(format, 1);
        } else if (characteristic.getUuid().equals(UUID.fromString(SampleGattAttributes
                .SKIN_READ_CHARAC))) {
            String str;
            final byte[] data = characteristic.getValue();
            Message message = mHandler.obtainMessage();
            if (data == null) {
                ToastHelper.showShortMsg("data null");
                return;
            }
            if ((data[0] & 0xFF) != 0xFF) {

                ToastHelper.showShortMsg("data head error");
                return;
            }
            if ((data[1] & 0xAA) == 0xAA && (data[2] & 0xFF) == 0xFF) {
                message.what = 8;
                mHandler.sendMessage(message);
                return;
            }
            if ((data[1] & 0xAA) == 0xAA && (data[2] & 0xFE) == 0xFE) {//未完成清零，请擦拭测试探头后清零，正在清零中
                message.what = 3;
                mHandler.sendMessage(message);
                return;
            }
            if ((data[1] & 0xAA) == 0xAA && (data[2] & 0xFD) == 0xFD) {//已完成清零，请测试
                message.what = 4;
                mHandler.sendMessage(message);
                return;
            }
            if ((data[1] & 0xAA) == 0xAA && (data[2] & 0xFC) == 0xFC) {//测试结果出错，请将测试头和皮肤贴紧重新测试
//                message.what = 5;
//                mHandler.sendMessage(message);
//                return;
            }
            if ((data[5] & 0xFE) == 0xFE) {//重新测试，请在测试过程中将测试头紧贴皮肤，不要抖动
                //FF 20 01 7B 00 FE 00 5B 00 00 00 00 00 00 00 00 00 00 00 00

                if (device.getName().equals("het-31-8")) {
                    data[5] = 0x00;
                } else {
//                    message.what = 6;
//                    mHandler.sendMessage(message);
//                    return;
                }
            }

            double water;
            double oil;
            double flex;

            int h, l;
            h = data[2] * 256;
            l = data[1] & 0xff;
            water = h + l;
//            water = water / 10.0;
            water = water / 1000.0;

            h = data[4] * 256;
            l = data[3] & 0xff;
            oil = h + l;
//            oil = oil / 10.0;
            oil = oil / 1000.0;

            h = data[6] * 256;
            l = data[5] & 0xff;
            flex = h + l;
            flex = flex / 10.0;
            str = "get data success: water " + water + "%" + " oil " + oil + "%" + " flex " + flex;
            System.out.println("=======" + str);

            if(water<=0.0||oil<=0.0){
                message.what = 5;
                mHandler.sendMessage(message);
                return;
            }

            //因为设备传值过来有问题，这个公式是为了解决当油分大于等于水分的时候
//            if(oil>=water){
//                oil = water*0.68;
//            }
            message.what = 7;
            message.obj=new double[]{water,oil,flex};
            mHandler.sendMessage(message);
            return;
        }
    }

    private void WriteLog(byte[] data) {
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            Log.d("BLEClient",stringBuilder.toString());
        } else {
            Log.d("BLEClient","null or zero");
        }
    }

    public void closeBLE(){
        mHandler.removeCallbacksAndMessages(null);
        scanBLE(false);
        closeGatt();
    }

    private void closeGatt() {
        mIsConnect = false;
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public void noWorking() {
        byte[] b = new byte[20];
        for (int i = 1; i < 20; i++) {
            b[i] = 0;
        }
        try {
            WriteBleData(SampleGattAttributes.SKIN_SERIVCE, SampleGattAttributes
                            .SKIN_WRITE_CHARAC,
                    b, 20);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void WriteBleData(String sUuid, String cUuid, byte[] aa, int len) {

        byte[] value = new byte[20];
        value[0] = (byte) 0x00;

        if (getIsConnect() == false) {
            return;
        }
        if (mBluetoothGatt == null) {
            return;
        }
        List<BluetoothGattService> list = mBluetoothGatt.getServices();
        for (BluetoothGattService service : list) {
            if (service.getUuid().equals(UUID.fromString(sUuid))) {
                List<BluetoothGattCharacteristic> list1 = service.getCharacteristics();
                for (BluetoothGattCharacteristic cs : list1) {
                    if (cs.getUuid().equals(UUID.fromString(cUuid))) {
                        cs.setValue(aa);
                        cs.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        mBluetoothGatt.writeCharacteristic(cs);
                        break;
                    }
                }
                break;
            }
        }
    }

    public void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                final int charaProp = gattCharacteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    //mBluetoothLeService.readCharacteristic(characteristic);
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                        return;
                    }
                    if (gattCharacteristic.getUuid().equals(UUID.fromString(SampleGattAttributes
                            .SKIN_READ_CHARAC))) {
                        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor
                                (UUID.fromString(SampleGattAttributes
                                        .CLIENT_CHARACTERISTIC_CONFIG));
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }
    }

    public boolean getIsConnect(){
        return mIsConnect&&isEnable();
    }

    public boolean isScanning(){
        return mScanning&&isEnable();
    }
    private boolean isEnable(){
        if(mBluetoothAdapter==null){
            return false;
        }
        else{
            return mBluetoothAdapter.isEnabled();
        }
    }

    public void setmHandler(Handler handler){
        mHandler=handler;
    }
}
