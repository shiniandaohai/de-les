package com.boer.delos.activity.personal;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.Family;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.member.MemberController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author PengJiYang
 * @Description: "家人资料"界面
 * create at 2016/5/25 19:54
 */
public class FamilyDataListeningActivity extends BaseListeningActivity {

    private CircleImageView ivAvatar;// 头像
    private ImageView ivSex;// 性别
    private TextView tvPhone;// 手机号
    private TextView tvOnline;// 是否在线
    private TextView textViewName;//姓名
    private EditText etNote;// 备注
    private TextView etSignature;// 个性签名
    private ImageView ivConstellation;// 星座
    private TextView etPeopleHeight;// 身高
    private TextView etPeopleWeight;// 体重

    private Family family;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_data);

        family = (Family) getIntent().getSerializableExtra("FamilyData");
        user = family.getUser();
        String.valueOf(0);
        initView();
        setFamilyData();

    }

    private void initView() {
        vTitle = findViewById(R.id.vTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = vTitle.getLayoutParams();
            params.height = ScreenUtils.getStatusHeight(this);
            vTitle.setLayoutParams(params);
            vTitle.setBackgroundColor(getResources().getColor(R.color.layout_title_bg));
            vTitle.setVisibility(View.VISIBLE);
        } else {
            vTitle.setVisibility(View.GONE);
        }
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvRight = (TextView) findViewById(R.id.tvRight);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        tvTitle.setText(R.string.family_data_title);

        ivBack.setVisibility(View.VISIBLE);

        ivRight.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReMark(family.getHostId(), family.getUserId(), etNote.getText().toString(), family.getHostAlias());
            }
        });

        etPeopleWeight = (TextView) findViewById(R.id.etPeopleWeight);
        etPeopleHeight = (TextView) findViewById(R.id.etPeopleHeight);
        textViewName = (TextView) findViewById(R.id.etName);
        ivConstellation = (ImageView) findViewById(R.id.ivConstellation);
        etSignature = (TextView) findViewById(R.id.etSignature);
        etNote = (EditText) findViewById(R.id.etNote);
        tvOnline = (TextView) findViewById(R.id.tvOnline);// TODO 通过那个字段判断是否在线
        tvPhone = (TextView) findViewById(R.id.tvName);
        ivSex = (ImageView) findViewById(R.id.ivSex);
        ivAvatar = (CircleImageView) findViewById(R.id.ivAvatar);
    }

    /**
     * 填充界面上的数据
     */
    private void setFamilyData() {
        ivSex.setImageResource(Constant.sexImageId(user.getSex()));
        tvPhone.setText(user.getMobile());
        etNote.setText(family.getUserAlias()); // 备注
        textViewName.setText(user.getName());
        etSignature.setText(user.getRemark()); // 个性签名
        etPeopleHeight.setText(user.getHeight() + "");
        etPeopleWeight.setText(user.getWeight() + "");

        ivConstellation.setImageResource(Constant.constellationImageId(user.getConstellation()));
        L.e("user.getAvatarUrl()" + user.getAvatarUrl());
        if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
            ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
        } else if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
            ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
        } else {
            ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), ivAvatar, BaseApplication.getInstance().displayImageOptions);
        }
    }


    /**
     * 修改备注
     */
    private void updateReMark(String hostId, String userId, String userAlias, String hostAlias) {

        toastUtils.showProgress("正在更新个人信息...");

        MemberController.getInstance().updateAlias(this, hostId, userId, userAlias, hostAlias, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("FamilyDataListeningActivity updateReMark===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");
                if ("0".equals(ret)) {
                    toastUtils.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    finish();
//                    toastUtils.showErrorWithStatus("修改失败");
                }

            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (StringUtil.isEmpty(family.getUserAlias())
                    || !family.getUserAlias().equals(etNote.getText().toString())) {
                updateReMark(family.getHostId(), family.getUserId(),
                        etNote.getText().toString(), family.getHostAlias());
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return false;
    }
}
