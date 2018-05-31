package com.boer.delos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.boer.delos.R;
import com.boer.delos.utils.DensityUtil;

/**
 * Created by gaolong on 2017/4/14.
 */
public class AirCleanView extends LinearLayout {
    private int mWidth, mHeight;
    private Paint mPaintOut, mPaintMidd, mPaintBitmap, mProgressDscPaint, mProgressTextPaint;
    private float mArcWidth;
    private int outColor, middColor;
    private int screenXCenter;
    private int screenYCenter;
    private int radiusOut;
    private int radiusMidd;
    private int radiusIn;
    BitmapDrawable mBitmap;
    private String text;


    //
    private View child0;
    private View child1;
    private View child2;
    int mThumbHeight = 0;
    int mThumbWidth = 0;
    private int mThumbSize = 0;

    DensityUtil densityUitl;

    /*++++++++++++++++++++++++++++++++++++只用radiusMidd++++++++++++++++++++++++++++++++++*/


    public AirCleanView(Context context) {
        super(context);
        init(context, null);
    }


    public AirCleanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AirCleanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {


        densityUitl = new DensityUtil(context);

        TypedArray localTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AirCleanView);
        text = localTypedArray.getString(R.styleable.AirCleanView_text);

        mArcWidth = densityUitl.dp2px(1);
        outColor = getResources().getColor(R.color.white);
        middColor = getResources().getColor(R.color.black);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();

        screenXCenter = mWidth / 2;
        screenYCenter = mHeight / 2;
        Log.v("gl", "screenYCenter===" + screenYCenter);

        radiusOut = screenXCenter - densityUitl.dp2px(10);
        radiusMidd = screenXCenter - densityUitl.dp2px(10) - densityUitl.dp2px(10);
        radiusIn = screenXCenter - densityUitl.dp2px(10) - densityUitl.dp2px(10) - densityUitl.dp2px(5);

        mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true);
        mPaintOut.setStyle(Paint.Style.STROKE);
        mPaintOut.setStrokeWidth(mArcWidth);
        mPaintOut.setColor(outColor);


        mPaintMidd = new Paint();
        mPaintMidd.setAntiAlias(true);
        mPaintMidd.setStyle(Paint.Style.STROKE);
        mPaintMidd.setStrokeWidth(densityUitl.dp2px(10));
        mPaintMidd.setColor(middColor);


        mPaintBitmap = new Paint();
        mPaintBitmap.setAntiAlias(true);


        mProgressDscPaint = new Paint();
        mProgressDscPaint.setColor(middColor);
        mProgressDscPaint.setAntiAlias(true);
        mProgressDscPaint.setStrokeWidth(mArcWidth);
        mProgressDscPaint.setTextSize(densityUitl.dp2px(12));


        mProgressTextPaint = new Paint();
        mProgressTextPaint.setColor(middColor);
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setStrokeWidth(mArcWidth);
        mProgressTextPaint.setTextSize(densityUitl.dp2px(16));


        initBitmap();


    }

    private void initBitmap() {
        mBitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.air_data_bg));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


//        canvas.drawCircle(screenXCenter, screenYCenter, radiusOut, mPaintOut);
//
//        canvas.drawCircle(screenXCenter, screenYCenter, radiusMidd, mPaintMidd);
//
//        canvas.drawCircle(screenXCenter, screenYCenter, radiusIn, mPaintOut);

        Rect rect = new Rect();
        rect.left = screenXCenter - radiusMidd;
        rect.top = screenYCenter - radiusMidd;
        rect.right = screenXCenter + radiusMidd;
        rect.bottom = screenYCenter + radiusMidd;
        canvas.drawBitmap(mBitmap.getBitmap(), null, rect, mPaintBitmap);

        drawProgressText(canvas);

    }

    private void drawProgressText(Canvas canvas) {

        String stringDsc = getResources().getString(R.string.air_clean_data);
        float dscWidth = mProgressTextPaint.measureText(stringDsc);
        float textWidth = mProgressTextPaint.measureText(text);

        canvas.drawText(text, screenXCenter - textWidth / 2, screenYCenter + densityUitl.dp2px(16) / 2
                , mProgressTextPaint);

        canvas.drawText(stringDsc, screenXCenter - dscWidth / 2 - densityUitl.dp2px(5), screenYCenter
                - densityUitl.dp2px(16) / 2 - densityUitl.dp2px(5), mProgressDscPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        screenXCenter = mWidth / 2;
        screenYCenter = mHeight / 2;

        int airViewSize = mWidth > mHeight ? mHeight : mWidth;


        Log.v("gl", "mHeight===" + mHeight);

        mThumbWidth = child0.getMeasuredWidth();
        mThumbHeight = child0.getMeasuredHeight();
        mThumbSize = mThumbWidth > mThumbHeight ? mThumbHeight : mThumbWidth;


        radiusMidd = airViewSize / 2 - mThumbSize / 2;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        double ring = Math.PI / 8;
        setThumbPosition("0", Math.PI / 2 - ring);
        setThumbPosition("1", 0 - ring);
        setThumbPosition("2", Math.PI - ring);
        //        setThumbPosition(Math.toRadians(0));

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child0 = getChildAt(0);
        child1 = getChildAt(1);
        child2 = getChildAt(2);

    }


    private void setThumbPosition(String child, double radian) {
        Log.v("gl", "setThumbPosition radian = " + radian);
        double x = screenXCenter - radiusMidd * Math.cos(radian);
        double y = screenYCenter - radiusMidd * Math.sin(radian);
        int mThumbLeft = (int) (x - mThumbSize / 2);
        int mThumbTop = (int) (y - mThumbSize / 2);
        int mThumbRight = (int) (x + mThumbSize / 2);
        int mThumbBottom = (int) (y + mThumbSize / 2);

        if (child.equals("0"))
            child0.layout(mThumbLeft,
                    mThumbTop,
                    mThumbRight,
                    mThumbBottom);

        if (child.equals("1"))
            child1.layout(mThumbLeft,
                    mThumbTop,
                    mThumbRight,
                    mThumbBottom);

        if (child.equals("2"))
            child2.layout(mThumbLeft,
                    mThumbTop,
                    mThumbRight,
                    mThumbBottom);

    }

    public void setText(String text) {
        this.text = text;
    }
}
