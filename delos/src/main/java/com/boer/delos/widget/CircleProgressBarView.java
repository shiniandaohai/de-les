package com.boer.delos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.boer.delos.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gaolong on 2017/4/17.
 */

public class CircleProgressBarView extends View {
    //View默认最小宽度
    private static final int DEFAULT_MIN_WIDTH = 500;
    private final TypedArray typedArray;
    //圆环的边距
    private int pandding = 30;
    //圆环的宽度
    private int widthing = 5;
    private Context mContext;
    private Paint mPaint;
    private int countdownTime = 9;//默认时间10秒
    private float progress = 0;//总进度360
    private boolean canDrawProgress = false;
    private boolean toggle = false;
    private double r;
    private Timer progressTimer;
    /**
     * 圆环颜色
     */
    private int[] doughnutColors = new int[]{0xFFDAF6FE, 0xFF82DFF9, 0xFF70D7F4, 0xFF62D2F1, 0xFF57CCEC};


    private String hintText = "";
    /**
     * 进度条终点图片
     */
    private Drawable progressDrawable;


    private TimerTask progressTask;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                progress += 360.00 / (countdownTime * 950.00 / 5.00);
                if (progress > 360) {
                    progress = 0;
                }
                postInvalidate();
            }
        }
    };
    private float textHintSize;
    private ArrayList<Path> paths;

    public CircleProgressBarView(Context context) {
        this(context, null);
    }

    public CircleProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.circleProgressBarView);
        initAtts();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void initAtts() {
        hintText = typedArray.getString(R.styleable.circleProgressBarView_hintText);
        progressDrawable = typedArray.getDrawable(R.styleable.circleProgressBarView_progressSrc) == null ?
                getResources().getDrawable(R.mipmap.light_blue) : typedArray.getDrawable(R.styleable.circleProgressBarView_progressSrc);
        textHintSize = typedArray.getDimension(R.styleable.circleProgressBarView_hintTextSize, 15);
        paths = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            paths.add(new Path());
        }
    }

    /**
     * 当布局为wrap_content时设置默认长宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDefaultForPlay(canvas);


        //这边开启画进度条
        if (canDrawProgress) {
            drawProgress(canvas);
        }


        drawDash(canvas);


    }

    private void drawDash(Canvas canvas) {

        float dash = dip2px(mContext, 1);
        float dash_width = dip2px(mContext, 10);
        Paint mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true);
        mPaintOut.setStyle(Paint.Style.STROKE);
        mPaintOut.setStrokeWidth(dash_width);
        mPaintOut.setColor(getResources().getColor(R.color.RoundFillColor));


        RectF oval = new RectF(dash_width
                , dash_width
                , getWidth() - dash_width
                , getHeight() - dash_width);

        PathEffect effects = new DashPathEffect(new float[]{dash, dash_width}, 0);
        mPaintOut.setPathEffect(effects);
        canvas.drawArc(oval, 0, 360, false, mPaintOut);
    }

    private void drawDefaultForPlay(Canvas canvas) {
        /**
         * 先画一个大圆
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dip2px(mContext, widthing));
        mPaint.setColor(mContext.getResources().getColor(R.color.RoundColor));
        RectF oval = new RectF(dip2px(mContext, pandding)
                , dip2px(mContext, pandding)
                , getWidth() - dip2px(mContext, pandding)
                , getHeight() - dip2px(mContext, pandding));
//        canvas.drawArc(oval, 0, 360, false, mPaint);    //绘制圆弧

        /**
         * 播放的点
         * */
        drawImageDot(canvas);

        /**
         * 画时间
         * */
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setTextSize(dip2px(mContext, 14));
        paint2.setColor(mContext.getResources().getColor(R.color.RoundFillColor));
        paint2.setTextAlign(Paint.Align.CENTER);
        if (hintText == null) {
            hintText = getResources().getString(R.string.progress_loading);
        }

        try {
            Integer.valueOf(hintText);
            paint2.setTextSize(dip2px(mContext, 18));
        }
        catch (NumberFormatException e){
            paint2.setTextSize(dip2px(mContext, 14));
        }

        canvas.drawText(hintText, getWidth() / 2, getHeight() / 2, paint2);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 绘制圆弧
     */
    private void drawProgress(Canvas canvas) {

        mPaint.setColor(getResources().getColor(R.color.RoundFillColor));
        mPaint.setStrokeWidth(dip2px(mContext, widthing));
        RectF oval = new RectF(dip2px(mContext, pandding)
                , dip2px(mContext, pandding)
                , getWidth() - dip2px(mContext, pandding)
                , getHeight() - dip2px(mContext, pandding));
        r = getHeight() / 2f - dip2px(mContext, pandding);


        /**
         * 画一个大圆(渐变)
         */
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.rotate(-90, getWidth() / 2, getHeight() / 2);
        mPaint.setStrokeWidth(dip2px(mContext, widthing));
        mPaint.setShader(new SweepGradient(getWidth() / 2, getHeight() / 2, doughnutColors, null));
        canvas.drawArc(oval, 0, progress, false, mPaint);    //绘制圆弧
        canvas.rotate(90, getWidth() / 2, getHeight() / 2);
        mPaint.reset();

        drawImageDot(canvas);
    }

    private void drawImageDot(Canvas canvas) {
        /**
         * 画一个点（图片）
         * */
        if (r > 0) {
            if (progress > 360)
                return;
            double hu = Math.PI * Double.parseDouble(String.valueOf(progress)) / 180.0;
//            Log.d(TAG, "hu: " + hu);
            double p = Math.sin(hu) * r;
//            Log.d(TAG, "p: " + p);
            double q = Math.cos(hu) * r;
//            Log.d(TAG, "q: " + q);
            float x = (float) ((getWidth() - progressDrawable.getIntrinsicWidth()) / 2f + p);
//            Log.d(TAG, "x: " + x);
            float y = (float) ((dip2px(mContext, pandding) - progressDrawable.getIntrinsicHeight() / 2f) + r - q);
//            Log.d(TAG, "y: " + y);
            canvas.drawBitmap(((BitmapDrawable) progressDrawable).getBitmap(), x, y, mPaint);
        }
    }


    public void startAnimation() {
        //重置计时器显示的时间
        canDrawProgress = true;
        toggle = true;


        if (progressTask != null) {
            if (progressTask != null) {
                progressTask.cancel();
                progressTask=null;
            }
        }

        if (progressTimer != null) {
            if (progressTimer != null) {
                progressTimer.cancel();
                progressTimer=null;
            }
        }

        if(progressTask==null)
        progressTask = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        };

        if(progressTimer==null){
            progressTimer=new Timer(true);
        }

        if (progressTimer != null && toggle)
            progressTimer.schedule(progressTask, 0, 5);
    }


    public void setHintText(String hintText) {
        this.hintText = hintText;
        postInvalidate();
    }

    public void stopAnimation() {
        toggle = false;
        postInvalidate();
        if (progressTask != null) {
            progressTask.cancel();
            progressTask = null;
        }

        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
    }
}
