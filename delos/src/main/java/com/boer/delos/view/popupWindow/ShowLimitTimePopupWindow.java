package com.boer.delos.view.popupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boer.delos.R;
import com.jzxiang.pickerview.adapters.ArrayWheelAdapter;
import com.jzxiang.pickerview.wheel.OnWheelScrollListener;
import com.jzxiang.pickerview.wheel.WheelView;

import java.util.Calendar;


public class ShowLimitTimePopupWindow {
    private Context mContext;
    private View topView;
    private ShowLimitTimePopupWindowInterface showLimitTimePopupWindowInterface;
    private PopupWindow pop;
    private TextView tv_textCancel, tv_textSure;
    private RadioGroup rg_limit_time;
    String[] hour;
    String[] day;
    String strDay;
    String strHour;
    String strStatus;


    public ShowLimitTimePopupWindow(Context context, View topView) {
        mContext = context;
        this.topView = topView;
    }

    public void showPopupWindow() {
        //控件初始化
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.popupwindows_limit_time, null);
        final WheelView wheelDay = (WheelView) view.findViewById(R.id.day);
        final WheelView wheelHour = (WheelView) view.findViewById(R.id.hour);
        tv_textCancel = (TextView) view.findViewById(R.id.id_textCancel);
        tv_textSure = (TextView) view.findViewById(R.id.id_textSure);
        rg_limit_time = (RadioGroup) view.findViewById(R.id.rg_limit_time);

        hour = new String[24];
        for (int i = 0; i < 24; i++) {

            hour[i] = i + "";

        }
        day = new String[32];
        for (int i = 0; i < 32; i++) {

            day[i] = i + "";

        }

        strDay = day[0];
        strHour = hour[0];
        strStatus = "0";
        wheelHour.setEnabled(false);
        wheelDay.setEnabled(false);


        final ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(mContext, day);

        //得到滚轮，实现滚轮滑动中的接口，获取时间，并进行显示
        wheelDay.setViewAdapter(adapter);
        wheelDay.setCyclic(true);
        wheelDay.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                strDay = adapter.getItemText(wheel.getCurrentItem()).toString();

            }
        });

        final ArrayWheelAdapter<String> adapterHour = new ArrayWheelAdapter<>(mContext, hour);
        wheelHour.setViewAdapter(adapterHour);
        wheelHour.setCyclic(true);
        wheelHour.setCurrentItem(1);
        strHour = hour[1];
        wheelHour.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                strHour = adapterHour.getItemText(wheel.getCurrentItem()).toString();

            }
        });


        rg_limit_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_limit:
                        wheelHour.setEnabled(true);
                        wheelDay.setEnabled(true);
                        strStatus = "1";
                        break;
                    case R.id.rbtn_unlimit:
                        wheelHour.setEnabled(false);
                        wheelDay.setEnabled(false);
                        strStatus = "0";
                        break;
                }
            }
        });

        tv_textSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showLimitTimePopupWindowInterface != null) {

                    showLimitTimePopupWindowInterface.rightButtonClick(strDay,strHour, strStatus);
                    dismissPop();
                }
            }
        });
        tv_textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showLimitTimePopupWindowInterface != null) {
                    showLimitTimePopupWindowInterface.leftButtonClick();
                }
                dismissPop();
            }
        });

        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }

        pop = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
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

            }
        });

    }

    public void setShowLimitTimePopupWindowInterface(ShowLimitTimePopupWindowInterface s) {
        showLimitTimePopupWindowInterface = s;
    }

    //关闭popupwindow
    public void dismissPop() {
        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }
    }

    public interface ShowLimitTimePopupWindowInterface {
        void leftButtonClick();//左边按钮点击

        void rightButtonClick(String limitDay, String limitHour,String limitStatus);//右边按钮点击
    }

    public boolean isPopWindowShowing() {
        return pop.isShowing();
    }

}
