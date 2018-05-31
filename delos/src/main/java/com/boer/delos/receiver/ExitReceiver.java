package com.boer.delos.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zhukang on 16/7/13.
 */
public class ExitReceiver extends BroadcastReceiver {

    private Context mContext;

    public ExitReceiver(Context context){
        super();
        this.mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((Activity)mContext).finish();
    }
}
