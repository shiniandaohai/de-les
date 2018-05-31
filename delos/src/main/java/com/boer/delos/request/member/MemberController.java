package com.boer.delos.request.member;

import android.content.Context;
import android.util.Log;

import com.boer.delos.commen.BaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Gateway;
import com.boer.delos.model.User;
import com.boer.delos.request.BaseController;
import com.boer.delos.request.OKHttpRequest;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.RequestTag;
import com.boer.delos.utils.DigitalTrans;
import com.boer.delos.utils.L;
import com.boer.delos.utils.Md5Encrypt;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.sign.AESUtils;
import com.boer.delos.utils.sign.Base64Utils;
import com.boer.delos.utils.sign.HexUtils;
import com.boer.delos.utils.sign.MD5Utils;
import com.boer.delos.utils.sign.iHomeUtils;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author PengJiYang
 * @Description: 用户信息相关的接口实现, 如登录、注册等
 * create at 2016/3/24 9:13
 */
public class MemberController extends BaseController {
    public static MemberController instance = null;

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static MemberController getInstance() {
        if (instance == null) {
            synchronized (MemberController.class) {
                if (instance == null) {
                    instance = new MemberController();
                }
            }
        }
        return instance;
    }

    /**
     * 握手(获取握手令牌)
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void negotiate(Context context, RequestResultListener listener) {
        try {
            String url = URLConfig.HTTP + "/auth/negotiate";
            Constant.KEY = AESUtils.generateKey();
            L.i("Base64(AESKey)+++++++:" + Base64Utils.encode(Constant.KEY));

            String keyHexString = HexUtils.bytesToHexString(Constant.KEY);
            InputStream is = context.getAssets().open("public_key.pem");
            String keyString = iHomeUtils.RSAEncryData(keyHexString.getBytes(), is);
            Constant.SESSION_KEY = keyHexString;

            HashMap<String, String> maps = new HashMap<>();
            maps.put("key", keyString);
            L.i("哈哈" + url + ":" + maps);

            OKHttpRequest.RequestPost(context, url, RequestTag.MEMBER_NEGOTIATE, maps, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户登录
     *
     * @param context  上下文
     * @param mobile   手机号
     * @param password 登录密码
     * @param model    手机类(Android/ios)
     * @param listener 回调接口
     */
    public void login(Context context, String mobile, String password, String model, RequestResultListener listener) {
        if (StringUtil.isEmpty(Constant.NEGOTIATE_TOKEN) || StringUtil.isEmpty(Constant.SESSION_KEY)) {
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).toastUtils.showErrorWithStatus("请重新登录后重试");
            }
            return;
        }

        String url = URLConfig.HTTP + "/auth/login?token=" + Constant.NEGOTIATE_TOKEN;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("mobile", mobile);
        maps.put("password", MD5Utils.MD5(password));
        maps.put("model", model);
        maps.put("UUID", "");// 全局唯一标识符
        maps.put("sessionKey", Constant.SESSION_KEY);
        String json = new Gson().toJson(maps);
        L.i(url + ":" + json);

        OKHttpRequest.postWithNoKey(context, url, RequestTag.MEMBER_LOGIN, json, listener);
    }

    /**
     * 用户注册
     *
     * @param context  上下文
     * @param mobile   手机号
     * @param password 用户密码
     * @param username 用户名
     * @param model    手机类(Android/ios)
     * @param listener 回调接口
     */
    public void register(Context context, String mobile, String password, String username, String model, RequestResultListener listener) {
        String url = URLConfig.HTTP + "/auth/register?token=" + Constant.NEGOTIATE_TOKEN;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("mobile", mobile);
        maps.put("password", Md5Encrypt.stringMD5(password));
        maps.put("name", username);
        maps.put("model", model);
        maps.put("UUID", ""); // 全局唯一标识符
        maps.put("sessionKey", Constant.SESSION_KEY); // 加密key
        String json = new Gson().toJson(maps);
        L.e("register params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.MEMBER_REGISTER, json, listener);
    }

    /**
     * 验证手机号是否已注册
     *
     * @param context  上下文
     * @param mobile   手机号
     * @param listener 回调接口
     */
    public void mobileVerify(Context context, String mobile, RequestResultListener listener) {
//        String url = URLConfig.HTTP + "/auth/mobile_verify?uid=" + Constant.USERID;
        String url = URLConfig.HTTP + "/auth/mobile_verify?token=" + Constant.NEGOTIATE_TOKEN;
        HashMap<String, String> maps = new HashMap<>();
//        maps.put("token", Constant.TOKEN);
        maps.put("mobile", mobile);

        String json = new Gson().toJson(maps);
        L.e("mobileVerify_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.MOBILE_VERIFY, json, listener);
    }

    /**
     * 获取手机验证码
     *
     * @param context  上下文
     * @param mobile   手机号
     * @param listener 回调接口
     */
    public void smsVerify(Context context, String mobile, RequestResultListener listener) {
//        String url = URLConfig.HTTP + "/auth/sms_verify?token=" + Constant.TOKEN + "&uid=" + Constant.USERID;
        String url = URLConfig.HTTP + "/auth/sms_verify?token=" + Constant.NEGOTIATE_TOKEN;
        HashMap<String, String> maps = new HashMap<>();
//        maps.put("token", Constant.TOKEN);
        maps.put("mobile", mobile);

        String json = new Gson().toJson(maps);
        L.e("smsVerify_params===" + json);
        L.e("smsVerify url===" + url);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.MOBILE_VERIFY, json, listener);
    }

    /**
     * 重置密码
     *
     * @param context  上下文
     * @param password 密码
     * @param sms      短信验证码
     * @param smsToken 验证码令牌
     * @param listener 回调接口
     */
    public void resetPassword(Context context, String password, String sms, String smsToken, RequestResultListener listener) {
        String url = URLConfig.HTTP + "/auth/reset_password?token=" + Constant.NEGOTIATE_TOKEN;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("password", Md5Encrypt.stringMD5(password));
        maps.put("sms", sms);
        maps.put("smsToken", smsToken);
        String json = new Gson().toJson(maps);

        L.e("resetPassword_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.MEMBER_RESETPASSWORD, json, listener);
    }

    /**
     * 根据手机号和用户ID查询个人信息
     *
     * @param context  上下文
     * @param userId   用户Id
     * @param mobile   手机号
     * @param listener 回调接口
     */
    public void queryUserInfo(Context context, String userId, String mobile, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("userId", userId);
        maps.put("mobile", mobile);

        String url = URLConfig.HTTP + "/user/userInfo";
        postWithInternetRequest(context, url, maps, RequestTag.USER_INFO, listener);
    }

    /**
     * 退出账号
     *
     * @param context  上下文
     * @param listener 回调接口
     */
    public void logout(Context context, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("uid", Constant.USERID);

        String url = URLConfig.HTTP + "/auth/logout";
        postWithInternetRequest(context, url, maps, RequestTag.USER_LOGOUT, listener);
    }

    /**
     * 更新个人信息
     *
     * @param context  上下文
     * @param user     用户信息
     * @param listener 回调接口
     */
    public void updateUserInfo(Context context, User user, RequestResultListener listener) {
        String mobile = user.getMobile();
        if (StringUtil.isEmpty(mobile)) {
            mobile = Constant.LOGIN_USER.getMobile();
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("name", user.getName());
        maps.put("email", user.getEmail());
        maps.put("mobile", mobile);
        maps.put("avatarUrl", (String) user.getAvatarUrl());
        maps.put("height", user.getHeight() + "");
        maps.put("weight", user.getWeight() + "");
//        maps.put("signature", DigitalTrans.gbEncoding(user.getSignature()));
        maps.put("remark", user.getRemark());
        maps.put("sex", user.getSex());
        maps.put("birthday", user.getBirthday());
        maps.put("constellation", user.getConstellation());

        String url = URLConfig.HTTP + "/user/update";
        postWithInternetRequest(context, url, maps, RequestTag.USER_UPDATE, listener);
    }

    /**
     * 重置手机号
     *
     * @param context  上下文
     * @param sms      短信验证码
     * @param smsToken 验证码令牌
     * @param userId   用户Id
     * @param listener 回调接口
     */
    public void resetMobile(Context context, String sms, String smsToken, String userId, RequestResultListener listener) {
        String url = URLConfig.HTTP + "/auth/resetMobile?uid=" + userId;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("sms", sms);
        maps.put("smsToken", smsToken);
        maps.put("uid", userId);
        String json = new Gson().toJson(maps);

        L.e("resetMobile_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, RequestTag.RESET_MOBILE, json, listener);
    }

    public void bindMobile(Context context,String pwd, String sms, String smsToken, String userId, RequestResultListener listener) {
        String url = URLConfig.HTTP + "/auth/resetMobile?uid=" + userId;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("token", Constant.TOKEN);
        maps.put("pwd",pwd);
        maps.put("sms", sms);
        maps.put("smsToken", smsToken);
        maps.put("uid", userId);
        String json = new Gson().toJson(maps);

        L.e("bindMobile_params===" + json);
        OKHttpRequest.postWithNoKey(context, url, "BIND_MOBILE", json, listener);
    }

    /**
     * 修改用户密码
     *
     * @param context     上下文
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param listener    回调接口
     */
    public void resetCloudPassword(Context context, String oldPassword, String newPassword, RequestResultListener listener) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("old", oldPassword);
        maps.put("new", newPassword);
        maps.put("type", "0");

        String url = URLConfig.HTTP + "/auth/resetCloudpassword";
        postWithInternetRequest(context, url, maps, RequestTag.RESET_PASSWORD, listener);
    }

    /**
     * 判断用户是否是管理员
     *
     * @param context       上下文
     * @param currentHostId 当前主机Id
     * @param listener      回调接口
     */
    public void isManager(Context context, String currentHostId, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("hostId", currentHostId);

        String url = URLConfig.HTTP + "/host/host_admin";
        postWithInternetRequest(context, url, map, RequestTag.IS_MANAGER, listener);
    }

    /**
     * 修改网管属性
     *
     * @param context  上下文
     * @param gateway  主机实体类
     * @param listener 回掉接口
     */
    public void modifyHostProperty(Context context, Gateway gateway, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("detail", gateway);

        String url = URLConfig.HTTP + "/host/modifyproperty";
        postWithInternetRequest(context, url, maps, RequestTag.MODIFY_HOST_PROPERTY, listener);
    }

    /**
     * 根据手机号或用户Id查询用户
     *
     * @param context
     * @param mobile
     * @param userId
     * @param listener
     */
    public void getUserInfo(Context context, String mobile, String userId, RequestResultListener listener) {
        HashMap<String, Object> maps = new HashMap<>();
        if (!StringUtil.isEmpty(mobile)) {
            maps.put("mobile", mobile);
        } else {
            maps.put("userId", mobile);
        }

        String url = URLConfig.HTTP + "/user/userInfo";
        postWithInternetRequest(context, url, maps, "getUserInfo", listener);
    }

    /**
     * 直连登录
     *
     * @param context
     * @param username
     * @param password
     * @param listener
     */
    public void localLogin(Context context, String username, String password, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", MD5Utils.MD5(password));

        String url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/user/login";
        postWithLocalRequest(context, url, map, "localLogin", listener);
    }


    /**
     * 更新主机别名或用户别名
     *
     * @param context
     * @param hostId
     * @param userId
     * @param userAlias
     * @param hostAlias
     * @param listener
     */
    public void updateAlias(Context context, String hostId, String userId, String userAlias, String hostAlias, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("userId", userId);
        map.put("userAlias", userAlias);

        if (!StringUtil.isEmpty(hostAlias))
            map.put("hostAlias", hostAlias);

        String url = URLConfig.HTTP + "/family/update_alias";
        postWithInternetRequest(context, url, map, "updateAlias", listener);
    }

    /**
     * 更新主机名  --->更新别名updateAlias
     *
     * @param context
     * @param hostId
     * @param hostName
     * @param listener
     */
    public void updataGatewayName(Context context, String hostId, String hostName, RequestResultListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("hostName", hostName);
        String url = "";
        if (!hostId.equals(Constant.CURRENTHOSTID)) {
            url = URLConfig.HTTP + "/wrapper/request/cmdType=1020";
            postWithInternetRequest(context, url, map, RequestTag.UPDATE_HOST_NAME, listener);
        } else {
            //判断是否直连
            if (Constant.IS_LOCAL_CONNECTION) {
                url = "http://" + Constant.LOCAL_CONNECTION_IP + ":8080/host/modifyHostName";
            } else {
                url = URLConfig.HTTP + "/wrapper/request?cmdType=1020";
            }
            postRequest(context, url, map, RequestTag.UPDATE_HOST_NAME, listener);
        }
    }

    /**
     * 第三方登录（weibo wechat qq）
     *
     * @param context
     * @param avatarUrl
     * @param gender
     * @param name
     * @param thirdPartyAccount
     * @param thirdPartyType
     * @param listener
     */
    public void thirdLogin(Context context, String avatarUrl, String gender, String name, String thirdPartyAccount, String thirdPartyType, RequestResultListener listener) {


        String url = URLConfig.HTTP + "/auth/loginFromThirdParty?token=" + Constant.NEGOTIATE_TOKEN;
        HashMap<String, String> maps = new HashMap<>();
        maps.put("avatarUrl", avatarUrl);
        maps.put("gender", gender);
        maps.put("name",name);
        maps.put("thirdPartyAccount", thirdPartyAccount);
        maps.put("thirdPartyType", thirdPartyType);
        maps.put("sessionKey", Constant.SESSION_KEY);
        String json = new Gson().toJson(maps);
        OKHttpRequest.postWithNoKey(context, url, "loginFromThirdParty", json, listener);
    }
}
