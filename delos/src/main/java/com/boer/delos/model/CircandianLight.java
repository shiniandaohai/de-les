package com.boer.delos.model;

import java.util.List;

/**
 * Created by gaolong on 2017/5/19.
 */
public class CircandianLight {


    /**
     * addr : KS00000591
     * Level : 65535
     * IsVirtual : false
     * SerialNumber : KS00000591
     * Logical : 8
     * name : 节律灯
     * Buttons : [{"Active":false,"Position":0,"Level":65535,"Name":"昼夜节律","Id":"60f16b5c-1d75-4b96-9712-7d3546de5c72"},{"Active":false,"Position":1,"Level":65535,"Name":"增加体能","Id":"d2b3f4e7-a835-4ccd-aee2-70f1bd372455"},{"Active":false,"Position":2,"Level":65535,"Name":"放松","Id":"a4962e21-5193-4891-a813-14db8dd9a7b7"},{"Active":false,"Position":3,"Level":65535,"Name":"娱乐","Id":"3723a299-7738-43f3-b1b1-b951be1dc9e2"},{"Active":true,"Position":4,"Level":65535,"Name":"关","Id":"ce9f0747-57d2-4d97-8980-ffb6bea8b334"}]
     * n4SerialNo : KP00001622
     * type : CircadianLight
     * Id : e9c684bc-598b-4c59-802f-4b95cb502210
     * Name : KS00000591
     */

    private String addr;
    private int Level;
    private boolean IsVirtual;
    private String SerialNumber;
    private int Logical;
    private String name;
    private String n4SerialNo;
    private String type;
    private String Id;
    private String Name;
    private List<ButtonsBean> Buttons;
    private String roomId;
    private String roomname;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int Level) {
        this.Level = Level;
    }

    public boolean isIsVirtual() {
        return IsVirtual;
    }

    public void setIsVirtual(boolean IsVirtual) {
        this.IsVirtual = IsVirtual;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String SerialNumber) {
        this.SerialNumber = SerialNumber;
    }

    public int getLogical() {
        return Logical;
    }

    public void setLogical(int Logical) {
        this.Logical = Logical;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getN4SerialNo() {
        return n4SerialNo;
    }

    public void setN4SerialNo(String n4SerialNo) {
        this.n4SerialNo = n4SerialNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<ButtonsBean> getButtons() {
        return Buttons;
    }

    public void setButtons(List<ButtonsBean> Buttons) {
        this.Buttons = Buttons;
    }

    public static class ButtonsBean {
        /**
         * Active : false
         * Position : 0
         * Level : 65535
         * Name : 昼夜节律
         * Id : 60f16b5c-1d75-4b96-9712-7d3546de5c72
         */

        private boolean Active;
        private int Position;
        private int Level;
        private String Name;
        private String Id;

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

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }
    }
}
