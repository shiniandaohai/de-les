package com.boer.delos.utils;

/**
 * @author: sunzhibin
 * @e-mail:
 * @Description:
 * @Date 2016/11/20 0020 ${HORT}:27
 */


public class BytesArray2HexString {
    // byte数组转换成16进制字符串String：

    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte数组转换成16进制字符数组
     *
     * @param src
     * @return
     */
    public static String[] bytesToHexStrings(byte[] src) {

        if (src == null || src.length <= 0) {
            return null;

        }
        String[] str = new String[src.length];
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                str[i] = "0";
            }
            str[i] = hv;
        }
        return str;
    }

}
