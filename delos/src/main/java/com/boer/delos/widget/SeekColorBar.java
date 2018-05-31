package com.boer.delos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.boer.delos.R;

/**
 * Created by gaolong on 2017/4/18.
 */
public class SeekColorBar extends View {

    private static final int DEFAULT_MIN_WIDTH = 100;
    private Context context;
    private AttributeSet attrs;
    private TypedArray typedArray;
    private Paint mPaintColorFirst;
    private Paint mPaintColorSecond;
    private Paint mPaintColorThird;
    private Paint mPaintText;
    private int strokeWidth = 10;
    private int MAX = 100;
    private String firstSize = "40";
    private String secondSize = "20";
    private String thirdSize = "40";
    private Drawable mThumbDrawable;
    private int mThumbHeight = 0;
    private int mThumbWidth = 0;
    private String progress;
    private String firstText = "";
    private String secondText = "";
    private String thirdText = "";


    public SeekColorBar(Context context) {
        this(context, null);
    }


    public SeekColorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }


    private void initView() {

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.seekColorBar);
        int firstColor = typedArray.getColor(R.styleable.seekColorBar_firstColor, Color.BLUE);
        int secondColor = typedArray.getColor(R.styleable.seekColorBar_secondColor, Color.GRAY);
        int thirdColor = typedArray.getColor(R.styleable.seekColorBar_thirdColor, Color.GRAY);

        firstSize = typedArray.getString(R.styleable.seekColorBar_firstSize);
        secondSize = typedArray.getString(R.styleable.seekColorBar_secondSize);
        thirdSize = typedArray.getString(R.styleable.seekColorBar_thirdSize);


        firstText = typedArray.getString(R.styleable.seekColorBar_firstText);
        secondText = typedArray.getString(R.styleable.seekColorBar_secondText);
        thirdText = typedArray.getString(R.styleable.seekColorBar_thirdText);


        progress = typedArray.getString(R.styleable.seekColorBar_seekProgress);
        mThumbDrawable = typedArray.getDrawable(R.styleable.seekColorBar_seekThumb);
        mThumbWidth = this.mThumbDrawable.getIntrinsicWidth();
        mThumbHeight = this.mThumbDrawable.getIntrinsicHeight();


        mPaintColorFirst = new Paint();
        mPaintColorFirst.setAntiAlias(true);
        mPaintColorFirst.setStyle(Paint.Style.STROKE);
        mPaintColorFirst.setColor(firstColor);
        mPaintColorFirst.setStrokeWidth(strokeWidth);

        mPaintColorSecond = new Paint();
        mPaintColorSecond.setAntiAlias(true);
        mPaintColorSecond.setStyle(Paint.Style.STROKE);
        mPaintColorSecond.setStrokeWidth(strokeWidth);
        mPaintColorSecond.setColor(secondColor);


        mPaintColorThird = new Paint();
        mPaintColorThird.setAntiAlias(true);
        mPaintColorThird.setStyle(Paint.Style.STROKE);
        mPaintColorThird.setStrokeWidth(strokeWidth);
        mPaintColorThird.setColor(thirdColor);


        mPaintColorThird = new Paint();
        mPaintColorThird.setAntiAlias(true);
        mPaintColorThird.setStyle(Paint.Style.STROKE);
        mPaintColorThird.setStrokeWidth(strokeWidth);
        mPaintColorThird.setColor(thirdColor);


        mPaintText = new Paint();
        mPaintText.setColor(Color.DKGRAY);
        mPaintText.setTextSize(dip2px(14));


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawFirstLine(canvas);

        drawImageDot(canvas);

    }

    private void drawFirstLine(Canvas canvas) {

        float value = getWidth() * Integer.parseInt(firstSize) / 100f;
        int fistValue = Math.round(value);

        float[] pts = {0, getHeight() / 2, fistValue, getHeight() / 2};


        Log.v("gl", "fistValue==" + fistValue);
        canvas.drawLines(pts, mPaintColorFirst);

        drawPercentText(canvas, 0 + "%", 0, 0);
        drawPercentText(canvas, firstSize + "%", fistValue, 1);

        drawDescText(canvas, firstText, fistValue / 2, 1);

        drawSecondLine(canvas, fistValue);

    }


    private void drawSecondLine(Canvas canvas, int fistValue) {

        float value = getWidth() * Integer.parseInt(secondSize) / 100f;
        int secondValue = Math.round(value);

        Log.v("gl", "secondValue==" + secondValue);

        float[] pts = {fistValue, getHeight() / 2, fistValue + secondValue, getHeight() / 2};
        canvas.drawLines(pts, mPaintColorSecond);


        drawPercentText(canvas, Integer.parseInt(firstSize) + Integer.parseInt(secondSize) + "%", fistValue + secondValue, 2);

        drawThirdLine(canvas, fistValue + secondValue);

        drawDescText(canvas, secondText, fistValue + secondValue / 2, 1);

    }

    private void drawThirdLine(Canvas canvas, int secondValue) {


        float value = getWidth() * Integer.parseInt(thirdSize) / 100f;
        int thirdValue = Math.round(value);
        float[] pts = {secondValue, getHeight() / 2, secondValue + thirdValue, getHeight() / 2};

        drawPercentText(canvas, MAX + "%", secondValue + thirdValue, 3);

        canvas.drawLines(pts, mPaintColorThird);


        drawDescText(canvas, thirdText, secondValue + thirdValue / 2, 3);

    }

    private void drawPercentText(Canvas canvas, String txt, int pos, int flag) {

        float textWidth = mPaintText.measureText(txt);
        int textHeight = dip2px(20);

        if (flag == 3) {
            canvas.drawText(txt, (getWidth() - textWidth), getHeight() / 2 + strokeWidth + textHeight + mThumbHeight / 2, mPaintText);
        } else if (flag == 0) {
            canvas.drawText(txt, 0, getHeight() / 2 + strokeWidth + textHeight + mThumbHeight / 2, mPaintText);
        } else
            canvas.drawText(txt, pos - textWidth / 2, getHeight() / 2 + strokeWidth + textHeight + mThumbHeight / 2, mPaintText);


        Log.v("gl", "height===" + mThumbHeight / 2 + "flag===" + flag);

    }

    private void drawDescText(Canvas canvas, String txt, int pos, int flag) {

        float textWidth = mPaintText.measureText(txt);

        canvas.drawText(txt, pos - textWidth / 2, getHeight() / 2 - strokeWidth - textWidth / 2 - mThumbHeight / 2, mPaintText);

    }

    private void drawImageDot(Canvas canvas) {
        float value = 0;
        if (!TextUtils.isEmpty(progress))
            value = getWidth() * Integer.parseInt(progress) / 100f;
        int progressValue = Math.round(value);

        Log.v("gl", "progressValue==" + progressValue);


        this.mThumbDrawable.setBounds(progressValue, getHeight() / 2 - mThumbHeight / 2 - strokeWidth,
                progressValue + mThumbWidth, getHeight() / 2 + mThumbHeight);
        this.mThumbDrawable.draw(canvas);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {

        Log.v("gl", "origin==" + origin);
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);

        Log.v("gl", "specMode==" + specMode);
        Log.v("gl", "specSize==" + specSize);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setSeekProgress(String seekProgress) {
        double progress = 0;
        if (!TextUtils.isEmpty(seekProgress))
            progress = Double.parseDouble(seekProgress);

        this.progress = Math.round(progress) + "";

        postInvalidate();

    }


}
