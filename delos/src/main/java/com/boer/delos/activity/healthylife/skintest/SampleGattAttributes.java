/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boer.delos.activity.healthylife.skintest;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by All on 2016/10/11 0011.
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String SKIN_SERIVCE = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static String SKIN_READ_CHARAC = "0000fff7-0000-1000-8000-00805f9b34fb";
    public static String SKIN_WRITE_CHARAC = "0000fff6-0000-1000-8000-00805f9b34fb";
    
    static {
        // Sample Services.
    	//attributes.put("0000fff0-0000-1000-8000-00805f9b34fb", "my Service");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(SKIN_SERIVCE, "SKIN TEST SERVICE");
        // Sample Characteristics.
        //attributes.put("0000fff5-0000-1000-8000-00805f9b34fb", "my data");
        //attributes.put("0000fff6-0000-1000-8000-00805f9b34fb", "my data1");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put(SKIN_READ_CHARAC, "SKIN TEST READ CHARAC");
        attributes.put(SKIN_WRITE_CHARAC, "SKIN WIRTE READ CHARAC");
    }

    public static String lookup(String uuid, String defaultName) {
    	Log.w("mmyy", "**** received: " + uuid);
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
