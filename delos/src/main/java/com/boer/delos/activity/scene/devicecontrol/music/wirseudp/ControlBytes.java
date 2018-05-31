package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.boer.delos.utils.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sunzhibin on 2017/8/17.
 */

public class ControlBytes implements IWirseUdpCMD {
    private static ControlBytes instance;

    private static class SingletonHolder {
        private static final ControlBytes INSTANCE = new ControlBytes();
    }

    private ControlBytes() {
    }

    public static final ControlBytes getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 广播查找设备IP
     *
     * @return
     */
    public byte[] getWiseDevice() {
        return getDataWithByte(TCPCMD_PALPITANT);
    }

    /**
     * 播放状态
     *
     * @return
     */
    public byte[] wisePlayStatus() {
        return getDataWithByte(TCPCMD_PLAYSTATUS);
    }

    /**
     * //暂停或播放
     *
     * @return
     */
    public byte[] wiseMusicPlayOrPause() {
        return getDataWithByte(TCPCMD_PLAY_PAUSE);
    }

    /**
     * //上一首
     *
     * @return
     */
    public byte[] wiseMusicPrevious() {
        return getDataWithByte(TCPCMD_PRE);
    }

    /**
     * //下一首
     *
     * @return
     */
    public byte[] wiseMusicNext() {
        return getDataWithByte(TCPCMD_NEXT);
    }

    /**
     * 关机
     *
     * @return
     */
    public byte[] wiseMusicShutDown() {
        return getDataWithByte(TCPCMD_SHUTDOWN);
    }

    /**
     * //获取当前播放歌曲时长
     *
     * @return
     */
    public byte[] wiseMusicDuration() {
        return getDataWithByte(TCPCMD_DURATION);
    }

    /**
     * //获取当前播放进度
     *
     * @return
     */
    public byte[] wiseMusicPosition() {
        return getDataWithByte(TCPCMD_POSITION);
    }

    /**
     * 获取当前播放歌曲名字
     *
     * @return
     */
    public byte[] wiseMusicSongName() {
        return getDataWithByte(TCPCMD_SONGNAME);
    }

    /**
     * //获取房间号
     *
     * @return
     */
    public byte[] wiseMusicRoomName() {
        return getDataWithByte(TCPCMD_ROOMNAME);
    }

    /**
     * //设置进度条
     * :0x7e0x7e0x00x80xcc0x750x660x00x00x10xd0xa
     *
     * @return
     */
    public byte[] wiseMusicSetProgress() {
        return getDataWithByte(TCPCMD_PROGRESS);
    }

    /**
     * //返回歌曲总数
     *
     * @return
     */
    public byte[] wiseMusicMediaListSize() {
        return getDataWithByte(TCPCMD_MEDIA_LIST_SIZE);
    }

    /**
     * //获取当前歌曲艺术家
     *
     * @return
     */
    public byte[] wiseMusicGetArtist() {
        return getDataWithByte(TCPCMD_ARTIST);
    }

    /**
     * //设置音量
     *
     * @return
     */
    public byte[] wiseMusicSetVolume() {
        return getDataWithByte(TCPCMD_VOL_SET);
    }

    /**
     * //获取音量
     *
     * @return
     */
    public byte[] wiseMusicGetVolume() {
        return getDataWithByte(TCPCMD_VOL_GET);
    }

    /**
     * //获取音效
     *
     * @return
     */
    public byte[] wiseMusicGetEffect() {
        return getDataWithByte(TCPCMD_GET_EQ);
    }


    //获取歌曲列表
    public byte[] wiseMusicGetList(int index) {
        return getSongListWithIndex(index);
    }

    /**
     * //设置音量0是减音量,1是加音量
     *
     * @param i
     * @return
     */
    public byte[] setMusicVolume(int i) {
        byte ch1 = 0x7e;
        byte ch2 = 0x7e;
        byte ch3 = 0x0;
        byte ch4 = 0x5;
        byte ch5 = TCPCMD_VOL_CTRL;//(byte) 0xc5;
        byte ch6 = (byte) ((i == 0) ? 0x30 : 0x31);//i == 0 ? (ch6 = 0x30) : (ch6 = 0x31);
        byte ch7 = 0x1;
        byte ch8 = 0xd;
        byte ch9 = 0xa;
        byte ch[] = {ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9};
        return ch;
    }

    /**
     * //设置音效
     *
     * @param i 1->"普通/Normal"
     *          2->"摇滚/Rock"
     *          3->"流行/Pop"
     *          4->"舞曲/Dance"
     *          5->"嘻哈/Hip-Hop"
     *          6->"古典/Classic"
     *          7->"超重低音/Bass"
     *          8->"人声/Vocal"
     * @return
     */
    public byte[] setMusicEffect(int i) {
        byte ch1 = 0x7e;
        byte ch2 = 0x7e;
        byte ch3 = 0x0;
        byte ch4 = 0x8;
        byte ch5 = TCPCMD_SET_EQ;//(byte) 0xdf;
        byte ch6 = (byte) i;
        byte ch7 = 0;
        byte ch8 = 0;
        byte ch9 = 0;
        byte ch10 = 0x1;
        byte ch11 = 0xd;
        byte ch12 = 0xa;
        byte ch[] = {ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10, ch11, ch12};
        return ch;
    }

    /**
     * //设置进度条
     *
     * @param progress
     * @return
     */
    public byte[] setMusicProgress(int progress) {
        //0x75 0x66 0x0 0x0
        StringBuffer stringBuffer = new StringBuffer();
        String hex = Integer.toHexString(progress);
        if (hex.length() != 8) {
            int length = hex.length();
            for (int i = 0; i < 8 - length; i++) {
                hex = "0" + hex;
            }
        }
        byte[] bytes = new byte[4];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < chars.length - 1; i = i + 2) {
            int temp = Integer.parseInt(chars[i] + "" + chars[i + 1], 16);
            bytes[bytes.length - 1 - i / 2 - i % 2] = (byte) temp;
        }
        Log.d("进度条", "十进制：" + progress + " 16进制： " + hex + " cmd " + Arrays.toString(bytes));


        byte ch1 = 0x7e;
        byte ch2 = 0x7e;
        byte ch3 = 0x0;
        byte ch4 = 0x8;
        byte ch5 = TCPCMD_PROGRESS;//(byte) 0xdf;
        byte ch6 = bytes[0];
        byte ch7 = bytes[1];
        byte ch8 = bytes[2];
        byte ch9 = bytes[3];
        byte ch10 = 0x1;
        byte ch11 = 0xd;
        byte ch12 = 0xa;
        byte ch[] = {ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10, ch11, ch12};
        return ch;
    }

    /**
     * CMD 控制位
     *
     * @param open 0： 关 1：开
     * @return
     */
    public byte[] setMusicEffectOpen(int open) {
        byte ch1 = 0x7e;
        byte ch2 = 0x7e;
        byte ch3 = 0x0;
        byte ch4 = 0x8;
        byte ch5 = TCPCMD_SET_EQ_SWITCH;//(byte) 0xdf;
        byte ch6 = (byte) open;
        byte ch7 = 0;
        byte ch8 = 0;
        byte ch9 = 0;
        byte ch10 = 0x1;
        byte ch11 = 0xd;
        byte ch12 = 0xa;
        byte ch[] = {ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10, ch11, ch12};
        return ch;
    }

    /**
     * //播放指定歌曲
     *
     * @param index
     * @return
     */
    public byte[] wiseMusicPlayChooseSong(int index) {
        byte ch1 = 0x7e;
        byte ch2 = 0x7e;
        byte ch3 = 0x0;
        byte ch4 = 0x8;
        byte ch5 = TCPCMD_PLAY_POS;// //(byte) 0xd0
        byte ch6 = (byte) index;
        byte ch7 = 0;
        byte ch8 = 0;
        byte ch9 = 0;
        byte ch10 = 0x1;
        byte ch11 = 0xd;
        byte ch12 = 0xa;
        byte ch[] = {ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10, ch11, ch12};

        return ch;
    }

    /**
     * //切换循环模式
     *
     * @param idGet true: 0x30是获取模式; false: 0x31是切换模式
     * @return
     */
    public byte[] setOrGetLoopMode(boolean idGet) {
        byte ch1 = 0x7e;
        byte ch2 = 0x7e;
        byte ch3 = 0x0;
        byte ch4 = 0x5;
        byte ch5 = TCPCMD_LOOPMODE;//(byte) 0xc4;
        byte ch6 = (byte) (idGet ? 0x30 : 0x31);
        byte ch7 = 0xd;
        byte ch8 = 0xa;
        byte ch[] = {ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8};
        return ch;
    }

    private byte[] getDataWithByte(byte b) {
        byte ch1 = 0x7e;
        byte ch2 = 0x7e;
        byte ch3 = 0x0;
        byte ch4 = 0x4;
        byte ch5 = b;
        byte ch6 = 0x1;
        byte ch7 = 0xd;
        byte ch8 = 0xa;
        byte[] ch = {ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8};
        return ch;
    }

    private byte[] getSongListWithIndex(int index) {

        return new byte[]{};
    }

    private byte[] dataWithBytes(byte[] bytes) {
        byte[] result = new byte[bytes.length];
        int index = 0;
        for (byte b : bytes) {
//            result[index] =
            index++;
        }

        return result;
    }

}
