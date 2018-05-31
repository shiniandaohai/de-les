package com.boer.delos.activity.personal;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.DigitalTrans;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author XieQingTing
 * @Description: 个人资料界面
 * create at 2016/4/12 9:45
 */
public class PersonalDataListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private TimePickerView timePickerView;
    private EditText etUserName;
    private EditText etSign;
    private ImageView ivFemaleSex;
    private ImageView ivMaleSex;
    private EditText etPersonalWeight;
    private TextView tvBirthday, tvConstellation;
    private EditText etPersonalHeight;
    private ImageView ivConstellation;

    private User user;
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        user = GsonUtil.getObject(SharedUtils.getInstance().getTagSp(SharedTag.user_login), User.class);

        initTopBar(R.string.personal_data_title, null, true, false);
        initView();
        initData();

    }

    private void initView() {
        etPersonalHeight = (EditText) findViewById(R.id.etPersonalHeight);
        etPersonalWeight = (EditText) findViewById(R.id.etPersonalWeight);
        ivMaleSex = (ImageView) findViewById(R.id.ivMaleSex);
        ivFemaleSex = (ImageView) findViewById(R.id.ivFemaleSex);
        etSign = (EditText) findViewById(R.id.etSign);
        etUserName = (EditText) findViewById(R.id.etUserName);
        tvBirthday = (TextView) findViewById(R.id.tvBirthday);
        tvConstellation = (TextView) findViewById(R.id.tvConstellation);
        ivConstellation = (ImageView) findViewById(R.id.ivConstellation);

        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView.setCyclic(true);
        timePickerView.setCancelable(true);
        timePickerView.setRange(1900, 2100);

        ivMaleSex.setOnClickListener(this);
        ivFemaleSex.setOnClickListener(this);
        tvBirthday.setOnClickListener(this);
        ivBack.setOnClickListener(this);

    }

    private void initData() {
        L.e("个人资料界面的个人信息======" + SharedUtils.getInstance().getTagSp(SharedTag.user_login));
        if (user.getName() != null) {
            this.etUserName.setText(user.getName());
        }
        if (user.getRemark() != null) {
            this.etSign.setText(DigitalTrans.FromEncoding(user.getRemark()));
        }
        if (!StringUtil.isEmpty(user.getSex())) {
            if (Integer.parseInt(user.getSex()) == 0) {
                ivFemaleSex.setImageResource(R.drawable.ic_personal_data_female_grey);
                ivMaleSex.setImageResource(R.drawable.ic_personal_data_male);
            } else {
                ivFemaleSex.setImageResource(R.drawable.ic_personal_data_female_check);
                ivMaleSex.setImageResource(R.drawable.ic_personal_data_male_gray);
            }
        } else {
            user.setSex("0");// 性别默认设置为男性
        }
        if (user.getBirthday() != null) {
            /*
                仅对于生日格式为例如：2016-01-06
             */
            if (user.getBirthday().contains("-")) {
                String[] birthDay = user.getBirthday().split("-");
                int month = Integer.valueOf(birthDay[1]);
                int day = Integer.valueOf(birthDay[2]);
                setConstellation(month - 1, day);// 设置默认星座图标
            } else {
                this.tvBirthday.setText(user.getBirthday());// 是否需要格式化
                int yearIndex = user.getBirthday().indexOf("年");
                int monthIndex = user.getBirthday().indexOf("月");
                int dayIndex = user.getBirthday().indexOf("日");
                int month = Integer.parseInt(user.getBirthday().substring(yearIndex + 1, monthIndex));
                int day = Integer.parseInt(user.getBirthday().substring(monthIndex + 1, dayIndex));
                L.e("month======" + month + "    day======" + day);
                setConstellation(month - 1, day);// 设置星座图标
            }

        }
        this.etPersonalHeight.setText(user.getHeight() + "");
        this.etPersonalWeight.setText(user.getWeight() + "");
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    private void showTimePicker(Date date1, final TextView tv) {
        timePickerView.setTime(date1);
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Date date2 = new Date();
                if (date.after(date2)) {//如果选中时间比现在时间靠后，提示
                    Toast.makeText(PersonalDataListeningActivity.this, "生日不能比当前时间大", Toast.LENGTH_SHORT).show();
                    return;
                }
                tvBirthday.setText(getTime(date));
                user.setBirthday(getTime(date));

                // month从0开始，day从1开始
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                setConstellation(cal.get(Calendar.MONTH), day);
            }
        });
        timePickerView.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBirthday:
                hideInput();
                showTimePicker(date, tvBirthday);
                break;
            case R.id.ivFemaleSex:
                ivFemaleSex.setImageResource(R.drawable.ic_personal_data_female_check);
                ivMaleSex.setImageResource(R.drawable.ic_personal_data_male_gray);
                user.setSex("1");
                break;
            case R.id.ivMaleSex:
                ivFemaleSex.setImageResource(R.drawable.ic_personal_data_female_grey);
                ivMaleSex.setImageResource(R.drawable.ic_personal_data_male);
                user.setSex("0");
                break;
            case R.id.ivBack:
                updateUserInfo();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            updateUserInfo();
        }
        return false;
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo() {
        if (!StringUtil.isEmpty(etUserName.getText().toString())) {
            user.setName(etUserName.getText().toString());
            //user.setRemark(etUserName.getText().toString());
        } else {
            user.setName("default");
            //user.setRemark("default");
        }

        if (!StringUtil.isEmpty(etSign.getText().toString())) {
            user.setSignature(etSign.getText().toString());
            user.setRemark(etSign.getText().toString());
        } else {
            user.setSignature("default");
            user.setRemark("default");
        }

        if (!StringUtil.isEmpty(etPersonalHeight.getText().toString())) {
            int height = Integer.parseInt(etPersonalHeight.getText().toString());
            if (height > 300 || height < 0) {
                Toast.makeText(PersonalDataListeningActivity.this, "请输入正确身高值", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setHeight(height);
            }
        } else {
            Toast.makeText(PersonalDataListeningActivity.this, "请输入正确身高值", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!StringUtil.isEmpty(etPersonalWeight.getText().toString())) {
            int weight = Integer.parseInt(etPersonalWeight.getText().toString());
            if (weight > 300 || weight < 0) {
                Toast.makeText(PersonalDataListeningActivity.this, "请输入正确体重值", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setWeight(weight);
            }
        } else {
            Toast.makeText(PersonalDataListeningActivity.this, "请输入正确体重值", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setBirthday(tvBirthday.getText().toString());
        //user.setAvatarUrl("");
        //user.setEmail("123@126.com");
        //user.setEmail("");
        MemberController.getInstance().updateUserInfo(this, user, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("PersonalDataListeningActivity updateUserInfo===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    user = JsonUtil.parseDataObject(Json, User.class, "user");
                    Constant.LOGIN_USER = user;
                    SharedPreferencesUtils.saveUserInfoToPreferences(getApplicationContext());
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));
                    setResult(RESULT_OK);
                    finish();
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.parseString(Json, "msg"));
                    finish();
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(json);
                finish();
            }
        });
    }

    /**
     * 设置星座图标和星座名称及星座描述
     *
     * @param month 月(从0开始计算)
     * @param day   日(从1开始计算)
     */
    private void setConstellation(int month, int day) {
        switch (month) {
            case 0:
                if (day < 20) {
                    ivConstellation.setImageResource(R.drawable.ic_12);
                    user.setConstellation("摩羯座");
                    tvConstellation.setText(R.string.mo_jie_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_1);
                    user.setConstellation("水瓶座");
                    tvConstellation.setText(R.string.shui_ping_zuo);
                }
                break;
            case 1:
                if (day < 19) {
                    ivConstellation.setImageResource(R.drawable.ic_1);
                    user.setConstellation("水瓶座");
                    tvConstellation.setText(R.string.shui_ping_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_2);
                    user.setConstellation("双鱼座");
                    tvConstellation.setText(R.string.shuang_yu_zuo);
                }
                break;
            case 2:
                if (day < 21) {
                    ivConstellation.setImageResource(R.drawable.ic_2);
                    user.setConstellation("双鱼座");
                    tvConstellation.setText(R.string.shuang_yu_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_3);
                    user.setConstellation("白羊座");
                    tvConstellation.setText(R.string.bai_yang_zuo);
                }
                break;
            case 3:
                if (day < 20) {
                    ivConstellation.setImageResource(R.drawable.ic_3);
                    user.setConstellation("白羊座");
                    tvConstellation.setText(R.string.bai_yang_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_4);
                    user.setConstellation("金牛座");
                    tvConstellation.setText(R.string.jin_niu_zuo);
                }
                break;
            case 4:
                if (day < 21) {
                    ivConstellation.setImageResource(R.drawable.ic_4);
                    user.setConstellation("金牛座");
                    tvConstellation.setText(R.string.jin_niu_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_5);
                    user.setConstellation("双子座");
                    tvConstellation.setText(R.string.shuang_zi_zuo);
                }
                break;
            case 5:
                if (day < 22) {
                    ivConstellation.setImageResource(R.drawable.ic_5);
                    user.setConstellation("双子座");
                    tvConstellation.setText(R.string.shuang_zi_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_6);
                    user.setConstellation("巨蟹座");
                    tvConstellation.setText(R.string.ju_xie_zuo);
                }
                break;
            case 6:
                if (day < 23) {
                    ivConstellation.setImageResource(R.drawable.ic_6);
                    user.setConstellation("巨蟹座");
                    tvConstellation.setText(R.string.ju_xie_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_7);
                    user.setConstellation("狮子座");
                    tvConstellation.setText(R.string.shi_zi_zuo);
                }
                break;
            case 7:
                if (day < 23) {
                    ivConstellation.setImageResource(R.drawable.ic_7);
                    user.setConstellation("狮子座");
                    tvConstellation.setText(R.string.shi_zi_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_8);
                    user.setConstellation("处女座");
                    tvConstellation.setText(R.string.chu_nv_zuo);
                }
                break;
            case 8:
                if (day < 23) {
                    ivConstellation.setImageResource(R.drawable.ic_8);
                    user.setConstellation("处女座");
                    tvConstellation.setText(R.string.chu_nv_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_9);
                    user.setConstellation("天秤座");
                    tvConstellation.setText(R.string.tian_ping_zuo);
                }
                break;
            case 9:
                if (day < 24) {
                    ivConstellation.setImageResource(R.drawable.ic_9);
                    user.setConstellation("天秤座");
                    tvConstellation.setText(R.string.tian_ping_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_10);
                    user.setConstellation("天蝎座");
                    tvConstellation.setText(R.string.tian_xie_zuo);
                }
                break;
            case 10:
                if (day < 23) {
                    ivConstellation.setImageResource(R.drawable.ic_10);
                    user.setConstellation("天蝎座");
                    tvConstellation.setText(R.string.tian_xie_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_11);
                    user.setConstellation("射手座");
                    tvConstellation.setText(R.string.she_shou_zuo);
                }
                break;
            case 11:
                if (day < 22) {
                    ivConstellation.setImageResource(R.drawable.ic_11);
                    user.setConstellation("射手座");
                    tvConstellation.setText(R.string.she_shou_zuo);
                } else {
                    ivConstellation.setImageResource(R.drawable.ic_12);
                    user.setConstellation("摩羯座");
                    tvConstellation.setText(R.string.mo_jie_zuo);
                }
                break;
        }
    }
}
