package com.boer.delos.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.main.HomepageListeningActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.AdminResult;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.User;
import com.boer.delos.model.UserResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.SelectPhonePopUpWindow;
import com.google.gson.Gson;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 家人申请登录界面
 * create at 2016/3/29 10:54
 *
 */
public class FamilyApplyListeningActivity extends BaseListeningActivity implements View.OnClickListener{

    private EditText etApplyPhone;
    private TextView tvApplyContactBtn;
    private EditText etApplyContent;
    private TextView tvApplyContentNum;
    private TextView tvApplySendBtn;
    private PercentLinearLayout llApplyDeletePhone;

    private int PICK_CONTACT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_apply);

        initView();
    }

    private void initView() {
        initTopBar(R.string.family_login_title, null, true, false);
        this.tvApplySendBtn = (TextView) findViewById(R.id.tvApplySendBtn);
        this.tvApplyContentNum = (TextView) findViewById(R.id.tvApplyContentNum);
        this.etApplyContent = (EditText) findViewById(R.id.etApplyContent);
        this.tvApplyContactBtn = (TextView) findViewById(R.id.tvApplyContactBtn);
        this.etApplyPhone = (EditText) findViewById(R.id.etApplyPhone);
        this.llApplyDeletePhone = (PercentLinearLayout) findViewById(R.id.llApplyDeletePhone);

        this.llApplyDeletePhone.setOnClickListener(this);
        this.tvApplyContactBtn.setOnClickListener(this);
        this.tvApplySendBtn.setOnClickListener(this);
        this.etApplyPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    llApplyDeletePhone.setVisibility(View.VISIBLE);
                } else if (s.length() == 0) {
                    llApplyDeletePhone.setVisibility(View.GONE);
                }
            }
        });

        this.etApplyContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 30) {
                    etApplyContent.setText(s.subSequence(0, 30));
                    CharSequence content = etApplyContent.getText();
                    if (content instanceof Spannable) {
                        Spannable spannable = (Spannable) content;
                        Selection.setSelection(spannable, content.length());
                    }
                    BaseApplication.showToast("您输入的字数超过了限制");
                } else {
                    tvApplyContentNum.setText((30 - s.length()) + "");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvApplyContactBtn:
                getContactPhone();
                break;
            case R.id.tvApplySendBtn:
                // 如果没有管理者，则弹出对话框,否则发送申请
                String mobile = etApplyPhone.getText().toString();
                User user = Constant.LOGIN_USER;
                if(!StringUtil.isMobile(mobile)) {
                    toastUtils.showErrorWithStatus("手机号码格式不正确");
                    return;
                }
                if(user != null&& user.getMobile()!=null&&mobile.equals(user.getMobile())){
                    toastUtils.showErrorWithStatus("请勿输入自己手机号");
                    return;
                }
                requestMobileIsAdmin(etApplyPhone.getText().toString());
                break;
            case R.id.llApplyDeletePhone:
                this.etApplyPhone.setText("");
                this.llApplyDeletePhone.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 查询手机号是否为管理员的
     * @param mobile
     */
    private void requestMobileIsAdmin(final String mobile) {
        toastUtils.showProgress("正在申请...");
        FamilyManageController.getInstance().userIsAdmin(this, mobile,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try{
                            AdminResult result = new Gson().fromJson(Json, AdminResult.class);
                            if(result.getRet() != 0){
                                toastUtils.showErrorWithStatus(result.getMsg());
                            }else{
                                int isAdmin = result.getIsAdmin();
                                if(isAdmin == 1){
                                    //查询当前手机号用户
                                    getUserInfo(mobile);
                                }else{
                                    toastUtils.showErrorWithStatus("当前手机号用户不是管理员");
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
        });
    }

    /**
     * 获取当前用户信息
     * @param mobile
     */
    private void getUserInfo(String mobile) {
        MemberController.getInstance().getUserInfo(this, mobile, null, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try{
                    UserResult result = new Gson().fromJson(Json, UserResult.class);
                    if(result.getRet() != 0){
                        toastUtils.showErrorWithStatus(result.getMsg());
                    }else{
                        applyUserWithUserId(result.getUser().getId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }


    /**
     * 申请加入
     * @param adminUserId
     */
    private void applyUserWithUserId(String adminUserId) {
        FamilyManageController.getInstance().userApplyToAdmin(this, adminUserId, Constant.USERID,
                etApplyContent.getText().toString(), "1", "", "", "0","","",
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        try{
                            BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                            if(result.getRet() != 0){
                                toastUtils.showErrorWithStatus(result.getMsg());
                            }else{
                                toastUtils.showSuccessWithStatus("申请成功");
                                //1秒后跳转
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(FamilyApplyListeningActivity.this, HomepageListeningActivity.class));
                                        finish();
                                    }
                                }, 1000);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String Json) {

                    }
                });
    }

    /**
     * 获取
     */
    private void getContactPhone() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String phoneNumber = "";
        if(requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            if(cursor.moveToFirst()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if("1".equals(hasPhone)) {
                    hasPhone = "true";
                } else {
                    hasPhone = "false";
                }

                if(Boolean.parseBoolean(hasPhone)) {
                    List<String> phoneList = new ArrayList<>();
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//                    L.e("号码个数======" + phones.getCount());
                    if(phones.moveToFirst()) {
                        do {
                            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneList.add(phoneNumber);
//                            etApplyPhone.setText(checkPhoneNumber(phoneNumber));
                        } while (phones.moveToNext());
                    }
                    phones.close();
                    if (phoneList.size() == 1) {
                        etApplyPhone.setText(StringUtil.mobileReplaceWithoutNumber(phoneList.get(0)));
                    }
                    if (phoneList.size() > 1) {
                        SelectPhonePopUpWindow popUpWindow = new SelectPhonePopUpWindow(FamilyApplyListeningActivity.this, phoneList, new SelectPhonePopUpWindow.ClickResultListener() {
                            @Override
                            public void result(String phone) {
//                                L.e("从弹出框获取的号码===" + phone);
                                String checkedPhone = StringUtil.mobileReplaceWithoutNumber(phone);
//                                L.e("处理后的号码===" + checkedPhone);
                                etApplyPhone.setText(checkedPhone);
                            }
                        });
                        popUpWindow.showAtLocation(tvApplyContactBtn, Gravity.NO_GRAVITY, 0, 0);
                    }
                } else {
                    toastUtils.showInfoWithStatus("没有可用的号码");
                }
            }
        }
    }
}
