package com.boer.delos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.boer.delos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/11 0011 10:05
 * @Modify:
 * @ModifyDate:
 */


public class LightControlView extends View {
    private Paint mBLuePaint;
    private Paint mBgCirclePaint;
    private Paint mInSideCirclePaint;
    private Paint mTextPint;

    private Paint mONPaint;


    private int stroke = (int) dp2px(5);
    private int distance = (int) dp2px(25);
    private int padding = (int) dp2px(60);

    private int colorArcGray = Color.parseColor("#e8e8e8");//圆环
    private int colorStroke = Color.parseColor("#d3d3d3");
    private int colortextGray = Color.parseColor("#c3c3c3");
    private int colorCircleGray = Color.parseColor("#f0f0f0");/*内圆*/
    private int colorblue = Color.parseColor("#468ae4");/*内圆*/

    private int centerX = 0;
    private int centerY = 0;
    private int radius;

    private int mWidth;
    private int mHeight;
    private RectF mArcRectF;

    private List<Boolean> listOpens;
    private int lightNum = 4;
    private int mStartAngle = 0; //开始在左边点处
    private int mSweepAngle = 0 / lightNum;
    private boolean isOpen = false;
    private float mTextSize = dp2px(50);
    private boolean isSensor;//是否是传感器或灯

    private Bitmap mBitmap;

    public LightControlView(Context context) {
        super(context);
        init();

    }

    public LightControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCofig(context, attrs);
        init();
    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LightControlView);

        mTextSize = a.getDimensionPixelSize(R.styleable.LightControlView_text_size, (int) dp2px(50));
        lightNum = (int) a.getInt(R.styleable.LightControlView_light_num, 4);
        isSensor = a.getBoolean(R.styleable.LightControlView_isSensor,false);

        mSweepAngle = 360 / lightNum;
        listOpens = new ArrayList<>();
        for (int i = 0; i < lightNum; i++) {
            listOpens.add(false);
        }

        a.recycle();

    }

    private void init() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        //内圆开
        mBLuePaint = new Paint();
        mBLuePaint.setAntiAlias(true);
        mBLuePaint.setColor(colorblue);
        mBLuePaint.setMaskFilter(new BlurMaskFilter(dp2px(20), BlurMaskFilter.Blur.OUTER));
        /*背景圆环*/
        mBgCirclePaint = new Paint();
        mBgCirclePaint.setColor(colorArcGray);
        mBgCirclePaint.setAntiAlias(true);
        mBgCirclePaint.setStyle(Paint.Style.STROKE);
        mBgCirclePaint.setStrokeWidth(stroke);
        /*打开圆环*/
        mONPaint = new Paint();
        mONPaint.setColor(colorblue);
        mONPaint.setAntiAlias(true);
        mONPaint.setStyle(Paint.Style.STROKE);
        mONPaint.setStrokeWidth(stroke);

        //内圆关
        mInSideCirclePaint = new Paint();
        mInSideCirclePaint.setAntiAlias(true);
        mInSideCirclePaint.setColor(colorCircleGray);

        //开关字
        mTextPint = new Paint();
        mTextPint.setColor(colortextGray);
        mTextPint.setTextSize(mTextSize);
        mONPaint.setAntiAlias(true);
        mTextPint.setTypeface(Typeface.DEFAULT);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getHeight();

        centerX = mWidth / 2;
        radius = (int) (mWidth / 2 - padding);
        centerY = (int) (dp2px(40) + radius);
        mArcRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        setMeasuredDimension(mWidth, (int) (centerY+radius+dp2px(5)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //灰色圆环
        canvas.drawCircle(centerX, centerY, radius, mBgCirclePaint);
        if (isOpen) {
            //内圆 外发光
            canvas.drawCircle(centerX, centerY, radius - distance, mBLuePaint);
            //文字
            mTextPint.setColor(colorblue);
            if(isSensor)
                canvas.drawText(getContext().getString(R.string.defence_model_config), centerX - mTextSize * 0.8f, centerY + mTextSize * 0.4f, mTextPint);
            else
                canvas.drawText("ON", centerX - mTextSize * 0.8f, centerY + mTextSize * 0.4f, mTextPint);
        } else {
            //内圆不发光
            canvas.drawCircle(centerX, centerY, radius - distance, mInSideCirclePaint);
            //文字
            mTextPint.setColor(colortextGray);
            if(isSensor)
                canvas.drawText(getContext().getString(R.string.disarming_model_config), centerX - mTextSize * 0.8f, centerY + mTextSize * 0.4f, mTextPint);
            else
                canvas.drawText("OFF", centerX - mTextSize * 0.8f, centerY + mTextSize * 0.4f, mTextPint);
        }
        mStartAngle = 180;
        for (int i = 0; i < listOpens.size(); i++) {
            if (listOpens.get(i)) {
                mStartAngle = 180 + i * mSweepAngle;
                //画开灯效果
                canvas.drawArc(mArcRectF, mStartAngle, mSweepAngle, false, mONPaint);
            }

        }

    }

    /**
     * 开灯
     *
     * @param position
     */
    public void startLight(int position) {
        if (position == -1) {
            return;
        }
        for (int i = 0; i < listOpens.size(); i++) {
            if (position == i) {
                listOpens.set(position, true);
            }
        }
        if (listOpens.contains(true)) {
            isOpen = true;
        } else {
            isOpen = false;
        }
        invalidate();
    }

    /**
     * 关灯
     *
     * @param position
     */
    public void closeLight(int position) {
//        mStartAngle += 180 + position * mSweepAngle;
        for (int i = 0; i < listOpens.size(); i++) {
            if (position == i) {
                listOpens.set(position, false);
            }
        }
        if (listOpens.contains(true)) {
            isOpen = true;
        } else {
            isOpen = false;
        }

        invalidate();
    }

    /**
     * 首先设置灯数量
     *
     * @param lightNum
     */
    public void settingLightNum(int lightNum) {
        mSweepAngle = 360 / lightNum;
        listOpens = new ArrayList<>();
        for (int i = 0; i < lightNum; i++) {
            listOpens.add(false);
        }
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public void setSensor(boolean sensor) {
        isSensor = sensor;
    }
}

