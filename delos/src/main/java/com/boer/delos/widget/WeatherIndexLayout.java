package com.boer.delos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;

public class WeatherIndexLayout extends LinearLayout{
	private ImageView indexImg;
	private TextView keytv,valuetv;
	private String key;
	private int drawableId;
	public WeatherIndexLayout(Context context) {
		super(context);
		initView(context, null, 0);
		// TODO Auto-generated constructor stub
	}
	public WeatherIndexLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		//initView(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public WeatherIndexLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	private void initView(Context context, AttributeSet attrs, int defStyle){
		View view = LayoutInflater.from(context).inflate(R.layout.layout_index_weather, this);
		indexImg = (ImageView)view.findViewById(R.id.img_index);
		keytv = (TextView)view.findViewById(R.id.tv_index);
		valuetv = (TextView)view.findViewById(R.id.tv_value);
		
		TypedArray array = context
				.obtainStyledAttributes(attrs,R.styleable.WeatherIndex);
		int n = array.getIndexCount();
		for(int i=0; i<n; i++){
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.WeatherIndex_imageId:
				drawableId = array.getResourceId(attr, 0);
//				if(drawableId!=0)
//					indexImg.setImageResource(drawableId);
				break;

			case R.styleable.WeatherIndex_weathertext:
				key = array.getString(attr);
				break;
			}
		}
		
		keytv.setText(key);
		
		array.recycle();
	}
	
	public void setTextValue(String value){
		valuetv.setText(value);
	}
	public void setImage(int drawableId){
		indexImg.setImageResource(drawableId);
	}
}