package com.boer.delos.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.result.TelParsedResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * GsonUtil是Gson解析工具类
 *
 * @author wk
 */
public class GsonUtil {


    /**
     * 使用Gson解析数据成Object
     *
     * @param json 需要解析的Json数据
     * @param cls  类名
     * @return T
     */
    public static <T> T getObject(String json, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 使用Gson解析数据成List<Object>
     *
     * @param json 需要解析的Json数据
     * @param cls  类名
     * @return List<T>
     */
    public static <T> List<T> getList(String json, Class<T> cls) {

        if (TextUtils.isEmpty(json)) {
            return Collections.EMPTY_LIST;
        }

        List<T> list = new ArrayList<T>();
        try {
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement element : array) {
                list.add(new Gson().fromJson(element, cls));
            }
        } catch (Exception e) {
            Loger.d(e.toString());
            L.e(e.toString());
        }
        return list;
    }

    /**
     * 将json数据转成List<Map<String, Object>>
     *
     * @param json 需要解析的Json数据
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getListMaps(String json) {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            maps = gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }

    public static String Object2Json(Object o) {
        try {
            Gson gson = new Gson();
            return gson.toJson(o);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 函数名称: parseData
     * 函数描述: 将json字符串转换为map
     *
     * @param data
     * @return
     */
    public static Map<String, Object> json2Map(String data) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        Map<String, Object> map = g.fromJson(data, new TypeToken<Map<String, Object>>() {
        }.getType());
        return map;
    }


}
