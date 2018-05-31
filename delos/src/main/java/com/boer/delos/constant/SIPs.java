package com.boer.delos.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description: 安徽六安碧桂园SIP
 * @CreateDate: 2017/2/10 0010 13:35
 * @Modify:
 * @ModifyDate:
 */


public class SIPs {
    /*
       "051008010300010101011"
       "051008010300010102011"
       "051008010300010103011"
       "051008010300010104011"
       "051008010300010105011"
       "051008010300010106011"
       */
    private static int SIP_SIZE = 6;
    static List<String> listSIPs = new ArrayList<>(SIP_SIZE);

    public static List<String> getSIPs() {
        if (listSIPs != null && listSIPs.size() == SIP_SIZE) {
            return listSIPs;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < SIP_SIZE; i++) {
            stringBuilder.setLength(0);

            stringBuilder.append("05100801030001010");
            stringBuilder.append(i);
            stringBuilder.append("011");

            listSIPs.add(stringBuilder.toString());
        }
        return listSIPs;
    }


}
