package com.boer.delos.activity.healthylife.skintest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boer.delos.R;
import com.boer.delos.adapter.AirControlAdapter;
import com.boer.delos.adapter.SkinControlAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.AirClean;
import com.boer.delos.model.SkinArea;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.utils.DensityUtil;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.widget.SeekColorBar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/4/17.
 */
public class SkinWaterAndOilActivity extends CommonBaseActivity {

    SkinControlAdapter skinAirAdapter;
    @Bind(R.id.gv_skin_area)
    GridView gvSkinArea;
    @Bind(R.id.seek_color_water)
    SeekColorBar seekColorWater;
    @Bind(R.id.seek_color_oil)
    SeekColorBar seekColorOil;
    @Bind(R.id.btn_reset)
    Button btnReset;
    @Bind(R.id.water_value)
    TextView tvWaterValue;
    @Bind(R.id.oil_value)
    TextView tvOilValue;


    SkinArea skinArea;
    List<AirClean> skinAirCleans;//AirClean模型

    private String mWater_date = "0";
    private String mOil_date = "0";
    private String mElastic_date = "0";

    private int checkedPos = -1;
    HealthController healthController;

    @Override
    protected int initLayout() {
        return R.layout.activity_skin_water_and_oil;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle(R.string.skin_water_oil);
    }

    @Override
    protected void initData() {
        BLEClient.getInstance().setmHandler(mHandler);
        boolean isScanning = BLEClient.getInstance().isScanning();
        boolean isConnect = BLEClient.getInstance().getIsConnect();
        if (!isScanning && !isConnect) {
            BLEClient.getInstance().initBLE(this);
        } else if (BLEClient.getInstance().getIsConnect()) {

        }
        healthController = new HealthController();
        skinAirCleans = new ArrayList<>();
        skinArea = new SkinArea();
        SkinArea.DetailBean detai = new SkinArea.DetailBean();
        skinArea.setDetail(detai);
        for (int i = 0; i < 6; i++) {

            AirClean airClean = new AirClean();

            switch (i) {

                case 0:
                    airClean.setRes(R.mipmap.skin_brow);
                    airClean.setResSelector(R.mipmap.skin_brow_selector);
                    airClean.setName(getString(R.string.skin_brow));
                    break;
                case 1:
                    airClean.setRes(R.mipmap.skin_left_face);
                    airClean.setResSelector(R.mipmap.skin_left_face_selector);
                    airClean.setName(getString(R.string.skin_left_face));
                    break;
                case 2:
                    airClean.setRes(R.mipmap.skin_right_face);
                    airClean.setResSelector(R.mipmap.skin_right_face_selector);
                    airClean.setName(getString(R.string.skin_right_face));
                    break;
                case 3:
                    airClean.setRes(R.mipmap.skin_nose);
                    airClean.setResSelector(R.mipmap.skin_nose_selector);
                    airClean.setName(getString(R.string.skin_nose));
                    break;
                case 4:
                    airClean.setRes(R.mipmap.skin_eye);
                    airClean.setResSelector(R.mipmap.skin_eye_selector);
                    airClean.setName(getString(R.string.skin_eye));
                    break;
                case 5:
                    airClean.setRes(R.mipmap.skin_hand);
                    airClean.setResSelector(R.mipmap.skin_hand_selector);
                    airClean.setName(getString(R.string.skin_hand));
                    break;
            }

            skinAirCleans.add(airClean);
        }


        DensityUtil desityUtil = new DensityUtil(this);
        int size = skinAirCleans.size();  //得到集合长度
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int pxClum = desityUtil.dip2px(10);
        int itemWidth = (width - 5 * pxClum) / 4;
        int gridviewWidth = itemWidth * size + pxClum * (size + 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        gvSkinArea.setLayoutParams(params);
        gvSkinArea.setPadding(pxClum, pxClum, pxClum, pxClum);
        gvSkinArea.setColumnWidth(itemWidth);
        gvSkinArea.setHorizontalSpacing(desityUtil.dip2px(10));
        gvSkinArea.setStretchMode(GridView.NO_STRETCH);
        gvSkinArea.setNumColumns(size);


        skinAirAdapter = new SkinControlAdapter(this, skinAirCleans);
        gvSkinArea.setAdapter(skinAirAdapter);
        skinAirAdapter.notifyDataSetChanged();


        initSelected(0);
    }

    private void initSeekProgress() {

        if (checkedPos == -1)
            return;

        btnReset.setVisibility(View.VISIBLE);


        for (int i = 0; i < 6; i++) {

            switch (checkedPos) {
                case 0:
                    SkinArea.DetailBean.SkinBrowBean brow = new SkinArea.DetailBean.SkinBrowBean();
                    brow.setElastic(mElastic_date);
                    brow.setGrease(mOil_date + "%");
                    brow.setWater(mWater_date + "%");
                    brow.setCompleted(true);
                    skinArea.getDetail().setSkin_brow(brow);
                    break;
                case 1:
                    SkinArea.DetailBean.SkinLeftBean leftFace = new SkinArea.DetailBean.SkinLeftBean();
                    leftFace.setElastic(mElastic_date);
                    leftFace.setGrease(mOil_date + "%");
                    leftFace.setWater(mWater_date + "%");
                    leftFace.setCompleted(true);
                    skinArea.getDetail().setSkin_left(leftFace);
                    break;
                case 2:
                    SkinArea.DetailBean.SkinRightBean rightFace = new SkinArea.DetailBean.SkinRightBean();
                    rightFace.setElastic(mElastic_date);
                    rightFace.setGrease(mOil_date + "%");
                    rightFace.setWater(mWater_date + "%");
                    rightFace.setCompleted(true);
                    skinArea.getDetail().setSkin_right(rightFace);
                    break;
                case 3:
                    SkinArea.DetailBean.SkinNoseBean nose = new SkinArea.DetailBean.SkinNoseBean();
                    nose.setElastic(mElastic_date);
                    nose.setGrease(mOil_date + "%");
                    nose.setWater(mWater_date + "%");
                    nose.setCompleted(true);
                    skinArea.getDetail().setSkin_nose(nose);
                    break;
                case 4:
                    SkinArea.DetailBean.SkinEyeBean eye = new SkinArea.DetailBean.SkinEyeBean();
                    eye.setElastic(mElastic_date);
                    eye.setGrease(mOil_date + "%");
                    eye.setWater(mWater_date + "%");
                    eye.setCompleted(true);
                    skinArea.getDetail().setSkin_eye(eye);
                    break;
                case 5:
                    SkinArea.DetailBean.SkinHandBean hand = new SkinArea.DetailBean.SkinHandBean();
                    hand.setElastic(mElastic_date);
                    hand.setGrease(mOil_date + "%");
                    hand.setWater(mWater_date + "%");
                    hand.setCompleted(true);
                    skinArea.getDetail().setSkin_hand(hand);
                    break;
            }
        }


        Gson gson = new Gson();
        Log.v("gl", "gson.Object2Json(skinArea)==" + gson.toJson(skinArea));

        if (skinArea.getDetail().getSkin_right() != null &&
                skinArea.getDetail().getSkin_nose() != null &&
                skinArea.getDetail().getSkin_left() != null &&
                skinArea.getDetail().getSkin_hand() != null &&
                skinArea.getDetail().getSkin_brow() != null &&
                skinArea.getDetail().getSkin_eye() != null) {

            toastUtils.showProgress("");

            Calendar calendar = Calendar.getInstance();
            skinArea.setFamilyMemberId("0");
            skinArea.setMeasuretime(calendar.getTimeInMillis() / 1000 + "");
            healthController.reportSkinInfo(this, skinArea, new RequestResultListener() {
                @Override
                public void onSuccess(String json) {
                    if (toastUtils != null && toastUtils.isShowing()) {
                        toastUtils.dismiss();
                    }
                    LocalBroadcastManager.getInstance(SkinWaterAndOilActivity.this).sendBroadcast(new Intent().setAction(SkinChartActivity.ACTION_SKIN));
                    ToastHelper.showShortMsg(getString(R.string.save_success));
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onFailed(String json) {
                    if (toastUtils != null && toastUtils.isShowing()) {
                        toastUtils.dismiss();
                    }
                    ToastHelper.showShortMsg(getString(R.string.link_time_out));

                }
            });
        }

        Log.v("gl", "mWater_date==" + mWater_date + "mOil_date==" + mOil_date);
        seekColorWater.setSeekProgress(mWater_date);
        seekColorOil.setSeekProgress(mOil_date);
        tvWaterValue.setText(mWater_date + "%");
        tvOilValue.setText(mOil_date + "%");
    }

    @Override
    protected void initAction() {


        gvSkinArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                for (AirClean airClean : skinAirCleans) {


                    airClean.setCheck(false);

                }


                boolean isCheck = skinAirCleans.get(position).isCheck();
                isCheck = !isCheck;

                skinAirCleans.get(position).setCheck(isCheck);
                skinAirAdapter.notifyDataSetChanged();


                checkedPos = position;
                reTest();
            }
        });
    }

    private void initSelected(int position) {
        for (AirClean airClean : skinAirCleans) {
            airClean.setCheck(false);
        }
        boolean isCheck = skinAirCleans.get(position).isCheck();
        isCheck = !isCheck;
        skinAirCleans.get(position).setCheck(isCheck);
        skinAirAdapter.notifyDataSetChanged();
        checkedPos = position;

//        reTest();
        toastUtils.showProgress("开始检测");
        seekColorWater.setSeekProgress(mWater_date);
        seekColorOil.setSeekProgress(mOil_date);
        tvWaterValue.setText(mWater_date + "%");
        tvOilValue.setText(mOil_date + "%");
        BLEClient.getInstance().noWorking();
    }

    @OnClick(R.id.btn_reset)
    public void onClick() {
        reTest();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Log.v("gl", "message.what==" + message.what);

            if (message.what == 1) {   //连接成功
//                toastUtils.showProgress(getResources().getString(R.string.bt_state1) + ((String)message.obj));
            } else if (message.what == 2) {  //断开连接
//                toastUtils.showProgress("设备断开");
                ToastHelper.showShortMsg("设备断开");
                if (toastUtils != null && toastUtils.isShowing())
                    toastUtils.dismiss();
            } else if (message.what == 3) {//未完成零
                toastUtils.showProgress(getResources().getString(R.string.testpro_1));
            } else if (message.what == 4) {  //已完成清零
                toastUtils.showProgress(getResources().getString(R.string.testpro_2));
                mWater_date = "0";
                mOil_date = "0";
                seekColorWater.setSeekProgress(mWater_date);
                seekColorOil.setSeekProgress(mOil_date);
                tvWaterValue.setText(mWater_date + "%");
                tvOilValue.setText(mOil_date + "%");
            } else if (message.what == 5) {
                toastUtils.showErrorWithStatus(getApplication().getResources().getString(R.string.testpro_3));
            } else if (message.what == 6) {
                toastUtils.showErrorWithStatus(getApplication().getResources().getString(R.string.testpro_4));
            } else if (message.what == 7) {
                ToastHelper.showShortMsg(getResources().getString(R.string.testpro_5));

                double[] data = (double[]) message.obj;

                mWater_date = BigDecimalUtil.doubleScale(data[0] * 100, 1) + "";
                mOil_date = BigDecimalUtil.doubleScale(data[1] * 100, 1) + "";
                mElastic_date = BigDecimalUtil.doubleScale(data[2] * 100, 1) + "";

                initSeekProgress();

                if (toastUtils != null && toastUtils.isShowing())
                    toastUtils.dismiss();


            } else if (message.what == 8) {
                toastUtils.showProgress(getResources().getString(R.string.testpro_6));
            }
            return false;
        }
    });

    //蓝牙打开回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Toast.makeText(this, getResources().getString(R.string.txt_bluetooth_on), Toast.LENGTH_SHORT).show();
                BLEClient.getInstance().scanBLE(true);
            } else if (requestCode == 1000) {
                if (data == null)
                    return;
                mWater_date = data.getStringExtra("mWater_date");
                mOil_date = data.getStringExtra("mOil_date");
                mElastic_date = data.getStringExtra("mElastic_date");
                checkedPos = data.getIntExtra("checkedPos", checkedPos);
                initSeekProgress();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("gl", "onDestroy");
    }

    private void reTest() {
        if (BLEClient.getInstance().getIsConnect()) {
            toastUtils.showProgress("开始检测");
            seekColorWater.setSeekProgress(mWater_date);
            seekColorOil.setSeekProgress(mOil_date);
            tvWaterValue.setText(mWater_date + "%");
            tvOilValue.setText(mOil_date + "%");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BLEClient.getInstance().noWorking();
                }
            }, 0);
        } else {
            btnReset.setVisibility(View.GONE);
            ToastHelper.showShortMsg("打开皮肤检测仪");
            toastUtils.showProgress(getResources().getString(R.string.txt_bluetooth_scaning));
            BLEClient.getInstance().scanBLE(true);
        }
    }
}
