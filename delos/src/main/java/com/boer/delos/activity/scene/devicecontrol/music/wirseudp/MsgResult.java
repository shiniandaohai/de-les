package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;

import android.text.TextUtils;
import android.util.Log;

import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sunzhibin on 2017/8/18.
 */

public class MsgResult implements IWirseUdpCMD {

    private static class MsgHelper {
        static MsgResult instance = new MsgResult();
    }

    private MsgResult() {
    }

    public static MsgResult getInstance() {
        return MsgHelper.instance;
    }

    /**
     * 操作的 命令
     */
    public List<Byte> listCMd = new ArrayList<>();
    private static byte FRIST_1 = 0x7E;//起始位
    private static byte FRIST_2 = 0x7E;//起始位

    private static byte BYTE_LENTH_1 = 0x0;//包长度 4+N
    private static byte BYTE_LENTH_2 = 0x0;//包长度

    private static byte PROPOL_NUM = 0;//协议号 即控制命令
    private static byte CONTENT = 0;//信息内容

    private static byte NUM = 0x01;//帧序列号
    private static byte STOP_1 = 0x0D;//停止位
    private static byte STOP_2 = 0x0A;//停止位

    public synchronized byte[] dealWithData(byte[] result) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(result[3] - 4);
        for (int i = 0; i < result.length; i++) {

            if (i <= 4) continue;
            if (result[i] == 0)
                break;
//            }c8 9d fc 03
            if (result[i + 1] == STOP_1
                    && result[i + 2] == STOP_2) {
                break;
            }

            byteBuffer.put(result[i]);
        }

        return byteBuffer.array();
    }

    public synchronized String data2String(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            return "";
        }
        if (bytes.length == 1) {
            return bytes[0] + "";
        }
        if (bytes[0] != 0 && bytes[1] == 0) {
            return bytes[0] + "";
        }
        if (bytes[0] == 0) {
            return bytes[0] + "";
        }
        //部分数据位为0的状态、默认设置为时间
        if (bytes[bytes.length - 1] == 0) {
            return ByteUtils.bytesToHexString(bytes);
        }

        String temp = Arrays.toString(bytes);
        if (temp.contains("0")) {
            //时间
        }
        return new String(bytes);
    }

    /**
     * 处理UDP返回的结果
     *
     * @param result
     * @param udpMsg
     * @return
     */
    public static String dealWithBytes(String result, UdpMsg udpMsg) {
        PROPOL_NUM = udpMsg.getSourceDataBytes()[4];//
        String res = "";
        switch (PROPOL_NUM) {
            case TCPCMD_PALPITANT:
                //暂不做处理
                break;
            case TCPCMD_PLAY_PAUSE:
                //不做处理
                break;
            case TCPCMD_PRE:
                //收到数据不做处理
                break;
            case TCPCMD_NEXT:
                //
                break;
            case TCPCMD_LOOPMODE:
                dealWith(result, udpMsg);
                return "";
            case TCPCMD_VOL_CTRL:
                //0: '-'
                //1:'+'
                break;
            case TCPCMD_PLAYSTATUS:
                // -1: nofile
                //0:invalid
                //1: play
                //2: pause
                //3: pause
                //4: paresync
                //5: parecomplete
                //6: complete
                break;
            case TCPCMD_SHUTDOWN:
                //暂时无此功能

                break;
            case TCPCMD_DURATION: //获取当前播放歌曲时长

                // 带参数返回毫秒级时间数字

                break;
            case TCPCMD_POSITION:
                //   带参数返回毫秒级时间数字

                break;
            case TCPCMD_SONGNAME:
//                带参数返回歌曲名字

                break;
            case TCPCMD_ROOMNAME:
//                带参数返回房间名和房间号，用"::"分隔

                break;
            case TCPCMD_PROGRESS://设置进度条,需带参数，参数为时间，毫秒级。
                break;
            case TCPCMD_MEDIATYPE:
                break;

            case TCPCMD_MEDIA_LIST_SIZE:
                break;

            case TCPCMD_GET_MEDIA_LIST:
                break;
            case TCPCMD_PLAY_POS:
                break;
            case TCPCMD_ARTIST:
                break;
            case TCPCMD_VOL_SET:
                break;
            case TCPCMD_VOL_GET:
                break;
            case TCPCMD_UPDATE_ROOMNAME:
                break;
            case TCPCMD_UPDATE_ROOMNO:
                break;
            case TCPCMD_SIWTCH_SOUND_SOURCE:
                break;
            case TCPCMD_GET_TIMER_SIZE:
                break;
            case TCPCMD_GET_TIMER_DATA:
                break;
            case TCPCMD_SET_TIMER_DATA:
                break;
            case TCPCMD_GET_TIMER_SWITCH:
                break;
            case TCPCMD_SET_TIMER_SWITCH:
                break;
            case TCPCMD_GET_SUBAREA_CONTROL:
                break;
            case TCPCMD_SET_SUBAREA_CONTROL:
                break;
            case TCPCMD_GET_EQ:
                break;
            case TCPCMD_SET_EQ:
                break;
            case TCPCMD_GET_EQ_SWITCH:
                break;
        }
        return "";
    }


    private static void dealWith(String result, UdpMsg udpMsg) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        switch (udpMsg.getSourceDataString()) {
            case "-1": //error
                break;
            case "0"://normal
                break;
            case "1"://repeat all
                break;
            case "2"://repeat one
                break;
            case "3":// shuffle
                break;

        }
    }


}
