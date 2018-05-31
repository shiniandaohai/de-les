package com.boer.delos.view.popupWindow;

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
import com.boer.delos.utils.ToastHelper;
import com.jzxiang.pickerview.adapters.ArrayWheelAdapter;
import com.jzxiang.pickerview.wheel.OnWheelScrollListener;
import com.jzxiang.pickerview.wheel.WheelView;

import java.util.Calendar;


public class ShowYearMonthPopupWindow {
    private Context mContext;
    private View topView;
    private ShowTimePopupWindowInterface showTimePopupWindowInterface;
    private PopupWindow pop;
    private int sortIndex = 0;
    private TextView tv_textCancel, tv_textSure;
    String[] year = {"2017,2018"};
    String[] month;
    String strYear;
    String strMonth;


    public ShowYearMonthPopupWindow(Context context, View topView) {
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

        month = new String[12];
        for (int i = 0; i < 12; i++) {

            month[i] = i + 1+ "";

        }


        strYear = Calendar.getInstance().get(Calendar.YEAR)+"";
        strMonth = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";

        final ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(mContext, year);
        final ArrayWheelAdapter<String> adapterMonth = new ArrayWheelAdapter<>(mContext, month);

        //得到滚轮，实现滚轮滑动中的接口，获取时间，并进行显示
        final WheelView wheel1 = (WheelView) timepickerview.getChildAt(0);
        wheel1.setViewAdapter(adapter);
        wheel1.setCyclic(false);
        wheel1.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                strYear = adapter.getItemText(wheel.getCurrentItem()).toString();

            }
        });
        WheelView wheel2 = (WheelView) timepickerview.getChildAt(2);
        wheel2.setViewAdapter(adapterMonth);

        int index=0;
        String curMonth=(Calendar.getInstance().get(Calendar.MONTH)+1)+"";
        for(int i=0;i<month.length;i++){
            if(month[i].equals(curMonth)){
                index=i;
                break;
            }
        }
        wheel2.setCurrentItem(index);
        wheel2.setCyclic(true);
        wheel2.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                strMonth = adapterMonth.getItemText(wheel.getCurrentItem()).toString();
            }
        });


        tv_textSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showTimePopupWindowInterface != null) {
                    dismissPop();
                    int tempMonth=Integer.valueOf(strMonth);
                    if(tempMonth<=Calendar.getInstance().get(Calendar.MONTH)+1){
                        showTimePopupWindowInterface.rightButtonClick(strYear, strMonth);
                    }
                    else{
                        ToastHelper.showShortMsg("不能选择未来的时间");
                    }
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
