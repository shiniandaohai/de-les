package com.boer.delos.activity.remotectler;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.RemoteDeviceModeAdapter;
import com.boer.delos.adapter.RemoteDeviceModeDetailAdapter;
import com.boer.delos.dao.DAO;
import com.boer.delos.dao.entity.DeviceBrandEntity;
import com.boer.delos.dao.entity.DeviceFormatsEntity;
import com.boer.delos.dao.entity.DeviceModelEntity;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceType;
import com.boer.delos.model.DeviceWithRemoteCtlInfo;
import com.boer.delos.model.RCAirConditionCmd;
import com.boer.delos.model.RCAirConditionCmdData;
import com.boer.delos.model.RCTVCmd;
import com.boer.delos.model.RemoteCMatchData;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备型号 - 格力A， 格力B。。。 品牌型号界面
 * Created by dell on 2016/7/14.
 * Modify: 数据库更新BREANDS.db 查询方式：以前是用m_format_id 去查询 fid的 ，现在是用m_keyfile去查询fid
 * ModifyTime: 2016/11/21
 */
public class DeviceModeListeningActivity extends BaseListeningActivity {
    public static final String BUNDLE_ACITON_DEVICE_ID = "DeviceModeActivity_device_id"; //从设备类型进来，传ID。
    public static final String BUNDLE_ACITON_DEVICE_TPYS = "DeviceModeActivity_device_type"; //从添加设备进来， 传type
    public static final String BUNDLE_ACITON_DEVICE_INFO = "DeviceModeActivity_device_info"; //从添加设备进来， 传deviceinfo
    //品牌
    private ListView mListView;
    private RemoteDeviceModeAdapter mAdapter;

    private List<DeviceBrandEntity> mBrandDatas;
    private List<DeviceModelEntity> mModelDatas = new ArrayList<>();

    //设备类型ID
    private int mDeviceTypeID;
    private String mDeviceTypeStr;
    private Device mDevice;

    DAO mDao;

    //快速搜索
    private ImageView img_search_fast;
    private EditText ev_search_fast;
    private List<DeviceBrandEntity> mBrandTemp;
    private List<DeviceBrandEntity> mBrandCache;
    private List<DeviceModelEntity> mModelTemp;
    private List<DeviceModelEntity> mModelCache;
    //品牌细节
    private RemoteDeviceModeDetailAdapter mAdapterDetail;
    private ListView mListViewDetail;
    private ImageView img_delete_text;

    private List<Integer> flagList = new ArrayList<Integer>();
    private static final int FLAG_TYPE = 0;
    private static final int FLAG_DETAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_device_type);

        mDevice = (Device) getIntent().getExtras().getSerializable(BUNDLE_ACITON_DEVICE_INFO);
        mDeviceTypeID = DeviceType.getDeviceIdByType(mDevice.getType());

        initView();
        initLisener();
    }

    private void initView() {
        initTopBar(R.string.remote_device_brand_model_title, null, true, false);

        mListView = (ListView) findViewById(R.id.device_type_lv);
        mListViewDetail = (ListView) findViewById(R.id.device_type_lv_detail);
        mListView.setVisibility(View.GONE);
        mListViewDetail.setVisibility(View.VISIBLE);

        ev_search_fast = (EditText) findViewById(R.id.ev_search_fast);
        img_search_fast = (ImageView) findViewById(R.id.img_search_fast);
        img_delete_text = (ImageView) findViewById(R.id.img_delete_text);


    }

    private void initLisener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toastUtils.showInfoWithStatus(mModelDatas.get(position).getmBrandanModel());
                updateDeviceRemoteControllerInfo(position);

            }
        });

        mListViewDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapterDetail.initFlagList(position);

                showSpecificModelOfBrand(mBrandDatas.get(position).getBrandname());
                mListView.setVisibility(View.VISIBLE);
                mListViewDetail.setVisibility(View.GONE);

            }
        });
        //快速搜索
        ev_search_fast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFast(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    img_delete_text.setVisibility(View.GONE);
                } else {
                    img_delete_text.setVisibility(View.VISIBLE);

                }

                searchFast(s.toString());
            }
        });

        img_search_fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFast(ev_search_fast.getText().toString().trim());
            }
        });
        img_delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ev_search_fast.setText("");
            }
        });
    }

    /**
     * 快速查询方法
     *
     * @param key 关键词
     */
    private void searchFast(String key) {
        if (StringUtil.isEmpty(key)) {

            mBrandDatas.clear();
            mBrandDatas.addAll(mBrandCache);
            mAdapterDetail.notifyDataSetChanged();

            if (mModelCache == null) return;
            mModelDatas.clear();
            mModelDatas.addAll(mModelCache);
            mAdapter.notifyDataSetChanged();

            return;
        }
        if (key.contains("-")) {
//            mListViewDetail.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
            String[] s = key.split("-");
            key = s[0];
        }
        if (mListViewDetail.getVisibility() == View.VISIBLE) {
            if (mBrandTemp == null) mBrandTemp = new ArrayList<>();
            mBrandTemp.clear();

            for (DeviceBrandEntity brand : mBrandCache) {
                if (brand.getBrandname().contains(key)) {

                    mBrandTemp.add(brand);
                }
            }
            mBrandDatas.clear();
            mBrandDatas.addAll(mBrandTemp);
            mAdapterDetail.notifyDataSetChanged();
        } else {

            if (mModelTemp == null) mModelTemp = new ArrayList<>();
            mModelTemp.clear();

            for (DeviceModelEntity model : mModelCache) {
                if (model.getmBrandanModel().contains(key)) {

                    mModelTemp.add(model);
                }
            }

            mModelDatas.clear();
            mModelDatas.addAll(mModelTemp);
            if (mAdapter == null) {
                mAdapter = new RemoteDeviceModeAdapter(this, mModelDatas);
                mListView.setAdapter(mAdapter);
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDao = DAO.getSingleton(this);
        mDao.connectDb(); //, 前面连过了。 这边就不用了。

        mBrandDatas = mDao.getDeviceBrandByDeviceTypeID(mDeviceTypeID); //全部型号

        //保存数据库查询到的数据
        if (mBrandCache == null) {
            mBrandCache = new ArrayList<>();
            mBrandCache.addAll(mBrandDatas);
        } else {
            mBrandCache.clear();
            mBrandCache.addAll(mBrandDatas);
        }

        //品牌
        if (mAdapterDetail == null) {
            mAdapterDetail = new RemoteDeviceModeDetailAdapter(this, mBrandDatas);
        }
        mListViewDetail.setAdapter(mAdapterDetail);

    }

    /**
     * 展示具体品牌的型号
     */
    private void showSpecificModelOfBrand(String brandname) {
        mModelDatas.clear();
        for (DeviceBrandEntity bt : mBrandDatas) {
            if (!bt.getBrandname().equals(brandname)) continue;

            L.e("fooo : + id " + mDeviceTypeID + "    " + bt.getModel_list());

            //model_lsit 是个字符数组。
            String m_codes[] = bt.getModel_list().split(",");

            for (int i = 0; i < m_codes.length; i++) {
//                L.e("fooo : + id "  + mDeviceTypeID + "    " + m_codes[i]);
            }
            for (String m_code : m_codes) {
                List<DeviceModelEntity> ldme = mDao.getDeviceModel(mDeviceTypeID, m_code);
                for (DeviceModelEntity dme : ldme) {
//                    String mSearchString = dme.getM_search_string();
//                    boolean s = mSearchString.equals(brandname);
//                    boolean ss = mSearchString.contains(brandname);

                    if (!dme.getM_search_string().contains(brandname)) { // add by sunzhibin 2017/2/28
                        // 查找到的m_search_string字段类似品牌
                        continue;
                    }
                    String label = dme.getM_label();
                    if (!TextUtils.isEmpty(label)) {
                        dme.setmBrandanModel(bt.getBrandname() + "-" + dme.getM_label());
                    } else {
                        dme.setmBrandanModel(bt.getBrandname() + "-" + dme.getM_code());
                    }
                    dme.setmBrandName(bt.getBrandname());

                    mModelDatas.add(dme);

                }
//                mModelDatas.clearAll();
//                mModelDatas.addAll(ldme);
            }

        }

        //保存查询到具体型号的数据
        if (mModelCache == null) mModelCache = new ArrayList<>();

        mModelCache.clear();
        mModelCache.addAll(mModelDatas);

        //具体型号
        if (mAdapter == null) {
            mAdapter = new RemoteDeviceModeAdapter(this, mModelDatas);
        }
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    /**
     * 更新设备遥控器信息.
     *
     * @param position 数据position
     */
    private void updateDeviceRemoteControllerInfo(int position) {
        DeviceModelEntity dme = mModelDatas.get(position);
//        int fid = Integer.valueOf(dme.getM_format_id());  //modify :2016/11/21
        int fid = Integer.valueOf(dme.getM_keyfile());
        int deviceid = dme.getDevice_id();

        DeviceFormatsEntity dfe = mDao.getDeviceFormat(fid, deviceid);
        //用来匹配遥控数据的格式。
        RemoteCMatchData rcd = new RemoteCMatchData();
        rcd.setBrandname(dme.getmBrandName());
        rcd.setC3rv(dfe.getC3rv());
        rcd.setDevice_type(mDeviceTypeID);
        rcd.setFid(dfe.getFid());
        rcd.setFormat_string(dfe.getFormat_string());
        rcd.setM_code(dme.getM_code());
        Log.i("gwq", "key=" + dme.getM_key_squency());
        rcd.setM_key_squency(String.valueOf(dme.getM_key_squency()));
        if (TextUtils.isEmpty(dme.getM_label())) {
            rcd.setM_label("");
        } else {
            rcd.setM_label(dme.getM_label());
        }
        //配置空调参数信息
        /*
   conoff ,cmode ,ctemp, cwind ,cwinddir, ckey
   1,开关（2）： 0=开，1=关
   2,运转模式（5）： 0=自动 ，1=制冷， 2=除湿， 3=送风， 4=制热
   3,温度（15）： 16-30度  0=16 。。。。 14=30
           4,风速（4）： 0=自动，1=风速1，2=风速2，3=风速3
   5,风向（5）： 0=自动，1=风向1，2=风向2，3=风向3，4=风向4
   6,键值（5）： 0=开关，1=运转模式，2=温度，3=风量，4=风向
   */
        RCAirConditionCmdData conditionCmdData = new RCAirConditionCmdData();
        if (mDevice.getType().equals(DeviceType.AIRCONTIDION)) {
            conditionCmdData.setcOnoff(1);
            conditionCmdData.setcMode(0);
            conditionCmdData.setcWind(0);//风速
            conditionCmdData.setcWinddir(0);
            conditionCmdData.setcTemp(0);
            conditionCmdData.setcKey(0);
        }
        Device device = mDevice;
        device.setRemoteInfo(rcd);
        device.setRcAirConditionCmdData(conditionCmdData);
        DeviceWithRemoteCtlInfo deviceWithRemoteCtlInfo = new DeviceWithRemoteCtlInfo(mDevice.getRoomname());
        deviceWithRemoteCtlInfo.setmDevice(mDevice);
        deviceWithRemoteCtlInfo.setRemoteInfo(rcd);

        DeviceController.getInstance().updateProp(this, device, "true", new RequestResultListener() {

            //WifiAirCleanController.getInstance().updateProp(this, deviceWithRemoteCtlInfo, "true", new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.e("modeupdateProp onSuccess's json===" + Json);
                String ret = JsonUtil.parseString(Json, "ret");

                String response = JsonUtil.parseString(Json, "response");


                if (ret != null && "0".equals(ret)) {
//                    toastUtils.showSuccessWithStatus("更新成功");

                    switch (mDeviceTypeID) {
                        case DeviceType.idAirCondition:
                            //RCAirConditionCmd acCmdRes = new Gson().fromJson(response, RCAirConditionCmd.class);
                            Device airDevice = new Gson().fromJson(response, Device.class);
                            RCAirConditionCmd acCmdRes = new RCAirConditionCmd();
                            acCmdRes.setAddr(airDevice.getAddr());
                            acCmdRes.setAreaName(JsonUtil.parseString(response, "areaname"));
                            acCmdRes.setDeviceName(JsonUtil.parseString(response, "name"));
                            acCmdRes.setRoomName(JsonUtil.parseString(response, "roomname"));
                            //acCmdRes.setValue(JsonUtil.parseDataObject(response, RemoteCMatchData.class, "remoteInfo"));
                            acCmdRes.setValue(JsonUtil.parseDataObject(response, RemoteCMatchData.class, "remoteInfo"));//与上面不同点在于参数声明时参数名不同
                            acCmdRes.setAcData(JsonUtil.parseDataObject(response, RCAirConditionCmdData.class, "rcAirConditionCmdData"));


                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("device", acCmdRes);
                            // change by sunzhibin ?? RCAirConditionCmd

                            bundle.putSerializable("device", airDevice);

                            Intent intent = new Intent(DeviceModeListeningActivity.this, AirConditionControllerListeningActivity.class);
                            intent.putExtras(bundle);
                            DeviceModeListeningActivity.this.startActivity(intent);
                            break;
                        case DeviceType.idTV:
                            Device tvDevice = new Gson().fromJson(response, Device.class);
                            //RCTVCmd cmdTVRes = new Gson().fromJson(response, RCTVCmd.class);
                            RCTVCmd cmdTVRes = new RCTVCmd();
                            cmdTVRes.setAddr(tvDevice.getAddr());
                            cmdTVRes.setAreaName(JsonUtil.parseString(response, "areaname"));
                            cmdTVRes.setDeviceName(JsonUtil.parseString(response, "name"));
                            cmdTVRes.setRoomName(JsonUtil.parseString(response, "roomname"));
                            cmdTVRes.setValue(JsonUtil.parseDataObject(response, RemoteCMatchData.class, "remoteInfo"));

                            L.e("fooo " + cmdTVRes.getAddr());
                            L.e("fooo " + cmdTVRes.getAreaName());
                            L.e("fooo " + cmdTVRes.getDeviceName());
                            L.e("fooo " + cmdTVRes.getExtraCmd());
                            L.e("fooo " + cmdTVRes.getType());
                            L.e("fooo " + cmdTVRes.getRoomName());

                            if (cmdTVRes.getValue() == null) {
                                L.e("fooo cmd remoteinfo err");
                            } else {
                                L.e("fooo cmd " + cmdTVRes.getValue().toString());
                            }


                            Bundle bundle_b = new Bundle();
                            bundle_b.putSerializable(TVControllerListeningActivity.BUNDLE_ACITON_DEVICE_CONTROL_CMD, cmdTVRes);
                            Intent intent_b = new Intent(DeviceModeListeningActivity.this, TVControllerListeningActivity.class);
                            intent_b.putExtras(bundle_b);
                            startActivity(intent_b);
                            break;
                    }

                    finish();
                } else {
                    toastUtils.showErrorWithStatus(JsonUtil.ShowMessage(Json));
                }
            }

            @Override
            public void onFailed(String json) {
                toastUtils.showErrorWithStatus(json);
                L.e("fooo" + json);
            }
        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mListViewDetail.getVisibility() != View.VISIBLE) {

                mListViewDetail.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                mBrandDatas.clear();
                mBrandDatas.addAll(mBrandCache);
                mAdapterDetail.initFlagList(-1);

                mAdapterDetail.notifyDataSetChanged();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
