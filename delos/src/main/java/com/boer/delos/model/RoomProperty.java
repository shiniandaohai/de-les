package com.boer.delos.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaolong on 2017/4/2.
 */
public class RoomProperty  implements Serializable{


    /**
     * room : {"name":"xxx","type":"客厅","roomId":"x"}
     * deviceList : [{"addr":"xxxxx","type":"Light1","roonname":"xxxx","roomId":"xx","name":"devName","note":"","dismiss":false,"X":"400","Y":"400"}]
     */

    private RoomBean room;
    private List<DeviceListBean> deviceList;

    public RoomBean getRoom() {
        return room;
    }

    public void setRoom(RoomBean room) {
        this.room = room;
    }

    public List<DeviceListBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceListBean> deviceList) {
        this.deviceList = deviceList;
    }

    public static class RoomBean  implements Serializable {
        /**
         * name : xxx
         * type : 客厅
         * roomId : x
         */

        private String name;
        private String type;
        private String roomId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }
    }

    public static class DeviceListBean  implements Serializable{
        /**
         * addr : xxxxx
         * type : Light1
         * roonname : xxxx
         * roomId : xx
         * name : devName
         * note :
         * dismiss : false
         * X : 400
         * Y : 400
         */

        private String addr;
        private String type;
        private String roomname;
        private String roomId;
        private String name;
        private String note;
        private boolean dismiss;
        private String X;
        private String Y;

        public String getFavorite() {
            return favorite;
        }

        public void setFavorite(String favorite) {
            this.favorite = favorite;
        }

        private String favorite = "0";

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRoomname() {
            return roomname;
        }

        public void setRoomname(String roomname) {
            this.roomname = roomname;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public boolean isDismiss() {
            return dismiss;
        }

        public void setDismiss(boolean dismiss) {
            this.dismiss = dismiss;
        }

        public String getX() {
            return X;
        }

        public void setX(String X) {
            this.X = X;
        }

        public String getY() {
            return Y;
        }

        public void setY(String Y) {
            this.Y = Y;
        }
    }
}
