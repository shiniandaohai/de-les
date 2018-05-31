package com.boer.delos.utils;


import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.commen.BaseActivity;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangkai
 * @Description: String相关工具类
 * create at 2015/10/30 11:40
 */
public class StringUtil {
    /**
     * 判断字符串是否为空或者空字符串 如果字符串是空或空字符串则返回true，否则返回false
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || "null".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * int补0
     *
     * @param intnum
     * @return
     */
    public static String addZero(int intnum) {
        if (intnum < 10 && intnum > 0) {
            return "0" + intnum;
        } else {
            return intnum + "";
        }
    }

    /**
     * 是否是合法的手机号 *
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {
        if (isEmpty(mobiles))
            return false;

        Pattern p = Pattern.compile("^(13|15|17|18|14)\\d{9}$");
//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\\\D])|(18[0,5-9]))\\\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 获取Textview或者Edittext中的文字
     *
     * @param tv
     * @return
     */
    public static String getTextViewString(TextView tv) {
        try {
            if (tv != null) {
                if (!StringUtil.isEmpty(tv.getText().toString().trim())) {
                    return tv.getText().toString().trim();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;

    }

    /**
     * 检查密码格式(密码由6-16位字符组成，不能是9位纯数字)
     *
     * @param pass 密码
     * @return
     */
    public static boolean checkPassword(String pass) {
        return checkPassword(null, pass);
    }

    public static boolean checkPassword(Context context, String pass) {
        int length = pass.length();
        if (length < 6 || length > 16) {
            if (context != null && context instanceof BaseActivity) {
            }
//            SquirrelCallImpl.showToast("密码长度不正确");
            return false;
        }
        Pattern p = Pattern.compile("^\\d{9}$");
        Matcher matcher = p.matcher(pass);
        if (matcher.matches()) {

//            SquirrelCallImpl.showToast("密码不能是9位纯数字");
            return false;
        }
        return true;
    }

    /**
     * 检查邮箱格式
     *
     * @param email 邮箱
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static boolean isOnlyEightByte(String intput) {

        String str = " ^[\\u4e00-\\u9fa5]{1,4}$|^[\\dA-Za-z_]{1,8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(intput);
        String str2 = "^[a-zA-Z\\u4e00-\\u9fa5]{1,4}$";
        Pattern p2 = Pattern.compile(str2);
        Matcher m2 = p2.matcher(intput);

        return m.matches() || m2.matches();
    }

    /**
     * 将String转换为list
     *
     * @param alarmPhone 报警手机
     * @return
     */
    public static List<String> convertStrToList(String alarmPhone) {
        String[] arr = alarmPhone.split(",");
        List<String> list = Arrays.asList(arr);
        return list;
    }

    /**
     * 设备状态替换
     *
     * @param source
     * @return
     */
    public static String deviceStateReplace(String source) {
        return source.replace("\"state\":0", "\"state\":\"0\"").replace("\"state2\":0", "\"state2\":\"0\"").replace("\"state3\":\"0\"", "\"state3\":\"0\"").replace("\"deviceStatus\":\"\"", "\"deviceStatus\":{}");
    }

    /**
     * 设备状态字符串替换为Map
     *
     * @param source
     * @return
     */
    public static String deviceStateStringReplaceMap(String source) {
        return source.replace("\"deviceStatus\": \"\"", "\"deviceStatus\": {}").replace("\"deviceStatus\":\"\"", "\"deviceStatus\": {}");
    }

    /**
     * 将手机号中的"-"和空格去掉
     *
     * @param mobile
     * @return
     */
    public static String mobileReplaceWithoutNumber(String mobile) {
        return mobile.replace("-", "").replace(" ", "");
    }

    /**
     * null替换
     *
     * @param string
     * @return
     */
    public static String nullReplace(String string) {
        return string.replace("null", "\"\"");
    }

    /**
     * 判断Edittext 失去焦点时，是否为空或者包含空格
     *
     * @param editText
     * @return
     */
    public static boolean contansBlankSpace(final EditText editText) {
        final boolean result[] = new boolean[]{false};
        try {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (StringUtil.isEmpty(getTextViewString(editText))) {
                            result[0] = true;
                        }
                        if (getTextViewString(editText).contains(" ")) {
                            result[0] = true;
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Loger.d("" + e.toString());
        }

        return result[0];
    }


    public static boolean checkPhoneNum(String str){
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean checkDigitAndLetter(String str){
        String regExp = "^[A-Za-z0-9]+$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
