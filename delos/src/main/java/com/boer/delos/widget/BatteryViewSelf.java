package com.boer.delos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.boer.delos.R;
import com.boer.delos.utils.DensityUitl;

/**
 * 主要实现功能，显示电量
 * 如果充电中，则显示动画
 * 实现思路
 */

public class BatteryViewSelf
        extends View {
    private static final String TAG = "BatteryViewSelf";
    private int mColor;
    private int mStrokeColor;
    /**
     * 电量的最大值
     */
    private int mPower = 100;
    /**
     * 电池的宽度
     */
    int mBatteryWidth = 25;
    /**
     * 电池的高度
     */
    int mBatteryHeight = 15;

    /**
     * 电池内边距
     */
    int mBatteryInsideMargin = 0;

    private Paint mBorderPaint;
    private Paint mBatteryPaint;
    private Paint mBatteryHeaderPaint;
    private Paint mBatteryPercent;

    /**
     * 电池更新的时间间隔
     */
    private int mInterval = 1000;
    /**
     * 充电时的颜色
     */
    private int mChargingColor = Color.RED;
    /**
     * 低电量的颜色
     */
    private int mLowBatteryColor = Color.RED;
    /**
     * 充满电的颜色
     */
    private int mBatteryFullColor = Color.GREEN;
    /**
     * 圆角距形的角度
     */
    float mRadius;
    /**
     * 外部圆角距形
     */
    private RectF mBoardRF;

    private float mStrokeWidth;

    /**
     * 电池头
     */
    private RectF mHeadRF;

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    /**
     * 正在充电中
     */
    private boolean isCharging = false;

    private boolean mLastStatus = false;

    public BatteryViewSelf(Context context) {
        this(context, null);
    }

    public BatteryViewSelf(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public BatteryViewSelf(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        mBatteryWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                80,
                getContext().getResources()
                        .getDisplayMetrics());
        mBatteryHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                30,
                getContext().getResources()
                        .getDisplayMetrics());

        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs,
                        R.styleable.BatteryView,
                        defStyleAttr,
                        0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.BatteryView_bvRadius:
                    mRadius = a.getFloat(attr, 9f);
                    break;
                case R.styleable.BatteryView_bvStrokeWidth:
                    mStrokeWidth = a.getDimension(attr, 2);
                    break;
                case R.styleable.BatteryView_bvWidth:
                    mBatteryWidth = a.getDimensionPixelSize(attr, mBatteryWidth);
                    break;
                case R.styleable.BatteryView_bvHeight:
                    mBatteryHeight = a.getDimensionPixelSize(attr, mBatteryHeight);
                    break;
                case R.styleable.BatteryView_bvStrokeColor:
                    mStrokeColor = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.BatteryView_bvColor:
                    mColor = a.getColor(attr, Color.WHITE);
                    break;

                case R.styleable.BatteryView_bvInsideMargin:
                    mBatteryInsideMargin = a.getDimensionPixelSize(attr, 0);
                    break;

            }

        }
        a.recycle();
    }

    void initPaint() {
        //初始画笔
        //外框画笔
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mStrokeColor);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mStrokeWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);


        //电池画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setStyle(Paint.Style.FILL);
        mBatteryPaint.setColor(mColor);
        mBatteryPaint.setAntiAlias(true);

        //电池头画笔

        mBatteryHeaderPaint = new Paint();
        mBatteryHeaderPaint.setStyle(Paint.Style.FILL);
        mBatteryHeaderPaint.setColor(mStrokeColor);
        mBatteryHeaderPaint.setAntiAlias(true);

        //百分比%画笔
        mBatteryPercent = new Paint();
        mBatteryPercent.setStyle(Paint.Style.FILL);
        mBatteryPercent.setColor(mStrokeColor);
        mBatteryPercent.setTextSize(DensityUitl.dip2px(getContext(), 12));
        mBatteryPercent.setAntiAlias(true);
    }

    /**
     * 中心点X坐标
     */
    float centerX = 0;
    /**
     * 中心点Y坐标
     */
    float centerY = 0;


    RectF mBatteryVolume;
    int mBleft;
    int mBright;
    int mBtop;
    int mBbottom;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        initPaint();
        mBatteryWidth = getMeasuredWidth();
        mBatteryHeight = getHeight();

        mBoardRF = new RectF();
        mBoardRF.left = mStrokeWidth;
        mBoardRF.top = mStrokeWidth;
        mBoardRF.right = mBatteryWidth - mStrokeWidth - mBatteryHeight / 4;
        mBoardRF.bottom = mBatteryHeight - mStrokeWidth;

        mHeadRF = new RectF(mBoardRF.right,
                mBoardRF.bottom / 4,
                mBoardRF.right + mBoardRF.bottom / 4,
                mBoardRF.bottom / 4 * 3);


        //画电池电量

        mBleft = (int) (mBoardRF.left + mBatteryInsideMargin + mStrokeWidth * 2);
        mBtop = (int) (mBoardRF.top + mBatteryInsideMargin + mStrokeWidth * 2);
        mBright = (int) (mBoardRF.right - mBatteryInsideMargin - mStrokeWidth * 2);
        mBbottom = (int) (mBoardRF.bottom - mBatteryInsideMargin - mStrokeWidth * 2);

        mBatteryVolume = new RectF();
        mBatteryVolume.left = mBleft;
        mBatteryVolume.top = mBtop;
        mBatteryVolume.bottom = mBbottom;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        if (modeWidth == MeasureSpec.AT_MOST || modeWidth == MeasureSpec.UNSPECIFIED) {
            sizeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    mBatteryWidth,
                    getContext().getResources()
                            .getDisplayMetrics());
            sizeWidth += getPaddingLeft() + getPaddingRight();
        }
        if (modeHeight == MeasureSpec.AT_MOST || modeHeight == MeasureSpec.UNSPECIFIED) {
            sizeHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    mBatteryHeight,
                    getContext().getResources()
                            .getDisplayMetrics());
            sizeHeight += getPaddingBottom() + getPaddingTop();
        }

        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    /**
     * 绘制电池
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画边框
        canvas.drawRoundRect(mBoardRF, mRadius, mRadius, mBorderPaint);
        float power_percent = mPower / 100.0f;

        //画电池电量

        mBatteryVolume.right = getDynamicVolume(mBatteryVolume.left + mBatteryWidth * power_percent) - mRadius;

        canvas.drawRoundRect(mBatteryVolume, mRadius, mRadius, mBatteryPaint);

//        //画电池头
//        canvas.drawRect(mBoardRF.right,
//                mBatteryHeight / 4,
//                mBoardRF.right + mBatteryWidth / 6,
//                mBatteryHeight / 4 * 3,
//                mBatteryHeaderPaint);


        canvas.drawArc(mHeadRF, -90, 180, false, mBatteryHeaderPaint);//画圆弧，这个时候，绘制没有经过圆心
//        canvas.drawRect(mHeadRF, mBatteryHeaderPaint);//画矩形
//        canvas.drawText(power_percent + "%", mHeadRF.right, 0, mBatteryPercent);

    }


    /**
     * @param mWidth
     * @return
     */
    private int getDynamicVolume(float mWidth) {
        if (mBatteryWidth - mBatteryInsideMargin - mBatteryInsideMargin - mBatteryHeight / 4 >= mWidth) {
            return (int) ((int) mWidth - mBatteryInsideMargin - mStrokeWidth * 2) - mBatteryHeight / 4;
        } else if (mWidth < 0) {
            return 0;
        } else {
            return (int) (mBatteryWidth - mBatteryInsideMargin - mStrokeWidth * 2) - mBatteryHeight / 4;
        }
    }

    /**
     * 充电
     */
    void chargingPower() {
        postInvalidate();
    }

    public void setPower(int power) {
        if (mBatteryPaint != null) {
            if (power <= 10) {
                mBatteryPaint.setColor(mLowBatteryColor);
            } else if (power < 80) {
                mBatteryPaint.setColor(mChargingColor);
            } else {
                mBatteryPaint.setColor(mBatteryFullColor);
            }
        }
        mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        if (isCharging) {
            //增加状态控制
            if (!mLastStatus) {
                mLastStatus = isCharging;
                chargingPower();
            }
        } else {
            mLastStatus = false;
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isCharging = false;
        mLastStatus = false;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getChargingColor() {
        return mChargingColor;
    }

    public void setChargingColor(int chargingColor) {
        mChargingColor = chargingColor;
    }

    public int getLowBatteryColor() {
        return mLowBatteryColor;
    }

    public void setLowBatteryColor(int lowBatteryColor) {
        mLowBatteryColor = lowBatteryColor;
    }

    public int getBatteryFullColor() {
        return mBatteryFullColor;
    }

    public void setBatteryFullColor(int batteryFullColor) {
        mBatteryFullColor = batteryFullColor;
    }
}
