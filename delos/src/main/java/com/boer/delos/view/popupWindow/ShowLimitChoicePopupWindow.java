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
import com.jzxiang.pickerview.adapters.ArrayWheelAdapter;
import com.jzxiang.pickerview.wheel.OnWheelScrollListener;
import com.jzxiang.pickerview.wheel.WheelView;

import java.util.Calendar;


public class ShowLimitChoicePopupWindow {
    private Context mContext;
    private View topView;
    private ShowLimitChoicePopupWindowInterface showLimitChoicePopupWindowInterface;
    private PopupWindow pop;
    private TextView tv_textCancel, tv_textSure;
    String[] choice;
    String strChoice;


    public ShowLimitChoicePopupWindow(Context context, View topView) {
        mContext = context;
        this.topView = topView;
    }

    public void showPopupWindow() {
        //控件初始化
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.popupwindow_sex, null);
        LinearLayout timepickerview = (LinearLayout) view.findViewById(R.id.id_linePickTime);
        tv_textCancel = (TextView) view.findViewById(R.id.id_textCancel);
        tv_textSure = (TextView) view.findViewById(R.id.id_textSure);

        choice = new String[]{mContext.getString(R.string.family_text_update_limit), mContext.getString(R.string.family_text_unlimit_unblind)};
        strChoice = choice[0];

        //默认选中当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int hours = calendar.get(Calendar.HOUR_OF_DAY);


        final ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<>(mContext, choice);

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

                strChoice = adapter.getItemText(wheel.getCurrentItem()).toString();

            }
        });


        tv_textSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showLimitChoicePopupWindowInterface != null) {


                    if (strChoice.equals(choice[0])) {

                        showLimitChoicePopupWindowInterface.rightButtonClick("update");
                        dismissPop();

                    } else {

                        showLimitChoicePopupWindowInterface.rightButtonClick("stop");
                        dismissPop();
                    }


                }
            }
        });
        tv_textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showLimitChoicePopupWindowInterface != null) {
                    showLimitChoicePopupWindowInterface.leftButtonClick();
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

    public void setShowChoicePopupWindowInterface(ShowLimitChoicePopupWindowInterface s) {
        showLimitChoicePopupWindowInterface = s;
    }

    //关闭popupwindow
    public void dismissPop() {
        if (pop != null) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }
    }

    public interface ShowLimitChoicePopupWindowInterface {
        void leftButtonClick();//左边按钮点击

        void rightButtonClick(String result);//右边按钮点击
    }

    public boolean isPopWindowShowing() {
        return pop.isShowing();
    }
}
