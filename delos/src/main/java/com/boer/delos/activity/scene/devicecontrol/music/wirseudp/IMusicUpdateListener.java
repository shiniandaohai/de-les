package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;

/**
 * Created by sunzhibin on 2017/8/21.
 */

public interface IMusicUpdateListener {

    void musicName(String name); //音乐名字

    //
    void musicDuration(int duration); //时间总长

    //
    void musicPosition(int position);//进度位置

    //
    void musicArticle(String article);//

    //
    void musicMode(String level);//声音音效

    //
    void musicLoopMode(String level);//循环模式

    void musicPlayState(boolean play);//播放状态

    void musicVolume(String volume);//音量

}
