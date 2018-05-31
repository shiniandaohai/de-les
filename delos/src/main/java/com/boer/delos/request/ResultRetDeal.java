package com.boer.delos.request;

import com.boer.delos.model.BaseResult;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.StringUtil;

/**
 * @author: sunzhibin
 * @E-mail:
 * @Description:
 * @CreateDate: 2017/1/23 0023 16:49
 * @Modify:
 * @ModifyDate:
 */


public class ResultRetDeal {
    private static ResultRetDeal instance;

    private ResultRetDeal() {

    }

    public static ResultRetDeal getInstance() {
        if (instance == null) {
            instance = new ResultRetDeal();
        }
        return instance;
    }

    public  String dealWith(String json) {

        String message = "";
        BaseResult result = GsonUtil.getObject(json, BaseResult.class);
        switch (result.getRet()) {
            case 0:
                break;

            case 10001: // @"系统未知错误",@"10001",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "系统未知错误";
                break;
            case 10002: // @"数据库连接错误",@"10002",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "数据库连接错误";

                break;
            case 10003: // @"查询错误",@"10003",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "查询错误";
                break;
            case 10004: // @"您输入的手机号未注册，请注册后再登录！",@"10004",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "您输入的手机号未注册，请注册后再登录！";
                break;
            case 10005: // @"无法创建token",@"10005",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "无法创建token";
                break;
            case 10006: // @"API不存在",@"10006",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "API不存在";
                break;
            case 10007: // @"400错误的请求",@"10007",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "400错误的请求";
                break;
            case 10101: // @"昵称不能少于三位哦！",@"10101",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "昵称不能少于三位哦！";
                break;
            case 10102: // @"认证失败",@"10102",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "认证失败";
                break;
            case 10103: // @"token无法解析或过期",@"10103",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "token无法解析或过期";
                break;
            case 10104: // @"JSON解析失败",@"10104",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "JSON解析失败";
                break;
            case 10105: // @"消息解密失败",@"10105",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "消息解密失败";
                break;
            case 10106: // @"请求超过上限",@"10106",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "请求超过上限";
                break;
            case 10107: // @"没有权限",@"10107",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "没有权限";
                break;
            /*--------------------------------------------*/
            case 20001: // @"该手机号已注册",@"20001",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "该手机号已注册";
                break;
            case 20002:// @"昵称已被使用",@"20002",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "昵称已被使用";
                break;
            case 20003:// @"您输入的密码有误，请重新输入！",@"20003",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "您输入的密码有误，请重新输入！";
                break;
            case 20004:// @"主机名已存在",@"20004",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "主机名已存在";
                break;
            case 20005:// @"主机不存在",@"20005",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "主机不存在";
                break;
            case 20006:// @"主机已经绑定",@"20006",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "主机已经绑定";
                break;
            case 20007:// @"尚未绑定主机",@"20007",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "尚未绑定主机";
                break;
            case 20008:// @"命令发送失败",@"20008",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "命令发送失败";
                break;
            case 20009:// @"房间不存在",@"20009",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "房间不存在";
                break;
            case 20010:// @"上传文件失败",@"20010",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "上传文件失败";
                break;
            case 20011:// @"MD5校验失败",@"20011",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "MD5校验失败";
                break;
            case 20012:// @"模式禁止使用",@"20012",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "模式禁止使用";
                break;
            case 20013:// @"主机连接超时",@"20013",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "主机连接超时";
                break;
            case 20016:// @"主机不在线",@"20016",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "主机不在线";
                break;
            case 20018:// @"该成员不是管理员",@"20018",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "该成员不是管理员";
                break;
            case 20019:// @"当前手机号未注册",@"20019",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "当前手机号未注册";
                break;
            case 20020:// @"不存在家庭成员",@"20020",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "不存在家庭成员";
                break;
            /*---------------主机-----------------*/

            case 50000:// @"主机未知错误",@"50000",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "未知错误";
                break;
            case 50001:// @"主机消息码错误",@"50001",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "消息码错误";
                break;
            case 50002:// @"需要云账号连接",@"50002",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "需要云账号连接";
                break;
            case 50003:// @"需要本地连接",@"50003",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "需要本地连接";
                break;
            case 50004:// @"设备重复添加",@"50004",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "设备重复添加";
                break;

            case 500100:// @"主机无效请求",@"50100",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "无效请求";
                break;
            case 50101: // @"主机参数错误",@"50101",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "参数错误";
                break;
            case 50105: // @"主机消息解析错误",@"50105",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "消息解析错误";
                break;
            case 50106: // @"数据过期",@"50106",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "数据过期";
                break;
            case 50005: // @"密码错误",@"50005",
                if (StringUtil.isEmpty(result.getMsg()))
                    message = "密码错误";
                break;

        }

        return message;
    }


}
