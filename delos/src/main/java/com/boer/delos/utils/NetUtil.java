package com.boer.delos.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 网络工具类
 *
 * @author aizhimin
 */
public class NetUtil {
    private static final String TAG = "NetUtil";

    public static boolean isBreak = false;

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNet(Context context) {// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 判断是否有wifi或3g网络
     *
     * @param context
     * @return
     */
    public static boolean checkWifiOr3gNet(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();

        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            return false;
        }

        // 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        int netType = info.getType();
        int netSubtype = info.getSubtype();

        if (netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        } else if (netType == ConnectivityManager.TYPE_MOBILE
                && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
                && !mTelephony.isNetworkRoaming()) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    /**
     * 判断是否为2G网络：gprs
     *
     * @param context
     * @return
     */
    public static boolean is2gNet(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            return false;
        }
        // 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        int netType = info.getType();
        int netSubtype = info.getSubtype();
        if (netType == ConnectivityManager.TYPE_MOBILE
                && netSubtype != TelephonyManager.NETWORK_TYPE_UMTS) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    /**
     * 判断是否为3g网络
     *
     * @param context
     * @return
     */
    public static boolean is3gNet(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo mWifi = mConnectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mWifi.isConnected();
    }

    /**
     * 检查是否有wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        // NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        // if (info == null || !mConnectivity.getBackgroundDataSetting()) {
        // return false;
        // }
        // //判断网络连接类型，只有在3G或wifi里进行一些数据更新。
        // int netType = info.getType();
        // if (netType == ConnectivityManager.TYPE_WIFI) {
        // return info.isConnected();
        // } else {
        // return false;
        // }

        NetworkInfo mWifi = mConnectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    /**
     * 获得mac地址
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取手机的ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    /**
     * 判断端口是否可用
     *
     * @param host
     * @param port
     * @return
     */
    public static boolean isAvailable(String host, int port) {
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /**
     * 从某端口开始，活动一个未被占用，可使用的端口。
     *
     * @param host
     * @param startPort
     * @return
     */
    public static int getAvailablePort(String host, int startPort) {
        for (int i = 80; i < 65535; i++) {
            if (NetUtil.isAvailable(host, i)) {
                startPort = i;
                break;
            }
        }
        return startPort;
    }

    public static int breakpointDownload(int startposition, String fileName, String filePath,
                                         String netUrl) {

        isBreak = false;
        String localPath = filePath + "/" + fileName;

        try {
            File file = new File(localPath);
            if (!file.exists()) {
                startposition = 0;
            }

            URL url = new URL(netUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Range", "bytes=" + startposition + "-");
            conn.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");

            InputStream inStream = conn.getInputStream();
            int length = conn.getContentLength();

            if (!file.exists()) {
                RandomAccessFile rfile = new RandomAccessFile(localPath, "rw");
                rfile.setLength(length);
                ;
                rfile.close();
            }
            RandomAccessFile rfile = new RandomAccessFile(localPath, "rw");
            rfile.writeUTF("utf-8");
            rfile.seek(startposition);
            int downPosition = startposition;

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                rfile.write(buffer, 0, len);
                downPosition += len;
                Log.i(TAG, "downPosition:" + downPosition);
                if (isBreak) {
                    break;
                }
            }
            inStream.close();
            rfile.close();
            return downPosition;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage().toString());
        }
        return 0;
    }


    /**
     * @author suncat
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return
     */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            /*为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞*/
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "Error");
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "Output");
            errorGobbler.start();
            outputGobbler.start();
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }


    public static class StreamGobbler extends Thread {

        InputStream is;
        String type;

        public StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (type.equals("Error")) {
                        System.out.println("Error   :" + line);
                    } else {
                        System.out.println("Debug:" + line);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static boolean isNetWorkMobileConnect(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            return false;
        }
    }
}
