package com.boer.delos.activity.scene;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.BaseResult;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.DeviceRelateResult;
import com.boer.delos.model.DeviceStatus;
import com.boer.delos.model.HGCAirConditionConfig;
import com.boer.delos.model.HGCCurtainConfig;
import com.boer.delos.model.HGCLightConfig;
import com.boer.delos.model.HGCSceneConfig;
import com.boer.delos.model.HGCSocketConfig;
import com.boer.delos.model.Link;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 中控面板设置界面
 */
public class DeviceSettingListeningActivity extends BaseListeningActivity implements
        RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.id_textViewMatching)
    TextView mIdTextViewMatching;
    @Bind(R.id.id_textViewMode1)
    TextView mIdTextViewMode1;
    @Bind(R.id.id_gridViewMode1)
    GridView mIdGridViewMode1;
    @Bind(R.id.id_textViewMode2)
    TextView mIdTextViewMode2;
    @Bind(R.id.id_gridViewMode2)
    GridView mIdGridViewMode2;
    @Bind(R.id.rb_scence)
    RadioButton mRbScence;
    @Bind(R.id.rb_air)
    RadioButton mRbAir;
    @Bind(R.id.rb_socket)
    RadioButton mRbSocket;
    @Bind(R.id.rb_window)
    RadioButton mRbWindow;
    @Bind(R.id.rg_tab)
    RadioGroup mRgTab;
    @Bind(R.id.tvTitle)
    TextView mTvTitle;
    @Bind(R.id.rb_light)
    RadioButton mRbLight;

    private gridViewAdapter mGridViewAdapter1;
    private HardwareGridViewAdapter mGridViewAdapter2;

    /**
     * 第一个场景模式数据写死
     */
    //第一个场景模式的数据和图片列表
    private List<String> textViewValue1 = new ArrayList<>();  //文字
    private List<String> adapter1Value = new ArrayList<>();
    //第一个场景模式的数据，包括textView显示的值，显示图片的资源，是否显示匹配图片，用“，”分开
    private List<ArrayList<String>> value1 = new ArrayList<>();
    //第二个场景模式的数据和图片列表
    private List<int[]> hardWareImgList; //存放图片
    private List<Device> hardWareDeviceList;//Adapter用
    private List<Device> deviceAllList; //存放所有数据
    private List<Device> deviceList; //存放过滤过的数据
    private List<Device> deviceTempList;
    private List<DeviceStatus> devideStatusList;

    private List<Boolean> flagLinkedListSoft;//是否关联的标签
    private List<Boolean> flagLinkedList2Hard;//是否关联的标签
    private List<Boolean> flagSelectListSoft; //处理背景图片用
    private List<Boolean> flagSelectList2Hard;
    //第一层： 每一个切换的界面 key: 界面标记
    //第二层： 绑定信息 List 的position: 通道 value: MAC
    private Map<String, List<String>> mFlagListLinked;
    private List<Integer> mHardOrderList; //用来将绑定过的数据顺序排序
    private boolean mIsPairing = false; //是否是匹配模式
    private String mSelectShow = ""; //选中位置的和他的相关绑定的展示《背景变化》
    private static String HGCADDR; // 中控的MAC地址

    private final static int CHANNEL_SOCKET_NUM = 8; //中控插座通道数
    private final static int CHANNEL_AIRCONDITION_NUM = 2; //中控空调通道数
    private final static int CHANNEL_SCENE_NUM = 6; //中控场景通道数
    private final static int CHANNEL_LIGHT_NUM = 8; //中控灯光通道数
    private final static int CHANNEL_CURTAIN_NUM = 8; //中控窗帘、红外通道数

    private final static String V_TYPE_LIGHT = "0x1";
    private final static String V_TYPE_SOCKET = "0x2";
    private final static String V_TYPE_CURTAIN = "0x4";
    private final static String V_TYPE_SCENE = "0xF";
    private final static String V_TYPE_AIRCONDITION = "0x50";
    private static String V_TYPE_CURRENT = V_TYPE_SCENE;//默认
    private static Map<String, Object> mDevicesControlsInfoMap; //用来保存设备控制信息
    private final static int DELAYTIME = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        ButterKnife.bind(this);
        initTopBar("场景配置", null, true, false);
        Bundle bundle = getIntent().getExtras();
        HGCADDR = bundle.getString("HGCADDR");

        initView();
        initListener();

    }

    private void init() {
        hardWareImgList = new ArrayList<>();
        hardWareDeviceList = new ArrayList<>();//Adapter用
        deviceAllList = new ArrayList<>(); //存放所有数据
        deviceList = new ArrayList<>(); //存放过滤过的数据
        deviceTempList = new ArrayList<>();
        devideStatusList = new ArrayList<>();

        flagLinkedListSoft = new ArrayList<>();//是否关联的标签
        flagLinkedList2Hard = new ArrayList<>();//是否关联的标签
        flagSelectListSoft = new ArrayList<>(); //处理背景图片用
        flagSelectList2Hard = new ArrayList<>();
        mFlagListLinked = new HashMap<>(); //存储关联信息
        mDevicesControlsInfoMap = new HashMap<>();
        mHardOrderList = new ArrayList<>();
    }

    private void initView() {
        String source = "<font color='#555555'>场景模式(</font><font color='#00a0e9'>中控</font><font color='#555555'>)</font>";
        mIdTextViewMode1.setText(Html.fromHtml(source));

        String source2 = "<font color='#555555'>场景模式(</font><font color='#00a0e9'>家卫士</font><font color='#555555'>)</font>";
        mIdTextViewMode2.setText(Html.fromHtml(source2));

        mTvTitle.setText("场景配置");

    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        initData();
        queryHGC(HGCADDR, V_TYPE_SCENE);
//        loadingGateWayDeviceStatus();
        getDeviceStatus();
    }

    private void initData() {
        initializeMode1();

        mIdGridViewMode1.setClickable(mIsPairing);
        mIdGridViewMode2.setClickable(mIsPairing);

        adapter1Value = value1.get(0);
        mGridViewAdapter1 = new gridViewAdapter(DeviceSettingListeningActivity.this, adapter1Value);
        mIdGridViewMode1.setAdapter(mGridViewAdapter1);

        //主机的数据才对啊
        mGridViewAdapter2 = new HardwareGridViewAdapter(DeviceSettingListeningActivity.this, hardWareDeviceList);
        mIdGridViewMode2.setAdapter(mGridViewAdapter2);
        //场景

        initSecenModel(Constant.GLOBAL_MODE);
        initFlagListSelectInfo(CHANNEL_SCENE_NUM, -1);
        initFlagListLinkedInfo(CHANNEL_SCENE_NUM);
    }

    private void initListener() {
        mRgTab.setOnCheckedChangeListener(this);
        //切换模式按钮
        mIdTextViewMatching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPairing = !mIsPairing;
                mIdGridViewMode1.setClickable(mIsPairing);
                mIdGridViewMode2.setClickable(mIsPairing);
                mIdTextViewMatching.setText(!mIsPairing ? getResources().getString(R.string.device_setting_activity_open_pairing) : getResources().getString(R.string.device_setting_activity_close_pairing));
                int size = channelNUM(V_TYPE_CURRENT);
                initFlagListSelectInfo(size, -1);
                mSelectShow = "";
                mGridViewAdapter1.notifyDataSetChanged();
                mGridViewAdapter2.notifyDataSetChanged();
            }
        });
        mIdGridViewMode1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 就是通道
                if (mIsPairing) {
                    changeSoftLink(position, V_TYPE_CURRENT);
                }

            }
        });

        mIdGridViewMode2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mIsPairing) {
                    changeHardLink(position, V_TYPE_CURRENT);
                }

            }
        });
        mIdGridViewMode1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState != SCROLL_STATE_IDLE) {
                    mGridViewAdapter1.setmGridViewScroll(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mIdGridViewMode2.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState != SCROLL_STATE_IDLE) {
                    mGridViewAdapter2.setmGridViewScroll(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    /**
     * 查询主机的所有设备 和设备的状态
     */
    private void loadingGateWayDeviceStatus() {
        DeviceController.getInstance().queryDeviceRelateInfo(this, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                try {
                    Json = StringUtil.deviceStateStringReplaceMap(Json);
                    DeviceRelateResult result = new Gson().fromJson(Json, DeviceRelateResult.class);
                    if (result.getRet() != 0) {
                        toastUtils.showErrorWithStatus(result.getMsg());

                        return;
                    }
                    List<DeviceRelate> deviceRelateList = result.getResponse();
                    if (devideStatusList == null) devideStatusList = new ArrayList<DeviceStatus>();

                    deviceAllList.clear();
                    for (DeviceRelate deviceRelate : deviceRelateList) {
                        deviceAllList.add(deviceRelate.getDeviceProp());
                        devideStatusList.add(deviceRelate.getDeviceStatus());
                    }
                    //过滤掉未绑定的设备
                    deviceList.clear();
                    for (Device d : deviceAllList) {
                        if (d.getDismiss()) {
                            continue;
                        }
                        deviceList.add(d);
                    }
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String Json) {

            }
        });
    }

    /**
     * 主机的所有设备 和设备的状态
     */
    private void getDeviceStatus() {
        List<DeviceRelate> deviceRelateList = Constant.DEVICE_RELATE;
        if (devideStatusList == null) devideStatusList = new ArrayList<DeviceStatus>();
        deviceAllList.clear();
        for (DeviceRelate deviceRelate : deviceRelateList) {
            deviceAllList.add(deviceRelate.getDeviceProp());
            devideStatusList.add(deviceRelate.getDeviceStatus());
        }
        //过滤掉未绑定的设备
        deviceList.clear();
        for (Device d : deviceAllList) {
            if (d.getDismiss()) {
                continue;
            }
            deviceList.add(d);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //切换radioButton,重置数据，刷新gridView

        switch (checkedId) {
            case R.id.rb_scence:
                V_TYPE_CURRENT = V_TYPE_SCENE;
                initSecenModel(Constant.GLOBAL_MODE);

                initFlagListSelectInfo(CHANNEL_SCENE_NUM, -1);
                initFlagListLinkedInfo(CHANNEL_SCENE_NUM);
                queryHGC(HGCADDR, V_TYPE_SCENE);

                break;
            case R.id.rb_air:
                mTvTitle.setText("空调配置");
                //加载数据
                initModelChangeData("CAC");

                V_TYPE_CURRENT = V_TYPE_AIRCONDITION;
                initFlagListSelectInfo(CHANNEL_AIRCONDITION_NUM, -1);
                initFlagListLinkedInfo(CHANNEL_AIRCONDITION_NUM);
                queryHGC(HGCADDR, V_TYPE_AIRCONDITION);
//
//                textViewValue1.add("空调");
//                textViewValue1.add("中央空调");
                //加载图标
                hardWareImgList.clear();
                int imgAirCondition[] = Constant.getModeImages("空调");
                hardWareImgList.add(imgAirCondition);

                //切换上面gridView数据
                adapter1Value = value1.get(1);

                break;
            case R.id.rb_light:
                mTvTitle.setText("灯光配置");

                initModelChangeData("Light");

                V_TYPE_CURRENT = V_TYPE_LIGHT;
                initFlagListSelectInfo(CHANNEL_LIGHT_NUM, -1);
                initFlagListLinkedInfo(CHANNEL_LIGHT_NUM);
                queryHGC(HGCADDR, V_TYPE_LIGHT);

                //加载图标
                hardWareImgList.clear();
                int imgLight[] = Constant.getModeImages("灯");
                hardWareImgList.add(imgLight);

                textViewValue1.clear();
                textViewValue1.add("灯");


                adapter1Value = value1.get(2);
//                textViewValue1.add("灯1");
//                textViewValue1.add("灯2");
//                textViewValue1.add("灯3");
//                textViewValue1.add("吊灯1");
//                textViewValue1.add("吊灯2");
//                textViewValue1.add("筒灯1");
//                textViewValue1.add("筒灯2");
//                textViewValue1.add("筒灯3");
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                imageViewValue1.add(R.drawable.ic_family_manage_person_gray);

                break;
            case R.id.rb_socket:
                mTvTitle.setText("插座配置");
                initModelChangeData("Socket");

                initFlagListSelectInfo(CHANNEL_SOCKET_NUM, -1);
                initFlagListLinkedInfo(CHANNEL_SOCKET_NUM);
                queryHGC(HGCADDR, V_TYPE_SOCKET);
                V_TYPE_CURRENT = V_TYPE_SOCKET;
                //加载图标
                hardWareImgList.clear();
                int imgSocket[] = Constant.getModeImages("插座");
                hardWareImgList.add(imgSocket);

                adapter1Value = value1.get(3);
//                for (int i = 1; i < 9; i++) {
//                    textViewValue1.add("插座" + i);
//                    imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                }

                break;
            case R.id.rb_window:
                mTvTitle.setText("窗帘配置");
                initModelChangeData("Curtain");

                initFlagListSelectInfo(CHANNEL_CURTAIN_NUM, -1);
                initFlagListLinkedInfo(CHANNEL_CURTAIN_NUM);
                queryHGC(HGCADDR, V_TYPE_CURTAIN);
                V_TYPE_CURRENT = V_TYPE_CURTAIN;
                //加载图标
                hardWareImgList.clear();
                int imgCurtain[] = Constant.getModeImages("窗帘");
                hardWareImgList.add(imgCurtain);

                adapter1Value = value1.get(4);
//                for (int i = 1; i < 9; i++) {
//                    textViewValue1.add("窗帘" + i);
//                    imageViewValue1.add(R.drawable.ic_family_manage_person_gray);
//                }
                break;
        }
        mGridViewAdapter1.setData(adapter1Value);
        mGridViewAdapter2.setData(hardWareDeviceList);
    }

    /**
     * 初始化第一个场景模式的数据
     */
    private void initializeMode1() {
        ArrayList<String> arrayList = null;
        //场景配置
        arrayList = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            textViewValue1.add("场景" + i);
            arrayList.add("场景" + i + ",0,false");
        }
        value1.add(arrayList);

        //空调配置
        arrayList = new ArrayList<>();
        arrayList.add("空调" + ",0,false");
        arrayList.add("中央空调" + ",0,false");
        value1.add(arrayList);
        //灯光配置
        arrayList = new ArrayList<>();
        arrayList.add("灯1" + ",0,false");
        arrayList.add("灯2" + ",0,false");
        arrayList.add("灯3" + ",0,false");
        arrayList.add("吊灯1" + ",0,false");
        arrayList.add("吊灯2" + ",0,false");
        arrayList.add("筒灯1" + ",0,false");
        arrayList.add("筒灯2" + ",0,false");
        arrayList.add("筒灯3" + ",0,false");
        value1.add(arrayList);
        //插座配置
        arrayList = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            arrayList.add("插座" + i + ",0,false");
        }
        value1.add(arrayList);
        //窗帘配置
        arrayList = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            arrayList.add("窗帘" + i + ",0,false");
        }
        value1.add(arrayList);
    }

    private void initSecenModel(List<Link> links) {

        mTvTitle.setText("场景配置");
//        sceneMode = Constant.GLOBAL_MODE;
        hardWareImgList.clear();

        int img1[] = Constant.getModeImages("回家");
        int img2[] = Constant.getModeImages("离家");
        int img3[] = Constant.getModeImages("会客"); //晨起
        int img4[] = Constant.getModeImages("就餐");
        int img5[] = Constant.getModeImages("撤防");
        int img6[] = Constant.getModeImages("布防");
        hardWareImgList.add(img1);
        hardWareImgList.add(img2);
        hardWareImgList.add(img3);
        hardWareImgList.add(img4);
        hardWareImgList.add(img5);
        hardWareImgList.add(img6);


        deviceTempList.clear();
        for (Link l : links) { //将模式处理成Device,方便Adapter
            Device d = new Device("");
            d.setName(l.getName());
            d.setAddr(l.getModeId() + "");
            d.setType("0xF" + "," + l.getTag() + "," + "," + l.getModeId() + l.getCurrent() + "," + l.getHasActiveTask());
            deviceTempList.add(d);
        }
        hardWareDeviceList.clear();
        hardWareDeviceList.addAll(deviceTempList);

        adapter1Value = value1.get(0);
    }

    /**
     * 获取不同类型的设备信息
     *
     * @param type
     */
    private void initModelChangeData(String type) {
        deviceTempList.clear();
        hardWareDeviceList.clear();
        for (Device d : deviceList) {
            if (d.getType().equals(type) || d.getType().contains(type)) {

                deviceTempList.add(d);
            }
        }
        hardWareDeviceList.addAll(deviceTempList);
    }


    /**
     * 下部的GridView 的 Adapter
     */
    private class HardwareGridViewAdapter extends BaseAdapter {
        private List<Device> devices;
        private Context mContext;
        private char FLAG = 'a'; //标记绑定的通道
        private boolean mGridViewScroll = false;

        public void setmGridViewScroll(boolean mGridViewScroll) {
            this.mGridViewScroll = mGridViewScroll;
        }

        public HardwareGridViewAdapter(Context mContext, List<Device> deviceList) {
            this.devices = deviceList;
            this.mContext = mContext;
        }

        public void setData(List<Device> devices) {
            this.devices = devices;
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            FLAG = 'a';
            mGridViewScroll = false;
            return hardWareDeviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return hardWareDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_device_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //处理场景
            if (devices.get(position).getType().contains("0xF")) {
                String[] temp = devices.get(position).getType().split(",");
                if (StringUtil.isEmpty(temp[1])) {
                    viewHolder.mIdTextViewDevice.setText(devices.get(position).getName().substring(0, 4));
                } else {
                    viewHolder.mIdTextViewDevice.setText(temp[1]);
                }
            } else {
                viewHolder.mIdTextViewDevice.setText(devices.get(position).getName());
            }
            //处理背景高亮
            if (!StringUtil.isEmpty(mSelectShow)) {
                if (devices.get(position).getAddr().equals(mSelectShow)) {
                    viewHolder.mIdImageViewDevice1.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIdImageViewDevice1.setVisibility(View.GONE);
                }
            } else {
                viewHolder.mIdImageViewDevice1.setVisibility(View.GONE);
            }

            if (!mGridViewScroll && flagLinkedList2Hard.size() != 0) {
                for (int i = 0; i < mHardOrderList.size(); i++) {
                    if (mHardOrderList.get(i) == position)
                        viewHolder.mIdTextViewModeInfo.setText(flagLinkedList2Hard.get(position) ? String.valueOf((char) (FLAG + i)) : "");

                }
            }

            if (hardWareImgList.size() != 0) {
                int imgResId = selectButtonBackGround(hardWareImgList, position, flagSelectList2Hard.get(position));
                if (imgResId != -1)
                    viewHolder.mIdImageViewDevice2.setBackground(getResources().getDrawable(imgResId));
            }
            return convertView;
        }

    }

    private class gridViewAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mStringArrayList;//textView显示的值
        //        private char FLAG = 'a'; //标记绑定的通道
        private char FLAG = 96; //标记绑定的通道
        private boolean mGridViewScroll = false;

        public void setmGridViewScroll(boolean mGridViewScroll) {
            this.mGridViewScroll = mGridViewScroll;
        }

        public gridViewAdapter(Context context, List stringArrayList) {
            this.mContext = context;
            mStringArrayList = stringArrayList;
        }

        public void setData(List stringArrayList) {
//            FLAG = 'a';
            FLAG = 96; //标记绑定的通道
            this.mStringArrayList = stringArrayList;
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
//            FLAG = 'a';
            FLAG = 96; //标记绑定的通道
            mGridViewScroll = false;
            return mStringArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_device_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mIdTextViewDevice.setText(mStringArrayList.get(position).split(",")[0]);//限制字的正确显示

            //处理背景高亮
            if (!StringUtil.isEmpty(mSelectShow)) {
                if (position == Integer.valueOf(mSelectShow)) {
                    viewHolder.mIdImageViewDevice1.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mIdImageViewDevice1.setVisibility(View.GONE);
                }
            } else {
                viewHolder.mIdImageViewDevice1.setVisibility(View.GONE);
            }

            if (!mGridViewScroll && flagLinkedListSoft.size() != 0) {
                viewHolder.mIdTextViewModeInfo.setText(flagLinkedListSoft.get(position) ? String.valueOf(FLAG++) : "");
            }
            if (hardWareImgList.size() != 0) {
                int imgResId = selectButtonBackGround(hardWareImgList, position, flagSelectListSoft.get(position));
                if (imgResId != -1)
                    viewHolder.mIdImageViewDevice2.setBackground(getResources().getDrawable(imgResId));
            }

            return convertView;
        }


    }

    class ViewHolder {
        @Bind(R.id.id_imageViewDevice1)
        ImageView mIdImageViewDevice1;
        @Bind(R.id.id_imageViewDevice2)
        ImageView mIdImageViewDevice2;
        @Bind(R.id.id_textViewDevice)
        TextView mIdTextViewDevice;
        @Bind(R.id.id_textViewModeInfo)
        TextView mIdTextViewModeInfo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 返回图片资源文件的id
     *
     * @param hardWareImgList
     * @param position
     * @return
     */
    private int selectButtonBackGround(List<int[]> hardWareImgList, int position, boolean mIsSelected) {
        int temp = 0;//选择那种图片 0 ：灰色圆圈 1：蓝色圆圈 2：蓝色背景
        if (hardWareImgList.size() == 0) return -1;
        if (hardWareImgList.size() == 1) position = 0;
        if (hardWareImgList.size() > 1) {
            if (adapter1Value.size() > hardWareImgList.size()) position = 0;
            if (hardWareDeviceList.size() > hardWareImgList.size()) position = 0;
        }
        int[] temps = hardWareImgList.get(position); //存放三种图片
        if (!mIsPairing) {
            temp = 0;
        } else if (!mIsSelected) {
            temp = 1;
        } else {
            temp = 2;
        }
        return temps[temp];
    }

    /**
     * 标签的处理
     *
     * @param flagList 标签的list
     * @param position 标签改变的位置 负数：表示重新初始化
     * @param flag     true 表示只保留一个标签为true《选中状态》
     */
    private List<Boolean> initFlagList(List<Boolean> flagList, int size, int position, boolean flag) {

        if (position > size) return null;
        if (flagList == null) flagList = new ArrayList<>();
        if (flag || position < 0) {
            flagList.clear();
            for (int i = 0; i < size; i++) {
                flagList.add(false);
            }
        }
        if (position >= 0) {
            flagList.remove(position);
            flagList.add(position, true);
        }
        return flagList;
    }

    private void initFlagListSelectInfo(int SIZE, int position) {

        flagSelectListSoft = initFlagList(flagSelectListSoft, SIZE, position, true);
        if (hardWareDeviceList.size() == 0) {
            flagSelectList2Hard = initFlagList(flagSelectList2Hard, SIZE, position, true);
        } else {
            flagSelectList2Hard = initFlagList(flagSelectList2Hard, hardWareDeviceList.size(), position, true);
        }

    }

    private void initFlagListLinkedInfo(int SIZE) {
        flagLinkedListSoft = initFlagList(flagLinkedListSoft, SIZE, -1, false);
        flagLinkedList2Hard = initFlagList(flagLinkedList2Hard, hardWareDeviceList.size(), -1, false);

    }

    /**
     * 判断中控channel是否绑定
     */
    private void changeSoftLink(int position, String v_type) {
        mSelectShow = "";
        String addr = null;
        int SIZE = channelNUM(v_type);
        flagSelectListSoft = initFlagList(flagSelectListSoft, SIZE, position, true);
        addr = mFlagListLinked.get(v_type).get(position);//选中的通道绑定的硬件地址 每个通道对应一个绑定可能为空

        for (int i = 0; i < flagSelectList2Hard.size(); i++) {
            if (flagSelectList2Hard.get(i)) {
                String tempAddr = hardWareDeviceList.get(i).getAddr(); //选中的硬件地址
                if (StringUtil.isEmpty(addr)) { // 如果此通道绑定为空，且硬件有选中，去尝试绑定
                    for (int j = 0; j < mFlagListLinked.get(v_type).size(); j++) {
                        if (mFlagListLinked.get(v_type).get(j).equals(tempAddr)) {

                            toastUtils.showErrorWithStatus("绑定失败");
                            mGridViewAdapter1.setData(adapter1Value);
                            mGridViewAdapter2.setData(hardWareDeviceList);
                            return;
                        }
                    }
                    configHGCBindInfo((position + 1) + "", hardWareDeviceList.get(i), v_type);
                    return;

                }
                if (addr.equals(tempAddr)) {
                    deleteHGCBindInfo((position + 1) + "", hardWareDeviceList.get(i), v_type);
                    return;
                } else {
                    toastUtils.showErrorWithStatus("绑定失败");
                    mGridViewAdapter1.setData(adapter1Value);
                    mGridViewAdapter2.setData(hardWareDeviceList);
                    return;
                }

            } else {
                String tempAddr = hardWareDeviceList.get(i).getAddr(); //选中的硬件地址
                if (!StringUtil.isEmpty(addr)) {
                    if (addr.equals(tempAddr)) {
                        //有绑定，但是未选中，则背景高亮
                        mSelectShow = tempAddr;
//                        mGridViewAdapter1.setDatas(adapter1Value);
//                        mGridViewAdapter2.setDatas(hardWareDeviceList);
//                        return;
                    }
                }
            }
        }

        mGridViewAdapter1.setData(adapter1Value);
        mGridViewAdapter2.setData(hardWareDeviceList);

    }

    private void changeHardLink(int position, String v_type) {
        int channel = -1;
        mSelectShow = "";
        initFlagList(flagSelectList2Hard, hardWareDeviceList.size(), position, true);
        String addr = hardWareDeviceList.get(position).getAddr(); //选中的硬件地址
        String tempAddr = null; //通道绑定的硬件地址 可能为空
        for (int i = 0; i < flagSelectListSoft.size(); i++) {
            if (flagSelectListSoft.get(i)) {
                tempAddr = mFlagListLinked.get(v_type).get(i);
                if (StringUtil.isEmpty(tempAddr)) { //说明选中的channel 未绑定过设备，绑定
//                    for (int j = 0; j < mFlagListLinked.get(v_type).size(); j++) {
//                        if (mFlagListLinked.get(v_type).get(j).equals(addr)) {
//
//                            toastUtils.showErrorWithStatus("绑定失败");
//                            mGridViewAdapter1.setDatas(adapter1Value);
//                            mGridViewAdapter2.setDatas(hardWareDeviceList);
//                            return;
//                        } else {
//                            configHGCBindInfo((i + 1) + "", hardWareDeviceList.get(position), v_type);
//                            return;
//                        }
//                    }
                    if (mFlagListLinked.get(v_type).contains(addr)) {
                        toastUtils.showErrorWithStatus("绑定失败");
                        mGridViewAdapter1.setData(adapter1Value);
                        mGridViewAdapter2.setData(hardWareDeviceList);
                        return;
                    } else {
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                toastUtils.dismiss();
//                                toastUtils.showErrorWithStatus("网络状态不好");
//                            }
//                        }, 1000 * DELAYTIME);
                        toastUtils.showProgress("正在绑定");
                        configHGCBindInfo((i + 1) + "", hardWareDeviceList.get(position), v_type);
                        return;
                    }
                }
                if (addr.equals(tempAddr)) { //说明选中的channel绑定了此设备，解绑
                    toastUtils.showProgress("正在解除绑定");
                    deleteHGCBindInfo((i + 1) + "", hardWareDeviceList.get(position), v_type);
                    return;
                } else { //说明选中的channel绑定了其他设备，提示错误

                    toastUtils.showErrorWithStatus(getResources().getString(R.string.device_setting_activity_bind_fail));
                    mGridViewAdapter1.setData(adapter1Value);
                    mGridViewAdapter2.setData(hardWareDeviceList);
                    return;
                }
            } else {
                tempAddr = mFlagListLinked.get(v_type).get(i);
                if (addr.equals(tempAddr)) { //没有选中，channel中绑定了此设备，背景高亮
                    mSelectShow = (i + 1) + "";

//                    mGridViewAdapter1.setDatas(adapter1Value);
//                    mGridViewAdapter2.setDatas(hardWareDeviceList);
//                    return;
                }
            }


        }
        int SIZE = channelNUM(V_TYPE_CURRENT);
        initFlagListSelectInfo(SIZE, -1);
        mGridViewAdapter1.setData(adapter1Value);
        mGridViewAdapter2.setData(hardWareDeviceList);

//        //找到通道设置背景闪烁
//        initFlagList(flagSelectList2Hard, hardWareDeviceList.size(), position, true);
////
//        mGridViewAdapter1.notifyDataSetChanged();
//        mGridViewAdapter2.notifyDataSetChanged();
    }

    /**
     * 查询中控的绑定信息
     *
     * @param addr
     * @param v_type
     */
    private void queryHGC(String addr, final String v_type) {
        toastUtils.showProgress("同步数据中...");
        DeviceController.getInstance().queryHGC(this, addr, v_type, null, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
//                toastUtils.dismiss();
                if (new Gson().fromJson(Json, BaseResult.class).getRet() != 0) {
                    toastUtils.showErrorWithStatus("请求信息错误");
                    return;
                }
                int SIZE = channelNUM(v_type);

                switch (v_type) {
                    case "0x1": //灯
                        dealLightJson(Json, v_type, CHANNEL_LIGHT_NUM);
                        break;
                    case "0x2": //插座
                        dealSocketJson(Json, v_type, CHANNEL_SOCKET_NUM);

                        break;
                    case "0x4": //窗帘 或红外
                        dealCurtainJson(Json, v_type, CHANNEL_CURTAIN_NUM);
                        break;
                    case "0xF": //场景控制
                        dealSceneJson(Json, v_type, CHANNEL_SCENE_NUM);
                        break;
                    case "0x50": ///空调
                        dealAirConditionJson(Json, v_type, CHANNEL_AIRCONDITION_NUM);
                        break;
                }
            }

            @Override
            public void onFailed(String Json) {
                toastUtils.dismiss();
            }
        });

    }


    /**
     * 解析插座的数据
     *
     * @param Json
     */
    private void dealSocketJson(String Json, String v_type, int NUM) {
        String channel = null;
        String addr = null;
        List<String> tempList = initHGCChannelList(NUM);
//        List<Map<Integer, String>> mapArrayList = new ArrayList<>();
        HGCSocketConfig result = new Gson().fromJson(Json, HGCSocketConfig.class);
        if (result.getResponse().size() != 0) {
            HGCSocketConfig.ResponseBean.ControlsBean controlsBean = result.getResponse().get(0).getControls().get(0);
            mDevicesControlsInfoMap.clear();
            mDevicesControlsInfoMap.put(v_type, controlsBean);
        }
        List<HGCSocketConfig.ResponseBean> responses = result.getResponse(); //关联信息
        for (HGCSocketConfig.ResponseBean r : responses) {

            channel = r.getChannel();//绑定过的通道
            List<HGCSocketConfig.ResponseBean.ControlsBean> ControlsBeans = r.getControls();
            for (HGCSocketConfig.ResponseBean.ControlsBean c : ControlsBeans) {
                addr = c.getAddr();
                tempList.remove(Integer.valueOf(channel) - 1);
                tempList.add(Integer.valueOf(channel) - 1, addr);
            }
        }

        mFlagListLinked.put(v_type, tempList);
        settingHGCBindinfo(v_type);

    }

    /**
     * 解析灯的数据
     *
     * @param Json
     */
    private void dealLightJson(String Json, String v_type, int NUM) {
        HGCLightConfig result = GsonUtil.getObject(Json, HGCLightConfig.class);
        if (result.getResponse().size() != 0) {
            HGCLightConfig.ResponseBean.ControlsBean controlsBean = result.getResponse().get(0).getControls().get(0);
            mDevicesControlsInfoMap.clear();
            mDevicesControlsInfoMap.put(v_type, controlsBean);
        }

        dealSocketJson(Json, v_type, NUM);

    }

    /**
     * 解析空调的数据
     *
     * @param Json
     */
    private void dealAirConditionJson(String Json, String v_type, int NUM) {
        HGCAirConditionConfig result = new Gson().fromJson(Json, HGCAirConditionConfig.class);
        if (result.getResponse().size() != 0) {
            HGCAirConditionConfig.ResponseBean.ControlsBean controlsBean = result.getResponse().get(0).getControls().get(0);
            mDevicesControlsInfoMap.clear();
            mDevicesControlsInfoMap.put(v_type, controlsBean);
        }
        dealSocketJson(Json, v_type, NUM);
    }

    /**
     * 解析窗帘或者红外的数据
     *
     * @param Json
     */
    private void dealCurtainJson(String Json, String v_type, int NUM) {
        HGCCurtainConfig result = new Gson().fromJson(Json, HGCCurtainConfig.class);
        if (result.getResponse().size() != 0) {
            HGCCurtainConfig.ResponseBean.ControlsBean controlsBean = result.getResponse().get(0).getControls().get(0);
            mDevicesControlsInfoMap.clear();
            mDevicesControlsInfoMap.put(v_type, controlsBean);
        }
        dealSocketJson(Json, v_type, NUM);
    }

    /**
     * 解析场景的数据
     *
     * @param Json
     */
    private void dealSceneJson(String Json, String v_type, int NUM) {
        String channel = null;
        String modeId = null;
        List<String> tempList = initHGCChannelList(NUM);

        HGCSceneConfig result = new Gson().fromJson(Json, HGCSceneConfig.class);
        if (result.getRet() != 0) {
            toastUtils.showErrorWithStatus("服务器错误");
            mFlagListLinked.put(v_type, tempList);
            settingHGCBindinfo(v_type);
            return;
        }
        if (result.getResponse().size() == 0) {
            mFlagListLinked.put(v_type, tempList);
            settingHGCBindinfo(v_type);
            return;
        }
        HGCSceneConfig.ResponseBean.ControlsBean controlsBean = result.getResponse().get(0).getControls().get(0);

        mDevicesControlsInfoMap.clear();
        mDevicesControlsInfoMap.put(v_type, controlsBean);

        List<HGCSceneConfig.ResponseBean> responses = result.getResponse(); //关联信息
        for (HGCSceneConfig.ResponseBean r : responses) {
            channel = r.getChannel();
            List<HGCSceneConfig.ResponseBean.ControlsBean> ControlsBeans = r.getControls();
            for (HGCSceneConfig.ResponseBean.ControlsBean c : ControlsBeans) {
                modeId = c.getModeId();
                tempList.remove(Integer.valueOf(channel) - 1);
                tempList.add(Integer.valueOf(channel) - 1, modeId);
            }

        }
        mFlagListLinked.put(v_type, tempList);
        settingHGCBindinfo(v_type);
    }

    private void settingHGCBindinfo(String v_type) {
        mHardOrderList.clear();
        List<String> tempList = mFlagListLinked.get(v_type);
        for (int channel = 0; channel < tempList.size(); channel++) {//遍历每个通道
            if (StringUtil.isEmpty(tempList.get(channel))) continue;
            flagLinkedListSoft.remove(channel);
            flagLinkedListSoft.add(channel, true); //如果通道存在绑定信息，做标记， 并找到和他绑定的硬件的位置
            String info = tempList.get(channel); //MAC 或者 modeId
            //遍历 显示硬件设备的集合， 找到和他绑定的硬件的位置，并打标记
            if (hardWareDeviceList.size() == 0) continue;
            for (int i = 0; i < hardWareDeviceList.size(); i++) { //遍历集合，找到了将对应的位置返回回来
                if (info.equals(hardWareDeviceList.get(i).getAddr())) {
                    flagLinkedList2Hard.remove(i);
                    flagLinkedList2Hard.add(i, true);
                    mHardOrderList.add(channel, i);//用来将对应channel的addr 按顺序存储
                }
            }

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //根据打标记的信息，显示绑定信息
                mGridViewAdapter1.setData(adapter1Value);
                mGridViewAdapter2.setData(hardWareDeviceList);
            }
        }, 1000 * 2);
      if (toastUtils !=null) toastUtils.dismiss();
    }

    /**
     * 初始化中控上的通道
     *
     * @param SIZE 通道数量
     * @return
     */
    private List<String> initHGCChannelList(int SIZE) {
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            tempList.add("");
        }
        return tempList;
    }

    private void configHGCBindInfo(final String channel, final Device tempDevice, final String v_type) {
        Map<String, Object> controls = null;
        controls = configControls(tempDevice, v_type);
//        L.i("AAAABA DeviceSettingListeningActivity "+  " == " + controls);
        DeviceController.getInstance().configHGC(this, channel, HGCADDR, v_type, controls, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
//                L.i("AAAABA DeviceSettingListeningActivity configHGCBindInfo " + Json );

                BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                if (result.getRet() == 0) {
                    queryHGC(HGCADDR, V_TYPE_CURRENT);
                    mHardOrderList.clear();
//                    mFlagListLinked.clearAll();
                    mSelectShow = "";
                    toastUtils.showSuccessWithStatus(getResources().getString(R.string.device_setting_activity_close_success));
                    mFlagListLinked.get(v_type).remove(channel);
                    mFlagListLinked.get(v_type).add(Integer.valueOf(channel), tempDevice.getAddr());
                } else {
                    mSelectShow = "";
                    toastUtils.showSuccessWithStatus(getResources().getString(R.string.device_setting_activity_bind_fail));
                    mGridViewAdapter1.setData(adapter1Value);
                    mGridViewAdapter2.setData(hardWareDeviceList);
                }
                //初始化选中信息
                int SIZE = channelNUM(V_TYPE_CURRENT);
                initFlagListSelectInfo(SIZE, -1);
                initFlagListLinkedInfo(SIZE);
            }

            @Override
            public void onFailed(String Json) {
                L.i("AAAABA" + Json);
            }
        });

    }

    private void deleteHGCBindInfo(final String channel, Device tempDevice, final String v_type) {
        String controls = null;
//        controls = configControls(tempDevice, v_type);

        DeviceController.getInstance().deleteConfigHGC(this, channel, HGCADDR, v_type, controls, new RequestResultListener() {
            @Override
            public void onSuccess(String Json) {
                L.i("AAAABA DeviceSettingListeningActivity deleteHGCBindInfo " + Json);

                BaseResult result = new Gson().fromJson(Json, BaseResult.class);
                if (result.getRet() == 0) {
                    queryHGC(HGCADDR, V_TYPE_CURRENT);
                    mHardOrderList.clear();
                    toastUtils.showSuccessWithStatus(getResources().getString(R.string.device_setting_activity_bind_dismiss));
                    mSelectShow = "";
                    mFlagListLinked.get(v_type).remove(channel);
                } else {
                    queryHGC(HGCADDR, V_TYPE_CURRENT);
                    mSelectShow = "";
                    toastUtils.showSuccessWithStatus(getResources().getString(R.string.device_setting_activity_bind_fail));
                    mGridViewAdapter1.setData(adapter1Value);
                    mGridViewAdapter2.setData(hardWareDeviceList);
                }

                //初始化选中信息
                int SIZE = channelNUM(V_TYPE_CURRENT);
                initFlagListSelectInfo(SIZE, -1);
                initFlagListLinkedInfo(SIZE);

            }

            @Override
            public void onFailed(String Json) {
                L.i("AAAABA" + Json);
            }
        });
    }

    /**
     * 每种模式对应的通道数量
     *
     * @param v_type
     * @return
     */
    private int channelNUM(String v_type) {
        int SIZE = 0;
        switch (v_type) {
            case V_TYPE_AIRCONDITION:
                SIZE = CHANNEL_AIRCONDITION_NUM;
                break;
            case V_TYPE_SCENE:
                SIZE = CHANNEL_SCENE_NUM;
                break;
            case V_TYPE_SOCKET:
                SIZE = CHANNEL_SOCKET_NUM;
                break;
            case V_TYPE_CURTAIN:
                SIZE = CHANNEL_CURTAIN_NUM;
                break;
            case V_TYPE_LIGHT:
                SIZE = CHANNEL_LIGHT_NUM;
                break;
        }
        return SIZE;
    }


    private Map<String, Object> configControls(Device tempDevice, String v_type) {
        Map<String, Object> values = new HashMap<>();

        switch (v_type) {
            case V_TYPE_AIRCONDITION:
                List<HGCAirConditionConfig.ResponseBean.ControlsBean> controls = new ArrayList<>();

                HGCAirConditionConfig.ResponseBean.ControlsBean value1 = (HGCAirConditionConfig.ResponseBean.ControlsBean) mDevicesControlsInfoMap.get(v_type);
                if (value1 == null) {
                    value1 = new HGCAirConditionConfig.ResponseBean.ControlsBean();
                    HGCAirConditionConfig.ResponseBean.ControlsBean.ValueBean valueBean = new HGCAirConditionConfig.ResponseBean.ControlsBean.ValueBean();
                    valueBean.setControlCmd("1"); //默认值目前没用到
                    valueBean.setControlData("1");
                    valueBean.setLength("1");
                    valueBean.setProtocol("1");
                    valueBean.setAddr(tempDevice.getAddr());
                    valueBean.setControlDeviceType(v_type);

                    value1.setValue(valueBean);
                    value1.setType(tempDevice.getType());

                }
                value1.setAddr(tempDevice.getAddr());
                value1.getValue().setAddr(tempDevice.getAddr());
                controls.add(value1);

//                values = new Gson().Object2Json(controls);
                values.put("controls", controls);
                break;
            case V_TYPE_SOCKET:
                List<HGCSocketConfig.ResponseBean.ControlsBean> controls2 = new ArrayList<>();
                Map<String, List<HGCSocketConfig.ResponseBean.ControlsBean>> tempMap = new HashMap<>();
                HGCSocketConfig.ResponseBean.ControlsBean value2 = (HGCSocketConfig.ResponseBean.ControlsBean) mDevicesControlsInfoMap.get(v_type);
                if (value2 == null) {
                    value2 = new HGCSocketConfig.ResponseBean.ControlsBean();
                    HGCSocketConfig.ResponseBean.ControlsBean.ValueBean valueBean = new HGCSocketConfig.ResponseBean.ControlsBean.ValueBean();
                    valueBean.setI("0"); //这里处理默认值
                    valueBean.setP("0");
                    valueBean.setEnergy("0");
                    valueBean.setU("0");
                    valueBean.setState("0");
                    value2.setValue(valueBean);
                    value2.setType(tempDevice.getType());

                }
                value2.setAddr(tempDevice.getAddr());
                controls2.add(value2);
                tempMap.put("controls", controls2);

                values.put("controls", controls2);
//                values = new Gson().Object2Json(controls2);
                break;
            case V_TYPE_LIGHT:
                List<HGCLightConfig.ResponseBean.ControlsBean> controls3 = new ArrayList<>();
                HGCLightConfig.ResponseBean.ControlsBean bean3 = (HGCLightConfig.ResponseBean.ControlsBean) mDevicesControlsInfoMap.get(v_type);
                if (bean3 == null) {
                    bean3 = new HGCLightConfig.ResponseBean.ControlsBean();
                    HGCLightConfig.ResponseBean.ControlsBean.ValueBean valueBean = new HGCLightConfig.ResponseBean.ControlsBean.ValueBean();
                    bean3.setValue(valueBean);
                    bean3.setType(tempDevice.getType());
                    switch (tempDevice.getType()) {
                        case "Lignt1":
                            valueBean.setState(0); //这里仅做了默认处理
                            break;
                        case "Lignt2":
                            valueBean.setState(0);
                            valueBean.setState2(0);
                            break;
                        case "Lignt3":
                            valueBean.setState2(0);
                            valueBean.setState3(0);
                            valueBean.setState4(0);
                            break;
                        case "Lignt4":
                            valueBean.setState(0);
                            valueBean.setState2(0);
                            valueBean.setState3(0);
                            valueBean.setState4(0);
                            break;
                        default:
//                            valueBean.setState();//调节灯
                            break;
                    }
                }
                bean3.setAddr(tempDevice.getAddr());
                controls3.add(bean3);
//                values = new Gson().Object2Json(controls3);

                values.put("controls", controls3);
                break;
            case V_TYPE_CURTAIN:
                List<HGCCurtainConfig.ResponseBean.ControlsBean> controls4 = new ArrayList<>();
                HGCCurtainConfig.ResponseBean.ControlsBean bean4 = (HGCCurtainConfig.ResponseBean.ControlsBean) mDevicesControlsInfoMap.get(v_type);
                if (bean4 == null) {
                    bean4 = new HGCCurtainConfig.ResponseBean.ControlsBean();
                    bean4.setType(tempDevice.getType());
                }
                bean4.setAddr(tempDevice.getAddr());
                controls4.add(bean4);
//                values = new Gson().Object2Json(controls4);

                values.put("controls", controls4);
                break;
            case V_TYPE_SCENE:
                List<HGCSceneConfig.ResponseBean.ControlsBean> controls5 = new ArrayList<>();
                HGCSceneConfig.ResponseBean.ControlsBean bean5 = (HGCSceneConfig.ResponseBean.ControlsBean) mDevicesControlsInfoMap.get(v_type);
                if (bean5 == null) {
                    bean5 = new HGCSceneConfig.ResponseBean.ControlsBean();
                }
                bean5.setModeId(tempDevice.getAddr());
                controls5.add(bean5);
//                values = new Gson().Object2Json(controls5);

                values.put("controls", controls5);
                break;

        }

        return values;
    }

    @Override
    protected void deviceStatusUpdate() {
        getDeviceStatus();
    }
}
