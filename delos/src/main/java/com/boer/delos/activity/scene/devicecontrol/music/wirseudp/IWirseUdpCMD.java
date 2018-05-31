package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;

/**
 * Created by sunzhibin on 2017/8/17.
 */

public interface IWirseUdpCMD {
    byte TCPCMD_PALPITANT = (byte) 0xC0;//心跳包，服务端收到对方的心跳包返回机器型号(如BM209)
    byte TCPCMD_PLAY_PAUSE = (byte) 0xC1;//音乐播放/暂停
    byte TCPCMD_PRE = (byte) 0xC2;//音乐上一首
    byte TCPCMD_NEXT = (byte) 0xC3;//音乐下一首
    byte TCPCMD_LOOPMODE = (byte) 0xC4;//音乐循环模式

    byte TCPCMD_VOL_CTRL = (byte) 0xC5;//音乐播放声音控制
    byte TCPCMD_PLAYSTATUS = (byte) 0xC6;//音乐播放状态
    byte TCPCMD_SHUTDOWN = (byte) 0xC7;//关机
    byte TCPCMD_DURATION = (byte) 0xC8;//获取当前播放歌曲时长
    byte TCPCMD_POSITION = (byte) 0xC9;//获取进度位置

    byte TCPCMD_SONGNAME = (byte) 0xCA;//获取当前播放歌曲
    byte TCPCMD_ROOMNAME = (byte) 0xCB;//返回房间名号，用"::"分隔
    byte TCPCMD_PROGRESS = (byte) 0xCC;//设置进度条 设置进度条,需带参数，参数为时间，毫秒级。
    byte TCPCMD_MEDIATYPE = (byte) 0xCD;//1音乐，2电台, 3：视频,4：图片
    byte TCPCMD_MEDIA_LIST_SIZE = (byte) 0xCE;//返回歌曲总数

    byte TCPCMD_GET_MEDIA_LIST = (byte) 0xCF;//每次取一条，返回歌曲名和播放时长
    byte TCPCMD_PLAY_POS = (byte) 0xD0;//播放列表中的某一位置的歌曲
    byte TCPCMD_ARTIST = (byte) 0xD1;//获取当前歌曲艺术家，仅限音频
    byte TCPCMD_VOL_SET = (byte) 0xD2;//设置音量
    byte TCPCMD_VOL_GET = (byte) 0xD3;//获取音量
    byte TCPCMD_UPDATE_ROOMNAME = (byte) 0xD4;//修改房间名

    byte TCPCMD_UPDATE_ROOMNO = (byte) 0xD5;//修改房间号
    byte TCPCMD_SIWTCH_SOUND_SOURCE = (byte) 0xD6;//获取和设置音源
    byte TCPCMD_GET_TIMER_SIZE = (byte) 0xD7;//获取定时器列表总数，不带参数
    byte TCPCMD_GET_TIMER_DATA = (byte) 0xD8;//获取定时器数据
    byte TCPCMD_SET_TIMER_DATA = (byte) 0xD9;//设置定时器

    byte TCPCMD_GET_TIMER_SWITCH = (byte) 0xDA;//获取定时器开关
    byte TCPCMD_SET_TIMER_SWITCH = (byte) 0xDB;//设置定时器开关
    byte TCPCMD_GET_SUBAREA_CONTROL = (byte) 0xDC;//获取分区控制，仅限机型BM209
    byte TCPCMD_SET_SUBAREA_CONTROL = (byte) 0xDD;//设置分区控制，仅限机型BM209
    byte TCPCMD_GET_EQ = (byte) 0xDE;//不带参数获取当前音效

    byte TCPCMD_SET_EQ = (byte) 0xDF;//设置音效
    byte TCPCMD_GET_EQ_SWITCH = (byte) 0xE0;//获取音效开关
    byte TCPCMD_SET_EQ_SWITCH = (byte) 0xE1;//带参数0或1设置音效开关

    /**
     * 播放模式{@link TCPCMD_LOOPMODE}
     */
    int MPM_ERROR = -1; // error
    int MPM_NORMAL = 0; // normal
    int MPM_REPEAT_ALL = 1; // repeat all
    int MPM_REPEAT_ONE = 2; // repeat one
    int MPM_SHUFFLE = 3; // shuffle
    /**
     * 播放状态{@link TCPCMD_PLAYSTATUS}
     */
     int MPS_NOFILE = -1;// no file
     int MPS_INVALID = 0;// invalid
     int MPS_PLAYING = 1;// play
     int MPS_PAUSE = 2;// pause
     int MPS_STOP = 3;	// pause
     int MPS_PARESYNC = 4;// paresync
     int MPS_PARECOMPLETE = 5;// parecomplete
     int MPS_COMPLETE = 6;// complete


}
