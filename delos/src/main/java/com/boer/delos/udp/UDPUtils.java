package com.boer.delos.udp;

import com.boer.delos.constant.Constant;
import com.boer.delos.model.GatewayInfo;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.sign.HexUtils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;

public class UDPUtils {

    private static final String broadCast_Address = "255.255.255.255";
    private static final String broadCast_String = "53434347010002000100200047434353";
    //    private static final int broadCast_Port = 9999;
    private static final int broadCast_Port = 9777;

    private static final int listener_Port = 9998;

    public static Boolean isListener = Boolean.TRUE;

    /**
     * 发送广播
     */
    public static void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] data = HexUtils.HexStringTobytes(broadCast_String);
                    MulticastSocket sender = new MulticastSocket();
                    InetAddress group = InetAddress.getByName(broadCast_Address);
                    DatagramPacket dj = new DatagramPacket(data, data.length, group, broadCast_Port);
                    sender.send(dj);
                    sender.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Loger.d(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * udp监听
     */
    public static void listener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //清除当前手机上的所有主机信息
                    Constant.gatewayInfos.clear();
                    byte[] data = new byte[1024];
                    MulticastSocket ms = new MulticastSocket(listener_Port);
                    while (isListener) {
                        DatagramPacket dp = new DatagramPacket(data, data.length);
                        if (ms != null)
                            ms.receive(dp);
                        if (data.length > 0) {
                            GatewayInfo info = gatewayHandler(dp.getData());
                            constantGatewaysHandler(info);
                        }
                    }
                } catch (Exception e) {
                    L.e(e.toString());
                }
            }
        }).start();
    }

    /**
     * 全局主机数据处理,判断是否在主机列表中,如果没有,加入主机列表
     *
     * @param info
     */
    private static void constantGatewaysHandler(GatewayInfo info) {
        Boolean addToList = Boolean.TRUE;
        for (GatewayInfo gatewayInfo : Constant.gatewayInfos) {
            if (gatewayInfo.getHostId().equals(info.getHostId())) {
                addToList = Boolean.FALSE;
            }
        }
        if (addToList) {
            Constant.gatewayInfos.add(info);
        }
    }

    /**
     * udp返回数据处理
     *
     * @param bytes
     * @return
     */
    private static GatewayInfo gatewayHandler(byte[] bytes) {
        byte[] hostIdBytes = subBytes(bytes, 10, 12);
        byte[] hostNameBytes = subBytes(bytes, 58, 18);
        byte[] hostIPBytes = subBytes(bytes, 24, 16);
        GatewayInfo info = new GatewayInfo();

        info.setHostId(handlerBytes(hostIdBytes));
        info.setHostName(handlerBytes(hostNameBytes));
        info.setIp(handlerBytes(hostIPBytes));

        return info;
    }

    /**
     * 从一个byte[]数组中截取一部分
     *
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) bs[i - begin] = src[i];
        return bs;
    }

    /**
     * 对字节进行处理
     *
     * @param src
     * @return
     */
    public static String handlerBytes(byte[] src) {
        int index =0;
        for (byte b : src) {
            if (b == 0){
                break;
            }
            index++;
        }
        return new String(src,0,index);
    }

    /**
     * 开始UDP广播,3秒后取消
     */
    public static void startUDPBroadCast(final ScanCallback callback) {
        isListener = Boolean.TRUE;
        listener();
        sendMessage();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isListener = Boolean.FALSE;
                timer.cancel();
                if (callback != null) {
                    callback.callback();
                }
            }
        }, 3000);
    }

    public interface ScanCallback {
        void callback();
    }
}