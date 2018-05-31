package com.boer.delos.activity.smartdoorbell;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boer.delos.R;
import com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.view.popupWindow.ShowCommonPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_CAPTURE_NUM;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_FORMAT;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_GET_DEVICE_INFO;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_GET_PIR_INFO;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_LIGHT_SWITCH;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_PIR_SWITCH;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_RINGTONE;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_SENSE_SENSITIVITY;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_SENSE_TIME;
import static com.boer.delos.activity.smartdoorbell.imageloader.CmdEventBusEntity.CMD_TYPE_DOOR_BELL_SET_PIR_INFO;
import static com.eques.icvss.utils.Method.ATTR_DOORBELL_RINGTONE;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_CAPTURE_NUM;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_FORMAT;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_SENSE_SENSITIVITY;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_SENSE_TIME;
import static com.eques.icvss.utils.Method.ATTR_SETTINGS_VOLUME;
import static com.eques.icvss.utils.Method.METHOD_ALARM_ENABLE;
import static com.eques.icvss.utils.Method.METHOD_DB_LIGHT_ENABLE;

/**
 * Created by Administrator on 2018/1/15.
 */

public class SmartDoorbellSetupActivity extends CommonBaseActivity {
    @Bind(R.id.tvAlarmTime)
    TextView tvAlarmTime;
    @Bind(R.id.tvSensitivity)
    TextView tvSensitivity;
    @Bind(R.id.tvAlarmMode)
    TextView tvAlarmMode;
    @Bind(R.id.tvSheetCount)
    TextView tvSheetCount;
    @Bind(R.id.tvRingType)
    TextView tvRingType;
    @Bind(R.id.sbVolume)
    SeekBar sbVolume;
    @Bind(R.id.tb0)
    ToggleButton tb0;
    @Bind(R.id.tb1)
    ToggleButton tb1;
    @Bind(R.id.rlSheetCount)
    RelativeLayout rlSheetCount;
    private JSONObject mPirInfo;
    private int requestCount;
    @Override
    protected int initLayout() {
        return R.layout.activity_door_bell_setup;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("设置");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress=seekBar.getProgress();
                int level=progress/10+(progress%10)/5;
                seekBar.setProgress(level*10);
                try {
                    mPirInfo.put(ATTR_SETTINGS_VOLUME,level+1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendCmd(0,CMD_TYPE_DOOR_BELL_SET_PIR_INFO,mPirInfo);
            }
        });
        tb0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd(tb0.isChecked()?1:0, CMD_TYPE_DOOR_BELL_PIR_SWITCH,"");
            }
        });

        tb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCmd(tb1.isChecked()?1:0, CMD_TYPE_DOOR_BELL_LIGHT_SWITCH,"");
            }
        });


        toastUtils.showProgress("");
        sendCmd(0,CMD_TYPE_DOOR_BELL_GET_PIR_INFO,"");
        sendCmd(0,CMD_TYPE_DOOR_BELL_GET_DEVICE_INFO,"");

        sbVolume.setEnabled(false);
    }

    private void showPickerView(final int type, String... strs) {
        ShowCommonPopupWindow showCommonPopupWindow = new ShowCommonPopupWindow(this, strs);
        showCommonPopupWindow.setShowSexPopupWindowInterface(new ShowCommonPopupWindow.ShowSexPopupWindowInterface() {
            @Override
            public void popupDismiss(int position) {

            }

            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String result) {
                int cmd=-1;
                try {
                    switch (type){
                        case CMD_TYPE_DOOR_BELL_SENSE_TIME:
                            cmd=Integer.valueOf(result.replace("秒",""));
                            mPirInfo.put(ATTR_SETTINGS_SENSE_TIME,cmd);
                            break;
                        case CMD_TYPE_DOOR_BELL_SENSE_SENSITIVITY:
                            if(result.equals("高")){
                                cmd=1;
                            }
                            else if(result.equals("低")){
                                cmd=2;
                            }
                            mPirInfo.put(ATTR_SETTINGS_SENSE_SENSITIVITY,cmd);
                            break;
                        case CMD_TYPE_DOOR_BELL_FORMAT:
                            if(result.equals("拍照")){
                                cmd=0;
                            }
                            else if(result.equals("录像")){
                                cmd=1;
                            }
                            mPirInfo.put(ATTR_SETTINGS_FORMAT,cmd);
                            break;
                        case CMD_TYPE_DOOR_BELL_CAPTURE_NUM:
                            cmd=Integer.valueOf(result.replace("张",""));
                            mPirInfo.put(ATTR_SETTINGS_CAPTURE_NUM,cmd);
                            break;
                        case CMD_TYPE_DOOR_BELL_RINGTONE:
                            for(int i=0;i<ringtongStrs.length;i++){
                                if(result.equals(ringtongStrs[i])){
                                    cmd=i+1;
                                    break;
                                }
                            }
                            mPirInfo.put(ATTR_DOORBELL_RINGTONE,cmd);
                            break;
                    }
                    sendCmd(0,CMD_TYPE_DOOR_BELL_SET_PIR_INFO,mPirInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        showCommonPopupWindow.showPopupWindow();
    }

    private String[] ringtongStrs={"你是谁啊","嘟嘟声","警报声","尖叫声","静音"};
    private String[] formatStrs={"拍照","录像"};
    private String[] sensitivityStrs={"高","低"};
    @OnClick({R.id.rlAlarmTime, R.id.rlSensitivity, R.id.rlAlarmMode, R.id.rlSheetCount, R.id.rlRingType})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlAlarmTime:
                String [] strTimes=new String[17];
                for(int i=3;i<=19;i++){
                    strTimes[i-3]=i+"秒";
                }
                showPickerView(CMD_TYPE_DOOR_BELL_SENSE_TIME,strTimes);
                break;
            case R.id.rlSensitivity:
                showPickerView(CMD_TYPE_DOOR_BELL_SENSE_SENSITIVITY,sensitivityStrs);
                break;
            case R.id.rlAlarmMode:
                showPickerView(CMD_TYPE_DOOR_BELL_FORMAT,formatStrs);
                break;
            case R.id.rlSheetCount:
                showPickerView(CMD_TYPE_DOOR_BELL_CAPTURE_NUM,new String[]{"1张","3张","5张"});
                break;
            case R.id.rlRingType:
                showPickerView(CMD_TYPE_DOOR_BELL_RINGTONE,ringtongStrs);
                break;
        }
    }

    private void sendCmd(int cmd,int cmdType,JSONObject jsonObject){
        if(jsonObject==null){
            return;
        }
        CmdEventBusEntity entity = new CmdEventBusEntity();
        entity.setCmd(cmd);
        entity.setCmdType(cmdType);
        entity.setCmdStr(jsonObject.toString());
        entity.setmHandler(mHanlder);
        EventBus.getDefault().post(entity);
    }
    private void sendCmd(int cmd,int cmdType,String cmdStr){
        CmdEventBusEntity entity = new CmdEventBusEntity();
        entity.setCmd(cmd);
        entity.setCmdType(cmdType);
        entity.setCmdStr(cmdStr);
        entity.setmHandler(mHanlder);
        EventBus.getDefault().post(entity);
    }

    Handler mHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CMD_TYPE_DOOR_BELL_LIGHT_SWITCH:
                    int result=(int)msg.obj;
                    if(result!=1){
                        ToastHelper.showShortMsg("操作失败");
                        tb1.setChecked(!tb1.isChecked());
                    }
                    else{
                        tb1.setChecked(tb1.isChecked());
                    }
                    break;
                case CMD_TYPE_DOOR_BELL_PIR_SWITCH:
                    int result0=(int)msg.obj;
                    if(result0!=1){
                        ToastHelper.showShortMsg("操作失败");
                        tb0.setChecked(!tb0.isChecked());
                    }
                    else{
                        tb0.setChecked(tb0.isChecked());
                    }
                    break;
                case CMD_TYPE_DOOR_BELL_GET_PIR_INFO:
                    requestCount++;
                    if(requestCount==2){
                        toastUtils.dismiss();
                    }
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    mPirInfo=jsonObject;
                    int captureNum=jsonObject.optInt(ATTR_SETTINGS_CAPTURE_NUM);
                    int format=jsonObject.optInt(ATTR_SETTINGS_FORMAT);
                    int ringtone=jsonObject.optInt(ATTR_DOORBELL_RINGTONE);
                    int senseSensitivity=jsonObject.optInt(ATTR_SETTINGS_SENSE_SENSITIVITY);
                    int senseTime=jsonObject.optInt(ATTR_SETTINGS_SENSE_TIME);
                    int volume=jsonObject.optInt(ATTR_SETTINGS_VOLUME);
                    tvSheetCount.setText(captureNum+"张");
                    if(format==1){
                        tvSheetCount.setVisibility(View.GONE);
                        rlSheetCount.setEnabled(false);
                    }
                    else{
                        tvSheetCount.setVisibility(View.VISIBLE);
                        rlSheetCount.setEnabled(true);
                    }
                    tvAlarmMode.setText(formatStrs[format]);
                    if(ringtone-1==4){
                        sbVolume.setEnabled(false);
                    }
                    else{
                        sbVolume.setEnabled(true);
                    }
                    tvRingType.setText(ringtongStrs[ringtone-1]);
                    tvSensitivity.setText(sensitivityStrs[senseSensitivity-1]);
                    tvAlarmTime.setText(senseTime+"秒");
                    sbVolume.setProgress((volume-1)*10);
                    break;
                case CMD_TYPE_DOOR_BELL_GET_DEVICE_INFO:
                    requestCount++;
                    if(requestCount==2){
                        toastUtils.dismiss();
                    }
                    JSONObject jsonDeviceInfo=(JSONObject)msg.obj;
                    tb0.setChecked(jsonDeviceInfo.optInt(METHOD_ALARM_ENABLE)==1?true:false);
                    tb1.setChecked(jsonDeviceInfo.optInt(METHOD_DB_LIGHT_ENABLE)==1?true:false);
                    break;
            }
        }
    };
}
