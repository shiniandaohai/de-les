package com.boer.delos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.boer.delos.R;

public class DrawableTextView extends AppCompatTextView {
    private int mWidth;
    private int mHeight;
    private int mLocation;
    public static final int LEFT = 1, TOP = 2, RIGHT = 3, BOTTOM = 4;

    public DrawableTextView(Context context) {
        this(context, null);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
            mWidth = a
                    .getDimensionPixelSize(R.styleable.DrawableTextView_drawable_width, 0);
            mHeight = a.getDimensionPixelSize(R.styleable.DrawableTextView_drawable_height,
                    0);
            Drawable mDrawable = a.getDrawable(R.styleable.DrawableTextView_drawable_src);
            mLocation = a.getInt(R.styleable.DrawableTextView_drawable_location, LEFT);
            a.recycle();
            drawDrawable(mDrawable);
        }
    }

    /**
     * 绘制Drawable宽高,位置
     */
    public void drawDrawable(Drawable mDrawable) {
        if (mDrawable != null) {
            if (mWidth != 0 && mHeight != 0) {
                mDrawable.setBounds(0, 0, mWidth, mHeight);
            }
            switch (mLocation) {
                case LEFT:
                    this.setCompoundDrawables(mDrawable, null,
                            null, null);
                    break;
                case TOP:
                    this.setCompoundDrawables(null, mDrawable,
                            null, null);
                    break;
                case RIGHT:
                    this.setCompoundDrawables(null, null,
                            mDrawable, null);
                    break;
                case BOTTOM:
                    this.setCompoundDrawables(null, null, null,
                            mDrawable);
                    break;
            }
        }
    }

    public void setDrawable(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawDrawable(drawable);
    }
}