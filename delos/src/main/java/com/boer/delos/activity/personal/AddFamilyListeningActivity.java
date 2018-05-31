package com.boer.delos.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Family;
import com.boer.delos.model.Host;
import com.boer.delos.model.User;
import com.boer.delos.model.UserResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.popupWindow.SelectPhonePopUpWindow;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 家人添加 界面
 */
public class AddFamilyListeningActivity extends BaseListeningActivity implements View.OnClickListener {
    private EditText etMobile;
    private TextView tvConfirm;
    Host host;
    private TextView tvContacts;
    private int PICK_CONTACT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family);
        initView();
        initData();
    }

    private void initData() {
        host = (Host) getIntent().getSerializableExtra(Constant.KEY_HOST);
    }

    private void initView() {
        initTopBar(R.string.family_family_add, null, true, false);
        etMobile = (EditText) findViewById(R.id.etMobile);
        tvConfirm = (TextView) findViewById(R.id.tvConfirm);
        tvContacts = (TextView) findViewById(R.id.tvContacts);
        tvContacts.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //选取联系人
            case R.id.tvContacts:
                getContactPhone();
                break;
            case R.id.tvConfirm:
                hideInput();
                String mobile = etMobile.getText().toString().trim();
                boolean isMobile = checkMobile(mobile);
                if (isMobile) {
                    getUserInfo(mobile);
                }
                break;
        }
    }


    /**
     * 获取联系人手机号
     */
    private void getContactPhone() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String phoneNumber = "";
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            if (cursor.moveToFirst()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if ("1".equals(hasPhone)) {
                    hasPhone = "true";
                } else {
                    hasPhone = "false";
                }

                if (Boolean.parseBoolean(hasPhone)) {
                    List<String> phoneList = new ArrayList<>();
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    if (phones.moveToFirst()) {
                        do {
                            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneList.add(phoneNumber);
                        } while (phones.moveToNext());
                    }
                    phones.close();
                    if (phoneList.size() == 1) {
                        etMobile.setText(StringUtil.mobileReplaceWithoutNumber(phoneList.get(0)));
                    }
                    if (phoneList.size() > 1) {
                        SelectPhonePopUpWindow popUpWindow = new SelectPhonePopUpWindow(getApplicationContext(), phoneList, new SelectPhonePopUpWindow.ClickResultListener() {
                            @Override
                            public void result(String phone) {
                                String checkedPhone = StringUtil.mobileReplaceWithoutNumber(phone);
                                etMobile.setText(checkedPhone);
                            }
                        });
                        popUpWindow.showAtLocation(tvConfirm, Gravity.NO_GRAVITY, 0, 0);
                    }
                } else {
                    toastUtils.showInfoWithStatus("没有可用的号码");
                }
            }
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }


    /**
     * 检查手机号是否符合规则
     *
     * @return
     */
    private boolean checkMobile(String mobile) {
        if (StringUtil.isEmpty(mobile)) {
            toastUtils.showInfoWithStatus(getString(R.string.toast_input_mobile));
            return false;
        }
        if (!StringUtil.isMobile(mobile)) {
            toastUtils.showErrorWithStatus(getString(R.string.toast_error_mobile));
            return false;
        }
        return true;
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo(String mobile) {
        MemberController.getInstance().getUserInfo(AddFamilyListeningActivity.this, mobile, "", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    UserResult result = new Gson().fromJson(Json, UserResult.class);

                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());
                    } else {
                        User user = result.getUser();

                        boolean isExist = hostIsHaveAddUser(user);
                        if (isExist) {
                            toastUtils.showInfoWithStatus("用户已存在主机中");
                            return;
                        }
                        if (user != null) {
                            startActivityForResult(new Intent(AddFamilyListeningActivity.this, AddFamilyDetailListeningActivity.class)
                                    .putExtra(Constant.KEY_USER, user)
                                    .putExtra(Constant.KEY_HOST, host), 101);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String json) {
            }
        });
    }

    /**
     * 判断所要添加的用户是否存在在主机下
     *
     * @param user
     * @return
     */
    private boolean hostIsHaveAddUser(User user) {
        if (user == null) {
            return true;
        }
        if (host == null) {
            return true;
        }
        String userId = user.getId();
        for (Family f :
                host.getFamilies()) {
            if (f.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }


}
