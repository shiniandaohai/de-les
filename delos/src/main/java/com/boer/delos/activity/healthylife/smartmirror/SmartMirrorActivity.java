package com.boer.delos.activity.healthylife.smartmirror;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.camera.zxing.activity.CaptureActivity;
import com.boer.delos.adapter.MirrorAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.SmartMirror;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.health.HealthController;
import com.boer.delos.view.popupWindow.ShowRequestEditPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by gaolong on 2017/4/22.
 */
public class SmartMirrorActivity extends CommonBaseActivity implements MirrorAdapter.IClickResult {
    @Bind(R.id.lst_mirror)
    ListView lstMirror;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    private final int REQ_SCAN = 1000;
    private final int REQ_MIRROR_LOGIN = 1001;
    private HealthController healthController;
    private List<SmartMirror> list;
    private MirrorAdapter mirrorAdapter;

    private ShowRequestEditPopupWindow showRequestEditPopupWindow;

    @Override
    protected int initLayout() {
        return R.layout.activity_smart_mirror;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.mirror_title);
        tlTitleLayout.setLinearRightImage(R.mipmap.scan);

    }

    @Override
    protected void initData() {
        healthController = new HealthController();
        list = new ArrayList<>();
        mirrorAdapter = new MirrorAdapter(this, list);
        lstMirror.setAdapter(mirrorAdapter);
        mirrorAdapter.setClickListener(this);

        showRequestEditPopupWindow = new ShowRequestEditPopupWindow(this, tlTitleLayout, getResources().getString(R.string.mirror_bind_name_title));


        loginMirror();
    }

    @Override
    protected void initAction() {

    }


    @Override
    public void rightViewClick() {
        super.rightViewClick();
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQ_SCAN);
    }


    private void loginMirror() {

        toastUtils.showProgress("");
        healthController.showSmartMirrors(this, Constant.LOGIN_USER.getMobile(), new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                toastUtils.dismiss();


                try {
                    JSONObject jsonObject = new JSONObject(json);

                    if (jsonObject.has("mirrors")) {

                        Gson gson = new Gson();
                        List<SmartMirror> mirrors = gson.fromJson(jsonObject.getString("mirrors"), new TypeToken<List<SmartMirror>>() {
                        }.getType());
                        list.clear();
                        list.addAll(mirrors);
                        mirrorAdapter.notifyDataSetChanged();
                    }

                    initStyle();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();

            }
        });


    }

    private void initStyle() {

        if (list.size() > 0) {

            lstMirror.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.GONE);

        } else {

            lstMirror.setVisibility(View.GONE);
            tvTip.setVisibility(View.VISIBLE);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SCAN && resultCode == RESULT_OK) {


            String result = data.getStringExtra("result");


            if (!TextUtils.isEmpty(result)) {

                Intent intent = new Intent(this, SmartMirrorLoginActivity.class);
                intent.putExtra("result", result);
                startActivityForResult(intent, REQ_MIRROR_LOGIN);


            }

        } else if (requestCode == REQ_MIRROR_LOGIN && resultCode == RESULT_OK) {

            loginMirror();

        }
    }

    @Override
    public void reName(final String reName, final String id, final int pos) {

        showRequestEditPopupWindow.setShowRequestPopupWindow(new ShowRequestEditPopupWindow.IShowRequest() {
            @Override
            public void rightButtonClick(String name) {


                reNameMirror(name, id, pos);


            }
        });
        showRequestEditPopupWindow.showPopupWindow();


    }

    private void reNameMirror(final String name, String id, final int pos) {

        toastUtils.showProgress("");
        healthController.reNameSmartMirrors(this, Constant.LOGIN_USER.getMobile(), id, name, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                toastUtils.dismiss();


                list.get(pos).setRemark(name);
                mirrorAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();

            }
        });


    }

    @Override
    public void offLine(String id,int pos) {

        offLineMirror(id,pos);


    }

    private void offLineMirror(String id, final int pos) {

        toastUtils.showProgress("");
        healthController.offLineSmartMirrors(this, Constant.LOGIN_USER.getMobile(), id, new RequestResultListener() {
            @Override
            public void onSuccess(String result) {
                toastUtils.dismiss();


                list.remove(pos);
                mirrorAdapter.notifyDataSetChanged();


                Log.v("gl","result==="+result);





            }

            @Override
            public void onFailed(String json) {
                if (toastUtils != null)
                    toastUtils.dismiss();

            }
        });


    }
}
