package com.boer.delos.request.smartdoorbell;

import com.eques.icvss.api.ICVSSUserInstance;

/**
 * Created by Administrator on 2018/1/16.
 */

public class SmartDoorBellController {
    private static SmartDoorBellController instance;
    private static final String DISTRIBUTE_URL = "thirdparty.ecamzone.cc:8443";
    private static ICVSSUserInstance icvss;
    private SmartDoorBellController(){
//        icvss = ICVSSUserModule.getInstance(this).getIcvss();
    }
    public SmartDoorBellController getInstance(){
        if(instance==null){
            instance=new SmartDoorBellController();
        }
        return instance;
    }


}
