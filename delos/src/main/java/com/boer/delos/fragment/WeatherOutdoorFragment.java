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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.boer.delos.R;
import com.boer.delos.activity.weather.WeatherActivity;
import com.boer.delos.commen.LazyFragment;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.WeatherImageRes;
import com.boer.delos.model.Weather;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.Loger;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.widget.WeatherIndexLayout;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.boer.delos.R.id.tv_weather_1;
import static com.boer.delos.R.id.tv_wind_1;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/10 0010 13:54
 * @Modify:
 * @ModifyDate:
 */


public class WeatherOutdoorFragment extends LazyFragment implements BDLocationListener {
    @Bind(R.id.layout_blum)
    WeatherIndexLayout blumIndexLayout;
    @Bind(R.id.layout_cold)
    WeatherIndexLayout coldIndexLayout;
    @Bind(R.id.layout_dress)
    WeatherIndexLayout dressIndexLayout;
    @Bind(R.id.layout_rash)
    WeatherIndexLayout rashIndexLayout;
    @Bind(R.id.layout_sport)
    WeatherIndexLayout sportIndexLayout;
    @Bind(R.id.layout_uv)
    WeatherIndexLayout uvIndexLayout;

    @Bind(R.id.tv_week_1)
    TextView mTvWeek1;
    @Bind(R.id.iv_weather_1)
    ImageView mIvWeather1;
    @Bind(tv_weather_1)
    TextView mTvWeather1;

    @Bind(tv_wind_1)
    TextView mTvWind1;
    @Bind(R.id.tv_week_2)
    TextView mTvWeek2;
    @Bind(R.id.iv_weather_2)
    ImageView mIvWeather2;
    @Bind(R.id.tv_weather_2)
    TextView mTvWeather2;
    @Bind(R.id.tv_wind_2)
    TextView mTvWind2;
    @Bind(R.id.tv_week_3)
    TextView mTvWeek3;
    @Bind(R.id.iv_weather_3)
    ImageView mIvWeather3;
    @Bind(R.id.tv_weather_3)
    TextView mTvWeather3;

    @Bind(R.id.tv_wind_3)
    TextView mTvWind3;
    @Bind(R.id.tv_week_4)
    TextView mTvWeek4;
    @Bind(R.id.iv_weather_4)
    ImageView mIvWeather4;
    @Bind(R.id.tv_weather_4)
    TextView mTvWeather4;
    @Bind(R.id.tv_wind_4)
    TextView mTvWind4;

    @Bind(R.id.tv_temperature)
    TextView tempTextView;
    @Bind(R.id.iv_weather)
    ImageView mIvWeather;
    @Bind(R.id.tv_weather_info)
    TextView mTvWeatherInfo;
    @Bind(R.id.tv_temperature_detail)
    TextView tv_temperature_detail;
    @Bind(R.id.tv_pm)
    TextView tv_pm;
    @Bind(R.id.tv_humidityweather)
    TextView tv_humidity;
    @Bind(R.id.tv_speedfangxiang)
    TextView tv_speed;
    @Bind(R.id.tv_aqi)
    TextView aqiTv;
    @Bind(R.id.tv_level)
    TextView levelTv;
    @Bind(R.id.llayout_status)
    LinearLayout llayoutStatus;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.id_left)
    ImageView idLeft;


    private View rootView;

    private String title;
    private double mLatitude = Constant.latitude;
    private double mLongitude = Constant.longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather_outdoornew, container, false);
        ButterKnife.bind(this, rootView);
        llayoutStatus.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getStatusHeight(getActivity())));
        tvTitle.setText("无锡");
        idLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        blumIndexLayout.setImage(R.drawable.weather_comfortable);
        coldIndexLayout.setImage(R.drawable.weather_cold);
        dressIndexLayout.setImage(R.drawable.weather_dress);
        rashIndexLayout.setImage(R.drawable.weather_rash);
        sportIndexLayout.setImage(R.drawable.weather_sport);
        uvIndexLayout.setImage(R.drawable.weather_uv);
        updateUI();
        return rootView;
    }

    @Override
    protected void lazyLoad() {
        //getWeatherInfo(mLatitude, mLongitude);
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        mLatitude = bdLocation.getLatitude();
        mLongitude = bdLocation.getLongitude();
        Log.i("gwq","天气 " + bdLocation.getCity());
        Constant.latitude = mLatitude;
        Constant.longitude = mLongitude;
        //getWeatherInfo(mLongitude, mLatitude);

    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    /**
     * 获取天气信息
     */
    private void getWeatherInfo(double mLatitude, double mLongitude) {

        DeviceController.getInstance().getWeatherTemperature(mLongitude, mLatitude, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    if (StringUtil.isEmpty(Json)) {
                        return;
                    }
                    Weather weather = GsonUtil.getObject(Json, Weather.class);
                    if (weather.getError() != 0) {
                        return;
                    }
                    List<Map<String, String>> listWeather = WeatherImageRes.dealWithWeather(weather);
                    updateUI(listWeather);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {
                Loger.d(Json);
            }
        });
    }

    private void updateUI() {
        String weather = ((WeatherActivity) getActivity()).weatherStr;
        Log.i("gwq", "weather=" + weather);
        if (StringUtil.isEmpty(weather)) {
            return;
        }
        Document dom = null;
        try {
            dom = DocumentHelper.parseText(weather);
        } catch (DocumentException e) {
            e.printStackTrace();
        }if (dom == null)return; // add by sunzhibin nullPointerException
        Element root = dom.getRootElement();
        if (root == null)
            return;
        Element envElement = root.element("environment");
        Element indexsElement = root.element("zhishus");
        List<Element> elements = null;
        if (indexsElement != null)
            elements = indexsElement.elements("zhishu");
        Element foreElement = root.element("forecast");
        List<Element> elements1 = null;
        if (foreElement != null)
            elements1 = foreElement.elements("weather");
        String city = root.elementText("city");
        tvTitle.setText("" + city);

        tempTextView.setText(root.elementText("wendu")+"°");
        tv_humidity.setText(root.elementText("shidu"));
        tv_speed.setText(root.elementText("fengxiang") + root.elementText("fengli"));
        if (envElement != null) {
            aqiTv.setText(envElement.elementText("aqi"));
            tv_pm.setText(envElement.elementText("pm25"));
            levelTv.setText(envElement.elementText("quality"));
        }

        if (elements1 != null && elements1.size() > 0) {
            for (int i = 0; i < elements1.size(); i++) {
                Element element = elements1.get(i);
                String date = element.elementText("date");
                String hightemp = element.elementText("high");
                String lowtemp = element.elementText("low");
                String temp = hightemp.substring(2) + "/" + lowtemp.substring(3);
                Element dayElement = element.element("day");
                String wind = dayElement.elementText("fengli");
                String type = dayElement.elementText("type");
                switch (i) {
                    case 0:
                        mTvWeatherInfo.setText(type);
                        tv_temperature_detail.setText(temp);
                        mIvWeather.setImageResource(WeatherImageRes.getWeatherResIdByValue(type));
                        break;
                    case 1:
                        mTvWeek1.setText(date.substring(date.length() - 3, date.length()));
                        mIvWeather1.setImageResource(WeatherImageRes.getWeatherResIdByValue(type));
                        mTvWeather1.setText(temp);
                        mTvWind1.setText(wind);
                        break;
                    case 2:
                        mTvWeek2.setText(date.substring(date.length() - 3, date.length()));
                        mIvWeather2.setImageResource(WeatherImageRes.getWeatherResIdByValue(type));
                        mTvWeather2.setText(temp);
                        mTvWind2.setText(wind);
                        break;
                    case 3:
                        mTvWeek3.setText(date.substring(date.length() - 3, date.length()));
                        mIvWeather3.setImageResource(WeatherImageRes.getWeatherResIdByValue(type));
                        mTvWeather3.setText(temp);
                        mTvWind3.setText(wind);
                        break;
                    case 4:
                        mTvWeek4.setText(date.substring(date.length() - 3, date.length()));
                        mIvWeather4.setImageResource(WeatherImageRes.getWeatherResIdByValue(type));
                        mTvWeather4.setText(temp);
                        mTvWind4.setText(wind);
                        break;
                }
            }

        }

        if (elements != null && elements.size() > 0) {
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String name = element.elementText("name");
                String value = element.elementText("value");
                String detail = element.elementText("detail");
                switch (i) {
                    case 1:
                        blumIndexLayout.setTextValue(value);
                        break;
                    case 2:
                        dressIndexLayout.setTextValue(value);
                        break;
                    case 3:
                        coldIndexLayout.setTextValue(value);
                        break;

                    case 6:
                        uvIndexLayout.setTextValue(value);
                        break;
                    case 7:
                        rashIndexLayout.setTextValue(value);
                        break;
                    case 8:
                        sportIndexLayout.setTextValue(value);
                        break;
                }
            }
        }
    }

    private void updateUI(List<Map<String, String>> listWeather) {
//        map.put("temp", temp);
//        map.put("city", city);
//        map.put("pm25", pm25);
//        map.put("date", date);
//    map.put("weather", weatherInfo);
//    map.put("url", WeatherImageRes.getWeatherResIdByUrl(urlImg) + "");
//
//    map.put("windSpeed", win2);
//    map.put("windDirection", winds[0]);
//    map.put("temperature", temperatureNew);
//    map.put("weekDay", weekDay);
        boolean isFirst = true;
        int index = 0;
        for (Map<String, String> map : listWeather) {

            switch (index) {
                case 0:
                  /*  mTvTemperature.setText(map.get("temp"));
                    mIvWeather.setImageResource(Integer.valueOf(map.get("url")));
                    mTvWeatherInfo.setText(map.get("weather"));

                    tv_temperature_detail.setText(map.get("temperature"));
                    mTvWindSpeed.setText(map.get("windSpeed") + "级");
                    //mTvWindDirection.setText(map.get("windDirection"));

                    mTvpm.setText(map.get("pm25"));*/

                    mTvWeek1.setText(map.get("weekDay"));
                    mIvWeather1.setImageResource(Integer.valueOf(map.get("url")));
                    mTvWeather1.setText(map.get("temp"));
                    mTvWind1.setText(map.get("windSpeed"));

                    title = map.get("city");
                    break;
                case 1:

                    mTvWeek2.setText(map.get("weekDay"));
                    mIvWeather2.setImageResource(Integer.valueOf(map.get("url")));
                    mTvWeather2.setText(map.get("temperature"));
                    mTvWind2.setText(map.get("windSpeed"));

                    break;
                case 2:

                    mTvWeek3.setText(map.get("weekDay"));
                    mIvWeather3.setImageResource(Integer.valueOf(map.get("url")));
                    mTvWeather3.setText(map.get("temperature"));
                    mTvWind3.setText(map.get("windSpeed"));

                    break;
                case 3:

                    mTvWeek4.setText(map.get("weekDay"));
                    mIvWeather4.setImageResource(Integer.valueOf(map.get("url")));
                    mTvWeather4.setText(map.get("temperature"));
                    mTvWind4.setText(map.get("windSpeed"));

                    break;

            }
            index++;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
