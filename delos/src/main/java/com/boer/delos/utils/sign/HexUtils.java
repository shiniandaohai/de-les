package com.boer.delos.utils.sign;

/**
 * Created by zhukang on 16/5/21.
 */
public class HexUtils {

    /**
     * 将字节转换成16进制字符串
     * @param bytes
     * @return
     */
    public final static String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toLowerCase());
        }
        return sb.toString();
    }


    /**
     * hex字符串转byte数组<br/>
     * 2个hex转为一个byte
     *
     * @param src
     * @return
     */
    public final static byte[] HexStringTobytes(String src) {
        byte[] res = new byte[src.length() / 2];
        char[] chs = src.toCharArray();
        int[] b = new int[2];

        for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
            for (int j = 0; j < 2; j++) {
                if (chs[i + j] >= '0' && chs[i + j] <= '9') {
                    b[j] = (chs[i + j] - '0');
                } else if (chs[i + j] >= 'A' && chs[i + j] <= 'F') {
                    b[j] = (chs[i + j] - 'A' + 10);
                } else if (chs[i + j] >= 'a' && chs[i + j] <= 'f') {
                    b[j] = (chs[i + j] - 'a' + 10);
                }
            }

            b[0] = (b[0] & 0x0f) << 4;
            b[1] = (b[1] & 0x0f);
            res[c] = (byte) (b[0] | b[1]);
        }

        return res;
    }



}
