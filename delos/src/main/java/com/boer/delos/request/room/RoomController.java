package com.boer.delos.request.room;

import android.content.Context;

import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Area;
import com.boer.delos.model.Room;
import com.boer.delos.model.RoomProperty;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.StringUtil;

import java.util.HashMap;

/**
 * @author XieQingTing
 * @Description: 房间相关接口
 * create at 2016/5/18 10:01
 */
public class RoomController extends BaseController {
    public static RoomController instance = null;

    public static RoomController getInstance() {
        if (instance == null) {
            synchronized (RoomController.class) {
                if (instance == null) {
                    instance = new RoomController();
                }
            }
        }
        return instance;
    }

    /**
     * 添加或修改房间属性(新增一个信访件或者修改已有房间的属性，如房间名)
     *
     * @param context
     * @param room
     * @param listener
     */
    public void updateRoom(Context context, Room room, RequestResultListener listener) {
        if (room == null) {
            return;
        }
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("room", room);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/update");
//        String url = String.format("http://%s%s", http, "/room/update");
        postRequest(context, url, maps, RequestTag.UPDATE_ROOM, listener);
    }

    /**
     * 删除房间
     *
     * @param context   上下文
     * @param timestamp 时间戳
     * @param roomId    房间Id
     */
    public void removeRoom(Context context, int timestamp, String roomId, RequestResultListener listener) {
        if (StringUtil.isEmpty(timestamp + "") || StringUtil.isEmpty(roomId)) {
            return;
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("timestamp", timestamp + "");
        maps.put("roomId", roomId);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/remove");
        postRequest(context, url, maps, RequestTag.REMOVE_ROOM, listener);
    }


    /**
     * 查询房间已有的模式
     *
     * @param context  上下文
     * @param name     全局模式名称
     * @param roomId   房间ID
     * @param listener 回调接口
     */
    public void showMode(Context context, String name, String roomId, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        if (name != null) {
            maps.put("name", name);
        }
        if (roomId != null) {
            maps.put("roomId", roomId);
        }

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/showmode");
        postRequest(context, url, maps, RequestTag.SHOWROOMMODEL, listener);
    }

    /**
     * 查询房间属性
     *
     * @param context
     * @param roomId   房间id
     * @param listener
     */
    public void roomShow(Context context, String roomId, RequestResultListener listener) {
        if (StringUtil.isEmpty(roomId)) {
            return;
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("roomId", roomId);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/show");
        postRequest(context, url, maps, RequestTag.ROOMSHOW, listener);
    }

    /**
     * 房间区域更新 TODO
     *
     * @param context
     * @param hostId
     * @param area
     * @param listener
     */
    public void areaUpdate(Context context, String hostId, Area area, RequestResultListener listener) {
        if (StringUtil.isEmpty(hostId)) {
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("area", area);
        map.put("hostId", hostId);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/updatearea");
        postRequest(context, url, map, RequestTag.AREAUPDATE, listener);
    }

    /**
     * 房间区域删除 TODO
     *
     * @param context
     * @param roomId
     * @param areaId
     * @param listener
     */
    public void areaDelete(Context context, String roomId, String areaId, RequestResultListener listener) {
        if (StringUtil.isEmpty(areaId) || StringUtil.isEmpty(roomId)) {
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("areaId", areaId);
        map.put("roomId", roomId);

        String http = Constant.IS_LOCAL_CONNECTION ? "http://" + Constant.LOCAL_CONNECTION_IP + ":8080" : URLConfig.HTTP;
        String url = String.format("%s%s", http, "/room/removearea");
        postRequest(context, url, map, RequestTag.AREA_DELETE, listener);
    }



    /**
     * delos添加或修改或新增房间和设备
     */
    public void updateRoomProperty(Context context, RoomProperty roomProperty, RequestResultListener listener) {
        if (roomProperty == null) {
            return;
        }
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("deviceList", roomProperty.getDeviceList());
        maps.put("room", roomProperty.getRoom());

        String url = splitHttpUrl("/room/update", "1017", Constant.LOCAL_CONNECTION_IP, false, true, true);

        postRequest(context, url, maps, RequestTag.UPDATE_ROOM, listener);
    }



}
