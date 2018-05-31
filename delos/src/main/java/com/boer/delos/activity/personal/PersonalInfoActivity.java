package com.boer.delos.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.boer.delos.R;
import com.boer.delos.activity.login.BindPhoneNumActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.SharedTag;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.ImageUtils;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.SharedUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.utils.UploadFileManger;
import com.boer.delos.utils.sharedPreferences.SharedPreferencesUtils;
import com.boer.delos.view.CircleImageView;
import com.boer.delos.view.popupWindow.BindEmailPopUpWindow;
import com.boer.delos.view.popupWindow.ShowSexPopupWindow;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaolong on 2017/3/30.
 */
public class PersonalInfoActivity extends CommonBaseActivity implements UploadFileManger.UploadFileListener {


    @Bind(R.id.ivAvatar)
    CircleImageView ivAvatar;
    @Bind(R.id.llUserInfo)
    LinearLayout llUserInfo;
    @Bind(R.id.etUserName)
    EditText etUserName;
    @Bind(R.id.tvBirthday)
    TextView tvBirthday;
    @Bind(R.id.etPersonalHeight)
    EditText etPersonalHeight;
    @Bind(R.id.etPersonalWeight)
    EditText etPersonalWeight;
    @Bind(R.id.tvBindMobile)
    TextView tvBindMobile;
    @Bind(R.id.llBindPhone)
    LinearLayout llBindPhone;
    @Bind(R.id.tvEmail)
    TextView tvEmail;
    @Bind(R.id.llBindEmail)
    LinearLayout llBindEmail;
    @Bind(R.id.layout_sex)
    LinearLayout layoutSex;
    @Bind(R.id.tv_sex)
    TextView tvSex;

    //
    private PopupWindow popuWindow;
    public static final int PICTURE = 10000;
    public static final int CAMERA = 10001;
    public static final int REQUESTCODE_CUTOUT = 10002;
    private User user;
    private TimePickerView timePickerView;
    private Date date = new Date();
    private BindEmailPopUpWindow bindPopUpWindow;
    private String newEmail;

    ShowSexPopupWindow showSexPopupWindow;

    @Override
    protected int initLayout() {
        return R.layout.activity_personal_info;
    }

    @Override
    protected void initView() {
//        statusBarTheme(true,R.color.blue);
        tlTitleLayout.setTitle(R.string.personal_data_title);
        tlTitleLayout.setRightText(R.string.save);

    }

    @Override
    protected void initData() {
        user = Constant.LOGIN_USER;
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        timePickerView.setCyclic(true);
        timePickerView.setCancelable(true);
        timePickerView.setRange(1900, 2100);

        showSexPopupWindow = new ShowSexPopupWindow(this, layoutSex);
        showSexPopupWindow.setShowSexPopupWindowInterface(new ShowSexPopupWindow.ShowSexPopupWindowInterface() {
            @Override
            public void popupDismiss(int position) {

            }

            @Override
            public void leftButtonClick() {

            }

            @Override
            public void rightButtonClick(String result) {

                tvSex.setText(result);
                user.setSex(result);


            }
        });

        setUserInfo();

        hideInput();
    }

    @Override
    protected void initAction() {
    }

    private void setUserInfo() {
        if (user == null)
            return;


        if (user.getName() != null) {
            etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        etUserName.setCursorVisible(true);
                    }
                }
            });
            this.etUserName.setText(user.getName());
            etUserName.setSelection(etUserName.getText().toString().length());
        }

        if (!StringUtil.isEmpty(user.getMobile())) {
            this.tvBindMobile.setText(user.getMobile());
        }
        else{
            this.tvBindMobile.setText("未绑定");
        }


        if (!StringUtil.isEmpty(user.getSex())) {
            tvSex.setText(user.getSex());
        } else {
            tvSex.setText("");
        }
        if (user.getBirthday() != null) {
            /*
                仅对于生日格式为例如：2016-01-06
             */
            if (user.getBirthday().contains("-")) {
                String[] birthDay = user.getBirthday().split("-");
                int month = Integer.valueOf(birthDay[1]);
                int day = Integer.valueOf(birthDay[2]);
            } else {
                this.tvBirthday.setText(user.getBirthday());// 是否需要格式化
                int yearIndex = user.getBirthday().indexOf("年");
                int monthIndex = user.getBirthday().indexOf("月");
                int dayIndex = user.getBirthday().indexOf("日");
                int month = Integer.parseInt(user.getBirthday().substring(yearIndex + 1, monthIndex));
                int day = Integer.parseInt(user.getBirthday().substring(monthIndex + 1, dayIndex));
                L.e("month======" + month + "    day======" + day);
            }

        }
        this.etPersonalHeight.setText(user.getHeight() + "");
        this.etPersonalWeight.setText(user.getWeight() + "");

        tvEmail.setText(user.getEmail());

        if (!TextUtils.isEmpty(user.getAvatarUrl())) {
            if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            } else if (user.getAvatarUrl().contains("http")) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            } else {
                ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
            }
        }
    }


    @OnClick({R.id.ivAvatar, R.id.llUserInfo, R.id.tvBirthday, R.id.llBindPhone, R.id.llBindEmail, R.id.layout_sex})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAvatar:
                showPhotoViewPopuWindow();
                break;
            case R.id.llUserInfo:
                break;
            case R.id.tvBirthday:
                hideInput();
                showTimePicker(date, tvBirthday);
                break;

            case R.id.llBindEmail:
                bindPopUpWindow = new BindEmailPopUpWindow(this, getString(R.string.my_center_bind_email), new BindEmailPopUpWindow.ClickResultListener() {
                    @Override
                    public void ClickResult(String tag) {
                        newEmail = tag;
                        tvEmail.setText(newEmail);
                        bindPopUpWindow.dismiss();
                    }
                });
                String oldEmail = tvEmail.getText().toString();
                if (!StringUtil.isEmpty(oldEmail)) {
                    bindPopUpWindow.setEditText(oldEmail);
                }
                bindPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.layout_sex:
                showSexPopupWindow.showPopupWindow();
                break;
            case R.id.llBindPhone:
                if(StringUtil.isEmpty(user.getMobile())){
                    startActivity(new Intent(this, BindPhoneNumActivity.class));
                }
                else{
                    startActivity(new Intent(this, CheckPasswordListeningActivity.class));
                }
                break;
        }
    }

    private void showTimePicker(Date date1, final TextView tv) {
        timePickerView.setTime(date1);
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Date date2 = new Date();
                if (date.after(date2)) {//如果选中时间比现在时间靠后，提示
                    Toast.makeText(PersonalInfoActivity.this, "生日不能比当前时间大", Toast.LENGTH_SHORT).show();
                    return;
                }
                tvBirthday.setText(getTime(date));
                user.setBirthday(getTime(date));

                // month从0开始，day从1开始
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int day = cal.get(Calendar.DAY_OF_MONTH);
            }
        });
        timePickerView.show();
    }

    public String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    public void showPhotoViewPopuWindow() {
        View parentView = LayoutInflater.from(PersonalInfoActivity.this).inflate(
                R.layout.view_photo_camera, null);

        if (popuWindow == null) {
            popuWindow = new PopupWindow(parentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popuWindow.setFocusable(true);
            popuWindow.setOutsideTouchable(true);
            popuWindow.update();
            popuWindow.setBackgroundDrawable(new BitmapDrawable());
            popuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1f;
                    getWindow().setAttributes(lp);
                    popuWindow = null;
                }
            });
        }
        parentView.findViewById(R.id.photo).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        photoClick();

                    }
                });
        parentView.findViewById(R.id.camera).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        cameraClick();

                    }
                });
        parentView.findViewById(R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissPopuWindow();
                    }
                });
        popuWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popuWindow.showAtLocation(ivAvatar, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);


    }

    public void dismissPopuWindow() {
        if (popuWindow != null) {
            popuWindow.dismiss();
        }

    }


    private void photoClick() {
        dismissPopuWindow();
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, PICTURE);
    }

    private void cameraClick() {
        if (popuWindow.isShowing())
            dismissPopuWindow();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA:
                    initCameraData(data);
                    break;
                case PICTURE:
                    initPictureData(data);
                    break;
                case REQUESTCODE_CUTOUT:
                    if (data != null) {
                        String path = data.getStringExtra(Constant.KEY_PATH);
                        if (!StringUtil.isEmpty(path) && new File(path).exists()) {
                            ImageLoader.getInstance().displayImage(
                                    "file://" + path, ivAvatar);

                            uploadImage(path);
                        } else {
                            Toast.makeText(this, "图片选择失败，请重新选择一张头像图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    setImageToHeadView(data);
                    break;
            }


        }
    }


    private void initCameraData(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            // 获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            if (bitmap != null) {
                String sNewImagePath = Constant.PATH + System.currentTimeMillis() + ".cache";
                boolean isSave = ImageUtils.saveImage(bitmap, sNewImagePath);
//                startActivityForResult(new Intent(PersonalInfoActivity.this,
//                                ImageCutOutListeningActivity.class).putExtra(
//                        Constant.KEY_PATH, sNewImagePath),
//                        REQUESTCODE_CUTOUT);
                Uri uri=Uri.fromFile(new File(sNewImagePath));
                cropRawPhoto(uri);
            }
        }
    }

    private void initPictureData(Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (!StringUtil.isEmpty(picturePath)) {
//                startActivityForResult(new Intent(PersonalInfoActivity.this,
//                                ImageCutOutListeningActivity.class).putExtra(
//                        Constant.KEY_PATH, picturePath),
//                        REQUESTCODE_CUTOUT);

                Uri uri=Uri.fromFile(new File(picturePath));
                cropRawPhoto(uri);
            }
        }
    }


    private void uploadImage(String filePath) {
        toastUtils.showProgress("上传头像中...");
        UploadFileManger.getInstance().uploadAvatar(PersonalInfoActivity.this, filePath, this);
    }

    private String hearUrl;
    @Override
    public void uploadSuccess(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toastUtils.dismiss();
                if (!TextUtils.isEmpty(url) && user != null) {
//            user.setAvatarUrl(url);
                    hearUrl=url;
                    ToastHelper.showShortMsg("头像上传成功");
                }
                else{
                    ToastHelper.showShortMsg("头像上传失败");
                }
            }
        });
    }

    @Override
    public void uploadFailed(int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toastUtils.dismiss();
                ToastHelper.showShortMsg("头像上传失败");
            }
        });
    }


    @Override
    public void rightViewClick() {
        super.rightViewClick();

        updateUserInfo();
    }

    private void updateUserInfo() {
        if (!StringUtil.isEmpty(etUserName.getText().toString())) {
            user.setName(etUserName.getText().toString());
        } else {
//            user.setName("default");
            ToastHelper.showShortMsg("用户名不可以为空");
            return;
        }

        if(!TextUtils.isEmpty(hearUrl) && user != null){
            user.setAvatarUrl(hearUrl);
        }

        if (!StringUtil.isEmpty(etPersonalHeight.getText().toString())) {
            int height = Integer.parseInt(etPersonalHeight.getText().toString());
            if (height > 300 || height < 0) {
                Toast.makeText(PersonalInfoActivity.this, "请输入正确身高值", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setHeight(height);
            }
        } else {
            Toast.makeText(PersonalInfoActivity.this, "请输入正确身高值", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!StringUtil.isEmpty(etPersonalWeight.getText().toString())) {
            float weight = Float.parseFloat(etPersonalWeight.getText().toString());
            if (weight > 300 || weight < 0) {
                Toast.makeText(PersonalInfoActivity.this, "请输入正确体重值", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setWeight(weight);
            }
        } else {
            Toast.makeText(PersonalInfoActivity.this, "请输入正确体重值", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setBirthday(tvBirthday.getText().toString());


        if (!TextUtils.isEmpty(tvEmail.getText())) {
            if (!StringUtil.isEmail(tvEmail.getText().toString())) {
                BaseApplication.showToast("邮箱格式不正确");
                return;
            } else
                user.setEmail(tvEmail.getText().toString());

        }
        user.setEmail(tvEmail.getText().toString());

        toastUtils.showProgress("");
        MemberController.getInstance().updateUserInfo(this, user, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                toastUtils.dismiss();
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    user = JsonUtil.parseDataObject(Json, User.class, "user");
                    Constant.LOGIN_USER = user;
                    SharedPreferencesUtils.saveUserInfoToPreferences(getApplicationContext());
                    SharedUtils.getInstance().saveJsonByTag(SharedTag.user_login, new Gson().toJson(user));

                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();
                    ToastHelper.showShortMsg("保存成功");
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.parseString(Json, "msg"));
                    finish();
                    ToastHelper.showShortMsg("保存失败");
                }
            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.showErrorWithStatus(json);
                finish();
                ToastHelper.showShortMsg("保存失败");
            }
        });
    }


    private static final int CODE_RESULT_REQUEST=1021;
    private Uri uritempFile;
    private void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        //把裁剪的数据填入里面

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
//        intent.putExtra("return-data", true);

        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }


    private void setImageToHeadView(Intent intent) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
            ivAvatar.setImageBitmap(bitmap);

            File nf = new File(Constant.PATH);
            nf.mkdir();
            //在根目录下面的ASk文件夹下 创建okkk.jpg文件
            File f = new File(Constant.PATH, "okkk.jpg");
            FileOutputStream out = null;
            try {//打开输出流 将图片数据填入文件中
                out = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                uploadImage(f.getAbsolutePath());
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "图片选择失败，请重新选择一张头像图片", Toast.LENGTH_SHORT).show();
        }
    }

}
