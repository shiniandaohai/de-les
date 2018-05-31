package com.boer.delos.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author wangkai
 * @Description: JSONObject解析工具类
 * create at 2015/11/13 8:54
 */

public class JsonUtil {
    public static final int TYPE_STRING = 1;
    public static final int TYPE_INT = 2;
    public static final int TYPE_DOUBLE = 3;

    /**
     * 解析Json数据中的state值，返回true/false
     *
     * @param json 需要解析的Json数据
     * @return 如果解析得到State为0，返回true，如果解析得到State为-1，返回false；
     */
    public static boolean parseStateCode(String json) {
        int state;
        if (StringUtil.isEmpty(json)) {
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            state = jsonObject.getInt("ret");
            return (state == 0);
        } catch (JSONException e) {
            L.e(e.toString());
        }
        return false;
    }

    public static int parseInt(String json, String tag) {
        int state;
        if (StringUtil.isEmpty(json)) {
            return 0;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            state = jsonObject.getInt(tag);
            return state;
        } catch (JSONException e) {
            L.e(e.toString());
        }
        return 0;
    }

    public static String parseString(String json, String tag) {
        String state = null;
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            state = jsonObject.getString(tag);
            return state;
        } catch (JSONException e) {
            L.e(e.toString());
        }
        return null;
    }

    /**
     * 解析Json数据中的FieldMessage字段
     *
     * @param json 需要解析的Json数据
     */
    public static String ShowMessage(String json) {
        String fieldMessage = null;
        if (!StringUtil.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                fieldMessage = jsonObject.getString("msg");
                return fieldMessage;
            } catch (JSONException e) {
                L.e(e.toString());
            }
        }
        return null;
    }


    /**
     * 当Json中data字段为List时调用的解析方法
     *
     * @param json 需要解析的Json数据
     * @return List.toString();
     */
    public static <T> List<T> parseDataList(String json, Class<T> cls, String tag) {
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray(tag);
            if (jsonArray != null) {
                return GsonUtil.getList(jsonArray.toString(), cls);
            }
        } catch (JSONException e) {
            L.e(e.toString());
        }
        return null;
    }


    /**
     * 当Json中data字段为String时调用的解析方法
     *
     * @param json 需要解析的Json数据
     * @return
     */

    public static String parseDataString(String json) {
        String data;
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            data = jsonObject.getString("data");
            return data;
        } catch (JSONException e) {
            L.e(e.toString());
        }
        return null;
    }

    /**
     * 当Json中data字段为Object时调用的解析方法
     *
     * @param json 需要解析的Json数据
     * @return
     */
    public static <T> T parseDataObject(String json, Class<T> cls, String tag) {
        String data;
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json).getJSONObject(tag);
            data = jsonObject.toString();
            if (StringUtil.isEmpty(data)) {
                return null;
            }
            return GsonUtil.getObject(data, cls);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object parseDataOne(String json, String tag, int type) {
        String data;
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(json).getJSONObject("data");
            data = jsonObject.toString();
            if (StringUtil.isEmpty(data)) {
                return null;
            }
            if (type == TYPE_STRING) {
                return jsonObject.getString(tag);
            } else if (type == TYPE_INT) {
                return jsonObject.getInt(tag);
            }
        } catch (JSONException e) {
            L.e(e.toString());
        }
        return null;
    }
}

