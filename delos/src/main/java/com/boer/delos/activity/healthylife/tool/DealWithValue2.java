package com.boer.delos.activity.healthylife.tool;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.constant.Constant;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/4/18 0018 11:44
 * @Modify:
 * @ModifyDate:
 */


public class DealWithValue2 {
    /**
     * 判断状态
     *
     * @param textView
     * @param bpH      高压
     * @param bpL      低压
     */
    public static void judgeBPColor(Context context, TextView textView,
                                    int bpH, int bpL, boolean isColor) {
        int level = DealWithValues.judgeBloodPressureState(bpH, bpL);
        int color = 0;
        String result = "";
        switch (level) {
            case 0:
                color = context.getResources().getColor(R.color.gray_text_delete);
                result = context.getString(R.string.blood_press_danchun);
                break;
            case 1:
                color = context.getResources().getColor(R.color.gray_text_delete);
                result = context.getString(R.string.third_blood_press);
                break;
            case 2:
                color = context.getResources().getColor(R.color.gray_text_delete);
                result = context.getString(R.string.second_blood_press);
                break;
            case 3:
                color = context.getResources().getColor(R.color.gray_text_delete);
                result = context.getString(R.string.first_blood_press);
                break;
            case 4:
                color = context.getResources().getColor(R.color.gray_text_delete);
                result = context.getString(R.string.normal_value);
                break;
            case 5:
                color = context.getResources().getColor(R.color.blue_progress_end);
                result = context.getString(R.string.text_normal);
                break;
            case 6:
                color = context.getResources().getColor(R.color.state_low);
                result = context.getString(R.string.text_low);
                break;
            default:
                color = context.getResources().getColor(R.color.blue_progress_end);
                result = context.getString(R.string.text_normal);
                break;
        }
        if (isColor)
            textView.setTextColor(color);
        textView.setText(result);

    }

    /**
     * 判断血糖状态
     *
     * @param textView
     * @param value
     * @param isColor
     */
    public static void judgeBSColor(Context context, TextView textView, float value, boolean isColor) {
        int level = DealWithValues.judgeBloodSugarState(value);
        int color = 0;
        String result = "";
        switch (level) {
            case 0:
                color = context.getResources().getColor(R.color.gray_text_delete);
                result = context.getString(R.string.text_higher);
                break;

            case 1:
                color = context.getResources().getColor(R.color.blue_progress_end);
                result = context.getString(R.string.text_normal);
                break;
            case 2:
                color = context.getResources().getColor(R.color.state_low);
                result = context.getString(R.string.text_low);
                break;
            default:
                color = context.getResources().getColor(R.color.blue_progress_end);
                result = context.getString(R.string.text_normal);
                break;
        }
        if (isColor)
            textView.setTextColor(color);
        textView.setText(result);
    }

    /**
     * 时间戳
     *
     * @param measureTime 餐前餐后标志
     *                    0 早餐前《空腹》 1早餐后
     *                    2 中餐         3
     *                    4 晚餐         5
     *                    6 睡前
     */
    public static String judgeBeforeOrAfterDinner(Context context, int measureTime) {
        boolean before = false;
        String type = "";
        switch (measureTime) {
            case 0:
                return context.getString(R.string.blood_sugar_fasting);//早餐
//            type = context.getString(R.string.blood_sugar_fasting);
//            before = true;
//            break;
            case 1:
                type = context.getString(R.string.blood_sugar_fasting);
                before = false;
                break;
            case 2:
                type = context.getString(R.string.blood_sugar_lunch);
                before = true;
                break;
            case 3:
                type = context.getString(R.string.blood_sugar_lunch);
                before = false;
                break;
            case 4:
                type = context.getString(R.string.blood_sugar_dinner);
                before = true;
                break;
            case 5:
                type = context.getString(R.string.blood_sugar_dinner);
                before = false;
                break;
            case 6:
                return context.getString(R.string.blood_sugar_sleep_before);

        }
        String result = before ? context.getString(R.string.blood_sugar_before)
                : context.getString(R.string.blood_sugar_after);

        return type + result;
    }

    /**
     * 判断状态
     *
     * @param textView
     */
    public static void judgeWeightColor(Context context, TextView textView,
                                        String bmi, boolean isColor) {
        int level = 4;
        if (!TextUtils.isEmpty(bmi)) {
            try {
                float temp = Float.valueOf(bmi);
                level = DealWithValues.judgeWeightState(temp);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        int color = 0;
        String result = "";
        switch (level) {


            case 0:
            case 1:
            case 2:
            case 3:
                color = context.getResources().getColor(R.color.gray_text_delete);
                result = "偏胖";
                break;
            case 4:
                color = context.getResources().getColor(R.color.blue_progress_end);
                result = context.getString(R.string.text_normal);
                break;
            case 5:
                color = context.getResources().getColor(R.color.state_low);
                result = "消瘦";
                break;
            default:
                color = context.getResources().getColor(R.color.blue_progress_end);
                result = context.getString(R.string.text_normal);
                break;
        }
        if (isColor)
            textView.setTextColor(color);
        textView.setText(result);

    }


}
