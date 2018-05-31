package com.boer.delos.view.popupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.jzxiang.pickerview.adapters.ArrayWheelAdapter;
import com.jzxiang.pickerview.wheel.OnWheelScrollListener;
import com.jzxiang.pickerview.wheel.WheelView;

import java.util.Calendar;


public class ShowTimePopupWindow {
    private Context mContext;
    private View topView;
    private ShowTimePopupWindowInterface showTimePopupWindowInterface;
    private PopupWindow pop;
    private int sortIndex = 0;
    private TextView tv_textCancel, tv_textSure;
    String[] hour = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};
    String start = hour[0];
    String end =  hour[0];


    public ShowTimePopupWindow(Context context, View topView) {
        mContext = context;
        this.topView = topView;
    }

    public void showPopupWindow() {
        //控件初始化
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.popupwindow_time, null);
        LinearLayout timepickerview = (LinearLayout) view.findViewById(R.id.timepicker);
        tv_textCancel = (TextView) view.findViewById(R.id.id_textCancel);
        tv_textSure = (TextView) view.findViewById(R.id.id_textSure);

        start = hour[0];
        end =  hour[0];

        //默认选中当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int hours = calendar.get(Calendar.HOUR_OF_DAY);


        final ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(mContext, hour);

        //得到滚轮，实现滚轮滑动中的接口，获取时间，并进行显示
        final WheelView wheel1 = (WheelView) timepickerview.getChildAt(0);
        wheel1.setViewAdapter(adapter);
        wheel1.setCyclic(true);
        wheel1.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                start = adapter.getItemText(wheel.getCurrentItem()).toString();

            }
        });
        WheelView wheel2 = (WheelView) timepickerview.getChildAt(2);
        wheel2.setViewAdapter(adapter);
        wheel2.setCyclic(true);
        wheel2.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                end = adapter.getItemText(wheel.getCurrentItem()).toString();
            }
        });


        tv_textSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showTimePopupWindowInterface != null) {

                    showTimePopupWindowInterface.rightButtonClick(start, end);
                    dismissPop();
                }
            }
        });
        tv_textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showTimePopupWindowInterface != null) {
                    showTimePopupWindowInterface.leftButtonClick();
                }
                dismissPop();
            }
        });

        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }

        pop = new PopupWindow(view,  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        //防止被底部虚拟键挡住
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // pop.setAnimationStyle(R.style.MenuAnimationFade);
        pop.setBackgroundDrawable(new BitmapDrawable());//需要设置背景，用物理键返回的时候
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);

        pop.showAtLocation(topView, Gravity.BOTTOM, 0, 0);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pop.dismiss();
                if (showTimePopupWindowInterface != null) {
                    showTimePopupWindowInterface.popupDismiss(sortIndex);
                }

            }
        });

    }

    public void setShowTimePopupWindowInterface(ShowTimePopupWindowInterface s) {
        showTimePopupWindowInterface = s;
    }

    //关闭popupwindow
    public void dismissPop() {
        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }
    }

    public interface ShowTimePopupWindowInterface {
        void popupDismiss(int position);

        void leftButtonClick();//左边按钮点击

        void rightButtonClick(String startTime, String endTime);//右边按钮点击
    }

    public boolean isPopWindowShowing() {
        return pop.isShowing();
    }
}
