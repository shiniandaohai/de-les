package com.boer.delos.activity.healthylife.skintest;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2015/8/31.
 */
public class BigDecimalUtil {
    public static boolean checkStringNull(String dataStr) {
        return dataStr == null || "".equals(dataStr) || "null".equals(dataStr) || "NULL".equals(dataStr) || "Null".equals(dataStr);
    }
    public static Double doubleScale(Double value, Integer scale )
    {
        BigDecimal mData = new BigDecimal(value.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP);
        return mData.doubleValue();
    }
}

