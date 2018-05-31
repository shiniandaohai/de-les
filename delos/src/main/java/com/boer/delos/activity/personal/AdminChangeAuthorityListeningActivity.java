package com.boer.delos.activity.personal;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.commen.BaseApplication;
import com.boer.delos.constant.Constant;
import com.boer.delos.constant.URLConfig;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Family;
import com.boer.delos.model.User;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.family.FamilyManageController;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.view.CircleImageView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 管理员修改用户权限界面
 * Created by Administrator on 2016/10/25 0025.
 */

public class AdminChangeAuthorityListeningActivity extends BaseListeningActivity {

    @Bind(R.id.ivUserHead)
    CircleImageView ivUserHead;
    @Bind(R.id.tvUserName)
    TextView tvUserName;

    @Bind(R.id.llSelectAll)
    LinearLayout llSelectAll;
    @Bind(R.id.llComfortLive)
    PercentRelativeLayout llComfortLive;
    @Bind(R.id.llHomeSecurity)
    PercentRelativeLayout llHomeSecurity;
    @Bind(R.id.llHealthLive)
    PercentRelativeLayout llHealthLive;
    @Bind(R.id.llGreenLive)
    PercentRelativeLayout llGreenLive;
    @Bind(R.id.llDeviceManage)
    PercentRelativeLayout llDeviceManage;
    @Bind(R.id.llAreaManage)
    PercentRelativeLayout llAreaManage;
    @Bind(R.id.llLinkManage)
    PercentRelativeLayout llLinkManage;

    @Bind(R.id.tvConfirmLimitBtn)
    TextView tvConfirmLimitBtn;
    @Bind(R.id.ivSelectAll)
    ImageView ivSelectAll;
    @Bind(R.id.ivComfortLiveChecked)
    ImageView ivComfortLiveChecked;
    @Bind(R.id.ivHomeSecurityChecked)
    ImageView ivHomeSecurityChecked;
    @Bind(R.id.ivHealthLiveChecked)
    ImageView ivHealthLiveChecked;
    @Bind(R.id.ivGreenLiveChecked)
    ImageView ivGreenLiveChecked;
    @Bind(R.id.ivDeviceManageChecked)
    ImageView ivDeviceManageChecked;
    @Bind(R.id.ivAreaManageChecked)
    ImageView ivAreaManageChecked;
    @Bind(R.id.ivLinkManageChecked)
    ImageView ivLinkManageChecked;

    private List<Map<String, Boolean>> mapList;
    private final static int AUTHORITY_NUM = 7; //权限个数
    private boolean change = false;
    private User user;
    private Family family;

    private String prePermission = "";
    private String[] permission = {"舒适生活", "家庭安全", "健康生活", "绿色生活", "设备管理", "场景管理", "联动管理"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_authority);
        ButterKnife.bind(this);
        initTopBar("更改权限", null, true, false);
        Bundle bundle = getIntent().getExtras();
        family = (Family) bundle.getSerializable("family");
        user = family.getUser();
        prePermission = family.getPermission();
        mapList = new ArrayList<>();
        initListMap(change);
        initData();
    }

    private void initData() {

        tvUserName.setText(user.getName());
        //加载头像
        if (!StringUtil.isEmpty(user.getAvatarUrl())) {
            if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivUserHead, BaseApplication.getInstance().displayImageOptions);
            } else if (user.getAvatarUrl().contains(URLConfig.HTTP)) {
                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), ivUserHead, BaseApplication.getInstance().displayImageOptions);
            } else {
                ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), ivUserHead, BaseApplication.getInstance().displayImageOptions);
            }
            // TODO 如果当前成员的头像url不为空，则加载其头像
            //ImageLoader.getInstance().displayImage(URLConfig.HTTP + user.getAvatarUrl(), childHolder.ivUserHead, BaseApplication.getInstance().displayImageOptions);
        } else {
            ivUserHead.setImageResource(R.drawable.ic_avatar);
        }
    }
//

    @OnClick({R.id.llSelectAll, R.id.llComfortLive, R.id.llHomeSecurity, R.id.llHealthLive, R.id.llGreenLive, R.id.llDeviceManage, R.id.llAreaManage, R.id.llLinkManage, R.id.tvConfirmLimitBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llSelectAll:
                change = !change;
                initListMap(change);
                SelectAll();
                break;
            case R.id.ivComfortLiveChecked:
            case R.id.llComfortLive:
                mapList.get(0).put("isSelected", !mapList.get(0).get("isSelected"));
                ivComfortLiveChecked.setVisibility(mapList.get(0).get("isSelected") ? View.VISIBLE : View.GONE);

                break;
            case R.id.ivHomeSecurityChecked:
            case R.id.llHomeSecurity:
                mapList.get(1).put("isSelected", !mapList.get(1).get("isSelected"));
                ivHomeSecurityChecked.setVisibility(mapList.get(1).get("isSelected") ? View.VISIBLE : View.GONE);
                break;
            case R.id.ivHealthLiveChecked:
            case R.id.llHealthLive:
                mapList.get(2).put("isSelected", !mapList.get(2).get("isSelected"));

                ivHealthLiveChecked.setVisibility(mapList.get(2).get("isSelected") ? View.VISIBLE : View.GONE);
                break;
            case R.id.ivGreenLiveChecked:
            case R.id.llGreenLive:
                mapList.get(3).put("isSelected", !mapList.get(3).get("isSelected"));

                ivGreenLiveChecked.setVisibility(mapList.get(3).get("isSelected") ? View.VISIBLE : View.GONE);
                break;
            case R.id.ivDeviceManageChecked:
            case R.id.llDeviceManage:
                mapList.get(4).put("isSelected", !mapList.get(4).get("isSelected"));

                ivDeviceManageChecked.setVisibility(mapList.get(4).get("isSelected") ? View.VISIBLE : View.GONE);
                break;
            case R.id.ivAreaManageChecked:
            case R.id.llAreaManage:
                mapList.get(5).put("isSelected", !mapList.get(5).get("isSelected"));

                ivAreaManageChecked.setVisibility(mapList.get(5).get("isSelected") ? View.VISIBLE : View.GONE);
                break;
            case R.id.ivLinkManageChecked:
            case R.id.llLinkManage:
                mapList.get(6).put("isSelected", !mapList.get(6).get("isSelected"));

                ivLinkManageChecked.setVisibility(mapList.get(6).get("isSelected") ? View.VISIBLE : View.GONE);
                break;
            case R.id.tvConfirmLimitBtn:
                if (user == null) {
                    return;
                }
                String userPermission = "";
                boolean isFirst = true;//去逗号用
                if (change) {
                    for (String s : permission) {
                        if (isFirst) {
                            userPermission += s;
                            isFirst = false;
                        } else
                            userPermission += "," + s;
                    }
                } else {
                    for (int i = 0; i < mapList.size(); i++) {
                        if (mapList.get(i).get("isSelected")) {
                            if (isFirst) {
                                userPermission += permission[i];
                                isFirst = false;
                            } else
                                userPermission += "," + permission[i];
                        }
                    }
                }

                toastUtils.showProgress("正在进行授权");

                adminChangeAuthority(user.getId(), userPermission);
                break;
        }
    }

    private void adminChangeAuthority(String userId, String userPerssion) {
        String hostId = Constant.CURRENTHOSTID;
        if (StringUtil.isEmpty(hostId) || StringUtil.isEmpty(userId)) {
            return;
        }
        FamilyManageController.getInstance().updateUserPermission(this, hostId, userId, userPerssion, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
//                toastUtils.showProgress("正在进行授权");
                BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                if (result.getRet() != 0) {
                    toastUtils.showErrorWithStatus(result.getMsg());
                    return;
                }
                toastUtils.showSuccessWithStatus("授权成功");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1300);
                L.i("A" + Json);

            }

            @Override
            public void onFailed(String Json) {
                toastUtils.showErrorWithStatus(Json);
                L.d("AdminChangeAuthorityListeningActivity adminChangeAuthority() " + Json);
            }
        });

    }

    private void initListMap(boolean change) {
        mapList.clear();
        for (int i = 0; i < AUTHORITY_NUM; i++) {
            Map<String, Boolean> map = new HashMap<>();
            map.put("isSelected", change);
//            change = !change;
            mapList.add(map);
        }
        if (!StringUtil.isEmpty(prePermission)) {
            for (int i = 0; i < permission.length; i++) {
                if (prePermission.contains(permission[i])) {
                    mapList.get(i).put("isSelected", true);
                }
            }
            SelectAll();
            prePermission = "";
        }
    }

    private void SelectAll() {

        ivSelectAll.setVisibility(change ? View.VISIBLE : View.GONE);

        ivComfortLiveChecked.setVisibility(mapList.get(0).get("isSelected") ? View.VISIBLE : View.GONE);
        ivHomeSecurityChecked.setVisibility(mapList.get(1).get("isSelected") ? View.VISIBLE : View.GONE);
        ivHealthLiveChecked.setVisibility(mapList.get(2).get("isSelected") ? View.VISIBLE : View.GONE);
        ivGreenLiveChecked.setVisibility(mapList.get(3).get("isSelected") ? View.VISIBLE : View.GONE);

        ivDeviceManageChecked.setVisibility(mapList.get(4).get("isSelected") ? View.VISIBLE : View.GONE);
        ivAreaManageChecked.setVisibility(mapList.get(5).get("isSelected") ? View.VISIBLE : View.GONE);
        ivLinkManageChecked.setVisibility(mapList.get(6).get("isSelected") ? View.VISIBLE : View.GONE);
    }

}
