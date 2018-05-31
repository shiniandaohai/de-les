package com.boer.delos.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.boer.delos.R;

/**
 * Created by Administrator on 2018/1/25.
 */

public class BatteryStateView extends View {
    private Context mContext;
    private float width;
    private float height;
    private Paint mPaint;
    private float powerQuantity = 0.5f;//电量
    private Bitmap batteryBitmap;

    public BatteryStateView(Context context) {
        super(context);
        mContext = context;
        mPaint = new Paint();
        init();
    }

    public BatteryStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        init();
    }

    public BatteryStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaint = new Paint();
        init();
    }

    private void init() {
        batteryBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_door_bell_battery);//读取图片资源
        width = batteryBitmap.getWidth();
        height = batteryBitmap.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)(width+0.5),(int)(height+0.5));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (powerQuantity > 0.3f && powerQuantity <= 1) {
//            电量少于30%显示红色
            mPaint.setColor(Color.GREEN);
        } else if (powerQuantity >= 0 && powerQuantity <= 0.3) {
            mPaint.setColor(Color.RED);
        }
//        计算绘制电量的区域
        float left = width * 0.04f;
        float right =left+(width * 0.86f-left)*powerQuantity;
        float tope = height * 0.1f;
        float bottom = height * 0.85f;

        canvas.drawRect(left, tope, right, bottom, mPaint);
        canvas.drawBitmap(batteryBitmap, 0, 0, mPaint);
    }

    public void setPower(float power) {
        powerQuantity = power;
        if (powerQuantity > 1.0f)
            powerQuantity = 1.0f;
        if (powerQuantity < 0)
            powerQuantity = 0;
        invalidate();
    }
}
