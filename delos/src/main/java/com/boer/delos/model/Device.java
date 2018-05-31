package com.boer.delos.model;


import com.boer.delos.utils.gson.TypeAdapterString2boolean;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Observer;

public class Device implements java.io.Serializable, Comparable {
    private static final long serialVersionUID = -6155124745185982446L;
    private String X;
    private String Y;
    private String addr;
    private String areaId;
    private String areaname;
    @JsonAdapter(TypeAdapterString2boolean.class)
    private boolean dismiss;// 是否解除绑定，false:未解除；true:解除；默认为false
    private GuardInfo guardInfo;
    private String keyId;
    private String name;
    private String note;
    private String roomId;
    private String roomname;
    private Long timestamp;
    private String type;
    private Map<String, String> lightName;
    String favorite = "0"; //常用设备标记，1-常用，0-非常用
    //addBatch
    private String hardwareVer;
    private String softwareVer;
    private String id;

    //背景音乐用到
    private String brand;
    private Map<String, Object> songDict;
    private String setSong;
    private String setVolume;
    //节律灯用到
    private String SerialNumber;
    private String n4SerialNo;
    private String Name;
    private String Level;
    private String IsVirtual;
    private String Logical;
    private List<ButtonsBean> Buttons;

    //N4
//    private String areaId;
//    private String keyId;
//    private String hardwareVer;
//    private String addr;
//    private String timestamp;
//    private String dismiss;
//    private String favorite;
    private String n4IP; //": "192.168.16.129",
//    private String softwareVer;//": "",
//    private String note; //": "",
//    private String roomId;//": "-1",
//    private String type; // ": "N4",
//    private String name; //": "N4"


    public String getSetVolume() {
        return setVolume;
    }

    public void setSetVolume(String setVolume) {
        this.setVolume = setVolume;
    }

    public String getSetSong() {
        return setSong;
    }

    public void setSetSong(String setSong) {
        this.setSong = setSong;
    }

    public String getBrand() {
        if(brand==null)brand="";
        return brand;
    }

    public Map<String, Object> getSongDict() {
        return songDict;
    }

    public void setSongDict(Map<String, Object> songDict) {
        this.songDict = songDict;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<ButtonsBean> getButtons() {
        return Buttons;
    }

    public void setButtons(List<ButtonsBean> buttons) {
        Buttons = buttons;
    }

    public String getN4IP() {
        return n4IP;
    }

    public void setN4IP(String n4IP) {
        this.n4IP = n4IP;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isDismiss() {
        return dismiss;
    }

    public void setDismiss(boolean dismiss) {
        this.dismiss = dismiss;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getN4SerialNo() {
        return n4SerialNo;
    }

    public void setN4SerialNo(String n4SerialNo) {
        this.n4SerialNo = n4SerialNo;
    }

    public String getX2Name() {
        return this.Name;
    }

    public void setX2Name(String name) {
        this.name = Name;
    }


    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getIsVirtual() {
        return IsVirtual;
    }

    public void setIsVirtual(String isVirtual) {
        IsVirtual = isVirtual;
    }

    public String getLogical() {
        return Logical;
    }

    public void setLogical(String logical) {
        Logical = logical;
    }

    private transient int updateStatus;//设备的状态变化（绑定到解绑）2无变化 0解绑 1绑定

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getHardwareVer() {
        return hardwareVer;
    }

    public void setHardwareVer(String hardwareVer) {
        this.hardwareVer = hardwareVer;
    }

    public String getSoftwareVer() {
        return softwareVer;
    }

    public void setSoftwareVer(String softwareVer) {
        this.softwareVer = softwareVer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private transient boolean isChecked;  //选中标识

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int compareTo(Object another) {
        Device tempDevice = (Device) another;
        if (this.getAddr().equals(tempDevice.getAddr())) {
            return 0;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this.getClass() == o.getClass()) {
            Device tempDevice = (Device) o;
            if (this.getAddr().equals(tempDevice.getAddr())) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> getLightName() {
        return lightName;
    }

    public void setLightName(Map<String, String> lightName) {
        this.lightName = lightName;
    }

    //add by guwenqiang
    private RemoteCMatchData remoteInfo;
    private RCAirConditionCmdData AcData;

    public transient int n_gcm_count = 0;

    //add by sunzhibin Pannel用到
    public ModecfgBean modecfg;
    private String protocol;

    public static class ModecfgBean implements Serializable {
        @SerializedName("1")
        private String value1;
        @SerializedName("3")
        private String value3;
        @SerializedName("2")
        private String value2;
        @SerializedName("4")
        private String value4;

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue3() {
            return value3;
        }

        public void setValue3(String value3) {
            this.value3 = value3;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        public String getValue4() {
            return value4;
        }

        public void setValue4(String value4) {
            this.value4 = value4;
        }
    }

    public RCAirConditionCmdData getAcData() {
        return AcData;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public ModecfgBean getModecfg() {
        return modecfg;
    }

    public void setModecfg(ModecfgBean modecfg) {
        this.modecfg = modecfg;
    }

//    public Map<String, String> getModecfg() {
//        return modecfg;
//    }
//
//    public void setModecfg(Map<String, String> modecfg) {
//        this.modecfg = modecfg;
//    }

    public void setAcData(RCAirConditionCmdData acData) {
        AcData = acData;
    }

    public int getN_gcm_count() {
        return n_gcm_count;
    }

    public void setN_gcm_count(int n_gcm_count) {
        this.n_gcm_count = n_gcm_count;
    }

    public RemoteCMatchData getRemoteInfo() {
        return remoteInfo;
    }

    public void setRemoteInfo(RemoteCMatchData remoteInfo) {
        this.remoteInfo = remoteInfo;
    }

    public RCAirConditionCmdData getRcAirConditionCmdData() {
        return AcData;
    }

    public void setRcAirConditionCmdData(RCAirConditionCmdData rcAirConditionCmdData) {
        this.AcData = rcAirConditionCmdData;
    }

    public GuardInfo getGuardInfo() {
        return guardInfo;
    }

    public void setGuardInfo(GuardInfo guardInfo) {
        this.guardInfo = guardInfo;
    }

    public Device() {

    }

    public Device(String roomname) {
        this.roomname = roomname;
    }

    public String getAreaname() {
        return this.areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAreaId() {
        return this.areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRoomname() {
        return this.roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getY() {
        return this.Y;
    }

    public void setY(String Y) {
        this.Y = Y;
    }

    public String getX() {
        return this.X;
    }

    public void setX(String X) {
        this.X = X;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public Boolean getDismiss() {
        return this.dismiss;
    }


    //节律灯用到
    public static class ButtonsBean implements Serializable {
        /**
         * Active : false
         * Position : 0
         * Level : 65535
         * Id : 60f16b5c-1d75-4b96-9712-7d3546de5c72
         * Name : 昼夜节律
         */

        private boolean Active;
        private int Position;
        private int Level;
        private String Id;
        private String Name;

        public boolean isActive() {
            return Active;
        }

        public void setActive(boolean Active) {
            this.Active = Active;
        }

        public int getPosition() {
            return Position;
        }

        public void setPosition(int Position) {
            this.Position = Position;
        }

        public int getLevel() {
            return Level;
        }

        public void setLevel(int Level) {
            this.Level = Level;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }


}
