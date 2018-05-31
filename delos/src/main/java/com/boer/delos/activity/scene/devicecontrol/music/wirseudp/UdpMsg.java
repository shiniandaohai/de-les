package com.boer.delos.activity.scene.devicecontrol.music.wirseudp;

/**
 */
public class UdpMsg extends BaseMsg {

    public UdpMsg(byte[] data, TargetInfo target, MsgType type) {
        super(data, target, type);
    }

    public UdpMsg(String data, TargetInfo target, MsgType type) {
        super(data, target, type);
    }

    public UdpMsg(int id) {
        super(id);
    }
}
