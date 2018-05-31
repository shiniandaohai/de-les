package com.boer.delos.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;

import butterknife.Bind;

/**
 * Created by apple on 17/5/4.
 */

public class WeatherIndexView extends LinearLayout {
    TextView tvIndex;
    TextView tvValue;

    public WeatherIndexView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public WeatherIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public WeatherIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WeatherIndexView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_weather_index, this,true);
        tvIndex = (TextView)view.findViewById(R.id.tv_index);
        tvValue = (TextView)view.findViewById(R.id.tv_value);
    }

    public void setTvIndex(String index){
        tvIndex.setText(index);
    }
    public void setTvValue(String value){
        tvValue.setText(value);
    }

}
