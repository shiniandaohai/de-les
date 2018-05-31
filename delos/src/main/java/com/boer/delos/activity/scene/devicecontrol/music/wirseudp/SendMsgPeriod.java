package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.boer.delos.commen.BaseApplication;
import com.squareup.leakcanary.RefWatcher;

import java.lang.ref.WeakReference;


/**
 * Created by sunzhibin on 2017/8/21.
 * 发送查询音乐状态的UDP广播
 */

public class SendMsgPeriod implements IWirseUdpCMD {
    private MyHandler mHandler = new MyHandler();
    private int sendPeriod = 1000 * 3;
    private XUdp mXUdp;
    private UdpClientListener listener;
    private String mMusicServerIP;
    private TargetInfo mTargetInfo;

    public SendMsgPeriod() {
    }

    public void start(UdpClientListener listener, int... cmd) {
        this.listener = listener;
        for (int i : cmd)
            mHandler.sendEmptyMessage(i);
    }

    public void startPeriod(UdpClientListener listener, int... cmd) {
        this.listener = listener;
        int index = 0;
        for (int i : cmd) {
            Message message = Message.obtain();
            message.arg1 = 100;
            message.what = i;
            mHandler.sendMessage(message);
            index++;
        }
    }

    private void sendMsg(byte[] cmd) {
        if (mXUdp == null) {
            mXUdp = XUdp.getUdpClient();
            mXUdp.addUdpClientListener(listener);
            mXUdp.config(new UdpClientConfig.Builder()
                    .setLocalPort(11001).create());
        }
        if (TextUtils.isEmpty(mMusicServerIP)) {
            mTargetInfo = new TargetInfo("255.255.255.255", 11000);
        } else {
            mTargetInfo.setIp(mMusicServerIP);
        }

        mXUdp.sendMsg(new UdpMsg(cmd, mTargetInfo, BaseMsg.MsgType.Send), true);


    }

    private void clearAll() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mXUdp != null)
            mXUdp.clear();
        listener = null;
        mXUdp = null;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TCPCMD_PALPITANT:
                    //暂不做处理、返回设备名--BOER-7C-TFANBTR
                    sendMsg(ControlBytes.getInstance().getWiseDevice());
                    break;
                case TCPCMD_PLAY_PAUSE:
                    //不做处理
                    sendMsg(ControlBytes.getInstance().wiseMusicPlayOrPause());
                    break;
                case TCPCMD_PRE:
                    //收到数据不做处理
                    sendMsg(ControlBytes.getInstance().wiseMusicPrevious());
                    break;
                case TCPCMD_NEXT:
                    //
                    sendMsg(ControlBytes.getInstance().wiseMusicNext());
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
                    sendMsg(ControlBytes.getInstance().wisePlayStatus());
                    break;

                case TCPCMD_DURATION: //获取当前播放歌曲时长

                    // 带参数返回毫秒级时间数字
                    sendMsg(ControlBytes.getInstance().wiseMusicDuration());
                    break;
                case TCPCMD_POSITION:
                    //   带参数返回毫秒级时间数字
//                seekBar_play.setProgress();
                    sendMsg(ControlBytes.getInstance().wiseMusicPosition());
                    break;
                case TCPCMD_SONGNAME:
//                带参数返回歌曲名字
                    sendMsg(ControlBytes.getInstance().wiseMusicSongName());
                    break;
                case TCPCMD_ROOMNAME:
//                带参数返回房间名和房间号，用"::"分隔
                    sendMsg(ControlBytes.getInstance().wiseMusicRoomName());
                    break;
                case TCPCMD_PROGRESS://设置进度条,需带参数，参数为时间，毫秒级。
                    sendMsg(ControlBytes.getInstance().wiseMusicSetProgress());
                    break;

                case TCPCMD_MEDIA_LIST_SIZE:
                    sendMsg(ControlBytes.getInstance().wiseMusicMediaListSize());
                    break;

                case TCPCMD_PLAY_POS:
//                    sendMsg(ControlBytes.getInstance().wiseMusicPlayChooseSong());

                    break;
                case TCPCMD_ARTIST:
                    sendMsg(ControlBytes.getInstance().wiseMusicGetArtist());

                    break;
                case TCPCMD_VOL_SET:
                    break;
                case TCPCMD_VOL_GET:
                    sendMsg(ControlBytes.getInstance().wiseMusicGetVolume());

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
                    sendMsg(ControlBytes.getInstance().wiseMusicGetEffect());
                    break;
            }
            if (msg.arg1 == 100)
                mHandler.sendEmptyMessageDelayed(msg.what, sendPeriod);
        }

    }

    public void stopAll() {
        clearAll();
    }

    public void stop(byte... cmd) {
        for (int i : cmd)
            mHandler.removeMessages(i);
    }

    public int getSendPeriod() {
        return sendPeriod;
    }

    public void setSendPeriod(int sendPeriod) {
        this.sendPeriod = sendPeriod;
    }


    public String getmMusicServerIP() {
        return mMusicServerIP;
    }

    public void setmMusicServerIP(String mMusicServerIP) {
        this.mMusicServerIP = mMusicServerIP;
    }

}
