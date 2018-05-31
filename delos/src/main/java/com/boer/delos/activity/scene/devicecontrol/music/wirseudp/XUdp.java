package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;

import android.text.TextUtils;
import android.util.Log;

import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.utils.CharsetUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * udp 客户端
 */
public class XUdp extends BaseXSocket {
    private static final String TAG = "BaseXSocket";
    protected UdpClientConfig mUdpClientConfig;
    protected List<UdpClientListener> mUdpClientListeners;
    private DatagramSocket datagramSocket;
    private DatagramSocket datagramSocketSend;
    private SendThread sendThread;
    private ReceiveThread receiverThread;
    private boolean isSendThreadSend = true;
    private boolean isreceiverThreadReceive = true;

    private XUdp() {
        super();
    }

    public static XUdp getUdpClient() {
        XUdp client = new XUdp();
        client.init();
        return client;
    }

    private void init() {
        mUdpClientListeners = new ArrayList<>();
        mUdpClientConfig = new UdpClientConfig.Builder().create();
        isSendThreadSend = true;
        isreceiverThreadReceive = true;
    }

    private void closeSocket() {
        if (datagramSocket != null && datagramSocket.isConnected()) {
            datagramSocket.disconnect();
            datagramSocket = null;
        }
        if (datagramSocketSend != null && datagramSocketSend.isConnected()) {
            datagramSocketSend.disconnect();
            datagramSocketSend = null;
        }
    }

    public void clear() {
        removeUdpClientListener(null);
        if (sendThread != null)
            sendThread.getMsgQueue().clear();
        isSendThreadSend = false;
        isreceiverThreadReceive = false;
        closeSocket();
        receiverThread = null;
        sendThread = null;
    }

    public void startUdpServer() {
        if (!getReceiveThread().isAlive()) {
            getReceiveThread().start();
            Log.d(TAG, "udp server started");
        }
    }

    public void stopUdpServer() {
        getReceiveThread().interrupt();
        notifyStopListener();
    }

    public boolean isUdpServerRuning() {
        return getReceiveThread().isAlive();
    }

    public void sendMsg(UdpMsg msg, boolean isReply) {
        if (!getSendThread().isAlive()) {//开启发送线程
            getSendThread().start();
        }
        getSendThread().enqueueUdpMsg(msg);
        if (isReply) {//根据是否需要回复，开启接受线程
            startUdpServer();
        }
    }

    public void sendMsg(UdpMsg msg) {
        sendMsg(msg, false);
    }

    private SendThread getSendThread() {
        if (sendThread == null || !sendThread.isAlive()) {
            sendThread = new SendThread();
        }
        return sendThread;
    }

    private ReceiveThread getReceiveThread() {
        if (receiverThread == null || !receiverThread.isAlive()) {
            receiverThread = new ReceiveThread();
        }
        return receiverThread;
    }

    private DatagramSocket getDatagramSocketSend() {
        if (datagramSocketSend != null) {
            return datagramSocketSend;
        }
        synchronized (this) {
            if (datagramSocketSend != null) {
                return datagramSocketSend;
            }
//            int localPort = mUdpClientConfig.getLocalPort();
            int localPort = mUdpClientConfig.getLocalPort();
            try {
                if (localPort > 0) {
                    datagramSocketSend = UdpSocketManager.getUdpSocket(localPort);
                    if (datagramSocketSend == null) {
                        datagramSocketSend = new DatagramSocket(localPort);
                        UdpSocketManager.putUdpSocket(datagramSocketSend);
                    }
                } else {
                    datagramSocketSend = new DatagramSocket();
                }
                datagramSocketSend.setSoTimeout((int) mUdpClientConfig.getReceiveTimeout());
            } catch (SocketException e) {
//                e.printStackTrace();
                notifyErrorListener("udp create socket error", e);
                datagramSocketSend = null;
            }
            return datagramSocketSend;
        }
    }

    private DatagramSocket getDatagramSocket() {
        if (datagramSocket != null) {
            return datagramSocket;
        }
        synchronized (lock) {
//            if (datagramSocket != null) {
//                return datagramSocket;
//            }
            int localPort = mUdpClientConfig.getReceivePort();
            try {
                if (localPort > 0) {
                    datagramSocket = UdpSocketManager.getUdpSocket(localPort);
                    if (datagramSocket == null) {
                        datagramSocket = new DatagramSocket(localPort);
                        UdpSocketManager.putUdpSocket(datagramSocket);
                    }
                } else {
                    datagramSocket = new DatagramSocket();
                }
                datagramSocket.setSoTimeout((int) mUdpClientConfig.getReceiveTimeout());
            } catch (SocketException e) {
//                e.printStackTrace();
                notifyErrorListener("udp create socket error", e);
                datagramSocket = null;
            }
            return datagramSocket;
        }
    }

    private class SendThread extends Thread {
        private LinkedBlockingQueue<UdpMsg> msgQueue;
        private UdpMsg sendingMsg;

        protected LinkedBlockingQueue<UdpMsg> getMsgQueue() {
            if (msgQueue == null) {
                msgQueue = new LinkedBlockingQueue<UdpMsg>();
            }
            return msgQueue;
        }

        protected SendThread setSendingMsg(UdpMsg sendingMsg) {
            this.sendingMsg = sendingMsg;
            return this;
        }

        public UdpMsg getSendingMsg() {
            return this.sendingMsg;
        }

        public boolean enqueueUdpMsg(final UdpMsg tcpMsg) {
            if (tcpMsg == null || getSendingMsg() == tcpMsg
                    || getMsgQueue().contains(tcpMsg)) {
                return false;
            }
            try {
                getMsgQueue().put(tcpMsg);
                return true;
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            return false;
        }

        public boolean cancel(UdpMsg packet) {
            return getMsgQueue().remove(packet);
        }

        public boolean cancel(int tcpMsgID) {
            return getMsgQueue().remove(new UdpMsg(tcpMsgID));
        }

        @Override
        public void run() {
            UdpMsg msg;
            if (getDatagramSocketSend() == null) {
                return;
            }
            try {
                while (isSendThreadSend
                        && (msg = getMsgQueue().take()) != null) {
                    setSendingMsg(msg);//设置正在发送的
                    Log.d(TAG, "udp send msg=" + msg);
                    byte[] data = msg.getSourceDataBytes();
                    if (data == null) {//根据编码转换消息
                        data = CharsetUtil.stringToData(msg.getSourceDataString(), mUdpClientConfig.getCharsetName());
                    }
                    if (data != null && data.length > 0) {
                        try {
                            TargetInfo mTargetInfo = msg.getTarget();
                            DatagramPacket packet = new DatagramPacket(data, data.length,
                                    new InetSocketAddress(mTargetInfo.getIp(), mTargetInfo.getPort()));
                            try {
                                msg.setTime();
                                datagramSocketSend.send(packet);
                                notifySendedListener(msg);
                            } catch (IOException e) {
//                                e.printStackTrace();
                                notifyErrorListener("发送消息失败", e);
                            }
                        } catch (Exception e) {
                            notifyErrorListener("发送消息失败", e);
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {

            byte[] buff = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buff, buff.length);
            notifyStartListener();
            while (isreceiverThreadReceive) {
                try {
                    getDatagramSocket().receive(pack);
                    byte[] res = Arrays.copyOf(buff, pack.getLength());

                    Log.d(TAG, "udp receive byte=" + Arrays.toString(res));
                    UdpMsg udpMsg = new UdpMsg(res, new TargetInfo(pack.getAddress().getHostAddress(), pack.getPort()),
                            BaseMsg.MsgType.Receive);
                    udpMsg.setTime();

                    udpMsg.setResultDataBytes(MsgResult.getInstance().dealWithData(res));
                    String msgstr = MsgResult.getInstance().data2String(udpMsg.getResultDataBytes());
                    udpMsg.setSourceDataString(msgstr);
//                    udpMsg.setSourceDataBytes(pack.getData());
                    Log.d(TAG, "udp receive msg=" + udpMsg);
                    notifyReceiveListener(udpMsg);
                } catch (IOException e) {
                    if (!(e instanceof SocketTimeoutException)) {//不是超时报错
                        notifyErrorListener(e.getMessage(), e);
                        notifyStopListener();
                    }
                }
            }
        }
    }

    public void config(UdpClientConfig udpClientConfig) {
        mUdpClientConfig = udpClientConfig;
    }

    public UdpClientConfig getmUdpClientConfig() {
        return mUdpClientConfig;
    }

    public void addUdpClientListener(UdpClientListener listener) {
        if (mUdpClientListeners.contains(listener)) {
            return;
        }
        this.mUdpClientListeners.add(listener);
    }

    public void removeUdpClientListener(UdpClientListener listener) {
        if (listener != null)
            this.mUdpClientListeners.remove(listener);
        else {
            mUdpClientListeners.clear();
        }
    }

    private void notifyReceiveListener(final UdpMsg msg) {
        for (UdpClientListener l : mUdpClientListeners) {
            final UdpClientListener listener = l;
            if (listener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onReceive(XUdp.this, msg);
                    }
                });
            }
        }
    }

    private void notifyStartListener() {
        for (UdpClientListener l : mUdpClientListeners) {
            final UdpClientListener listener = l;
            if (listener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStarted(XUdp.this);
                    }
                });
            }
        }
    }

    private void notifyStopListener() {
        for (UdpClientListener l : mUdpClientListeners) {
            final UdpClientListener listener = l;
            if (listener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStoped(XUdp.this);
                    }
                });
            }
        }
    }

    private void notifySendedListener(final UdpMsg msg) {
        for (UdpClientListener l : mUdpClientListeners) {
            final UdpClientListener listener = l;
            if (listener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSended(XUdp.this, msg);
                    }
                });
            }
        }
    }

    private void notifyErrorListener(final String msg, final Exception e) {
        for (UdpClientListener l : mUdpClientListeners) {
            final UdpClientListener listener = l;
            if (listener != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(XUdp.this, msg, e);
                    }
                });
            }
        }
    }

    @Override
    public String toString() {
        return "XUdp{" +
                "datagramSocket=" + datagramSocket +
                '}';
    }

}