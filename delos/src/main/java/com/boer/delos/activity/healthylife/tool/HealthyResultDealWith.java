package com.boer.delos.activity.healthylife.tool;

import com.boer.delos.model.WeightBean;
import com.boer.delos.model.PressureResult;
import com.boer.delos.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/3/14 0014 14:45
 * @Modify:
 * @ModifyDate:
 */
public class HealthyResultDealWith {

    private static List<WeightBean> mWeightList;
    private static List<PressureResult.PressureBean> mPressureList;
    private static ArrayList[] mPressureData;

    /**
     * 处理体重的数据
     *
     * @param weightLists
     */
    public static List<String> dealWeightData(List<WeightBean> weightLists) {
        mWeightList = weightLists;

        Collections.reverse(weightLists);
        List<String> weightList = new ArrayList<>();

        for (WeightBean weight : weightLists) {
            double tWeight = weight.getWeight();
            long measureTime = weight.getMeasuretime();
            String time = TimeUtil.formatStamp2Time(measureTime, "yyyy-MM-dd HH:mm"); //yyyy-MM-dd HH:MM
            weightList.add(time + " " + tWeight);
        }
        if (weightList.size() == 0) return Collections.emptyList();
        return weightList;
//        initChartData2Weight(weightList);
    }

    /**
     * 处理血压
     *
     * @param pressureList
     * @return
     */
    public static ArrayList[] dealWithPressureData(List<PressureResult.PressureBean> pressureList) {
        mPressureList = pressureList;

        ArrayList<String> pressureH = new ArrayList<>();
        ArrayList<String> pressureL = new ArrayList<>();

        for (PressureResult.PressureBean pressureBean : pressureList) {
            String formatTime = TimeUtil.formatStamp2Time(pressureBean.getMeasuretime(), "MM.dd");
            pressureH.add(formatTime + "," + pressureBean.getValueH());
            pressureL.add(formatTime + "," + pressureBean.getValueL());
        }
        mPressureData = new ArrayList[]{pressureH, pressureL};
        return mPressureData;
    }

    public static void clear() {

        mWeightList = null;
        mPressureList = null;

    }

    public static List<WeightBean> getmWeightList() {
        return mWeightList == null ? Collections.<WeightBean>emptyList() : mWeightList;
    }

    public static List<PressureResult.PressureBean> getmPressureList() {
        return mPressureList == null ? Collections.<PressureResult.PressureBean>emptyList() : mPressureList;
    }


    public static ArrayList[] getmPressureData() {

        return mPressureData == null ? new ArrayList[]{} : mPressureData;
    }

}
