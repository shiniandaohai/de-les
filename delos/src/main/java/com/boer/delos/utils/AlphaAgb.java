package com.boer.delos.utils;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/16 0016 16:15
 * @Modify:
 * @ModifyDate:
 */


public class AlphaAgb {
    public static void rgba(float alphaI) throws Exception {
        System.out.println("透明度 | 十六进制");
        System.out.println("---- | ----");
//        for (double i = 1; i >= 0; i -= 0.01) {
//            i = Math.round(i * 100) / 100.0d;
            int alpha = (int) Math.round(alphaI * 255);
            String hex = Integer.toHexString(alpha).toUpperCase();
            if (hex.length() == 1) hex = "0" + hex;
            int percent = (int) (alphaI * 100);
            System.out.println(String.format("%d%% | %s", percent, hex));
//        }
    }
}
