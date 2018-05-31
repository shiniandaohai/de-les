package com.boer.delos.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boer.delos.R;


/**
 * Created by dell on 2015/11/10.
 * 布局标题
 */
public class TitleLayout extends LinearLayout {
    TextView textViewContent;
    TextView textViewContentLeft, textViewContentRight;
    public LinearLayout linearLeft;
    LinearLayout linearContent;
    ImageView imageViewLeft;
    public LinearLayout linearRight;
    ImageView imageViewRight;
    TextView rightTextView;
    TextView leftTextView;
    private titleLayoutClick mListener;
    private titleLayoutContentClick mListener1;
    private RelativeLayout rlayoutTop;

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title, this);
        linearContent = (LinearLayout) findViewById(R.id.id_line_titleContent);
        textViewContentLeft = (TextView) findViewById(R.id.id_textviewContentLeft);
        textViewContentRight = (TextView) findViewById(R.id.id_textviewContentRight);
        textViewContent = (TextView) findViewById(R.id.id_textviewContent);
        leftTextView = (TextView) findViewById(R.id.title_text_left);
        linearLeft = (LinearLayout) findViewById(R.id.id_linearLeft);
        imageViewLeft = (ImageView) findViewById(R.id.id_left);

        linearRight = (LinearLayout) findViewById(R.id.id_linearRight);
        imageViewRight = (ImageView) findViewById(R.id.id_right);
        rightTextView = (TextView) findViewById(R.id.title_text_right);
        rlayoutTop = (RelativeLayout) findViewById(R.id.rlayout_top);

        try {
            mListener = (titleLayoutClick) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mListener1 = (titleLayoutContentClick) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        textViewContentLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener1 != null) {
                    mListener1.leftViewContentClick();
                }
                setDefault();
                textViewContentLeft.setTextColor(getResources().getColor(R.color.blue));
                textViewContentLeft.setBackgroundResource(R.mipmap.left_white_sel);

            }
        });
        textViewContentRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener1 != null) {
                    mListener1.rightViewContentClick();
                }
                setDefault();
                textViewContentRight.setTextColor(getResources().getColor(R.color.blue));
                textViewContentRight.setBackgroundResource(R.mipmap.right_white_sel);
            }
        });
        linearLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.leftViewClick();
                }
            }
        });
        linearRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.rightViewClick();
                }

            }
        });

    }

    //设置中间可切换界面文字
    public void setDefault() {
        textViewContentLeft.setTextColor(getResources().getColor(R.color.white));
        textViewContentLeft.setBackgroundResource(R.mipmap.left_white_nor);
        textViewContentRight.setTextColor(getResources().getColor(R.color.white));
        textViewContentRight.setBackgroundResource(R.mipmap.right_white_nor);
    }

    public void setViewContent(String titleLeft, String titleRight) {
        linearContent.setVisibility(VISIBLE);
        textViewContentLeft.setText(titleLeft);
        textViewContentRight.setText(titleRight);
    }

    public void setViewContent(int titleLeft, int titleRight) {
        linearContent.setVisibility(VISIBLE);
        textViewContentLeft.setText(titleLeft);
        textViewContentRight.setText(titleRight);
    }

    public void setTitle(int title) {
        textViewContent.setVisibility(VISIBLE);
        textViewContent.setText(title);
    }

    public void setTitle(int title, int color) {
        textViewContent.setVisibility(VISIBLE);
        textViewContent.setText(title);
        textViewContent.setTextColor(getResources().getColor(color));
    }


    public void setTitle(String title) {
        textViewContent.setVisibility(VISIBLE);
        textViewContent.setText(title);
    }
    public void setTitle(String title, int color) {
        textViewContent.setVisibility(VISIBLE);
        textViewContent.setText(title);
        textViewContent.setTextColor(getResources().getColor(color));
    }
    //设置左边图片
    public void setLinearLeftImage(int drawableID) {
        imageViewLeft.setBackgroundResource(drawableID);
        imageViewLeft.setVisibility(VISIBLE);
        leftTextView.setVisibility(GONE);
    }

    //设置左边文字
    public void setLeftText(String leftText) {
        leftTextView.setText(leftText);
        leftTextView.setVisibility(VISIBLE);
        imageViewLeft.setVisibility(GONE);
    }

    //设置左边边文字
    public void setLeftText(int leftText) {
        leftTextView.setText(leftText);
        leftTextView.setVisibility(VISIBLE);
        imageViewLeft.setVisibility(GONE);
    }


    public void setTitleBackgroundColor(int color) {
        rlayoutTop.setBackgroundColor(color);
    }


    //设置右边图片
    public void setLinearRightImage(int drawableID) {
        imageViewRight.setImageResource(drawableID);//.setBackgroundResource(drawableID);
        imageViewRight.setVisibility(VISIBLE);
        rightTextView.setVisibility(GONE);
    }

    //设置右边文字
    public void setRightText(int rightText) {
        rightTextView.setText(rightText);
        rightTextView.setVisibility(VISIBLE);
        imageViewRight.setVisibility(GONE);
    }

    //设置右边文字
    public void setRightText(String rightText) {
        rightTextView.setText(rightText);
        rightTextView.setVisibility(VISIBLE);
        imageViewRight.setVisibility(GONE);
    }


    public void hideRight() {
        rightTextView.setVisibility(INVISIBLE);
    }

    public interface titleLayoutContentClick {
        void leftViewContentClick();//左图片点击

        void rightViewContentClick();//右边图片点击
    }

    public interface titleLayoutClick {
        public void leftViewClick();//左图片点击

        public void rightViewClick();//右边图片点击
    }
//    public TextView getRightTextView() {
//        return rightTextView;
//    }
}
