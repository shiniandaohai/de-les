package com.boer.delos.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.boer.delos.R;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/27 0027 18:42
 * @Modify:
 * @ModifyDate:
 */

public class CustomeView extends LinearLayout {

    private View child0;
    private View child1;
    private View child2;
    private View child3;
    private View child4;
    private View child5;

    private int mWidth;
    private int mHeight;

    private int centerX = 100;
    private int centerY = 100;

    private int radius = (int) dp2px(40); //半径

    private float distance = dp2px(50); //内外圆间隔

    private float distanceI = dp2px(1);//虚线间隔
    /**
     * 虚线默认颜色
     */
    private int mColor = getResources().getColor(R.color.blue_login_btn);

    private Paint mPaintOut;
    private Paint mPaintIn;
    private Paint mPaintScal;

    private float mArcWidth = dp2px(1);//弧宽

    private int startAngle = 150;
    private int sweepAngle = 240;
    private int count = 100;
    private RectF mArcRectF;
    private int child0Height;

    private int mWithDistance; //渐进宽度
    private String TAG = "哈哈";
    private int childCenterHeight;
    private int childCenterWidth;
    private int padding = (int) dp2px(30);
    int init = 2;
    Thread refreshThread;
    private boolean toggle;

    public CustomeView(Context context) {
        super(context);
        init();
    }

    public CustomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();

        mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true);
        mPaintOut.setStyle(Paint.Style.STROKE);
        mPaintOut.setStrokeWidth(mArcWidth);
        mPaintOut.setColor(mColor);

        mPaintIn = new Paint();
        mPaintIn.setAntiAlias(true);
        mPaintIn.setStyle(Paint.Style.STROKE);
        mPaintIn.setStrokeWidth(mArcWidth);
        mPaintIn.setColor(mColor);


        mPaintScal = new Paint();
        mPaintScal.setAntiAlias(true);
        mPaintScal.setStyle(Paint.Style.STROKE);
        mPaintScal.setStrokeWidth(mArcWidth);
        mPaintScal.setColor(mColor);
        mPaintScal.setAlpha(63);

        centerX = mWidth / 2;
        centerY = centerX;
        radius = (int) (mWidth / 2 - padding);
        mArcRectF = new RectF(centerX - radius, centerX - radius, centerX + radius, centerX + radius);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        /**
         * 记录如果是wrap_content是设置的宽和高
         */
        int width = 0;
        int height = 0;

        int cCount = getChildCount();
        int child0Height = 0;
        int child5Height = 0;
        int child5Width = 0;
        if (cCount >= 5) {
            View childView0 = getChildAt(0);
            View childView5 = getChildAt(5);
            child0Height = childView0.getMeasuredWidth();
            child5Height = childView5.getMeasuredHeight();
            child5Width = childView5.getMeasuredWidth();
            this.child0Height = child0Height;
            childCenterHeight = child5Height;
            childCenterWidth = child5Width;
        }


        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        float dash = dp2px(10);
        PathEffect effects = new DashPathEffect(new float[]{dash, dash}, 0);
        mPaintOut.setPathEffect(effects);
        canvas.drawArc(mArcRectF, 150, 240, false, mPaintOut);
        canvas.restore();

    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float angle = (float) (Math.asin(Float.valueOf(child0Height) / radius) * 180 / Math.PI);
        //TODO 画虚线

        float schedual = dp2px(30);

        float lefttop = centerX - (radius - distance);

        float rightblow = centerX + (radius - distance);

        RectF mRectF = new RectF(lefttop, lefttop, rightblow, rightblow);


        RectF mRectF1 = new RectF(lefttop + schedual, lefttop + schedual, rightblow - schedual, rightblow - schedual);


        RectF mRectF2 = new RectF(lefttop + schedual + schedual, lefttop + schedual + schedual, rightblow - schedual - schedual, rightblow - schedual - schedual);


        for (int i = 0; i < 240; i++) {

            if (i < 45 && mWithDistance < 5 && i % 5 == 0) {
                mWithDistance++;
            }
            if (i > 205 && mWithDistance > 0 && i % 5 == 0) {
                mWithDistance--;
            }
            mPaintIn.setAntiAlias(true);
            mPaintIn.setStrokeWidth(mWithDistance);


            switch (init) {
                case 0:
                    canvas.drawArc(mRectF2, 150 + i, 5, false, mPaintIn);
                    break;
                case 1:
                    canvas.drawArc(mRectF2, 150 + i, 5, false, mPaintScal);
                    canvas.drawArc(mRectF1, 150 + i, 5, false, mPaintIn);
                    break;
                case 2:
//                    canvas.drawArc(mRectF2, 150 + i, 5, false, mPaintScal);
                    canvas.drawArc(mRectF1, 150 + i, 5, false, mPaintIn);
                    canvas.drawArc(mRectF, 150 + i, 5, false, mPaintIn);

                    break;
            }

        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int tempX = (int) (radius * Math.sqrt(3) / 2);
        int tempY = (int) (radius / 2);
        if (child0Height == 0 || childCenterHeight == 0 || childCenterHeight == 0) {
            child0Height = (int) dp2px(60);
            childCenterHeight = (int) dp2px(100);
            childCenterWidth = (int) dp2px(60);
        }

        child0.layout(centerX - tempX - child0Height / 2,
                centerX + tempY - child0Height / 2,
                centerX - tempX + child0Height / 2,
                centerX + tempY + child0Height / 2);

        child1.layout(centerX - tempX - child0Height / 2,
                centerX - tempY - child0Height / 2,
                centerX - tempX + child0Height / 2,
                centerX - tempY + child0Height / 2);

        child2.layout(centerX - child0Height / 2,
                centerX - radius - child0Height / 2,
                centerX + child0Height / 2,
                centerX - radius + child0Height / 2);

        child3.layout(centerX + tempX - child0Height / 2,
                centerX - tempY - child0Height / 2,
                centerX + tempX + child0Height / 2,
                centerX - tempY + child0Height / 2);

        child4.layout(centerX + tempX - child0Height / 2,
                centerX + tempY - child0Height / 2,
                centerX + tempX + child0Height / 2,
                centerX + tempY + child0Height / 2);

        child5.layout(centerX - childCenterWidth / 2,
                centerX,
                centerX + childCenterWidth / 2,
                centerX + childCenterHeight);


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child0 = getChildAt(0);
        child1 = getChildAt(1);
        child2 = getChildAt(2);
        child3 = getChildAt(3);
        child4 = getChildAt(4);
        child5 = getChildAt(5);

    }

    private int[] getScreenWH() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int[] wh = {displayMetrics.widthPixels, displayMetrics.heightPixels};
        return wh;
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }


    public void setWithDistance(int withDistance) {
        mWithDistance = (int) dp2px(withDistance);
    }

    public void setChildCenterHeight(int childCenterHeight) {
        this.childCenterHeight = (int) dp2px(childCenterHeight);
    }

    public void setChildCenterWidth(int childCenterWidth) {
        this.childCenterWidth = (int) dp2px(childCenterWidth);
    }

    public void setPadding(int padding) {
        this.padding = (int) dp2px(padding);
    }

    public void setRadius(int radius) {
        this.radius = (int) dp2px(radius);
    }

    public void setDistance(float distance) {
        this.distance = (int) dp2px(distance);
    }

    public void setDistanceI(float distanceI) {
        this.distanceI = (int) dp2px(distanceI);
    }

    public void setColor(int color) {
        mColor = getResources().getColor(color);
    }


    public void startAnimation() {
        super.onAttachedToWindow();
        toggle = true;
        refreshThread = new Thread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                while (toggle) {
                    i++;

                    init = i % 3;

                    SystemClock.sleep((long) 300);
                    postInvalidate();
                }
            }
        });
        refreshThread.start();
    }

    public void stopAnimation() {
        super.onDetachedFromWindow();
        toggle = false;
        init = 2;
        postInvalidate();
        if (refreshThread != null)
            refreshThread.interrupt();
    }


}
