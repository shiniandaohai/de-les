package com.boer.delos.activity.healthylife.weight;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boer.delos.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by apple on 17/5/11.
 * 体重的详情页
 */

public class FragmentFatRate extends Fragment {


    @Bind(R.id.tv_weight_data)
    TextView tvWeightData;
    @Bind(R.id.tv_weight_state)
    TextView tvWeightState;
    @Bind(R.id.tv_weight_normal_info)
    TextView tvWeightNormalInfo;
    @Bind(R.id.tv_weight_info)
    TextView tvWeightInfo;

    private float mValue;
    private String mType;

    public static FragmentFatRate newInstance(String value, String type) {

        Bundle args = new Bundle();
        if (TextUtils.isEmpty(value)) {
            value = "0";
        }
        try {
            float tempValue = Float.valueOf(value);
            args.putFloat("value", tempValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            args.putFloat("value", 0);
        }

        args.putString("type", type);
        FragmentFatRate fragment = new FragmentFatRate();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weight_major, null);
        ButterKnife.bind(this, rootView);

        initData();
        return rootView;

    }

    private boolean isRetract=false;
    private void initData() {
        mValue = getArguments().getFloat("value");
        mType = getArguments().getString("type");
        isRetract=false;
        tvWeightInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isRetract){
                    isRetract=true;
                    SpannableStringBuilder span = new SpannableStringBuilder("缩进"+tvWeightInfo.getText());
                    span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    tvWeightInfo.setText(span);
                }
            }
        });

        switch (mType) {
            case "fatrate":
                tvWeightData.setText(mValue + "%");
                judgeFatRate(mValue, tvWeightState, tvWeightInfo);
                tvWeightNormalInfo.setText(getString(R.string.weight_fat_rate_level));
                break;
            case "bone":
                tvWeightData.setText(mValue + "%");
                judgeBone(mValue, tvWeightState, tvWeightInfo);
                tvWeightNormalInfo.setText(getString(R.string.weight_bone_level));
                break;
            case "muscle":
                tvWeightData.setText(mValue + "%");
                judgeMuscle(mValue, tvWeightState, tvWeightInfo);
                tvWeightNormalInfo.setText(getString(R.string.weight_muscle_level));
                break;

            case "water":
                tvWeightData.setText(mValue + "%");
                judgeFatWater(mValue, tvWeightState, tvWeightInfo);
                tvWeightNormalInfo.setText(getString(R.string.weight_water_level));
                break;
            case "bmr": //basal metabolism rate
                tvWeightData.setText(mValue + "Kcal");
                judgeBMR(mValue, tvWeightState, tvWeightInfo);
                tvWeightNormalInfo.setText(getString(R.string.weight_basal_metabolism_level));
                break;
            case "bmi":
                tvWeightData.setText(mValue + "kg/㎡");
                judgeBMI(mValue, tvWeightState, tvWeightInfo);
                tvWeightNormalInfo.setText(getString(R.string.weight_bmi_normal));
                break;
        }


    }

    /**
     * 判断体脂率
     *
     * @param value
     * @return <!--质量标准-->
     * <string name="text_normal">正常</string>
     * <string name="text_higher">偏高</string>
     * <string name="text_low">偏低</string>
     */
    private String judgeFatRate(float value, TextView tvWeightState, TextView tvWeightInfo) {
        String result = "";
        if (value == 0) {
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_fat_rate_normal));
        } else if (value < 21.0) { //偏低

            result = getString(R.string.text_low);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_fat_rate_low));

        } else if (value >= 21.0 && value <= 28.0) {//正常
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_fat_rate_normal));
        } else { //偏低
            result = getString(R.string.text_higher);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_fat_rate_high));
        }

        return result;
    }

    /**
     * 判断骨骼
     *
     * @param value
     * @return
     */
    private String judgeBone(float value, TextView tvWeightState, TextView tvWeightInfo) {
        String result = "";
        if (value == 0) {
            result = getString(R.string.text_normal);


            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_bone_normal));
        } else if (value < 3.0) { //偏低

            result = getString(R.string.text_low);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_bone_low));

        } else if (value >= 3.0 && value <= 4.8) {//正常
            result = getString(R.string.text_normal);


            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_bone_normal));
        } else { //偏低
            result = getString(R.string.text_higher);


            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_bone_high));
        }

        return result;
    }

    /**
     * 判断肌肉率
     *
     * @param value
     * @return
     */
    private String judgeMuscle(float value, TextView tvWeightState, TextView tvWeightInfo) {
        String result = "";
        if (value == 0) {
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_muscle_normal));
        } else if (value < 32.0) { //偏低

            result = getString(R.string.text_low);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_muscle_low));
        } else if (value >= 32.0 && value <= 38.0) {//正常
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_muscle_normal));
        } else { //偏低
            result = getString(R.string.text_higher);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_muscle_high));
        }

        return result;
    }

    /**
     * 判断水分
     *
     * @param value
     * @return
     */
    private String judgeFatWater(float value, TextView tvWeightState, TextView tvWeightInfo) {
        String result = "";
        if (value == 0) {
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_water_normal));
        } else if (value < 50.0) { //偏低

            result = getString(R.string.text_low);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_water_low));
        } else if (value >= 50.0 && value <= 60.0) {//正常
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_water_normal));
        } else { //偏低
            result = getString(R.string.text_higher);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_water_high));
        }

        return result;
    }

    /**
     * 判断基础代谢
     *
     * @param value
     * @return
     */
    private String judgeBMR(float value, TextView tvWeightState, TextView tvWeightInfo) {
        String result = "";
        if (value == 0) {
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_basal_metabolism_normal));
        } else if (value < 1514.2) { //偏低

            result = getString(R.string.text_low);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_basal_metabolism_low));
        } else if (value >= 1514.2 && value <= 1850.7) {//正常
            result = getString(R.string.text_normal);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_basal_metabolism_normal));
        } else { //偏高
            result = getString(R.string.text_higher);

            tvWeightState.setText(result);
            tvWeightInfo.setText(getString(R.string.weight_basal_metabolism_high));
        }

        return result;
    }

    /**
     * 判断基础代谢
     *
     * @param value
     * @return
     */
    private String judgeBMI(float value, TextView tvWeightState, TextView tvWeightInfo) {
        String result = "";
        if (value == 0) {
            result = getString(R.string.text_weight_normal);
//            tvWeightState.setTextColor(getResources().getColor(R.color.blue_progress_end));
            tvWeightState.setText(result);
//            tvWeightInfo.setText(getString(R.string.weight_bmi_normal));
        } else if (value < 18.5) { //偏低

            result = getString(R.string.text_weight_pink);

            tvWeightState.setText(result);
//            tvWeightState.setTextColor(getResources().getColor(R.color.state_low));

//            tvWeightInfo.setText(getString(R.string.weight_bmi_thin));
        } else if (value > 18.5 && value < 23.9) {//正常
            result = getString(R.string.text_weight_normal);

            tvWeightState.setText(result);
//            tvWeightState.setTextColor(getResources().getColor(R.color.blue_progress_end));

//            tvWeightInfo.setText(getString(R.string.weight_bmi_normal));
        } else if (value > 24 && value < 27.9) { //超重
            result = getString(R.string.text_weight_over);

            tvWeightState.setText(result);
//            tvWeightState.setTextColor(getResources().getColor(R.color.gray_text_delete));

//            tvWeightInfo.setText(getString(R.string.weight_bmi_over));
        } else if (value > 28 && value < 29.9) { //肥胖
            result = getString(R.string.text_fat);

            tvWeightState.setText(result);
//            tvWeightState.setTextColor(getResources().getColor(R.color.gray_text_delete));

//            tvWeightInfo.setText(getString(R.string.weight_bmi_fate));
        } else if (value > 30 && value < 39.9) { //严重肥胖
            result = getString(R.string.text_fat_severe);

            tvWeightState.setText(result);
//            tvWeightState.setTextColor(getResources().getColor(R.color.gray_text_delete));

//            tvWeightInfo.setText(getString(R.string.weight_bmi_more));
        } else if (value > 39.9) { //超级肥胖
            result = getString(R.string.text_fat_super);

            tvWeightState.setText(result);
//            tvWeightState.setTextColor(getResources().getColor(R.color.gray_text_delete));

//            tvWeightInfo.setText(getString(R.string.weight_bmi_super));
        }

        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
