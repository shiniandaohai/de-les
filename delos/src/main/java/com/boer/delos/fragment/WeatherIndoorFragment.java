package com.boer.delos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.lidroid.xutils.util.LogUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/10 0010 13:54
 * @Modify:
 * @ModifyDate:
 */


public class WeatherIndoorFragment extends LazyFragment {
    @Bind(R.id.tv_temperature_value)
    TextView tvTempValue;
    @Bind(R.id.tv_aqi_value)
    TextView tvAqiValue;
    @Bind(R.id.tv_hum_value)
    TextView tvHumValue;
    @Bind(R.id.tv_pm25_value)
    TextView tvPm25Value;
    @Bind(R.id.tv_pm10_value)
    TextView tvPm10Value;
    @Bind(R.id.tv_tvoc_value)
    TextView tvTvocValue;
    @Bind(R.id.tv_aqi_level)
    TextView tvAqiLevel;

    @Bind(R.id.llayout_status)
    LinearLayout llayoutStatus;
    @Bind(R.id.id_left)
    ImageView idLeft;
    private View rootView;

    private DeviceStatus mDeviceStatus;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather_indoor, container, false);
        ButterKnife.bind(this, rootView);
        llayoutStatus.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getStatusHeight(getActivity())));
        idLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        queryEnvDevice();

        return rootView;

    }

    @Override
    protected void lazyLoad() {

    }

    private void queryEnvDevice() {
        DeviceController.getInstance().queryDeviceRelateInfo(getActivity(),"LaserEgg",new RequestResultListener(){
            @Override
            public void onSuccess(String json) {
                Log.d("json", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt("ret") != 0) {
                        return;
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    jsonObject = jsonArray.getJSONObject(0).getJSONObject("deviceStatus");
                    mDeviceStatus = GsonUtil.getObject(jsonObject.toString(), DeviceStatus.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                updateUI();
            }
            @Override
            public void onFailed(String json) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void updateUI(){
        DecimalFormat decimalFormatTemp=new DecimalFormat("0.0");
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        tvTempValue.setText(decimalFormatTemp.format(Double.valueOf(mDeviceStatus.getValue().getTemp())));
        tvHumValue.setText(decimalFormat.format(Double.valueOf(mDeviceStatus.getValue().getHumidity())));
        tvPm25Value.setText(mDeviceStatus.getValue().getPm25()+"");
        tvAqiValue.setText(mDeviceStatus.getValue().getAqiPm25()+"");
        int levelAqi=judgeAQILevel(mDeviceStatus.getValue().getAqiPm25());
        String[] aqiDeses = getResources().getStringArray(R.array.aqi_des);
        tvAqiLevel.setText(aqiDeses[levelAqi]);
        tvPm25Value.setText(mDeviceStatus.getValue().getPm25()+"");
        tvPm10Value.setText(mDeviceStatus.getValue().getPm10()+"");

        String rtvoc=mDeviceStatus.getValue().getRtvoc();
        int rtvocInt=0;
        try{
            rtvocInt= Integer.parseInt(rtvoc);
        }
        catch(NumberFormatException e){

        }
        tvTvocValue.setText(decimalFormat.format(rtvocInt*0.0012));
    }


    private int judgeAQILevel(int value) {
        if (value <= 50) {
            return 0;
        } else if (value > 50 && value <= 100) {
            return 1;
        } else if (value > 100 && value <= 150) {
            return 2;
        } else if (value > 150 && value <= 200) {
            return 3;
        } else if (value > 200 && value <= 300) {
            return 4;
        } else {
            return 5;
        }
    }
}
