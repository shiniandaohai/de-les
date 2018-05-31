package com.boer.delos.activity.healthylife.wifiAirClean;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.camera.zxing.activity.CaptureActivity;
import com.boer.delos.adapter.WifiAirCleanAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.WifiAirCleanDeviceInfo;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.wifiAirClean.WifiAirCleanController;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.view.customDialog.CustomFragmentDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.boer.delos.R.id.tvAgree;

/**
 * Created by Administrator on 2017/11/27.
 */

public class WifiAirCleanManagerActivity extends CommonBaseActivity {
    @Bind(R.id.lv_show_device)
    PullToRefreshListView mPullToRefreshListView;
    @Bind(R.id.tv_no_data)
    TextView tvNoData;
    private ListView mLvShowDevice;
    private WifiAirCleanAdapter mDeviceAdapter;
    private List<WifiAirCleanDeviceInfo> mDeviceList = new ArrayList<>();
    public ToastUtils toastUtils;

    @Override
    protected int initLayout() {
        return R.layout.activity_wifi_air_clean_manager;
    }

    @Override
    protected void initView() {
        tlTitleLayout.setTitle("空气净化器");
        tlTitleLayout.setLinearRightImage(R.mipmap.ic_nav_home_add);
        mLvShowDevice = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mLvShowDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiAirCleanDeviceInfo wifiAirCleanDeviceInfo=mDeviceAdapter.getItem(position-mLvShowDevice.getHeaderViewsCount());
                startActivity(new Intent(mContext,WifiAirCleanDetailsActivity.class).putExtra("info",wifiAirCleanDeviceInfo));
            }
        });
        toastUtils = new ToastUtils(this);
    }

    @Override
    protected void initData() {
        mDeviceAdapter = new WifiAirCleanAdapter(this, mDeviceList, R.layout.item_wifi_air_clean_manager);
        mLvShowDevice.setAdapter(mDeviceAdapter);
        mDeviceAdapter.setItemViewOnclickListener(new WifiAirCleanAdapter.ItemViewOnclickListener() {
            @Override
            public void onReName(WifiAirCleanDeviceInfo item, String oldName) {
                reName(item, oldName);
            }

            @Override
            public void onUnBind(WifiAirCleanDeviceInfo item) {
                unBind(item);
            }

            @Override
            public void onResetNetwork(WifiAirCleanDeviceInfo item) {
                resetNetwork(item);
            }
        });
        mPullToRefreshListView.setRefreshing();
        requestDeviceInfo();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        requestDeviceInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPullToRefreshListView != null && mPullToRefreshListView.isRefreshing()) {
            mPullToRefreshListView.onRefreshComplete();
            mPullToRefreshListView = null;
        }
    }

    @Override
    protected void initAction() {
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestDeviceInfo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestDeviceInfo();
    }

    @Override
    public void rightViewClick() {
        showOption();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUESTCODE_SCAN_QRCODE) {
                String result=data.getStringExtra("result");
                if(result.contains("sn")&&result.contains("nickName")){
                    doCommit(result);
                }
                else{
                    ToastHelper.showShortMsg("无效的设备分享二维码");
                }
            }
        }
    }

    private static final int REQUESTCODE_SCAN_QRCODE = 1;

    private void showOption() {
        View popupView = View.inflate(this, R.layout.popupview_normal, null);
        final PopupWindow window = new PopupWindow(this);
        popupView.findViewById(R.id.tv_add_new_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                startActivity(new Intent(WifiAirCleanManagerActivity.this, WifiAirCleanChoiceTypeActivity.class));
            }
        });
        popupView.findViewById(R.id.tv_scan_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                startActivityForResult(new Intent(WifiAirCleanManagerActivity.this, CaptureActivity.class), REQUESTCODE_SCAN_QRCODE);
            }
        });
        window.setBackgroundDrawable(null);
        window.setTouchable(true);
        window.setOutsideTouchable(true);
        window.setContentView(popupView);
        window.update();
        window.showAsDropDown(tlTitleLayout.findViewById(R.id.id_right));
    }

    String Uid;

    private void requestDeviceInfo() {
        if (StringUtil.isEmpty(Uid)) {
            doThirdReg();
        } else {
            doGetBingDevice();
        }
    }

    private void doThirdReg() {
        String mobile=Constant.LOGIN_USER.getMobile().equals("")?"12345678901":Constant.LOGIN_USER.getMobile();
        WifiAirCleanController.getInstance().thirdReg(this, mobile,
                Constant.LOGIN_USER.getId(), new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            if (code == 0) {
                                JSONObject obj = jsonObject.getJSONObject("obj");
                                Uid = obj.getString("id");
                                Constant.WIFI_AIR_CLEAN_UID = Uid;
                                doGetBingDevice();
                            } else {
//                                ToastHelper.showShortMsg("获取设备列表失败");
                                mPullToRefreshListView.onRefreshComplete();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            ToastHelper.showShortMsg("获取设备列表失败");
                            mPullToRefreshListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
//                        ToastHelper.showShortMsg("获取设备列表失败");
                        mPullToRefreshListView.onRefreshComplete();
                    }
                });
    }

    private void doGetBingDevice() {
        WifiAirCleanController.getInstance().userGetBingDevice(this, Uid, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                mPullToRefreshListView.onRefreshComplete();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONArray objs = jsonObject.getJSONArray("obj");
                        List<WifiAirCleanDeviceInfo> wifiAirCleanDeviceInfos = new Gson().
                                fromJson(objs.toString(), new TypeToken<List<WifiAirCleanDeviceInfo>>() {
                                }.getType());
                        mDeviceList.clear();
                        mDeviceAdapter.setCurPosition(-1);
                        mDeviceList.addAll(wifiAirCleanDeviceInfos);
                        mDeviceAdapter.notifyDataSetChanged();
                        if(mDeviceAdapter.getCount()>0){
                            tvNoData.setVisibility(View.GONE);
                        }
                        else{
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
//                                ToastHelper.showShortMsg("获取设备列表失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                            ToastHelper.showShortMsg("获取设备列表失败");
                }
            }

            @Override
            public void onFailed(String json) {
                mPullToRefreshListView.onRefreshComplete();
//                ToastHelper.showShortMsg("获取设备列表失败");
            }
        });
    }

    private CustomFragmentDialog deleteDialog;

    private void unBind(final WifiAirCleanDeviceInfo item) {
        if (deleteDialog != null) {
            deleteDialog.dismiss();
        }
        if (deleteDialog == null) {
            deleteDialog = CustomFragmentDialog.newInstanse(
                    getString(R.string.text_prompt),
                    "确认要解绑空气净化器吗？", false);
        }
        deleteDialog.show(getSupportFragmentManager(), null);
        deleteDialog.setListener(new CustomFragmentDialog.EditComfireDialogListener() {
            @Override
            public void onComfire(String inputText) {
                deleteDialog.dismiss();
                doUnBind(item);
            }
        });
    }

    private void reName(final WifiAirCleanDeviceInfo item, String oldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alertDialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_rename, null);
        final EditText etContent = (EditText) view.findViewById(R.id.etContent);
        etContent.setText(oldName);
        etContent.setSelection(etContent.getText().length());
        view.findViewById(R.id.tvDisagree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(tvAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                doReName(item, etContent.getText().toString());
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void doReName(WifiAirCleanDeviceInfo item, String newName) {
        if (StringUtil.isEmpty(newName)) {
            ToastHelper.showShortMsg("名称不能为空");
            return;
        }
        if (newName.length()>8) {
            ToastHelper.showShortMsg("设备名称不能超过8位");
            return;
        }
        toastUtils.showProgress("");
        WifiAirCleanController.getInstance().bingDevice(mContext, item.getSn(), Uid, newName, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        requestDeviceInfo();
                    } else {
                        ToastHelper.showShortMsg(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastHelper.showShortMsg("重命名失败");
                }
                toastUtils.dismiss();
            }

            @Override
            public void onFailed(String json) {
                ToastHelper.showShortMsg("重命名失败");
                toastUtils.dismiss();
            }
        });
    }

    private void doUnBind(WifiAirCleanDeviceInfo item) {
        toastUtils.showProgress("");
        WifiAirCleanController.getInstance().cancelBingDevice(mContext, item.getSn(), Uid, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        requestDeviceInfo();
                    } else {
                        ToastHelper.showShortMsg(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastHelper.showShortMsg("解绑失败");
                }
                toastUtils.dismiss();
            }

            @Override
            public void onFailed(String json) {
                ToastHelper.showShortMsg("解绑失败");
                toastUtils.dismiss();
            }
        });
    }

    private void doCommit(String result) {
        if(StringUtil.isEmpty(result)){
            ToastHelper.showShortMsg("添加新设备失败");
            return;
        }
        String sn="";
        String nickName="";
        try {
            JSONObject resultJson=new JSONObject(result);
            sn=resultJson.getString("sn");
            nickName=resultJson.getString("nickName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        toastUtils.showProgress("");
        WifiAirCleanController.getInstance().bingDevice(mContext, sn, Constant.WIFI_AIR_CLEAN_UID, nickName, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        startActivity(new Intent(mContext,WifiAirCleanManagerActivity.class));
                    } else {
                        ToastHelper.showShortMsg(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastHelper.showShortMsg("添加新设备失败");
                }
                toastUtils.dismiss();
            }

            @Override
            public void onFailed(String json) {
                ToastHelper.showShortMsg("添加新设备失败");
                toastUtils.dismiss();
            }
        });
    }

    private void resetNetwork(WifiAirCleanDeviceInfo item) {
        if(item.getDeviceName().equals("T30S")){
            startActivity(new Intent(mContext,WifiAirCleanAddNewDeviceActivity.class).putExtra("type","T30")
            .putExtra("sn",item.getSn()));
        }
        else if(item.getDeviceName().equals("T66_II")){
            startActivity(new Intent(mContext,WifiAirCleanAddNewDeviceActivity.class).putExtra("type","T66")
                    .putExtra("sn",item.getSn()));
        }
    }
}
